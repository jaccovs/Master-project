package org.exquisite.core.querycomputation;

import org.exquisite.core.AbstractTest;
import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.exquisite.core.utils.OWLUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.*;

/**
 * Tests the running example ontology (ontologies/running_example_annotated.owl).
 * This ontology has 6 diagnoses.
 * init() defines the measures of these 6 diagnoses. setDiagnosesMeasures() sets them.
 *
 * @author wolfi
 */
public abstract class AbstractTestQueryComputation<T> extends AbstractTest {

    protected static Map<Set<String>,BigDecimal> mapping = new HashMap<>();

    @BeforeClass
    public static void init() {
        mapping.put(getSet("A", "C", "E", "F", "M", "X", "Z"),  new BigDecimal("0.04"));
        mapping.put(getSet("C", "F", "H", "M", "X", "Z"),       new BigDecimal("0.07"));
        mapping.put(getSet("E", "F", "H", "K", "X"),            new BigDecimal("0.33"));
        mapping.put(getSet("B", "C", "F", "H", "X"),            new BigDecimal("0.14"));
        mapping.put(getSet("E", "F", "H", "M", "X"),            new BigDecimal("0.01"));
        mapping.put(getSet("A", "C", "F", "G", "H", "M", "Z"),  new BigDecimal("0.41"));
    }

    @Test
    public void testQueryComputation() throws OWLOntologyCreationException, DiagnosisException {
        File ontology = new File(ClassLoader.getSystemResource("ontologies/running_example_annotated.owl").getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology);

        IDiagnosisEngine<OWLLogicalAxiom> engine = getDiagnosisEngine(reasoner);
        engine.setMaxNumberOfDiagnoses(9);

        System.out.println("Start calculating diagnosis on the following " + engine.getSolver().getDiagnosisModel());

        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();
        System.out.println(diagnoses.size() + " diags found");
        assertEquals(6, diagnoses.size());

        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            System.out.println(" ----");
            for (OWLLogicalAxiom axiom : diagnosis.getFormulas()) {
                System.out.println(OWLUtils.getString(axiom));
            }
        }

        System.out.println(" ----");

        setDiagnosesMeasures(diagnoses);

        System.out.println(" ----");

        reasoner.setEntailmentTypes(InferenceType.DISJOINT_CLASSES, InferenceType.CLASS_HIERARCHY);

        final long nanoTime = System.nanoTime();
        testQueryComputation(reasoner, engine, diagnoses);
        System.out.println((double)((System.nanoTime()-nanoTime) / (double)1000000000L) + " seconds");
        engine.resetEngine();
    }

    protected void testQueryComputation(ExquisiteOWLReasoner reasoner, IDiagnosisEngine<OWLLogicalAxiom> engine, Set<Diagnosis<OWLLogicalAxiom>> diagnoses) throws DiagnosisException {
        System.out.println("Start Query computation");

        // query computation
        IQueryComputation<OWLLogicalAxiom> queryComputation = getQueryComputation(engine);
        assertNotNull(queryComputation);
        if (queryComputation instanceof HeuristicQueryComputation) {
            System.out.println("Configuration: " + ((HeuristicQueryComputation)queryComputation).getConfig());
        }
        queryComputation.initialize(diagnoses);

        int i = 1;
        assertTrue(queryComputation.hasNext());
        while (queryComputation.hasNext()) {
            Query<OWLLogicalAxiom> query = queryComputation.next();

            assertNotNull(query);
            assertNotNull(query.score);
            // assertTrue("score " + query.score + " not in [0..1]",query.score.compareTo(BigDecimal.ZERO) >= 0 && query.score.compareTo(BigDecimal.ONE) <= 0);
            assertNotNull(query.formulas);

            System.out.println("query " + (i++) + ": score = " + query.score);
            for (OWLLogicalAxiom formula : query.formulas) {
                assertNotNull(formula);
                System.out.println("\t-> " + OWLUtils.getString(formula));
            }

            query.qPartition.computeProbabilities();

            assertNotNull(query.qPartition);
            assertNotNull(query.qPartition.dx);
            assertNotNull(query.qPartition.probDx);

            System.out.println("dx - partition: " + query.qPartition.probDx);
            for (Diagnosis<OWLLogicalAxiom> diagnosis : query.qPartition.dx) {
                System.out.println("\t-> " + OWLUtils.getString(diagnosis) + " : " + diagnosis.getMeasure());
            }
            assertTrue(query.qPartition.probDx.compareTo(BigDecimal.ZERO) >= 0 && query.qPartition.probDx.compareTo(BigDecimal.ONE) <= 0);

            assertNotNull(query.qPartition.dnx);
            assertNotNull(query.qPartition.probDnx);
            System.out.println("dnx - partition: " + query.qPartition.probDnx);
            for (Diagnosis<OWLLogicalAxiom> diagnosis : query.qPartition.dnx) {
                System.out.println("\t-> " + OWLUtils.getString(diagnosis) + " : " + diagnosis.getMeasure());
            }
            assertTrue(query.qPartition.probDnx.compareTo(BigDecimal.ZERO) >= 0 && query.qPartition.probDnx.compareTo(BigDecimal.ONE) <= 0);

            assertNotNull(query.qPartition.dz);
            System.out.println("dz - partition: ");
            for (Diagnosis<OWLLogicalAxiom> diagnosis : query.qPartition.dz) {
                System.out.println("\t-> " + OWLUtils.getString(diagnosis) + " : " + diagnosis.getMeasure());
            }

            assertEquals(0, BigDecimal.ONE.compareTo(query.qPartition.probDx.add(query.qPartition.probDnx)));
        }

        assertTrue(i>=1);
        queryComputation.reset();
    }

    abstract protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner);

    abstract protected IQueryComputation<OWLLogicalAxiom> getQueryComputation(IDiagnosisEngine engine);


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
