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

    protected ManchesterOWLSyntaxParser parser;

    OWLLogicalAxiom parse(String axiom) {
        this.parser.setStringToParse(axiom);
        return (OWLLogicalAxiom) this.parser.parseAxiom();
    }

    ExquisiteOWLReasoner createReasoner(File file) throws OWLOntologyCreationException, DiagnosisException {
        return createReasoner(file, false, false);
    }

    ExquisiteOWLReasoner createReasoner(File file, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        parser = new ManchesterOWLSyntaxParserImpl(new OWLAPIConfigProvider(), man.getOWLDataFactory());

        return createReasoner(ontology, extractModule, reduceIncoherencyToInconsistency);
    }

    ExquisiteOWLReasoner createReasoner(String... axioms) throws OWLOntologyCreationException, DiagnosisException {
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

        return new ExquisiteOWLReasoner(diagnosisModel, ontology.getOWLOntologyManager(), reasonerFactory);
    }


}
