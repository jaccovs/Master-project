package org.exquisite.diagnosis.quickxplain.choco3;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.exquisite.data.ConstraintsFactory;
import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.datamodel.ExquisiteValueBound;
import org.exquisite.i8n.CultureInfo;
import org.exquisite.i8n.en.gb.EnglishGB;
import org.exquisite.parser.FormulaParser;
import org.exquisite.tools.IntegerUtilities;
import org.exquisite.tools.StringUtilities;

import solver.constraints.Constraint;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import solver.variables.IntVar;
import solver.variables.VariableFactory;


/**
 * Class that implements the constraint creation logic
 * @author dietmar
 *
 */
public class Choco3ConstraintFactory {

	Choco3FormulaSolver c3solver;
	
	// for convenient access
	solver.Solver solver = null;

	/**
	 * Create a factory for this solver
	 * @param c3solver
	 */
	public Choco3ConstraintFactory(Choco3FormulaSolver c3solver) {
		super();
		this.c3solver = c3solver;
		this.graph = c3solver.diagnosisModel.graph;
		solver = c3solver.solver;
		ExquisiteValueBound valueBounds = c3solver.qx.getSessionData().appXML.getDefaultValueBound();
//		this.min = (int) valueBounds.getLower();
//		this.max = (int) valueBounds.getUpper();
		
		
	}
	
	// Defaults
//	int min = Integer.MIN_VALUE + 1000000;
//	int max = Integer.MAX_VALUE - 1000000;
	int min = -1000000;
	int max = 1000000;
	
	static int tmpID = 0;
	

	// The cultuer
	public CultureInfo culture = new EnglishGB();
	
	// A graph
	ExquisiteGraph<String> graph = null;
	
	// to differentiate the if statements..
//	public int MIN_VALUE = -120000;
	/**
	 * Create the constraints for this problem
	 */
	public void createConstraint(choco.kernel.model.constraints.Constraint constraint) {
		// Get the formula
		FormulaInfo formulaInfo = c3solver.diagnosisModel.getFormulaInfoOfConstraints().get(constraint);
		if (formulaInfo != null) {
//			System.out.println("Have to translate constraint: " + formulaInfo.formula);
			solver.constraints.Constraint c = null;
			if (formulaInfo.tree != null) {
				c = buildAndPostChoco3Constraint(formulaInfo.cellName, formulaInfo.cellName, formulaInfo.operator, formulaInfo.tree, ConstraintsFactory.WORKSHEET_PREFIX, formulaInfo.formula);
			}
			else {

//				System.out.println("No tree: " + formulaInfo.formula);
				// Create a simple constraint
				// Get the variable
				IntVar cellVariable = c3solver.variablesMap.get(formulaInfo.cellName);
				if (cellVariable == null) {
//					System.out.println("Creating new variable for target cell " + formulaInfo.cellName);
					cellVariable = VariableFactory.bounded(formulaInfo.cellName, cellVariable.getLB(), cellVariable.getUB(), c3solver.solver);
					c3solver.variablesMap.put(formulaInfo.cellName, cellVariable);
				}
				// Post the constraint
				c = IntConstraintFactory.arithm(cellVariable, formulaInfo.operator, Integer.parseInt(formulaInfo.formula));
				c3solver.solver.post(c);
			}
			
			
		}
		else {
			System.err.println(constraint);
			System.err.println("No formula info found ..hmm!!");
		}
		
	}
	
	
	/**
	 * Creates a choco constraint ..
	 * @param id cell name... unclear why we need id and cell reference
	 * @param cellReference the name of the cell
	 * @param tree the parsed tree 
	 * @param variables the list of variables
	 * @param worksheetReferencePrefix
	 * @param excelFormula the formula in its original form
	 * @return a choco3 constraint
	 */
	Constraint buildAndPostChoco3Constraint(	String id, 
										String cellReference,
										String operator,
										Tree tree, 
										String worksheetReferencePrefix,
										String excelFormula
										) {
//		System.out.println("Creating constraint from " + excelFormula);
		IntVar var = buildExpression(cellReference, tree, worksheetReferencePrefix);	
//		return Choco.eq(variables.get(cellReference), (IntegerExpressionVariable) expression));
		// Create the final equality constraint
		IntVar cellVariable = c3solver.variablesMap.get(cellReference);
		if (cellVariable == null) {
			System.out.println("Creating new variable for target cell " + cellReference);
			cellVariable = VariableFactory.bounded(cellReference, cellVariable.getLB(), cellVariable.getUB(), c3solver.solver);
			c3solver.variablesMap.put(cellReference, cellVariable);
		}
		Constraint finalConstraint = null;
		if (var != null) {
			finalConstraint = IntConstraintFactory.arithm(var, operator, cellVariable);
			solver.post(finalConstraint);
		}
		else {
			System.out.println("Constraint was null");
		}
		return finalConstraint;

	}
	
	
	/**
	 * Builds a Choco IntegerExpressionVariable.
	 * @param tree
	 * @param variables
	 * @return
	 */
	public IntVar buildExpression(	
								String cellReference, 
								Tree tree, 
								String worksheetReferencePrefix)
    {
		
        if (tree == null) {
        	System.out.println("Tree: " + tree);
            return null;
        }

        int intChild0;
        boolean isChild0Numeric = false;
        int intChild1;
        boolean isChild1Numeric = false;
        
        String treeString = tree.toString();
//        System.out.println("Tree string: " + treeString);
        //NOTE: Switch statements don't allow variables/method output as case clauses therefore changed to if..else...
        //Check for ROOT or FUNC
        if (treeString.equalsIgnoreCase("ROOT") || treeString.equalsIgnoreCase("FUNC"))
        {
        	return buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix);
        }
        //"+"
        else if (treeString.equalsIgnoreCase(culture.PLUS()))
        {
        	Constraint c = null;
//        	System.out.println("Got an addition");
        	// TODO: Do a calculation of possible bounds...
        	
        	
        	IntVar returnVariable = VariableFactory.bounded("tmp" + tmpID++, min, max, solver);
        	 if ((tree.getChild(0) != null) && (tree.getChild(1) != null))
             {             	
             	isChild0Numeric = FormulaParser.isNumeric(tree.getChild(0).toStringTree());
            	isChild1Numeric = FormulaParser.isNumeric(tree.getChild(1).toStringTree());
            	                    
            	//do we need to handle if both children are numbers??
            	if (isChild0Numeric && isChild1Numeric)
            	{
            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
            		intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                	c = IntConstraintFactory.arithm(returnVariable, "=", intChild0 + intChild1);
            	}
            	else if (isChild0Numeric)
            	{
            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
            		c = IntConstraintFactory.arithm(
            				returnVariable, 
            				"=", 
            				buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix), 
            				"+", 
            				intChild0);
//                	c = IntConstraintFactory.distance(
//							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
//							returnVariable, 
//							"=",
//							intChild0);
            	}
            	else if (isChild1Numeric)
	            {                    	
             		intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
             		c = IntConstraintFactory.arithm(
            				returnVariable, 
            				"=", 
            				buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
            				"+", 
            				intChild1);
//                	c = IntConstraintFactory.distance(
//							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
//							returnVariable, 
//							"=",
//							intChild1);
	            }
            	else
            	{           	
	            	// Post the constraint and return the new intermediate variable
	            	c = IntConstraintFactory.sum(new IntVar[] {
	            							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
	            							buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix)}, 
	            							returnVariable);
            	}

            	// Post it
            	solver.post(c);
            	return returnVariable;
             }
             return null;
        }
        //"-"
        else if (treeString.equalsIgnoreCase(culture.MINUS()))
        {
        	Constraint c = null;
//        	System.out.println("Got a subtraction");
        	// TODO: Do a calculation of possible bounds...
        	IntVar returnVariable = VariableFactory.bounded("tmp" + tmpID++,min, max, solver);
        	
        	if ((tree.getChild(0) != null) && (tree.getChild(1) != null))
            {
        		isChild0Numeric = FormulaParser.isNumeric(tree.getChild(0).toStringTree());
            	isChild1Numeric = FormulaParser.isNumeric(tree.getChild(1).toStringTree());
            	                    
            	//do we need to handle if both children are integers??
            	if (isChild0Numeric && isChild1Numeric)
            	{
            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
            		intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                	c = IntConstraintFactory.arithm(returnVariable, "=", intChild0 - intChild1);
            	}
//            	else if (isChild0Numeric)
//            	{
//            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
//                	c = IntConstraintFactory.distance(
//							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
//							returnVariable, 
//							"=",
//							intChild0);
//            	}
            	else if (isChild1Numeric)
                {
                	intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                	c = IntConstraintFactory.arithm(
            				returnVariable, 
            				"=", 
            				buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
            				"-", 
            				intChild1);
//                	c = IntConstraintFactory.distance(
//							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
//							returnVariable, 
//							"=",
//							intChild1);
                }
            	else
            	{
		        	// Post the constraint and return the new intermediate variable
            		c = IntConstraintFactory.sum(new IntVar[] {
            				buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix),
            				returnVariable}, 
            				buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix));
//		        	c = IntConstraintFactory.distance(
//		        							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
//		        							buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix), 
//		        							"=", 
//		        							returnVariable);
            	}

            	// Post it
            	solver.post(c);
            }
            return returnVariable;
        }
        //"*"
        else if (treeString.equalsIgnoreCase(culture.MULTIPLY()))
        {
        	Constraint c = null;
//        	System.out.println("Got a multiplication");
        	// TODO: Do a calculation of possible bounds...
        	IntVar returnVariable = VariableFactory.bounded("tmp" + tmpID++, min, max, solver);
        	
        	if ((tree.getChild(0) != null) && (tree.getChild(1) != null))
            {
        		
        		isChild0Numeric = FormulaParser.isNumeric(tree.getChild(0).toStringTree());
            	isChild1Numeric = FormulaParser.isNumeric(tree.getChild(1).toStringTree());
            	                    
            	//do we need to handle if both children are integers??
            	if (isChild0Numeric && isChild1Numeric)
            	{
            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
            		intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                	c = IntConstraintFactory.arithm(returnVariable, "=", intChild0 * intChild1);

            	}
            	else if (isChild0Numeric)
            	{
            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
                	c = IntConstraintFactory.times(
							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
							intChild0, 
							returnVariable);
            	}
            	else if (isChild1Numeric)
                {
        			intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                	c = IntConstraintFactory.times(
							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
							intChild1, 
							returnVariable);
                }
            	else
            	{
	            	// Post the constraint and return the new intermediate variable
	            	c = IntConstraintFactory.times(
	            							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
	            							buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix), 
	            							returnVariable);
            	}

            	// Post it
            	solver.post(c);
            	
            }
            return returnVariable;
        }
        //"/"
        else if (treeString.equalsIgnoreCase(culture.DIVIDE())|| treeString.equalsIgnoreCase(culture.QUOTIENT()))
        {
        	Constraint c = null;
//        	System.out.println("Got a division");
        	// TODO: Do a calculation of possible bounds...
        	IntVar returnVariable = VariableFactory.bounded("tmp" + tmpID++, min, max, solver);
        	if ((tree.getChild(0) != null) && (tree.getChild(1) != null))
            {
        		isChild0Numeric = FormulaParser.isNumeric(tree.getChild(0).toStringTree());
            	isChild1Numeric = FormulaParser.isNumeric(tree.getChild(1).toStringTree());
            	                    
            	//do we need to handle if both children are integers??
            	if (isChild0Numeric && isChild1Numeric)
            	{
            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
            		intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                	c = IntConstraintFactory.arithm(returnVariable, "=", intChild0 / intChild1);
            	}
            	else if (isChild0Numeric)
            	{
            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
            		// Create a new variable
            		IntVar c0var = VariableFactory.bounded("tmp", intChild0, intChild0, solver);
                	c = IntConstraintFactory.eucl_div(
							c0var, 
							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
							returnVariable);
            	}
            	else if (isChild1Numeric)
                {
        			intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
                	// Post the constraint and return the new intermediate variable
            		IntVar c1var = VariableFactory.bounded("tmp", intChild1, intChild1, solver);
                	c = IntConstraintFactory.eucl_div(
                							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
                							c1var, 
                							returnVariable);
                }
            	else
            	{
	            	// Post the constraint and return the new intermediate variable
	            	c = IntConstraintFactory.eucl_div(
	            							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
	            							buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix), 
	            							returnVariable);
            	}

            	// Post it
            	solver.post(c);
            	return returnVariable;
            }
            return null;
        }
        //Excel sum function
        else if (treeString.equalsIgnoreCase(culture.SUM()))
        {
        	return buildSumExpression(cellReference, tree, worksheetReferencePrefix);
        }
        //Excel min function
        else if (treeString.equalsIgnoreCase(culture.MIN()))
        {
        	if ((tree.getChild(0) != null) && (tree.getChild(1) != null))
            {
        		
            	Constraint c = null;
//            	System.out.println("Got a min");
            	// TODO: Do a calculation of possible bounds...
            	IntVar returnVariable = VariableFactory.bounded("tmp" + tmpID++, min, max, solver);
            	c = IntConstraintFactory.minimum(returnVariable,
            										buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
            										buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix));
            	solver.post(c);
            	
            	return returnVariable;
            }
        	System.err.println("Min expression without children..");
        	return null;
        }
        //Excel if statement
        else if (treeString.equalsIgnoreCase(culture.IF()))
        {
        	Constraint c = null;
//        	System.out.println("Got an if");
        	// TODO: Do a calculation of possible bounds...
        	IntVar returnVariable = VariableFactory.bounded("tmp" + tmpID++, min, max, solver);

        	if ((tree.getChild(0) != null) && (tree.getChild(1) != null) && (tree.getChild(2) != null))
            {
        		
        		IntVar exp1 = buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix);
        		IntVar exp2 = buildExpression(cellReference, tree.getChild(2), worksheetReferencePrefix);
        		
        		// Create tmp vars to connect the results
//        		IntVar tmp1 = VariableFactory.bounded("tmp1", MIN_VALUE, max, this.solver);
//        		IntVar tmp2 = VariableFactory.bounded("tmp2", MIN_VALUE, max, this.solver);
        		
//        		Constraint c1 = IntConstraintFactory.arithm(exp1, "=", tmp1);
//        		Constraint c2 = IntConstraintFactory.arithm(exp2, "=", tmp2);
        		
        		Constraint c1 = IntConstraintFactory.arithm(returnVariable, "=", exp1);
        		Constraint c2 = IntConstraintFactory.arithm(returnVariable, "=", exp2);
        		
        		c = LogicalConstraintFactory.ifThenElse(
        				buildConstraint(cellReference, tree.getChild(0), worksheetReferencePrefix), c1, c2);
        		
        		solver.post(c);

        		// Add another if then else to connect the binding with the return value
//        		Constraint c_logic = LogicalConstraintFactory.ifThenElse(
//        				IntConstraintFactory.arithm(tmp1, "!=", MIN_VALUE), 
//        				IntConstraintFactory.arithm(returnVariable, "=", exp1), 
//        				IntConstraintFactory.arithm(returnVariable, "=", exp2));
//
//        		solver.post(c_logic);
        		// Need to somehow bind the constraint
          		return returnVariable;
          		  		//    
            } else if ((tree.getChild(0) != null) && (tree.getChild(1) != null)) {
//            	return (Choco.ifThenElse(buildConstraint(cellReference, tree.getChild(0), variables, worksheetReferencePrefix), buildExpression(cellReference, tree.getChild(1), variables, worksheetReferencePrefix), null));
          		System.err.println("IF STATEMENT WITHOUT ELSE");
            	return null;
          		//    
            }
        	return null;
        }
        else //default: return variable
        {
        	String variableName = worksheetReferencePrefix + tree.toString();   
        	IntVar returnVariable;
//        	System.out.println("Looking for variable: " + variableName);
    		if (FormulaParser.isNumeric(tree.toString())) {
//    			System.out.println("We got a constant");
//        		System.out.println("Creating tmp and posting it" );
        		returnVariable = VariableFactory.bounded("tmp" + tmpID++, min, max, solver);
        		c3solver.variablesMap.put(variableName, returnVariable);
    			// Post an equality constraint
    			Integer value = Integer.parseInt(tree.toString());
    			solver.post(IntConstraintFactory.arithm(returnVariable, "=", value));
    			return returnVariable;
    		}
        	returnVariable = c3solver.variablesMap.get(variableName);
        	if (returnVariable == null) {
        		// TODO: Use the specific domains defined for the variable
//        		System.out.println("Creating " + variableName);
        		returnVariable = VariableFactory.bounded(variableName, min, max, solver);
        		c3solver.variablesMap.put(variableName, returnVariable);
        	}
        	
        	try {
				graph.addEdge(variableName, cellReference);
			} catch (Exception e) {
				e.printStackTrace();
			}
//        	return variables.get(variableName);
//        	System.out.println("Returning variable " + variableName);
        	return returnVariable;
        }  
    }
	/**
	 * Builds a Choco constraint
	 * @param tree
	 * @param variables
	 * @return
	 */
	private Constraint buildConstraint(String cellReference, Tree tree, String worksheetReferencePrefix)
	{			
//		System.out.println("-- " + tree.toString());

		String symbol = tree.toString();
		if (symbol.equals("<>")) {
			symbol = "!=";
		}
		switch (tree.toString().toUpperCase())
        {
            case "ROOT":
                return buildConstraint(cellReference, tree.getChild(0), worksheetReferencePrefix);
            case "FUNC":
            	return buildConstraint(cellReference, tree.getChild(0), worksheetReferencePrefix);
            default:
             if (	
            		 symbol.equals("=")||
            		 symbol.equals(">")||
            		 symbol.equals("<")||
            		 symbol.equals("<=")||
            		 symbol.equals(">=")||
            		 symbol.equals("!=")
                )  {
            	  return IntConstraintFactory.arithm(buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
            			  							symbol, 
            			  							 buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix));
              }
            	
            	if (tree.toString().equalsIgnoreCase(culture.AND())) {
//            		System.out.println("Creating an if");
            		Constraint[] theConstraints = buildConstraintsFromChildren(cellReference, tree, worksheetReferencePrefix);
            		Constraint c = LogicalConstraintFactory.and(theConstraints);
            		return c;
            	}
            	else if (tree.toString().equalsIgnoreCase(culture.OR())) {
            		Constraint[] theConstraints = buildConstraintsFromChildren(cellReference, tree, worksheetReferencePrefix);
            		Constraint c = LogicalConstraintFactory.or(theConstraints);
            		return c;
            	}
            	else if (tree.toString().equalsIgnoreCase(culture.NOT())) {
            		Constraint c = buildConstraint(cellReference, tree.getChild(0), worksheetReferencePrefix);
            		return LogicalConstraintFactory.not(c);
            	}
            	else
            	{
            		// TODO: What to do in this case.
            		String variableName = worksheetReferencePrefix + tree.toString();
            		System.err.println("oops.. not here");
            		return (Constraint) c3solver.variablesMap.get(variableName);
            	}
        }
	}
	
	
	/**
	 * Builds Choco constraints from all children of given Tree element.
	 * @param cellReference
	 * @param tree
	 * @param variables
	 * @param worksheetReferencePrefix
	 * @return
	 */
	private Constraint[] buildConstraintsFromChildren(String cellReference, Tree tree, String worksheetReferencePrefix)
	{
		int constraintCount = tree.getChildCount();
		Constraint[] constraints = new Constraint[constraintCount];
		for (int i = 0; i < constraintCount; i++)
		{
			constraints[i] = buildConstraint(cellReference, tree.getChild(i), worksheetReferencePrefix);
		}
		return constraints;
	}
	
	
	/**
	 * Builds a Choco.sum constraint
	 * @param tree
	 * @param variables
	 * @param worksheetReferencePrefix
	 * @return
	 */
	private IntVar buildSumExpression(String cellReference, Tree tree, String worksheetReferencePrefix) {
		if (tree == null)
            return null;
		int childCount = tree.getChildCount();
		
		List<IntVar> argsList = new ArrayList<IntVar>();
		List<Integer> constants = new ArrayList<Integer>();
		for ( int i = 0; i < childCount; i++ ) {
			String childString = tree.getChild(i).toString();
			if(childString.contains(":")){
				List<String> cells = StringUtilities.rangeToCells(childString);
				for (String cell : cells){
					
					if(FormulaParser.isNumeric(cell)){
						constants.add(IntegerUtilities.parseToInt(cell));
					}
					else {
						String name = worksheetReferencePrefix + cell;
						IntVar variable = c3solver.variablesMap.get(name);
						if (variable == null) {
							variable = VariableFactory.bounded(name, min, max, this.solver);
							c3solver.variablesMap.put(name, variable);
						}
						argsList.add(variable);
						try {
							graph.addEdge(name, cellReference);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				argsList.add(buildExpression(cellReference, tree.getChild(i), worksheetReferencePrefix));				
			}
		}
		// Now create the constraints
		int argCount = argsList.size();
		IntVar[] args = new IntVar[argCount];
		for ( int i = 0; i < argCount; i++ ) {
			args[i] = argsList.get(i);			
		}
		// Create the result
		IntVar returnVariable = VariableFactory.bounded("tmp" + tmpID++, min, max, this.solver);
		Constraint c = IntConstraintFactory.sum(args, returnVariable);
		solver.post(c);
		
		return returnVariable;
	}
	
	/**
	 * Deep copy of a parse tree.
	 * @param original
	 * @return the copy
	 */
	 public static CommonTree copyTreeRecursive(CommonTree original) {

		    CommonTree copy = new CommonTree(original); // Leverage constructor

		    if(original.getChildren() != null) {
		      for(Object o : original.getChildren()) {
		        CommonTree childCopy  = copyTreeRecursive((CommonTree)o);
		        childCopy.setParent(copy);
		        copy.addChild(childCopy);
		      }
		    }
		    return copy;
		  }
	

}
