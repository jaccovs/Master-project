package evaluations.mutation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.exquisite.data.ConstraintsFactory;
import org.exquisite.data.DiagnosisModelLoader;
import org.exquisite.data.VariablesFactory;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;
import org.exquisite.xml.XMLParser;

import choco.kernel.model.variables.integer.IntegerExpressionVariable;

/**
 * Where are the comments, David?
 * @author dietmar
 *
 */
public class Mutation {

	private ExquisiteAppXML exquisiteAppXML;
	
	private List<String> mutatationDescriptions = new ArrayList<String>();//records names of cells whose content was changed during last call to mutate.
	
	
	public Mutation(ExquisiteAppXML exquisiteAppXML)
	{			
		this.exquisiteAppXML = exquisiteAppXML;		
	}
		
	public Mutation(String xmlFilePath)
	{		
		this.exquisiteAppXML = this.makeAppXML(xmlFilePath);
	}	
	
	public static void main(String[] args) {
		String xmlFilePath = "C:\\Users\\David\\workspace\\exquisite-service\\src\\tests\\exquisite\\data\\mappe1.xml";
		Mutation mutation = new Mutation(xmlFilePath);
		//mutation.mutate(1, 1);
		//mutation.run();
		ArrayList<String> supportedArithmeticOperators = new ArrayList<String>();
		supportedArithmeticOperators.add("*"); 
		supportedArithmeticOperators.add("+");
		supportedArithmeticOperators.add("-");	
		
		String testFormula = "S4*C4";
		String mutant = mutation.mutateFormula(testFormula, 1, supportedArithmeticOperators);
		
		System.out.println(mutant);
		//mutant = mutation.mutateFormula(testFormula, 2, supportedArithmeticOperators);
		//mutant = mutation.mutateFormula(testFormula, 3, supportedArithmeticOperators);		
	}
	
	public void run()
	{		
		ExquisiteSession sessionData = new ExquisiteSession(this.exquisiteAppXML);
		ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
		Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
		VariablesFactory varFactory = new VariablesFactory(variablesMap);
		DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
		modelLoader.loadDiagnosisModelFromXML();	
		
		IDiagnosisEngine engine = EngineFactory.makeDAGEngineStandardQx(sessionData);
		
		try{		
			List<Diagnosis> diagnoses = engine.calculateDiagnoses();
			System.out.println("Found " + diagnoses.size() + " diagnoses");
			for (int i = 0; i < diagnoses.size(); i++) 
			{
				System.out.println("-- Diagnosis #" + i);
				System.out.println("    " + Utilities.printConstraintList(diagnoses.get(i).getElements(), sessionData.diagnosisModel));
				System.out.println("--");
			}
		} catch (NegativeArraySizeException e){
			System.out.println("Negative array size exception caught from Choco, because of inconsistent test case values. (Inconsistent background)");
		} catch (DiagnosisException e){
			System.out.println("DiagnosisException caught in Mutation.run().");
		}
		
	}	

	public String getMutatationDescriptions()
	{
		StringBuffer result = new StringBuffer();
		Iterator<String> it = mutatationDescriptions.iterator();
		while (it.hasNext()) {
			String var = it.next();
			result.append(var);
		}		
		return result.toString();
	}
	
	/**
	 * Returns one of the supported arithmetic operators at random to replace
	 * the operator specified in originalOperator.
	 * Trys again if random operator is the same as originalOperator.
	 * @param originalOperator - the operator to replace.
	 * @return a different operator.
	 */	
	public String getRandomOperator(String originalOperator, List<String> arithmeticOperators)
	{		
		int min = 0;
		int max = arithmeticOperators.size() - 1;		
		int index = Utilities.randomRange(min, max);//min + (int)(Math.random() * ((max - min) + 1));
	
		if (arithmeticOperators.contains(originalOperator) && arithmeticOperators.size() > 1)
		{
			while(index == arithmeticOperators.indexOf(originalOperator))
				index = Utilities.randomRange(min, max);
		}

		String newOperator = arithmeticOperators.get(index);
		return newOperator;
	}
	
	/**
	 * Mutates a specific cell formula.
	 * @param cellReference - the target cell with the formula to mutate.
	 * @param operatorCount - the number of operators in the formula to change.
	 */
	public void mutate(String cellReference, int operatorCount, List<String>arithmeticOperators)
	{
		String formula = exquisiteAppXML.getFormulas().get(cellReference);
		String mutatedFormula = mutateFormula(formula, operatorCount, arithmeticOperators);
		Debug.msg("Mutating " + cellReference + " from: " + formula + " to: " + mutatedFormula);
		replaceFormulaWithMutant(mutatedFormula, cellReference);		
	}
	
	/**
	 * Changes (mutates) formulae in a spreadsheet.
	 * @param mutationCount - the number of mutations to make.
	 * @param operatorCount - the number of operators in an equation to change.
	 */
	public List<String> mutate(int mutationCount, int operatorCount, List<String> arithmeticOperators)
	{
		mutationCount = (mutationCount > exquisiteAppXML.getFormulas().size()) ? exquisiteAppXML.getFormulas().size() : mutationCount;
		
		List<String> mutatedFormulas = new ArrayList<String>();

		Enumeration<String> formulaEnumerator = exquisiteAppXML.getFormulas().keys();
		Integer[] indices = Utilities.getRandomIndices(mutationCount, exquisiteAppXML.getFormulas().size());
		int i = 0;
		int elementCounter = 0;
		while(formulaEnumerator.hasMoreElements() && i < mutationCount)
		{			
			String key = formulaEnumerator.nextElement();

			if (indices[i] == elementCounter)
			{
				mutate(key, operatorCount, arithmeticOperators);
				mutatedFormulas.add(key);
				i++;
			}
			elementCounter++;
		}
		return mutatedFormulas;
	}
	
	
	public void replaceFormulaWithMutant(String mutatedFormula, String key)
	{
		String old = exquisiteAppXML.getFormulas().remove(key);
		exquisiteAppXML.getFormulas().put(key, mutatedFormula);	
		mutatationDescriptions.add("{cellName: " + key + " from: " + old + " to: " + mutatedFormula + "}" );
	}
	
	
	/**
	 * Changes some/all of the operators in an equation.
	 * @param targetFormula - the formula to mutate
	 * @param operatorCount - number of operators in the target formula to change.
	 * @param arithmeticOperators - operators that can be used in a mutation.
	 * @return - the mutated formula.
	 */
	public String mutateFormula(String targetFormula, int operatorCount, List<String> arithmeticOperators)
	{		
		String regex = "(?<=op)|(?=op)".replace("op", "[-+*/()]");
		String[] array = targetFormula.split(regex);
		String result = "";

		List<String> searchableOperators = new ArrayList<String>();
		searchableOperators.add("*");
		searchableOperators.add("+");
		searchableOperators.add("-");
		//System.out.println("Changing formula FROM: " + targetFormula);
		
		int numOperatorChanges = 0;
		for (int j = 0; j < array.length; j++) {			
			String formulaElement = array[j];
			if(searchableOperators.contains(formulaElement) && numOperatorChanges < operatorCount)
			{
				result+=getRandomOperator(formulaElement, arithmeticOperators);
				numOperatorChanges++;
			}
			else
			{
				result+=formulaElement;
			}								
		}		
		return result;
	}
	
	/**
	 * Load appXML from source xml file.
	 * @param xmlFilePath - the full path to the xml file to be read.
	 */
	public ExquisiteAppXML makeAppXML(String xmlFilePath)
	{	
		XMLParser xmlParser = new XMLParser();
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(xmlFilePath)));
			String line;
			StringBuilder sb = new StringBuilder();
	
			while((line=br.readLine())!= null){
			    sb.append(line.trim());
			}
			br.close();
			xmlParser.parse(sb.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return xmlParser.getExquisiteAppXML();
	}
}
