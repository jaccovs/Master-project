package org.exquisite.evals.conferences.ecai2016;

import org.exquisite.core.perfmeasures.*;
import org.exquisite.core.perfmeasures.Timer;
import org.exquisite.core.query.querycomputation.IQueryComputation;

import java.util.*;

import static org.exquisite.evals.conferences.ecai2016.Evaluation.TIMER_QUERY_CALCULATION;

/**
 * @author wolfi
 */
public class Statistics {

    private int diagnosesSize;
    private String ontology;

    public Statistics(int diagnosesSize, String ontology) {
        this.diagnosesSize = diagnosesSize;
        this.ontology = ontology;
    }

    private Map<IQueryComputation, List<Iteration>> iterations = new LinkedHashMap<>(16, 0.75f, false); // with last parameter we force FIFO

    private List<Iteration> getIterations(IQueryComputation qc) {
        return iterations.computeIfAbsent(qc, (key) -> new ArrayList<>());
    }

    public void addIteration(IQueryComputation qc, Iteration iteration) {
        getIterations(qc).add(iteration);
    }

    public String getStatistics() {
        StringBuilder sb = new StringBuilder(this.getHeader()).append("\n");
        for (IQueryComputation qc : iterations.keySet())
            sb.append(getLineForQueryComputation(qc)).append("\n");
        sb.append('\n');
        return sb.toString();
    }

    private String getHeader() {

        StringBuilder sb = new StringBuilder("\nStatistics for ").append(ontology).append(" and ").append(diagnosesSize).append(" diagnoses and ").append(this.iterations.values().iterator().next().size()).append(" iterations: \n");
        sb.append("Query Computation;");
        // diagnoses
        sb.append("AVG ").append(Evaluation.TIMER_DIAGNOSES_CALCULATION).append(';');
        sb.append("diag: AVG # calls to solver.isConsistent()").append(';');
        sb.append("diag: AVG time used for solver.isConsistent()").append(';');
        sb.append("diag: AVG # calls to solver.isConsistent(Formulas)").append(';');
        sb.append("diag: AVG time used for solver.isConsistent(Formulas)").append(';');
        // queries
        sb.append("AVG ").append(TIMER_QUERY_CALCULATION).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT_FORMULAS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_SOLVER_ISENTAILED).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_SOLVER_ISENTAILED).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_SOLVER_CALCULATE_ENTAILMENTS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_SOLVER_CALCULATE_ENTAILMENTS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST).append(';');

        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_QPARTITIONS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_QPARTITIONS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_BACKTRACKINGS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_PRUNINGS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS_SIZE).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_HS_NODES).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_HS_NODES).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_LABELED_HS_NODES).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_CANONICAL_QUERIES_SIZE).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_BEFORE_ENRICHMENT).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_ENRICHTMENT).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_MINIMIZE).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_NAIVE_QUERYPOOL_SIZE).append(';');


        sb.append("AVG ").append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_FINDQPARTITION).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_SELECTQUERIES).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ENRICHQUERY).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_OPTIMIZEQUERY).append(';');
        return sb.toString();
    }

    private String getLineForQueryComputation(IQueryComputation qc) {
        final List<Iteration> iterations = this.iterations.get(qc);
        StringBuilder sb = new StringBuilder(qc.toString()).append(';');
        // diagnoses
        sb.append(computeAvgTimeDiags(iterations,Evaluation.TIMER_DIAGNOSES_CALCULATION)).append(';');
        sb.append(computeAvgCountDiags(iterations,PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT)).append(';');
        sb.append(computeAvgTimeDiags(iterations,PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT)).append(';');
        sb.append(computeAvgCountDiags(iterations,PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
        sb.append(computeAvgTimeDiags(iterations,PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
        // queries
        sb.append(computeAvgTimeQueries(iterations,TIMER_QUERY_CALCULATION)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT)).append(';');
        sb.append(computeAvgTimeQueries(iterations,PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
        sb.append(computeAvgTimeQueries(iterations,PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_SOLVER_ISENTAILED)).append(';');
        sb.append(computeAvgTimeQueries(iterations,PerfMeasurementManager.TIMER_SOLVER_ISENTAILED)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_SOLVER_CALCULATE_ENTAILMENTS)).append(';');
        sb.append(computeAvgTimeQueries(iterations,PerfMeasurementManager.TIMER_SOLVER_CALCULATE_ENTAILMENTS)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST)).append(';');
        sb.append(computeAvgTimeQueries(iterations,PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST)).append(';');

        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_QPARTITIONS)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_QPARTITIONS)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_BACKTRACKINGS)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_PRUNINGS)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS_SIZE)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_HS_NODES)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_HS_NODES)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_LABELED_HS_NODES)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_CANONICAL_QUERIES_SIZE)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_BEFORE_ENRICHMENT)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_ENRICHTMENT)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_MINIMIZE)).append(';');
        sb.append(computeAvgCountQueries(iterations,PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_NAIVE_QUERYPOOL_SIZE)).append(';');


        sb.append(computeAvgTimeQueries(iterations,PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_FINDQPARTITION)).append(';');
        sb.append(computeAvgTimeQueries(iterations,PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_SELECTQUERIES)).append(';');
        sb.append(computeAvgTimeQueries(iterations,PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ENRICHQUERY)).append(';');
        sb.append(computeAvgTimeQueries(iterations,PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_OPTIMIZEQUERY)).append(';');
        return sb.toString();
    }

    private String computeAvgTimeDiags(final List<Iteration> iterations,final String key) {
        long total = 0;
        for (Iteration it : iterations) {
            final Timer timer = it.diagnosesTimers.get(key);
            total += (timer!=null) ? timer.total() : 0;
        }
        //return Double.toString(((double)total/(double)iterations.size())/1000000000.0)+"s";
        return Double.toString(((double)total/(double)iterations.size())/1000000000.0);
    }

    private String computeAvgTimeQueries(final List<Iteration> iterations, final String key) {
        long total = 0;
        for (Iteration it : iterations) {
            final Timer timer = it.queryTimers.get(key);
            total += (timer!=null) ? timer.total() : 0;
        }

        //return Double.toString(((double)total/(double)iterations.size())/1000000000.0)+"s";
        return Double.toString(((double)total/(double)iterations.size())/1000000000.0);
    }

    private String computeAvgCountDiags(final List<Iteration> iterations, final String key) {
        long total = 0;
        for (Iteration it : iterations) {
            final Counter counter = it.diagnosesCounters.get(key);
            total += (counter!=null) ? counter.value() : 0;
        }

        return Double.toString((double)total/(double)iterations.size());
    }

    private String computeAvgCountQueries(final List<Iteration> iterations, final String key) {
        long total = 0;
        for (Iteration it : iterations) {
            final Counter counter = it.queryCounters.get(key);
            total += (counter!=null) ? counter.value() : 0;
        }

        return Double.toString((double)total/(double)iterations.size());
    }

}
