package org.exquisite.core.diagnoses;

import org.exquisite.core.model.Diagnosis;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;

import java.util.*;

public class ARSemantics extends Semantics {

    MyOntology original;
    MyOntology[] repairs;

    ARSemantics (MyOntology ontology) throws Exception{
        original = ontology;
        repairs = super.createRepairs(original);
    }

    public MyOntology[] getRepairs() {
        return repairs;
    }

    public MyOntology getOriginal() {
        return original;
    }

    public void setOriginal(MyOntology newOnt){
        original = newOnt;
    }

    public void setRepairs(MyOntology[] newRepairs){
        repairs = newRepairs;
    }

    public Boolean isEntailed (OWLAxiom query){
        Boolean[] answers = super.repairEntailments(repairs, query);

        for(boolean b : answers) {

            if(!b){
                return false;
            }
        }

        return true;
    }

    public Set<OWLClassAssertionAxiom> getClassAssertionAxioms() throws Exception{
        Hashtable<OWLClassAssertionAxiom, Integer> instancesInRepairs = new Hashtable<OWLClassAssertionAxiom, Integer>();

        for (int i = 0 ; i < repairs.length ; i++) {
            OWLDataFactory df = repairs[i].getManager().getOWLDataFactory();
            OWLReasonerFactory rf = new ReasonerFactory();
            OWLReasoner r = rf.createReasoner(repairs[i].getOntology());

            InferredClassAssertionAxiomGenerator classAssertionAxiomGenerator = new InferredClassAssertionAxiomGenerator();
            Set<OWLClassAssertionAxiom> classAssertionAxioms = classAssertionAxiomGenerator.createAxioms(df, r);

            for (OWLClassAssertionAxiom classAssertionAxiom: classAssertionAxioms){
                instancesInRepairs.put(classAssertionAxiom, instancesInRepairs.containsKey(classAssertionAxiom) ? instancesInRepairs.get(classAssertionAxiom) + 1 : 1);
            }
        }

//        for (Map.Entry<OWLClassAssertionAxiom, Integer> entry : instancesInRepairs.entrySet()) {
//            OWLClassAssertionAxiom key = entry.getKey();
//            Integer value = entry.getValue();
//
//            System.out.println ("Key: " + key + " Value: " + value);
//        }

        Integer value = repairs.length;
        Set<OWLClassAssertionAxiom> ARClassAssertionAxioms = new HashSet();

        for(Map.Entry entry: instancesInRepairs.entrySet()){
            if(value.equals(entry.getValue())){
                ARClassAssertionAxioms.add((OWLClassAssertionAxiom) entry.getKey());
            }
        }

        return ARClassAssertionAxioms;
    }

}
