package org.exquisite.core;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.fastinfoset.sax.SystemIdResolver;
import org.exquisite.core.engines.*;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.IQueryComputation;
import org.exquisite.core.query.querycomputation.SimpleNaiveQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.scoring.MinScoreQSS;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.OWLUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.Set;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;

/**
 * @author wolfi
 */
public class TestExquisiteOWLReasonerWithOntologies extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(TestExquisiteOWLReasonerWithOntologies.class);

    private ExquisiteOWLReasoner loadOntology(String ontologyName) throws OWLOntologyCreationException, DiagnosisException {
        return loadOntology(ontologyName, false, false);
    }

    private ExquisiteOWLReasoner loadOntology(String ontologyName, boolean extractModule, boolean reduceIncoherencyToInconsistency) throws OWLOntologyCreationException, DiagnosisException {
        File ontology = new File(ClassLoader.getSystemResource(ontologyName).getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology, extractModule, reduceIncoherencyToInconsistency);
        assertNotNull(reasoner);
        return reasoner;
    }

    private void testConsistency(ExquisiteOWLReasoner reasoner, boolean isConsistent) {
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = reasoner.getDiagnosisModel();
        boolean b = reasoner.isConsistent(diagnosisModel.getPossiblyFaultyFormulas());
        if (isConsistent) assertTrue(b);
        else assertFalse(b);
    }

    private Set<Diagnosis<OWLLogicalAxiom>> calculateDiagnoses(final String ontology, final IDiagnosisEngine diagnosisEngine, final int nrOfDiagnoses) throws DiagnosisException {
        diagnosisEngine.resetEngine();
        diagnosisEngine.setMaxNumberOfDiagnoses(nrOfDiagnoses);

        if (logger.isDebugEnabled()) logger.debug("calculating " + nrOfDiagnoses + " diagnoses for OWL-ontology " + ontology + " using reasoner " + diagnosisEngine.getSolver() + " with engine " + diagnosisEngine);
        final Set diagnoses = diagnosisEngine.calculateDiagnoses();
        assertNotNull(diagnoses);
        assertEquals(nrOfDiagnoses, diagnoses.size());
        if (logger.isDebugEnabled()) logger.debug("done");
        return diagnoses;
    }

    private void testConsistentButIncoherentOntology(String ontologyName, int... nrOfDiags) throws DiagnosisException, OWLOntologyCreationException {
        ExquisiteOWLReasoner reasoner = loadOntology(ontologyName, false, false);
        this.testConsistency(reasoner, true); // ontology is consistent
        // therefore no diagnosis
        IDiagnosisEngine inverseDiagnosisEngine = new InverseDiagnosisEngine(reasoner);
        final Set<Diagnosis<OWLLogicalAxiom>> noDiagnoses = calculateDiagnoses(ontologyName, inverseDiagnosisEngine, 0);

        // make sure that ontology is incoherent by converting to inconsistent ontology (TODO shall moduleExtractor be used)?
        reasoner = loadOntology(ontologyName, false, true);
        testInConsistentOntology(ontologyName, reasoner, nrOfDiags);

    }

    private void testInConsistentOntology(final String ontology, ExquisiteOWLReasoner reasoner, int... nrOfDiags) throws DiagnosisException {
        this.testConsistency(reasoner, false);

        reasoner.setEntailmentTypes(InferenceType.DISJOINT_CLASSES, InferenceType.CLASS_HIERARCHY);

        final InverseDiagnosisEngine<OWLLogicalAxiom> inverseDiagnosisEngine = new InverseDiagnosisEngine<>(reasoner);
        //final HSTreeEngine<OWLLogicalAxiom> hsTreeEngine = new HSTreeEngine<>(reasoner);
        //final HSDAGEngine<OWLLogicalAxiom> hsdagEngine = new HSDAGEngine<>(reasoner);
        IDiagnosisEngine<OWLLogicalAxiom>[] diagnosisEngines = new IDiagnosisEngine[]{inverseDiagnosisEngine/*, hsTreeEngine, hsdagEngine*/};

        HeuristicQueryComputation<OWLLogicalAxiom> hqc = new HeuristicQueryComputation<>(new HeuristicConfiguration((AbstractDiagnosisEngine)inverseDiagnosisEngine));
        SimpleNaiveQueryComputation<OWLLogicalAxiom> sqc = new SimpleNaiveQueryComputation<>(inverseDiagnosisEngine, new MinScoreQSS<>());
        IQueryComputation<OWLLogicalAxiom>[] queryComputations = new IQueryComputation[]{ hqc, sqc};

        for (IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine : diagnosisEngines) {
            for (int nr : nrOfDiags) {
                final Set<Diagnosis<OWLLogicalAxiom>> diagnoses = calculateDiagnoses(ontology, diagnosisEngine, nr);
                setDiagnosesMeasures(diagnoses);
                for (IQueryComputation<OWLLogicalAxiom> qc : queryComputations) {
                    testQueryGeneration(qc, diagnoses);
                }
            }
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

    @Test
    public void testECAI2010Ontology() throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = loadOntology("ontologies/ecai2010.owl");
        testInConsistentOntology("ontologies/ecai2010.owl",reasoner, 3,4);
}

    @Test
    public void testRunningExampleOntology() throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = loadOntology("ontologies/running_example_annotated.owl");
        testInConsistentOntology("ontologies/running_example_annotated.owl", reasoner, 6,4);
    }

    @Test
    public void testEconomyOntology() throws OWLOntologyCreationException, DiagnosisException {
        testConsistentButIncoherentOntology("ontologies/Economy-SDA.owl", 2);
    }

    @Test
    public void testTransportationOntology() throws OWLOntologyCreationException, DiagnosisException {
        testConsistentButIncoherentOntology("ontologies/Transportation-SDA.owl", 2);
    }

    @Test
    public void testUniversityOntology() throws OWLOntologyCreationException, DiagnosisException {
        testConsistentButIncoherentOntology("ontologies/University.owl", 2,3);
    }

}
