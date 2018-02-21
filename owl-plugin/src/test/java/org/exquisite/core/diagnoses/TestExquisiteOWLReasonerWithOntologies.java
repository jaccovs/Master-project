package org.exquisite.core.diagnoses;

import org.exquisite.core.AbstractTest;
import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.*;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.exquisite.core.utils.OWLUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * @author wolfi
 */
public class TestExquisiteOWLReasonerWithOntologies extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(TestExquisiteOWLReasonerWithOntologies.class);

    private void testConsistency(ExquisiteOWLReasoner reasoner, boolean isConsistent) {
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = reasoner.getDiagnosisModel();
        boolean b = reasoner.isConsistent(diagnosisModel.getPossiblyFaultyFormulas());
        if (isConsistent) assertTrue(b);
        else assertFalse(b);
    }

    private Set<Diagnosis<OWLLogicalAxiom>> calculateDiagnoses(final String ontology, final IDiagnosisEngine diagnosisEngine, final int expectedNumberOfDiagnoses, final int maxNumberOfDiagnoses) throws DiagnosisException {
        diagnosisEngine.resetEngine();
        diagnosisEngine.setMaxNumberOfDiagnoses(maxNumberOfDiagnoses);

        if (logger.isDebugEnabled()) {
            logger.debug("calculating a maximum of " + maxNumberOfDiagnoses + " diagnoses (expected: " + expectedNumberOfDiagnoses + ") for OWL-ontology " + ontology + " using reasoner " + diagnosisEngine.getSolver() + " with engine " + diagnosisEngine);
            logger.debug("based on " + diagnosisEngine.getSolver().getDiagnosisModel());
        }

        final long start = System.currentTimeMillis();
        final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = diagnosisEngine.calculateDiagnoses();
        assertNotNull(diagnoses);
        if (logger.isDebugEnabled()) {
            logger.debug("got " + diagnoses.size() + " diagnoses in " + (System.currentTimeMillis() - start) + "ms : ");
//            for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses)  // omitted to reduce output
//                logger.debug('\t' + OWLUtils.getString(diagnosis)); // omitted to reduce output
        }
        assertEquals(expectedNumberOfDiagnoses, diagnoses.size());
        if (logger.isDebugEnabled()) logger.debug("done");
        return diagnoses;
    }

    private void testConsistentButIncoherentOntology(String ontologyName, int expectedNrOfDiags, int... nrOfDiags) throws DiagnosisException, OWLOntologyCreationException {
        ExquisiteOWLReasoner reasoner = loadOntology(ontologyName, false, false);
        this.testConsistency(reasoner, true); // ontology is consistent
        // therefore no diagnosis
        IDiagnosisEngine inverseDiagnosisEngine = new InverseDiagnosisEngine(reasoner);
        final Set<Diagnosis<OWLLogicalAxiom>> noDiagnoses = calculateDiagnoses(ontologyName, inverseDiagnosisEngine, 0, 0);

        // make sure that ontology is incoherent by converting to inconsistent ontology
        reasoner = loadOntology(ontologyName, false, true);
        testInConsistentOntology(ontologyName, reasoner, expectedNrOfDiags, nrOfDiags);

        // last test: enable extractModule
        reasoner = loadOntology(ontologyName, true, true);
        testInConsistentOntology(ontologyName, reasoner, expectedNrOfDiags, nrOfDiags);

    }

    private void testInConsistentOntology(final String ontology, ExquisiteOWLReasoner reasoner, int expectedNrOfDiags, int... nrOfDiags) throws DiagnosisException {
        this.testConsistency(reasoner, false);

        reasoner.setEntailmentTypes(InferenceType.DISJOINT_CLASSES, InferenceType.CLASS_HIERARCHY);

        final InverseDiagnosisEngine<OWLLogicalAxiom> inverseDiagnosisEngine = new InverseDiagnosisEngine<>(reasoner);
        final HSTreeEngine<OWLLogicalAxiom> hsTreeEngine = new HSTreeEngine<>(reasoner);
        final HSDAGEngine<OWLLogicalAxiom> hsdagEngine = new HSDAGEngine<>(reasoner);
        IDiagnosisEngine<OWLLogicalAxiom>[] diagnosisEngines =
                expectedNrOfDiags <= 10
                        ?
                new IDiagnosisEngine[]{inverseDiagnosisEngine, hsTreeEngine, hsdagEngine}
                        :
                new IDiagnosisEngine[]{hsTreeEngine}; // use just one engine for ontologies with more than 10 expected diags.

        HeuristicQueryComputation<OWLLogicalAxiom> hqc = new HeuristicQueryComputation<>(new HeuristicConfiguration((AbstractDiagnosisEngine)inverseDiagnosisEngine, monitor));
        //SimpleNaiveQueryComputation<OWLLogicalAxiom> sqc = new SimpleNaiveQueryComputation<>(inverseDiagnosisEngine, new MinScoreQSS<>());
        IQueryComputation<OWLLogicalAxiom>[] queryComputations = new IQueryComputation[]{ hqc/*, sqc*/};

        for (IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine : diagnosisEngines) {
            for (int nr : nrOfDiags) {
                final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = calculateDiagnoses(ontology, diagnosisEngine, nr, nr);
                setDiagnosesMeasures(diagnoses);
//                for (IQueryComputation<OWLLogicalAxiom> qc : queryComputations) {
//                    testQueryGeneration(qc, diagnoses);
//                }
            }
        }

        // test if we get all expected diagnoses
        for (IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine : diagnosisEngines) {
            final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = calculateDiagnoses(ontology, diagnosisEngine, expectedNrOfDiags, Integer.MAX_VALUE);
        }

    }

    private void setDiagnosesMeasures(Set<Diagnosis<OWLLogicalAxiom>> diagnoses) {
        int size = diagnoses.size();
        BigDecimal measure = BigDecimal.valueOf(1.0/(double)size);

        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            diagnosis.setMeasure(measure);
        }
    }

    private void testQueryGeneration(IQueryComputation<OWLLogicalAxiom> qc, Set<Diagnosis<OWLLogicalAxiom>> diagnoses) throws DiagnosisException {
        if (logger.isDebugEnabled()) logger.debug("starting query computation with " + qc + " for " + diagnoses.size() + " diagnoses");
        long start = System.currentTimeMillis();
        qc.initialize(diagnoses);
        assertTrue(qc.hasNext());
        if (qc.hasNext()) {
            Query<OWLLogicalAxiom> query = qc.next();
            assertNotNull(query);
            long end = System.currentTimeMillis();
            if (logger.isDebugEnabled()) logger.debug("Got query: " + OWLUtils.getString(query) + " in " + ((end-start)/1000) + " seconds");
        }
        qc.reset();
    }

//    @Test
//    public void testECAI2010Ontology() throws OWLOntologyCreationException, DiagnosisException {
//        ExquisiteOWLReasoner reasoner = loadOntology("ontologies/ecai2010.owl");
//        testInConsistentOntology("ontologies/ecai2010.owl",reasoner, 4, 3,4);
//    }

    @Test
    public void testRunningExampleOntology() throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = loadOntology("ontologies/toyExample.owl");
        testInConsistentOntology("ontologies/toyExample.owl", reasoner,4, 4);
    }

//    @Test
//    public void testKoalaOntology() throws OWLOntologyCreationException, DiagnosisException {
//        testConsistentButIncoherentOntology("ontologies/koala.owl", 10,3,4);
//    }
//
//    @Test
//    public void testMiniTambisOntology() throws OWLOntologyCreationException, DiagnosisException {
//        testConsistentButIncoherentOntology("ontologies/miniTambis.owl", 48,2,3);
//    }
//
//    @Test
//    public void testUniversityOntology() throws OWLOntologyCreationException, DiagnosisException {
//        testConsistentButIncoherentOntology("ontologies/University.owl", 90,2,3);
//    }
//
//    @Test
//    public void testEconomyOntology() throws OWLOntologyCreationException, DiagnosisException {
//        testConsistentButIncoherentOntology("ontologies/Economy-SDA.owl", 864,2);
//    }
//
//    @Test
//    public void testTransportationOntology() throws OWLOntologyCreationException, DiagnosisException {
//        testConsistentButIncoherentOntology("ontologies/Transportation-SDA.owl", 1782,2);
//    }

}
