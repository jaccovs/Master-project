package org.exquisite.core.query.qc.heuristic;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.Utils;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.IQueryComputation;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;
import org.exquisite.core.query.qc.heuristic.sortcriteria.MinQueryCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Framework for a heuristic Query Computation Algorithm for knowledge base debugging.
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
    public void initialize(Set<Diagnosis<F>> diagnoses) throws DiagnosisException {
        calcQuery(this.diagnosisModel, diagnoses, this.rm);
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
     * @param rm some requirements in order to guide the search faster towards a (nearly) optimal q-partitions.
     */
    public void calcQuery(DiagnosisModel<F> diagnosisModel, Set<Diagnosis<F>> leadingDiagnoses, IQPartitionRequirementsMeasure rm) {

        // we start with the search for an (nearly) optimal q-partition, such that a query associated with this
        // q-partition can be extracted in the next step by selectQueryForQPartition
        qPartition = findQPartition(leadingDiagnoses, rm); // (2)

        // after a suitable q-partition has been identified, q query Q with qPartition(Q) is calculated such
        // that Q is optimal as to some criterion such as minimum cardinality or maximum likeliness of being
        // answered correctly.
        Set<F> query = selectQueryForQPartition(qPartition); // (3)

        // then in order to come up with a query that is as simple and easy to answer as possible for the
        // respective user U, this query Q can optionally enriched by additional logical formulas by invoking
        // a reasoner for entailments calculation.
        enrichQuery(qPartition, diagnosisModel); // (4)

        // the previous step causes a larger pool of formulas to select from in the query optimization step
        // which constructs a set-minimal query where most complex sentences in terms of the logical construct
        // and term fault estimates are eliminated from Q and the most simple ones retained
        Query<F> q = optimizeQuery(qPartition); // (5)
    }

    private QPartition<F> findQPartition(Set<Diagnosis<F>> leadingDiagnoses, IQPartitionRequirementsMeasure rm) {
        return OptimalQPartitionFinder.findQPartition(leadingDiagnoses, rm, this.diagnosisEngine.getCostsEstimator());
    }


    /**
     * A q query Q with qPartition(Q) is calculated such that Q is optimal as to some criterion such as minimum
     * cardinality or maximum likeliness of being answered correctly.
     *
     * @param qPartition TODO documentation
     */
    private Set<F> selectQueryForQPartition(QPartition<F> qPartition) {

        Set<Set<F>> setOfMinTraits = Utils.removeSuperSets(qPartition.diagsTraits.values());//getSetOfMinTraits(qPartition.diagsTraits.values());

        Set<Set<F>> result = HittingSet.hittingSet(setOfMinTraits,1000,1,1,new MinQueryCardinality());
        if (result.isEmpty()) return null;
        return result.iterator().next();
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

    public IQPartitionRequirementsMeasure getPartitionRequirementsMeasure() {
        return rm;
    }

    public AbstractDiagnosisEngine<F> getDiagnosisEngine() {
        return diagnosisEngine;
    }



}
