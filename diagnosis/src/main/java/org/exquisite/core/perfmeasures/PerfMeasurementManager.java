package org.exquisite.core.perfmeasures;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kostya on 23.11.2015.
 */
public class PerfMeasurementManager {

    public static final String TIMER_DIAGNOSIS_SESSION = "diagnosis.session";
    public static final String TIMER_INTERACTIVE_SESSION = "interactive.session";
    public static final String TIMER_INTERACTIVE_DIAGNOSES = "time.interactive.diagnoses";
    public static final String TIMER_INVERSE_DIAGNOSES = "time.inverse.diagnoses";
    public static final String TIMER_SOLVER = "solver.time";
    public static final String TIMER_SOLVER_ISCONSISTENT_FORMULAS = "time.solver.isConsistent(formulas)"; // timer for ISolver.isConsistent(Collection<F>)
    public static final String TIMER_SOLVER_ISCONSISTENT = "time.solver.isConsistent()"; // timer for AbstractSolver.isConsistent()
    public static final String TIMER_SOLVER_ISENTAILED = "time.solver.isEntailed()"; // timer for AbstractSolver.isEntailed(Collection<F>)
    public static final String TIMER_SOLVER_CALCULATE_ENTAILMENTS = "time.solver.calculateEntailments()"; // timer for AbstractSolver.calculateEntailments()
    public static final String TIMER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST = "time.querycomputation.heuristic.isQPartConst()"; // timer for MinQ.isQPartConst()
    public static final String TIMER_QUERYCOMPUTATION_HEURISTIC_FINDQPARTITION = "time.querycomputation.heuristic.findQPartition()"; // timer for HeuristicQueryComputation.findQPartition()
    public static final String TIMER_QUERYCOMPUTATION_HEURISTIC_SELECTQUERIES = "time.querycomputation.heuristic.selectQueries()"; // timer for HeuristicQueryComputation.selectQueriesForQPartition()
    public static final String TIMER_QUERYCOMPUTATION_HEURISTIC_ENRICHQUERY = "time.querycomputation.heuristic.enrichQuery()"; // timer for HeuristicQueryComputation.enrichQuery()
    public static final String TIMER_QUERYCOMPUTATION_HEURISTIC_OPTIMIZEQUERY = "time.querycomputation.heuristic.optimizeQuery()"; // timer for HeuristicQueryComputation.optimizeQuery()

    public static final String COUNTER_PROPAGATION = "propagation.count";
    public static final String COUNTER_CONSTRUCTED_NODES = "constructed.nodes";
    public static final String COUNTER_SOLVER_CALLS = "solver.calls";
    public static final String COUNTER_CSP_SOLUTIONS = "csp.solution.count";
    public static final String COUNTER_SEARCH_CONFLICTS = "searches.for.conflicts";
    public static final String COUNTER_QXP_CALLS = "qxp.calls";
    public static final String COUNTER_MXP_CONFLICTS = "counter.MXP.conflicts";
    public static final String COUNTER_MXP_SPLITTING = "counter.MXP.splitting";
    public static final String COUNTER_REUSE = "counter.reuse";
    public static final String COUNTER_INTERACTIVE_NQUERIES = "interactive.nqueries";
    public static final String COUNTER_INTERACTIVE_PARTITIONS = "interactive.partitions";
    public static final String COUNTER_INTERACTIVE_QSTMT = "numberOfQueriedStatements";
    public static final String COUNTER_INTERACTIVE_DIAGNOSES = "numberOfDiagnosisComputations";
    public static final String COUNTER_INVERSE_DIAGNOSES = "counter.inverse.diagnoses"; // number of calling calculateDiagnoses()
    public static final String COUNTER_SOLVER_ISCONSISTENT_FORMULAS = "count.calls.solver.isConsistent(formulas)"; // counter for ISolver.isConsistent(Collection<F>)
    public static final String COUNTER_SOLVER_ISCONSISTENT = "count.calls.solver.isConsistent()"; // counter for AbstractSolver.isConsistent()
    public static final String COUNTER_SOLVER_ISENTAILED = "count.calls.solver.isEntailed()"; // counter for AbstractSolver.isEntailed(Collection<F>)
    public static final String COUNTER_SOLVER_CALCULATE_ENTAILMENTS = "count.calls.solver.calculateEntailments()"; // counter for ISolver.calculateEntailments()
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST = "count.calls.querycomputation.heuristic.isQPartConst()"; // counter for MinQ.isQPartConst()
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_QPARTITIONS = "count.querycomputation.heuristic.generated.qpartitions";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_QPARTITIONS = "count.querycomputation.heuristic.expanded.qpartitions";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_BACKTRACKINGS = "count.querycomputation.heuristic.backtrackings";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_PRUNINGS = "count.querycomputation.heuristic.prunings";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS = "count.querycomputation.heuristic.traits";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS_SIZE = "size.querycomputation.heuristic.traits";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_HS_NODES = "count.querycomputation.heuristic.generated.hs.nodes";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_HS_NODES = "count.querycomputation.heuristic.expanded.hs.nodes";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_LABELED_HS_NODES = "count.querycomputation.heuristic.labeled.hs.nodes";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_CANONICAL_QUERIES_SIZE = "size.querycomputation.heuristic.canonical.queries";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_BEFORE_ENRICHMENT = "size.querycomputation.heuristic.queries.before.enrichment";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_ENRICHTMENT = "size.querycomputation.heuristic.queries.after.enrichment";
    public static final String COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_MINIMIZE = "size.querycomputation.heuristic.queries.after.minimize";
    public static final String COUNTER_QUERYCOMPUTATION_NAIVE_QUERYPOOL_SIZE = "size.querycomputation.naive.querypool";

    private static HashMap<String, Timer> timers = new HashMap<>();
    private static HashMap<String, Counter> counters = new HashMap<>();

    /**
     * Returns a timer with the given name. If timer does not exist, it will be created by the method and added to
     * the set of timers
     *
     * @param name of the timer
     * @return a timer
     */
    public static Timer getTimer(String name) {
        return timers.computeIfAbsent(name, (key) -> new Timer(name));
    }

    /**
     * Returns a counter with the given name. If counter does not exist, it will be created by the method and added
     * to the set of counters
     *
     * @param name of the counter
     * @return a timer
     */

    public static Counter getCounter(String name) {
        return counters.computeIfAbsent(name, (key) -> new Counter(name));
    }

    /**
     * Increments a counter and returns its new value
     *
     * @param name of the counter
     * @return new value of the counter
     */
    public static long incrementCounter(String name) {
        return getCounter(name).increment(1);
    }

    /**
     * Increments counter to a ginen number of steps
     *
     * @param name of the counter
     * @param step to increment the counter
     * @return new value of the counter
     */
    public static long incrementCounter(String name, int step) {
        return getCounter(name).increment(step);
    }

    /**
     * Starts a timer with the given name
     *
     * @param name of the counter
     */
    public static void start(String name) {
        getTimer(name).start();
    }

    /**
     * Stops a timer with the given name
     *
     * @param name of the timer
     * @return elapsed time since the timer was started
     */
    public static Long stop(String name) {
        return getTimer(name).stop();
    }

    /**
     * @return an unmodifiable map of timers
     */
    public static Map<String, Timer> getTimers() {
        return Collections.unmodifiableMap(timers);
    }

    /**
     * @return an unmodifiable map of counters
     */
    public static Map<String, Counter> getCounters() {
        return Collections.unmodifiableMap(counters);
    }

    /**
     * Reinitialize all existing counters. Use this method to reset the perfmeasures manager in case one and the same
     * diagnosis routine is executed multiple times.
     */
    public static void reset() {
        counters = new HashMap<>();
        timers = new HashMap<>();
    }
}
