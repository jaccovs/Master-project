package org.exquisite.core.diagnoses;

import org.exquisite.core.model.Diagnosis;
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

    public Set<OWLClassAssertionAxiom> getClassAssertionAxioms() throws Exception{

        Set<OWLClassAssertionAxiom> braveClassAssertionAxioms = new HashSet<>();

        for (int i = 0 ; i < repairs.length ; i++) {
            OWLDataFactory df = repairs[i].getManager().getOWLDataFactory();
            OWLReasonerFactory rf = new ReasonerFactory();
            OWLReasoner r = rf.createReasoner(repairs[i].getOntology());

            InferredClassAssertionAxiomGenerator classAssertionAxiomGenerator = new InferredClassAssertionAxiomGenerator();
            Set<OWLClassAssertionAxiom> classAssertionAxioms = classAssertionAxiomGenerator.createAxioms(df, r);

            braveClassAssertionAxioms.addAll(classAssertionAxioms);
        }

        return braveClassAssertionAxioms;
    }

}
