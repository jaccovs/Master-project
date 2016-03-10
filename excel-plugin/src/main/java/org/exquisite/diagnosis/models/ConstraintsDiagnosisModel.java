package org.exquisite.diagnosis.models;

import choco.cp.model.CPModel;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealVariable;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.diagnosis.quickxplain.choco3.C3Runner;
import org.exquisite.diagnosis.quickxplain.choco3.FormulaInfo;
import org.exquisite.tools.Utilities;

import java.util.*;

/**
 * A variant of the diagnosis model that stores additional information about constraints that are in the model
 */
public class ConstraintsDiagnosisModel<T> extends DiagnosisModel<T> {


    // Store the runners for the choco 3 constraints and the examples
    public Map<T, C3Runner> c3runners = new HashMap<>();
    public Map<Example, C3Runner> c3examplerunners = new HashMap<>();
    /**
     * A pointer to the graph
     */
    public ExquisiteGraph<String> graph = null;
    /**
     * Mapping the constraints to their original formulas
     */
    Map<T, FormulaInfo> formulaInfoOfConstraints = new HashMap<T, FormulaInfo>();
    /**
     * The variables of the model
     */
    List<Variable> variables = new ArrayList<Variable>();
    /**
     * A map to store the variable names
     * tests.dj: converted to linkedhashmap to preserve the order
     */
    Map<T, String> constraintNames = new LinkedHashMap<T, String>();


    public ConstraintsDiagnosisModel() {
    }

    public ConstraintsDiagnosisModel(ConstraintsDiagnosisModel<T> orig) {
        super(orig);
        this.constraintNames = new HashMap<>(orig.constraintNames);
        this.variables = new ArrayList<>(orig.variables);

        // copy the additional fields
        this.formulaInfoOfConstraints = new HashMap<T, FormulaInfo>(orig.formulaInfoOfConstraints);
        this.graph = orig.graph;

        // copy more
        this.c3runners = new HashMap<T, C3Runner>(orig.c3runners);
        this.c3examplerunners = new HashMap<Example, C3Runner>(orig.c3examplerunners);

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
    public T addCorrectFormula(T c, String name) {
        addCorrectFormula(c);
        this.constraintNames.put(c, name);
        return c;
    }

    /**
     * A method that reorders the constraints by weight (highest weights, preferred elements,  first)
     */
    public void sortPossiblyFaultyConstraintsByWeight() {
        Map<T, Float> sortedConstraints = Utilities.sortByValueDescending(getStatementWeights());
        setPossiblyFaultyStatements(new ArrayList<T>(sortedConstraints.keySet()));
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

        getPossiblyFaultyStatements().add(c);
        this.constraintNames.put(c, name);
        getStatementWeights().put(c, (float) weight);

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


    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
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
     * A method to remove constraints from possibly faulty collection, also updates statementWeights hashmap
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

        getPossiblyFaultyStatements().removeAll(constraintsToIgnore);
        for (T aConstraintsToIgnore : constraintsToIgnore) {
            getStatementWeights().remove(aConstraintsToIgnore);
        }
    }
}
