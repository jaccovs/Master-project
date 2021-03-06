package org.exquisite.core.diagnosis;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;

import java.util.*;

public class BraveSemantics extends Semantics{

    MyOntology original;

    MyOntology[] repairs;

    BraveSemantics (MyOntology ontology) throws Exception{
        original = ontology;
        repairs = super.createRepairs(original);
    }

    BraveSemantics (MyOntology ontology, MyOntology[] givenRepairs) throws Exception{
        original = ontology;
        repairs = givenRepairs;
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

    public Boolean isEntailed (OWLAxiom query) throws Exception{
        Boolean[] answers = super.repairEntailments(repairs, query);

        for(boolean b : answers) {

            if(b){
                return true;
            }
        }

        return false;
    }

    public Set<OWLAxiom> getClassAssertionAxioms() throws Exception{

        Set<OWLAxiom> braveClassAssertionAxioms = new HashSet<>();

        for (int i = 0 ; i < repairs.length ; i++) {
            OWLDataFactory df = repairs[i].getManager().getOWLDataFactory();
            OWLReasonerFactory rf = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory();
            OWLReasoner r = rf.createReasoner(repairs[i].getOntology());

            InferredClassAssertionAxiomGenerator classAssertionAxiomGenerator = new InferredClassAssertionAxiomGenerator();
            Set<OWLClassAssertionAxiom> classAssertionAxioms = classAssertionAxiomGenerator.createAxioms(df, r);

            for (OWLClassAssertionAxiom classAssertionAxiom: classAssertionAxioms){
                braveClassAssertionAxioms.add(classAssertionAxiom);
            }

            for (OWLClass cls : original.getOntology().getClassesInSignature()) {
                for (OWLClassAssertionAxiom classAssertionAxiom : repairs[i].getOntology().getClassAssertionAxioms(cls)) {
                    braveClassAssertionAxioms.add(classAssertionAxiom);
                }
            }

            braveClassAssertionAxioms.addAll(classAssertionAxioms);
        }

        return braveClassAssertionAxioms;
    }

    public Set<OWLPropertyAssertionAxiom> getPropertyAssertionAxioms() throws Exception{
        Set BravePropertyAssertionAxioms = new HashSet();

        for (int i = 0 ; i < repairs.length ; i++) {
            OWLDataFactory df = repairs[i].getManager().getOWLDataFactory();
            OWLReasonerFactory rf = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory();
            OWLReasoner r = rf.createReasoner(repairs[i].getOntology());

            InferredPropertyAssertionGenerator propertyAssertionGenerator = new InferredPropertyAssertionGenerator();
            Set<OWLPropertyAssertionAxiom<?,?>> propertyAssertionAxioms = propertyAssertionGenerator.createAxioms(df, r);

            BravePropertyAssertionAxioms.addAll(propertyAssertionAxioms);
        }

        return BravePropertyAssertionAxioms;
    }

}
