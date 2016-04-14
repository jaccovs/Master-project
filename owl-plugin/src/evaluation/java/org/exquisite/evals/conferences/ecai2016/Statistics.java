package org.exquisite.evals.conferences.ecai2016;

import org.exquisite.core.perfmeasures.*;
import org.exquisite.core.query.querycomputation.IQueryComputation;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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

    private Map<IQueryComputation, List<Iteration>> iterations = new HashMap<>();

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

        StringBuilder sb = new StringBuilder("\nStatistics for ").append(ontology).append(" and ").append(diagnosesSize).append(" diagnoses:").append('\n');
        sb.append("Query Computation;");
        sb.append("AVG ").append(TIMER_QUERY_CALCULATION).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_SOLVER_ISENTAILED).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_SOLVER_CALCULATE_ENTAILMENTS).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_FINDQPARTITION).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_SELECTQUERIES).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ENRICHQUERY).append(';');
        sb.append("AVG ").append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_OPTIMIZEQUERY).append(';');
        return sb.toString();
    }

    private String getLineForQueryComputation(IQueryComputation qc) {
        final List<Iteration> iterations = this.iterations.get(qc);
        StringBuilder sb = new StringBuilder(qc.toString()).append(';');
        sb.append(computeAvgTime(iterations,TIMER_QUERY_CALCULATION)).append(';');
        sb.append(computeAvgTime(iterations,PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT)).append(';');
        sb.append(computeAvgTime(iterations,PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
        sb.append(computeAvgTime(iterations,PerfMeasurementManager.TIMER_SOLVER_ISENTAILED)).append(';');
        sb.append(computeAvgTime(iterations,PerfMeasurementManager.TIMER_SOLVER_CALCULATE_ENTAILMENTS)).append(';');
        sb.append(computeAvgTime(iterations,PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST)).append(';');
        sb.append(computeAvgTime(iterations,PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_FINDQPARTITION)).append(';');
        sb.append(computeAvgTime(iterations,PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_SELECTQUERIES)).append(';');
        sb.append(computeAvgTime(iterations,PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ENRICHQUERY)).append(';');
        sb.append(computeAvgTime(iterations,PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_OPTIMIZEQUERY)).append(';');
        return sb.toString();
    }

    private String computeAvgTime(List<Iteration> iterations, String key) {
        long total = 0;
        for (Iteration it : iterations) {
            final org.exquisite.core.perfmeasures.Timer timer = it.queryTimers.get(key);
            total += (timer!=null)?timer.total():0;
        }

        //double result = ((double)total/(double)iterations.size())/1000000000.0;
        //return NumberFormat.getNumberInstance(Locale.GERMAN).format(result);
        //return result;
        //DecimalFormat formatter = new DecimalFormat("#.###,00");
        //return formatter.format(result);


        return Double.toString(((double)total/(double)iterations.size())/1000000000.0)+"s";
    };
/*
    private String computeAvgCount(List<Iteration> iterations, String key) {
        long total = 0;
        for (Iteration it : iterations)
            total += it.queryCounters.get(key).value();
        return Double.toString((double)total/(double)iterations.size());
    };
*/
    private List<Iteration> getIterations(IQueryComputation qc) {
        return iterations.computeIfAbsent(qc, (key) -> new ArrayList<>());
    }
}
