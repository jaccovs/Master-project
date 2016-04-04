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

            // in order to come up with a query that is as simple and easy to answer as possible for the
            // respective user U, the query can optionally enriched by additional logical formulas by invoking
            // a reasoner for entailments calculation.
            Set<F> enrichedQuery = enrichQuery(originalQuery, qPartition, config.diagnosisEngine.getSolver().getDiagnosisModel()); // (3)

            // the previous step causes a larger pool of formulas to select from in the query optimization step
            // which constructs a set-minimal query where most complex sentences in terms of the logical construct
            // and term fault estimates are eliminated from Q and the most simple ones retained
            Set<F> optimizedQuery = optimizeQuery(enrichedQuery, originalQuery, qPartition, config.diagnosisEngine); // (4)
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
        // q-partition can be extracted in the next step by selectQueriesForQPartition
        qPartition = findQPartition(leadingDiagnoses, this.config.rm); // (1)

        // after a suitable q-partition has been identified, q query Q with qPartition(Q) is calculated such
        // that Q is optimal as to some criterion such as minimum cardinality or maximum likeliness of being
        // answered correctly.
        Set<Set<F>> queries = selectQueriesForQPartition(qPartition); // (2)

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
        return QPartitionOperations.findQPartition(leadingDiagnoses, rm, config.diagnosisEngine.getCostsEstimator());
    }

    /**
     * Queries with given q-partition (qPartition) are calculated such that Q is optimal to some criterion such as minimum
     * cardinality or maximum likeliness of being answered correctly. This criterion is given via ISortCriterion.
     *
     * This query computation is done by the computation of the hitting sets of the traits (diagsTraits) of the q-partition.
     * This method will return at least min and at most max queries as stated in the HeuristicQCConfiguration if timeout
     * is big enough.
     *
     * @param qPartition A (nearly) optimal q-partion computed in findQPartition (1).
     */
    private Set<Set<F>> selectQueriesForQPartition(QPartition<F> qPartition) {

        Set<Set<F>> setOfMinTraits = Utils.removeSuperSets(qPartition.diagsTraits.values());

        return HittingSet.hittingSet(setOfMinTraits, config.timeout, config.minQueries, config.maxQueries, config.sortCriterion);
    }

    /**
     * A query can be enriched with additional logical formulas by invoking a reasoner for entailments calculation.
     *
     * <p>
     * Description of algorithm:
     * <ul>
     *     <li>u = union set operation</li>
     *     <li>E: entailment operation (= call of reasoner, calculates all entailments for DPI)</li>
     *     <li>UD = union of all leading diagnoses (represented by dx, dnx, dz in qPartition)</li>
     *     <li>UP = union of all elements of P in DPI (K,B,P,N)R</li>
     *     <li>step 1: calculate: Q_impl := [E((K\UD) u Q u B u UP) \ E((K\UD) u B u UP)] \ Q</li>
     *     <li>step 2: Q <- Q u Q_impl</></li>
     * </ul>
     *
     * </p>
     *
     * @param query A query Q.
     * @param qPartition The q-partition this query belongs to, see findQPartition (1).
     * @param diagnosisModel A diagnosis model representing the DPI (K,B,P,N)R.
     * @return An new query enriched by addition logical formulas by invoking a reasoner for entailments calculation.
     */
    private Set<F> enrichQuery(final Set<F> query, final QPartition<F> qPartition, final DiagnosisModel<F> diagnosisModel) {
        Set<F> unionOfLeadingDiagnoses = setUnion(qPartition.dx, qPartition.dnx, qPartition.dz);

        // step 1
        Set<F> set1 = new HashSet<>(diagnosisModel.getPossiblyFaultyFormulas()); // K
        boolean hasBeenRemoved = set1.removeAll(unionOfLeadingDiagnoses);  // K\UD
        assert hasBeenRemoved;

        set1.addAll(diagnosisModel.getCorrectFormulas()); // (K\UD) u B
        set1.addAll(diagnosisModel.getEntailedExamples()); // (K\UD) u B u UP

        Set<F> set2 = new HashSet<>(set1); // copy of (K\UD) u B u UP

        set1.addAll(query); // (K\UD) u B u UP u Q

        Set<F> qImplicit = getEntailments(set1); // E((K\UD) u Q u B u UP)
        qImplicit.removeAll(getEntailments(set2)); // [E((K\UD) u Q u B u UP) \ E((K\UD) u B u UP)]
        qImplicit.removeAll(query); // [E((K\UD) u Q u B u UP) \ E((K\UD) u B u UP)] \ Q

        // step 2
        Set<F> result = new HashSet<>(query);
        result.addAll(qImplicit);
        return result;
    }

    /**
     * Call a reasoner to calculate all entailments.
     *
     * @param set set of formulas.
     * @return open
     */
    private Set<F> getEntailments(Set<F> set) {
        return new HashSet<>(); // TODO reasoner Aufruf
    }

    /**
     * The query optimization constructs a set-minimal query where most complex sentences in terms of the logical
     * construct and term fault estimates are eliminated from Q and the most simple ones retained. This is done by
     * a variant of QuickXplain (MinQ).
     *
     * @param enrichedQuery The enriched query computed in enrichedQuery (3).
     * @param originalQuery The query computed after findQPartition (1) and selectQueriesForQPartition (2).
     * @param qPartition The q-partition computed in findQPartition (1).
     * @param diagnosisEngine The engine containing the solver.
     * @return An optimized query.
     */
    private Set<F> optimizeQuery(final Set<F> enrichedQuery,
                                 final Set<F> originalQuery,
                                 final QPartition<F> qPartition,
                                 final AbstractDiagnosisEngine<F> diagnosisEngine) {

        List<F> sortedFormulas = sort(enrichedQuery, originalQuery, diagnosisEngine.getSolver().getDiagnosisModel().getFormulaWeights());

        return new HashSet<F>(new MinQ<F>().minQ(
                new ArrayList<F>(sortedFormulas.size()),
                new ArrayList<F>(sortedFormulas.size()),
                sortedFormulas,
                qPartition,
                diagnosisEngine));
    }

    /**
     *
     * @param enrichedQuery enriched query (Q')
     * @param originalQuery original query (Q)
     * @param formulaWeights
     * @return
     */
    private List<F> sort(final Set<F> enrichedQuery, final Set<F> originalQuery, final Map<F,Float> formulaWeights) {
        Set<F> qImplicit = new HashSet<>(enrichedQuery);
        qImplicit.removeAll(originalQuery);

        List<F> result = new ArrayList<>(qImplicit);

        List<F> sortedQuery = new ArrayList<>(originalQuery); // sort originalQuery ascending by natural order
        sortedQuery.sort((o1, o2) -> formulaWeights.get(o1).compareTo(formulaWeights.get(o2)));

        result.addAll(sortedQuery); // append the sorted list
        return result;
    }

    /**
     * Union of all formulas from sets of diagnoses.
     *
     * @param set Many sets of diagnoses.
     * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
     * @return Union of all formulas from sets of diagnoses.
     */
    @SafeVarargs
    private static <F> Set<F> setUnion(Set<Diagnosis<F>>... set) {
        Set<F> unitedFormulas = new HashSet<>();
        for (Set<Diagnosis<F>> s : set)
            for (Diagnosis<F> d : s)
                unitedFormulas.addAll(d.getFormulas());
        return unitedFormulas;
    }

    /**
     * Get the configuration of this heuristic query computation.
     *
     * @return configuration for heuristic query computation.
     */
    public HeuristicQCConfiguration getConfig() {
        return config;
    }

    /**
     * Set a new configuration for another heuristic query computation. Proper use of initialize, init, hasNext and next
     * is advised!
     *
     * @param config Another configuration.
     */
    public void setConfig(HeuristicQCConfiguration config) {
        this.config = config;
    }
}
