package tests.dj.choco3;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.exquisite.data.ConstraintsFactory;
import org.exquisite.data.DiagnosisModelLoader;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.datamodel.ExquisiteValueBound;
import org.exquisite.i8n.CultureInfo;
import org.exquisite.i8n.en.gb.EnglishGB;
import org.exquisite.parser.FormulaParser;
import org.exquisite.tools.IntegerUtilities;
import org.exquisite.tools.StringUtilities;

import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import solver.variables.IntVar;
import solver.variables.Variable;
import solver.variables.VariableFactory;

/**
 * Do some tests with Choco 3
 * @author dietmar
 *
 */
public class Choco3Tests {
	
//	String testfile = "experiments/spreadsheetsbenchmark/doubleFault/ex30.xml";
	String testfile = "experiments/spreadsheetsbenchmark/doubleFault/ex02.xml";
	
	public CultureInfo culture = new EnglishGB();
	ExquisiteGraph<String> graph = null;
	
	// Create a solver object
	Solver solver = null; 
	
	// default value bounds
	int min = 0;
	int max = 1000;
	
	// to differentiate the if statements..
	public int MIN_VALUE = -100000;

	//A collection of variables objects that are created for the model to use.
	private Dictionary<String, IntVar> variablesMap = new Hashtable<String, IntVar>() ;

	/**
	 * Work starts here
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Staring ..");
		try {
			Choco3Tests tests = new Choco3Tests();
			tests.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Program ended");
	}

	/**
	 * Do the work
	 */
	private void run() {
		solver = new Solver("Choco3Parsing");
		
		System.out.println("Loading XML");
		
		// Do all the default stuff
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(testfile);
		ExquisiteSession sessionData = new ExquisiteSession(appXML);
		ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
		DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, null, conFactory);
		System.out.println("Loading from file done");
		System.out.println("Start to create diagnosis model");
		// Now, we should have a specific logic to create the model (overwrite loadDiagnosisModelFromXML)
		//Make variables with any **global** user defined value bounds.
		ExquisiteValueBound defaultValueBound = appXML.getDefaultValueBound();
		
		//Input cell variables with default value bounds.
		this.min = (int)defaultValueBound.getLower();
		this.max = (int)defaultValueBound.getUpper();
		
		//Now add all the variables to the diagnosis model.
//		sessionData.diagnosisModel.getVariables().addAll(varsWithGlobalValueBounds);
//		sessionData.diagnosisModel.getVariables().addAll(inputVariables);
//		sessionData.diagnosisModel.getVariables().addAll(interimVariables);
//		sessionData.diagnosisModel.getVariables().addAll(outputVariables);
		
		//Build a graph representation of the dependencies between the variables.
		// the graph
		this.graph = sessionData.graph;
		appXML.buildGraph(graph);
		
		//Make the constraint representations of the spreadsheet formulae.
		FormulaParser formulaParser = new FormulaParser(graph);
		Dictionary<String, Constraint> result = new Hashtable<String, Constraint>();
		Dictionary<String, String> formulae = appXML.getFormulas();

		Map<String, CommonTree> parsedFormulas = new HashMap<String, CommonTree>();
		for(Enumeration<String> keys = formulae.keys(); keys.hasMoreElements();)
		{			
			String cellReference = keys.nextElement();			
			String formula = formulae.get(cellReference);
			formulaParser.parse(formula);
			String id = cellReference;
//			System.out.println("parsed tree: " + formulaParser.FormulaTree.getChild(0));
			parsedFormulas.put(cellReference, copyTreeRecursive(formulaParser.FormulaTree));
		}	
		System.out.println("Parsed " + parsedFormulas.size() + " formulas");
//		System.out.println(parsedFormulas.keySet());

		int cnt = 0;
		List<Constraint> constraints = new ArrayList<Constraint>();
		for (String key : parsedFormulas.keySet()) {
			CommonTree tree = parsedFormulas.get(key);
			System.out.println("thre tree: " + tree.getChild(0));
//			formulaParser.buildChocoStatements(key, key, formulaParser.FormulaTree, variablesMap, "WS_1_");
//			System.out.println("formula: " + formulae.get(key));
			Constraint c = buildChoco3Constraint(key, key, tree, "WS_1_",formulae.get(key));
			constraints.add(c);
			
			cnt++;
			if (cnt > 10) {
				break;
			}
		}
		
		
		System.out.println("Number of constraints: " + constraints.size());
		
		// Setting some inputs
       Variable[] variables = solver.getVars();
        for (Variable v : variables) {
        	IntVar intv = (IntVar) v;
        	if (v.getName().equalsIgnoreCase("WS_1_S21")) {
    			solver.post(IntConstraintFactory.arithm(intv, "=", 3));
        	}
        	if (v.getName().equalsIgnoreCase("WS_1_D21")) {
    			solver.post(IntConstraintFactory.arithm(intv, "=", 2));
        	}
        	if (v.getName().equalsIgnoreCase("WS_1_T6")) {
    			solver.post(IntConstraintFactory.arithm(intv, "=", 10));
        	}
        	if (v.getName().equalsIgnoreCase("WS_1_T7")) {
    			solver.post(IntConstraintFactory.arithm(intv, "=", 5));
        	}
        }
 		
		// Now, let's solve the problem.
		boolean solutionFound = solver.findSolution();
		System.out.println("Found solution: " + solutionFound);
		
        variables = solver.getVars();
        for (Variable v : variables) {
        	IntVar intv = (IntVar) v;
        	if (!v.getName().contains("TMP")) {
        			System.out.println("Found var : " + v.getName()+ ":" + intv.getValue() );
        	}
        }
        
        System.out.println("-----------------");

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
	Constraint buildChoco3Constraint(	String id, 
										String cellReference, 
										Tree tree, 
										String worksheetReferencePrefix,
										String excelFormula
										) {
		System.out.println("Creating constraint from " + excelFormula);
		IntVar var = buildExpression(cellReference, tree, worksheetReferencePrefix);	
//		return Choco.eq(variables.get(cellReference), (IntegerExpressionVariable) expression));
		// Create the final equality constraint
		IntVar cellVariable = this.variablesMap.get(cellReference);
		if (cellVariable == null) {
			System.out.println("Creating new variable for target cell " + cellReference);
			cellVariable = VariableFactory.bounded(cellReference, min, max, solver);
			this.variablesMap.put(cellReference, cellVariable);
		}
		Constraint finalConstraint = null;
		if (var != null) {
			finalConstraint = IntConstraintFactory.arithm(var, "=", cellVariable);
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
        System.out.println("Tree string: " + treeString);
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
        	System.out.println("Got an addition");
        	// TODO: Do a calculation of possible bounds...
        	IntVar returnVariable = VariableFactory.bounded("tmp", 0, 10000, solver);
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
            	else{
	        		if (isChild0Numeric)
	            	{
	            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
	                	c = IntConstraintFactory.distance(
    							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
    							returnVariable, 
    							"=",
    							intChild0);
	            	}
	             	if (isChild1Numeric)
		            {                    	
	             		intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
	                	c = IntConstraintFactory.distance(
    							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
    							returnVariable, 
    							"=",
    							intChild1);
		            }
            	}            	
            	// Post the constraint and return the new intermediate variable
            	c = IntConstraintFactory.sum(new IntVar[] {
            							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
            							buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix)}, 
            							returnVariable);

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
        	System.out.println("Got a subtraction");
        	// TODO: Do a calculation of possible bounds...
        	IntVar returnVariable = VariableFactory.bounded("tmp", 0, 10000, solver);
        	
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
            	else
            	{
	        		if (isChild0Numeric)
	            	{
	            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
	                	c = IntConstraintFactory.distance(
    							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
    							returnVariable, 
    							"=",
    							intChild0);
	            	}
	            	if (isChild1Numeric)
	                {
	                	intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
	                	c = IntConstraintFactory.distance(
    							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
    							returnVariable, 
    							"=",
    							intChild1);
	                }
            	}
            	// Post the constraint and return the new intermediate variable
            	c = IntConstraintFactory.distance(
            							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
            							buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix), 
            							"=", 
            							returnVariable);

            	// Post it
            	solver.post(c);
            }
            return returnVariable;
        }
        //"*"
        else if (treeString.equalsIgnoreCase(culture.MULTIPLY()))
        {
        	Constraint c = null;
        	System.out.println("Got a multiplication");
        	// TODO: Do a calculation of possible bounds...
        	IntVar returnVariable = VariableFactory.bounded("tmp", 0, 10000, solver);
        	
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
            	else
            	{
	        		if (isChild0Numeric)
	            	{
	            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
	                	c = IntConstraintFactory.times(
    							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
    							intChild0, 
    							returnVariable);
	            	}
	        		if (isChild1Numeric)
	                {
	        			intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
	                	c = IntConstraintFactory.times(
    							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
    							intChild1, 
    							returnVariable);
	                }
            	}
            	// Post the constraint and return the new intermediate variable
            	c = IntConstraintFactory.times(
            							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
            							buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix), 
            							returnVariable);

            	// Post it
            	solver.post(c);
            	
            }
            return returnVariable;
        }
        //"/"
        else if (treeString.equalsIgnoreCase(culture.DIVIDE())|| treeString.equalsIgnoreCase(culture.QUOTIENT()))
        {
        	Constraint c = null;
        	System.out.println("Got a division");
        	// TODO: Do a calculation of possible bounds...
        	IntVar returnVariable = VariableFactory.bounded("tmp", 0, 10000, solver);
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
            	else
            	{
	        		if (isChild0Numeric)
	            	{
	            		intChild0 = IntegerUtilities.parseToInt(tree.getChild(0).toStringTree());
	            		// Create a new variable
	            		IntVar c0var = VariableFactory.bounded("tmp", intChild0, intChild0, solver);
	                	c = IntConstraintFactory.eucl_div(
    							c0var, 
    							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
    							returnVariable);
	            	}
	        		if (isChild1Numeric)
	                {
	        			intChild1 = IntegerUtilities.parseToInt(tree.getChild(1).toStringTree());
	                	// Post the constraint and return the new intermediate variable
	            		IntVar c1var = VariableFactory.bounded("tmp", intChild1, intChild1, solver);
	                	c = IntConstraintFactory.eucl_div(
	                							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
	                							c1var, 
	                							returnVariable);

	                	// Post it
	                	solver.post(c);
	                }
            	}
            	// Post the constraint and return the new intermediate variable
            	c = IntConstraintFactory.eucl_div(
            							buildExpression(cellReference, tree.getChild(0), worksheetReferencePrefix), 
            							buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix), 
            							returnVariable);

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
            	System.out.println("Got a min");
            	// TODO: Do a calculation of possible bounds...
            	IntVar returnVariable = VariableFactory.bounded("tmp", 0, 10000, solver);
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
        	System.out.println("Got an if");
        	// TODO: Do a calculation of possible bounds...
        	IntVar returnVariable = VariableFactory.bounded("tmp", 0, 10000, solver);

        	if ((tree.getChild(0) != null) && (tree.getChild(1) != null) && (tree.getChild(2) != null))
            {
        		
        		IntVar exp1 = buildExpression(cellReference, tree.getChild(1), worksheetReferencePrefix);
        		IntVar exp2 = buildExpression(cellReference, tree.getChild(2), worksheetReferencePrefix);
        		
        		// Create tmp vars to connect the results
        		IntVar tmp1 = VariableFactory.bounded("tmp1", MIN_VALUE, 1000, this.solver);
        		IntVar tmp2 = VariableFactory.bounded("tmp2", MIN_VALUE, 1000, this.solver);
        		
        		Constraint c1 = IntConstraintFactory.arithm(exp1, "=", tmp1);
        		Constraint c2 = IntConstraintFactory.arithm(exp2, "=", tmp2);
        		
        		c = LogicalConstraintFactory.ifThenElse(
        				buildConstraint(cellReference, tree.getChild(0), worksheetReferencePrefix), 
        				c1, 
        				c2);
        		
        		solver.post(c);

        		// Add another if then else to connect the binding with the return value
        		Constraint c_logic = LogicalConstraintFactory.ifThenElse(
        				IntConstraintFactory.arithm(tmp1, "!=", MIN_VALUE), 
        				IntConstraintFactory.arithm(returnVariable, "=", exp1), 
        				IntConstraintFactory.arithm(returnVariable, "=", exp2));

        		solver.post(c_logic);
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
        	System.out.println("Looking for variable: " + variableName);
    		if (FormulaParser.isNumeric(tree.toString())) {
    			System.out.println("We got a constant");
        		System.out.println("Creating tmp and posting it" );
        		returnVariable = VariableFactory.bounded("tmp", 0, 100, solver);
        		this.variablesMap.put(variableName, returnVariable);
    			// Post an equality constraint
    			Integer value = Integer.parseInt(tree.toString());
    			solver.post(IntConstraintFactory.arithm(returnVariable, "=", value));
    			return returnVariable;
    		}
        	returnVariable = this.variablesMap.get(variableName);
        	if (returnVariable == null) {
        		// TODO: Use the specific domains defined for the variable
        		System.out.println("Creating " + variableName);
        		returnVariable = VariableFactory.bounded(variableName, 0, 100, solver);
        		this.variablesMap.put(variableName, returnVariable);
        	}
        	
        	try {
				graph.addEdge(variableName, cellReference);
			} catch (Exception e) {
				e.printStackTrace();
			}
//        	return variables.get(variableName);
        	System.out.println("Returning variable " + variableName);
        	return returnVariable;
        }  
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
						IntVar variable = this.variablesMap.get(name);
						if (variable == null) {
							variable = VariableFactory.bounded(name, 0, 1000, this.solver);
							this.variablesMap.put(name, variable);
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
		IntVar returnVariable = VariableFactory.bounded("tmp", 0, 1000, this.solver);
		Constraint c = IntConstraintFactory.sum(args, returnVariable);
		solver.post(c);
		
		return returnVariable;
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
            		System.out.println("Creating an if");
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
            		return (Constraint) this.variablesMap.get(variableName);
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
	 * Tree copy..
	 * @param original
	 * @return
	 */
	 private static CommonTree copyTreeRecursive(CommonTree original) {

		    CommonTree copy = new CommonTree(original); // Leverage constructor

		    if(original.getChildren() != null) {
		      for(Object o : original.getChildren()) {
		        CommonTree childCopy  = copyTreeRecursive((CommonTree)o);
		        childCopy.setParent(copy);
		        copy.addChild(childCopy);
		      }
		    };
		    return copy;
		  }

}
