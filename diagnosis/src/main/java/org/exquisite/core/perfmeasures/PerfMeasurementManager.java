package org.exquisite.core.perfmeasures;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kostya on 23.11.2015.
 */
public class PerfMeasurementManager {

    public static final String TIMER_SOLVER = "solver.time";
    public static final String TIMER_DIAGNOSIS_SESSION = "diagnosis.session";
    public static final String TIMER_INTERACTIVE_SESSION = "interactive.session";
    public static final String TIMER_INTERACTIVE_DIAGNOSES = "interactive.diagnoses";
    public static final String TIMER_INVERSE_SESSION = "inverse.session";

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
