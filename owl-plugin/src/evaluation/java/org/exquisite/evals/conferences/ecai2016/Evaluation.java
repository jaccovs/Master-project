package org.exquisite.evals.conferences.ecai2016;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.perfmeasures.*;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.utils.OWLUtils;
import org.junit.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.*;

/**
 * @author wolfi
 */
public class Evaluation {

    protected static String TIMER_DIAGNOSES_CALCULATION = "time.calculation.diagnoses";
    protected static String TIMER_QUERY_CALCULATION = "time.query.computation";

    @Test // TODO ACTIVATE TEST BY UNCOMMENTING THE ANNOTATION
    public void eval() throws OWLOntologyCreationException, DiagnosisException {
        CSVWriter w = new CSVWriter();

        final int TOTALSTEPS = Configuration.getOntologies().size() * Configuration.getDiagnoseSizes().size() * Configuration.getIterations() * Configuration.getNrOfQueryComputers();
        int step = 0;
        long start = System.currentTimeMillis();

        try {
            w.open();
            w.writeConfiguration();

            for (String ontologyFile : Configuration.getOntologies()) {
                ExquisiteOWLReasoner solver = createSolver(ontologyFile, false, true);

                System.out.println("created solver " + solver + " with ontology " + ontologyFile);
                assertFalse(solver.isConsistent(solver.getDiagnosisModel().getPossiblyFaultyFormulas()));

                solver.setEntailmentTypes(InferenceType.DISJOINT_CLASSES, InferenceType.CLASS_HIERARCHY);

                final InverseDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = new InverseDiagnosisEngine<>(solver);

                Configuration.createQueryComputers(diagnosisEngine);

                System.out.println("created diagnosis engine: " + diagnosisEngine);

                for (int maxNumberOfDiagnoses : Configuration.getDiagnoseSizes()) {
                    diagnosisEngine.setMaxNumberOfDiagnoses(maxNumberOfDiagnoses);

                    System.out.println("set max numbers of diagnoses to: " + diagnosisEngine.getMaxNumberOfDiagnoses());

                    List<OWLLogicalAxiom> list = new ArrayList<>(diagnosisEngine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());

                    Statistics statistics = new Statistics(maxNumberOfDiagnoses, ontologyFile);

                    w.writeHeader(Iteration.getHeader());

                    for (int iteration = 1; iteration <= Configuration.getIterations(); iteration++) {



                        System.out.println("Iteration: " + iteration );


                        Collections.shuffle(list, new Random(iteration)); // shuffle with seed
                        List<OWLLogicalAxiom> originalShuffleResult = new ArrayList<>(list);
                        assertFalse(list.equals(diagnosisEngine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas()));
                        diagnosisEngine.getSolver().getDiagnosisModel().setPossiblyFaultyFormulas(list);

                        PerfMeasurementManager.reset();

                        PerfMeasurementManager.start(TIMER_DIAGNOSES_CALCULATION);
                        final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = diagnosisEngine.calculateDiagnoses();// berechnet Diagnosen
                        PerfMeasurementManager.stop(TIMER_DIAGNOSES_CALCULATION);

                        assertEquals(originalShuffleResult, diagnosisEngine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());

                        System.out.println("calculated " + diagnoses.size() + " diagnoses");
                        System.out.println("diagnoses: " + OWLUtils.getString(diagnoses));
                        final Map<String, Counter> diagcounters = Collections.unmodifiableMap(PerfMeasurementManager.getCounters());
                        System.out.println("counters: " + diagcounters);
                        final Map<String, org.exquisite.core.perfmeasures.Timer> diagtimers = Collections.unmodifiableMap(PerfMeasurementManager.getTimers());
                        System.out.println("timers: " + diagtimers);
                        System.out.println();



                        assertEquals(maxNumberOfDiagnoses, diagnoses.size());
                        setMeasures(diagnoses, iteration);

                        for (IQueryComputation queryComputation : Configuration.getQueryComputers()) {
                            System.out.println(queryComputation);

                            PerfMeasurementManager.reset();

                            PerfMeasurementManager.start(TIMER_QUERY_CALCULATION);
                            queryComputation.initialize(diagnoses);
                            assertTrue(queryComputation.hasNext());
                            Query<OWLLogicalAxiom> query = queryComputation.next();
                            queryComputation.reset();
                            PerfMeasurementManager.stop(TIMER_QUERY_CALCULATION);

                            System.out.println("query: " + OWLUtils.getString(query));
                            System.out.println("counters: " + PerfMeasurementManager.getCounters());
                            System.out.println("timers: " + PerfMeasurementManager.getTimers());


                            Iteration i = new Iteration();
                            i.iteration = iteration;
                            i.ontologyFile = ontologyFile;
                            i.solver = solver;
                            i.engine = diagnosisEngine;
                            i.diagnosesSize = diagnoses.size();
                            i.diagnoses = diagnoses;
                            i.diagnosesCounters = diagcounters;
                            i.diagnosesTimers = diagtimers;
                            i.qc = queryComputation;
                            i.query = query;
                            i.queryCounters = Collections.unmodifiableMap(PerfMeasurementManager.getCounters());
                            i.queryTimers = Collections.unmodifiableMap(PerfMeasurementManager.getTimers());

                            w.writeIteration(i);


                            final long now = System.currentTimeMillis();
                            long predictedEnd = (now - start) * TOTALSTEPS / ++step;
                            System.out.println("Progress: (" + (step) + "/" + TOTALSTEPS + "), predicted end: " + new Date(now + predictedEnd));
                            System.out.println();

                            statistics.addIteration(queryComputation, i);
                        }

                        diagnosisEngine.resetEngine();
                    }

                    w.writeStatistics(statistics);
                }
            }
            w.close(null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail(e.getMessage());
            w.close(e);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
            w.close(e);
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


}
