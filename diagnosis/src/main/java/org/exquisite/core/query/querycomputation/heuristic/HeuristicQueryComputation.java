package org.exquisite.core.query.querycomputation.heuristic;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.Utils;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.QPartitionOperations;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;

import java.util.*;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.*;

/**
 * A heuristic query computation algorithm for interactive knowledge base debugging.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 *
 * @author wolfi
 * @author patrick
 */
public class HeuristicQueryComputation<F> implements IQueryComputation<F> {

    private HeuristicConfiguration config = null;

    private QPartition<F> qPartition = null;

    private Iterator<Set<F>> queriesIterator;

    public HeuristicQueryComputation(HeuristicConfiguration config) {
        this.config = config;
    }

    @Override
    public void initialize(Set<Diagnosis<F>> diagnoses) throws DiagnosisException {
        queriesIterator = null;
        qPartition = null;
        calcQuery(diagnoses);
    }

    @Override
    public Query<F> next() {
        Set<F> originalQuery = queriesIterator.next(); // (next) query computed in steps (1) and (2) -> see calcQuery()
        Set<F> query = originalQuery;
        if (config.enrichQueries) {

            // (3) in order to come up with a query that is as simple and easy to answer as possible for the
            // respective user U, the query can optionally enriched by additional logical formulas by invoking
            // a reasoner for entailments calculation.
            Set<F> enrichedQuery = enrichQuery(originalQuery, qPartition, config.diagnosisEngine.getSolver().getDiagnosisModel());

            // (4) the previous step causes a larger pool of formulas to select from in the query optimization step
            // which constructs a set-minimal query where most complex sentences in terms of the logical construct
            // and term fault estimates are eliminated from Q and the most simple ones retained
            incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_ENRICHTMENT, enrichedQuery.size()); // SIZE of queries vor minimieren
            Set<F> optimizedQuery = optimizeQuery(enrichedQuery, originalQuery, qPartition, config.diagnosisEngine);
            incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_MINIMIZE, optimizedQuery.size()); // SIZE of queries nach minimieren
            query = optimizedQuery;
        }
        Query<F> nextQuery = new Query<>(query, qPartition);
        nextQuery.score = config.getRm().getScore(nextQuery);
        return nextQuery;
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

        // (1) we start with the search for an (nearly) optimal q-partition, such that a query associated with this
        // q-partition can be extracted in the next step by selectQueriesForQPartition
        qPartition = findQPartition(leadingDiagnoses, this.config.rm);

        incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS, qPartition.diagsTraits.size()); // Nr of DiagTraits
        int count = 0;
        for (Set<F> set : qPartition.diagsTraits.values()) count+=set.size();
        incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS_SIZE, count); // cnt size of traits for each trait

        // canonical query
        Set<F> canonicalQuery = new HashSet<>();
        Set<F> unionOfDxDiagnoses = new HashSet<>();
        for (Diagnosis<F> diagnosis : leadingDiagnoses) {
            canonicalQuery.addAll(diagnosis.getFormulas());
            if (qPartition.dx.contains(diagnosis)) {
                unionOfDxDiagnoses.addAll(diagnosis.getFormulas());
            }
        }
        canonicalQuery.removeAll(unionOfDxDiagnoses);
        incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_CANONICAL_QUERIES_SIZE, canonicalQuery.size());

        // (2) after a suitable q-partition has been identified, q query Q with qPartition(Q) is calculated such
        // that Q is optimal as to some criterion such as minimum cardinality or maximum likeliness of being
        // answered correctly.
        Set<Set<F>> queries = selectQueriesForQPartition(qPartition);

        assert queries.size() == 1;
        incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_BEFORE_ENRICHMENT, queries.iterator().next().size());

        // creates an iterator on the queries to be used in hasNext() and next()
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
        start(TIMER_QUERYCOMPUTATION_HEURISTIC_FINDQPARTITION);
        try {
            return QPartitionOperations.findQPartition(leadingDiagnoses, rm, config.diagnosisEngine.getCostsEstimator());
        } finally {
            stop(TIMER_QUERYCOMPUTATION_HEURISTIC_FINDQPARTITION);
        }
    }

    /**
     * Queries with given q-partition (qPartition) are calculated such that Q is optimal to some criterion such as minimum
     * cardinality or maximum likeliness of being answered correctly. This criterion is given via ISortCriterion.
     *
     * This query computation is done by the computation of the hitting sets of the traits (diagsTraits) of the q-partition.
     * This method will return at least min and at most max queries as stated in the HeuristicConfiguration if timeout
     * is big enough.
     *
     * @param qPartition A (nearly) optimal q-partion computed in findQPartition (1).
     */
    private Set<Set<F>> selectQueriesForQPartition(QPartition<F> qPartition) {
        start(TIMER_QUERYCOMPUTATION_HEURISTIC_SELECTQUERIES);
        try {
            Set<Set<F>> setOfMinTraits = Utils.removeSuperSets(qPartition.diagsTraits.values());
            return HittingSet.hittingSet(setOfMinTraits, config.timeout, config.minQueries, config.maxQueries, config.sortCriterion);
        } finally {
            stop(TIMER_QUERYCOMPUTATION_HEURISTIC_SELECTQUERIES);
        }
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
        start(TIMER_QUERYCOMPUTATION_HEURISTIC_ENRICHQUERY);
        try {
            Set<F> unionOfLeadingDiagnoses = setUnion(qPartition.dx, qPartition.dnx, qPartition.dz);

            // step 1: calculate: Q_impl := [E((K\UD) u Q u B u UP) \ E((K\UD) u B u UP)] \ Q
            Set<F> set1 = new HashSet<>(diagnosisModel.getPossiblyFaultyFormulas());
            boolean hasBeenRemoved = set1.removeAll(unionOfLeadingDiagnoses);
            assert hasBeenRemoved; // assertion that something has been removed
            set1.addAll(diagnosisModel.getCorrectFormulas());
            set1.addAll(diagnosisModel.getEntailedExamples());

            Set<F> set2 = new HashSet<>(set1); // copy of (K\UD) u B u UP

            set1.addAll(query); // (K\UD) u B u UP u Q

            Set<F> qImplicit = getEntailments(set1); // E((K\UD) u Q u B u UP)
            qImplicit.removeAll(getEntailments(set2)); // [E((K\UD) u Q u B u UP) \ E((K\UD) u B u UP)]
            qImplicit.removeAll(query); // [E((K\UD) u Q u B u UP) \ E((K\UD) u B u UP)] \ Q

            // step 2: Q <- Q u Q_impl
            Set<F> result = new HashSet<>(query);
            result.addAll(qImplicit);
            return result;
        } finally {
            stop(TIMER_QUERYCOMPUTATION_HEURISTIC_ENRICHQUERY);
        }
    }

    /**
     * Calls a reasoner to calculate entailments given a set of formulas. Expensive!
     * @param set
     * @return Entailed formulas
     */
    private Set<F> getEntailments(Set<F> set) {
        Set<F> entailments = config.getDiagnosisEngine().getSolver().calculateEntailments(set);
        return entailments;
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
        start(TIMER_QUERYCOMPUTATION_HEURISTIC_OPTIMIZEQUERY);
        try {
            List<F> sortedFormulas = sort(enrichedQuery, originalQuery, diagnosisEngine.getSolver().getDiagnosisModel().getFormulaWeights());
            Set<F> optimizedQuery = new HashSet<F>(new MinQ<F>().minQ(
                                                   new ArrayList<F>(sortedFormulas.size()),
                                                   new ArrayList<F>(sortedFormulas.size()),
                                                   sortedFormulas,
                                                   qPartition,
                                                   diagnosisEngine));
            return optimizedQuery;
        } finally {
            stop(TIMER_QUERYCOMPUTATION_HEURISTIC_OPTIMIZEQUERY);
        }
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
        sortedQuery.sort((o1, o2) -> {
            Float aFloat = formulaWeights.get(o1);
            Float anotherFloat = formulaWeights.get(o2);
            if (aFloat != null && anotherFloat != null)
                return aFloat.compareTo(anotherFloat);
            else
                return 0;
        });

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
    public HeuristicConfiguration getConfig() {
        return config;
    }

    /**
     * Set a new configuration for another heuristic query computation. Proper use of initialize, init, hasNext and next
     * is advised!
     *
     * @param config Another configuration.
     */
    public void setConfig(HeuristicConfiguration config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return new StringBuilder("HeuristicQueryComputation{").append(config).append('}').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeuristicQueryComputation<?> that = (HeuristicQueryComputation<?>) o;

        return config != null ? config.equals(that.config) : that.config == null;
    }

    @Override
    public int hashCode() {
        return config != null ? config.hashCode() : 0;
    }
}
