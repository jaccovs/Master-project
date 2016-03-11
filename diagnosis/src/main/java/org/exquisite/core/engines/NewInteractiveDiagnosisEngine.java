package org.exquisite.core.engines;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.costestimators.CostsEstimator;
import org.exquisite.core.costestimators.SimpleCostsEstimator;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Answer;
import org.exquisite.core.query.NewQC;
import org.exquisite.core.query.NewQueryAnswering;
import org.exquisite.core.query.NewQueryComputation;
import org.exquisite.core.solver.ISolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import static org.exquisite.core.Utils.hasIntersection;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.*;

/**
 * A diagnosis engine that interacts with a user or any of the query answering interface to find the one correct
 * diagnosis. This class depends on another engine that allows computation of diagnoses.
 *
 * Copy of InteractiveDiagnosisEngine and slightly adapted for requirements of NewQC.
 *
 * Created by pr8 and wolfi on 10.03.2015.
 */
public class NewInteractiveDiagnosisEngine<F> extends AbstractDiagnosisEngine<F> implements IDiagnosisEngine<F> {

    private static final Logger logger = LoggerFactory.getLogger(NewInteractiveDiagnosisEngine.class);

    /**
     * The maximum number of diagnoses which must be computed by the inner engine and forwarded to query computation.
     */
    private int diagnosesPerQuery = 9;

    /**
     * Indicates whether the solver has to compute explicit entailments for query computation
     * Default value is <code>true</code>
     */
    private boolean useEntailments = true;

    /**
     * Inner engine is used to calculate the tests.diagnosis candidates.
     */
    private IDiagnosisEngine<F> innerEngine;

    /**
     * Query computation algorithm used in the interactive session
     */
    private NewQueryComputation<F> queryComputation;

    /**
     * Estimator of the costs for a set of statements
     */
    private CostsEstimator<F> costsEstimator;

    /**
     * Threshold that allows to stop computations if one of the diagnoses is above it.
     * Default value is <code>0.95</code>
     */
    private BigDecimal threshold = new BigDecimal(0.95);

    /**
     * An interface that must be implemented by the front-ends allowing a user to answer a query
     */
    private NewQueryAnswering<F> queryAnswering;

    public NewInteractiveDiagnosisEngine(IDiagnosisEngine<F> innerEngine,
                                         NewQueryComputation<F> queryComputation,
                                         NewQueryAnswering<F> queryAnswering,
                                         CostsEstimator<F> estimator) {
        super(innerEngine.getSolver());
        this.costsEstimator = estimator;
        this.innerEngine = innerEngine;
        this.queryComputation = queryComputation;
        this.queryAnswering = queryAnswering;
    }

    public NewInteractiveDiagnosisEngine(ISolver<F> solver, NewQueryAnswering<F> queryAnswering) {
        super(solver);
        this.costsEstimator = new SimpleCostsEstimator<>();
        this.innerEngine = new HSTreeEngine<>(solver);
        this.queryComputation = new NewQC<>(null, this.getDiagnosisModel()); // TODO create implementation of first parameter
        this.queryAnswering = queryAnswering;
    }


    public Set<Diagnosis<F>> calculateDiagnoses() throws DiagnosisException {

        Set<Diagnosis<F>> diagnoses = new HashSet<>(this.diagnosesPerQuery);
        Set<Set<F>> conflicts = new HashSet<>();
        Set<F> partialDiagnosis = new HashSet<>();

        start(TIMER_INTERACTIVE_SESSION);
        do {
            innerEngine.resetEngine();
            logger.debug("Searching for diagnoses");

            start(TIMER_INTERACTIVE_DIAGNOSES);
            int maxNumberOfDiagnoses = this.diagnosesPerQuery - diagnoses.size();
            if (maxNumberOfDiagnoses > 0) {
                conflicts = removeHitConflicts(conflicts, partialDiagnosis);
                innerEngine.setConflicts(conflicts);
                innerEngine.setMaxNumberOfDiagnoses(maxNumberOfDiagnoses);
                diagnoses.addAll(innerEngine.calculateDiagnoses());
            }
            stop(TIMER_INTERACTIVE_DIAGNOSES);

            incrementCounter(COUNTER_INTERACTIVE_DIAGNOSES);

            if (diagnoses.size() > 1) {
                if (logger.isInfoEnabled()) {
                    logger.info("Found " + diagnoses.size() + " diagnoses");
                }
                queryComputation.initialize(diagnoses);
                if (queryComputation.hasNext()) {
                    Set<F> query = queryComputation.next();

                    Answer<F> answer = this.queryAnswering.getAnswer(query);

                    if (answer.positive.containsAll(query))
                        getDiagnosisModel().getEntailedTestCases().add(query);
                    else
                        getDiagnosisModel().getNotEntailedTestCases().add(query);
                }
            }
        } while (diagnoses.size() > 1 && belowThreshold(diagnoses)); // TODO LOGISCHES UND ???
        stop(TIMER_INTERACTIVE_SESSION);

        return diagnoses;
    }

    /**
     * Checks whether the most probable diagnosis is below the given threshold
     *
     * @param diagnoses set of diagnoses
     * @return <code>true</code> if measures of all diagnoses are below the threshold and false otherwise
     */
    private boolean belowThreshold(Set<Diagnosis<F>> diagnoses) {
        if (diagnoses instanceof SortedSet) {
            return ((SortedSet<Diagnosis<F>>) diagnoses).last().getMeasure().compareTo(this.threshold) < 1;
        }
        for (Diagnosis<F> diagnosis : diagnoses) {
            if (diagnosis.getMeasure().compareTo(this.threshold) > 0)
                return false;
        }
        return true;
    }

    private Set<Set<F>> removeHitConflicts(Set<Set<F>> conflicts, Set<F> partialDiagnosis) {
        Iterator<Set<F>> iterator = conflicts.iterator();
        while (iterator.hasNext()) {
            Set<F> next = iterator.next();
            if (hasIntersection(next, partialDiagnosis))
                iterator.remove();
        }
        return conflicts;
    }
}
