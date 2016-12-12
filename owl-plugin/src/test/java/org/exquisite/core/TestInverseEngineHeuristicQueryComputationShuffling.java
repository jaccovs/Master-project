package org.exquisite.core;

import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.engines.InverseDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.EntropyBasedMeasure;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import utils.OWLUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.*;

/**
 * @author wolfi
 */
public class TestInverseEngineHeuristicQueryComputationShuffling<F> extends AbstractTestQueryComputation<F> {

    protected static int ITERATIONS = 50;

    private Set<Set<Diagnosis<OWLLogicalAxiom>>> allDiagnosesFound;
    private Set<Query<OWLLogicalAxiom>> allQueriesFound;

    @Before
    public void initBeforeTest() {
        allDiagnosesFound = new HashSet<>(ITERATIONS);
        allQueriesFound = new HashSet<>(ITERATIONS);
    }

    @Override
    protected IDiagnosisEngine<OWLLogicalAxiom> getDiagnosisEngine(ExquisiteOWLReasoner reasoner) {
        return new InverseDiagnosisEngine<>(reasoner);
    }

    @Override
    protected IQueryComputation<OWLLogicalAxiom> getQueryComputation(IDiagnosisEngine engine) {
        HeuristicConfiguration config = new HeuristicConfiguration<OWLLogicalAxiom>((AbstractDiagnosisEngine) engine, logger);
        config.setRm(new EntropyBasedMeasure<>(new BigDecimal("0.05")));                                          // ENT
        return new HeuristicQueryComputation<>(config);
    }

    @Override
    public void testQueryComputation() throws OWLOntologyCreationException, DiagnosisException {

        File ontology = new File(ClassLoader.getSystemResource("ontologies/running_example_annotated.owl").getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology);
        IDiagnosisEngine<OWLLogicalAxiom> engine = getDiagnosisEngine(reasoner);
        engine.setMaxNumberOfDiagnoses(2); // get 2 out of 6 possible diagnoses

        List<OWLLogicalAxiom> list = new ArrayList<>(engine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());

        for (int iteration = 1; iteration <= ITERATIONS; iteration++) {

            Collections.shuffle(list);

            engine.getSolver().getDiagnosisModel().setPossiblyFaultyFormulas(list);

            Set<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();
            System.out.println(diagnoses.size() + " diags found");
            assertEquals(2, diagnoses.size());
            allDiagnosesFound.add(diagnoses);

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
            startQueryGeneration(reasoner, engine, diagnoses);
            engine.resetEngine();
            System.out.println((double) ((System.nanoTime() - nanoTime) / (double) 1000000000L) + " seconds");
        }

        System.out.println("found " + allDiagnosesFound.size() + " different diagnoses after " + ITERATIONS + " shuffled iterations");
        System.out.println("found " + allQueriesFound.size() + " different queries after " + ITERATIONS + " shuffled iterations");
        assertTrue(allDiagnosesFound.size() > 1);
        assertTrue(allQueriesFound.size() > 1);
        assertEquals(allQueriesFound.size(), allDiagnosesFound.size());
    }

    @Override
    protected void startQueryGeneration(ExquisiteOWLReasoner reasoner, IDiagnosisEngine<OWLLogicalAxiom> engine, Set<Diagnosis<OWLLogicalAxiom>> diagnoses) throws DiagnosisException {
        IQueryComputation<OWLLogicalAxiom> queryComputation = getQueryComputation(engine);
        queryComputation.initialize(diagnoses);

        if (queryComputation.hasNext()) {
            Query<OWLLogicalAxiom> query = queryComputation.next();
            assertNotNull(query);
            allQueriesFound.add(query);
            System.out.println(query);
        } else {
            fail("expected at least one query");
        }

        queryComputation.reset();

    }
}
