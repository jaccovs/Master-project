package tests.ts.interactivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.exquisite.data.ConstraintsFactory;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.tools.Utilities;

import tests.ts.interactivity.spreadsheets.SpreadsheetFormulaInteraction;

/**
 * Test class to test the user interactivity diagnosis engine
 * 
 * @author Schmitz
 *
 */
public class InteractivityTest {

	private static final Logger log = Logger.getLogger(InteractivityTest.class.getSimpleName());

	static String inputFileDirectory = "experiments/spreadsheetsindividual/";

	// static String scenario = "formula_query_example";
	// static String scenario = "Hospital_Payment_Calculation_TS";
	static String scenario = "salesforecast_TC_2Faults";

	static String correctExtension = "_correct";
	static String fileEnding = ".xml";

	// new StdScenario("new/benchmark-5faults.xml", searchDepth, maxDiags),
	// new StdScenario("new/Hospital_Payment_Calculation.xml", searchDepth, maxDiags),
	// new StdScenario("new/xxen.xml", searchDepth, maxDiags),
	// new StdScenario("SemUnitEx2_2fault.xml", searchDepth, maxDiags),
	// new StdScenario("VDEPPreserve_1fault.xml", searchDepth, maxDiags),

	public void runInteractivityTest() {
		log.info("Starting interacitivity test");

		ConstraintsFactory.PRUNE_IRRELEVANT_CELLS = true;

		String fullInputFilename = inputFileDirectory + scenario + fileEnding;
		String fullCorrectFilename = inputFileDirectory + scenario + correctExtension + fileEnding;

		EngineType engineType = EngineType.HSDagStandardQX;
		// EngineType engineType = EngineType.HeuristicSearch;

		int threadPoolSize = 4;

		IDiagnosisEngine innerEngine = EngineFactory.makeEngineFromXMLFile(engineType, fullInputFilename, threadPoolSize);

		Collections.shuffle(innerEngine.getModel().getPossiblyFaultyStatements());

		int diagnosesPerQuery = 2;
		SpreadsheetFormulaInteraction userInteraction = new SpreadsheetFormulaInteraction(fullCorrectFilename, innerEngine.getSessionData().appXML,
				innerEngine.getSessionData().diagnosisModel);
		// IBestQueryFinder bestQueryFinder = new FirstQueryFinder();
		IBestQueryFinder bestQueryFinder = new SplitInHalfQueryFinder(userInteraction);

		InteractivityDiagnosisEngine engine = new InteractivityDiagnosisEngine(innerEngine.getSessionData(), innerEngine, diagnosesPerQuery,
				userInteraction, bestQueryFinder);

		try {
			List<Diagnosis> diagnoses = engine.calculateDiagnoses();
			log.info("Found " + diagnoses.size() + " diagnosis: " + Utilities.printSortedDiagnoses(diagnoses, ' '));
			log.info("Needed " + engine.getNumberOfDiagnosisRuns() + " diagnosis runs and " + engine.getNumberOfQueries() + " user interactions.");
			log.info(String.format("Diagnoses calculation took %.2f ms.", engine.getDiagnosisTime()).toString());
			log.info(String.format("User interaction calculation took %.2f ms.", engine.getUserInteractionTime()).toString());
		} catch (DiagnosisException e) {
			e.printStackTrace();
		}

		log.info("Finished interactivity test");
	}

	public static void main(String[] args) throws IOException {
		LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));

		InteractivityTest test = new InteractivityTest();
		test.runInteractivityTest();
	}
}
