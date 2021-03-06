package org.exquisite.core.diagnoses;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.exquisite.core.model.Diagnosis;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CARSemantics extends Semantics{

        MyOntology original;

        MyOntology test;

        MyOntology[] repairs;

        MyOntology[] CARrepairs;

        CARSemantics (MyOntology ontology) throws Exception{
            test = ontology;
            original = ontology;
            repairs = super.createRepairs(original);
            CARrepairs = getCARRepairs();
        }

        CARSemantics(MyOntology ontology, MyOntology[] givenRepairs) throws Exception {
            test = ontology;
            original = ontology;
            repairs = givenRepairs;
            CARrepairs = getCARRepairs();
        }

        private MyOntology[] getCARRepairs() throws Exception{
            Set<OWLClassAssertionAxiom> braveInferences = new HashSet<>();

            for (int i = 0 ; i < repairs.length ; i++) {
                OWLDataFactory dfRepair = repairs[i].getManager().getOWLDataFactory();
                OWLReasonerFactory rf = new ReasonerFactory();
                OWLReasoner r = rf.createReasoner(repairs[i].getOntology());

                InferredClassAssertionAxiomGenerator classAssertionAxiomGenerator = new InferredClassAssertionAxiomGenerator();
                Set<OWLClassAssertionAxiom> classAssertionAxioms = classAssertionAxiomGenerator.createAxioms(dfRepair, r);

                Set<OWLClassAssertionAxiom> removeThings = new HashSet<>();
                for (OWLClassAssertionAxiom axiom : classAssertionAxioms){
                    if (axiom.getClassExpression().isOWLThing()){
                        removeThings.add(axiom);
                    }
                }
                classAssertionAxioms.removeAll(removeThings);
                braveInferences.addAll(classAssertionAxioms);
            }

            ArrayList<OWLOntologyChange> addition = new ArrayList<>();

            for(OWLAxiom axiom : braveInferences){
                addition.add(new AddAxiom(test.getOntology(), axiom));
            }
            test.getManager().applyChanges(addition);

            MyOntology[] ARRepairsAfterClosure = super.createRepairs(test);

            return ARRepairsAfterClosure;
        }

        public MyOntology[] getRepairs() {
            return CARrepairs;
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

        public Boolean isEntailed (OWLAxiom query) throws Exception{
            Boolean[] answers = super.repairEntailments(CARrepairs, query);

            for(boolean b : answers) {

                if(b){
                    return true;
                }
            }

            return false;
        }

        public Set<OWLClassAssertionAxiom> getClassAssertionAxioms() throws Exception{
            Hashtable<OWLClassAssertionAxiom, Integer> instancesInRepairs = new Hashtable<OWLClassAssertionAxiom, Integer>();

            for (int i = 0 ; i < CARrepairs.length ; i++) {
                OWLDataFactory df = CARrepairs[i].getManager().getOWLDataFactory();
                OWLReasonerFactory rf = new ReasonerFactory();
                OWLReasoner r = rf.createReasoner(CARrepairs[i].getOntology());

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

            Integer value = CARrepairs.length;
            Set<OWLClassAssertionAxiom> ARClassAssertionAxioms = new HashSet();

            for(Map.Entry entry: instancesInRepairs.entrySet()){
                if(value.equals(entry.getValue())){
                    ARClassAssertionAxioms.add((OWLClassAssertionAxiom) entry.getKey());
                }
            }

            return ARClassAssertionAxioms;
        }

        public Set<OWLPropertyAssertionAxiom> getPropertyAssertionAxioms() throws Exception{
            Hashtable<OWLPropertyAssertionAxiom, Integer> instancesInRepairs = new Hashtable<OWLPropertyAssertionAxiom, Integer>();

            for (int i = 0 ; i < CARrepairs.length ; i++) {
                OWLDataFactory df = CARrepairs[i].getManager().getOWLDataFactory();
                OWLReasonerFactory rf = new ReasonerFactory();
                OWLReasoner r = rf.createReasoner(CARrepairs[i].getOntology());

                InferredPropertyAssertionGenerator propertyAssertionGenerator = new InferredPropertyAssertionGenerator();
                Set<OWLPropertyAssertionAxiom<?,?>> propertyAssertionAxioms = propertyAssertionGenerator.createAxioms(df, r);

                for (OWLPropertyAssertionAxiom propertyAssertionAxiom: propertyAssertionAxioms){
                    instancesInRepairs.put(propertyAssertionAxiom, instancesInRepairs.containsKey(propertyAssertionAxiom) ? instancesInRepairs.get(propertyAssertionAxiom) + 1 : 1);
                }
            }

//        for (Map.Entry<OWLClassAssertionAxiom, Integer> entry : instancesInRepairs.entrySet()) {
//            OWLClassAssertionAxiom key = entry.getKey();
//            Integer value = entry.getValue();
//
//            System.out.println ("Key: " + key + " Value: " + value);
//        }

            Integer value = CARrepairs.length;
            Set<OWLPropertyAssertionAxiom> ARPropertyAssertionAxioms = new HashSet();

            for(Map.Entry entry: instancesInRepairs.entrySet()){
                if(value.equals(entry.getValue())){
                        ARPropertyAssertionAxioms.add((OWLPropertyAssertionAxiom) entry.getKey());
                }
            }

            return ARPropertyAssertionAxioms;
        }

    }
