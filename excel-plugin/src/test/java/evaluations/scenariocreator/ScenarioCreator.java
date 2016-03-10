package evaluations.scenariocreator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.TestCase;
import org.exquisite.tools.Utilities;
import org.exquisite.xml.XMLWriter;

import evaluations.mutation.Mutation;
import evaluations.tools.PositiveTestCaseGenerator;

/**
 * Class for creating mutations of a given ExquisiteAppXML
 * @author Thomas
 *
 */
public class ScenarioCreator {
	
	private ArrayList<String> supportedArithmeticOperators = new ArrayList<String>();
	
	public ScenarioCreator() {
		supportedArithmeticOperators.add("*"); 
		supportedArithmeticOperators.add("+");
		supportedArithmeticOperators.add("-");
	}
	
	/**
	 * Creates mutated scenarios from given xml.
	 * @param xmlPath the path to the ExquisiteAppXML file that should be mutated
	 * @param outputPath the path of the directory where the created mutations should be stored
	 * @param count the count of mutations to be created
	 */
	public void createScenarios(String xmlPath, String outputPath, int count) {
		for (int i = 0; i < count; i++) {
			String mutatedXMLPath = FilenameUtils.concat(outputPath, FilenameUtils.getBaseName(xmlPath) + "_" + i + ".xml");
			createScenario(xmlPath, mutatedXMLPath, 2, 2, 0.1f, 0.1f, 1f);	
		}
	}
	
	/**
	 * Creates one mutated scenario from given xml file and saves it to given path.
	 * @param xmlPath the path to the ExquisiteAppXML file that should be mutated
	 * @param mutatedXMLPath the filename of the mutated xml that is to be created
	 * @param mutationCount the number of mutations to make
	 * @param testCaseCount the number of positive test cases to generate
	 * @param correctFormulasRatio the ratio of formulas that should be marked as correct
	 * @param correctValuesRatio the ratio of values that should be marked as correct in each test case. CURRENTLY NOT IN USE
	 * @param valueBoundsRatio the ratio of value bounds that should not be deleted
	 */
	public void createScenario(String xmlPath, String mutatedXMLPath, int mutationCount, int testCaseCount, float correctFormulasRatio, 
			float correctValuesRatio, float valueBoundsRatio) {
		try {
			ExquisiteAppXML exquisiteAppXML = ExquisiteAppXML.parseToAppXML(xmlPath);

			createScenario(exquisiteAppXML, mutationCount, testCaseCount, correctFormulasRatio, correctValuesRatio, valueBoundsRatio);
			
			writeXML(exquisiteAppXML, mutatedXMLPath);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Mutates given ExquisiteAppXML.
	 * @param exquisiteAppXML the ExquisiteAppXML to mutate
	 * @param mutationCount the number of mutations to make
	 * @param testCaseCount the number of positive test cases to generate
	 * @param correctFormulasRatio the ratio of formulas that should be marked as correct
	 * @param correctValuesRatio the ratio of values that should be marked as correct in each test case. CURRENTLY NOT IN USE
	 * @param valueBoundsRatio the ratio of value bounds that should not be deleted
	 */
	public void createScenario(ExquisiteAppXML exquisiteAppXML, int mutationCount, int testCaseCount, float correctFormulasRatio, 
			float correctValuesRatio, float valueBoundsRatio) {
		PositiveTestCaseGenerator positiveTestCaseGenerator = new PositiveTestCaseGenerator(exquisiteAppXML, new Random());
		positiveTestCaseGenerator.generateTestCases(testCaseCount, true);
		Dictionary<TestCase, Dictionary<String, String>> formulaValues = positiveTestCaseGenerator.getFormulaValues();

		List<String> mutatedFormulas = mutateFormulas(exquisiteAppXML, mutationCount);
//		List<String> mutatedFormulas = mutateFormula(exquisiteAppXML, "WS_1_S12");		
//		mutatedFormulas.clear();
//		mutatedFormulas.add("WS_1_T22");

		markFaultyValues(exquisiteAppXML, mutatedFormulas, formulaValues);

		markCorrectFormulas(exquisiteAppXML, correctFormulasRatio, mutatedFormulas);
		deleteValueBounds(exquisiteAppXML, valueBoundsRatio);
		// TS: TODO - Correct Values are not set. Needs recalculation of interims and outputs because of mutated formulas
	}

	private void deleteValueBounds(ExquisiteAppXML exquisiteAppXML, float valueBoundsRatio) {
		// TS: TODO - Deletes complete ranges of ValueBounds, not only ValueBounds of single cells
		
		int valueBoundsSize = exquisiteAppXML.getValueBounds().size();
		int valueBoundsToDelete = valueBoundsSize - (int)(valueBoundsSize * valueBoundsRatio);
		
		Enumeration<String> valueBoundEnumerator = exquisiteAppXML.getValueBounds().keys();
		Integer[] indices = Utilities.getRandomIndices(valueBoundsToDelete, valueBoundsSize);
		int i = 0;
		int elementCounter = 0;
		while(valueBoundEnumerator.hasMoreElements() && i < valueBoundsToDelete)
		{
			String key = valueBoundEnumerator.nextElement();

			if (indices[i] == elementCounter)
			{
				exquisiteAppXML.getValueBounds().remove(key);
				i++;
			}
			elementCounter++;
		}
	}

	private void markCorrectFormulas(ExquisiteAppXML exquisiteAppXML, float correctFormulasRatio, List<String> mutatedFormulas) {
		Hashtable<String, String> copy = new Hashtable<String, String>();
		copy.putAll((Hashtable<String, String>)exquisiteAppXML.getFormulas());
		
		for (int i = 0; i < mutatedFormulas.size(); i++) {
			copy.remove(mutatedFormulas.get(i));
		}
		
		int correctFormulasCount = (int)(copy.size() * correctFormulasRatio);
		
		
		Enumeration<String> formulaEnumerator = copy.keys();
		Integer[] indices = Utilities.getRandomIndices(correctFormulasCount, copy.size());
		int i = 0;
		int elementCounter = 0;
		while(formulaEnumerator.hasMoreElements() && i < correctFormulasCount)
		{
			String key = formulaEnumerator.nextElement();

			if (indices[i] == elementCounter)
			{
				exquisiteAppXML.getCorrectFormulas().put(key, "0");
				i++;
			}
			elementCounter++;
		}
	}

	private List<String> mutateFormulas(ExquisiteAppXML exquisiteAppXML, int mutationCount) {
		System.out.println("Creating mutant");
		
		Mutation mutation = new Mutation(exquisiteAppXML);
		return mutation.mutate(mutationCount, 1, supportedArithmeticOperators);
	}
	
	public List<String> mutateFormula(ExquisiteAppXML exquisiteAppXML, String formula) {
		List<String> mutatedFormulas = new ArrayList<String>();
		Mutation mutation = new Mutation(exquisiteAppXML);
		mutation.mutate(formula, 1, supportedArithmeticOperators);
		mutatedFormulas.add(formula);
		return mutatedFormulas;
	}
	
	private void markFaultyValues(ExquisiteAppXML exquisiteAppXML, List<String> mutatedFormulas, Dictionary<TestCase, Dictionary<String, String>> formulaValues) {
		for (Enumeration<String> en = exquisiteAppXML.getTestCases().keys(); en.hasMoreElements(); )
		{
			String key = en.nextElement();
			TestCase testCase = exquisiteAppXML.getTestCases().get(key);
			
			for (int i = 0; i < mutatedFormulas.size(); i++)
			{
				String mutatedFormula = mutatedFormulas.get(i);
				testCase.getFaultyValues().put(mutatedFormula, formulaValues.get(testCase).get(mutatedFormula));
			}
		}
	}
	
	/*
	private ExquisiteAppXML loadXML(String xmlPath) throws IOException {
		System.out.println("Loading XML");
		XMLParser parser = new XMLParser(FileUtils.readFileToString(new File(xmlPath), "UTF-8"));
		parser.parse();
		return parser.getExquisiteAppXML();
	}
	*/
	
	private void writeXML(ExquisiteAppXML exquisiteAppXML, String xmlPath) throws IOException {
		System.out.println("Writing xml");
		XMLWriter writer = new XMLWriter();
		FileUtils.writeStringToFile(new File(xmlPath), writer.writeXML(exquisiteAppXML), "UTF-8");
	}

	public static void main(String[] args) {
		try {
			ScenarioCreator scenarioCreator = new ScenarioCreator();
			scenarioCreator.createScenario("C:\\exquisite testing\\scenarios\\test.xml", "C:\\exquisite testing\\100testcases.xml", 1, 100, 0f, 0f, 1f);
			
			System.out.println("Finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
