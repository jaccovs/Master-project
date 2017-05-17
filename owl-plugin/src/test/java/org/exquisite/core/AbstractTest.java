package org.exquisite.core;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.OWLAPIConfigProvider;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import java.io.File;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by kostya on 21-Mar-16.
 */
public class AbstractTest {

    protected ManchesterOWLSyntaxParser parser;

    protected final IExquisiteProgressMonitor monitor = null;

    protected OWLLogicalAxiom parse(String axiom) {
        this.parser.setStringToParse(axiom);
        return (OWLLogicalAxiom) this.parser.parseAxiom();
    }

    protected ExquisiteOWLReasoner createReasoner(File file) throws OWLOntologyCreationException, DiagnosisException {
        return createReasoner(file, false, false);
    }

    protected ExquisiteOWLReasoner createReasoner(File file, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        return createReasoner(file, null, extractModule, reduceIncoherencyToInconsistency);
    }

    protected ExquisiteOWLReasoner createReasoner(File file, OWLOntologyIRIMapper[] ontologyIRIMappers, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();

        // This is a fix for for locally imported ontologies (see imported families.owl in primer.owl ontology)
        // A test unit will not find the location of the imported ontology otherwise. The caller has to supply an IRIMapper
        // to give the ontology manager a hint where the imported ontology is physically.
        if (ontologyIRIMappers != null) {
            for (OWLOntologyIRIMapper ontologyIRIMapper : ontologyIRIMappers) {
                man.getIRIMappers().add(ontologyIRIMapper);
            }
        }

        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        parser = new ManchesterOWLSyntaxParserImpl(new OWLAPIConfigProvider(), man.getOWLDataFactory());

        return createReasoner(ontology, extractModule, reduceIncoherencyToInconsistency);
    }

    protected ExquisiteOWLReasoner createReasoner(String... axioms) throws OWLOntologyCreationException, DiagnosisException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.createOntology();

        parser = new ManchesterOWLSyntaxParserImpl(new OWLAPIConfigProvider(), man.getOWLDataFactory());
        for (String axiom : axioms) {
            man.addAxiom(ontology, parse(axiom));
        }

        return createReasoner(ontology, false, false);
    }


    protected ExquisiteOWLReasoner createReasoner(OWLOntology ontology, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        ReasonerFactory reasonerFactory = new ReasonerFactory();
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology/*, reasonerFactory, extractModule, reduceIncoherencyToInconsistency*/);
        diagnosisModel = ExquisiteOWLReasoner.consistencyCheck(diagnosisModel, ontology, reasonerFactory, extractModule, reduceIncoherencyToInconsistency);

        for (OWLIndividual ind : ontology.getIndividualsInSignature()) {
            diagnosisModel.getCorrectFormulas().addAll(ontology.getClassAssertionAxioms(ind));
            diagnosisModel.getCorrectFormulas().addAll(ontology.getObjectPropertyAssertionAxioms(ind));
        }
        diagnosisModel.getPossiblyFaultyFormulas().removeAll(diagnosisModel.getCorrectFormulas());

        return new ExquisiteOWLReasoner(diagnosisModel, reasonerFactory);
    }

    protected ExquisiteOWLReasoner loadOntology(String ontologyName) throws OWLOntologyCreationException, DiagnosisException {
        return loadOntology(ontologyName, false, false);
    }

    protected ExquisiteOWLReasoner loadOntology(String ontologyName, OWLOntologyIRIMapper... ontologyIRIMappers) throws OWLOntologyCreationException, DiagnosisException {
        return loadOntology(ontologyName, ontologyIRIMappers, false, false);
    }

    protected ExquisiteOWLReasoner loadOntology(String ontologyName, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        File ontology = new File(ClassLoader.getSystemResource(ontologyName).getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology, extractModule, reduceIncoherencyToInconsistency);
        assertNotNull(reasoner);
        return reasoner;
    }

    protected ExquisiteOWLReasoner loadOntology(String ontologyName, OWLOntologyIRIMapper[] ontologyIRIMappers, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        File ontology = new File(ClassLoader.getSystemResource(ontologyName).getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology, ontologyIRIMappers, extractModule, reduceIncoherencyToInconsistency);
        assertNotNull(reasoner);
        return reasoner;
    }

}
