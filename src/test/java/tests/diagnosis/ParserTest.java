package tests.diagnosis;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.i8n.Culture;
import org.exquisite.parser.ExcelLexer;
import org.exquisite.parser.ExcelParser;
import org.exquisite.parser.FormulaParser;
import org.exquisite.tools.StringUtilities;
import org.exquisite.tools.Utilities;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class ParserTest 
{
	public static void main(String[] args) {
		
		System.out.println("Starting Parser test");
		new ParserTest().run();
		System.out.println("ParserTest ended");
		//*/
		/*FormulaParser formulaParser = new FormulaParser();
		formulaParser.parse("A1+A2");
		
		Dictionary<String, IntegerExpressionVariable> decisionVars = new Hashtable<String, IntegerExpressionVariable>();
		decisionVars.put("A1", Choco.makeIntVar("A1"));
		decisionVars.put("A2", Choco.makeIntVar("A2"));
		
		formulaParser.BuildChocoStatements("id", "", formulaParser.FormulaTree, decisionVars, "");
		Constraint constraint = formulaParser.constraints.get("id");
		
		System.out.println("constraint: " + constraint.toString());*/
		
	}
	
	private static CommonTree BuildAntlrTree(String cellReference, String formula)
    {
        ANTLRStringStream Input = new ANTLRStringStream(formula);
        ExcelLexer Lexer = new ExcelLexer(Input);
        CommonTokenStream Tokens = new CommonTokenStream(Lexer);
        ExcelParser parser = new ExcelParser(Tokens);
        //parser
        parser.setTreeAdaptor(new CommonTreeAdaptor());
        CommonTree tree = null;
		try {
			tree = (CommonTree)parser.parse().getTree();
		} catch (RecognitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return tree;
	}
	
	public static String BuildExpression(Tree tree)
    {
        if (tree == null)
            return null;

        int intChild1;
        boolean isChild1Numeric = false;

        switch (tree.toString().toUpperCase())
        {
            case "ROOT":
            case "FUNC":
                return BuildExpression(tree.getChild(0));
            case "+":
                if ((tree.getChild(0) != null) && (tree.getChild(1) != null))
                {
                	isChild1Numeric = FormulaParser.isNumeric(tree.getChild(1).toStringTree());
					if (isChild1Numeric) {
						intChild1 = Integer.parseInt(tree.getChild(1).toStringTree());
						return "(Choco.plus(" + BuildExpression(tree.getChild(0)) + ", " + intChild1 + "))";
					}
					return "(Choco.plus(" + BuildExpression(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) + "))";
                }
                return null;
			case "-":
				if ((tree.getChild(0) != null) && (tree.getChild(1) != null)) {
                	isChild1Numeric = FormulaParser.isNumeric(tree.getChild(1).toStringTree());
                	if (isChild1Numeric)
                    {
                    	intChild1 = Integer.parseInt(tree.getChild(1).toStringTree());
                    	return "(Choco.minus(" + BuildExpression(tree.getChild(0)) + ", " + intChild1 + "))";
                    }
                	return "(Choco.minus(" + BuildExpression(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) + "))";
                }
                return null;
            case "*":
            	if ((tree.getChild(0) != null) && (tree.getChild(1) != null))
                {
            		isChild1Numeric = FormulaParser.isNumeric(tree.getChild(1).toStringTree());
            		if (isChild1Numeric)
                    {
            			intChild1 = Integer.parseInt(tree.getChild(1).toStringTree());
            			return "(Choco.mult(" + BuildExpression(tree.getChild(0)) + ", " + intChild1 + "))";
                    }
            		return "(Choco.mult(" + BuildExpression(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) + "))";
                }
                return null;
            case "/":
            	if ((tree.getChild(0) != null) && (tree.getChild(1) != null))
                {
            		isChild1Numeric = FormulaParser.isNumeric(tree.getChild(1).toStringTree());
            		if (isChild1Numeric)
                    {
            			intChild1 = Integer.parseInt(tree.getChild(1).toStringTree());
            			return "(Choco.div(" + BuildExpression(tree.getChild(0)) + ", " + intChild1 + "))";
                    }
            		return "(Choco.div(" + BuildExpression(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) + "))";
                }
                return null;
            case "SUM":
            case "SUMME":
            	return BuildPlusExpression(tree);
            case "WENN":
            	if ((tree.getChild(0) != null) && (tree.getChild(1) != null) && (tree.getChild(2) != null))
                {
            		return "(Choco.ifThenElse( " + BuildConstraint(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) + ", " + BuildExpression(tree.getChild(2)) + "))";
                } else if ((tree.getChild(0) != null) && (tree.getChild(1) != null)) {
					return "(Choco.ifThenElse( " + BuildConstraint(tree.getChild(0)) + ", " + BuildExpression(
							tree.getChild(1)) + ", ))";
				}
				return null;
            default:
				return tree.toString();
		}
	}

	private static String BuildConstraint(Tree tree) {
		switch (tree.toString())
        {
            case "ROOT":
                return BuildConstraint(tree.getChild(0));
            case "==":
            	return "Choco.eq(" + BuildExpression(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) +")";
            case ">":
            	return "Choco.gt(" + BuildExpression(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) +")";
            case "<":
            	return "Choco.lt(" + BuildExpression(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) +")";
            case ">=":
            	return "Choco.geq(" + BuildExpression(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) +")";
            case "<=":
            	return "Choco.leq(" + BuildExpression(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) +")";
            case "<>":
            	return "Choco.neq(" + BuildExpression(tree.getChild(0)) + ", " + BuildExpression(tree.getChild(1)) +")";
            default:
            	return tree.toString();
        }
	}

	private static String BuildPlusExpression(Tree tree) {
		return BuildPlusExpression(tree, 0);
	}

	private static String BuildPlusExpression(Tree tree, int index) {
		if (tree == null)
            return null;
		int childCount = tree.getChildCount();
		String sumExp = "";

		if (index < childCount){
			sumExp += "(Choco.plus(";
			String childString = tree.getChild(index).toString();
			if(childString.contains(":")){
				List<String> cells = StringUtilities.rangeToCells(childString);
				if (index == childCount -1){
					return BuildPlusExpressionFromRange (cells);
				} else {
					sumExp += BuildPlusExpressionFromRange(cells) + ", " + BuildPlusExpression(tree, index + 1);
				}
			} else {
				if (index == childCount -1){
					return BuildExpression(tree.getChild(index));
				} else {
					sumExp += BuildExpression(tree.getChild(index)) + ", " + BuildPlusExpression(tree, index + 1);
				}
			}
			sumExp += "))";
		}
		return sumExp;
	}
	
	private static String BuildPlusExpressionFromRange(List<String> cells) {
		return BuildPlusExpressionFromRange(cells, 0);
	}

	private static String BuildPlusExpressionFromRange(List<String> cells, int index) {
		if (cells == null)
            return null;
		int count = cells.size();
		String plusExp = "";
		if (index < count){
			if (index == count -1){
				plusExp += cells.get(index);
			} else {
				plusExp += "(Choco.plus(" + cells.get(index) + ", " + BuildPlusExpressionFromRange(cells, index +1) +  "))";
			}
		}
		return plusExp;
	}

	/**
	 * A runner method
	 */
	public void run() {
		try {
			testIsNumeric();
			//testGermanToEnglish();
			//testSumFunction();
			//testMultiplication();
			// Create a constraint model
			/*DiagnosisModel model = this.defineModelAndInputs();
			System.out.println("Created the model with examples");
			DiagnosisEngine hsdag = new DiagnosisEngine();
			hsdag.setMaxDepth(Config.searchDepth);
			hsdag.setMaxDiagnoses(Config.maxDiagnoses);
			hsdag.setModel(model);

			List<Diagnosis> diagnoses = hsdag.calculateDiagnosis();
			System.out.println("Found " + diagnoses.size() + " diagnoses");
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void testIsNumeric() {
		String test = "0.1";
		boolean result = (FormulaParser.isNumeric(test) == true);
		System.out.println("'" + test + "' is numeric? " + result);

	}

	private void testSumFunction() {
		Culture.setCulture(Locale.ENGLISH);
		String test = "SUM(1,2,A2)";
		FormulaParser formulaParser = new FormulaParser(new ExquisiteGraph<String>());
		formulaParser.parse(test);

		Dictionary<String, IntegerExpressionVariable> variables = new Hashtable<String, IntegerExpressionVariable>();
		String a1Name = "A1";
		IntegerVariable a1Variable = Choco.makeIntVar(a1Name, 0, 100, Options.V_BOUND);
		String a2Name = "A2";
		IntegerVariable variable = Choco.makeIntVar(a2Name, 0, 100, Options.V_BOUND);

		String b1Name = "B1";
		IntegerVariable b1Variable = Choco.makeIntVar(b1Name, 0, 100, Options.V_BOUND);

		variables.put(a1Name, a1Variable);
		variables.put(a2Name, variable);
		variables.put(b1Name, b1Variable);

		IntegerExpressionVariable result = formulaParser.buildExpression(b1Name, formulaParser.FormulaTree, variables);

		System.out.println("Result = " + result);
	}

	private void testMultiplication() {
		FormulaParser formulaParser = new FormulaParser(new ExquisiteGraph<String>());

		String b1Formula = "A1/2";
		formulaParser.parse(b1Formula);


		Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
		IntegerVariable a1 = new IntegerVariable("A1", 0, 1000);
		IntegerVariable b1 = new IntegerVariable("B1", 0, 1000);
		IntegerVariable c1 = new IntegerVariable("C1", 0, 1000);

		variablesMap.put("A1", a1);
		variablesMap.put("B1", b1);
		variablesMap.put("C1", c1);

		System.out.println("making a1 / 2");
		formulaParser.buildChocoStatements("a1*2", "B1", formulaParser.FormulaTree, variablesMap, "");
		Constraint b1Constraint = formulaParser.constraints.get("a1*2");

		String c1Formula = "A1*B1";
		formulaParser.parse(c1Formula);


		System.out.println("making a1 * b1");
		formulaParser.buildChocoStatements("a1*b1", "C1", formulaParser.FormulaTree, variablesMap, "");
		Constraint c1Constraint = formulaParser.constraints.get("a1*b1");

		CPSolver solver = new CPSolver();
		CPModel cpmodel = new CPModel();

		cpmodel.addVariable(a1);
		cpmodel.addVariable(b1);
		cpmodel.addVariable(c1);
		cpmodel.addConstraint(b1Constraint);
		cpmodel.addConstraint(c1Constraint);
		//cpmodel.addConstraint(Choco.eq(b1, Choco.mult(a1, 2)));
		//cpmodel.addConstraint(Choco.eq(c1, Choco.mult(a1, b1)));
		cpmodel.addConstraint(Choco.eq(a1, 6));
		//cpmodel.addConstraint(Choco.eq(b1, 6));
		//cpmodel.addConstraint(Choco.eq(c1, 18));

		solver.read(cpmodel);
		boolean result = solver.solve();
		System.out.println("result = " + result);
		System.out.println(solver.solutionToString());
		Utilities.printSolution(solver);


	}

	/**
	 * A method to define model
	 *
	 * @return
	 */
	DiagnosisModel<Constraint> defineModelAndInputs() {
		/*Hashtable<String, RealExpressionVariable> variables = new Hashtable<String, RealExpressionVariable>();
		variables.put("A1", Choco.makeRealVar("A1", 0, 10));
		variables.put("A2", Choco.makeRealVar("A2", 0, 10));
		variables.put("B1", Choco.makeRealVar("B1", 0, 10));
		variables.put("B2", Choco.makeRealVar("B2", 0, 10));
		variables.put("C1", Choco.makeRealVar("C1", 0, 10));

		String b1Expression = "A1*2";
		String b2Expression = "A2*3";
		String c1Expression = "B1*B2";

		FormulaParser formulaParser = new FormulaParser();

		formulaParser.parse(b1Expression);
		RealExpressionVariable parsedB1expression = formulaParser.BuildExpression(formulaParser.FormulaTree, variables);

		formulaParser.parse(b2Expression);
		RealExpressionVariable parsedB2expression = formulaParser.BuildExpression(formulaParser.FormulaTree, variables);

		formulaParser.parse(c1Expression);
		RealExpressionVariable parsedC1expression = formulaParser.BuildExpression(formulaParser.FormulaTree, variables);


		DiagnosisModel model = new DiagnosisModel();

		Iterator<RealExpressionVariable> iterator = variables.values().iterator();
		while(iterator.hasNext())
		{
			model.addRealVariable((RealVariable)iterator.next());
		}


		model.addPossiblyFaultyConstraint(Choco.eq(variables.get("B1"), parsedB1expression), b1Expression);
		model.addPossiblyFaultyConstraint(Choco.eq(variables.get("B2"), parsedB2expression), b2Expression);
		model.addPossiblyFaultyConstraint(Choco.eq(variables.get("C1"), parsedC1expression), c1Expression);

		String example1Test1 = "A1=1";
		String example1Test2 = "A2=6";
		String example1Test3 = "C1=20";

		String example2Test1 = "A1=4";
		String example2Test2 = "A2=5";
		String example2Test3 = "C1=23";

		String example3Test1 = "A1=15";
		String example3Test2 = "A2=10";
		String example3Test3 = "C1=60";


		/*Constraint parsedexample1Test1 = formulaParser.BuildConstraint(example1Test1, variables);
		Constraint parsedexample1Test2 = formulaParser.BuildConstraint(example1Test2, variables);
		Constraint parsedexample1Test3 = formulaParser.BuildConstraint(example1Test3, variables);

		Constraint parsedexample2Test1 = formulaParser.BuildConstraint(example2Test1, variables);
		Constraint parsedexample2Test2 = formulaParser.BuildConstraint(example2Test2, variables);
		Constraint parsedexample2Test3 = formulaParser.BuildConstraint(example2Test3, variables);

		Constraint parsedexample3Test1 = formulaParser.BuildConstraint(example3Test1, variables);
		Constraint parsedexample3Test2 = formulaParser.BuildConstraint(example3Test2, variables);
		Constraint parsedexample3Test3 = formulaParser.BuildConstraint(example3Test3, variables);

		Example pExample1 = new Example();
		pExample1.addConstraint(parsedexample1Test1, example1Test1);
		pExample1.addConstraint(parsedexample1Test2, example1Test2);
		pExample1.addConstraint(parsedexample1Test3, example1Test3);

		Example pExample2 = new Example();
		pExample2.addConstraint(parsedexample2Test1, example2Test1);
		pExample2.addConstraint(parsedexample2Test2, example2Test2);
		pExample2.addConstraint(parsedexample2Test3, example2Test3);

		Example pExample3 = new Example();
		pExample3.addConstraint(parsedexample3Test1, example3Test1);
		pExample3.addConstraint(parsedexample3Test2, example3Test2);
		pExample3.addConstraint(parsedexample3Test3, example3Test3);

		model.getPositiveExamples().add(pExample1);
		model.getPositiveExamples().add(pExample2);
		model.getPositiveExamples().add(pExample3);

		return model;*/
		return null;
	}

}
