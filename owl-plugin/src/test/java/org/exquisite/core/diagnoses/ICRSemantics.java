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

    ICRSemantics(MyOntology ontology, MyOntology[] givenRepairs) throws Exception {
        original = ontology;
        repairs = givenRepairs;
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

    public MyOntology getIntersectionClosedRepair() {
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

            entailedRepairs[i].getManager().applyChanges(addAxioms);


            ontologies.remove(repairs[i].getOntology());
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

//        for (Map.Entry<OWLAxiom, Integer> entry : axiomOccurrences.entrySet()) {
//            OWLAxiom key = entry.getKey();
//            Integer value = entry.getValue();
//
//            System.out.println ("Key: " + key + " Value: " + value);
//        }

        Set<OWLAxiom> axiomsToRemove = findAxiomsToRemove(axiomOccurrences, repairs.length);

//        System.out.println(axiomsToRemove);

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
        Set<OWLClassAssertionAxiom> ICRClassAssertionAxioms = classAssertionAxiomGenerator.createAxioms(df, r);

        return ICRClassAssertionAxioms;
    }

    public Set<OWLPropertyAssertionAxiom<?,?>> getPropertyAssertionAxioms() throws Exception {
        OWLDataFactory df = intersectionClosedRepairs.getManager().getOWLDataFactory();
        OWLReasonerFactory rf = new ReasonerFactory();
        OWLReasoner r = rf.createReasoner(intersectionClosedRepairs.getOntology());

        InferredPropertyAssertionGenerator propertyAssertionAxiomGenerator = new InferredPropertyAssertionGenerator();
        Set<OWLPropertyAssertionAxiom<?,?>> ICRPropertyAssertionAxioms = propertyAssertionAxiomGenerator.createAxioms(df, r);

        return ICRPropertyAssertionAxioms;
    }
}
