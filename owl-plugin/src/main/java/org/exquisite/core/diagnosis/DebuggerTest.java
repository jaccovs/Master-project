package org.exquisite.core.diagnosis;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;


import java.io.File;

public class DebuggerTest {

    public static void main(String[] args) throws Exception {
        
        MyOntology original = new MyOntology("ontologies/Matthew/boat.owl");
        File ontologySource = new File(ClassLoader.getSystemResource(original.getOntologyName()).getFile());
        original.setOntology(original.getManager().loadOntologyFromOntologyDocument(ontologySource));

        OWLReasonerFactory rf = new com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory();
        OWLReasoner r = rf.createReasoner(original.getOntology());
        OWLDataFactory df = original.getManager().getOWLDataFactory();
        OWLClass car = df.getOWLClass(IRI.create("http://owl.api.toyExample#Car"));
        OWLObjectProperty drives = df.getOWLObjectProperty(IRI.create("http://owl.api.toyExample#Drives"));
        OWLClassExpression drivesSomeCar = df.getOWLObjectSomeValuesFrom(drives, car);

        OWLClass mechanic = df.getOWLClass(IRI.create("http://owl.api.toyExample#Human"));
        OWLIndividual max = df.getOWLNamedIndividual(IRI.create("http://owl.api.toyExample#Max"));
        OWLIndividual rb14 = df.getOWLNamedIndividual(IRI.create("http://owl.api.toyExample#rb14"));
        OWLAxiom mechanicMax = df.getOWLClassAssertionAxiom(mechanic, max);
        OWLAxiom CarRb14 = df.getOWLClassAssertionAxiom(car, rb14);
        OWLAxiom maxDrivesCar = df.getOWLClassAssertionAxiom(drivesSomeCar, max);

        if (!r.isConsistent()) {

            ARSemantics AR = new ARSemantics(original);

            BraveSemantics Brave = new BraveSemantics(original, AR.getRepairs());

            IARSemantics IAR = new IARSemantics(original, AR.getRepairs());

            CARSemantics CAR = new CARSemantics(original, AR.getRepairs());

            ICRSemantics ICR = new ICRSemantics(original, AR.getRepairs());

//            MyOntology[] repairList = AR.getRepairs();
//            for (int i = 0; i < repairList.length; i++) {
//                System.out.println(repairList[i].getOntology());
//            }

        System.out.println("The number of Abox Repairs = " + AR.getRepairs().length + "\n");

        System.out.println("The number of asserted + inferred classAssertionAxioms under AR semantics = " + AR.getClassAssertionAxioms().size());
        System.out.println("The number of asserted + inferred propertyAssertionAxioms under AR semantics = " + AR.getPropertyAssertionAxioms().size() + "\n");

        System.out.println("The number of asserted + inferred classAssertionAxioms under Brave semantics = " + Brave.getClassAssertionAxioms().size());
        System.out.println("The number of asserted + inferred propertyAssertionAxioms under Brave semantics = " + Brave.getPropertyAssertionAxioms().size() + "\n");

        System.out.println("The number of asserted + inferred classAssertionAxioms under IAR semantics = " + IAR.getClassAssertionAxioms().size());
        System.out.println("The number of asserted + inferred propertyAssertionAxioms under IAR semantics = " + IAR.getPropertyAssertionAxioms().size() + "\n");

        System.out.println("The number of asserted + inferred classAssertionAxioms under CAR semantics = " + CAR.getClassAssertionAxioms().size());
        System.out.println("The number of asserted + inferred propertyAssertionAxioms under CAR semantics = " + CAR.getPropertyAssertionAxioms().size() + "\n");

        System.out.println("The number of asserted + inferred classAssertionAxioms under ICR semantics = " + ICR.getClassAssertionAxioms().size());
        System.out.println("The number of asserted + inferred propertyAssertionAxioms under ICR semantics = " + ICR.getPropertyAssertionAxioms().size());

        }
        else {
            System.out.println("Ontology is consistent!");
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