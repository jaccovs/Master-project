package org.exquisite.evals.conferences.ecai2016;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.perfmeasures.PerfMeasurement;
import org.exquisite.core.perfmeasures.PerfMeasurementManager;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.junit.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;

import java.io.File;
import java.util.*;

import static junit.framework.Assert.*;

/**
 * @author wolfi
 */
public class Evaluation {

    @Test
    public void eval() throws OWLOntologyCreationException, DiagnosisException {
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

                for (int iteration = 1; iteration <= Configuration.getIterations(); iteration++) {

                    System.out.println("Iteration: " + iteration);

                    System.out.print("shuffling kb ... ");
                    Collections.shuffle(list, new Random(iteration)); // shuffle with seed
                    List<OWLLogicalAxiom> copyShuffleResult = new ArrayList<>(list);
                    diagnosisEngine.getSolver().getDiagnosisModel().setPossiblyFaultyFormulas(list);
                    System.out.println("done");

                    PerfMeasurementManager.reset();

                    final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = diagnosisEngine.calculateDiagnoses();// berechnet Diagnosen

                    System.out.println("calculated " + diagnoses.size() + " diagnoses");
                    System.out.println("counters: " + PerfMeasurementManager.getCounters());
                    System.out.println("timers: " + PerfMeasurementManager.getTimers());

                    assertEquals(maxNumberOfDiagnoses, diagnoses.size());
                    // TODO setze measures

                    for (IQueryComputation queryComputation : Configuration.getQueryComputers()) {
                        System.out.println("QueryComputer: " + queryComputation);

                        /*
                        queryComputation.initialize(diagnoses);
                        assertTrue(queryComputation.hasNext());
                        Query<OWLLogicalAxiom> query = queryComputation.next();
                        queryComputation.reset();
                        */

                        //final List<OWLLogicalAxiom> kb = diagnosisEngine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas();
                        //assertEquals(copyShuffleResult, kb);
                    }

                    diagnosisEngine.resetEngine();
                }
            }
        }
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
