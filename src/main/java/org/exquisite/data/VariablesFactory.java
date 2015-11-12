package org.exquisite.data;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.exquisite.datamodel.ExquisiteValueBound;
import org.exquisite.tools.StringUtilities;
import org.exquisite.tools.Utilities;

import choco.Choco;
import choco.Options;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * This class is responsible for making and configuring the decision variables that are added to
 * the model of the constraint solver.<p>
 * Currently the constraint solver being used in Exquisite is Choco.
 * Due to various other limitations, only Integer variables are supported in Exquisite at the moment.
 * 
 * @author David
 * @see org.exquisite.data.DiagnosisModelLoader
 */
public class VariablesFactory {

	//A collection of IntegerExpressionVariable objects that are created for the model to use.
	private Dictionary<String, IntegerExpressionVariable> variablesMap;
	
	/**
	 * The constructor takes a Dictionary that maps a cell reference to the corresponding integer variable.<p>
	 * In production you would normally pass in an empty (new) Dictionary. However you could also test
	 * various aspects of the class with a pre-populated Dictionary.
	 * 
	 * @param variablesMap
	 */
	public VariablesFactory(Dictionary<String, IntegerExpressionVariable> variablesMap){
		this.variablesMap = variablesMap;
	}
	
	/**
	 * @return a Dictionary containing all the variables that have been created so far.
	 */
	public Dictionary<String, IntegerExpressionVariable> getVariablesMap(){
		return variablesMap;
	}	
	
	/**
	 * Creates model variables that have a user specified, global, value range.
	 * @param appXML
	 * @return a list of IntegerVariables with custom min/max bounds.
	 */
	public List<IntegerVariable> makeVariablesWithGlobalValueBounds(Dictionary<String, ExquisiteValueBound> valueBounds, Dictionary<String, List<String>> cellsInRange){	
		List<IntegerVariable> result = new ArrayList<IntegerVariable>();		
		
		//iterate through collection creating an integer value with upper and lower bounds as specified by the valueBound object.
		for (Enumeration<String> keys = valueBounds.keys(); keys.hasMoreElements();){			
			String cellReference = keys.nextElement();
			ExquisiteValueBound valueBound = valueBounds.get(cellReference);
			
			//check if the cell reference referees to a single cell or a range of cells.
			if (Utilities.isCellRangeReference(cellReference)){	
				List<String> cells = cellsInRange.get(cellReference);
				for (String cellName : cells){					
					//Check if the variable has already been constructed (could have happend if a global value bound had been specified).
					if (!variableAlreadyExists(cellName)){
						result.add(makeIntegerVariable(cellName, (int)valueBound.getLower(), (int)valueBound.getUpper()));
					}
				}								
			}
			else{				
				//if it is just a single variable to make
				if (!variableAlreadyExists(cellReference)){
					result.add(makeIntegerVariable(cellReference, (int)valueBound.getLower(), (int)valueBound.getUpper()));
				}
			}
		}
		return result;
	}
		
	/**
	 * Creates variables for the solver model from xml	
	 * @param node
	 * @param model
	 * @param min
	 * @param max
	 * containing formulae as needed when processing the test case data. 
	 * TODO if variable has custom defined min/max values then use these instead of defaults.
	 * 
	 */	
	public List<IntegerVariable> makeVariables(List<String> variableNames, int min, int max)
	{
		List<IntegerVariable> result = new ArrayList<IntegerVariable>();
		
		for(String variableName : variableNames)
		{			
			//check if variableName refers to a range of variables...
			boolean isRange = Utilities.isCellRangeReference(variableName);
			if (isRange)
			{
				List<String> variablesInRange = StringUtilities.rangeToCells(variableName);
				for(String variableInRange : variablesInRange)
				{
					if(!variableAlreadyExists(variableInRange))
					{
						result.add(makeIntegerVariable(variableInRange, min, max));			
					}
				}
			}
			else //otherwise create individual variable.
			{
				if(!variableAlreadyExists(variableName))
				{
					result.add(makeIntegerVariable(variableName, min, max));			
				}
			}
		}
		return result;
	}	
	
	/**
	 * Checks if a decision variable of a given name has already been constructed.
	 * This situation arises if any variables have been defined with a global value bound.
	 * Which results in them being created first.
	 * This test is used to avoid creating duplicate variables.
	 * 
	 * @return true if decision variable already exists.
	 * @return false if decision variable does not yet exist. 
	 */
	public boolean variableAlreadyExists(String variableName){
		return variablesMap.get(variableName) != null;
	}	
	
	/**
	 * Make a Choco integer variable to be added to the solver model.
	 * @param variableName - name to be given to model variable.
	 * @param min - minimum integer value the variable will store.
	 * @param max - maximum integer value the variable will store.
	 * @return Choco IntegerVariable
	 */
	public IntegerVariable makeIntegerVariable(String variableName, int min, int max){	
		IntegerVariable variable = Choco.makeIntVar(variableName, min, max, Options.V_BOUND);
		variablesMap.put(variableName, variable);	
		return variable;				
	}		
}
