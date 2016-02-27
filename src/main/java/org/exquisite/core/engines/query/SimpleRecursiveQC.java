package org.exquisite.core.engines.query;

import org.exquisite.core.engines.query.scoring.QuerySelection;
import org.exquisite.core.model.Diagnosis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.COUNTER_INTERACTIVE_PARTITIONS;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.getCounter;

/**
 * Simple query computation that implements recursive brute force computation method
 */
public class SimpleRecursiveQC<T> extends SimpleQC<T> {

    private static Logger logger = LoggerFactory.getLogger(SimpleQC.class);

    public SimpleRecursiveQC() {
        super();
    }

    public SimpleRecursiveQC(QuerySelection<T> querySelection) {
        super(querySelection);
    }

    @Override
    protected long computeQueries(Set<T> kb, ArrayList<Diagnosis<T>> diagnoses, Set<Query<T>> queries) {
        computeQueries(kb, new ArrayList<>(diagnoses), new HashSet<>(diagnoses.size()),
                new HashSet<>(diagnoses.size()), queries);
        return queries.size();
    }

    protected void computeQueries(Set<T> kb, ArrayList<Diagnosis<T>> hittingSets, Set<Diagnosis<T>> dx,
                                  Set<Diagnosis<T>> dnx,
                                  Set<Query<T>> queries) {

        if (hittingSets.isEmpty()) {
            if (dx.isEmpty() || dnx.isEmpty())
                return;
            Query<T> query = createQuery(kb, dx, dnx);
            if (query != null && query.score.compareTo(getThreshold()) < 0) {
                queries.add(query);
                if (logger.isDebugEnabled())
                    logger.debug("Created query: \n dx:" + query.dx + "\n dnx:" + query.dnx + "\n dz:" + query.dz);
            }
        } else {
            Diagnosis<T> diag = hittingSets.remove(0);
            if (logger.isDebugEnabled())
                logger.debug("Partitions: " + getCounter(COUNTER_INTERACTIVE_PARTITIONS).value()
                        + " dx: " + dx.size() + " " + "hsets:" + hittingSets.size());

            dx.add(diag);
            computeQueries(kb, hittingSets, dx, dnx, queries);
            dx.remove(diag);

            dnx.add(diag);
            computeQueries(kb, hittingSets, dx, dnx, queries);
            dnx.remove(diag);

            hittingSets.add(diag);
        }
    }

}
