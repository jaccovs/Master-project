package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.scoring.QuerySelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.COUNTER_INTERACTIVE_PARTITIONS;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.getCounter;

/**
 * Simple query computation that implements recursive brute force computation method
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public class SimpleRecursiveQC<F> extends SimpleQC<F> {

    private static Logger logger = LoggerFactory.getLogger(SimpleQC.class);

    public SimpleRecursiveQC() {
        super();
    }

    public SimpleRecursiveQC(QuerySelection<F> querySelection) {
        super(querySelection);
    }

    @Override
    protected long computeQueries(Set<F> kb, ArrayList<Diagnosis<F>> diagnoses, Set<Query<F>> queries) {
        computeQueries(kb, new ArrayList<>(diagnoses), new HashSet<>(diagnoses.size()),
                new HashSet<>(diagnoses.size()), queries);
        return queries.size();
    }

    protected void computeQueries(Set<F> kb, ArrayList<Diagnosis<F>> hittingSets, Set<Diagnosis<F>> dx,
                                  Set<Diagnosis<F>> dnx,
                                  Set<Query<F>> queries) {

        if (hittingSets.isEmpty()) {
            if (dx.isEmpty() || dnx.isEmpty())
                return;
            Query<F> query = createQuery(kb, dx, dnx);
            if (query != null && query.qPartition.score.compareTo(getThreshold()) < 0) {
                queries.add(query);
                if (logger.isDebugEnabled())
                    logger.debug("Created query: \n dx:" + query.qPartition.dx + "\n dnx:" + query.qPartition.dnx + "\n dz:" + query.qPartition.dz);
            }
        } else {
            Diagnosis<F> diag = hittingSets.remove(0);
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
