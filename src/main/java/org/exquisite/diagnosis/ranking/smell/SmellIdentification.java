package org.exquisite.diagnosis.ranking.smell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.exquisite.datamodel.ExquisiteAppXML;

public class SmellIdentification {

	public static final String NONCELL = "-";
	public static final int lowestRow = 1;
	public static final int highestRow = 1048576;
	public static final String lowestColumn = "A";
	public static final String highestColumn = "XFD";
	public static final String worksheetSeparator = "WS_";
	public static final String secondWorksheetSeparator = "_";
	public static final String ifRegularExpression = "\\bIF\\b";
	public static final String regularCellNameSeparatorExpression = "[^A-Z0-9]+";
	public static final String regularMultipleReferencesSeparatedExpression = "[A-Z]{1,3}[0-9]{1,7}:[A-Z]{1,3}[0-9]{1,7}";
	public static final String regularCapitalExpression = "[A-Z]{1,3}";
	public static final String regularNumberExpression = "[0-9]{1,7}";

	/**
	 * Calculates the length of all cells for the given xml
	 * 
	 * @param xmlFilePath
	 *            Filepath to the excel xml file
	 * 
	 * @return Returns a map with (Cell name, length of the cell as integer)
	 * 
	 */
	public static Map<String, Integer> getFormulasLength(String xmlFilePath) {
		@SuppressWarnings("unchecked")
		Map<String, String> formulasMap = (Map<String, String>) ExquisiteAppXML
				.parseToAppXML(xmlFilePath).getFormulas();
		Map<String, Integer> result = new HashMap<>();

		for (String cell : formulasMap.keySet()) {
			// Get the length and put it in the map
			result.put(cell, formulasMap.get(cell).length());
		}

		// Returns <Cell name, length of cell>
		return result;
	}

	/**
	 * Calculates the number of operations for all cells from the given
	 * xmlFilePath
	 * 
	 * @param xmlFilePath
	 *            Filepath to the excel xml file
	 * 
	 * @return Map with (Cell name, Number of occurring formulas as integer)
	 * 
	 */
	public static Map<String, Integer> getMulitpleOperations(String xmlFilePath) {
		Dictionary<String, String> formulas = ExquisiteAppXML.parseToAppXML(
				xmlFilePath).getFormulas();

		@SuppressWarnings("unchecked")
		Map<String, String> formulasMap = (Map<String, String>) formulas;
		Map<String, Integer> result = new HashMap<>();

		int operationCounter = 0;
		String tmpFormulaString;

		for (String cell : formulasMap.keySet()) {

			tmpFormulaString = formulas.get(cell).toString();

			for (int index = 0; tmpFormulaString.length() > index; index++) {

				// Tests if there is a operation in the cell by opened and
				// closed parenthesis with foregoing capital
				if (isOpertaion(tmpFormulaString, index)) {
					operationCounter++;
				}
			}
			// Counter needs to be halved because counter is raised for each
			// parenthesis
			result.put(cell, (operationCounter / 2));
			operationCounter = 0;
		}
		// Returns <Cell name, number of formulas in cell>
		return result;
	}

	/**
	 * Checks if the given string is a operation or not. Considers the string
	 * around j to determine if there's a operation.
	 * 
	 * @param tmpFormulaString
	 * @param index
	 * @return True if the given string at j is an operation, false if not.
	 */
	private static boolean isOpertaion(String tmpFormulaString, int index) {
		return isStartingFormula(tmpFormulaString, index)
				|| tmpFormulaString.charAt(index) == ')';
	}

	/**
	 * Calculates the number of if statements for all cells from the given
	 * xmlFilePath
	 * 
	 * @param xmlFilePath
	 *            Filepath to the excel xml file
	 * 
	 * @return Map with (Cell name, number of occurring if statements)
	 * 
	 */
	public static Map<String, Integer> getNumberOfIfStatements(
			String xmlFilePath) {
		Dictionary<String, String> formulas = ExquisiteAppXML.parseToAppXML(
				xmlFilePath).getFormulas();

		@SuppressWarnings("unchecked")
		Map<String, String> formulasMap = (Map<String, String>) formulas;
		Map<String, Integer> result = new HashMap<>();

		int ifStatementCounter = 0;
		Pattern p = Pattern.compile(ifRegularExpression);

		for (String cell : formulasMap.keySet()) {
			Matcher m = p.matcher(formulasMap.get(cell).toString());
			while (m.find()) {
				ifStatementCounter++;
			}

			result.put(cell, ifStatementCounter);
			ifStatementCounter = 0;
		}
		// Returns <Cell name, number of if statements in cell>
		return result;
	}

	/**
	 * Separates all cells of a formula into a new map for all formulas in the
	 * given xml
	 * 
	 * @param xmlFilePath
	 *            Filepath to the excel xml file
	 * 
	 * @return Map with (Cell name, [cells occurring in formula of cell
	 *         (separated)]) Example: E10 = E8 + E9 - (E10,[E8,E9])
	 * 
	 */

	public static Map<String, ArrayList<String>> separateCellNamesFromFormula(
			String xmlFilePath) {

		Dictionary<String, String> formulas = ExquisiteAppXML.parseToAppXML(
				xmlFilePath).getFormulas();
		@SuppressWarnings("unchecked")
		Map<String, String> formulasMap = (Map<String, String>) formulas;
		Map<String, ArrayList<String>> result = new HashMap<>();

		// Transfer all content of formula into list
		for (String cell : formulasMap.keySet()) {

			ArrayList<String> tmpListOfCellNamesInFormula = new ArrayList<>();

			// Separate cell names out of string
			for (String cell2 : formulasMap.get(cell).split(
					regularCellNameSeparatorExpression)) {
				if (isCell(cell2)) {
					String currentWorksheetkNumber = cell
							.split(secondWorksheetSeparator)[1];
					// Add cell names to map and add the right worksheet number
					tmpListOfCellNamesInFormula.add(worksheetSeparator
							+ currentWorksheetkNumber
							+ secondWorksheetSeparator + cell2);

				}
			}
			// Formula: "E10 = E9+E10" - content of the map: <E10,[E9,E10]>
			result.put(cell, tmpListOfCellNamesInFormula);
		}

		// Returns <Cell name, [referencing cells]>
		return result;
	}

	/**
	 * Determines if a given String is a cell
	 * 
	 * @param currentPossibleCell
	 *            String with a possibly cell
	 * 
	 * @return Returns true or false, depending if it's a cell or not.
	 * 
	 */
	private static boolean isCell(String currentPossibleCell) {
		boolean hasLetter = false;
		boolean hasNumber = false;

		for (int i = 0; i < currentPossibleCell.length()
				&& (!hasLetter || !hasNumber); i++) {
			if (isCapitalLetter(currentPossibleCell, i)) {
				// Is capital letter
				hasLetter = true;
			} else if (currentPossibleCell.charAt(i) >= '0'
					&& currentPossibleCell.charAt(i) <= '9') {
				// Is number
				hasNumber = true;
			}
		}
		return hasLetter && hasNumber;
	}

	/**
	 * Checks if the given string at position i is a capital letter or not.
	 * 
	 * @param current
	 * @param i
	 * @return True if the given string at i is a capital letter, false if not.
	 */
	private static boolean isCapitalLetter(String current, int i) {
		return current.charAt(i) >= 65 && current.charAt(i) <= 90;
	}

	/**
	 * 
	 * Calculates all values of all formulas from the given xml
	 * 
	 * @param xmlFilePath
	 *            Filepath to the excel xml file
	 * 
	 * @return Map with (Cell name, cell value)
	 * 
	 */
	public static Map<String, String> getAllCellValues(String xmlFilePath) {
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(xmlFilePath);
		Map<String, String> result = new HashMap<>();
		Enumeration<String> testCases = appXML.getTestCases().keys();

		// First dictionary (tier 1) - includes number of implemented test cases
		// "currentTestCase" includes all existing test cases
		// This function is always using the first test case from dictionary
		while (testCases.hasMoreElements()) {
			String currentTestCase = testCases.nextElement();

			Dictionary<String, String> valuesOfCurrentTestcase = appXML
					.getTestCases().get(currentTestCase).getValues();

			// (tier 2) - includes all cells with existing
			// test cases
			Enumeration<String> testCells = valuesOfCurrentTestcase.keys();
			while (testCells.hasMoreElements()) {

				String cell = testCells.nextElement();

				// Add cell to map - Map<cell name, value of cell>
				result.put(cell, valuesOfCurrentTestcase.get(cell));
			}
		}

		// Returns <cell name, content as number or formula>
		// content depends on the given values in appXml
		return result;
	}

	/**
	 * Determines all references to empty cells for all cells of the given xml
	 * 
	 * @param xmlFilePath
	 *            Filepath to the excel xml file
	 * @return Map with (Cell name, number of empty cells])
	 * @throws FileNotFoundException
	 */
	public static Map<String, Integer> getEmptyCell(String xmlFilePath)
			throws FileNotFoundException {
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(xmlFilePath);
		// Get map of separated formulas
		Map<String, ArrayList<String>> separatedCells = getAllSeparatedCells(xmlFilePath);

		Map<String, Integer> result = new HashMap<>();
		int emptyCellsCounter = 0;

		// For all cells
		for (String cell : separatedCells.keySet()) {
			// For all referencing cells
			for (String referencingCell : separatedCells.get(cell)) {

				boolean isTestcase = isTestcase(referencingCell, appXML);
				boolean isFormula = isFormula(referencingCell, appXML);

				if (isTestcase || isFormula) {
					// In this case the cell is not empty
				} else {
					emptyCellsCounter++;
				}
			}
			result.put(cell, emptyCellsCounter);
			emptyCellsCounter = 0;
		}
		return result;
	}

	/**
	 * Gets all possible separated cells of a formula. Does also generates
	 * separated cells for range references
	 * 
	 * @param xmlFilePath
	 * @return Map with all separated cells of a formula including all
	 *         references of ranges
	 * @throws FileNotFoundException
	 */
	private static Map<String, ArrayList<String>> getAllSeparatedCells(
			String xmlFilePath) throws FileNotFoundException {
		// Get separated cells
		Map<String, ArrayList<String>> separatedCells = separateCellNamesFromFormula(xmlFilePath);
		@SuppressWarnings("unchecked")
		Map<String, ArrayList<String>> separatedMultRefs = getMultRefs((Map<String, String>) ExquisiteAppXML
				.parseToAppXML(xmlFilePath).getFormulas());

		Map<String, ArrayList<String>> result = new HashMap<>();

		for (String cell : separatedCells.keySet()) {
			// Join lists for result
			ArrayList<String> list2 = separatedCells.get(cell);
			ArrayList<String> list3 = separatedMultRefs.get(cell);
			ArrayList<String> resultList = list2;
			resultList.addAll(list3);

			// Remove duplicated entries of list
			resultList = removeDublicatedEntriesOfList(resultList);

			// Put result
			result.put(cell, resultList);
		}

		return result;
	}

	/**
	 * Removes duplicates of a ArrayList
	 * 
	 * @param arrayList
	 * @return Given ArrayList without duplicated entries
	 */
	public static ArrayList<String> removeDublicatedEntriesOfList(
			ArrayList<String> arrayList) {
		HashSet<String> hashSet = new HashSet<>(arrayList);
		arrayList.clear();
		arrayList.addAll(hashSet);
		return arrayList;
	}

	/**
	 * Determines if a given cell contains a formula
	 * 
	 * @param cell
	 * @param appXML
	 * @return Returns true if it contains a formula, else if not
	 */
	@SuppressWarnings("unchecked")
	private static boolean isFormula(String cell, ExquisiteAppXML appXML) {
		Map<String, String> formulas = (Map<String, String>) appXML
				.getFormulas();
		boolean result = false;

		// Is the cell a formula?
		if (formulas.containsKey(cell)) {
			result = true;
		}
		return result;
	}

	/**
	 * Determines if a given cell is a testcase
	 * 
	 * @param cell
	 * @param appXML
	 * @return Returns true if the given cell is a testcase, else if not
	 */
	@SuppressWarnings("unchecked")
	private static boolean isTestcase(String cell, ExquisiteAppXML appXML) {
		Enumeration<String> testcases = appXML.getTestCases().keys();
		boolean result = false;
		while (testcases.hasMoreElements()) {
			// Get current testcase
			String currentTestcase = testcases.nextElement();
			// Get values of current testcase
			Map<String, String> valuesOfCurrentTestcase = (Map<String, String>) appXML
					.getTestCases().get(currentTestcase).getValues();
			// Does a testcase for cell exist?
			if (valuesOfCurrentTestcase.containsKey(cell)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Calculates the number of references in a cell for all cells from the
	 * given xmlFilePath
	 * 
	 * @param xmlFilePath
	 *            Filepath to the excel xml file
	 * @return Map with (Cell name, number of references)
	 * @throws FileNotFoundException
	 */
	public static Map<String, Integer> getNumberOfCellReferences(
			String xmlFilePath) throws FileNotFoundException {

		// Get map of used cells in a formula
		Map<String, ArrayList<String>> separatedCellNames = separateCellNamesFromFormula(xmlFilePath);
		Map<String, Integer> result = new HashMap<>();

		for (String cell : separatedCellNames.keySet()) {

			// Contains <Cell name, number of referencing cells>
			result.put(cell, separatedCellNames.get(cell).size());
		}

		return result;
	}

	/**
	 * Gets all cell names of ranges
	 * 
	 * @param formulasMap
	 * @return Map with all references of ranges
	 * @throws FileNotFoundException
	 */
	private static Map<String, ArrayList<String>> getMultRefs(
			Map<String, String> formulasMap) throws FileNotFoundException {
		Map<String, ArrayList<String>> separatedMultRefs = new HashMap<>();
		Map<String, ArrayList<String>> result = new HashMap<>();
		for (String cell : formulasMap.keySet()) {
			ArrayList<String> multRefResultMap = getAllMultipleReferencesSeparated(formulasMap
					.get(cell));

			separatedMultRefs.put(cell, multRefResultMap);
			String currentWorksheetkNumber = cell
					.split(secondWorksheetSeparator)[1];
			ArrayList<String> refOfRangeMap = generateRangeRefs(
					separatedMultRefs.get(cell), currentWorksheetkNumber);
			result.put(cell, refOfRangeMap);
		}
		return result;
	}

	/**
	 * Generates all cell names of range references
	 * 
	 * @param multRefsList
	 * @param currentWorksheetkNumber
	 * @return Generated ArrayList with all references of a range
	 * @throws FileNotFoundException
	 */
	private static ArrayList<String> generateRangeRefs(
			ArrayList<String> multRefsList, String currentWorksheetkNumber)
			throws FileNotFoundException {
		ArrayList<String> topologicallyList = generateTopologicalList();
		ArrayList<String> result = new ArrayList<>();

		for (int i = 0; i < multRefsList.size(); i++) {
			if (i % 2 == 0) {
				// Anfangszelle

				String[] numberOfStartRef = multRefsList.get(i).split(
						regularCapitalExpression);
				String[] numberOfEndRef = multRefsList.get(i + 1).split(
						regularCapitalExpression);

				String[] capitalLetterOfStartRef = multRefsList.get(i).split(
						regularNumberExpression);
				String[] capitalLetterOfEndRef = multRefsList.get(i + 1).split(
						regularNumberExpression);

				result.addAll(generateRange(
						topologicallyList.indexOf(capitalLetterOfStartRef[0]),
						topologicallyList.indexOf(capitalLetterOfEndRef[0]),
						Integer.parseInt(numberOfStartRef[1]),
						Integer.parseInt(numberOfEndRef[1]), topologicallyList,
						currentWorksheetkNumber));
			} else {
				// Nothing - This is the targeted cell
			}
		}
		return result;
	}

	/**
	 * Generates the entities of a range with the given indexes
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @param startNumber
	 * @param endNumber
	 * @param topologicallyList
	 * @param currentWorksheetkNumber
	 * @return List of all entities of a range for the given indexes
	 */

	private static Collection<? extends String> generateRange(int startIndex,
			int endIndex, int startNumber, int endNumber,
			ArrayList<String> topologicallyList, String currentWorksheetkNumber) {
		ArrayList<String> capitalLetters = new ArrayList<>();
		ArrayList<String> result = new ArrayList<>();

		if (startIndex < endIndex) {
			// Increment

			// Generate all capitals
			while (startIndex < endIndex) {
				capitalLetters.add(topologicallyList.get(startIndex));
				startIndex++;
			}
			capitalLetters.add(topologicallyList.get(endIndex));

			result = generateNumbersToCapitals(startNumber, endNumber,
					capitalLetters, currentWorksheetkNumber);

		} else if (endIndex < startIndex) {
			// Decrement

			// Generate all capitals
			while (endIndex < startIndex) {
				capitalLetters.add(topologicallyList.get(endIndex));
				endIndex++;
			}
			capitalLetters.add(topologicallyList.get(startIndex));

			result = generateNumbersToCapitals(startNumber, endNumber,
					capitalLetters, currentWorksheetkNumber);

		} else if (endIndex == startIndex) {
			// Generate capital
			capitalLetters.add(topologicallyList.get(startIndex));
			result = generateNumbersToCapitals(startNumber, endNumber,
					capitalLetters, currentWorksheetkNumber);
		}

		return result;
	}

	/**
	 * Generates all capital letters, needed in generateRange
	 * 
	 * @param startNumber
	 * @param endNumber
	 * @param capitalLetters
	 * @param currentWorksheetkNumber
	 * @return List of all needed capital letters
	 */
	private static ArrayList<String> generateNumbersToCapitals(int startNumber,
			int endNumber, ArrayList<String> capitalLetters,
			String currentWorksheetkNumber) {
		ArrayList<String> result = new ArrayList<>();

		for (String capital : capitalLetters) {
			int start = startNumber;
			int end = endNumber;
			if (startNumber < endNumber) {
				while (start <= end) {
					result.add(worksheetSeparator + currentWorksheetkNumber
							+ secondWorksheetSeparator + capital + start);
					start++;
				}
			} else if (endNumber < startNumber) {
				while (end <= start) {
					result.add(worksheetSeparator + currentWorksheetkNumber
							+ secondWorksheetSeparator + capital + end);
					end++;
				}
			} else if (startNumber == endNumber) {
				result.add(worksheetSeparator + currentWorksheetkNumber
						+ secondWorksheetSeparator + capital + start);
			}
		}
		return result;
	}

	/**
	 * Helps to get all multiple references, without single references
	 * 
	 * @param formula
	 * @return ArrayList of all multiple references in a formula
	 */
	private static ArrayList<String> getAllMultipleReferencesSeparated(
			String formula) {
		ArrayList<String> result = new ArrayList<>();
		Pattern myPattern = Pattern
				.compile(regularMultipleReferencesSeparatedExpression);
		Matcher m = myPattern.matcher(formula);
		// Fing all matches and split them
		while (m.find()) {
			String s = m.group();
			result.addAll(Arrays.asList(s.split(":")));
		}
		return result;
	}

	/**
	 * 
	 * Calculates the number of multiple references in a cell for all cells from
	 * the given xmlFilePath
	 * 
	 * Multiple references are range references ":" and single references ";"
	 * 
	 * @param xmlFilePath
	 *            Filepath to the excel xml file
	 * @return Map with (Cell name, number of multiple references)
	 */

	public static Map<String, Integer> getMultipleReferences(String xmlFilePath) {
		// Dictionary<String, String> formulas = ExquisiteAppXML.parseToAppXML(
		// xmlFilePath).getFormulas();

		@SuppressWarnings("unchecked")
		Map<String, String> formulasMap = (Map<String, String>) ExquisiteAppXML
				.parseToAppXML(xmlFilePath).getFormulas();
		Map<String, String> formulaReferencesList = new HashMap<>();
		Map<String, Integer> result = new HashMap<>();
		int i = 0;
		String tmpString;
		boolean insideFormula = false;

		for (String cell : formulasMap.keySet()) {
			// Insert all formulas to a new map for iteration
			formulaReferencesList.put(cell, formulasMap.get(cell));

			int referencesCounter = 0;
			while (formulaReferencesList.size() > i) {
				tmpString = formulasMap.get(cell).toString();
				for (int j = 0; tmpString.length() > j; j++) {
					// Counts existing number of ranges in formula - only in
					// formulas

					if (isStartingFormula(tmpString, j)
							|| insideFormula == true) {
						// Now we are inside a formula
						insideFormula = true;

						if (tmpString.charAt(j) == ':'
								|| tmpString.charAt(j) == ',') {
							referencesCounter++;
						}
					} else if (tmpString.charAt(j) == ')') {
						// Formula has ended
						insideFormula = false;
					}
				}
				tmpString = new String();
				i++;
			}
			result.put(cell, referencesCounter);
		}

		// Returns <Cell name, number of references>
		return result;
	}

	/**
	 * Determines if a formula is starting at the given index
	 * 
	 * @param tmpString
	 * @param j
	 * @return True if formula is starting, else false
	 */
	private static boolean isStartingFormula(String tmpString, int j) {
		return tmpString.charAt(j) == '(' && j >= 2
				&& tmpString.charAt(j - 1) > 64 && tmpString.charAt(j - 1) < 91;
	}

	/**
	 * 
	 * Calculates all adjacent cells for all cells from the given xmlFilePath
	 * 
	 * @param xmlFilePath
	 *            Filepath to the excel xml file
	 * @return Map with (Cell, [2 steps right, 1 step right, 2 step left, 1 step
	 *         left, 2 steps above, 1 step above, 2 steps below, 1 step below])
	 *         - Number: 3 = different from key cell but has the same value as
	 *         one other in array, 2 = is a R1C1 and also same as the key cell,
	 *         1 = is R1C1 but not same as the key cell, 0 = not a R1C1 cell
	 * @throws FileNotFoundException
	 */
	public static Map<String, ArrayList<NeighborComparisonState>> getCellsAroundCell(
			String xmlFilePath) throws FileNotFoundException {

		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(xmlFilePath);
		Map<String, ArrayList<NeighborComparisonState>> result = new HashMap<>();
		// Contains right and left cells from the current formula
		Map<String, ArrayList<String>> rightAndLeftCellsAroundR1C1FormulaMap = new HashMap<>();
		// Contains above and below cells from the current formula
		Map<String, ArrayList<String>> aboveAndBelowCellsAroundR1C1FormulaMap = new HashMap<>();
		// Above and below result map
		Map<String, ArrayList<NeighborComparisonState>> aboveAndBelowCellsResultMap = new HashMap<>();
		// Right and left result map
		Map<String, ArrayList<NeighborComparisonState>> rightAndLeftCellsResultMap = new HashMap<>();
		Map<String, String> r1c1KeyMap = new HashMap<>();
		Enumeration<String> testCases = appXML.getFormulas().keys();
		ArrayList<String> topologicalList = generateTopologicalList();

		// Create map with <Cell name, r1c1 formula>
		while (testCases.hasMoreElements()) {
			String cell = testCases.nextElement();
			r1c1KeyMap.put(cell, appXML.getFormulasR1C1().get(cell));
		}

		// Generates the cell names of cells around the formula cell
		generateCellNamesAroundCell(r1c1KeyMap, topologicalList,
				rightAndLeftCellsAroundR1C1FormulaMap,
				aboveAndBelowCellsAroundR1C1FormulaMap);

		// Calculates states of right and left cell list
		calculateRightAndLeftCellStates(rightAndLeftCellsAroundR1C1FormulaMap,
				rightAndLeftCellsResultMap, r1c1KeyMap);

		// Calculates states of above and below cell list
		calculateAboveAndBelowCellStates(
				aboveAndBelowCellsAroundR1C1FormulaMap,
				aboveAndBelowCellsResultMap, r1c1KeyMap);

		result.putAll(rightAndLeftCellsResultMap);

		mergeLists(aboveAndBelowCellsResultMap, result);

		return result;
	}

	/**
	 * Calculates states of above and below cell list
	 * 
	 * @param aboveAndBelowCellsAroundR1C1FormulaMap
	 * @param aboveAndBelowCellsResultMap
	 * @param r1c1KeyMap
	 */
	private static void calculateAboveAndBelowCellStates(
			Map<String, ArrayList<String>> aboveAndBelowCellsAroundR1C1FormulaMap,
			Map<String, ArrayList<NeighborComparisonState>> aboveAndBelowCellsResultMap,
			Map<String, String> r1c1KeyMap) {

		for (String cell : aboveAndBelowCellsAroundR1C1FormulaMap.keySet()) {
			ArrayList<NeighborComparisonState> tmpValueList = calculateSurroundingAboveBelowCellsAsList(
					cell, r1c1KeyMap, aboveAndBelowCellsAroundR1C1FormulaMap);
			aboveAndBelowCellsResultMap.put(cell, tmpValueList);
		}
	}

	/**
	 * Calculates states of right and left cell list
	 * 
	 * @param rightAndLeftCellsAroundR1C1FormulaMap
	 * @param rightAndLeftCellsResultMap
	 * @param r1c1KeyMap
	 */
	private static void calculateRightAndLeftCellStates(
			Map<String, ArrayList<String>> rightAndLeftCellsAroundR1C1FormulaMap,
			Map<String, ArrayList<NeighborComparisonState>> rightAndLeftCellsResultMap,
			Map<String, String> r1c1KeyMap) {
		for (String cell : rightAndLeftCellsAroundR1C1FormulaMap.keySet()) {
			ArrayList<NeighborComparisonState> tmpValueList = calculateSurroundingRightLeftCellsAsList(
					cell, r1c1KeyMap, rightAndLeftCellsAroundR1C1FormulaMap);
			rightAndLeftCellsResultMap.put(cell, tmpValueList);
		}
	}

	/**
	 * Generates topological list with elements A - XFD from the file
	 * "./smells/Cell name order/cellNameOrderList.txt"
	 * 
	 * @return Topological list as ArrayList<String>
	 * @throws FileNotFoundException
	 */
	public static ArrayList<String> generateTopologicalList()
			throws FileNotFoundException {
		Scanner s = new Scanner(new File(
				Config.getStringValue(Config.TopologicallyOrderListPath)));
		ArrayList<String> list = new ArrayList<String>();

		while (s.hasNext()) {
			// Contains topological list with elements A - XFD
			list.add(s.next());
		}
		s.close();
		return list;
	}

	/**
	 * Generates the cells, seperatet for right&left and above&below, which are
	 * 2 steps and 1 step around the key cell
	 * 
	 * @param tmpMap
	 * @param list
	 * @param rightAndLeftCellsAroundR1C1FormulaMap
	 * @param aboveAndBelowCellsAroundR1C1FormulaMap
	 */
	private static void generateCellNamesAroundCell(
			Map<String, String> tmpMap,
			ArrayList<String> list,
			Map<String, ArrayList<String>> rightAndLeftCellsAroundR1C1FormulaMap,
			Map<String, ArrayList<String>> aboveAndBelowCellsAroundR1C1FormulaMap) {

		for (String cell : tmpMap.keySet()) {
			ArrayList<String> rightLeftList = new ArrayList<String>();
			ArrayList<String> aboveBelowList = new ArrayList<>();

			// Get Spreadsheet number - example: WS_1_I34 -> 1
			String currentWorksheetkNumber = cell
					.split(secondWorksheetSeparator)[1];

			// Separate keys / cells - example: WS_1_I34 -> I34
			cell = cell.split(secondWorksheetSeparator)[2];

			// Get right Cells
			getRightCells(list, cell, rightLeftList);

			// Get left cells
			getLeftCells(list, cell, rightLeftList);

			// Get cells above
			getCellsAbove(cell, aboveBelowList);

			// Get cells below
			getCellsBelow(cell, aboveBelowList);

			listCellHelper(rightLeftList, aboveBelowList,
					currentWorksheetkNumber);

			cell = worksheetSeparator + currentWorksheetkNumber
					+ secondWorksheetSeparator + cell;

			// Puts right and left cells into a separate map as the cell names
			// above and below the cell
			rightAndLeftCellsAroundR1C1FormulaMap.put(cell, rightLeftList);
			aboveAndBelowCellsAroundR1C1FormulaMap.put(cell, aboveBelowList);
		}

	}

	/**
	 * Puts splitted cells like A34 back to the format with right spreadsheet
	 * number like WS_1_A34
	 * 
	 * @param rightLeftList
	 * @param aboveBelowList
	 * @param SpreadsheetNumber
	 */
	private static void listCellHelper(ArrayList<String> rightLeftList,
			ArrayList<String> aboveBelowList, String SpreadsheetNumber) {
		for (String cell : rightLeftList) {
			String tmpString = worksheetSeparator + SpreadsheetNumber
					+ secondWorksheetSeparator
					+ rightLeftList.get(rightLeftList.indexOf(cell));
			rightLeftList.set(rightLeftList.indexOf(cell), tmpString);
		}

		for (String cell : aboveBelowList) {
			String tmpString = worksheetSeparator + SpreadsheetNumber
					+ secondWorksheetSeparator
					+ aboveBelowList.get(aboveBelowList.indexOf(cell));
			aboveBelowList.set(aboveBelowList.indexOf(cell), tmpString);
		}
	}

	/**
	 * Get the cells below the given cell
	 * 
	 * @param cell
	 * @param aboveBelowList
	 */
	private static void getCellsBelow(String cell,
			ArrayList<String> aboveBelowList) {

		aboveBelowList.add(getCellBelow(cell));

		if (aboveBelowList.get(2).equals(NONCELL)) {
			aboveBelowList.add(NONCELL);
		} else {
			aboveBelowList.add(getCellBelow(aboveBelowList.get(2)));
		}
	}

	/**
	 * Get the cells above the given cell
	 * 
	 * @param cell
	 * @param aboveBelowList
	 */
	private static void getCellsAbove(String cell,
			ArrayList<String> aboveBelowList) {

		aboveBelowList.add(getCellAbove(cell));

		if (aboveBelowList.get(0).equals(NONCELL)) {
			aboveBelowList.add(NONCELL);
		} else {
			aboveBelowList.add(getCellAbove(aboveBelowList.get(0)));
		}
	}

	/**
	 * Gets the cell left from the given cell
	 * 
	 * @param list
	 * @param cell
	 * @param rightLeftList
	 */
	private static void getLeftCells(ArrayList<String> list, String cell,
			ArrayList<String> rightLeftList) {
		rightLeftList.add(getCellLeft(cell, list));
		if (rightLeftList.get(2).equals(NONCELL)) {
			rightLeftList.add(NONCELL);
		} else {
			rightLeftList.add(getCellLeft(rightLeftList.get(2), list));
		}
	}

	/**
	 * Gets the cell right from the given cell
	 * 
	 * @param list
	 * @param cell
	 * @param rightLeftList
	 */
	private static void getRightCells(ArrayList<String> list, String cell,
			ArrayList<String> rightLeftList) {
		rightLeftList.add(getCellRight(cell, list));

		if (rightLeftList.get(0).equals(NONCELL)) {
			rightLeftList.add(NONCELL);
		} else {
			rightLeftList.add(getCellRight(rightLeftList.get(0), list));
		}
	}

	/**
	 * Merges two lists into a new final one
	 * 
	 * @param aboveAndBelowCellsResultMap
	 *            First map for matching
	 * @param resultMap
	 *            Second map for matching
	 */
	private static void mergeLists(
			Map<String, ArrayList<NeighborComparisonState>> aboveAndBelowCellsResultMap,
			Map<String, ArrayList<NeighborComparisonState>> resultMap) {
		for (String cell : aboveAndBelowCellsResultMap.keySet()) {
			ArrayList<NeighborComparisonState> list2 = aboveAndBelowCellsResultMap
					.get(cell);
			ArrayList<NeighborComparisonState> list3 = resultMap.get(cell);
			if (list3 != null) {
				list3.addAll(list2);
			} else {
				resultMap.put(cell, list2);
			}
		}
	}

	/**
	 * Calculates the context of the cells above and below the formula cell into
	 * a ArrayList<NeighborComparisonState>
	 * 
	 * @param key
	 * @param tmpMap
	 * @param cellsAroundR1C1FormulaAboveBelowMap
	 * @return A List of NeighborComparisonState representing the states of the
	 *         four cells above and below from the formula cell
	 */
	private static ArrayList<NeighborComparisonState> calculateSurroundingAboveBelowCellsAsList(
			String key, Map<String, String> tmpMap,
			Map<String, ArrayList<String>> cellsAroundR1C1FormulaAboveBelowMap) {

		ArrayList<NeighborComparisonState> tmpValueList = new ArrayList<>();

		for (String cell : cellsAroundR1C1FormulaAboveBelowMap.get(key)) {

			if (tmpMap.keySet().contains(cell)) {
				// Same as the formula cell
				if (tmpMap.get(cell) != null && tmpMap.get(key) != null
						&& tmpMap.get(cell).equals(tmpMap.get(key))) {
					tmpValueList
							.add(NeighborComparisonState.ISSAMEASFORMULACELL);
				} else {
					boolean found = false;
					for (String cell2 : cellsAroundR1C1FormulaAboveBelowMap
							.get(key)) {
						if (!(cell2.equals(cell))) {
							if (tmpMap.keySet().contains(cell2)) {
								if (tmpMap.get(cell) != null
										&& tmpMap.get(cell2) != null
										&& tmpMap.get(cell2).equals(
												tmpMap.get(cell))) {
									found = true;

								}
							}
						}
					}
					// Not same as the formula cell but same with another of the
					// four in the row
					if (found) {
						tmpValueList
								.add(NeighborComparisonState.ISSAMEWITHOTHERBUTNOTWITHFORMULACELL);

						// Not same as the formula cell and also not with
						// another of the for in
						// the row
					} else {
						tmpValueList
								.add(NeighborComparisonState.ISDIFFERENTFROMFORMULACELL);
					}
				}
				// Non R1C1 cell
			} else {
				tmpValueList.add(NeighborComparisonState.ISNONR1C1);
			}
		}
		return tmpValueList;

	}

	/**
	 * Calculates the context of the cells right and left the formula cell into
	 * a ArrayList<NeighborComparisonState>
	 * 
	 * @param key
	 * @param tmpMap
	 * @param cellsAroundR1C1FormulaRightLeftMap
	 * @return A List of NeighborComparisonState representing the states of the
	 *         four cells right and left from the formula cell
	 */
	private static ArrayList<NeighborComparisonState> calculateSurroundingRightLeftCellsAsList(
			String key, Map<String, String> tmpMap,
			Map<String, ArrayList<String>> cellsAroundR1C1FormulaRightLeftMap) {

		ArrayList<NeighborComparisonState> tmpValueList = new ArrayList<>();
		for (String cell : cellsAroundR1C1FormulaRightLeftMap.get(key)) {

			if (tmpMap.keySet().contains(cell)) {
				// Same as the formula cell
				if (tmpMap.get(cell) != null && tmpMap.get(key) != null
						&& tmpMap.get(cell).equals(tmpMap.get(key))) {
					tmpValueList
							.add(NeighborComparisonState.ISSAMEASFORMULACELL);
				} else {

					boolean found = false;
					for (String cell2 : cellsAroundR1C1FormulaRightLeftMap
							.get(key)) {
						if (!(cell2.equals(cell))) {
							if (tmpMap.keySet().contains(cell2)) {
								if (tmpMap.get(cell) != null
										&& tmpMap.get(cell2) != null
										&& tmpMap.get(cell2).equals(
												tmpMap.get(cell))) {
									found = true;
								}
							}
						}
					}
					// Not same as the formula cell but same with another in the
					// row
					if (found) {
						tmpValueList
								.add(NeighborComparisonState.ISSAMEWITHOTHERBUTNOTWITHFORMULACELL);

						// Not same as the formula cell and also not with
						// another in the rows
					} else {
						tmpValueList
								.add(NeighborComparisonState.ISDIFFERENTFROMFORMULACELL);
					}
				}
				// Non R1C1 cell
			} else {
				tmpValueList.add(NeighborComparisonState.ISNONR1C1);
			}
		}
		return tmpValueList;
	}

	/**
	 * Separates char and number of a cell given as a string
	 * 
	 * @param cell
	 * @return List containing [char of cell , number of cell]
	 */
	private static ArrayList<String> separateCharAndNumberFromCell(String cell) {
		String tmpCellChar = new String();
		String tmpCellNumber = new String();
		ArrayList<String> result = new ArrayList<>();

		// Separate chars and numbers
		for (int i = 0; i < cell.length(); i++) {
			if (isCapitalLetter(cell, i)) {
				tmpCellChar += cell.charAt(i);
			} else if (cell.charAt(i) >= '0' && cell.charAt(i) <= '9') {
				tmpCellNumber += cell.charAt(i);
			}
		}
		result.add(tmpCellChar);
		result.add(tmpCellNumber);

		return result;
	}

	/**
	 * Generates the cell above the given cell string
	 * 
	 * @param cell
	 * @return Cell above the formula cell
	 */
	private static String getCellAbove(String cell) {
		int tmpIntNumber = 0;
		String keyAbove = new String();

		ArrayList<String> charNumberList = separateCharAndNumberFromCell(cell);

		tmpIntNumber = Integer.parseInt(charNumberList.get(1));
		// Special case - highest row
		if (tmpIntNumber == lowestRow) {
			// There is no cell
			keyAbove = NONCELL;
		} else {
			keyAbove = charNumberList.get(0)
					+ String.valueOf((tmpIntNumber - 1));
		}

		return keyAbove;
	}

	/**
	 * Generates the cell below the given key cell string
	 * 
	 * @param cell
	 * @return Cell below the key cell
	 */
	private static String getCellBelow(String cell) {
		int tmpIntNumber = 0;
		String keyBelow = new String();

		ArrayList<String> charNumberList = separateCharAndNumberFromCell(cell);

		tmpIntNumber = Integer.parseInt(charNumberList.get(1));
		// Special case - lowest row
		if (tmpIntNumber == highestRow) {
			// There is no cell
			keyBelow = NONCELL;
		} else {
			keyBelow = charNumberList.get(0)
					+ String.valueOf((tmpIntNumber + 1));
		}

		return keyBelow;
	}

	/**
	 * Generates the cell left the given key cell string
	 * 
	 * @param cell
	 * @return Cell left the key cell
	 */
	private static String getCellLeft(String cell, ArrayList<String> list) {
		String keyLeft = new String();

		ArrayList<String> charNumberList = separateCharAndNumberFromCell(cell);

		// Special case - lowest column
		if (charNumberList.get(0).equals(lowestColumn)) {
			// There is no cell
			keyLeft = NONCELL;
		} else {
			int index = list.indexOf((charNumberList.get(0)));
			keyLeft = list.get((index - 1)) + charNumberList.get(1);
		}

		return keyLeft;
	}

	/**
	 * Generates the cell right the given key cell string
	 * 
	 * @param cell
	 * @return Cell right the key cell
	 */
	private static String getCellRight(String cell, ArrayList<String> list) {
		String keyRight = new String();

		ArrayList<String> charNumberList = separateCharAndNumberFromCell(cell);

		// Special case - highest column
		if (charNumberList.get(0).equals(highestColumn)) {
			// There is no cell
			keyRight = NONCELL;
		} else {
			int index = list.indexOf((charNumberList.get(0)));
			keyRight = list.get((index + 1)) + charNumberList.get(1);
		}

		return keyRight;
	}

	/**
	 * 
	 * Gets all smells, calculates the severity and returns a list of all
	 * diagnoses candidates
	 * 
	 * @param xmlFilePath
	 *            Filepath to the excel xml file
	 * 
	 * @return A list of all diagnoses candidates, sorted by smells and already
	 *         rated
	 * @throws FileNotFoundException
	 */

	public static Map<String, Float> getSmells(String xmlFilePath)
			throws FileNotFoundException {

		// Get all smells
		Map<String, Integer> formulasLengthMap = getFormulasLength(xmlFilePath);
		Map<String, Integer> multipleOperationsMap = getMulitpleOperations(xmlFilePath);
		Map<String, Integer> ifMap = getNumberOfIfStatements(xmlFilePath);
		Map<String, Integer> emptyCellsMap = getEmptyCell(xmlFilePath);
		Map<String, Integer> multipleReferencesMap = getMultipleReferences(xmlFilePath);
		Map<String, Integer> referencesMap = getNumberOfCellReferences(xmlFilePath);
		Map<String, ArrayList<NeighborComparisonState>> cellsAroundCellMap = getCellsAroundCell(xmlFilePath);
		Map<String, Integer> cellsAroundCellMap2 = calcCellsAroundCell(cellsAroundCellMap);

		// Calculate function
		Map<String, Float> formulasLengthCalculationResultMap = calculateSmellFunctions(
				formulasLengthMap, Config.getIntValue(Config.LengthFunction),
				Config.getIntValue(Config.LengthStart),
				Config.getIntValue(Config.LengthEnd),
				Config.getIntValue(Config.LengthUpperEdge),
				Config.getFloatValue(Config.LengthCurveSlope),
				Config.getFloatValue(Config.LengthTurningPoint));

		Map<String, Float> multipleOperationsCalculationResultMap = calculateSmellFunctions(
				multipleOperationsMap,
				Config.getIntValue(Config.MultipleOperationsFunction),
				Config.getIntValue(Config.MultipleOperationsStart),
				Config.getIntValue(Config.MultipleOperationsEnd),
				Config.getIntValue(Config.MultipleOperationsUpperEdge),
				Config.getFloatValue(Config.MultipleReferencesCurveSlope),
				Config.getFloatValue(Config.MultipleOperationsTurningPoint));

		Map<String, Float> ifStatementsCalculationResult = calculateSmellFunctions(
				ifMap, Config.getIntValue(Config.IfFunction),
				Config.getIntValue(Config.IfStart),
				Config.getIntValue(Config.IfEnd),
				Config.getIntValue(Config.IfUpperEdge),
				Config.getFloatValue(Config.IfCurveSlope),
				Config.getFloatValue(Config.IfTurningPoint));

		Map<String, Float> multipleReferencesCalculationResultMap = calculateSmellFunctions(
				multipleReferencesMap,
				Config.getIntValue(Config.MultipleReferencesFunction),
				Config.getIntValue(Config.MultipleReferencesStart),
				Config.getIntValue(Config.MultipleReferencesEnd),
				Config.getIntValue(Config.MultipleReferencesUpperEdge),
				Config.getFloatValue(Config.MultipleReferencesCurveSlope),
				Config.getFloatValue(Config.MultipleReferencesTurningPoint));

		Map<String, Float> emptyCellsCalculationResultMap = calculateSmellFunctions(
				emptyCellsMap, Config.getIntValue(Config.EmptyCellFunction),
				Config.getIntValue(Config.EmptyCellsStart),
				Config.getIntValue(Config.EmptyCellsEnd),
				Config.getIntValue(Config.EmptyCellUpperEdge),
				Config.getFloatValue(Config.EmptyCellCurveSlope),
				Config.getFloatValue(Config.EmptyCellTurningPoint));

		Map<String, Float> cellsAroundCellCalculationResultMap = calculateSmellFunctions(
				cellsAroundCellMap2,
				Config.getIntValue(Config.CellsAroundCellFunction),
				Config.getIntValue(Config.CellsAroundCellStart),
				Config.getIntValue(Config.CellsAroundCellEnd),
				Config.getIntValue(Config.CellsAroundCellUpperEdge),
				Config.getFloatValue(Config.CellsAroundCellCurveSlope),
				Config.getFloatValue(Config.CellsAroundCellTurningPoint));

		Map<String, Float> referencesResultMap = calculateSmellFunctions(
				referencesMap, Config.getIntValue(Config.ReferencesFunction),
				Config.getIntValue(Config.ReferencesStart),
				Config.getIntValue(Config.ReferencesEnd),
				Config.getIntValue(Config.ReferencesUpperEdge),
				Config.getFloatValue(Config.ReferencesCurveSlope),
				Config.getFloatValue(Config.ReferencesTurningPoint));

		// Calculates total avg
		Map<String, Float> result = avgCalc(
				multipleReferencesCalculationResultMap,
				emptyCellsCalculationResultMap, ifStatementsCalculationResult,
				multipleOperationsCalculationResultMap,
				formulasLengthCalculationResultMap,
				cellsAroundCellCalculationResultMap, referencesResultMap);
		return result;
	}

	/**
	 * Distinguishes which function will be picked: linear / sigmoid
	 * 
	 * @param valueMap
	 * @param function
	 * @param start
	 * @param end
	 * @param apex
	 * @param stretch
	 * @return Result map of the calculated function with (Cell name as string,
	 *         result as float)
	 */
	private static Map<String, Float> calculateSmellFunctions(
			Map<String, Integer> valueMap, int function, int start, int end,
			int upperEdge, float curveSlope, float turningPoint) {

		Map<String, Float> resultMap = new HashMap<String, Float>();

		if (function == 1) {
			resultMap = linearFunction(valueMap, start, end);
		} else if (function == 0) {
			resultMap = sigmoidFunction(valueMap, upperEdge, curveSlope,
					turningPoint);
		}

		return resultMap;
	}

	/**
	 * Calculates the smell value of the given cellsAroundCellMap containing
	 * cells right, left, above and below around the formula cell
	 * 
	 * @param cellsAroundCellMap
	 * @return Returns 0 for a formula cell with two or more same cells in the
	 *         row; Returns maximum from stateCounters where the counter for
	 *         state with number 3 is higher (state 3 = cell is different from
	 *         formula cell but same with other in the row)
	 */
	public static Map<String, Integer> calcCellsAroundCell(
			Map<String, ArrayList<NeighborComparisonState>> cellsAroundCellMap) {

		Map<String, Integer> result = new HashMap<>();

		for (String cell : cellsAroundCellMap.keySet()) {

			int stateCounterRightAndLeftCells[] = new int[4];
			int stateCounterAboveAndBelowCells[] = new int[4];
			NeighborComparisonState cellsAroundCellValue[] = new NeighborComparisonState[8];

			// How many times exists (3,2,1,0) in the cells from right and left
			for (int i = 0; i < 4; i++) {
				cellsAroundCellValue[i] = cellsAroundCellMap.get(cell).get(i);
				int index = cellsAroundCellValue[i].ordinal();
				stateCounterRightAndLeftCells[index]++;
			}

			// How many times exists (3,2,1,0) in the cells from above and below
			for (int i = 4; i < 8; i++) {
				cellsAroundCellValue[i] = cellsAroundCellMap.get(cell).get(i);
				int index = cellsAroundCellValue[i].ordinal();
				stateCounterAboveAndBelowCells[index]++;
			}

			// Case differentiation / evaluation
			if (stateCounterRightAndLeftCells[2] >= 2
					|| stateCounterAboveAndBelowCells[2] >= 2) {

				result.put(cell, 0);
			} else {
				int max = Math.max(stateCounterRightAndLeftCells[3],
						stateCounterAboveAndBelowCells[3]);
				result.put(cell, max);
			}
		}
		return result;
	}

	/**
	 * Calculates average of all calculations of smells
	 * 
	 * @param multipleReferencesCalculationResultMap
	 * @param emptyCellCalculationResult
	 * @param ifStatementsCalculationResultMap
	 * @param multipleOperationsCalculationResultMap
	 * @param lengthCalculationResultMap
	 * @param cellsAroundCellCalculationResultMap
	 * @param referencesResultMap
	 * @return Total calculation of all smells as a map (cell name, result as
	 *         float)
	 */
	private static Map<String, Float> avgCalc(
			Map<String, Float> multipleReferencesCalculationResultMap,
			Map<String, Float> emptyCellCalculationResult,
			Map<String, Float> ifStatementsCalculationResultMap,
			Map<String, Float> multipleOperationsCalculationResultMap,
			Map<String, Float> lengthCalculationResultMap,
			Map<String, Float> cellsAroundCellCalculationResultMap,
			Map<String, Float> referencesResultMap) {

		Map<String, Float> result = new HashMap<>();

		for (String cell : multipleOperationsCalculationResultMap.keySet()) {
			float tmpSum = 0;
			int avgCounter = 0;

			if (valueCalculationHelper(multipleReferencesCalculationResultMap,
					cell, Config.MultipleReferencesSmellValue) > 0) {
				tmpSum += valueCalculationHelper(
						multipleReferencesCalculationResultMap, cell,
						Config.MultipleReferencesSmellValue);
				avgCounter++;
			}
			if (valueCalculationHelper(emptyCellCalculationResult, cell,
					Config.EmptyCellSmellValue) > 0) {
				tmpSum += valueCalculationHelper(emptyCellCalculationResult,
						cell, Config.EmptyCellSmellValue);
				avgCounter++;
			}

			if (valueCalculationHelper(ifStatementsCalculationResultMap, cell,
					Config.IfSmellValue) > 0) {
				tmpSum += valueCalculationHelper(
						ifStatementsCalculationResultMap, cell,
						Config.IfSmellValue);
				avgCounter++;
			}

			if (valueCalculationHelper(multipleOperationsCalculationResultMap,
					cell, Config.MultipleOperationsSmellValue) > 0) {
				tmpSum += valueCalculationHelper(
						multipleOperationsCalculationResultMap, cell,
						Config.MultipleOperationsSmellValue);
				avgCounter++;
			}

			if (valueCalculationHelper(lengthCalculationResultMap, cell,
					Config.LengthSmellValue) > 0) {
				tmpSum += valueCalculationHelper(lengthCalculationResultMap,
						cell, Config.LengthSmellValue);
				avgCounter++;
			}
			if (valueCalculationHelper(referencesResultMap, cell,
					Config.ReferencesSmellValue) > 0) {
				tmpSum += valueCalculationHelper(referencesResultMap, cell,
						Config.ReferencesSmellValue);
				avgCounter++;
			}

			if ((cellsAroundCellCalculationResultMap.get(cell) != null)
					&& (valueCalculationHelper(
							cellsAroundCellCalculationResultMap, cell,
							Config.CellsAroundCellSmellValue) > 0)) {
				tmpSum += valueCalculationHelper(
						cellsAroundCellCalculationResultMap, cell,
						Config.CellsAroundCellSmellValue);
				avgCounter++;
			}
			if (avgCounter == 0) {
				// Nothing - Divide by 0 not possible
			} else {
				tmpSum = tmpSum / (float) avgCounter;
			}

			result.put(cell, tmpSum);
		}

		return result;
	}

	/**
	 * Helps to calculate the overall ranking - multiples the rating with
	 * emphasis
	 * 
	 * @param calculationMap
	 * @param cell
	 * @param confParam
	 * @return
	 */
	private static float valueCalculationHelper(
			Map<String, Float> calculationMap, String cell, String confParam) {
		return Config.getFloatValue(confParam) * calculationMap.get(cell);
	}

	/**
	 * 
	 * Calculates sum of all calculations of smells
	 * 
	 * @param cellsAroundCellCalculationResultMap
	 * 
	 * @return Total calculation of all smells as a map (cell name, result as
	 *         float)
	 */
	@SuppressWarnings("unused")
	private static Map<String, Float> sumCalc(
			Map<String, Float> multipleReferencesCalculationResultMap,
			Map<String, Float> emptyCellCalculationResult,
			Map<String, Float> ifStatementsCalculationResultMap,
			Map<String, Float> multipleOperationsCalculationResultMap,
			Map<String, Float> lengthCalculationResultMap,
			Map<String, Float> cellsAroundCellCalculationResultMap,
			Map<String, Float> referencesResultMap) {

		Map<String, Float> result = new HashMap<>();

		for (String cell : multipleOperationsCalculationResultMap.keySet()) {
			float tmpSum = 0;
			tmpSum += valueCalculationHelper(
					multipleReferencesCalculationResultMap, cell,
					Config.MultipleReferencesSmellValue);
			tmpSum += valueCalculationHelper(emptyCellCalculationResult, cell,
					Config.EmptyCellSmellValue);
			tmpSum += valueCalculationHelper(ifStatementsCalculationResultMap,
					cell, Config.IfSmellValue);
			tmpSum += valueCalculationHelper(
					multipleOperationsCalculationResultMap, cell,
					Config.MultipleOperationsSmellValue);
			tmpSum += valueCalculationHelper(lengthCalculationResultMap, cell,
					Config.LengthSmellValue);
			tmpSum += valueCalculationHelper(referencesResultMap, cell,
					Config.ReferencesSmellValue);
			if (cellsAroundCellCalculationResultMap.get(cell) != null) {
				tmpSum += valueCalculationHelper(
						cellsAroundCellCalculationResultMap, cell,
						Config.CellsAroundCellSmellValue);
			}
			result.put(cell, tmpSum);
		}

		return result;
	}

	/**
	 * Calculates the sigmoid function with the given parameters
	 * 
	 * Definition: A sigmoid function is a mathematical function having an "S"
	 * shape (sigmoid curve).
	 * 
	 * @param valueMap
	 * @param apexParameter
	 * @param stretchParameter
	 * @return Map with (cell name, result as float)
	 */
	private static Map<String, Float> sigmoidFunction(
			Map<String, Integer> valueMap, int upperEdge, float curveSlope,
			float turningPoint) {

		Map<String, Float> result = new HashMap<>();

		for (String cell : valueMap.keySet()) {
			result.put(
					cell,
					(float) (upperEdge / (1 + Math.exp(curveSlope
							* (turningPoint - valueMap.get(cell))))));
		}

		return result;
	}

	/**
	 * Calculates the linear function with the given parameters
	 * 
	 * Definition: In calculus and related areas of mathematics, a linear
	 * function from the real numbers to the real numbers is a function whose
	 * graph (in Cartesian coordinates with uniform scales) is a line in the
	 * plane.
	 * 
	 * @param valueMap
	 * @param startParameter
	 * @param endParameter
	 * @return Map with (cell name, result of the calculation as float)
	 */
	private static Map<String, Float> linearFunction(
			Map<String, Integer> valueMap, int startParameter, int endParameter) {
		Map<String, Float> result = new HashMap<>();

		for (String cell : valueMap.keySet()) {

			if (valueMap.get(cell) < startParameter) {
				result.put(cell, 0f);
			} else if (valueMap.get(cell) > endParameter) {
				result.put(cell, 1f);
			} else {
				result.put(
						cell,
						(float) ((valueMap.get(cell) - startParameter) / (float) (endParameter - startParameter)));
			}
		}
		return result;
	}
}