package org.exquisite.core.diagnoses;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;

import java.util.*;

public class IARSemantics extends Semantics {

    MyOntology original;
    MyOntology[] repairs;
    MyOntology intersectionRepairs;

    IARSemantics(MyOntology ontology) throws Exception {
        original = ontology;
        repairs = super.createRepairs(original);
        intersectionRepairs = getIntersectionRepairs();
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
        return intersectionRepairs;
    }

    private MyOntology getIntersectionRepairs() {
        Hashtable<OWLAxiom, Integer> axiomOccurrences = new Hashtable<>();

        //Create new ontology object to reason with
        Set<OWLOntology> onts = new HashSet<OWLOntology>();
        onts.add(repairs[0].getOntology());
        MyOntology ont = null;
        try {
            ont = new MyOntology("urn:absolute: Intersection.owl", onts);
        } catch(Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i<repairs.length;i++){

            for (OWLAxiom axiom : repairs[i].getOntology().getAxioms()) {
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

        Boolean answer = super.repairEntailment(intersectionRepairs, axiomToCheck);

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
        OWLDataFactory df = intersectionRepairs.getManager().getOWLDataFactory();
        OWLReasonerFactory rf = new ReasonerFactory();
        OWLReasoner r = rf.createReasoner(intersectionRepairs.getOntology());

        InferredClassAssertionAxiomGenerator classAssertionAxiomGenerator = new InferredClassAssertionAxiomGenerator();
        Set<OWLClassAssertionAxiom> IARClassAssertionAxioms = classAssertionAxiomGenerator.createAxioms(df, r);

        return IARClassAssertionAxioms;
    }
}
