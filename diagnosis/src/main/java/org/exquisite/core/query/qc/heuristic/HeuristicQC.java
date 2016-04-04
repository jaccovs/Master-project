package org.exquisite.core.query.qc.heuristic;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.Utils;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.IQueryComputation;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.QPartitionOperations;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;

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

    private HeuristicQCConfiguration config = null;

    private QPartition<F> qPartition = null;

    private Iterator<Set<F>> queriesIterator;

    public HeuristicQC(HeuristicQCConfiguration config) {
        this.config = config;
    }

    @Override
    public void initialize(Set<Diagnosis<F>> diagnoses) throws DiagnosisException {
        calcQuery(diagnoses);
    }

    @Override
    public Query<F> next() {
        Set<F> nextQuery = queriesIterator.next();
        return new Query<>(nextQuery, qPartition);

    }

    @Override
    public boolean hasNext() {
        return queriesIterator != null && queriesIterator.hasNext();
    }

    @Override
    public void reset() {
        this.qPartition = null;
        this.queriesIterator = null;
    }

    /**
     * Heuristic query computation algorithm. Finds a (nearly) optimal q-partition for a set of diagnoses and
     * generates an iterator over heuristically computed queries.
     *
     * @param leadingDiagnoses the leading diagnoses.
     */
    private void calcQuery(Set<Diagnosis<F>> leadingDiagnoses) {

        // we start with the search for an (nearly) optimal q-partition, such that a query associated with this
        // q-partition can be extracted in the next step by selectQueryForQPartition
        qPartition = findQPartition(leadingDiagnoses, this.config.rm); // (2)

        // after a suitable q-partition has been identified, q query Q with qPartition(Q) is calculated such
        // that Q is optimal as to some criterion such as minimum cardinality or maximum likeliness of being
        // answered correctly.
        Set<Set<F>> queries = selectQueryForQPartition(qPartition); // (3)

        if (config.enrichQueries) {

            // then in order to come up with a query that is as simple and easy to answer as possible for the
            // respective user U, this query Q can optionally enriched by additional logical formulas by invoking
            // a reasoner for entailments calculation.
            enrichQuery(qPartition, this.config.diagnosisEngine.getSolver().getDiagnosisModel()); // (4)

            // the previous step causes a larger pool of formulas to select from in the query optimization step
            // which constructs a set-minimal query where most complex sentences in terms of the logical construct
            // and term fault estimates are eliminated from Q and the most simple ones retained
            Query<F> q = optimizeQuery(qPartition); // (5)
        }

        queriesIterator = queries.iterator();
    }

    private QPartition<F> findQPartition(Set<Diagnosis<F>> leadingDiagnoses, IQPartitionRequirementsMeasure rm) {
        return QPartitionOperations.findQPartition(leadingDiagnoses, rm, this.config.diagnosisEngine.getCostsEstimator());
    }


    /**
     * A q query Q with qPartition(Q) is calculated such that Q is optimal as to some criterion such as minimum
     * cardinality or maximum likeliness of being answered correctly.
     *
     * @param qPartition TODO documentation
     */
    private Set<Set<F>> selectQueryForQPartition(QPartition<F> qPartition) {

        Set<Set<F>> setOfMinTraits = Utils.removeSuperSets(qPartition.diagsTraits.values());

        Set<Set<F>> result = HittingSet.hittingSet(setOfMinTraits, config.timeout, config.minQueries, config.maxQueries, config.sortCriterion);
        if (result.isEmpty()) return new HashSet<>();
        return result;
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

    public HeuristicQCConfiguration getConfig() {
        return config;
    }

    public void setConfig(HeuristicQCConfiguration config) {
        this.config = config;
    }
}
