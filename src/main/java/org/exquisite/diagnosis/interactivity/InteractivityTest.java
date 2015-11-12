package org.exquisite.diagnosis.interactivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.exquisite.data.ConstraintsFactory;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.interactivity.partitioning.CKK;
import org.exquisite.diagnosis.interactivity.partitioning.Partitioning;
import org.exquisite.diagnosis.interactivity.partitioning.scoring.QSSFactory;
import org.exquisite.diagnosis.interactivity.partitioning.scoring.Scoring;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.tools.Utilities;

import choco.kernel.model.constraints.Constraint;

/**
 * Test class to test the user interactivity tests.diagnosis engine with spreadsheets
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

		int diagnosesPerQuery = 9;

		Scoring scoring = QSSFactory.createMinScoreQSS();

		Partitioning partitioning = new CKK(innerEngine, scoring);

		Diagnosis correctDiagnosis = getCorrectDiagnosis(innerEngine, fullCorrectFilename);

		InteractivityDiagnosisEngine engine = new InteractivityDiagnosisEngine(innerEngine.getSessionData(), innerEngine, diagnosesPerQuery,
				partitioning, correctDiagnosis);

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

	/**
	 * Returns the correct tests.diagnosis based on the differences between the mutated and the correct spreadsheet xmls. Diagnosis might not be minimal.
	 * 
	 * @param engine
	 * @param correctFilename
	 * @return
	 */
	private Diagnosis getCorrectDiagnosis(IDiagnosisEngine engine, String correctFilename) {
		ExquisiteAppXML mutatedXML = engine.getSessionData().appXML;
		ExquisiteAppXML correctXML = ExquisiteAppXML.parseToAppXML(correctFilename);

		Diagnosis diag = new Diagnosis(new ArrayList<Constraint>(), engine.getModel());

		for (Constraint c : engine.getModel().getPossiblyFaultyStatements()) {
			String cell = engine.getModel().getConstraintName(c);
			if (!mutatedXML.getFormulas().get(cell).equals(correctXML.getFormulas().get(cell))) {
				diag.getElements().add(c);
			}
		}

		return diag;
	}

	public static void main(String[] args) throws IOException {
		LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));

		InteractivityTest test = new InteractivityTest();
		test.runInteractivityTest();
	}
}
