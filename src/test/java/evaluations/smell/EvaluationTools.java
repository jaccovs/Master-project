package evaluations.smell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.diagnosis.ranking.smell.NeighborComparisonState;
import org.exquisite.diagnosis.ranking.smell.SmellIdentification;

public class EvaluationTools {

	public static final String lengthEvaluationInsertWithSeparator = "Laenge|";
	public static final String referenceCountEvaluationInsertWithSeparator = "Anzahl von einzelnen Referenzen|";
	public static final String multipleReferenceEvaluationInsertWithSeparator = "Mehrfache Referenzen|";
	public static final String functionsEvaluationInsertWithSeparator = "Funktionen|";
	public static final String emptyCellsEvaluationInsertWithSeparator = "Leere Referenzen|";
	public static final String ifStatementsEvaluationInsertWithSeparator = "If-Statements|";
	public static final String cellsAroundCellEvaluationInsertWithSeparator = "Umliegende Zellen|";
	public static final String separator = "|";
	public static final String newLine = "\n";
	public static final String evaluationFileHeadline = "Zelle|Formel|Laenge|Anzahl Referenzen|Mult. Referenzen|Funktionen|If-Statements|Leere Zellen|Umliegende Zellen";
	public static final String processedStatisticOutput = "Verarbeitete XMLs:";
	public static final String notEnoughFormulasStatisticOutput = "XMLs mit zu wenigen Formeln:";
	public static final String nonFormulasStatisticOutput = "XML ohne Formeln:";
	public static final String numberOfXMLsInPathOutput = "XMLs in Verzeichnis:";

	public static final int lengthEvaluation2Limit = 80;
	public static final int referenceCountEvaluation2Limit = 20;
	public static final int multipleReferencesEvaluation2Limit = 20;
	public static final int functionCountEvaluation2Limit = 5;
	public static final int emptyCellsEvaluation2Limit = 5;
	public static final int ifStatementEvaluation2Limit = 5;
	public static final int cellsAroundCellEvaluation2Limit = 3;

	/**
	 * Calculates an evaluation and writes it to a .csv
	 * (./smells/Evaluation/evaluation_1.csv). The evaluation includes all
	 * possible smells and the occurring number of each.
	 * 
	 * @param fullInputFilename
	 * @throws FileNotFoundException
	 */
	public static void calculateEvaluation1(String fullInputFilename,
			String evaluationFilePath) throws FileNotFoundException {

		Dictionary<String, String> formulas = ExquisiteAppXML.parseToAppXML(
				fullInputFilename).getFormulas();

		// Get each smell for each xml separated into a map
		Map<String, Integer> tmpLength = SmellIdentification
				.getFormulasLength(fullInputFilename);
		Map<String, Integer> tmpMultRefs = SmellIdentification
				.getMultipleReferences(fullInputFilename);
		Map<String, Integer> tmpMultOper = SmellIdentification
				.getMulitpleOperations(fullInputFilename);
		Map<String, Integer> tmpEmpty = SmellIdentification
				.getEmptyCell(fullInputFilename);
		Map<String, ArrayList<NeighborComparisonState>> tmpCellAround2 = SmellIdentification
				.getCellsAroundCell(fullInputFilename);
		Map<String, Integer> tmpCellAround = SmellIdentification
				.calcCellsAroundCell(tmpCellAround2);
		Map<String, Integer> tmpIfSum = SmellIdentification
				.getNumberOfIfStatements(fullInputFilename);
		Map<String, Integer> tmpRefCount = SmellIdentification
				.getNumberOfCellReferences(fullInputFilename);

		try {

			// Write evaluation file as csv
			File evaluationFile = new File(evaluationFilePath);
			FileWriter evaluationFileWriter = new FileWriter(evaluationFile,
					true);

			// Specification which file gets written
			evaluationFileWriter
					.append(writeCurrentFileNameForEvaluation(fullInputFilename));
			evaluationFileWriter.append(newLine);
			for (String cell : tmpLength.keySet()) {
				// Write all smells to file
				evaluationFileWriter
						.append(writeCompleteLineWithAllSmellsForEvaluation(
								formulas, tmpLength, tmpMultRefs, tmpMultOper,
								tmpEmpty, tmpCellAround, tmpIfSum, tmpRefCount,
								cell));
				evaluationFileWriter.append(newLine);
			}
			evaluationFileWriter.flush();
			evaluationFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Generates a string with all results for the implemented smells for a
	 * cell. Also containing a separator. Is used for writing a evaluation file
	 * 
	 * @param formulas
	 * @param tmpLength
	 * @param tmpMultRefs
	 * @param tmpMultOper
	 * @param tmpEmpty
	 * @param tmpCellAround
	 * @param tmpIfSum
	 * @param tmpRefCount
	 * @param cell
	 * @return String with all results for the implemented smells with a
	 *         separator ("|") between them
	 */
	private static String writeCompleteLineWithAllSmellsForEvaluation(
			Dictionary<String, String> formulas,
			Map<String, Integer> tmpLength, Map<String, Integer> tmpMultRefs,
			Map<String, Integer> tmpMultOper, Map<String, Integer> tmpEmpty,
			Map<String, Integer> tmpCellAround, Map<String, Integer> tmpIfSum,
			Map<String, Integer> tmpRefCount, String cell) {
		return cell + separator + formulas.get(cell) + separator
				+ tmpLength.get(cell) + separator + tmpRefCount.get(cell)
				+ separator + tmpMultRefs.get(cell) + separator
				+ tmpMultOper.get(cell) + separator + tmpIfSum.get(cell)
				+ separator + tmpEmpty.get(cell) + separator
				+ tmpCellAround.get(cell);
	}

	/**
	 * Generates a string with current file with leading "Datei: " - is used for
	 * writing the currentfile in evaluation
	 * 
	 * @param fullInputFilename
	 * @return String with "Datei: " + filePath and name
	 */

	private static String writeCurrentFileNameForEvaluation(
			String fullInputFilename) {
		return "Datei: " + fullInputFilename;
	}

	/**
	 * Calculates an evaluation and writes it to a .csv
	 * (./smells/Evaluation/evaluation_1.csv). The evaluation includes all
	 * extreme smells from xml file. The "extreme" limits can be configured in
	 * the static variables
	 * 
	 * @param fullInputFilename
	 * @throws FileNotFoundException
	 */
	public static void calculateEvaluation2(String fullInputFilename,
			String evaluationFilePath) throws FileNotFoundException {
		ExquisiteAppXML appXML = ExquisiteAppXML
				.parseToAppXML(fullInputFilename);
		Dictionary<String, String> formulas = appXML.getFormulas();

		// Get each smell for each xml separated into a map
		Map<String, Integer> tmpLength = SmellIdentification
				.getFormulasLength(fullInputFilename);
		Map<String, Integer> tmpMultRefs = SmellIdentification
				.getMultipleReferences(fullInputFilename);
		Map<String, Integer> tmpFunctions = SmellIdentification
				.getMulitpleOperations(fullInputFilename);
		Map<String, Integer> tmpEmpty = SmellIdentification
				.getEmptyCell(fullInputFilename);
		Map<String, ArrayList<NeighborComparisonState>> tmpCellAround2 = SmellIdentification
				.getCellsAroundCell(fullInputFilename);
		Map<String, Integer> tmpCellAround = SmellIdentification
				.calcCellsAroundCell(tmpCellAround2);
		Map<String, Integer> tmpIfSum = SmellIdentification
				.getNumberOfIfStatements(fullInputFilename);
		Map<String, Integer> tmpRefCount = SmellIdentification
				.getNumberOfCellReferences(fullInputFilename);

		try {

			// Write evaluation file as csv
			File evaluationFile = new File(evaluationFilePath);
			FileWriter evaluationFileWriter = new FileWriter(evaluationFile,
					true);

			// Gets the extreme smell cases of the xml
			for (String cell : tmpLength.keySet()) {
				if (tmpLength.get(cell) > lengthEvaluation2Limit) {
					evaluationFileWriter
							.append(writeSeparatedLineWithSmellForEvaluation(
									fullInputFilename, formulas, tmpLength,
									cell, lengthEvaluationInsertWithSeparator));
				}
				if (tmpRefCount.get(cell) > referenceCountEvaluation2Limit) {
					evaluationFileWriter
							.append(writeSeparatedLineWithSmellForEvaluation(
									fullInputFilename, formulas, tmpRefCount,
									cell,
									referenceCountEvaluationInsertWithSeparator));
				}
				if (tmpMultRefs.get(cell) > multipleReferencesEvaluation2Limit) {
					evaluationFileWriter
							.append(writeSeparatedLineWithSmellForEvaluation(
									fullInputFilename, formulas, tmpMultRefs,
									cell,
									multipleReferenceEvaluationInsertWithSeparator));
				}
				if (tmpFunctions.get(cell) > functionCountEvaluation2Limit) {
					evaluationFileWriter
							.append(writeSeparatedLineWithSmellForEvaluation(
									fullInputFilename, formulas, tmpFunctions,
									cell,
									functionsEvaluationInsertWithSeparator));
				}
				if (tmpEmpty.get(cell) > emptyCellsEvaluation2Limit) {
					evaluationFileWriter
							.append(writeSeparatedLineWithSmellForEvaluation(
									fullInputFilename, formulas, tmpEmpty,
									cell,
									emptyCellsEvaluationInsertWithSeparator));
				}
				if (tmpIfSum.get(cell) > ifStatementEvaluation2Limit) {
					evaluationFileWriter
							.append(writeSeparatedLineWithSmellForEvaluation(
									fullInputFilename, formulas, tmpIfSum,
									cell,
									ifStatementsEvaluationInsertWithSeparator));
				}
				if (tmpCellAround.get(cell) > cellsAroundCellEvaluation2Limit) {
					evaluationFileWriter
							.append(writeSeparatedLineWithSmellForEvaluation(
									fullInputFilename, formulas, tmpCellAround,
									cell,
									cellsAroundCellEvaluationInsertWithSeparator));

				}
			}
			evaluationFileWriter.flush();
			evaluationFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a complete line as string with given map of smells, cell,
	 * formula and filename - is used for evaluation with extreme cases
	 * 
	 * @param fillInputFilename
	 * @param formulas
	 * @param tmpSmellMap
	 * @param cell
	 * @param smellseparator
	 * @return String containing name of smell, cell, formula, smell value and
	 *         filename
	 */
	private static String writeSeparatedLineWithSmellForEvaluation(
			String fillInputFilename, Dictionary<String, String> formulas,
			Map<String, Integer> tmpSmellMap, String cell, String smellseparator) {
		return smellseparator + cell + separator + formulas.get(cell)
				+ separator + tmpSmellMap.get(cell) + separator
				+ fillInputFilename + newLine;

	}

	/**
	 * Writes the headlines for the given evaluation file
	 * 
	 */
	public static void writeEvaluationFileHeadline(String evaluationFilePath) {
		File evaluationFile = new File(evaluationFilePath);
		FileWriter evaluationFileWriter;

		try {
			evaluationFileWriter = new FileWriter(evaluationFile, true);

			// Write evaluation headline
			evaluationFileWriter.append(evaluationFileHeadline + newLine);

			evaluationFileWriter.flush();
			evaluationFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Writes the statistic for the processed run of xml files for the given
	 * evaluation file (path)
	 * 
	 * @param string
	 */
	public static void writeEvaluationStatistic(String evaluationFilePath,
			int processedXMLCounter, int notProcessedXMLCounter,
			int nonFormulasXMLCounter, int numberOfXmlsInRun) {
		File evaluationFile = new File(evaluationFilePath);
		FileWriter evaluationFilewriter;
		try {
			evaluationFilewriter = new FileWriter(evaluationFile, true);
			// Write evaluation statistic
			evaluationFilewriter.append(writeStatisticForEvaluation(
					processedXMLCounter, notProcessedXMLCounter,
					nonFormulasXMLCounter, numberOfXmlsInRun));

			evaluationFilewriter.flush();
			evaluationFilewriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Generates a String with the statistic of the current run - is used to
	 * write at the end of a evaluation .csv
	 * 
	 * @param processedXMLCounter
	 * @param notProcessedXMLCounter
	 * @param nonFormulasXMLCounter
	 * @return String with the statistic of the current run
	 */
	private static String writeStatisticForEvaluation(int processedXMLCounter,
			int notProcessedXMLCounter, int nonFormulasXMLCounter,
			int numberOfXmlsInRun) {

		return newLine + numberOfXMLsInPathOutput + separator
				+ numberOfXmlsInRun + newLine + processedStatisticOutput
				+ separator + processedXMLCounter + newLine
				+ notEnoughFormulasStatisticOutput + separator
				+ notProcessedXMLCounter + newLine + nonFormulasStatisticOutput
				+ separator + nonFormulasXMLCounter;
	}
}
