package org.exquisite.core;

import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.core.solver.ExquisiteOWLReasoner2;
import org.junit.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.OWLAPIConfigProvider;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import java.io.File;
import java.util.Set;

/**
 * @author wolfi
 */
public class TestHeuristicQueryComputation  {

    ExquisiteOWLReasoner2 createReasoner(File file) throws OWLOntologyCreationException, DiagnosisException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        /// ManchesterOWLSyntaxParser parser = new ManchesterOWLSyntaxParserImpl(new OWLAPIConfigProvider(), man.getOWLDataFactory());

        return createReasoner(ontology);
    }
    protected ExquisiteOWLReasoner2 createReasoner(OWLOntology ontology) throws OWLOntologyCreationException, DiagnosisException {
        ReasonerFactory reasonerFactory = new ReasonerFactory();
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner2.generateDiagnosisModel(ontology, reasonerFactory);

        for (OWLIndividual ind : ontology.getIndividualsInSignature()) {
            diagnosisModel.getCorrectFormulas().addAll(ontology.getClassAssertionAxioms(ind));
            diagnosisModel.getCorrectFormulas().addAll(ontology.getObjectPropertyAssertionAxioms(ind));
        }
        diagnosisModel.getPossiblyFaultyFormulas().removeAll(diagnosisModel.getCorrectFormulas());

        return new ExquisiteOWLReasoner2(diagnosisModel, ontology.getOWLOntologyManager(), reasonerFactory);
    }
    // deactivated test case to simulate interactive diagnosisengine with heuristic query computation and answering, currently infinite loop
    @Test
    public void testHeuristicQueryComputation() throws OWLOntologyCreationException, DiagnosisException {
        File ontology = new File(ClassLoader.getSystemResource("ontologies/running_example_annotated.owl").getFile());
        ExquisiteOWLReasoner2 reasoner = createReasoner(ontology);
        reasoner.setEntailmentTypes(

        );

        IDiagnosisEngine<OWLLogicalAxiom> engine = new InverseDiagnosisEngine<>(reasoner); // new HSTreeEngine<>(reasoner);

        if (reasoner.isConsistent(engine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas())) {
            System.out.println("OK");
        } else {
            System.out.println("KO");
        }
        engine.setMaxNumberOfDiagnoses(9);

        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();
        System.out.println(diagnoses.size() + " diags found");


        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            System.out.println(" ----");


            for (OWLLogicalAxiom axiom : diagnosis.getFormulas()) {

                System.out.println(axiom.getAxiomWithoutAnnotations());
            }
        }


    }
}
