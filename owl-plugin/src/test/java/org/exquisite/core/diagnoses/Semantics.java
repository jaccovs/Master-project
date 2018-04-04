package org.exquisite.core.diagnoses;

import org.exquisite.core.model.Diagnosis;
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
        TestClass test = new TestClass();
        Set<Diagnosis<OWLLogicalAxiom>> result = test.calculateDiagnoses(original);
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

    public Boolean repairEntailment(MyOntology repair, OWLAxiom axiomToCheck) {
        OWLOntology o = repair.getOntology();
        OWLReasonerFactory rf = new ReasonerFactory();
        OWLReasoner r = rf.createReasoner(o);
        Boolean answer = r.isEntailed(axiomToCheck);

        return answer;
    }
}
