package org.exquisite.core.engines;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.costestimators.FormulaWeightsCostEstimator;
import org.exquisite.core.costestimators.ICostsEstimator;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Answer;
import org.exquisite.core.query.IQueryAnswering;
import org.exquisite.core.query.IQueryComputation;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
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
 * @author Schmitz
 * @author wolfi
 * @author patrick
 */
public class InteractiveDiagnosisEngine<F> extends AbstractDiagnosisEngine<F> implements IDiagnosisEngine<F> {

    private static final Logger logger = LoggerFactory.getLogger(InteractiveDiagnosisEngine.class);

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
    private AbstractDiagnosisEngine<F> innerEngine;

    /**
     * Query computation algorithm used in the interactive session
     */
    private IQueryComputation<F> queryComputation;

    /**
     * Estimator of the costs for a set of statements
     */
    private ICostsEstimator<F> costsEstimator;

    /**
     * Threshold that allows to stop computations if one of the diagnoses is above it.
     * Default value is <code>0.95</code>
     */
    private BigDecimal threshold = new BigDecimal(0.95);

    /**
     * An interface that must be implemented by the front-ends allowing a user to answer a query
     */
    private IQueryAnswering<F> queryAnswering;

    public InteractiveDiagnosisEngine(AbstractDiagnosisEngine<F> innerEngine,
                                      IQueryComputation<F> queryComputation,
                                      IQueryAnswering<F> queryAnswering,
                                      ICostsEstimator<F> estimator) {
        super(innerEngine.getSolver());
        this.costsEstimator = estimator;
        this.innerEngine = innerEngine;
        this.queryComputation = queryComputation;
        this.queryAnswering = queryAnswering;
    }

    public InteractiveDiagnosisEngine(ISolver<F> solver, IQueryAnswering<F> queryAnswering) {
        super(solver);
        this.innerEngine = new HSTreeEngine<>(solver);
        DiagnosisModel<F> diagnosisModel = this.innerEngine.getSolver().getDiagnosisModel();
        this.costsEstimator = new FormulaWeightsCostEstimator<>(diagnosisModel.getPossiblyFaultyFormulas(), diagnosisModel.getFormulaWeights());
        //IQPartitionRequirementsMeasure<F> partitionQualityMeasure = new EntropyBasedMeasure<>(new BigDecimal(0.05));
        HeuristicConfiguration config = new HeuristicConfiguration(this.innerEngine);
        this.queryComputation = new HeuristicQueryComputation<>(config);
        this.queryAnswering = queryAnswering;
    }


    public Set<Diagnosis<F>> calculateDiagnoses() throws DiagnosisException {

        // TODO check if the set of diagnoses can be always computed and the set of conflicts updated
        Set<Diagnosis<F>> diagnoses = new HashSet<>(this.diagnosesPerQuery);
        Set<Set<F>> conflicts = new HashSet<>();

        start(TIMER_INTERACTIVE_SESSION);
        do {
            innerEngine.resetEngine();
            logger.debug("Searching for diagnoses");

            start(TIMER_INTERACTIVE_DIAGNOSES);
            int maxNumberOfDiagnoses = this.diagnosesPerQuery - diagnoses.size();
            if (maxNumberOfDiagnoses > 0) {
                conflicts = removeHitConflicts(conflicts, diagnoses);
                innerEngine.setConflicts(conflicts);
                innerEngine.setMaxNumberOfDiagnoses(maxNumberOfDiagnoses);
                diagnoses.addAll(innerEngine.calculateDiagnoses());
                conflicts.addAll(innerEngine.getConflicts());
            }
            stop(TIMER_INTERACTIVE_DIAGNOSES);

            incrementCounter(COUNTER_INTERACTIVE_DIAGNOSES);

            if (diagnoses.size() > 1) {
                if (logger.isInfoEnabled()) {
                    logger.info("Found " + diagnoses.size() + " diagnoses");
                }

                queryComputation.initialize(diagnoses);
                if (queryComputation.hasNext()) {
                    Query<F> query = queryComputation.next();
                    Answer<F> answer = this.queryAnswering.getAnswer(query);
                    getDiagnosisModel().getEntailedExamples().addAll(answer.positive);
                    getDiagnosisModel().getNotEntailedExamples().addAll(answer.negative);
                    // TODO check if every element of the set of diagnoses is still a diagnosis according to the definition
                    // TODO minimize conflicts wrt the updated DiagnosisModel
                }
            }
        } while (diagnoses.size() > 1 || belowThreshold(diagnoses));
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

    private Set<Set<F>> removeHitConflicts(Set<Set<F>> conflicts, Set<Diagnosis<F>> diagnoses) {
        Iterator<Set<F>> iterator = conflicts.iterator();
        while (iterator.hasNext()) {
            Set<F> next = iterator.next();
            for (Diagnosis<F> diagnosis : diagnoses)
                if (hasIntersection(next, diagnosis.getFormulas()))
                    iterator.remove();
        }
        return conflicts;
    }

    public IQueryComputation<F> getQueryComputation() {
        return queryComputation;
    }
}
