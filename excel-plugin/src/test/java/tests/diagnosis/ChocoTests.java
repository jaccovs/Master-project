package tests.diagnosis;

import java.util.ArrayList;

import org.exquisite.tools.Utilities;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.ImpactBasedBranching;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.solver.ContradictionException;

public class ChocoTests {
	
	final int MIN = 0;
	final int INPUT_MAX = 10000000; 
	final int B1_MAX = 20000000;
	final int B2_MAX = 30000000;
	final int C1_MAX = 1000000;
	
	public static void main(String[] args)
	{
		new ChocoTests().run();
	}
	
	public void run()
	{		
		//domainViolationTest();
		//realNumberTest();
		//wennParserTest();
		//sumParserTest();
		//divisionTest();
//		testcaseGenTest();
		variableTest();
	}
	
	private void variableTest()
	{
		final int DomainMin = 0;
		final int DomainMax = 100000;
		//Inputs
		IntegerExpressionVariable a1 = Choco.makeIntVar("WS_1_A1", DomainMin, DomainMax, Options.V_BOUND, Options.V_NO_DECISION);
		IntegerExpressionVariable b1 = Choco.makeIntVar("WS_1_B1", DomainMin, DomainMax, Options.V_BOUND, Options.V_NO_DECISION);
		IntegerExpressionVariable c1 = Choco.makeIntVar("WS_1_C1", DomainMin, DomainMax, Options.V_BOUND);
		
		IntegerExpressionVariable a2 = Choco.makeIntVar("WS_1_A2", DomainMin, DomainMax, Options.V_BOUND, Options.V_NO_DECISION);		
		IntegerExpressionVariable b2 = Choco.makeIntVar("WS_1_B2", DomainMin, DomainMax, Options.V_BOUND, Options.V_NO_DECISION);
		
		IntegerExpressionVariable a3 = Choco.makeIntVar("WS_1_A3", DomainMin, DomainMax, Options.V_BOUND);		
		IntegerExpressionVariable b3 = Choco.makeIntVar("WS_1_B3", DomainMin, DomainMax, Options.V_BOUND);
		IntegerExpressionVariable c3 = Choco.makeIntVar("WS_1_C3", DomainMin, DomainMax, Options.V_BOUND);
		
		IntegerExpressionVariable a5 = Choco.makeIntVar("WS_1_A5", DomainMin, DomainMax, Options.V_BOUND);
		IntegerExpressionVariable b5 = Choco.makeIntVar("WS_1_B5", DomainMin, DomainMax, Options.V_BOUND, Options.V_NO_DECISION);
		IntegerExpressionVariable c5 = Choco.makeIntVar("WS_1_C5", DomainMin, DomainMax, Options.V_BOUND);
		
		IntegerExpressionVariable a6 = Choco.makeIntVar("WS_1_A6", DomainMin, DomainMax, Options.V_BOUND, Options.V_NO_DECISION);		
		IntegerExpressionVariable b6 = Choco.makeIntVar("WS_1_B6", DomainMin, DomainMax, Options.V_BOUND, Options.V_NO_DECISION);
		
		IntegerExpressionVariable a7 = Choco.makeIntVar("WS_1_A7", DomainMin, DomainMax, Options.V_BOUND);		
		IntegerExpressionVariable b7 = Choco.makeIntVar("WS_1_B7", DomainMin, DomainMax, Options.V_BOUND);
		IntegerExpressionVariable c7 = Choco.makeIntVar("WS_1_C7", DomainMin, DomainMax, Options.V_BOUND);
		
		IntegerExpressionVariable a9 = Choco.makeIntVar("WS_1_A9", DomainMin, DomainMax, Options.V_BOUND);
		IntegerExpressionVariable b9 = Choco.makeIntVar("WS_1_B9", DomainMin, DomainMax, Options.V_BOUND, Options.V_NO_DECISION);
		IntegerExpressionVariable c9 = Choco.makeIntVar("WS_1_C9", DomainMin, DomainMax, Options.V_BOUND);
		
		IntegerExpressionVariable a10 = Choco.makeIntVar("WS_1_A10", DomainMin, DomainMax, Options.V_BOUND, Options.V_NO_DECISION);		
		IntegerExpressionVariable b10 = Choco.makeIntVar("WS_1_B10", DomainMin, DomainMax, Options.V_BOUND, Options.V_NO_DECISION);
		
		IntegerExpressionVariable a11 = Choco.makeIntVar("WS_1_A11", DomainMin, DomainMax, Options.V_BOUND);		
		IntegerExpressionVariable b11 = Choco.makeIntVar("WS_1_B11", DomainMin, DomainMax, Options.V_BOUND);
		IntegerExpressionVariable c11 = Choco.makeIntVar("WS_1_C11", DomainMin, DomainMax, Options.V_BOUND);
		
		IntegerExpressionVariable a13 = Choco.makeIntVar("WS_1_A13", DomainMin, DomainMax, Options.V_BOUND);		
		IntegerExpressionVariable b13 = Choco.makeIntVar("WS_1_B13", DomainMin, DomainMax, Options.V_BOUND);
		IntegerExpressionVariable c13 = Choco.makeIntVar("WS_1_C13", DomainMin, DomainMax, Options.V_BOUND);
			
		// Add Inputs
		Constraint iA1 = Choco.eq(a1, 2);
		Constraint iA2 = Choco.eq(a2, 3);
		Constraint iA6 = Choco.eq(a6, 3);
		Constraint iA10 = Choco.eq(a10, 3);
		Constraint iB1 = Choco.eq(b1, 2);
		Constraint iB2 = Choco.eq(b2, 2);
		Constraint iB5 = Choco.eq(b5,3);
		Constraint iB6 = Choco.eq(b6, 5);
		Constraint iB9 = Choco.eq(b9, 5);
		Constraint iB10 = Choco.eq(b10, 5);
		
		//formula
		Constraint cA3 = Choco.eq(a3, Choco.mult(a1, a2));
		Constraint cB3 = Choco.eq(b3, Choco.mult(b1, b2));
		Constraint cC1 = Choco.eq(c1, Choco.plus(a1, b1));
		Constraint cC3 = Choco.eq(c3, Choco.plus(a3, b3));
		Constraint cA7 = Choco.eq(a7, Choco.mult(a5, a6));
		Constraint cB7 = Choco.eq(b7, Choco.mult(b5, b6));
		Constraint cC5 = Choco.eq(c5, Choco.plus(a5, b5));
		Constraint cC7 = Choco.eq(c7, Choco.plus(a7, b7));
		Constraint cA11 = Choco.eq(a11, Choco.mult(a9, a10));
		Constraint cB11 = Choco.eq(b11, Choco.mult(b9, b10));
		Constraint cC9 = Choco.eq(c9, Choco.plus(a9, b9));
		Constraint cC11 = Choco.eq(c11, Choco.plus(a11, b11));
		Constraint cA13 = Choco.eq(a13, Choco.plus(a3, Choco.plus(a7, a11)));
		Constraint cB13 = Choco.eq(b13, Choco.plus(b3, Choco.plus(b7, b11)));
		Constraint cC13 = Choco.eq(c13, Choco.plus(a13, 0));
		
		//expected value
		Constraint exC13 = Choco.eq(c13, 50);
		
		
		CPModel model = new CPModel();
		
		model.addVariable(a1);
		model.addVariable(a2);
		model.addVariable(a3);
		model.addVariable(a5);
		model.addVariable(a6);
		model.addVariable(a7);
		model.addVariable(a9);
		model.addVariable(a10);
		model.addVariable(a11);
		model.addVariable(a13);
		
		model.addVariable(b1);
		model.addVariable(b2);
		model.addVariable(b3);
		model.addVariable(b5);
		model.addVariable(b6);
		model.addVariable(b7);
		model.addVariable(b9);
		model.addVariable(b10);
		model.addVariable(b11);
		model.addVariable(b13);
		
		model.addVariable(c1);
		model.addVariable(c3);
		model.addVariable(c5);
		model.addVariable(c7);
		model.addVariable(c9);
		model.addVariable(c11);
		model.addVariable(c13);
				
//add constraints to model.
		model.addConstraint(iA1);
		model.addConstraint(iA2);
		model.addConstraint(iA6);
		model.addConstraint(iA10);
		
		model.addConstraint(iB1);
		model.addConstraint(iB2);
		model.addConstraint(iB5);
		model.addConstraint(iB6);
		model.addConstraint(iB9);
		model.addConstraint(iB10);
		
		
		model.addConstraint(cA3);
		model.addConstraint(cA7);
		model.addConstraint(cA11);
		model.addConstraint(cA13);
		
		model.addConstraint(cB3);
		model.addConstraint(cB7);
		model.addConstraint(cB11);
		model.addConstraint(cB13);
		
		model.addConstraint(cC1);
		model.addConstraint(cC3);
		model.addConstraint(cC5);
		model.addConstraint(cC7);
		model.addConstraint(cC9);
		model.addConstraint(cC11);
		model.addConstraint(cC13);
		
		model.addConstraint(exC13);
				
		
		CPSolver solver = new CPSolver();
		
//		solver.addGoal(new ImpactBasedBranching(solver));
		solver.read(model);
		solver.addGoal(new ImpactBasedBranching(solver));
		
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			System.out.println("Condradiction exception...");
			e.printStackTrace();
		}
		
		System.out.println("Start solve...");
		boolean hasSolution = solver.solve();	
		System.out.println("End solve.");
		System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));
		
	}
	/*
	private void testcaseGenTest()
	{
		//Inputs
		IntegerExpressionVariable a1 = Choco.makeIntVar("WS_1_A1", 1, 10);
		IntegerExpressionVariable a2 = Choco.makeIntVar("WS_1_A2", 1, 10);
		IntegerExpressionVariable a3 = Choco.makeIntVar("WS_1_A3", 1, 10);
		IntegerExpressionVariable a4 = Choco.makeIntVar("WS_1_A4", 1, 10);
		IntegerExpressionVariable a5 = Choco.makeIntVar("WS_1_A5", 1, 10);
		
		IntegerExpressionVariable d1 = Choco.makeIntVar("WS_1_D1", 1, 99);
		//Interims
		IntegerExpressionVariable b1 = Choco.makeIntVar("WS_1_B1", 0, 1000);
		IntegerExpressionVariable c1 = Choco.makeIntVar("WS_1_C1", 0, 1000);
		//outputs
		IntegerExpressionVariable f1 = Choco.makeIntVar("WS_1_F1", 1, 1000);
		
		
		
		Dictionary<String, IntegerExpressionVariable> vars = new Hashtable<String, IntegerExpressionVariable>();
		vars.put("WS_1_A1", a1);
		vars.put("WS_1_A2", a2);
		vars.put("WS_1_A3", a3);	
		vars.put("WS_1_A4", a4);	
		vars.put("WS_1_A5", a5);	
		vars.put("WS_1_B1", b1);	
		vars.put("WS_1_C1", c1);	
		vars.put("WS_1_D1", d1);	
		vars.put("WS_1_F1", f1);	

		
		FormulaParser parser = new FormulaParser(new ExquisiteGraph<String>());
		
		parser.parse("SUM(A1:A5)");		
		//IntegerExpressionVariable sum = parser.BuildExpression("", parser.FormulaTree, vars, "WS_1_");
		
		parser.parse("B1/2");		
		IntegerExpressionVariable div = parser.BuildExpression(parser.FormulaTree, vars, "WS_1_");
		
		parser.parse("(D1*100)/C1");		
		IntegerExpressionVariable percent = parser.BuildExpression(parser.FormulaTree, vars, "WS_1_");
		
		
		Constraint cellConstraint1 = Choco.eq(b1, sum);
		Constraint cellConstraint2 = Choco.eq(c1, div);
		Constraint cellConstraint3 = Choco.eq(f1, percent);
		
		// Add Inputs
		Constraint cA1 = Choco.eq(a1, 2);
		Constraint cA2 = Choco.eq(a2, 3);
		Constraint cA3 = Choco.eq(a3, 4);
		Constraint cA4 = Choco.eq(a4, 4);
		Constraint cA5 = Choco.eq(a5, 5);
		Constraint cD1 = Choco.eq(d1, 2);
		
		
		
		CPModel model = new CPModel();
		
		model.addVariable(a1);
		model.addVariable(a2);
		model.addVariable(a3);
		model.addVariable(a4);
		model.addVariable(a5);
		model.addVariable(b1);
		model.addVariable(c1);
		model.addVariable(d1);
		model.addVariable(f1);

		model.addConstraint(cellConstraint1);
		model.addConstraint(cellConstraint2);
		model.addConstraint(cellConstraint3);
		
		// Add Inputs to model
		model.addConstraint(cA1);
		model.addConstraint(cA2);
		model.addConstraint(cA3);
		model.addConstraint(cA4);
		model.addConstraint(cA5);
		model.addConstraint(cD1);
		
		CPSolver solver = new CPSolver();
		solver.read(model);
		System.out.println("Start solve...");
		boolean hasSolution = solver.solve();	
		System.out.println("End solve.");
		System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));
	}
	*/
	
	private void domainTest()
	{
		/*IntegerExpressionVariable a1 = Choco.makeIntVar("A1", 0, 10000);
		IntegerExpressionVariable a2 = Choco.makeIntVar("A2", 0, 10000);
		IntegerExpressionVariable b1 = Choco.makeIntVar("B1", 0, 10000);
		
		
		Dictionary<String, IntegerExpressionVariable> vars = new Hashtable<String, IntegerExpressionVariable>();
		vars.put("A1", a1);
		vars.put("A2", a2);
		vars.put("A3", b1);	

		
		FormulaParser parser = new FormulaParser();
		
		parser.parse("A1*A2");		
		IntegerExpressionVariable mul = parser.BuildExpression(parser.FormulaTree, vars, "");

		Constraint cellConstraint1 = Choco.eq(b1, mul);
		
		Constraint cB1 = Choco.eq(b1, 0);		
		
		CPModel model = new CPModel();
		
		model.addVariable(a1);
		model.addVariable(a2);
		model.addVariable(b1);

		model.addConstraint(cellConstraint1);
		model.addConstraint(cB1);
		
		
		CPSolver solver = new CPSolver();
		solver.read(model);
		System.out.println("Start solve...");
		boolean hasSolution = solver.solve();	
		System.out.println("End solve.");
		System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));*/
	}
	
	private void wennParserTest()
	{
		/*IntegerExpressionVariable a1 = Choco.makeIntVar("A1", 0, 100);
		IntegerExpressionVariable a2 = Choco.makeIntVar("A2", 0, 100);
		IntegerExpressionVariable a3 = Choco.makeIntVar("A3", 0, 100);
		IntegerExpressionVariable a4 = Choco.makeIntVar("A4", 0, 100);
		IntegerExpressionVariable b2 = Choco.makeIntVar("B2", 0, 100);
		
		IntegerExpressionVariable c1 = Choco.constant(10);
		IntegerExpressionVariable c2 = Choco.constant(20);
		
		Constraint cA1 = Choco.eq(a1, 2);
		Constraint cA2 = Choco.eq(a2, 4);
		Constraint cA3 = Choco.eq(a3, 20);
		Constraint cA4 = Choco.eq(a4, 50);
		
		Dictionary<String, IntegerExpressionVariable> vars = new Hashtable<String, IntegerExpressionVariable>();
		vars.put("A1", a1);
		vars.put("A2", a2);
		vars.put("A3", a3);
		vars.put("A4", a4);
		vars.put("B2", b2);
		vars.put("C1", c1);
		vars.put("C2", c2);
		
		FormulaParser parser = new FormulaParser();
		parser.parse("(A1/A2)");
		
		IntegerExpressionVariable summation = parser.BuildExpression(parser.FormulaTree, vars, "");
		//IntegerExpressionVariable summation = Choco.ifThenElse( Choco.neq(a1, a2), a3, a4);
		
		
		Constraint cellConstraint = Choco.eq(b2, summation);
		
		
		
		CPModel model = new CPModel();
		model.addVariable(a1);
		model.addVariable(a2);
		model.addVariable(a3);
		model.addVariable(a4);
		model.addVariable(b2);
		model.addVariable(c1);
		model.addVariable(c2);
		
		model.addVariable(summation);
		//System.out.println("???");
				
		model.addConstraint(cA1);
		model.addConstraint(cA2);
		model.addConstraint(cA3);
		model.addConstraint(cA4);
		model.addConstraint(cellConstraint);
		
		CPSolver solver = new CPSolver();
		solver.read(model);
		System.out.println("Start solve...");
		boolean hasSolution = solver.solve();	
		System.out.println("End solve.");
		System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));*/
		
	}
	
	private void divisionTest()
	{
		/*IntegerExpressionVariable a1 = Choco.makeIntVar("A1", 0, 10);
		IntegerExpressionVariable a2 = Choco.makeIntVar("A2", 0, 10);
		IntegerExpressionVariable a3 = Choco.makeIntVar("A3", 0, 1000000);
		IntegerExpressionVariable a4 = Choco.makeIntVar("A4", 0, 1000000);
		IntegerExpressionVariable a5 = Choco.makeIntVar("A5", 0, 1000000);
				
		Constraint cA1 = Choco.eq(a1, 10);
		Constraint cA2 = Choco.eq(a2, 3);
		Constraint cA3 = Choco.eq(a3, 4);
		
		Dictionary<String, IntegerExpressionVariable> vars = new Hashtable<String, IntegerExpressionVariable>();
		vars.put("A1", a1);
		vars.put("A2", a2);
		vars.put("A3", a3);	
		vars.put("A4", a4);	
		vars.put("A5", a5);	
		
		FormulaParser parser = new FormulaParser();
		
		parser.parse("A1+A2");		
		IntegerExpressionVariable plus = parser.BuildExpression(parser.FormulaTree, vars, "");
		
		parser.parse("A1*A2");		
		IntegerExpressionVariable mul = parser.BuildExpression(parser.FormulaTree, vars, "");
		
		
		parser.parse("A4/A3");
		
		IntegerExpressionVariable division = parser.BuildExpression(parser.FormulaTree, vars, "");
		
		
		
		Constraint cellConstraint1 = Choco.eq(a3, plus);
		Constraint cellConstraint2 = Choco.eq(a4, mul);
		Constraint cellConstraint3 = Choco.eq(a5, division);
		
		
		CPModel model = new CPModel();
		
		model.addVariable(a1);
		model.addVariable(a2);
		model.addVariable(a3);
		model.addVariable(a4);
		model.addVariable(a5);
		model.addVariable(plus);
		model.addVariable(division);
				
		model.addConstraint(cA1);
		model.addConstraint(cA2);
		//model.addConstraint(cA3);
		model.addConstraint(cellConstraint1);
		model.addConstraint(cellConstraint2);
		model.addConstraint(cellConstraint3);
		
		
		CPSolver solver = new CPSolver();
		solver.read(model);
		System.out.println("Start solve...");
		boolean hasSolution = solver.solve();	
		System.out.println("End solve.");
		System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));*/
	}
	
	private void sumParserTest()
	{
		/*IntegerExpressionVariable a1 = Choco.makeIntVar("A1", 0, 100);
		IntegerExpressionVariable a2 = Choco.makeIntVar("A2", 0, 100);
		IntegerExpressionVariable a3 = Choco.makeIntVar("A3", 0, 100);
		IntegerExpressionVariable b2 = Choco.makeIntVar("B2", 0, 100);
		
		Constraint cA1 = Choco.eq(a1, 2);
		Constraint cA2 = Choco.eq(a2, 3);
		Constraint cA3 = Choco.eq(a3, 4);
		
		Dictionary<String, IntegerExpressionVariable> vars = new Hashtable<String, IntegerExpressionVariable>();
		vars.put("A1", a1);
		vars.put("A2", a2);
		vars.put("A3", a3);
		vars.put("B2", b2);
		
		FormulaParser parser = new FormulaParser();
		parser.parse("SUMME(A1;A2;A3)");
		
		IntegerExpressionVariable summation = parser.BuildExpression(parser.FormulaTree, vars, "");
		
		Constraint cellConstraint = Choco.eq(b2, summation);		
		
		CPModel model = new CPModel();
		model.addVariable(a1);
		model.addVariable(a2);
		model.addVariable(a3);
		model.addVariable(b2);
		model.addVariable(summation);
				
		model.addConstraint(cA1);
		model.addConstraint(cA2);
		model.addConstraint(cA3);
		model.addConstraint(cellConstraint);
		
		CPSolver solver = new CPSolver();
		solver.read(model);
		System.out.println("Start solve...");
		boolean hasSolution = solver.solve();	
		System.out.println("End solve.");
		System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));
		*/
	}
	
	private void sumTest()
	{
		IntegerExpressionVariable[] vars = new IntegerExpressionVariable[4];
		vars[0] = Choco.makeIntVar("a0", 0, 100);
		vars[1] = Choco.makeIntVar("a1", 0, 100);
		vars[2] = Choco.makeIntVar("a2", 0, 100);
		vars[3] = Choco.mult(vars[0], vars[1]);
		
		ArrayList<IntegerExpressionVariable> listVars = new ArrayList<IntegerExpressionVariable>();
		listVars.add(vars[0]);
		listVars.add(vars[1]);
		listVars.add(vars[2]);
		listVars.add(vars[3]);
		
		IntegerExpressionVariable b2 = Choco.makeIntVar("b2", 0, 100);
			
		IntegerExpressionVariable summation = Choco.sum(vars);
		Constraint cellConstraint = Choco.eq(b2, summation);
		
		Constraint cA0 = Choco.eq(vars[0], 2);
		Constraint cA1 = Choco.eq(vars[1], 2);
		Constraint cA2 = Choco.eq(vars[2], 2);
		
		CPModel model = new CPModel();
		model.addVariable(vars[0]);
		model.addVariable(vars[1]);
		model.addVariable(vars[2]);
		model.addVariable(b2);
		model.addVariable(summation);
		
		model.addConstraint(cA0);
		model.addConstraint(cA1);
		model.addConstraint(cA2);
		model.addConstraint(cellConstraint);
		
		CPSolver solver = new CPSolver();
		solver.read(model);
		System.out.println("Start solve...");
		boolean hasSolution = solver.solve();	
		System.out.println("End solve.");
		System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));
		
	}
	
	/*private void realNumberTest()
	{		
		PreProcessCPSolver solver = new PreProcessCPSolver();
		
		RealExpressionVariable a1 = Choco.makeRealVar("a1", 0, 1000);
		RealExpressionVariable a2 = Choco.makeRealVar("a2", MIN, INPUT_MAX);
		RealExpressionVariable b1 = Choco.makeRealVar("b1", MIN, B1_MAX);
		RealExpressionVariable b2 = Choco.makeRealVar("b2", MIN, B2_MAX);
		RealExpressionVariable c1 = Choco.makeRealVar("c1", MIN, C1_MAX);
		
		//constraints representing formulae
		Constraint con1 = Choco.eq(b1, Choco.mult(a1, 2)); //max 2 * INPUT_MAX
		
		
		//Constraint con2 = Choco.eq(b2, Choco.mult(a2, 3)); //max 3 * INPUT_MAX		
		//Constraint con3 = Choco.eq(c1, Choco.minus(b2, b1)); //max for B2_MAX - B1_MAX = C1_MAX
		//the fault - which is used in the second run...
		//Constraint con4 = Choco.eq(c1, Choco.mult(b1, b2)); 
		
		//a positive test case...
		//Constraint correctStatement1 =Choco.eq(a1, 10);
		//Constraint correctStatement2 =Choco.eq(a2, 10);
		//Constraint correctStatement3 =Choco.eq(c1, 10);
		
		//Create "working" model with no fault.
		CPModel model = new CPModel();
		model.addVariable(a1);
		model.addVariable(a2);
		model.addVariable(b1);
		model.addVariable(b2);
		model.addVariable(c1);
		
		model.addConstraint(con1);
		//model.addConstraint(con2);
		//model.addConstraint(con3);
		
		model.addConstraint(correctStatement1);
		model.addConstraint(correctStatement2);
		model.addConstraint(correctStatement3);
		
		solver.read(model);
		System.out.println("Start solve...");
		boolean hasSolution = solver.solve();	
		System.out.println("End solve.");
		System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));
		
	}*/
	
	
	private void domainViolationTest()
	{		
		//Note: moving the domain into the 10000s causes an out of memory exception to be thrown.
		
		
		//make some variables...
		IntegerVariable a1 = Choco.makeIntVar("a1", MIN, INPUT_MAX);//, Options.V_BOUND);
		IntegerVariable a2 = Choco.makeIntVar("a2", MIN, INPUT_MAX);//, Options.V_BOUND);
		IntegerVariable b1 = Choco.makeIntVar("b1", MIN, B1_MAX);//, Options.V_BOUND);
		IntegerVariable b2 = Choco.makeIntVar("b2", MIN, B2_MAX);//, Options.V_BOUND);
		IntegerVariable c1 = Choco.makeIntVar("c1", MIN, C1_MAX);//, Options.V_BOUND);
				
		//constraints representing formulae
		Constraint con1 = Choco.eq(b1, Choco.mult(a1, 2)); //max 2 * INPUT_MAX
		Constraint con2 = Choco.eq(b2, Choco.mult(a2, 3)); //max 3 * INPUT_MAX		
		Constraint con3 = Choco.eq(c1, Choco.minus(b2, b1)); //max for B2_MAX - B1_MAX = C1_MAX
		//the fault - which is used in the second run...
		Constraint con4 = Choco.eq(c1, Choco.mult(b1, b2)); 
		
		//a positive test case...
		Constraint correctStatement1 =Choco.eq(a1, 10);
		Constraint correctStatement2 =Choco.eq(a2, 10);
		Constraint correctStatement3 =Choco.eq(c1, 10);
		
		//A value bound constraint NB: Not adding to model, as it was not making a big difference.
		Constraint c1LowerBound = Choco.geq(c1, MIN);
		Constraint c1UpperBound = Choco.leq(c1, C1_MAX);
		Constraint c1ValueBound = Choco.and(c1LowerBound, c1UpperBound);
		
		//Create "working" model with no fault.
		CPModel model = new CPModel();
		model.addVariable(a1);
		model.addVariable(a2);
		model.addVariable(b1);
		model.addVariable(b2);
		model.addVariable(c1);
		
		model.addConstraint(con1);
		model.addConstraint(con2);
		model.addConstraint(con3);
		
		model.addConstraint(correctStatement1);
		model.addConstraint(correctStatement2);
		model.addConstraint(correctStatement3);
		
		model.addConstraint(c1ValueBound);
		//model.addConstraint(c1LowerBound);
		//model.addConstraint(c1UpperBound);		
		
		//make the call to a solver...
		CPSolver solver = new CPSolver();
		try{
		solver.read(model);
		}
		catch(OutOfMemoryError e)
		{
			e.printStackTrace();
		}
		System.out.println("Start solve...");
		//boolean hasSolution = solver.solve();	
		System.out.println("End solve.");
		//System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));
		
		
		//Now run again but with a model that contains the faulty constraint instead.
		CPModel faultyModel = new CPModel();
		faultyModel.addVariable(a1);
		faultyModel.addVariable(a2);
		faultyModel.addVariable(b1);
		faultyModel.addVariable(b2);
		faultyModel.addVariable(c1);
		
		faultyModel.addConstraint(con1);
		faultyModel.addConstraint(con2);
		faultyModel.addConstraint(con4); //the faulty statement.
		
		faultyModel.addConstraint(correctStatement1);
		faultyModel.addConstraint(correctStatement2);
		faultyModel.addConstraint(correctStatement3);
		
		faultyModel.addConstraint(c1ValueBound);
		//faultyModel.addConstraint(c1LowerBound);
		//faultyModel.addConstraint(c1UpperBound);	
		
		
		solver = new CPSolver();
		//solver.read(faultyModel);
		System.out.println("Start solve with faulty model...");
		//hasSolution = solver.solve();	
		System.out.println("End solve.");
		//System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));		
	}	
	
	private void realNumberTest()
	{		
		//PreProcessCPSolver solver = new PreProcessCPSolver();
		
		RealExpressionVariable a1 = Choco.makeRealVar("a1", 0, 1000);
		RealExpressionVariable a2 = Choco.makeRealVar("a2", MIN, INPUT_MAX);
		RealExpressionVariable b1 = Choco.makeRealVar("b1", MIN, B1_MAX);
		RealExpressionVariable b2 = Choco.makeRealVar("b2", MIN, B2_MAX);
		RealExpressionVariable c1 = Choco.makeRealVar("c1", MIN, C1_MAX);
		
		//constraints representing formulae
		Constraint con1 = Choco.eq(b1, Choco.mult(a1, 2)); //max 2 * INPUT_MAX
		
		
		Constraint con2 = Choco.eq(b2, Choco.mult(a2, 3)); //max 3 * INPUT_MAX		
		Constraint con3 = Choco.eq(c1, Choco.minus(b2, b1)); //max for B2_MAX - B1_MAX = C1_MAX
		//Constraint con3 = Choco.eq(c1, Choco.mult(b1, Choco.power(b2, -1)));
		//the fault - which is used in the second run...
		//Constraint con4 = Choco.eq(c1, Choco.mult(b1, b2)); 
		
		//a positive test case...
		Constraint correctStatement1 = Choco.eq(a1, 10);
		Constraint correctStatement2 = Choco.eq(a2, 100);
		Constraint correctStatement3 = Choco.eq(c1, 10);
		
		//Create "working" model with no fault.
		CPModel model = new CPModel();
		model.addVariable(a1);
		model.addVariable(a2);
		model.addVariable(b1);
		model.addVariable(b2);
		model.addVariable(c1);
		
		model.addConstraint(con1);
		model.addConstraint(con2);
		model.addConstraint(con3);
		//model.addConstraint(con4);
		
		model.addConstraint(correctStatement1);
		model.addConstraint(correctStatement2);
		model.addConstraint(correctStatement3);
		
		CPSolver solver = new CPSolver();
		solver.read(model);
		System.out.println("Start solve...");
		boolean hasSolution = solver.solve();	
		System.out.println("End solve.");
		System.out.println("Has solution: " + hasSolution);	
		System.out.println(Utilities.printSolution(solver));
		
	}
}
