package tests.interactivity;

import org.exquisite.core.engines.InteractiveDiagnosisEngine;
import org.exquisite.core.query.QueryComputation;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.query.CKK;
import org.exquisite.core.costestimators.OWLAxiomKeywordCostsEstimator;
import org.exquisite.core.query.scoring.QSSFactory;
import org.exquisite.core.query.scoring.Scoring;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain.SolverType;
import org.exquisite.diagnosis.quickxplain.ontologies.OntologyTools;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Test class to test the user interactivity tests.diagnosis engine with ontologies
 *
 * @author Schmitz
 */
public class InteractivityTestOntologies extends InteractivityTest{

    private static final Logger log = Logger.getLogger(InteractivityTestOntologies.class.getSimpleName());

    static String inputFileDirectory = "experiments/Ontologies/";

    //	static String scenario = "koala";
//	static String scenario = "University";
//	static String scenario = "Transportation-SDA";
//	static String scenario = "example1";
    static String scenario = "ecai2010";

    static String fileEnding = ".owl";

    public static void main(String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));

        InteractivityTestOntologies test = new InteractivityTestOntologies();
        test.runInteractivityTest();
    }

    public void runInteractivityTest() {
        log.info("Starting interacitivity test");

        ConstraintsQuickXPlain.SOLVERTYPE = SolverType.OWLAPI;

        String fullInputFilename = inputFileDirectory + scenario + fileEnding;

        EngineType engineType = EngineType.HSDagStandardQX;
        // EngineType engineType = EngineType.HeuristicSearch;

        int threadPoolSize = 4;

        try {
            // Load ontology
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            // map the ontology IRI to a physical IRI (files for example)
            File input = new File(fullInputFilename);
            OWLOntology o = m.loadOntologyFromOntologyDocument(input);
            //		o = OntologyTools.getIncoherentPartAsOntology(o);
            DiagnosisModel<OWLLogicalAxiom> diagModel = OntologyTools.createDiagnosisModel(o, true);
            OntologyTools.reduceToUnsatisfiability(diagModel);

            // Create the engine
            ExcelExquisiteSession<OWLLogicalAxiom> sessionData = new ExcelExquisiteSession<>(null,
                    null, new DiagnosisModel<>(diagModel));
            // Do not try to find a better strategy for the moment
            sessionData.getConfiguration().searchStrategy = SearchStrategies.Default;

            IDiagnosisEngine<OWLLogicalAxiom> innerEngine = EngineFactory
                    .makeEngine(engineType, sessionData, threadPoolSize);

            assert innerEngine != null;
            Collections.shuffle(innerEngine.getDiagnosisModel().getPossiblyFaultyStatements());

            int diagnosesPerQuery = 9;

            Scoring scoring = QSSFactory.createMinScoreQSS();

            QueryComputation<OWLLogicalAxiom> queryComputation = new CKK<>(innerEngine, scoring);

            Diagnosis<OWLLogicalAxiom> correctDiagnosis = getCorrectDiagnosis(innerEngine);

            OWLAxiomKeywordCostsEstimator estimator = new OWLAxiomKeywordCostsEstimator(diagModel);

            InteractiveDiagnosisEngine.ADD_IMPLICIT_STATEMENTS_AS_ENTAILMENTS = true;
            InteractiveDiagnosisEngine.ADD_EXPLICIT_STATEMENTS_AS_ENTAILMENTS = false;

            InteractiveDiagnosisEngine<OWLLogicalAxiom> engine = new InteractiveDiagnosisEngine<>(innerEngine
                    .getDiagnosisModel(),
                    innerEngine, diagnosesPerQuery,
                    queryComputation, correctDiagnosis);

            engine.setCostsEstimator(estimator);

            try {
                List<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();
                log(correctDiagnosis, engine, diagnoses);
            } catch (DiagnosisException e) {
                e.printStackTrace();
            }

            log.info("Finished interactivity test");
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

    private Diagnosis<OWLLogicalAxiom> getCorrectDiagnosis(IDiagnosisEngine<OWLLogicalAxiom> innerEngine) {
        ExcelExquisiteSession<OWLLogicalAxiom> sessionData = new ExcelExquisiteSession<>(null, null, new DiagnosisModel<>
                (innerEngine.getDiagnosisModel
                        ()));
        sessionData.getConfiguration().maxDiagnoses = 1;
        IDiagnosisEngine<OWLLogicalAxiom> engine = EngineFactory.makeEngine(EngineType.HeuristicSearch, sessionData, 1);
        try {
            assert engine != null;
            List<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();
            return diagnoses.get(0);
        } catch (DiagnosisException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

}
