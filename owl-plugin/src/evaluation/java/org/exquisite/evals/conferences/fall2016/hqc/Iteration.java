package org.exquisite.evals.conferences.fall2016.hqc;

import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.perfmeasures.Counter;
import org.exquisite.core.perfmeasures.PerfMeasurementManager;
import org.exquisite.core.perfmeasures.Timer;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.solver.ISolver;
import org.exquisite.utils.OWLUtils;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * @author wolfi
 */
public class Iteration {

    String ontologyFile;
    public ISolver<OWLLogicalAxiom> solver;
    public IDiagnosisEngine<OWLLogicalAxiom> engine;
    public int iteration;
    public int diagnosesSize;
    public Map<String, Counter> diagnosesCounters;
    public Map<String, Timer> diagnosesTimers;
    public Set<Diagnosis<OWLLogicalAxiom>> diagnoses;
    public IQueryComputation qc;
    public Query<OWLLogicalAxiom> query;
    public Map<String, Counter> queryCounters;
    public Map<String, Timer> queryTimers;


    static String getHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("ontology").append(';');
        sb.append("solver").append(';');
        sb.append("engine").append(';');
        sb.append("iteration").append(';');

        // Diagnose Zeit- und Countangaben

        sb.append(Evaluation.TIMER_DIAGNOSES_CALCULATION).append(';');
        sb.append("diagnoses").append(';');
        sb.append("diagnosessize").append(';');
        sb.append("measures").append(';');
        sb.append("diag: # calls to solver.isConsistent()").append(';');
        sb.append("diag: time used for solver.isConsistent()").append(';');
        sb.append("diag: # calls to solver.isConsistent(Formulas)").append(';');
        sb.append("diag: time used for solver.isConsistent(Formulas)").append(';');

        // Query Computation
        sb.append("query computation").append(';');
        sb.append(Evaluation.TIMER_QUERY_CALCULATION).append(';');
        sb.append("query").append(';');
        sb.append("formulasize").append(';');
        sb.append("score").append(';');
        sb.append("dx").append(';');
        sb.append("dx size").append(';');
        sb.append("dnx").append(';');
        sb.append("dnx size").append(';');
        sb.append("dz").append(';');
        sb.append("dz size").append(';');
        sb.append("probDx").append(';');
        sb.append("probDnx").append(';');

        // counters and timers for query computation

        sb.append(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT).append(';');
        sb.append(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT).append(';');
        sb.append(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT_FORMULAS).append(';');
        sb.append(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS).append(';');
        sb.append(PerfMeasurementManager.COUNTER_SOLVER_ISENTAILED).append(';');
        sb.append(PerfMeasurementManager.TIMER_SOLVER_ISENTAILED).append(';');
        sb.append(PerfMeasurementManager.COUNTER_SOLVER_CALCULATE_ENTAILMENTS).append(';');
        sb.append(PerfMeasurementManager.TIMER_SOLVER_CALCULATE_ENTAILMENTS).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST).append(';');
        sb.append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_QPARTITIONS).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_QPARTITIONS).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_BACKTRACKINGS).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_PRUNINGS).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS_SIZE).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_HS_NODES).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_HS_NODES).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_LABELED_HS_NODES).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_CANONICAL_QUERIES_SIZE).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_BEFORE_ENRICHMENT).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_ENRICHTMENT).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_MINIMIZE).append(';');
        sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_NAIVE_QUERYPOOL_SIZE).append(';');
        sb.append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_FINDQPARTITION).append(';');
        sb.append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_SELECTQUERIES).append(';');
        sb.append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ENRICHQUERY).append(';');
        sb.append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_OPTIMIZEQUERY).append(';');

        sb.append("Timestamp").append(';');
        return sb.toString();
    }
    String getLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(ontologyFile).append(';');
        sb.append(solver).append(';');
        sb.append(engine).append(';');
        sb.append(iteration).append(';');

        // Diagnose Zeit- und Countangaben
        sb.append(diagnosesTimers.get(Evaluation.TIMER_DIAGNOSES_CALCULATION)).append(';');
        sb.append(OWLUtils.getString(diagnoses)).append(';');
        sb.append(diagnosesSize).append(';');
        for (Diagnosis<OWLLogicalAxiom> d : diagnoses) {
            sb.append(d.getMeasure()).append(", ");
        }
        sb.append(';');
        sb.append(diagnosesCounters.get(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT)).append(';');
        sb.append(diagnosesTimers.get(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT)).append(';');
        sb.append(diagnosesCounters.get(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
        sb.append(diagnosesTimers.get(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');

        // Query Computation
        sb.append(qc).append(';');
        sb.append(queryTimers.get(Evaluation.TIMER_QUERY_CALCULATION)).append(';');
        sb.append(OWLUtils.getString(query)).append(';');
        sb.append(query.formulas.size()).append(';');
        sb.append(query.score).append(';');
        sb.append(OWLUtils.getString(query.qPartition.dx)).append(';');
        sb.append(query.qPartition.dx.size()).append(';');
        sb.append(OWLUtils.getString(query.qPartition.dnx)).append(';');
        sb.append(query.qPartition.dnx.size()).append(';');
        sb.append(OWLUtils.getString(query.qPartition.dz)).append(';');
        sb.append(query.qPartition.dz.size()).append(';');
        sb.append(query.qPartition.probDx).append(';');
        sb.append(query.qPartition.probDnx).append(';');

        // counters and timers for query computation

        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT)).append(';');
        sb.append(queryTimers.get(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
        sb.append(queryTimers.get(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_SOLVER_ISENTAILED)).append(';');
        sb.append(queryTimers.get(PerfMeasurementManager.TIMER_SOLVER_ISENTAILED)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_SOLVER_CALCULATE_ENTAILMENTS)).append(';');
        sb.append(queryTimers.get(PerfMeasurementManager.TIMER_SOLVER_CALCULATE_ENTAILMENTS)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST)).append(';');
        sb.append(queryTimers.get(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_QPARTITIONS)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_QPARTITIONS)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_BACKTRACKINGS)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_PRUNINGS)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS_SIZE)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_HS_NODES)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_HS_NODES)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_LABELED_HS_NODES)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_CANONICAL_QUERIES_SIZE)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_BEFORE_ENRICHMENT)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_ENRICHTMENT)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_MINIMIZE)).append(';');
        sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_NAIVE_QUERYPOOL_SIZE)).append(';');
        sb.append(queryTimers.get(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_FINDQPARTITION)).append(';');
        sb.append(queryTimers.get(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_SELECTQUERIES)).append(';');
        sb.append(queryTimers.get(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ENRICHQUERY)).append(';');
        sb.append(queryTimers.get(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_OPTIMIZEQUERY)).append(';');

        sb.append(getCurrentTime()).append(';');
        return sb.toString();
    }

    String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(cal.getTime());
    }

}
