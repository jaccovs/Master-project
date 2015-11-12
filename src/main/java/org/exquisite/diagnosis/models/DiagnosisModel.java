package org.exquisite.diagnosis.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.diagnosis.quickxplain.choco3.C3Runner;
import org.exquisite.diagnosis.quickxplain.choco3.FormulaInfo;
import org.exquisite.tools.Utilities;

import choco.cp.model.CPModel;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealVariable;

/**
 * Contains the knowledge base for constraint problems
 * @author Dietmar
 *
 */
public class DiagnosisModel {

	/**
	 * A copy constructor that copies all lists and links the pointers to non-changing information
	 * @param orig the original model to copy
	 */
	public DiagnosisModel(DiagnosisModel orig) {
		// only copy the
		this.constraintNames = new HashMap<Constraint, String>(orig.constraintNames);
		this.constraintWeights = new HashMap<Constraint, Integer>(orig.constraintWeights);
		this.correctStatements = new ArrayList<Constraint>(orig.correctStatements);
		this.negativeExamples = new ArrayList<Example>(orig.negativeExamples);
		this.positiveExamples = new ArrayList<Example>(orig.positiveExamples);
		this.notEntailedExamples = new ArrayList<Constraint>(orig.notEntailedExamples);
		this.possiblyFaultyStatements = new ArrayList<Constraint>(orig.possiblyFaultyStatements);
		this.certainlyFaultyStatements = new ArrayList<Constraint>(orig.certainlyFaultyStatements);
		this.variables = new ArrayList<Variable>(orig.variables);
		
		// copy the additional fields
		this.formulaInfoOfConstraints = new HashMap<Constraint, FormulaInfo>(orig.formulaInfoOfConstraints);
		this.graph = orig.graph;
		
		// copy more
		this.c3runners = new HashMap<Constraint, C3Runner>(orig.c3runners);
		this.c3examplerunners = new HashMap<Example, C3Runner>(orig.c3examplerunners);

	}
	
	/**
	 * Mapping the constraints to their original formulas
	 */
	Map<Constraint, FormulaInfo> formulaInfoOfConstraints = new HashMap<Constraint, FormulaInfo>();
	
	/**
	 * get the formula info of the constraints
	 * @return
	 */
	
	public Map<Constraint, FormulaInfo> getFormulaInfoOfConstraints() {
		return formulaInfoOfConstraints;
	}
	
	
	// Store the runners for the choco 3 constraints and the examples
	public Map<Constraint, C3Runner> c3runners = new HashMap<Constraint, C3Runner>();
	public Map<Example, C3Runner> c3examplerunners = new HashMap<Example, C3Runner>();
	
	

	/**
	 * The set of constraints which we assume to be always correct
	 */
	List<Constraint> correctStatements = new ArrayList<Constraint>();
	
	/**
	 * The set of the statements which could be faulty
	 */
	List<Constraint> possiblyFaultyStatements = new ArrayList<Constraint>();
	
	/**
	 * The set of the statements which are faulty for sure. These statements will be ignored durign tests.diagnosis process,
	 * but will afterwards be added to every found tests.diagnosis.
	 */
	List<Constraint> certainlyFaultyStatements = new ArrayList<Constraint>();
		
	/**
	 * The variables of the model
	 */
	List<Variable> variables = new ArrayList<Variable>();

	/**
	 * A map to store the variable names
	 * tests.dj: converted to linkedhashmap to preserve the order
	 */
	Map<Constraint, String> constraintNames = new LinkedHashMap<Constraint, String>();

	/**
	 * Weights of constraints
	 */
	Map<Constraint,Integer> constraintWeights = new HashMap<Constraint, Integer>();
	
	/**
	 * The positive examples
	 */
	List<Example> positiveExamples = new ArrayList<Example>();
	
	/**
	 * The negative examples
	 */
	List<Example> negativeExamples = new ArrayList<Example>();
	
	/**
	 * List of statements, that should not be entailed
	 */
	List<Constraint> notEntailedExamples = new ArrayList<Constraint>();
	
	/**
	 * A set of possible split points for the fault constraints
	 */
	List<Constraint> splitPoints = null;
	
	/**
	 * A pointer to the graph
	 */
	public ExquisiteGraph<String> graph = null;
	
	
	/**
	 * Adds an input value
	 * @param c
	 */
	public Constraint addTestcaseValue(Constraint c, String name) {
		this.constraintNames.put(c, name);
		return c;
	}
	

	/**
	 * Get the constraint name
	 * @param c the constraint 
	 * @return the name or null if the constraint does not exist
	 */
	public String getConstraintName(Constraint c) {
		return this.constraintNames.get(c);
	}	
	
	/**
	 * Returns a constraint from the model given a matching cell name.
	 * @param name
	 * @return a constraint or null if no match was found.
	 */
	public Constraint getConstraintByName(String name){
		return Utilities.getKeyByValue(this.constraintNames, name);
	}
	
	/**
	 * Add a correct constraint
	 * @param c
	 */
	public Constraint addCorrectConstraint(Constraint c, String name) {
		this.correctStatements.add(c);
		this.constraintNames.put(c, name);
		return c;
	}
	

	/**
	 * Add a possibly fault constraint with no weight value
	 * @param c the constraint
	 * @param name the name
	 * @return the constraint
	 */
	public Constraint addPossiblyFaultyConstraint(Constraint c, String name) { 
		return addPossiblyFaultyConstraint(c, name, 0);
	}

	/**
	 * Add a possibly faulty constraint
	 * @param c  the constraint
	 * @param name its name
	 * @param weight its importance (higher values are better)
	 * @return the constraint
	 */
	public Constraint addPossiblyFaultyConstraint(Constraint c, String name, int weight) {
		
		this.possiblyFaultyStatements.add(c);
		this.constraintNames.put(c, name);
		this.constraintWeights.put(c, weight);
		
		return c;
	}
	
	/**
	 * Add a decision variable
	 * @param v
	 */
	public IntegerVariable addIntegerVariable(IntegerVariable v) {
		this.variables.add(v);
		return v;
	}
	/**
	 * Add a real valued decision variable
	 * @param v
	 */
	public RealVariable addRealVariable(RealVariable v) {
		this.variables.add(v);
		return v;
	}
	
	
	/**
	 * Creates an empty constraint model
	 */
	public DiagnosisModel() {
		super();
	}

	/**
	 * Getter for constraint names map.
	 * @return
	 */
	public Map<Constraint, String> getConstraintNames() {
		return constraintNames;
	}
	
	/**
	 * Getter for the correct statements
	 * @return
	 */
	public List<Constraint> getCorrectStatements() {
		return correctStatements;
	}

	/**
	 * Sets the correct statements
	 * @param correctStatements
	 */
	public void setCorrectStatements(List<Constraint> correctStatements) {
		this.correctStatements = correctStatements;
	}

	/**
	 * Getter for the possibly faulty statements
	 * @return
	 */
	public List<Constraint> getPossiblyFaultyStatements() {
		return possiblyFaultyStatements;
	}

	/**
	 * Setter for the possibly faulty statements
	 * @param possiblyFaultyStatements
	 */
	public void setPossiblyFaultyStatements(
			List<Constraint> possiblyFaultyStatements) {
		this.possiblyFaultyStatements = possiblyFaultyStatements;
	}
	
	/**
	 * Getter for the certainly faulty statements
	 * @return
	 */
	public List<Constraint> getCertainlyFaultyStatements() {
		return certainlyFaultyStatements;
	}
	
	/**
	 * Setter for the certainly faulty statements
	 * @param certainlyFaultyStatements
	 */
	public void setCertainlyFaultyStatements(
			List<Constraint> certainlyFaultyStatements) {
		this.certainlyFaultyStatements = certainlyFaultyStatements;
	}


	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}
	
	/**
	 * A method that reorders the constraints by weight (highest weights, preferred elements,  first)
	 */
	public void sortPossiblyFaultyConstraintsByWeight() {
		Map<Constraint,Integer> sortedConstraints = Utilities.sortByValueDescending(constraintWeights);
		this.possiblyFaultyStatements = new ArrayList<Constraint> (sortedConstraints.keySet());
	}
	
	/**
	 * A method that randomizes the order of the possibly faulty constraints for experiments..
	 */
	public void shufflePossiblyFaulyConstraints() {
		Collections.shuffle(this.possiblyFaultyStatements);
	}


	/**
	 * A method to remove constraints from possibly faulty collection, also updates constraintWeights hashmap
	 * at the same time - otherwise when sortPossiblyFaultyConstraintsByWeight is called, it can load in the
	 * old constraints again!
	 * @param constraintsToIgnore : the list of constraints that should be ignored during this tests.diagnosis iteration.
	 */
	public void removeConstraintsToIgnore(List<Constraint> constraintsToIgnore)
	{
		// DJ remove the formula infos
		// TS FormulaInfos must not be removed here, as the formulaInfos are also needed for correct constraints
		// Other possibility would be to readd them, when correct constraints are added
//		for (Constraint c : constraintsToIgnore) {
//			this.getFormulaInfoOfConstraints().remove(c);
//
//		}
		
		this.possiblyFaultyStatements.removeAll(constraintsToIgnore);
		for (int i = 0; i < constraintsToIgnore.size(); i++) 
		{		
			this.constraintWeights.remove(constraintsToIgnore.get(i));
		}
	}
	
	/**
	 * returns the positive examples
	 * @return
	 */
	public List<Example> getPositiveExamples() {
		return positiveExamples;
	}


	/**
	 * Setter for the positive examples
	 * @param positiveExamples
	 */
	public void setPositiveExamples(List<Example> positiveExamples) {
		this.positiveExamples = positiveExamples;
	}

	/**
	 * Setter for the negative examples
	 * @return
	 */
	public List<Example> getNegativeExamples() {
		return negativeExamples;
	}


	public void setNegativeExamples(List<Example> negativeExamples) {
		this.negativeExamples = negativeExamples;
	}
	
	public List<Constraint> getNotEntailedExamples() {
		return notEntailedExamples;
	}
	
	public void setNotEntailedExamples(List<Constraint> notEntailedExamples) {
		this.notEntailedExamples = notEntailedExamples;
	}
	

	/**
	 * copies the variables to a given cp model
	 */
	public void copyVariablesToCPModel(CPModel cpmodel) {
		// copy the variables
		for (Variable v : this.getVariables()) {
			cpmodel.addVariable(v);
		}
	}


	/**
	 * Remember the split points 
	 * @param splitPoints
	 */
	public void setSplitPoints(List<Constraint> splitPoints) {
		this.splitPoints = splitPoints;
	}


	/**
	 * Returns the split points or null
	 * @return
	 */
	public List<Constraint> getSplitPoints() {
		return splitPoints;
	}
	
	
	
	

}
