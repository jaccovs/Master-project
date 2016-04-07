package org.exquisite.core;

import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.core.solver.ExquisiteOWLReasoner2;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author wolfi
 */
abstract public class AbstractTestHeuristicQueryComputation<T> extends AbstractTest {

    protected static Map<Set<String>,BigDecimal> mapping = new HashMap<>();

    @BeforeClass
    public static void init() {
        mapping.put(getSet("A", "C", "E", "F", "M", "X", "Z"),  new BigDecimal("0.01"));
        mapping.put(getSet("C", "F", "H", "M", "X", "Z"),       new BigDecimal("0.33"));
        mapping.put(getSet("E", "F", "H", "K", "X"),            new BigDecimal("0.14"));
        mapping.put(getSet("B", "C", "F", "H", "X"),            new BigDecimal("0.07"));
        mapping.put(getSet("E", "F", "H", "M", "X"),            new BigDecimal("0.41"));
        mapping.put(getSet("A", "C", "F", "G", "H", "M", "Z"),  new BigDecimal("0.04"));
    }

    @Test
    public void testHeuristicQueryComputation() throws OWLOntologyCreationException, DiagnosisException {
        File ontology = new File(ClassLoader.getSystemResource("ontologies/running_example_annotated.owl").getFile());
        ExquisiteOWLReasoner2 reasoner = (ExquisiteOWLReasoner2)createReasoner(ontology);

        IDiagnosisEngine<OWLLogicalAxiom> engine = getDiagnosisEngine(reasoner);// new InverseDiagnosisEngine<>(reasoner); // new HSTreeEngine<>(reasoner);
        engine.setMaxNumberOfDiagnoses(9);

        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();
        System.out.println(diagnoses.size() + " diags found");
        assertEquals(6, diagnoses.size());

        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            System.out.println(" ----");
            for (OWLLogicalAxiom axiom : diagnosis.getFormulas()) {
                System.out.println(axiom.getAxiomWithoutAnnotations());
            }
        }

        System.out.println(" ----");

        setDiagnosesMeasures(diagnoses);

        System.out.println(" ----");
        System.out.println("Start Query computation");
        final long nanoTime = System.nanoTime();

        // Query configuration
        HeuristicConfiguration config = new HeuristicConfiguration((AbstractDiagnosisEngine)engine);
        // config.setMinQueries(2);
        // config.setMaxQueries(4);
        // config.setTimeout(10000);
        reasoner.setEntailmentTypes(InferenceType.DISJOINT_CLASSES,
                InferenceType.CLASS_HIERARCHY,
                InferenceType.OBJECT_PROPERTY_HIERARCHY,
                InferenceType.DATA_PROPERTY_HIERARCHY,
                InferenceType.CLASS_ASSERTIONS,
                InferenceType.OBJECT_PROPERTY_ASSERTIONS,
                InferenceType.DATA_PROPERTY_ASSERTIONS,
                InferenceType.SAME_INDIVIDUAL,
                InferenceType.DIFFERENT_INDIVIDUALS);

        // query computation
        HeuristicQueryComputation<OWLLogicalAxiom> queryComputation = new HeuristicQueryComputation<>(config);
        queryComputation.initialize(diagnoses);

        int i = 1;
        while (queryComputation.hasNext()) {
            Query<OWLLogicalAxiom> query = queryComputation.next();
            System.out.println("query " + i + ": ");
            for (OWLLogicalAxiom formula : query.formulas) {
                System.out.println("\t-> " + formula);
            }
        }

        assertTrue(i >= config.getMinQueries());
        assertTrue(i <= config.getMaxQueries());

        System.out.println((double)((System.nanoTime()-nanoTime) / (double)1000000000L) + " seconds");
    }

    abstract protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner);

    @Override
    protected ExquisiteOWLReasoner createReasoner(OWLOntology ontology) throws OWLOntologyCreationException, DiagnosisException {
        ReasonerFactory reasonerFactory = new ReasonerFactory();
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = ExquisiteOWLReasoner2.generateDiagnosisModel(ontology, reasonerFactory);

        for (OWLIndividual ind : ontology.getIndividualsInSignature()) {
            diagnosisModel.getCorrectFormulas().addAll(ontology.getClassAssertionAxioms(ind));
            diagnosisModel.getCorrectFormulas().addAll(ontology.getObjectPropertyAssertionAxioms(ind));
        }
        diagnosisModel.getPossiblyFaultyFormulas().removeAll(diagnosisModel.getCorrectFormulas());

        return new ExquisiteOWLReasoner2(diagnosisModel, ontology.getOWLOntologyManager(), reasonerFactory);
    }

    protected void setDiagnosesMeasures(Set<Diagnosis<OWLLogicalAxiom>> diagnoses) {
        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            Set<String> axiomsInDiagnoses = new HashSet<>();
            for (OWLLogicalAxiom axiom : diagnosis.getFormulas()) {
                for (OWLClassExpression expression : axiom.getNestedClassExpressions()) {
                    if (expression instanceof OWLClass) {
                        final String s = ((OWLClass) expression).getIRI().getRemainder().get();
                        axiomsInDiagnoses.add(s);
                    }
                }
            }

            BigDecimal measure = mapping.get(axiomsInDiagnoses);
            if (measure != null) {
                diagnosis.setMeasure(measure);
                System.out.println("set measure " + measure + " for diagnosis " + axiomsInDiagnoses);
            }
        }
    }

    @SafeVarargs
    public static <T> HashSet<T> getSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }
}
