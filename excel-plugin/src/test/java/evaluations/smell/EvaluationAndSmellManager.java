package evaluations.smell;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.data.ConstraintsFactory;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.ranking.smell.Config;
import org.exquisite.diagnosis.ranking.smell.SmellIdentification;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * A class to run individual tests in different configurations
 * 
 * @author Philipp-Malte Lingnau
 * 
 */
public class EvaluationAndSmellManager {

	// Some constants and global settings
	// ----------------------------------------------------
	static int PARALLEL_THREADS = 0;
	static int NB_ITERATIONS = 1;
	static int SEARCH_DEPTH = -1;

	static boolean useFullParallelMode = false;
	@SuppressWarnings("unused")
	private int counter;

	/**
	 * Main entry point
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting run.");

		// Run diagnosis scenario and order candidates

		// runDiagnosisScenario(Config.getStringValue(Config.XMLFilesPath),
		// "salesforecast_TC_2Faults.xml",
		// parallelMode.off, pruningMode.off, NB_ITERATIONS);

		// Run evaluation and writes it to a file

		try {
			new EvaluationAndSmellManager().runEvaluation();
		} catch (XPathExpressionException | SAXException | IOException
				| ParserConfigurationException e) {
			e.printStackTrace();
		}

		System.out.println("Run finished.");
	}

	/**
	 * Calculates the number of formulas from xml, used for quality check
	 *
	 * @param currentFile
	 * @return Returns the number of formulas from xml as integer
	 * @throws FileNotFoundException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static int countd2p1Occurences(String currentFile)
			throws SAXException, IOException,
			ParserConfigurationException, XPathExpressionException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		// Get the currentFile as a document
		Document doc = db.parse(new FileInputStream(new File(currentFile)));
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		// Gets the number of formulas from xml using xPath
		Double result = (Double) xpath.evaluate(
				Config.getStringValue(Config.XPathFormulaCountExpression), doc,
				XPathConstants.NUMBER);

		return result.intValue();
	}

	/**
	 * Calculates smells and uses them to order diagnosis
	 * @param inputFileDirectory
	 * @param inputFilename
	 * @param parallelmode
	 * @param pruningmode
	 * @param iterations
	 */
	public static void runDiagnosisScenario(String inputFileDirectory,
											String inputFilename, parallelMode parallelmode,
											pruningMode pruningmode, int iterations) {

		String fullInputFilename = inputFileDirectory + inputFilename;
		EngineType engineType = EngineType.HSDagStandardQX;
		if (parallelmode == parallelMode.full) {
			if (EvaluationAndSmellManager.useFullParallelMode) {
				engineType = EngineType.FullParaHSDagStandardQX;
			} else if (parallelmode == parallelMode.level) {
				engineType = EngineType.ParaHSDagStandardQX;

			}
		}
		// Set the pruning mode
		ConstraintsFactory.PRUNE_IRRELEVANT_CELLS = pruningmode != pruningMode.off;
		for (int i = 0; i < iterations; i++) {
			// Create the engine
			AbstractHSDagEngine diagnosisEngine = (AbstractHSDagEngine) EngineFactory
					.makeEngineFromXMLFile(engineType, fullInputFilename,
							EvaluationAndSmellManager.PARALLEL_THREADS);

			// Set the search depth
			diagnosisEngine
					.setSearchDepth(EvaluationAndSmellManager.SEARCH_DEPTH);
			diagnosisEngine.getDiagnosisModel().getConfiguration().searchDepth = EvaluationAndSmellManager.SEARCH_DEPTH;

			// Make a call to the diagnosis engine.
			List<Diagnosis<Constraint>> diagnoses = null;

			try {
				diagnoses = diagnosisEngine.calculateDiagnoses();
			} catch (Exception e) {
				System.err.println("Error when calculating diagnosis for "
						+ fullInputFilename + " : " + e.getMessage());
				e.printStackTrace();
				return;
			}

			try {
				System.out.println(SmellIdentification
						.getSmells(fullInputFilename));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Gets all smells and puts them in order for current file
			try {
				Collections.sort(
						diagnoses,
						new org.exquisite.diagnosis.ranking.smell.SmellComparator(
								SmellIdentification
										.getSmells(fullInputFilename)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("New order: " + diagnoses);
		}
	}

	/**
	 * Runs a complexity check for each xml and determines if the spreadsheet
	 * gets tested for possible smells
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 * @throws XPathExpressionException
	 */
	public void runEvaluation() throws
			XPathExpressionException, SAXException, IOException,
			ParserConfigurationException {

		File f = new File(Config.getStringValue(Config.XMLFilesPath));
		File[] fileArray = f.listFiles();
		int notProcessedXMLCounter = 0;
		int processedXMLCounter = 0;
		int nonFormulasXMLCounter = 0;

		// Writes the headlines for the evaluation files
		EvaluationTools.writeEvaluationFileHeadline(Config
				.getStringValue(Config.FirstEvaluationFilePath));
		EvaluationTools.writeEvaluationFileHeadline(Config
				.getStringValue(Config.SecondEvaluationFilePath));

		for (int i = 0; i < fileArray.length; i++) {
			String currentFile = fileArray[i].getName();
			if (currentFile.endsWith(".xml")) {
				// Gets the total number of formulas in current file
				int numberOfFormulasInFile = countd2p1Occurences(Config
						.getStringValue(Config.XMLFilesPath) + currentFile);

				// Check if the document is complex enough to check
				if (numberOfFormulasInFile > Config
						.getIntValue(Config.FormulaQualityEdge)) {
					processedXMLCounter++;
					runScenario(fileArray[i].toString());
				} else if (numberOfFormulasInFile == 0) {
					nonFormulasXMLCounter++;
				} else {
					notProcessedXMLCounter++;
				}
			}
		}

		// Number of XMLs in path
		int numberOfXmlsInRun = processedXMLCounter + notProcessedXMLCounter
				+ nonFormulasXMLCounter;

		// Show statistic for current run
		System.out.println(EvaluationTools.numberOfXMLsInPathOutput + " "
				+ numberOfXmlsInRun + EvaluationTools.newLine
				+ EvaluationTools.processedStatisticOutput + " "
				+ processedXMLCounter + EvaluationTools.newLine
				+ EvaluationTools.notEnoughFormulasStatisticOutput + " "
				+ notProcessedXMLCounter + EvaluationTools.newLine
				+ EvaluationTools.nonFormulasStatisticOutput + " "
				+ nonFormulasXMLCounter);

		// Writes the evaluation statistic at the end of the file
		EvaluationTools.writeEvaluationStatistic(
				Config.getStringValue(Config.FirstEvaluationFilePath),
				processedXMLCounter, notProcessedXMLCounter,
				nonFormulasXMLCounter, numberOfXmlsInRun);

	}

	/**
	 * Runs a scenario and writes the output
	 * 
	 * @param inputFile
	 */
	public void runScenario(String fullInputFilename) {

		// Calculates the evaluation one and two for current file and adds the
		// result to the specified .csv
		try {

			EvaluationTools.calculateEvaluation1(fullInputFilename,
					Config.getStringValue(Config.FirstEvaluationFilePath));
			EvaluationTools.calculateEvaluation2(fullInputFilename,
					Config.getStringValue(Config.SecondEvaluationFilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Running modes
	enum pruningMode {
		on, off
	}

	enum parallelMode {
		full, level, off
	}

}
