package org.exquisite.core.query.qc.heuristic;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.Utils;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.IQueryComputation;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.QPartitionOperations;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;

import java.util.*;

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
        Set<F> originalQuery = queriesIterator.next();
        Set<F> query = originalQuery;
        if (config.enrichQueries) {

            // then in order to come up with a query that is as simple and easy to answer as possible for the
            // respective user U, this query Q can optionally enriched by additional logical formulas by invoking
            // a reasoner for entailments calculation.
            Set<F> enrichedQuery = enrichQuery(originalQuery, qPartition, this.config.diagnosisEngine.getSolver().getDiagnosisModel()); // (4)

            // the previous step causes a larger pool of formulas to select from in the query optimization step
            // which constructs a set-minimal query where most complex sentences in terms of the logical construct
            // and term fault estimates are eliminated from Q and the most simple ones retained
            Set<F> optimizedQuery = optimizeQuery(enrichedQuery, originalQuery, qPartition, this.config.diagnosisEngine, this.config.getDiagnosisEngine().getSolver().getDiagnosisModel().getFormulaWeights()); // (5)
            query = optimizedQuery;
        }
        return new Query<>(query, qPartition);

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

        queriesIterator = queries.iterator();
    }

    /**
     * Searches for an (nearly) optimal q-partition completely without reasoner support for some requirements rm,
     * a probability measure p and a set of leading diagnoses D as given input.
     *
     * @param leadingDiagnoses The leading diagnoses.
     * @param rm A partition requirements measure to find the (nearly) optimal q-partition.
     * @return A (nearly) optimal q-partition.
     */
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
    private Set<F> enrichQuery(Set<F> nextQuery, QPartition<F> qPartition, DiagnosisModel diagnosisModel) {
        Set<F> unionOfLeadingDiagnoses = setUnion(qPartition.dx, qPartition.dnx, qPartition.dz);

        Set<F> set1 = new HashSet<>(diagnosisModel.getPossiblyFaultyFormulas()); // K
        boolean hasBeenRemoved = set1.removeAll(unionOfLeadingDiagnoses);  // K\UD
        assert hasBeenRemoved;
        set1.addAll(diagnosisModel.getCorrectFormulas()); // (K\UD) u Q u B
        set1.addAll(diagnosisModel.getEntailedExamples()); // (K\UD) u Q u B u UP

        Set<F> set2 = new HashSet<>(set1);

        set1.addAll(nextQuery); // (K\UD) U Q

        //Set<F> qImplicit = getEntailments(set1).removeAll(getEntailments(set2)).removeAll(nextQuery);
        Set<F> qImplicit = getEntailments(set1);
        qImplicit.removeAll(getEntailments(set2));
        qImplicit.removeAll(nextQuery);

        Set<F> result = new HashSet<>(nextQuery);
        result.addAll(qImplicit);
        return result;
    }

    /**
     * TODO documentation
     *
     *
     * @param enrichedQuery
     * @param originalQuery
     * @param qPartition TODO documentation
     * @param diagnosisEngine
     * @param formulaWeights  @return TODO documentation
     */
    private Set<F> optimizeQuery(Set<F> enrichedQuery, Set<F> originalQuery, QPartition<F> qPartition, AbstractDiagnosisEngine<F> diagnosisEngine, Map<F,Double> formulaWeights) {

        List<F> sortedFormulas = sort(enrichedQuery, originalQuery, formulaWeights);
        return new HashSet<>(new MinQ().minQ(new ArrayList<F>(sortedFormulas.size()), new ArrayList<F>(sortedFormulas.size()), sortedFormulas, qPartition, diagnosisEngine));
    }

    /**
     *
     * @param enrichedQuery enriched query (Q')
     * @param originalQuery original query (Q)
     * @param formulaWeights
     * @return
     */
    private List<F> sort(final Set<F> enrichedQuery, final Set<F> originalQuery, final Map<F,Double> formulaWeights) {
        Set<F> qImplicit = new HashSet<>(enrichedQuery);
        qImplicit.removeAll(originalQuery);

        List<F> result = new ArrayList<>(qImplicit);

        List<F> sortedQuery = new ArrayList<>(originalQuery);
        sortedQuery.sort((o1, o2) -> formulaWeights.get(o1).compareTo(formulaWeights.get(o2)));

        result.addAll(sortedQuery);
        return result;
    }

    /**
     * TODO
     * @param set1
     * @return
     */
    private Set<F> getEntailments(Set<F> set1) {
        return null; // TODO reasoner Aufruf
    }

    private static <F> Set<F> setUnion(Set<Diagnosis<F>>... set) {
        Set<F> unitedFormulas = new HashSet<>();
        for (Set<Diagnosis<F>> s : set)
            for (Diagnosis<F> d : s)
                unitedFormulas.addAll(d.getFormulas());
        return unitedFormulas;
    }


    public HeuristicQCConfiguration getConfig() {
        return config;
    }

    public void setConfig(HeuristicQCConfiguration config) {
        this.config = config;
    }
}
