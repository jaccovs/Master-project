package tests.ts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.tools.Utilities;

/**
 * Class to test the use of user interactivity for querying formula values.
 * @author Thomas
 *
 */
public class FormulaInteractivityTest {
	
	static String inputFileDirectory = "experiments/spreadsheetsindividual/";
	
	DiagnosisModel lastCalculatedModel = null;
	List<Diagnosis> lastCalculatedDiagnoses = null;
	long lastCalculationTime = 0;
	
	int calculationsNeeded = 0;
	int userInteractionsNeeded = 0;
	
	List<String> formulasToSetCorrect = new ArrayList<String>();
	Dictionary<String, String> formulasToCorrect = new Hashtable<String, String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FormulaInteractivityTest test = new FormulaInteractivityTest();
//				test.runScenario("11_or_12_diagnoses_tc0.xml", "11_or_12_diagnoses_tc0_correct.xml");
		//		test.runScenario("11_or_12_diagnoses_tc0_3correctValues.xml", "11_or_12_diagnoses_tc0_correct.xml");		
//				test.runScenario("salesforecast_TC_2Faults.xml", "salesforecast_TC_2Faults_correct.xml");
//				test.runScenario("salesforecast_TC_1Fault.xml", "salesforecast_TC_2Faults_correct.xml");
//				test.runScenario("salesforecast_TC_2Faults_example2.xml", "salesforecast_TC_2Faults_correct.xml");
//				test.runScenario("xxen.xml", "xxen_correct.xml");
//				test.runScenario("Hospital_Payment_Calculation_TS.xml", "Hospital_Payment_Calculation_TS_correct.xml");
//				test.runScenario("formula_query_example.xml", "formula_query_example_correct.xml");
				test.runScenario("formula_query_example_big.xml", "formula_query_example_big_correct.xml");
	}

	/**
	 * Run a test for given scenario.
	 * @param mutatedXMLFilename
	 * @param correctXMLFilename
	 */
	public void runScenario(String mutatedXMLFilename, String correctXMLFilename) {
		System.out.println("Running test for " + mutatedXMLFilename);
		ExquisiteAppXML correctXML = ExquisiteAppXML.parseToAppXML(inputFileDirectory + correctXMLFilename);
		ExquisiteAppXML mutatedXML = ExquisiteAppXML.parseToAppXML(inputFileDirectory + mutatedXMLFilename);
		
		calculateDiagnoses(mutatedXMLFilename, "");
		System.out.println("Found " + lastCalculatedDiagnoses.size() + " diagnoses.");

		while (lastCalculatedDiagnoses.size() > 1) {
			String formulaCell = findFormulaToQuery(lastCalculatedDiagnoses);
			
			boolean answer = queryFormula(formulaCell, mutatedXML, correctXML);
			
			if (answer) {
				addCorrectFormula(formulaCell);
				List<String> equivalentFormulas = findEquivalentFormulas(formulaCell, mutatedXML);
				if (equivalentFormulas.size() > 0) {
					List<String> correctFormulas = queryFormulaEquivalency(formulaCell, equivalentFormulas, mutatedXML, correctXML);
					
					for (String f: correctFormulas) {
						addCorrectFormula(f);
					}
				} else {
					System.out.println("No equivalent formulas found.");
				}
				
				// TODO ask for formulas next, that are in equivalentFormulas, but not in correctFormulas?
			} else {
				addFormulaToCorrect(formulaCell, correctXML.getFormulas().get(formulaCell));
			}
			
			calculateDiagnoses(mutatedXMLFilename, "");
			System.out.println("Found " + lastCalculatedDiagnoses.size() + " diagnoses.");
		}
		
		showDiagnosisResults(mutatedXMLFilename);
		
		
	}

	/**
	 * Prints the result of a test run.
	 * @param mutatedXMLFilename
	 */
	private void showDiagnosisResults(String mutatedXMLFilename) {
		System.out.println("Found all faults with " + userInteractionsNeeded + " user interactions.");
		System.out.println();
		System.out.println("Corrected formulas: " + formulasToCorrect.size());
		Enumeration<String> e = formulasToCorrect.keys();
		while (e.hasMoreElements()) {
			String formulaCell = e.nextElement();
			System.out.println(formulaCell + ": " + formulasToCorrect.get(formulaCell));
		}
		System.out.println("Additional Diagnoses: " + lastCalculatedDiagnoses.size());
		System.out.println(Utilities.printSortedDiagnoses(lastCalculatedDiagnoses, ';'));
	}

	/**
	 * Simulates the user interaction for querying a list of equivalent formulas.
	 * @param formula
	 * @param equivalentFormulas
	 * @param mutatedXML
	 * @param correctXML
	 * @return
	 */
	private List<String> queryFormulaEquivalency(String formula, List<String> equivalentFormulas, ExquisiteAppXML mutatedXML, ExquisiteAppXML correctXML) {
		userInteractionsNeeded++;
		System.out.println("For which of the following cells is the copy equivalence semantically correct?");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < equivalentFormulas.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(equivalentFormulas.get(i));
		}
		System.out.println(sb.toString());


		List<String> r = new ArrayList<String>();
		for (String cell: equivalentFormulas) {
			if (mutatedXML.getFormulas().get(cell).equals(correctXML.getFormulas().get(cell))) {
				r.add(cell);
			}
		}
		return r;
	}

	/**
	 * Searches for equivalent formulas.
	 * @param formulaCell
	 * @param mutatedXML
	 * @return
	 */
	private List<String> findEquivalentFormulas(String formulaCell, ExquisiteAppXML mutatedXML) {
		List<String> r = new ArrayList<String>();
		Dictionary<String, String> r1c1 = mutatedXML.getFormulasR1C1(); 
		String formula = r1c1.get(formulaCell);
		Enumeration<String> cells = r1c1.keys();
		while (cells.hasMoreElements()) {
			String cell = (String) cells.nextElement();
			if (formula.equals(r1c1.get(cell)) && !formulaCell.equals(cell)) {
				r.add(cell);
			}
		}
		Collections.sort(r);
		return r;
	}

	private void addCorrectFormula(String formulaCell) {
		formulasToSetCorrect.add(formulaCell);
		System.out.println("Set " + formulaCell + " to be correct.");
	}
	
	private void addFormulaToCorrect(String formulaCell, String correctFormula) {
		formulasToCorrect.put(formulaCell, correctFormula);
		System.out.println("Corrected formula of " + formulaCell + ".");
	}

	/**
	 * Simulates user interaction to query the correctness of one formula. 
	 * @param formulaCell
	 * @param mutatedXML
	 * @param correctXML
	 * @return
	 */
	private boolean queryFormula(String formulaCell, ExquisiteAppXML mutatedXML, ExquisiteAppXML correctXML) {
		userInteractionsNeeded++;
		String mutatedFormula = mutatedXML.getFormulas().get(formulaCell);
		String correctFormula = correctXML.getFormulas().get(formulaCell);
		System.out.println("Is the formula " + mutatedFormula + " in cell " + formulaCell + " correct?");
		if (mutatedFormula.equals(correctFormula)) {
			System.out.println("Yes!");
			return true;
		} else {
			System.out.println("No!");
			return false;
		}
	}

	/**
	 * Chooses the formula that should be queried next.
	 * @param diagnoses
	 * @return
	 */
	private String findFormulaToQuery(List<Diagnosis> diagnoses) {
		return lastCalculatedModel.getConstraintName(diagnoses.get(0).getElements().get(0));
	}

	/**
	 * Calculates diagnoses for the current values of valuesToSetCorrect and valuesToSetFaulty
	 * @param mutatedXMLFilename
	 * @param additionalCorrectCell An additional cell to add to correct values
	 * @param additionalCorrectCellsValue The cells value
	 * @return
	 */
	private int calculateDiagnoses(String mutatedXMLFilename, String additionalCorrectFormula) {
		ExquisiteAppXML newXml = ExquisiteAppXML.parseToAppXML(inputFileDirectory + mutatedXMLFilename);
		
		Dictionary<String, String> newXmlCorrectFormulas = newXml.getCorrectFormulas();
		
		if (!additionalCorrectFormula.isEmpty()) {
			newXmlCorrectFormulas.put(additionalCorrectFormula, "0");
		}
		
		// Set all previous formulas to be correct in new xml
		setFormulasInDictionary(formulasToSetCorrect, newXmlCorrectFormulas);
		
		// Set faulty formulas
		//setFormulasInDictionary(formulasToSetFaulty, newXml.get);
		// TODO: Remove faulty formulas from csp. Add faulty formulas to all diagnoses later or add a diagnosis with faulty formulas, if no diagnosis was found.
		replaceFormulasInDictionary(formulasToCorrect, newXml.getFormulas());
		
		AbstractHSDagBuilder diagnosisEngine = (AbstractHSDagBuilder) EngineFactory.makeEngineFromAppXML(EngineType.HSDagStandardQX, newXml, 4);
		
		// Remove faulty formulas from csp. They will be added to diagnoses later
//		List<Constraint> constraintsToIgnore = new ArrayList<Constraint>(formulasToSetFaulty.size());
//		for (String formula: formulasToSetFaulty) {
//			constraintsToIgnore.add(diagnosisEngine.model.getConstraintByName(formula));
//		}
//		diagnosisEngine.model.removeConstraintsToIgnore(constraintsToIgnore);
		
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
	
	private void replaceFormulasInDictionary(Dictionary<String, String> formulasToCorrect, Dictionary<String, String> dict) {
		Enumeration<String> e = formulasToCorrect.keys();
		while (e.hasMoreElements()) {
			String formulaCell = e.nextElement();
			dict.remove(formulaCell);
			dict.put(formulaCell, formulasToCorrect.get(formulaCell));
		}
		
	}
	
	/**
	 * Sets values as correct values inside the xml (in first testcase only!)
	 * @param xml
	 * @param valuesToSetCorrect
	 */
	private void setFormulasInDictionary(List<String> formulasToSetCorrect, Dictionary<String, String> dict) {
		for (String formula: formulasToSetCorrect) {
			dict.put(formula, "0");
		}
	}

}
