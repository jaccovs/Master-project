package org.exquisite.core.diagnosis;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Semantics {

    MyOntology original;
    MyOntology[] repairs;

    public MyOntology[] createRepairs(MyOntology ont) throws Exception {
        original = ont;
        Set<Diagnosis<OWLLogicalAxiom>> result = calculateDiagnoses(original);
        Iterator<Diagnosis<OWLLogicalAxiom>> it = result.iterator();

        int numberOfRepairs = result.size();

        MyOntology[] repairList = new MyOntology[numberOfRepairs];
        Set ontologies = new HashSet<OWLOntology>();
        ontologies.add(original.getOntology());

        for (int i = 0; i < numberOfRepairs; i++) {
            repairList[i] = new MyOntology("urn:absolute:repair nr." + (i + 1) + ".owl", ontologies);
            Diagnosis<OWLLogicalAxiom> diagnosis = it.next();
            ArrayList<OWLOntologyChange> changes = determineRepair(repairList[i].getOntology(), diagnosis.getFormulas());
            repairList[i].getManager().applyChanges(changes);
        }

        return repairList;
    }

    private ArrayList<OWLOntologyChange> determineRepair(OWLOntology ont, Set<OWLLogicalAxiom> diagnosis) {
        ArrayList<OWLOntologyChange> removal = new ArrayList<>();
        for (OWLAxiom axiom : diagnosis) {
            removal.add(new RemoveAxiom(ont, axiom));
        }
        return removal;
    }

    public Boolean[] repairEntailments(MyOntology[] repairs, OWLAxiom axiomToCheck) {
        Boolean[] answers = new Boolean[repairs.length];
        for (int i = 0; i < repairs.length; i++) {
            answers[i] = repairEntailment(repairs[i], axiomToCheck);
        }
        return answers;
    }

    public  Set<Diagnosis<OWLLogicalAxiom>> calculateDiagnoses(MyOntology ont) throws DiagnosisException, OWLOntologyCreationException {
        ExquisiteOWLReasoner reasoner = createReasoner(ont.getOntology(), false, false);
        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = new InverseDiagnosisEngine<>(reasoner);
        diagnosisEngine.resetEngine();
        diagnosisEngine.setMaxNumberOfDiagnoses(40);
        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = diagnosisEngine.calculateDiagnoses();

        return diagnoses;

    }

    protected ExquisiteOWLReasoner createReasoner(OWLOntology ontology, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        OWLReasonerFactory reasonerFactory = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory();
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner.generateDiagnosisModel(ontology/*, reasonerFactory, extractModule, reduceIncoherencyToInconsistency*/);
        diagnosisModel = ExquisiteOWLReasoner.consistencyCheck(diagnosisModel, ontology, reasonerFactory, extractModule, reduceIncoherencyToInconsistency);

        for (OWLClass cls : ontology.getClassesInSignature()) {
            diagnosisModel.getCorrectFormulas().addAll(ontology.getSubClassAxiomsForSubClass(cls));
            diagnosisModel.getCorrectFormulas().addAll(ontology.getSubClassAxiomsForSuperClass(cls));
            diagnosisModel.getCorrectFormulas().addAll(ontology.getDisjointClassesAxioms(cls));
            diagnosisModel.getCorrectFormulas().addAll(ontology.getEquivalentClassesAxioms(cls));
        }

        for (OWLObjectProperty prop : ontology.getObjectPropertiesInSignature()) {
            diagnosisModel.getCorrectFormulas().addAll(ontology.getObjectPropertyDomainAxioms(prop));
            diagnosisModel.getCorrectFormulas().addAll(ontology.getObjectPropertyRangeAxioms(prop));
        }

        diagnosisModel.getPossiblyFaultyFormulas().removeAll(diagnosisModel.getCorrectFormulas());

        return new ExquisiteOWLReasoner(diagnosisModel, reasonerFactory);
    }

    public Boolean repairEntailment(MyOntology repair, OWLAxiom axiomToCheck) {
        OWLOntology o = repair.getOntology();
        OWLReasonerFactory rf = new ReasonerFactory();
        OWLReasoner r = rf.createReasoner(o);
        Boolean answer = r.isEntailed(axiomToCheck);

        return answer;
    }
}