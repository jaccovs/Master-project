package org.exquisite.evals.conferences.ecai2016;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.perfmeasures.*;
import org.exquisite.core.perfmeasures.Timer;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.core.solver.ISolver;
import org.exquisite.utils.OWLUtils;
import org.junit.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.Assert.*;

/**
 * @author wolfi
 */
public class Evaluation {

    private String TIMER_DIAGNOSES_CALCULATION = "Diag: time to calculate leading diagnoses";

    //@Test // TODO ACTIVATE TEST BY UNCOMMENTING THE ANNOTATION
    public void eval() throws OWLOntologyCreationException, DiagnosisException {
        CSVWriter w = new CSVWriter();
        Iteration i = new Iteration();
        try {
            w.open();
            w.writeConfiguration();
            w.writeHeader(i);
            for (String ontologyFile : Configuration.getOntologies()) {
                i.ontologyFile = ontologyFile;

                ExquisiteOWLReasoner solver = createSolver(ontologyFile, false, true);
                i.solver = solver;

                System.out.println("created solver " + solver + " with ontology " + ontologyFile);
                assertFalse(solver.isConsistent(solver.getDiagnosisModel().getPossiblyFaultyFormulas()));

                solver.setEntailmentTypes(InferenceType.DISJOINT_CLASSES, InferenceType.CLASS_HIERARCHY);

                final InverseDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = new InverseDiagnosisEngine<>(solver);
                i.engine = diagnosisEngine;
                Configuration.createQueryComputers(diagnosisEngine);

                System.out.println("created diagnosis engine: " + diagnosisEngine);

                for (int maxNumberOfDiagnoses : Configuration.getDiagnoseSizes()) {
                    diagnosisEngine.setMaxNumberOfDiagnoses(maxNumberOfDiagnoses);

                    System.out.println("set max numbers of diagnoses to: " + diagnosisEngine.getMaxNumberOfDiagnoses());

                    List<OWLLogicalAxiom> list = new ArrayList<>(diagnosisEngine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());

                    for (int iteration = 1; iteration <= Configuration.getIterations(); iteration++) {

                        System.out.println("Iteration: " + iteration);
                        i.iteration = iteration;

                        System.out.print("shuffling kb ... ");
                        Collections.shuffle(list, new Random(iteration)); // shuffle with seed
                        List<OWLLogicalAxiom> originalShuffleResult = new ArrayList<>(list);
                        assertFalse(list.equals(diagnosisEngine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas()));
                        diagnosisEngine.getSolver().getDiagnosisModel().setPossiblyFaultyFormulas(list);
                        System.out.println("done");

                        PerfMeasurementManager.reset();

                        PerfMeasurementManager.start(TIMER_DIAGNOSES_CALCULATION);
                        final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = diagnosisEngine.calculateDiagnoses();// berechnet Diagnosen
                        PerfMeasurementManager.stop(TIMER_DIAGNOSES_CALCULATION);

                        assertEquals(originalShuffleResult, diagnosisEngine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());

                        System.out.println("calculated " + diagnoses.size() + " diagnoses");
                        System.out.println("diagnoses: " + OWLUtils.getString(diagnoses));
                        System.out.println("counters: " + PerfMeasurementManager.getCounters());
                        System.out.println("timers: " + PerfMeasurementManager.getTimers());
                        System.out.println();

                        i.diagnosesSize = diagnoses.size();
                        i.diagnoses = diagnoses;
                        i.diagnosesCounters = Collections.unmodifiableMap(PerfMeasurementManager.getCounters());
                        i.diagnosesTimers = Collections.unmodifiableMap(PerfMeasurementManager.getTimers());

                        assertEquals(maxNumberOfDiagnoses, diagnoses.size());
                        setMeasures(diagnoses, iteration);

                        for (IQueryComputation queryComputation : Configuration.getQueryComputers()) {
                            System.out.println(queryComputation);
                            i.qc = queryComputation;

                            PerfMeasurementManager.reset();

                            PerfMeasurementManager.start("time query computation");
                            queryComputation.initialize(diagnoses);
                            assertTrue(queryComputation.hasNext());
                            Query<OWLLogicalAxiom> query = queryComputation.next();
                            queryComputation.reset();
                            PerfMeasurementManager.stop("time query computation");

                            System.out.println("query: " + OWLUtils.getString(query));
                            System.out.println("counters: " + PerfMeasurementManager.getCounters());
                            System.out.println("timers: " + PerfMeasurementManager.getTimers());
                            System.out.println();

                            i.query = query;
                            i.queryCounters = Collections.unmodifiableMap(PerfMeasurementManager.getCounters());
                            i.queryTimers = Collections.unmodifiableMap(PerfMeasurementManager.getTimers());

                            w.writeIteration(i);
                        }

                        diagnosisEngine.resetEngine();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            w.close();
        }
    }

    private void setMeasures(Set<Diagnosis<OWLLogicalAxiom>> diagnoses, int seed) {
        Random generator = new Random(seed);
        double sum = 0.0d;
        double[] doubles = new double[diagnoses.size()];
        int i = 0;
        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            doubles[i] = generator.nextDouble();
            sum += doubles[i];
            i++;
        }

        i = 0;
        BigDecimal add = BigDecimal.ZERO;
        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            if (i == diagnoses.size() - 1) {
                diagnosis.setMeasure(BigDecimal.ONE.subtract(add));
            } else {
                Double normalizedDouble = doubles[i] / sum;
                final BigDecimal measure = new BigDecimal(Double.toString(normalizedDouble));
                diagnosis.setMeasure(measure);
                add = add.add(measure);
            }
            i++;
        }

        BigDecimal result = BigDecimal.ZERO;
        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses)
            result = result.add(diagnosis.getMeasure());

        assertTrue(BigDecimal.ONE.compareTo(result) == 0);
    }


    private ExquisiteOWLReasoner createSolver(String ontologyName, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        File ontology = new File(ClassLoader.getSystemResource(ontologyName).getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology, extractModule, reduceIncoherencyToInconsistency);
        assertNotNull(reasoner);
        return reasoner;
    }

    private ExquisiteOWLReasoner createReasoner(File file, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);
        return createReasoner(ontology, extractModule, reduceIncoherencyToInconsistency);
    }

    private ExquisiteOWLReasoner createReasoner(OWLOntology ontology, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        ReasonerFactory reasonerFactory = new ReasonerFactory();
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology, reasonerFactory, extractModule, reduceIncoherencyToInconsistency);

        for (OWLIndividual ind : ontology.getIndividualsInSignature()) {
            diagnosisModel.getCorrectFormulas().addAll(ontology.getClassAssertionAxioms(ind));
            diagnosisModel.getCorrectFormulas().addAll(ontology.getObjectPropertyAssertionAxioms(ind));
        }
        diagnosisModel.getPossiblyFaultyFormulas().removeAll(diagnosisModel.getCorrectFormulas());

        return new ExquisiteOWLReasoner(diagnosisModel, ontology.getOWLOntologyManager(), reasonerFactory);
    }

    class Iteration {

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


        String getHeader() {
            StringBuilder sb = new StringBuilder();
            sb.append("Ontology").append(';');
            sb.append("Solver").append(';');
            sb.append("Engine").append(';');
            sb.append("Iteration").append(';');
            sb.append("DiagSize").append(';');
            sb.append("Diagnoses").append(';');

            // Diagnose Zeit- und Countangaben
            sb.append(TIMER_DIAGNOSES_CALCULATION).append(';');
            sb.append("Diag: # calls to solver.isConsistent()").append(';');
            sb.append("Diag: Time used for solver.isConsistent()").append(';');
            sb.append("Diag: # calls to solver.isConsistent(Formulas)").append(';');
            sb.append("Diag: Time used for solver.isConsistent(Formulas)").append(';');

            // Query Computation
            sb.append("QueryComputation").append(';');
            sb.append("Query").append(';');
            sb.append("QueryScore").append(';');
            sb.append("dx").append(';');
            sb.append("dnx").append(';');
            sb.append("dz").append(';');
            sb.append("probDx").append(';');
            sb.append("probDnx").append(';');

            // counters for query computation
            sb.append(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT_FORMULAS).append(';');
            sb.append(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT).append(';');
            sb.append(PerfMeasurementManager.COUNTER_SOLVER_ISENTAILED).append(';');
            sb.append(PerfMeasurementManager.COUNTER_SOLVER_CALCULATE_ENTAILMENTS).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_QPARTITIONS).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_QPARTITIONS).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_BACKTRACKINGS).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_PRUNINGS).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS_SIZE).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_HS_NODES).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_HS_NODES).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_BEFORE_MINIMIZE).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_MINIMIZE).append(';');
            sb.append(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_NAIVE_QUERYPOOL_SIZE).append(';');

            // timers
            sb.append(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS).append(';');
            sb.append(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT).append(';');
            sb.append(PerfMeasurementManager.TIMER_SOLVER_ISENTAILED).append(';');
            sb.append(PerfMeasurementManager.TIMER_SOLVER_CALCULATE_ENTAILMENTS).append(';');
            sb.append(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST).append(';');
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
            sb.append(diagnosesSize).append(';');
            sb.append(OWLUtils.getString(diagnoses)).append(';');

            // Diagnose Zeit- und Countangaben
            sb.append(diagnosesTimers.get(TIMER_DIAGNOSES_CALCULATION)).append(';');
            sb.append(diagnosesCounters.get(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT)).append(';');
            sb.append(diagnosesTimers.get(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT)).append(';');
            sb.append(diagnosesCounters.get(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
            sb.append(diagnosesTimers.get(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');

            // Query Computation
            sb.append(qc).append(';');
            sb.append(OWLUtils.getString(query)).append(';');
            sb.append(query.score).append(';');
            sb.append(OWLUtils.getString(query.qPartition.dx)).append(';');
            sb.append(OWLUtils.getString(query.qPartition.dnx)).append(';');
            sb.append(OWLUtils.getString(query.qPartition.dz)).append(';');
            sb.append(query.qPartition.probDx).append(';');
            sb.append(query.qPartition.probDnx).append(';');

            // counters for query computation
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_SOLVER_ISCONSISTENT)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_SOLVER_ISENTAILED)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_SOLVER_CALCULATE_ENTAILMENTS)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_QPARTITIONS)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_QPARTITIONS)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_BACKTRACKINGS)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_PRUNINGS)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_TRAITS_SIZE)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_HS_NODES)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_HS_NODES)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_BEFORE_MINIMIZE)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_HEURISTIC_QUERIES_SIZE_AFTER_MINIMIZE)).append(';');
            sb.append(queryCounters.get(PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_NAIVE_QUERYPOOL_SIZE)).append(';');

            // timers
            sb.append(queryTimers.get(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT_FORMULAS)).append(';');
            sb.append(queryTimers.get(PerfMeasurementManager.TIMER_SOLVER_ISCONSISTENT)).append(';');
            sb.append(queryTimers.get(PerfMeasurementManager.TIMER_SOLVER_ISENTAILED)).append(';');
            sb.append(queryTimers.get(PerfMeasurementManager.TIMER_SOLVER_CALCULATE_ENTAILMENTS)).append(';');
            sb.append(queryTimers.get(PerfMeasurementManager.TIMER_QUERYCOMPUTATION_HEURISTIC_ISQPARTCONST)).append(';');
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
}
