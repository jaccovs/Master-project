package tests.ts;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.tools.Utilities;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;

/**
 * Class to test the use of user interactivity for querying cell values.
 * @author Thomas
 *
 */
public class ValueInteractivityTest {
	
	enum CellDeterminationHeuristic {Random, MinimalDiagnoses, FirstDiagnosis};
	
	static String inputFileDirectory = "experiments/spreadsheetsindividual/";
	
	static int NB_ITERATIONS = 1;
	
	// TODO: Need to check if this can lead to errors
	// if set to true, cells are removed from interimsToCheck, if they do not lead to optimization once
	boolean removeNonOptimizingCells = true;
	
	// if set to true, cells are removed from interimsToCheck that are not in front of a faulty cell in tree dependency analysis
	boolean removeCellsTreeAnalysis = true;
	
	// if set to true, correct values will be added to the model, if user interaction determines a faulty cell
	boolean addCorrectValuesForFaultyCells = true;
	
	// Use the new algorithm that determines the number of diagnoses for a correct cell value by only using consistency checks
	boolean useNewAlgorithm = true;
	
	// Defines how the next cell is determined, that is used to ask the user
	CellDeterminationHeuristic cellDeterminationHeuristic = CellDeterminationHeuristic.MinimalDiagnoses;
	
	Random random = new Random();
	
	int calculationsNeeded = 0;
	int interactionsNeeded = 0;
	long timeNeeded = 0;
	
	HashMap<String, String> valuesToSetCorrect = new HashMap<String, String>();
	HashMap<String, String> valuesToSetFaulty = new HashMap<String, String>();
	
	List<String> interimsToCheck = new ArrayList<String>();
	
	List<Diagnosis> lastCalculatedDiagnoses;
	DiagnosisModel lastCalculatedModel;
	long lastCalculationTime;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int overallCalculations = 0;
		int overallInteractions = 0;
		long overallTime = 0;
		for (int i = 0; i < NB_ITERATIONS; i++)
		{
			ValueInteractivityTest test = new ValueInteractivityTest();
	//		test.runScenario("11_or_12_diagnoses_tc0.xml", "11_or_12_diagnoses_tc0_correct.xml");
	//		test.runScenario("11_or_12_diagnoses_tc0_3correctValues.xml", "11_or_12_diagnoses_tc0_correct.xml");		
//			test.runScenario("salesforecast_TC_2Faults.xml", "salesforecast_TC_2Faults_correct.xml");
//			test.runScenario("salesforecast_TC_1Fault.xml", "salesforecast_TC_2Faults_correct.xml");
//			test.runScenario("salesforecast_TC_2Faults_example2.xml", "salesforecast_TC_2Faults_correct.xml");
//			test.runScenario("xxen.xml", "xxen_correct.xml");
//			test.runScenario("Hospital_Payment_Calculation_TS.xml", "Hospital_Payment_Calculation_TS_correct.xml");
//			test.runScenario("formula_query_example.xml", "formula_query_example_correct.xml");
			test.runScenario("formula_query_example_big.xml", "formula_query_example_big_correct.xml");
			
			overallCalculations += test.calculationsNeeded;
			overallInteractions += test.interactionsNeeded;
			overallTime += test.timeNeeded;
			System.out.println();
		}
		
		System.out.println("Average calculations: " + (overallCalculations / (float)NB_ITERATIONS));
		System.out.println("Average interactions: " + (overallInteractions / (float)NB_ITERATIONS));
		System.out.println("Average time: " + (overallTime / (float)NB_ITERATIONS));
		
	}
	
	/**
	 * Runs a complete simulation of user interactivity.
	 * @param mutatedXMLFilename The mutated xml file with inserted errors.
	 * @param correctXMLFilename The original xml without errors for simulating user interaction.
	 */
	public void runScenario(String mutatedXMLFilename, String correctXMLFilename) {
		long start = System.currentTimeMillis();
		ExquisiteAppXML correctXML = ExquisiteAppXML.parseToAppXML(inputFileDirectory + correctXMLFilename);
		ExquisiteAppXML mutatedXML = ExquisiteAppXML.parseToAppXML(inputFileDirectory + mutatedXMLFilename);
		
		AbstractHSDagBuilder diagnosisEngine = (AbstractHSDagBuilder) 
				EngineFactory.makeEngineFromAppXML(EngineType.HSDagStandardQX, mutatedXML, 4);
		//System.out.println("Created an engine for " + fullInputFilename);
		// Set the search depth
		diagnosisEngine.setSearchDepth(-1);
		diagnosisEngine.getSessionData().config.searchDepth = -1;
		// Shuffle the constraints
		//diagnosisEngine.sessionData.diagnosisModel.shufflePossiblyFaulyConstraints();
				
		System.out.println("Running test for " + mutatedXMLFilename + " with " + diagnosisEngine.model.getConstraintNames().size() + " constraints.");
		
		try {
			valuesToSetCorrect.clear();
			valuesToSetFaulty.clear();
			
			//Make a call to the diagnosis engine.
			int diagnosesCount = calculateDiagnoses(mutatedXMLFilename, "", "");
			
			System.out.println("Found " + diagnosesCount + " diagnoses in " + lastCalculationTime+ "ms." );
			
			System.out.println("Negative examples: " + diagnosisEngine.model.getNegativeExamples().size());
			System.out.println("Positive examples: " + diagnosisEngine.model.getPositiveExamples().size());
			
			
			interimsToCheck.clear();
			interimsToCheck.addAll(diagnosisEngine.sessionData.appXML.getInterims());
			removeCells(interimsToCheck, mutatedXML.getTestCases().elements().nextElement().getCorrectValues());
			removeCells(interimsToCheck, mutatedXML.getTestCases().elements().nextElement().getFaultyValues());
			if (removeCellsTreeAnalysis) {
				removeCellsTreeAnalysis(interimsToCheck, diagnosisEngine.model);
			}
			
			int minDiagnoses = diagnosesCount;
			int lastMinDiagnoses = minDiagnoses + 1;
			
			// minDiagnoes = -1 means, that another calculation of minDiagnoses has to be done, as a faulty value was found
			while (((minDiagnoses < lastMinDiagnoses) && (minDiagnoses > 1)) || minDiagnoses == -1) {
				if (minDiagnoses != - 1) {
					lastMinDiagnoses = minDiagnoses;
				}
				minDiagnoses = findCellWithMinimalDiagnoses(mutatedXMLFilename, mutatedXML, correctXML, minDiagnoses);
			}
			long end = System.currentTimeMillis();
			timeNeeded = end - start;
			showDiagnosisResults(mutatedXMLFilename);
			
			
			
			
		}
		catch (Exception e) {
			System.err.println("Error when calculating diagnosis for " +mutatedXMLFilename + " : " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		System.out.println("Finished.");
	}
	
	/**
	 * Remove cells because of tree dependency analysis.
	 * @param interimsToCheck
	 * @param model
	 */
	private void removeCellsTreeAnalysis(List<String> interimsToCheck, DiagnosisModel model) {
		Map<String, Constraint> irrelevantConstraints = model.getPositiveExamples().get(0).irrelevantConstraints;
		for (int i = 0; i < interimsToCheck.size(); i++) {
			String cell = interimsToCheck.get(i);
			if (irrelevantConstraints.get(cell) != null) {
				System.out.println("Removed " + cell + " from interimsToCheck because of dependency analysis.");
				interimsToCheck.remove(i);
				i--;
			}
		}
	}

	/**
	 * Removes all cells from interimsToCheck that are also present in cellsToRemove
	 * @param interimsToCheck
	 * @param cellsToRemove
	 */
	private void removeCells(List<String> interimsToCheck, Dictionary<String, String> cellsToRemove) {
		for (int i = 0; i < interimsToCheck.size(); i++) {
			String cell = interimsToCheck.get(i);
			if (cellsToRemove.get(cell) != null) {
				System.out.println("Removed " + cell + " from interimsToCheck because it is marked as correct / faulty.");
				interimsToCheck.remove(i);
				i--;
			}
		}
	}

	/**
	 * Prints end results
	 * @param mutatedXMLFilename
	 */
	private void showDiagnosisResults(String mutatedXMLFilename) {
		int diagnosesCount = calculateDiagnoses(mutatedXMLFilename, "", "");
			
		StringBuilder correctCellnames = new StringBuilder();
		Iterator<String> keys = valuesToSetCorrect.keySet().iterator();
		while (keys.hasNext()) {
			String cellname = keys.next();
			correctCellnames.append(cellname);
			if (keys.hasNext()) {
				correctCellnames.append(", ");
			}
		}
		
		StringBuilder faultyCellnames = new StringBuilder();
		keys = valuesToSetFaulty.keySet().iterator();
		while (keys.hasNext()) {
			String cellname = keys.next();
			faultyCellnames.append(cellname + ": " + valuesToSetFaulty.get(cellname));
			if (keys.hasNext()) {
				faultyCellnames.append(", ");
			}
		}
		
		System.out.println();
		System.out.println("Results: ");
		System.out.println();
		System.out.println("Found Diagnoses: " + diagnosesCount);
		System.out.println(Utilities.printSortedDiagnoses(lastCalculatedDiagnoses, ';'));
		System.out.println("Cells to set correct:");
		System.out.println(correctCellnames.toString());
		System.out.println("Cells to set faulty:");
		System.out.println(faultyCellnames.toString());
		System.out.println("Diagnosis calculations needed: " + calculationsNeeded);
		System.out.println("User interactions needed: " + interactionsNeeded);
		System.out.println("Time needed: " + timeNeeded);
	}

	/**
	 * Starts one iteration of searching for a cell with minimal diagnoses
	 * @param mutatedXMLFilename
	 * @param mutatedXML
	 * @param correctXML
	 * @param lastMinDiagnoses
	 * @return
	 */
	private int findCellWithMinimalDiagnoses(String mutatedXMLFilename, ExquisiteAppXML mutatedXML, ExquisiteAppXML correctXML, int lastMinDiagnoses) {
		Dictionary<String, String> mutatedValues = mutatedXML.getTestCases().elements().nextElement().getValues();
		Dictionary<String, String> correctValues = correctXML.getTestCases().elements().nextElement().getValues();
		Dictionary<String, String> faultyValues = mutatedXML.getTestCases().elements().nextElement().getFaultyValues();
		Dictionary<String, Integer> cellsAndDiagnoses = new Hashtable<String, Integer>();
		
		List<String> currentInterimsToCheck = new ArrayList<String>(interimsToCheck);
		
		if (cellDeterminationHeuristic != CellDeterminationHeuristic.FirstDiagnosis) {
			// Calculate diagnosis counts for all interims to check
			for (String cellname : currentInterimsToCheck) {
				if (faultyValues.get(cellname) != null) {
					System.out.println("Cell " + cellname + " is marked as a faulty cell.");
				}
				else {
					
					int diagnosesCount;
					
					if (!useNewAlgorithm) {
						diagnosesCount = calculateDiagnoses(mutatedXMLFilename, cellname, mutatedValues.get(cellname));
					} else {
						diagnosesCount = calculateDiagnosesCount(lastCalculatedDiagnoses, lastCalculatedModel, cellname, mutatedValues.get(cellname));
					}
			
					System.out.println("Diagnoses for " + cellname + " (in " + lastCalculationTime + "ms): " + diagnosesCount);
						
					if (removeNonOptimizingCells && lastMinDiagnoses > -1 && diagnosesCount >= lastMinDiagnoses) {
						// Remove cell from interimsToCheck, as it did not lead to an optimization
						interimsToCheck.remove(cellname);
					}
					else {
						cellsAndDiagnoses.put(cellname, diagnosesCount);
					}
					
				}
			}
		}
		
		List<Diagnosis> diagnoses = null;
		DiagnosisModel model = null;
		if (cellDeterminationHeuristic == CellDeterminationHeuristic.FirstDiagnosis) {
			calculateDiagnoses(mutatedXMLFilename, "", "");
			diagnoses = lastCalculatedDiagnoses;
			model = lastCalculatedModel;
		}
		
		boolean foundNext = false;
		
		String chosenCell = "";
		int chosenCellsDiagnoses = 0;
		
		// search for minimal number of diagnoses with correct cell value
		while (!foundNext) {
			
			chosenCell = chooseCellForInteraction(cellsAndDiagnoses, diagnoses, faultyValues, model);
			if (cellDeterminationHeuristic != CellDeterminationHeuristic.FirstDiagnosis) {
				chosenCellsDiagnoses = cellsAndDiagnoses.get(chosenCell); 
			}
			
			// have we found a better one?
			if (lastMinDiagnoses > -1 && chosenCellsDiagnoses >= lastMinDiagnoses) {
				System.out.println("Cannot find cell to optimize count of diagnoses.");
				break;
			}
			
			boolean isCellCorrect = simulateUserInteraction(chosenCell, mutatedXML, correctXML); 
			
			if (isCellCorrect) {
				foundNext = true;
				System.out.println("Found cell " + chosenCell + " with " + chosenCellsDiagnoses + " diagnoses to be correct.");
				
				// remove chosen cell from interimsToCheck and add it to list that will be presented at the end
				interimsToCheck.remove(chosenCell);
				valuesToSetCorrect.put(chosenCell, correctValues.get(chosenCell));
			} else {
				cellsAndDiagnoses.remove(chosenCell);
				interimsToCheck.remove(chosenCell);
				if (addCorrectValuesForFaultyCells) {
					System.out.println("Adding cell " + chosenCell + " with value " + correctValues.get(chosenCell) + " to faulty cells.");
					// we add the cell to faulty values
					valuesToSetFaulty.put(chosenCell, correctValues.get(chosenCell));
					// to allow reevaluation of next cell to choose for user interaction, we return -1 as diagnoses count
					foundNext = true;
					chosenCellsDiagnoses = -1;
					
					int diagnosesCount = calculateDiagnoses(mutatedXMLFilename, "", "");
					
					System.out.println("New diagnoses count: " + diagnosesCount);
					if (diagnosesCount < lastMinDiagnoses) {
						chosenCellsDiagnoses = diagnosesCount;
					}
				}
			}
			if (cellDeterminationHeuristic == CellDeterminationHeuristic.FirstDiagnosis || useNewAlgorithm) {
				int diagnosesCount = calculateDiagnoses(mutatedXMLFilename, "", "");
				if (diagnosesCount < lastMinDiagnoses) {
					chosenCellsDiagnoses = diagnosesCount;
				}
			}
		}
		return chosenCellsDiagnoses;
	}

	private boolean simulateUserInteraction(String chosenCell, ExquisiteAppXML mutatedXML, ExquisiteAppXML correctXML) {
		Dictionary<String, String> mutatedValues = mutatedXML.getTestCases().elements().nextElement().getValues();
		Dictionary<String, String> correctValues = correctXML.getTestCases().elements().nextElement().getValues();
		
		// check if the found cell has correct value (Simulation of user interaction)
		String value = mutatedValues.get(chosenCell);
		System.out.println("Is the value " + value + " for cell " + chosenCell + " correct?      <======= User Interaction");
		interactionsNeeded++;
		if (correctValues.get(chosenCell).equals(value)) {
			System.out.println("Yes!");
			return true;
		} else {
			System.out.println("No");
			return false;
		}
	}
	
	/**
	 * Chooses a cell for interaction with the user based on the cellDeterminationHeuristic
	 * @param cellsAndDiagnoses
	 * @return
	 */
	private String chooseCellForInteraction(Dictionary<String, Integer> cellsAndDiagnoses, List<Diagnosis> diagnoses, Dictionary<String, String> faultyValues, DiagnosisModel model) {
		String chosenCell = "";
		Enumeration<String> cells = cellsAndDiagnoses.keys();
		
		switch (cellDeterminationHeuristic) {
		case MinimalDiagnoses:
			// search for minimal number of diagnoses
			int chosenCellsDiagnoses = Integer.MAX_VALUE;
			while (cells.hasMoreElements()) {
				String cellname = cells.nextElement();
			
				if (cellsAndDiagnoses.get(cellname) < chosenCellsDiagnoses) {
					chosenCellsDiagnoses = cellsAndDiagnoses.get(cellname);
					chosenCell = cellname;
				}
			}
			break;
		case Random:
			if (cellsAndDiagnoses.size() > 0) {
				int r = random.nextInt(cellsAndDiagnoses.size());
				for (int i=0; i <= r; i++) {
					chosenCell = cells.nextElement();
				}
			}
			break;
		case FirstDiagnosis:
			for (int i = 0; i < diagnoses.size(); i++) {
				Diagnosis diag = diagnoses.get(i);
				for (int k = 0; k < diag.getElements().size(); k++) {
					Constraint c = diag.getElements().get(k);
					String name = model.getConstraintName(c);
					if (faultyValues.get(name) == null && valuesToSetFaulty.get(name) == null) {
						chosenCell = name;
						return chosenCell;
					}
				}
			}
			break;
		}
		return chosenCell;
	}
	
	/**
	 * Calculates diagnoses for the current values of valuesToSetCorrect and valuesToSetFaulty
	 * @param mutatedXMLFilename
	 * @param additionalCorrectCell An additional cell to add to correct values
	 * @param additionalCorrectCellsValue The cells value
	 * @return
	 */
	private int calculateDiagnoses(String mutatedXMLFilename, String additionalCorrectCell, String additionalCorrectCellsValue) {
		ExquisiteAppXML newXml = ExquisiteAppXML.parseToAppXML(inputFileDirectory + mutatedXMLFilename);
		
		Dictionary<String, String> newXmlCorrectValues = newXml.getTestCases().elements().nextElement().getCorrectValues();
		
		if (!additionalCorrectCell.isEmpty() && !additionalCorrectCellsValue.isEmpty()) {
			newXmlCorrectValues.put(additionalCorrectCell, additionalCorrectCellsValue);
		}
		
		// Set all previous values to be correct in new xml
		setValuesInDictionary(valuesToSetCorrect, newXmlCorrectValues);
		
		// Set faulty values (used with boolean addCorrectValuesForFaultyCells)
		setValuesInDictionary(valuesToSetFaulty, newXml.getTestCases().elements().nextElement().getFaultyValues());
		
		AbstractHSDagBuilder diagnosisEngine = (AbstractHSDagBuilder) EngineFactory.makeEngineFromAppXML(EngineType.HSDagStandardQX, newXml, 4);
		
		lastCalculatedModel = diagnosisEngine.model;
		
		long startTime = System.currentTimeMillis();
		try {
			lastCalculatedDiagnoses = diagnosisEngine.calculateDiagnoses();
			long endTime = System.currentTimeMillis();
			lastCalculationTime = endTime - startTime;
			calculationsNeeded++;
			return lastCalculatedDiagnoses.size();
			
		} catch (DiagnosisException e) {
			System.err.println("Error wenn calcualting diagnoses!");
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Determines the number of diagnoses, that would remain, if the additionalCell is added to the correct values.
	 * @param diagnoses
	 * @param mutatedXMLFilename
	 * @param additionalCorrectCell
	 * @param additionalCorrectCellsValue
	 * @return
	 */
	private int calculateDiagnosesCount(List<Diagnosis> diagnoses, DiagnosisModel oldModel, String additionalCorrectCell, String additionalCorrectCellsValue) {
		
		DiagnosisModel startModel = new DiagnosisModel(oldModel);
		// Build CSP
//		ExquisiteAppXML newXml = ExquisiteAppXML.parseToAppXML(inputFileDirectory + mutatedXMLFilename);
//		AbstractHSDagBuilder diagnosisEngine = (AbstractHSDagBuilder) EngineFactory.makeEngineFromAppXML(EngineType.HSDagStandardQX, newXml, 4);
		
//		DiagnosisModel model = diagnosisEngine.sessionData.diagnosisModel;
		
		Example example = startModel.getPositiveExamples().get(0);
		
		for (Constraint constraint : example.constraints) {
			startModel.addCorrectConstraint(constraint,
					example.constraintNames.get(constraint));
		}
		startModel.removeConstraintsToIgnore(new ArrayList<Constraint>(example.irrelevantConstraints.values()));
		
		try {
		long startTime = System.currentTimeMillis();
		int count = 0;
		// Iterate over Diagnoses
		for (int i = 0; i < diagnoses.size(); i++) {
			Diagnosis diag = diagnoses.get(i);
			
			DiagnosisModel newModel = new DiagnosisModel(startModel);
			
			// Relax CSP with constraints of diganosis
			List<Constraint> constraintsToIgnore = new ArrayList<Constraint>();
			
			List<Constraint> diagConstraints = diag.getElements();
			for (Constraint constraint : diagConstraints) {
				String name = oldModel.getConstraintName(constraint);
//				System.out.println(name);
				Constraint newC = newModel.getConstraintByName(name);
//				System.out.println(newC.getName());
				constraintsToIgnore.add(newC);
			}
			newModel.removeConstraintsToIgnore(constraintsToIgnore);
			
			
			// add cell to CSP
			for (Variable v : newModel.getVariables()) {
				if (v.getName().equals(additionalCorrectCell)) {
					Constraint c = Choco.eq((IntegerExpressionVariable) v, Integer.parseInt(additionalCorrectCellsValue));
					newModel.addCorrectConstraint(c, additionalCorrectCell);
//					System.out.println(additionalCorrectCell);
					break;
				}
				else {
//					System.out.println(v.getName() + " != " + additionalCorrectCell);
				}
			}
			ExquisiteSession sessionData = new ExquisiteSession();
			sessionData.diagnosisModel = newModel;
			
			// Check for consistency
			QuickXPlain qxplain = NodeExpander.createQX(sessionData, null);
			
			boolean consistent = qxplain.checkConsistency();
			
//			ConflictCheckingResult checkingResult =	qxplain.checkExamples(diagnosisEngine.model.getPositiveExamples(), new ArrayList<Constraint>(), true);
			
			// Count consistent diagnoses
//			if (!checkingResult.conflictFound()) {
			if (consistent) {
				count++;
			}
		}
		long endTime = System.currentTimeMillis();
		lastCalculationTime = endTime - startTime;
		
		return count;
		} catch (DomainSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Sets values as correct values inside the xml (in first testcase only!)
	 * @param xml
	 * @param valuesToSetCorrect
	 */
	private void setValuesInDictionary(HashMap<String, String> valuesToSetCorrect, Dictionary<String, String> dict) {
		for (String key : valuesToSetCorrect.keySet()) {
			dict.put(key, valuesToSetCorrect.get(key));
		}
	}
}
