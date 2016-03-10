/**
 *
 */
package org.exquisite.parser;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.exquisite.data.ConstantsFactory;
import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.i8n.CultureInfo;
import org.exquisite.i8n.en.gb.EnglishGB;
import org.exquisite.tools.IntegerUtilities;
import org.exquisite.tools.StringUtilities;

import java.util.*;

/**
 * @author Arash
 *         This class contains methods for parsing Excel formulas into various formats stored in multiple collections
 */
public class FormulaParser {

    public CommonTree FormulaTree;
    public Map<String, Constraint> constraints = new Hashtable<String, Constraint>();
    public Map<String, ArrayList<IntegerExpressionVariable>> variablesInConstraint = new Hashtable<String, ArrayList<IntegerExpressionVariable>>();
    public CultureInfo culture = new EnglishGB();

    // DJ: remember the original formula
    public String formula;

    private ConstantsFactory constants = new ConstantsFactory();
    private ExquisiteGraph<String> graph;

    public FormulaParser(ExquisiteGraph<String> graph) {
        this.graph = graph;
    }

    /**
     * Converts string representation of formula to CommonTree.
     *
     * @param formula
     * @return
     */
    public static CommonTree parseToCommonTree(String formula) {
        ANTLRStringStream input = new ANTLRStringStream(formula);

        ExcelLexer lexer = new ExcelLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ExcelParser parser = new ExcelParser(tokens);
        parser.adaptor = new CommonTreeAdaptor();
        try {
            return parser.parse().tree;
        } catch (RecognitionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tests if a string could be parsed to Integer.
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        try {
            IntegerUtilities.parseToInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Parses an Excel formula into a Tree structure, used for building Choco constraint
     * representations of the formula later.
     *
     * @param formula
     */
    public void parse(String formula) {
        this.FormulaTree = parseToCommonTree(formula);
        this.formula = formula;
    }

    /**
     * Converts an Excel formula into a Choco constraint.
     *
     * @param id            - unique id for new constraint.
     * @param cellReference - the name of the variable that this constraint is associated with.
     * @param tree          - the tree representation of the Excel formula.
     * @param variables     - a hashmap of the variables in the solver model - used as a lookup when building the constraint.
     */
    public void buildChocoStatements(String id, String cellReference, Tree tree,
                                     Dictionary<String, IntegerExpressionVariable> variables,
                                     String worksheetReferencePrefix) {
        String firstChildContent = tree.getChild(0).getText();

        switch (firstChildContent) {
            // TODO: What is this first option here?
            case "==":
                System.err.println("INFO: Called with operator ==, " + tree.toString());
                Constraint constraint = buildConstraint(cellReference, tree, variables, worksheetReferencePrefix);
                constraints.put(id, constraint);
                break;
            default:
                Variable expression = buildExpression(cellReference, tree, variables, worksheetReferencePrefix);
                constraints.put(id, Choco.eq(variables.get(cellReference), (IntegerExpressionVariable) expression));
                break;
        }
    }

    /**
     * Builds a Choco constraint
     *
     * @param tree
     * @param variables
     * @return
     */
    private Constraint buildConstraint(String cellReference, Tree tree,
                                       Dictionary<String, IntegerExpressionVariable> variables,
                                       String worksheetReferencePrefix) {
//		System.out.println("-- " + tree.toString());

        switch (tree.toString().toUpperCase()) {
            case "ROOT":
                return buildConstraint(cellReference, tree.getChild(0), variables, worksheetReferencePrefix);
            case "FUNC":
                return buildConstraint(cellReference, tree.getChild(0), variables, worksheetReferencePrefix);
            case "=":
                return Choco.eq(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix));
            case ">":
                return Choco.gt(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix));
            case "<":
                return Choco.lt(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix));
            case ">=":
                return Choco.geq(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix));
            case "<=":
                return Choco.leq(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix));
            case "<>":
                return Choco.neq(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix));
            default:

                if (tree.toString().equalsIgnoreCase(culture.AND())) {
                    return Choco.and(buildConstraintsFromChildren(cellReference, tree, variables,
                            worksheetReferencePrefix));
                } else if (tree.toString().equalsIgnoreCase(culture.OR())) {
                    return Choco
                            .or(buildConstraintsFromChildren(cellReference, tree, variables, worksheetReferencePrefix));
                } else if (tree.toString().equalsIgnoreCase(culture.NOT())) {
                    return Choco
                            .not(buildConstraint(cellReference, tree.getChild(0), variables, worksheetReferencePrefix));
                } else {
                    String variableName = worksheetReferencePrefix + tree.toString();
                    return (Constraint) variables.get(variableName);
                }
        }
    }

    /**
     * Builds Choco constraints from all children of given Tree element.
     *
     * @param cellReference
     * @param tree
     * @param variables
     * @param worksheetReferencePrefix
     * @return
     */
    private Constraint[] buildConstraintsFromChildren(String cellReference, Tree tree,
                                                      Dictionary<String, IntegerExpressionVariable> variables,
                                                      String worksheetReferencePrefix) {
        int constraintCount = tree.getChildCount();

        Constraint[] constraints = new Constraint[constraintCount];

        for (int i = 0; i < constraintCount; i++) {
            constraints[i] = buildConstraint(cellReference, tree.getChild(i), variables, worksheetReferencePrefix);
        }

        return constraints;
    }

    /**
     * Builds a Choco IntegerExpressionVariable.
     *
     * @param tree
     * @param variables
     * @return
     */
    public IntegerExpressionVariable buildExpression(String cellReference, Tree tree,
                                                     Dictionary<String, IntegerExpressionVariable> variables) {
        return buildExpression(cellReference, tree, variables, "");
    }

    /**
     * Builds a Choco IntegerExpressionVariable.
     *
     * @param tree
     * @param variables
     * @return
     */
    public IntegerExpressionVariable buildExpression(String cellReference, Tree tree,
                                                     Dictionary<String, IntegerExpressionVariable> variables,
                                                     String worksheetReferencePrefix) {
        if (tree == null)
            return null;

        int intChild0;
        IntegerVariable intChildConstant0;
        boolean isChild0Numeric = false;
        int intChild1;
        IntegerVariable intChildConstant1;
        boolean isChild1Numeric = false;

        String treeString = tree.toString();
        //NOTE: Switch statements don't allow variables/method output as case clauses therefore changed to if..else...
        //Check for ROOT or FUNC
        if (treeString.equalsIgnoreCase("ROOT") || treeString.equalsIgnoreCase("FUNC")) {
            return buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix);
        }
        //"+"
        else if (treeString.equalsIgnoreCase(culture.PLUS())) {
            if ((tree.getChild(0) != null) && (tree.getChild(1) != null)) {
                isChild0Numeric = isNumeric(tree.getChild(0).toStringTree());
                isChild1Numeric = isNumeric(tree.getChild(1).toStringTree());

                //do we need to handle if both children are numbers??
                if (isChild0Numeric && isChild1Numeric) {
                    intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
                    intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                    intChildConstant0 = constants.makeIntegerConstant("CONSTANT_", intChild0);
                    intChildConstant1 = constants.makeIntegerConstant("CONSTANT_", intChild1);
                    return (Choco.plus(intChildConstant0, intChildConstant1));
                } else {
                    if (isChild0Numeric) {
                        intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
                        return (Choco.plus(intChild0,
                                buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix)));
                    }
                    if (isChild1Numeric) {
                        intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                        return (Choco.plus(buildExpression(cellReference, tree.getChild(0), variables,
                                worksheetReferencePrefix), intChild1));
                    }
                }
                return (Choco
                        .plus(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                                buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix)));
            }
            return null;
        }
        //"-"
        else if (treeString.equalsIgnoreCase(culture.MINUS())) {
            if ((tree.getChild(0) != null) && (tree.getChild(1) != null)) {
                isChild0Numeric = isNumeric(tree.getChild(0).toStringTree());
                isChild1Numeric = isNumeric(tree.getChild(1).toStringTree());

                //do we need to handle if both children are integers??
                if (isChild0Numeric && isChild1Numeric) {
                    intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
                    intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                    intChildConstant0 = constants.makeIntegerConstant("CONSTANT_", intChild0);
                    intChildConstant1 = constants.makeIntegerConstant("CONSTANT_", intChild1);
                    return (Choco.minus(intChildConstant0, intChildConstant1));
                } else {
                    if (isChild0Numeric) {
                        intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
                        return (Choco.minus(intChild0,
                                buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix)));
                    }
                    if (isChild1Numeric) {
                        intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                        return (Choco.minus(buildExpression(cellReference, tree.getChild(0), variables,
                                worksheetReferencePrefix), intChild1));
                    }
                }
                return (Choco
                        .minus(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                                buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix)));
            }
            return null;
        }
        //"*"
        else if (treeString.equalsIgnoreCase(culture.MULTIPLY())) {
            if ((tree.getChild(0) != null) && (tree.getChild(1) != null)) {
                isChild0Numeric = isNumeric(tree.getChild(0).toStringTree());
                isChild1Numeric = isNumeric(tree.getChild(1).toStringTree());

                //do we need to handle if both children are integers??
                if (isChild0Numeric && isChild1Numeric) {
                    intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
                    intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                    intChildConstant0 = constants.makeIntegerConstant("CONSTANT_", intChild0);
                    intChildConstant1 = constants.makeIntegerConstant("CONSTANT_", intChild1);
                    return (Choco.mult(intChildConstant0, intChildConstant1));
                } else {
                    if (isChild0Numeric) {
                        intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
                        return (Choco.mult(intChild0,
                                buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix)));
                    }
                    if (isChild1Numeric) {
                        intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                        return (Choco.mult(buildExpression(cellReference, tree.getChild(0), variables,
                                worksheetReferencePrefix), intChild1));
                    }
                }
                return (Choco
                        .mult(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                                buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix)));
            }
            return null;
        }
        //"/"
        else if (treeString.equalsIgnoreCase(culture.DIVIDE())) {
            if ((tree.getChild(0) != null) && (tree.getChild(1) != null)) {
                isChild0Numeric = isNumeric(tree.getChild(0).toStringTree());
                isChild1Numeric = isNumeric(tree.getChild(1).toStringTree());

                //do we need to handle if both children are integers??
                if (isChild0Numeric && isChild1Numeric) {
                    intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
                    intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                    intChildConstant0 = constants.makeIntegerConstant("CONSTANT_", intChild0);
                    intChildConstant1 = constants.makeIntegerConstant("CONSTANT_", intChild1);
                    return (Choco.div(intChildConstant0, intChildConstant1));
                } else {
                    if (isChild0Numeric) {
                        intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
                        return (Choco.div(intChild0,
                                buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix)));
                    }
                    if (isChild1Numeric) {
                        intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                        //System.out.println("Choco.mult(BuildExpression(" + tree.getChild(0) + ", {" + variables + "}, " + worksheetReferencePrefix + "), " + intChild1 + ")");
                        return (Choco.div(buildExpression(cellReference, tree.getChild(0), variables,
                                worksheetReferencePrefix), intChild1));
                    }
                }
                //String result = "Choco.mult(BuildExpression(" + tree.getChild(0) + ", " + variables + ", " + worksheetReferencePrefix + "), BuildExpression(" + tree.getChild(1) + ", " + variables + ", " + worksheetReferencePrefix + "));";
                //System.out.println(result);
                return (Choco.div(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix)));
            }
            return null;
        }
        //Excel sum function
        else if (treeString.equalsIgnoreCase(culture.SUM())) {
            return buildSumExpression(cellReference, tree, variables, worksheetReferencePrefix);
        }
        //Excel min function
        else if (treeString.equalsIgnoreCase(culture.MIN())) {
            if ((tree.getChild(0) != null) && (tree.getChild(1) != null)) {
                return (Choco.min(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix)));
            }
            System.err.println("Min expression without children..");
            return null;
        }
        //Excel quotient function
        else if (treeString.equalsIgnoreCase(culture.QUOTIENT())) {
            if ((tree.getChild(0) != null) && (tree.getChild(1) != null)) {
                return (Choco.div(buildExpression(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix)));
            }
            System.err.println("Quotient expression without children..");
            return null;
        }
        //Excel if statement
        else if (treeString.equalsIgnoreCase(culture.IF())) {
            if ((tree.getChild(0) != null) && (tree.getChild(1) != null) && (tree.getChild(2) != null)) {
                return (Choco.ifThenElse(
                        buildConstraint(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(2), variables, worksheetReferencePrefix)));
            } else if ((tree.getChild(0) != null) && (tree.getChild(1) != null)) {
                return (Choco.ifThenElse(
                        buildConstraint(cellReference, tree.getChild(0), variables, worksheetReferencePrefix),
                        buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix), null));
            }
            return null;
        } else //default: return variable
        {
            String variableName = worksheetReferencePrefix + tree.toString();
            IntegerExpressionVariable returnVariable;
            returnVariable = variables.get(variableName);

            if (returnVariable == null) {
                if (isNumeric(tree.toString())) {
                    return constants.makeIntegerConstant("CONSTANT_", IntegerUtilities.parseToInt(tree.toString()));
                }
            }
            try {
                graph.addEdge(variableName, cellReference);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return variables.get(variableName);
        }
    }

    /**
     * Builds a Choco.sum constraint
     *
     * @param tree
     * @param variables
     * @param worksheetReferencePrefix
     * @return
     */
    private IntegerExpressionVariable buildSumExpression(String cellReference, Tree tree,
                                                         Dictionary<String, IntegerExpressionVariable> variables,
                                                         String worksheetReferencePrefix) {
        if (tree == null)
            return null;
        int childCount = tree.getChildCount();

        List<IntegerExpressionVariable> argsList = new ArrayList<IntegerExpressionVariable>();
        for (int i = 0; i < childCount; i++) {
            String childString = tree.getChild(i).toString();
            if (childString.contains(":")) {
                List<String> cells = StringUtilities.rangeToCells(childString);
                for (String cell : cells) {
                    IntegerExpressionVariable variable = variables.get(worksheetReferencePrefix + cell);
                    if (variable == null) {
                        if (isNumeric(cell)) {
                            IntegerVariable constant = constants
                                    .makeIntegerConstant("CONSTANT_", IntegerUtilities.parseToInt(cell));
                            argsList.add(constant);
                        }
                    } else {
                        argsList.add(variables.get(worksheetReferencePrefix + cell));
                        try {
                            graph.addEdge(worksheetReferencePrefix + cell, cellReference);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                argsList.add(buildExpression(cellReference, tree.getChild(i), variables, worksheetReferencePrefix));
            }
        }

        int argCount = argsList.size();
        IntegerExpressionVariable[] args = new IntegerExpressionVariable[argCount];
        for (int i = 0; i < argCount; i++) {
            args[i] = argsList.get(i);
        }

        return (Choco.sum(args));
    }
}