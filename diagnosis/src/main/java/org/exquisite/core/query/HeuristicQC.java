package org.exquisite.core.query;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.partitionmeasures.IQPartitionRequirementsMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Framework for a heuristic Query Computation Algorithm for interactive debugging.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 *
 * @author wolfi
 * @author patrick
 */
public class HeuristicQC<F> implements IQueryComputation<F> {

    private static Logger logger = LoggerFactory.getLogger(HeuristicQC.class);

    private IQPartitionRequirementsMeasure rm;

    private QPartition<F> qPartition = null;

    private AbstractDiagnosisEngine<F> diagnosisEngine;

    private DiagnosisModel<F>  diagnosisModel;

    public HeuristicQC(IQPartitionRequirementsMeasure qPartitionRequirementsMeasure, AbstractDiagnosisEngine<F> diagnosisEngine) {
        this.rm = qPartitionRequirementsMeasure;
        this.diagnosisEngine = diagnosisEngine;
        this.diagnosisModel = diagnosisEngine.getSolver().getDiagnosisModel();
    }

    @Override
    public void initialize(Set<Diagnosis<F>> diagnoses)
            throws DiagnosisException {
        calcQuery(this.diagnosisModel, diagnoses, this.diagnosisModel.getFormulaWeights(), this.rm);
    }

    @Override
    public Query<F> next() {
        return qPartition.getQuery();
    }

    @Override
    public boolean hasNext() {
        return qPartition != null;
    }

    @Override
    public void reset() {
        this.qPartition = null;
    }

    /**
     * Query computation algorithm.
     *
     * @param diagnosisModel
     * @param leadingDiagnoses
     * @param formulaWeights
     * @param rm some requirements in order to guide the search faster towards a (nearly) optimal q-partitions.
     */
    private void calcQuery(DiagnosisModel<F>  diagnosisModel, Set<Diagnosis<F>> leadingDiagnoses, Map<F, Float> formulaWeights, IQPartitionRequirementsMeasure rm) {
        List<F> kb = diagnosisModel.getPossiblyFaultyFormulas();

        // we start with the search for an (nearly) optimal q-partition, such that a query associated with this
        // q-partition can be extracted in the next step by selectQueryForQPartition
        qPartition = findQPartition(leadingDiagnoses, formulaWeights, rm); // (2)

        // after a suitable q-partition has been identified, q query Q with qPartition(Q) is calculated such
        // that Q is optimal as to some criterion such as minimum cardinality or maximum likeliness of being
        // answered correctly.
        selectQueryForQPartition(leadingDiagnoses, qPartition); // (3)

        // then in order to come up with a query that is as simple and easy to answer as possible for the
        // respective user U, this query Q can optionally enriched by additional logical formulas by invoking
        // a reasoner for entailments calculation.
        enrichQuery(qPartition, diagnosisModel); // (4)

        // the previous step causes a larger pool of formulas to select from in the query optimization step
        // which constructs a set-minimal query where most complex sentences in terms of the logical construct
        // and term fault estimates are eliminated from Q and the most simple ones retained
        Query<F> q = optimizeQuery(qPartition); // (5)
    }

    /**
     * Searches for an (nearly) optimal q-partition completely without reasoner support for some requirements rm,
     * a probability measure p and a set of leading diagnoses D as given input.
     *
     * @param diagnoses TODO documentation
     * @param formulaWeights TODO documentation
     * @param partitionQualityMeasure TODO documentation
     * @return A suitable q-partition.
     */
    private QPartition<F> findQPartition(Set<Diagnosis<F>> diagnoses, Map formulaWeights, IQPartitionRequirementsMeasure partitionQualityMeasure) {
        assert diagnoses.size() >= 2;

        QPartition<F> partition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), diagnosisEngine.getCostsEstimator());
        QPartition<F> bestPartition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), diagnosisEngine.getCostsEstimator());





        // TODO implement (2) of Main Algorithm


        return null;
    }


    /**
     * TODO documentation
     *
     * @param diagnoses TODO documentation
     * @param qPartition TODO documentation
     */
    private void selectQueryForQPartition(Set<Diagnosis<F>> diagnoses, QPartition<F> qPartition) {
        // TODO implement (3) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @param diagnosisModel TODO documentation
     */
    private void enrichQuery(QPartition<F> qPartition, DiagnosisModel diagnosisModel) {
        // TODO implement (4) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @return TODO documentation
     */
    private Query<F> optimizeQuery(QPartition<F> qPartition) {
        // TODO implement (5) of main algorithm
        return null;
    }

}
