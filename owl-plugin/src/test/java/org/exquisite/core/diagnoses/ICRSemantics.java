package org.exquisite.core.diagnoses;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;

import java.util.*;

public class ICRSemantics extends Semantics {

    MyOntology original;
    MyOntology[] repairs;
    MyOntology intersectionClosedRepairs;

    ICRSemantics(MyOntology ontology) throws Exception {
        original = ontology;
        repairs = super.createRepairs(original);
        intersectionClosedRepairs = getClosureIntersectionRepairs();
    }

    public MyOntology[] getRepairs() {
        return repairs;
    }

    public MyOntology getOriginal() {
        return original;
    }

    public void setOriginal(MyOntology newOnt) {
        original = newOnt;
    }

    public void setRepairs(MyOntology[] newRepairs) {
        repairs = newRepairs;
    }

    public MyOntology getClosedRepair() {
        return intersectionClosedRepairs;
    }

    private MyOntology getClosureIntersectionRepairs() {
        Set ontologies = new HashSet<OWLOntology>();
        MyOntology[] entailedRepairs = new MyOntology[repairs.length];

        for (int i = 0 ; i < repairs.length ; i++) {
            ontologies.add(repairs[i].getOntology());
            try {
                entailedRepairs[i] = new MyOntology("urn:absolute:ClosureRepair nr." + (i + 1) + ".owl", ontologies);
            }   catch (Exception e) {
                e.printStackTrace();
            }

            InferredClassAssertionAxiomGenerator classAssertionGen = new InferredClassAssertionAxiomGenerator();
            InferredPropertyAssertionGenerator propertyAssertionGen = new InferredPropertyAssertionGenerator();

            OWLDataFactory df = repairs[i].getManager().getOWLDataFactory();
            OWLReasonerFactory rf = new ReasonerFactory();
            OWLReasoner r = rf.createReasoner(repairs[i].getOntology());

            Set<OWLClassAssertionAxiom> classAssertionInference = classAssertionGen.createAxioms(df, r);
            Set<OWLPropertyAssertionAxiom<?, ?>> propertyAssertionInference = propertyAssertionGen.createAxioms(df, r);

            Set<OWLAxiom> inferedAxiomsToAdd = new HashSet<>();
            inferedAxiomsToAdd.addAll(classAssertionInference);
            inferedAxiomsToAdd.addAll(propertyAssertionInference);

            ArrayList<OWLOntologyChange> addAxioms = new ArrayList<>();

            for (OWLAxiom axiom : inferedAxiomsToAdd) {
                addAxioms.add(new AddAxiom(entailedRepairs[i].getOntology(), axiom));
            }
            repairs[i].getManager().applyChanges(addAxioms);

            System.out.println(entailedRepairs[i].getOntology());

            ontologies.remove(repairs[i]);
        }

        Hashtable<OWLAxiom, Integer> axiomOccurrences = new Hashtable<>();

        //Create new ontology object to reason with
        Set<OWLOntology> onts = new HashSet<OWLOntology>();
        onts.add(entailedRepairs[0].getOntology());
        MyOntology ont = null;
        try {
            ont = new MyOntology("urn:absolute: Intersection.owl", onts);
        } catch(Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i<repairs.length;i++){

            for (OWLAxiom axiom : entailedRepairs[i].getOntology().getAxioms()) {
                axiomOccurrences.put(axiom, axiomOccurrences.containsKey(axiom) ? axiomOccurrences.get(axiom) + 1 : 1);
            }
        }

        Set<OWLAxiom> axiomsToRemove = findAxiomsToRemove(axiomOccurrences, repairs.length);

        ArrayList<OWLOntologyChange> removal = new ArrayList<>();

        for(OWLAxiom axiom :axiomsToRemove){
            removal.add(new RemoveAxiom(ont.getOntology(), axiom));
        }
        ont.getManager().applyChanges(removal);
        return ont;
    }

    public Boolean isEntailed(OWLAxiom axiomToCheck){

        Boolean answer = super.repairEntailment(intersectionClosedRepairs, axiomToCheck);

        return answer;
    }

    private Set<OWLAxiom> findAxiomsToRemove(Hashtable<OWLAxiom, Integer> axiomOccurrences, int nrOfRepairs){
        Set<OWLAxiom> axiomsToRemove = axiomOccurrences.keySet();
        Iterator<OWLAxiom> iter = axiomsToRemove.iterator();

        while(iter.hasNext()) {
            OWLAxiom axiom = iter.next();

            if(axiomOccurrences.get(axiom).equals(nrOfRepairs)){
                iter.remove();
            }
        }
        return axiomsToRemove;
    }

    public Set<OWLClassAssertionAxiom> getClassAssertionAxioms() throws Exception{
        OWLDataFactory df = intersectionClosedRepairs.getManager().getOWLDataFactory();
        OWLReasonerFactory rf = new ReasonerFactory();
        OWLReasoner r = rf.createReasoner(intersectionClosedRepairs.getOntology());

        InferredClassAssertionAxiomGenerator classAssertionAxiomGenerator = new InferredClassAssertionAxiomGenerator();
        Set<OWLClassAssertionAxiom> IARClassAssertionAxioms = classAssertionAxiomGenerator.createAxioms(df, r);

        return IARClassAssertionAxioms;
    }
}
