package org.exquisite.diagnosis.models;

import org.exquisite.datamodel.ExquisiteEnums.ExampleConstraintValueTypes;
import org.exquisite.diagnosis.quickxplain.choco3.FormulaInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An  class representing an example
 */
public class Example<Formula> {

    /**
     * A handle to the constraints
     */
    public List<Formula> constraints = new ArrayList<>();
    /**
     * The names
     */
    public Map<Formula, String> constraintNames = new HashMap<>();
    /**
     * Remember the formula info for choco3 - will have to add the constraints
     * later on during parsing
     */
    public Map<Formula, FormulaInfo> choco3FormulaInfos = new HashMap<>();
    /**
     * Constraints that are for this example, essentially redundant. i.e. they are independent
     * of the constraints that are to be tested (considered faulty).
     */
    public Map<String, Formula> irrelevantConstraints = new HashMap<>();
    boolean isNegative = false;
    /**
     * Groups constraints into a map, indexed by the kind of constraint they are i.e.
     * an input value, expected value or correct value constraint.
     */
    private Map<ExampleConstraintValueTypes, Map<String, Formula>> constraintTypeIndex = new HashMap<>();


    /**
     * To construct an example
     *
     * @param negative
     */
    public Example(boolean negative) {
        if (negative) {
            isNegative = true;
        }
    }

    /**
     * Default: A positive example
     */
    public Example() {

    }

    /**
     * Adds a constraint and stores the name
     *
     * @param c    the constraint
     * @param name and its name
     */
    public void addConstraint(Formula c, String name) {
        this.constraintNames.put(c, name);
        this.constraints.add(c);
    }

    /**
     * like addConstraint but also indexes the constraint by the type of value the constraint represents.
     * This method is uses in ConstraintsFactory.makeExamplesFromTestCase()
     *
     * @param c    the constraint
     * @param name and its name
     */
    public void addConstraint(Formula c, String name, String cellReference, ExampleConstraintValueTypes descriptor) {
        addConstraint(c, name);
        //initialises a map if none is present for that particular descriptor.
        if (!this.constraintTypeIndex.containsKey(descriptor)) {
            this.constraintTypeIndex.put(descriptor, new HashMap<String, Formula>());
        }
        this.constraintTypeIndex.get(descriptor).put(cellReference, c);
    }

    /**
     * Returns a map of constraints and their names grouped by the type of value they represent. e.g.
     * input value, expected output value or correct value.
     * <p>
     * e.g. to find all input values: getConstraintsGrouped(ExampleConstraintValueTypes.InputValue)
     * expected values: getConstraintsGrouped(ExampleConstraintValueTypes.ExpectedValue)
     * correct values: getConstraintsGrouped(ExampleConstraintValueTypes.CorrectValue)
     *
     * @param descriptor
     * @return
     */
    public Map<String, Formula> getConstraintsGrouped(ExampleConstraintValueTypes descriptor) {
        return this.constraintTypeIndex.get(descriptor);
    }


    /**
     * A string representation  of an example
     */
    public String toString() {
        String posneg = "Positive";
        if (isNegative) {
            posneg = "Negative";
        }
        String result = posneg + " Example: ";
        for (String name : this.constraintNames.values()) {
            result += name + " ";
        }
        return result;
    }
}
