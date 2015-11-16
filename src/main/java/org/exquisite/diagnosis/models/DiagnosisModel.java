package org.exquisite.diagnosis.models;

import choco.cp.model.CPModel;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealVariable;
import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.diagnosis.quickxplain.choco3.C3Runner;
import org.exquisite.diagnosis.quickxplain.choco3.FormulaInfo;
import org.exquisite.tools.Utilities;

import java.util.*;

/**
 * Contains the knowledge base for constraint problems
 *
 * @author Dietmar
 */
public class DiagnosisModel<T> {

    // Store the runners for the choco 3 constraints and the examples
    public Map<T, C3Runner> c3runners = new HashMap<T, C3Runner>();
    public Map<Example, C3Runner> c3examplerunners = new HashMap<Example, C3Runner>();
    /**
     * A pointer to the graph
     */
    public ExquisiteGraph<String> graph = null;
    /**
     * Mapping the constraints to their original formulas
     */
    Map<T, FormulaInfo> formulaInfoOfConstraints = new HashMap<T, FormulaInfo>();
    /**
     * The set of constraints which we assume to be always correct
     */
    List<T> correctStatements = new ArrayList<T>();
    /**
     * The set of the statements which could be faulty
     */
    List<T> possiblyFaultyStatements = new ArrayList<T>();
    /**
     * The set of the statements which are faulty for sure. These statements will be ignored durign tests.diagnosis process,
     * but will afterwards be added to every found tests.diagnosis.
     */
    List<T> certainlyFaultyStatements = new ArrayList<T>();
    /**
     * The variables of the model
     */
    List<Variable> variables = new ArrayList<Variable>();
    /**
     * A map to store the variable names
     * tests.dj: converted to linkedhashmap to preserve the order
     */
    Map<T, String> constraintNames = new LinkedHashMap<T, String>();
    /**
     * Weights of constraints
     */
    Map<T, Integer> constraintWeights = new HashMap<T, Integer>();
    /**
     * The positive examples
     */
    List<Example<T>> positiveExamples = new ArrayList<>();
    /**
     * The negative examples
     */
    List<Example<T>> negativeExamples = new ArrayList<>();
    /**
     * List of statements, that should not be entailed
     */
    List<T> notEntailedExamples = new ArrayList<T>();
    /**
     * A set of possible split points for the fault constraints
     */
    List<T> splitPoints = null;

    /**
     * A copy constructor that copies all lists and links the pointers to non-changing information
     *
     * @param orig the original model to copy
     */
    public DiagnosisModel(DiagnosisModel<T> orig) {
        // only copy the
        this.constraintNames = new HashMap<>(orig.constraintNames);
        this.constraintWeights = new HashMap<>(orig.constraintWeights);
        this.correctStatements = new ArrayList<>(orig.correctStatements);
        this.negativeExamples = new ArrayList<>(orig.negativeExamples);
        this.positiveExamples = new ArrayList<>(orig.positiveExamples);
        this.notEntailedExamples = new ArrayList<>(orig.notEntailedExamples);
        this.possiblyFaultyStatements = new ArrayList<>(orig.possiblyFaultyStatements);
        this.certainlyFaultyStatements = new ArrayList<>(orig.certainlyFaultyStatements);
        this.variables = new ArrayList<>(orig.variables);

        // copy the additional fields
        this.formulaInfoOfConstraints = new HashMap<T, FormulaInfo>(orig.formulaInfoOfConstraints);
        this.graph = orig.graph;

        // copy more
        this.c3runners = new HashMap<T, C3Runner>(orig.c3runners);
        this.c3examplerunners = new HashMap<Example, C3Runner>(orig.c3examplerunners);

    }

    /**
     * Creates an empty constraint model
     */
    public DiagnosisModel() {
        super();
    }

    /**
     * get the formula info of the constraints
     *
     * @return
     */

    public Map<T, FormulaInfo> getFormulaInfoOfConstraints() {
        return formulaInfoOfConstraints;
    }

    /**
     * Adds an input value
     *
     * @param c
     */
    public T addTestcaseValue(T c, String name) {
        this.constraintNames.put(c, name);
        return c;
    }

    /**
     * Get the constraint name
     *
     * @param c the constraint
     * @return the name or null if the constraint does not exist
     */
    public String getConstraintName(T c) {
        return this.constraintNames.get(c);
    }

    /**
     * Returns a constraint from the model given a matching cell name.
     *
     * @param name
     * @return a constraint or null if no match was found.
     */
    public T getConstraintByName(String name) {
        return Utilities.getKeyByValue(this.constraintNames, name);
    }

    /**
     * Add a correct constraint
     *
     * @param c
     */
    public T addCorrectConstraint(T c, String name) {
        this.correctStatements.add(c);
        this.constraintNames.put(c, name);
        return c;
    }

    /**
     * Add a possibly fault constraint with no weight value
     *
     * @param c    the constraint
     * @param name the name
     * @return the constraint
     */
    public T addPossiblyFaultyConstraint(T c, String name) {
        return addPossiblyFaultyConstraint(c, name, 0);
    }

    /**
     * Add a possibly faulty constraint
     *
     * @param c      the constraint
     * @param name   its name
     * @param weight its importance (higher values are better)
     * @return the constraint
     */
    public T addPossiblyFaultyConstraint(T c, String name, int weight) {

        this.possiblyFaultyStatements.add(c);
        this.constraintNames.put(c, name);
        this.constraintWeights.put(c, weight);

        return c;
    }

    /**
     * Add a decision variable
     *
     * @param v
     */
    public IntegerVariable addIntegerVariable(IntegerVariable v) {
        this.variables.add(v);
        return v;
    }

    /**
     * Add a real valued decision variable
     *
     * @param v
     */
    public RealVariable addRealVariable(RealVariable v) {
        this.variables.add(v);
        return v;
    }

    /**
     * Getter for constraint names map.
     *
     * @return
     */
    public Map<T, String> getConstraintNames() {
        return constraintNames;
    }

    /**
     * Getter for the correct statements
     *
     * @return
     */
    public List<T> getCorrectStatements() {
        return correctStatements;
    }

    /**
     * Sets the correct statements
     *
     * @param correctStatements
     */
    public void setCorrectStatements(List<T> correctStatements) {
        this.correctStatements = correctStatements;
    }

    /**
     * Getter for the possibly faulty statements
     *
     * @return
     */
    public List<T> getPossiblyFaultyStatements() {
        return possiblyFaultyStatements;
    }

    /**
     * Setter for the possibly faulty statements
     *
     * @param possiblyFaultyStatements
     */
    public void setPossiblyFaultyStatements(
            List<T> possiblyFaultyStatements) {
        this.possiblyFaultyStatements = possiblyFaultyStatements;
    }

    /**
     * Getter for the certainly faulty statements
     *
     * @return
     */
    public List<T> getCertainlyFaultyStatements() {
        return certainlyFaultyStatements;
    }

    /**
     * Setter for the certainly faulty statements
     *
     * @param certainlyFaultyStatements
     */
    public void setCertainlyFaultyStatements(
            List<T> certainlyFaultyStatements) {
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
        Map<T, Integer> sortedConstraints = Utilities.sortByValueDescending(constraintWeights);
        this.possiblyFaultyStatements = new ArrayList<T>(sortedConstraints.keySet());
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
     *
     * @param constraintsToIgnore : the list of constraints that should be ignored during this tests.diagnosis iteration.
     */
    public void removeConstraintsToIgnore(List<T> constraintsToIgnore) {
        // DJ remove the formula infos
        // TS FormulaInfos must not be removed here, as the formulaInfos are also needed for correct constraints
        // Other possibility would be to readd them, when correct constraints are added
//		for (T c : constraintsToIgnore) {
//			this.getFormulaInfoOfConstraints().remove(c);
//
//		}

        this.possiblyFaultyStatements.removeAll(constraintsToIgnore);
        for (int i = 0; i < constraintsToIgnore.size(); i++) {
            this.constraintWeights.remove(constraintsToIgnore.get(i));
        }
    }

    /**
     * returns the positive examples
     *
     * @return
     */
    public List<Example<T>> getPositiveExamples() {
        return positiveExamples;
    }


    /**
     * Setter for the positive examples
     *
     * @param positiveExamples
     */
    public void setPositiveExamples(List<Example<T>> positiveExamples) {
        this.positiveExamples = positiveExamples;
    }

    /**
     * Setter for the negative examples
     *
     * @return
     */
    public List<Example<T>> getNegativeExamples() {
        return negativeExamples;
    }


    public void setNegativeExamples(List<Example<T>> negativeExamples) {
        this.negativeExamples = negativeExamples;
    }

    public List<T> getNotEntailedExamples() {
        return notEntailedExamples;
    }

    public void setNotEntailedExamples(List<T> notEntailedExamples) {
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
     * Returns the split points or null
     *
     * @return
     */
    public List<T> getSplitPoints() {
        return splitPoints;
    }

    /**
     * Remember the split points
     *
     * @param splitPoints
     */
    public void setSplitPoints(List<T> splitPoints) {
        this.splitPoints = splitPoints;
    }


}
