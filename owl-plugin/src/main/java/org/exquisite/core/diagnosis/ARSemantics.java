package org.exquisite.core.diagnosis;

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

    public Set<OWLAxiom> getClassAssertionAxioms() throws Exception{
        Hashtable<OWLAxiom, Integer> instancesInRepairs = new Hashtable<OWLAxiom, Integer>();

        for (int i = 0 ; i < repairs.length ; i++) {
            OWLDataFactory df = repairs[i].getManager().getOWLDataFactory();
            OWLReasonerFactory rf = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory();
            OWLReasoner r = rf.createReasoner(repairs[i].getOntology());

            InferredClassAssertionAxiomGenerator classAssertionAxiomGenerator = new InferredClassAssertionAxiomGenerator();
            Set<OWLClassAssertionAxiom> classAssertionAxioms = classAssertionAxiomGenerator.createAxioms(df, r);

            for (OWLClassAssertionAxiom classAssertionAxiom: classAssertionAxioms){
                instancesInRepairs.put(classAssertionAxiom, instancesInRepairs.containsKey(classAssertionAxiom) ? instancesInRepairs.get(classAssertionAxiom) + 1 : 1);
            }

            for (OWLClass cls : original.getOntology().getClassesInSignature()) {
                for (OWLSubClassOfAxiom classAssertionAxiom : repairs[i].getOntology().getSubClassAxiomsForSubClass(cls)) {
                    instancesInRepairs.put(classAssertionAxiom, instancesInRepairs.containsKey(classAssertionAxiom) ? instancesInRepairs.get(classAssertionAxiom) + 1 : 1);
                }
            }
        }

//        for (Map.Entry<OWLAxiom, Integer> entry : instancesInRepairs.entrySet()) {
////            OWLAxiom key = entry.getKey();
////            Integer value = entry.getValue();
////
////            System.out.println ("Key: " + key + " Value: " + value);
////        }

        Integer value = repairs.length;
        Set<OWLAxiom> ARClassAssertionAxioms = new HashSet();

        for(Map.Entry entry: instancesInRepairs.entrySet()){
            if(value.equals(entry.getValue())){
                ARClassAssertionAxioms.add((OWLAxiom) entry.getKey());
            }
        }

        return ARClassAssertionAxioms;
    }

    public Set<OWLAxiom> getPropertyAssertionAxioms() throws Exception{
        Hashtable<OWLAxiom, Integer> instancesInRepairs = new Hashtable<OWLAxiom, Integer>();

        for (int i = 0 ; i < repairs.length ; i++) {
            OWLDataFactory df = repairs[i].getManager().getOWLDataFactory();
            OWLReasonerFactory rf = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory();
            OWLReasoner r = rf.createReasoner(repairs[i].getOntology());

            InferredPropertyAssertionGenerator propertyAssertionGenerator = new InferredPropertyAssertionGenerator();
            Set<OWLPropertyAssertionAxiom<?,?>> propertyAssertionAxioms = propertyAssertionGenerator.createAxioms(df, r);

            for (OWLPropertyAssertionAxiom propertyAssertionAxiom: propertyAssertionAxioms){
                instancesInRepairs.put(propertyAssertionAxiom, instancesInRepairs.containsKey(propertyAssertionAxiom) ? instancesInRepairs.get(propertyAssertionAxiom) + 1 : 1);
            }
        }

//        for (Map.Entry<OWLPropertyAssertionAxiom, Integer> entry : instancesInRepairs.entrySet()) {
//            OWLPropertyAssertionAxiom key = entry.getKey();
//            Integer value = entry.getValue();
//
//            System.out.println ("Key: " + key + " Value: " + value);
//        }

        Integer value = repairs.length;
        Set<OWLAxiom> ARPropertyAssertionAxioms = new HashSet();

        for(Map.Entry entry: instancesInRepairs.entrySet()){
            if(value.equals(entry.getValue())){
                ARPropertyAssertionAxioms.add((OWLAxiom) entry.getKey());
            }
        }

        return ARPropertyAssertionAxioms;
    }

}
