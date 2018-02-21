package org.exquisite.core.diagnoses;

import org.exquisite.core.model.Diagnosis;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.*;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

public class DebuggerTest {

    public static void main(String[] args) throws Exception {

        MyOntology original = new MyOntology("ontologies/University.owl");

        File ontologySource = new File(ClassLoader.getSystemResource(original.getOntologyName()).getFile());
        original.setOntology(original.getManager().loadOntologyFromOntologyDocument(ontologySource));

        System.out.println(original.getOntology());

        OWLReasonerFactory rf = new ReasonerFactory();
        OWLReasoner r = rf.createReasoner(original.getOntology());
        if (!r.isConsistent()) {

            ARSemantics AR = new ARSemantics(original);

            BraveSemantics Brave = new BraveSemantics(original);

            IARSemantics IAR = new IARSemantics(original);

            ICRSemantics ICR = new ICRSemantics(original);

            MyOntology[] repairList = AR.getRepairs();
            for (int i = 0; i < repairList.length; i++) {
                System.out.println(repairList[i].getOntology());
            }

//        OWLDataFactory df = original.getManager().getOWLDataFactory();
//
//        OWLClass mechanic = df.getOWLClass(IRI.create("http://owl.api.toyExample#Human"));
//        OWLIndividual max = df.getOWLNamedIndividual(IRI.create("http://owl.api.toyExample#Max"));
//        OWLAxiom mechanicMax = df.getOWLClassAssertionAxiom(mechanic, max);
//
//        System.out.println(AR.isEntailed(mechanicMax));
            System.out.println(AR.getClassAssertionAxioms().size());

//        System.out.println(Brave.isEntailed(mechanicMax));
            System.out.println(Brave.getClassAssertionAxioms().size());

//        System.out.println(IAR.isEntailed(mechanicMax));
            System.out.println(IAR.getClassAssertionAxioms().size());

//        System.out.println(ICR.isEntailed(mechanicMax));
            System.out.println(ICR.getClassAssertionAxioms().size());
        }
        else {
            System.out.println("Consistent Bitch!");
        }

//    private static Boolean CARSemantics(MyOntology original, OWLAxiom axiomToCheck) {
//        OWLDataFactory df = original.getManager().getOWLDataFactory();
//        OWLReasonerFactory rf = new ReasonerFactory();
//        OWLReasoner r = rf.createReasoner(original.getOntology());
//
//        InferredClassAssertionAxiomGenerator classAssertionGen = new InferredClassAssertionAxiomGenerator();
//        InferredPropertyAssertionGenerator propertyAssertionGen = new InferredPropertyAssertionGenerator();
//
//        Set<OWLClassAssertionAxiom> classAssertionInference = classAssertionGen.createAxioms(df, r);
//        Set<OWLPropertyAssertionAxiom<?, ?>> propertyAssertionInference = propertyAssertionGen.createAxioms(df, r);
//
//        Set<OWLAxiom> inferedAxiomsToAdd = new HashSet<>();
//        inferedAxiomsToAdd.addAll(classAssertionInference);
//        inferedAxiomsToAdd.addAll(propertyAssertionInference);
//
//        ArrayList<OWLOntologyChange> addAxioms = new ArrayList<>();
//
//        for (OWLAxiom axiom : inferedAxiomsToAdd) {
//            addAxioms.add(new AddAxiom(original.getOntology(), axiom));
//        }
//        original.getManager().applyChanges(addAxioms);
//
//        Boolean answer = null;
//
//        try {
//            answer = ARSemantics(createRepairs(original), axiomToCheck);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return answer;
//    }
    }
}