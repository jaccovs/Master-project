package evaluations.tools;

import java.io.IOException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.exquisite.data.ConstraintsFactory;
import org.exquisite.data.VariablesFactory;
import org.exquisite.datamodel.*;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.parser.FormulaParser;
import org.exquisite.tools.FileUtilities;
import org.exquisite.tools.Utilities;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class PositiveTestCaseGenerator 
{
	/**
	 * Target appXML
	 */
	private ExquisiteAppXML exquisiteAppXML;
	
	/**
	 * An internal solver
	 */
	CPSolver solver = new CPSolver();

	/**
	 * A constraint programming model
	 */
	CPModel cpmodel;
	
	/**
	 * For transforming formulae to Choco constraints.
	 */
	ConstraintsFactory conFactory;
	
	/**
	 * For generating Choco variables.
	 */
	VariablesFactory varFactory;
	
	String logfileName = "positiveTestCases.csv";
	
	//key: is test case instance,  (key is cell reference, value is calculated value)
	private Dictionary<TestCase, Dictionary<String, String>> formulaValues;//calculated values for formula cells retrived from solver.
	
	
	
	public Dictionary<TestCase, Dictionary<String, String>> getFormulaValues() {
		return formulaValues;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{		
		//ExquisiteAppXML appXml = SmallExquisiteAppXMLExample.getExample();
		

		final String appXMLFile = ".\\experiments\\enase-2013\\singleFault\\big.xml";		
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(appXMLFile);
		new PositiveTestCaseGenerator(appXML, new Random()).generateTestCases(1, false);
		
	}
	
	public PositiveTestCaseGenerator(ExquisiteAppXML exquisiteAppXML, Random random)
	{
		this.exquisiteAppXML = exquisiteAppXML;
		
		
		ExcelExquisiteSession sessionData = new ExcelExquisiteSession(this.exquisiteAppXML);
		this.conFactory = new ConstraintsFactory(sessionData);
		Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
		this.varFactory = new VariablesFactory(variablesMap);
		/*
		DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
		modelLoader.loadDiagnosisModelFromXML();	
		*/
		// TODO ... cleanup...
		
		this.formulaValues = new Hashtable<TestCase, Dictionary<String, String>>();
	}
	
	/**
	 * Generates test cases for a given model.
	 * @param testCaseCount - number of test cases to generate.
	 */
	public Dictionary<String, TestCase> generateTestCases(int testCaseCount, boolean originalValueBounds)
	{
		System.out.println("Generating [" + testCaseCount + "] test cases.");
		Double globalMin = exquisiteAppXML.getDefaultValueBound().getLower();
		Double globalMax = exquisiteAppXML.getDefaultValueBound().getUpper();
		
		Dictionary<String, TestCase> testCaseCollection = new Hashtable<String, TestCase>();
		for (int i = 0; i < testCaseCount; i++) 
		{	
			
			boolean isValidTestCase = false;
			//make new test case instance
			TestCase testCase = new TestCase();		
			
			while(!isValidTestCase)
			{				
				Dictionary<String, ExquisiteValueBound> valueBounds = new Hashtable<String, ExquisiteValueBound>();
							
				//inputs
				List<String> inputs = exquisiteAppXML.getInputs();
				Dictionary<String, String> testCaseValues = new Hashtable<String, String>();
				for (String input : inputs) 
				{
					int minValueBound;
					int maxValueBound;
					
					ExquisiteValueBound valueBound = findValueBound(input);
					if (originalValueBounds)
					{
						if (valueBound != null)
						{
							minValueBound = (int)valueBound.getLower();
							maxValueBound = (int)valueBound.getUpper();
						}
						else
						{
							//set new value bounds based on global defaults
							minValueBound = globalMin.intValue();
							maxValueBound = globalMax.intValue();
						}
					}
					else
					{
						if(valueBound != null)
						{
							minValueBound = generateRandomIntValue((int)valueBound.getLower(), (int)valueBound.getUpper() / 4);
							maxValueBound = generateRandomIntValue(minValueBound, (int)(valueBound.getUpper() * 0.75f));
						}
						else
						{
							//set new value bounds based on global defaults
							minValueBound = generateRandomIntValue(globalMin.intValue(), globalMax.intValue() / 4);
							maxValueBound = generateRandomIntValue(minValueBound, (int)(globalMax.intValue() * 0.75f));
						}
					}
					
					valueBounds.put(input, new ExquisiteValueBound(minValueBound, maxValueBound, 0.1));
					int inputValue = generateRandomIntValue(minValueBound, maxValueBound);
					testCaseValues.put(input, String.valueOf(inputValue));
				}
				testCase.setValueBounds(valueBounds);
				testCase.setValues(testCaseValues);				
							
				List<String> formulas = Collections.list(exquisiteAppXML.getFormulas().keys());
				isValidTestCase = collectFormulaValues(testCase, varFactory.getVariablesMap(), testCaseValues, formulas);

			}
			System.out.println("valid test case generated.");
			testCaseCollection.put("testCase" + i, testCase);
			try {
				FileUtilities.writeToFile(testCase.toCSV(exquisiteAppXML), ".\\logs\\positiveTestCases.txt", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		exquisiteAppXML.setTestCases(testCaseCollection);
		return testCaseCollection;
	}
	
	
	public ExquisiteValueBound findValueBound(String targetCellName)
	{
		//iterate through collection creating an integer value with upper and lower bounds as specified by the valueBound object.
		Dictionary<String, ExquisiteValueBound> valueBounds = exquisiteAppXML.getValueBounds();
		Dictionary<String, List<String>> cellsInRange = exquisiteAppXML.getCellsInRange();
		
		for (Enumeration<String> keys = valueBounds.keys(); keys.hasMoreElements();)     
		{			
			String cellReference = keys.nextElement();
			ExquisiteValueBound valueBound = valueBounds.get(cellReference);
			
			//check if the cell reference refers to a single cell or a range of cells.
			if (Utilities.isCellRangeReference(cellReference))
			{	
				List<String> cells = cellsInRange.get(cellReference);
				if (cells.contains(targetCellName))
				return valueBound;							
			}
			else
			{				
				if (cellReference.equalsIgnoreCase(targetCellName))
				{
					return valueBound;
				}
			}
		}
		return null;
	}
		
	
	/**
	 * Generates a random number between the values specified by min and max
	 * @param min - min value of random number.
	 * @param max - max value of random number.
	 * @return a random number from between the range of min and max
	 */
	public int generateRandomIntValue(int min, int max)
	{
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	private void buildCPModel()
	{
		List<IntegerVariable> varsWithGlobalValueBounds = varFactory.makeVariablesWithGlobalValueBounds(exquisiteAppXML.getValueBounds(), exquisiteAppXML.getCellsInRange());
			
		ExquisiteValueBound defaultValueBound = exquisiteAppXML.getDefaultValueBound();
		int min = (int)defaultValueBound.getLower();
		int max = (int)defaultValueBound.getUpper();			
		
		//Make the variables
		List<IntegerVariable> inputVariables = varFactory.makeVariables(exquisiteAppXML.getInputs(), min, max);
		//interim cells
		List<IntegerVariable> interimVariables = varFactory.makeVariables(exquisiteAppXML.getInterims(), min, max);
		//output cells
		List<IntegerVariable> outputVariables = varFactory.makeVariables(exquisiteAppXML.getOutputs(), min, max);
		
		
		ExquisiteGraph<String> graph = new ExquisiteGraph<String>();
		exquisiteAppXML.buildGraph(graph);
		
		//constraint representations of the spreadsheet formulae.");		
		FormulaParser formulaParser = new FormulaParser(graph);
		Dictionary<String, Constraint> formulae = conFactory.makeFormulae(exquisiteAppXML.getFormulas(), formulaParser, varFactory.getVariablesMap());
		
		cpmodel = new CPModel();
		this.addVariables(varsWithGlobalValueBounds);
		this.addVariables(inputVariables);
		this.addVariables(interimVariables);
		this.addVariables(outputVariables);		
		this.addConstraints(formulae.elements());		
	}
	
	private void addVariables(List<IntegerVariable> variables)
	{
		choco.kernel.model.variables.Variable[] variablesToAdd = new IntegerVariable[variables.size()];
		int index=0;
		for(choco.kernel.model.variables.Variable variable : variables)
		{
			variablesToAdd[index] = variable;
			index++;
		}
		cpmodel.addVariables(variablesToAdd);
	}
	
	private void addConstraints(Enumeration<Constraint> constraints)
	{
		while(constraints.hasMoreElements())
		{
			cpmodel.addConstraint(constraints.nextElement());
		}
	}
	
	/*
	 * Collects values for all formula cells (interims & outputs).
	 */
	private boolean collectFormulaValues(TestCase targetTestCase, Dictionary<String, IntegerExpressionVariable> variablesMap, Dictionary<String, String> inputValues, List<String> formulas)
	{
		solver = new CPSolver();
		buildCPModel();
		makeInputValueConstraints(targetTestCase, variablesMap, inputValues);
//		System.out.println("----- Reading MODEL!!");
		solver.read(cpmodel);
//		System.out.println("----- Start solving...");
		solver.solve();		
//		System.out.println("----- Finish solving...");
		boolean isFeasible = solver.isFeasible();
		
//		System.out.println("IS FEASIBLE? " + isConsistent);
		//System.out.println(solver.pretty());
		
		Iterator<IntDomainVar> it = solver.getIntVarIterator();
		Dictionary<String, String> calculatedFormulaValues = new Hashtable<String, String>();
		while (it.hasNext()) {
			IntDomainVar var = it.next();
			if (formulas.contains(var.getName()))
			{				
//				System.out.println("expected output for " + var.getName() + " = " + var.getVal());
				calculatedFormulaValues.put(var.getName(), String.valueOf(var.getVal()));								
			}			
		}
		this.formulaValues.put(targetTestCase, calculatedFormulaValues);
		//targetTestCase.setFaultyValues(faultyValues);
		
		return isFeasible;				
	}
	
	private void makeInputValueConstraints(TestCase targetTestCase, Dictionary<String, IntegerExpressionVariable> variablesMap, Dictionary<String, String>  inputCells)
	{
		for (Enumeration<String> keys = targetTestCase.getValues().keys(); keys.hasMoreElements();)     
		{			
			String cellReference = keys.nextElement();
			if (inputCells.get(cellReference) != null)
			{				
				String cellValue = targetTestCase.getValues().get(cellReference);
				IntegerExpressionVariable variable = variablesMap.get(cellReference);
				int intValue = Integer.parseInt(cellValue);	
//				System.out.println("Input: " + variable.getName() + " [" + variable.getLowB() + ".." + variable.getUppB() + "] = " + cellValue);
				Constraint constraint = Choco.eq(variable, intValue);
				cpmodel.addConstraint(constraint);
			}
		}
	}
}
