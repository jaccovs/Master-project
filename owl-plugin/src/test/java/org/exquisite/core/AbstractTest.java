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

/**
 * Created by kostya on 21-Mar-16.
 */

class AbstractTest {

    private ManchesterOWLSyntaxParser parser;

    OWLLogicalAxiom parse(String axiom) {
        this.parser.setStringToParse(axiom);
        return (OWLLogicalAxiom) this.parser.parseAxiom();
    }

    ExquisiteOWLReasoner createReasoner(File file) throws OWLOntologyCreationException, DiagnosisException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        parser = new ManchesterOWLSyntaxParserImpl(new OWLAPIConfigProvider(), man.getOWLDataFactory());

        return createReasoner(ontology);
    }

    ExquisiteOWLReasoner createReasoner(String... axioms) throws OWLOntologyCreationException, DiagnosisException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.createOntology();

        parser = new ManchesterOWLSyntaxParserImpl(new OWLAPIConfigProvider(), man.getOWLDataFactory());
        for (String axiom : axioms) {
            man.addAxiom(ontology, parse(axiom));
        }

        return createReasoner(ontology);
    }

    private ExquisiteOWLReasoner createReasoner(OWLOntology ontology) throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = new ExquisiteOWLReasoner(ontology, new ReasonerFactory());
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = reasoner.getDiagnosisModel();

        for (OWLIndividual ind : ontology.getIndividualsInSignature()) {
            ontology.getClassAssertionAxioms(ind).stream().forEach(diagnosisModel::addCorrectStatement);
            ontology.getObjectPropertyAssertionAxioms(ind).stream().forEach(diagnosisModel::addCorrectStatement);
        }

        return reasoner;
    }
}