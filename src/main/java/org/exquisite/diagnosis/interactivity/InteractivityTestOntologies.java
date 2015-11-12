package org.exquisite.diagnosis.interactivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.interactivity.partitioning.BruteForce;
import org.exquisite.diagnosis.interactivity.partitioning.CKK;
import org.exquisite.diagnosis.interactivity.partitioning.Partitioning;
import org.exquisite.diagnosis.interactivity.partitioning.costestimators.OWLAxiomKeywordCostsEstimator;
import org.exquisite.diagnosis.interactivity.partitioning.scoring.QSSFactory;
import org.exquisite.diagnosis.interactivity.partitioning.scoring.Scoring;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.diagnosis.quickxplain.QuickXPlain.SolverType;
import org.exquisite.diagnosis.quickxplain.ontologies.OntologyTools;
import org.exquisite.tools.Utilities;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Test class to test the user interactivity tests.diagnosis engine with ontologies
 * 
 * @author Schmitz
 *
 */
public class InteractivityTestOntologies {
	
	private static final Logger log = Logger.getLogger(InteractivityTestOntologies.class.getSimpleName());

	static String inputFileDirectory = "experiments/Ontologies/";

//	static String scenario = "koala";
//	static String scenario = "University";
//	static String scenario = "Transportation-SDA";
//	static String scenario = "example1";
	static String scenario = "ecai2010";
	
	static String fileEnding = ".owl";
	
	public void runInteractivityTest() {
		log.info("Starting interacitivity test");

		QuickXPlain.SOLVERTYPE = SolverType.OWLAPI;

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
			DiagnosisModel diagModel = OntologyTools.createDiagnosisModel(o, true);		
			OntologyTools.reduceToUnsatisfiability(diagModel);
			
			// Create the engine
			ExquisiteSession sessionData = new ExquisiteSession(null,
					null, new DiagnosisModel(diagModel));
			// Do not try to find a better strategy for the moment
			sessionData.config.searchStrategy = SearchStrategies.Default;
			
			IDiagnosisEngine innerEngine = EngineFactory.makeEngine(engineType, sessionData, threadPoolSize);
	
			Collections.shuffle(innerEngine.getModel().getPossiblyFaultyStatements());
	
			int diagnosesPerQuery = 9;
	
			Scoring scoring = QSSFactory.createMinScoreQSS();
	
			Partitioning partitioning = new CKK(innerEngine, scoring);
	
			Diagnosis correctDiagnosis = getCorrectDiagnosis(innerEngine);
			
			OWLAxiomKeywordCostsEstimator estimator = new OWLAxiomKeywordCostsEstimator(diagModel);
			
			InteractivityDiagnosisEngine.ADD_IMPLICIT_STATEMENTS_AS_ENTAILMENTS = true;
			InteractivityDiagnosisEngine.ADD_EXPLICIT_STATEMENTS_AS_ENTAILMENTS = false;
	
			InteractivityDiagnosisEngine engine = new InteractivityDiagnosisEngine(innerEngine.getSessionData(), innerEngine, diagnosesPerQuery,
					partitioning, correctDiagnosis);
			
			engine.setCostsEstimator(estimator);
	
			try {
				List<Diagnosis> diagnoses = engine.calculateDiagnoses();
				log.info("Found " + diagnoses.size() + " tests.diagnosis:  " + Utilities.printSortedDiagnoses(diagnoses, ' '));
				log.info("Real tests.diagnosis was: " + correctDiagnosis.toString());
				log.info("Needed " + engine.getNumberOfDiagnosisRuns() + " tests.diagnosis runs and " + engine.getNumberOfQueries() + " user interactions with a total of " + engine.getNumberOfQueriedStatements() + " queried statements.");
				log.info(String.format("Diagnoses calculation took %.2f s.", engine.getDiagnosisTime() / 1000f).toString());
				log.info(String.format("User interaction calculation took %.2f s.", engine.getUserInteractionTime() / 1000f).toString());
			} catch (DiagnosisException e) {
				e.printStackTrace();
			}
	
			log.info("Finished interactivity test");
		}
		catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}
	
	private Diagnosis getCorrectDiagnosis(IDiagnosisEngine innerEngine) {
		ExquisiteSession sessionData = new ExquisiteSession(null, null, new DiagnosisModel(innerEngine.getModel()));
		sessionData.config.maxDiagnoses = 1;
		IDiagnosisEngine engine = EngineFactory.makeEngine(EngineType.HeuristicSearch,  sessionData, 1);
		try {
			List<Diagnosis> diagnoses = engine.calculateDiagnoses();
			return diagnoses.get(0);
		} catch (DiagnosisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}

	public static void main(String[] args) throws IOException {
		LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));

		InteractivityTestOntologies test = new InteractivityTestOntologies();
		test.runInteractivityTest();
	}

}
