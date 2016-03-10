package tests.ts;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.exquisite.data.ConstraintsFactory;
import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.parser.FormulaParser;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;

/**
 * Reconstructed version of a single choco execution taking 15 seconds
 * @author Thomas
 *
 */
public class ChocoLoopTest {
	
	private boolean useUnnecessaryConstraints = false;
	
	private CPModel model;
	private Solver solver;
//	private List<IntegerVariable> variables = new ArrayList<IntegerVariable>();
//	private List<Constraint> constraints = new ArrayList<Constraint>();
	
	private ConstraintsFactory constraintsFactory = new ConstraintsFactory();
	private FormulaParser formulaParser;
	
	private ExquisiteGraph<String> graph = new ExquisiteGraph<String>();
	
	private Dictionary<String, IntegerExpressionVariable> variables = new Hashtable<String, IntegerExpressionVariable>();
	private Dictionary<String, String> formulas = new Hashtable<String, String>();
	private Dictionary<String, Constraint> variableConstraints;
	private Dictionary<String, Constraint> formulaConstraints;
	
	private IntegerExpressionVariable makeVar(String name, int min, int max) {
		IntegerExpressionVariable var = Choco.makeIntVar("WS_1_" + name, min, max);
		model.addVariable(var);
		variables.put("WS_1_" + name, var);
		graph.addVertex("WS_1_" + name);
		
		// unnecessary?
		if (this.useUnnecessaryConstraints) {
			model.addConstraint(Choco.and(Choco.leq(var, max), Choco.geq(var, min)));
		}
		
		return var;
	}
	
	private void makeVars(int min, int max, String... names) {
		for (int i = 0; i < names.length; i++) {
			makeVar(names[i], min, max);
		}
	}
	
	private void addFormula(String cell, String formula) {
		formulas.put("WS_1_" + cell, formula);
	}
	
	private void addFormulas(String formulaString) {
		String[] fs = formulaString.split(" ");
		for (int i = 0; i < fs.length; i+=2) {
			addFormula(fs[i], fs[i+1]);
		}
	}
	
	private void addConstraints(Dictionary<String, Constraint> constraints) {
		Enumeration<Constraint> cs = constraints.elements();
//		int i = 0;
		while (cs.hasMoreElements()) {
			Constraint c = cs.nextElement();
//			System.out.println("" + i + ": " + c.pretty());
			model.addConstraint(c);
//			i++;
		}
	}
	
	private void buildBugModel() {
		model = new CPModel();
		solver = new CPSolver();
		formulaParser = new FormulaParser(graph);
		makeVars(0, 100000, "B4", "B5", "B6", "B7", "C4", "C5", "C6", "C7", "D4", "D5", "D6", "D7");
		makeVars(0, 100000, "E4", "E5", "E6", "E7", "E8");
		
		addFormulas("B4 3 B5 3 B6 1 B7 1 C4 10 C5 6 C6 3 C7 2 D4 5 D5 3 D6 1 D7 1 E8 27");
		
		variableConstraints = constraintsFactory.makeFormulae(formulas, formulaParser, variables);
		formulas = new Hashtable<String, String>();
		
		addFormula("E8", "E4+E5+E6*E7");
		
		formulaConstraints = constraintsFactory.makeFormulae(formulas, formulaParser, variables);
		
		addConstraints(variableConstraints);
		addConstraints(formulaConstraints);
	}

	private void buildLoopModel() {
		model = new CPModel();
		solver = new CPSolver();
		formulaParser = new FormulaParser(graph);
		makeVars(0, 3, "C9", "C10", "C11", "C12", "C13", "C14", "C15", "C16");
		makeVars(3, 6, "D9", "D10", "D11", "D12", "D13", "D14", "D15", "D16");
		makeVars(0, 20, "F9", "F10", "F11", "F12", "F13", "F14", "F15", "F16");
		makeVars(0, 20, "G9", "G10", "G11", "G12", "G13", "G14", "G15", "G16");
		makeVars(0, 20, "H9", "H10", "H11", "H12", "H13", "H14", "H15", "H16");
		makeVars(0, 20, "I9", "I10", "I11", "I12", "I13", "I14", "I15", "I16");
		makeVars(0, 20, "J9", "J10", "J11", "J12", "J13", "J14", "J15", "J16");
		makeVars(0, 20, "K9", "K10", "K11", "K12", "K13", "K14", "K15", "K16");
		makeVars(0, 20, "L9", "L10", "L11", "L12", "L13", "L14", "L15", "L16");
		makeVars(0, 20, "M9", "M10", "M11", "M12", "M13", "M14", "M15", "M16");
		makeVars(0, 20, "N9", "N10", "N11", "N12", "N13", "N14", "N15", "N16");
		makeVars(0, 20, "O9", "O10", "O11", "O12", "O13", "O14", "O15", "O16");
		makeVars(0, 20, "P9", "P10", "P11", "P12", "P13", "P14", "P15", "P16");
		makeVars(0, 20, "Q9", "Q10", "Q11", "Q12", "Q13", "Q14", "Q15", "Q16");
		makeVars(0, 240, "S9", "S10", "S11", "S12", "S13", "S14", "S15", "S16");
		makeVars(0, 1440, "T9", "T10", "T11", "T12", "T13", "T14", "T15", "T16");

//		int domainsize = 2000;
//		makeVars(0, domainsize, "C9", "C10", "C11", "C12", "C13", "C14", "C15", "C16");
//		makeVars(0, domainsize, "D9", "D10", "D11", "D12", "D13", "D14", "D15", "D16");
//		makeVars(0, domainsize, "F9", "F10", "F11", "F12", "F13", "F14", "F15", "F16");
//		makeVars(0, domainsize, "G9", "G10", "G11", "G12", "G13", "G14", "G15", "G16");
//		makeVars(0, domainsize, "H9", "H10", "H11", "H12", "H13", "H14", "H15", "H16");
//		makeVars(0, domainsize, "I9", "I10", "I11", "I12", "I13", "I14", "I15", "I16");
//		makeVars(0, domainsize, "J9", "J10", "J11", "J12", "J13", "J14", "J15", "J16");
//		makeVars(0, domainsize, "K9", "K10", "K11", "K12", "K13", "K14", "K15", "K16");
//		makeVars(0, domainsize, "L9", "L10", "L11", "L12", "L13", "L14", "L15", "L16");
//		makeVars(0, domainsize, "M9", "M10", "M11", "M12", "M13", "M14", "M15", "M16");
//		makeVars(0, domainsize, "N9", "N10", "N11", "N12", "N13", "N14", "N15", "N16");
//		makeVars(0, domainsize, "O9", "O10", "O11", "O12", "O13", "O14", "O15", "O16");
//		makeVars(0, domainsize, "P9", "P10", "P11", "P12", "P13", "P14", "P15", "P16");
//		makeVars(0, domainsize, "Q9", "Q10", "Q11", "Q12", "Q13", "Q14", "Q15", "Q16");
//		makeVars(0, domainsize, "S9", "S10", "S11", "S12", "S13", "S14", "S15", "S16");
//		makeVars(0, domainsize, "T9", "T10", "T11", "T12", "T13", "T14", "T15", "T16");

		// unnecessary
		if (this.useUnnecessaryConstraints) {
			makeVars(0, 720, "U9", "U10", "U11", "U12", "U13", "U14", "U15", "U16");
			makeVar("T19", 0, 2400);
			makeVar("T21", 0, 7200);
			makeVar("T22", 0, 14400);
		}
		
		makeVar("T20", 0, 14400);
		
		addFormulas("Q16 6 Q15 9 Q14 20 Q13 16 Q12 14 Q11 11 P16 0 Q10 6 P15 2 P14 17 P13 9 P12 3 P11 16 O16 5 P10 7 F9 4 O15 14 O14 11 O13 12 O12 5 O11 15 L9 9 N16 1 O10 8 N15 17 N14 7");
		addFormulas("N13 13 N12 8 N11 13 M16 8 N10 13 M15 6 M14 0 M13 18 M12 9 M11 11 L16 6 M10 12 L15 4 L14 17 L13 12 L12 15 L11 8 K16 5 L10 20 K15 15 K14 2 K13 3 K12 20 K11 6 J16 10 K10 3");
		addFormulas("J15 5 J14 11 J13 7 J12 8 J11 0 I16 19 J10 17 K9 6 I15 15 I14 14 I13 12 I12 2 I11 15 Q9 14 H16 5 I10 20 H15 14 H14 13 H13 2 H12 6 H11 10 G16 13 H10 17 G15 2 G14 4 G13 15");
		addFormulas("G12 5 G11 4 F16 8 G10 8 F15 16 F14 17 F13 3 F12 3 F11 12 F10 2 D9 5 D16 6 D15 6 J9 19 D14 6 D13 6 D12 3 D11 5 C16 3 P9 4 D10 5 C15 1 C14 0 C13 3 C12 1 C11 2 C10 1 C9 2");
		addFormulas("I9 4 O9 20 H9 7 N9 1 G9 18 M9 20");
		addFormula("T20", "4954");
		
		variableConstraints = constraintsFactory.makeFormulae(formulas, formulaParser , variables);
		formulas = new Hashtable<String, String>();
		
		addFormula("T9", "S9*D9");
		addFormula("T10", "S10*D10");
		addFormula("T11", "S11*D11");
		addFormula("T12", "S12*D12");
		addFormula("T13", "S13*D13");
		addFormula("T14", "S14*D14");
		addFormula("T15", "S15*D15");
		addFormula("T16", "S16*D16");
		
		// add formulas for the ConstraintsFactory or directly add Choco constraints to use sums 
		if (true)
		{

			addFormula("S9", "F9+G9+H9+I9+J9+K9+L9+M9+N9+O9+P9+Q9");
			addFormula("S10", "F10+G10+H10+I10+J10+K10+L10+M10+N10+O10+P10+Q10");
			addFormula("S11", "F11+G11+H11+I11+J11+K11+L11+M11+N11+O11+P11+Q11");
			addFormula("S13", "F13+G13+H13+I13+J13+K13+L13+M13+N13+O13+P13+Q13");

			addFormula("T20", "T9-T10+T11+T12+T13+T14+T15+T16");
		}
		else
		{

			formulaConstraints.put("S9", Choco.eq(variables.get("WS_1_S9") ,Choco.sum(variables.get("WS_1_F9"), variables.get("WS_1_G9"), variables.get("WS_1_H9"),
					variables.get("WS_1_I9"), variables.get("WS_1_J9"), variables.get("WS_1_K9"), variables.get("WS_1_L9"), variables.get("WS_1_M9"), 
					variables.get("WS_1_N9"), variables.get("WS_1_O9"), variables.get("WS_1_P9"), variables.get("WS_1_Q9"))));
			formulaConstraints.put("S10", Choco.eq(variables.get("WS_1_S10") ,Choco.sum(variables.get("WS_1_F10"), variables.get("WS_1_G10"), variables.get("WS_1_H10"),
					variables.get("WS_1_I10"), variables.get("WS_1_J10"), variables.get("WS_1_K10"), variables.get("WS_1_L10"), variables.get("WS_1_M10"), 
					variables.get("WS_1_N10"), variables.get("WS_1_O10"), variables.get("WS_1_P10"), variables.get("WS_1_Q10"))));
			formulaConstraints.put("S11", Choco.eq(variables.get("WS_1_S11") ,Choco.sum(variables.get("WS_1_F11"), variables.get("WS_1_G11"), variables.get("WS_1_H11"),
					variables.get("WS_1_I11"), variables.get("WS_1_J11"), variables.get("WS_1_K11"), variables.get("WS_1_L11"), variables.get("WS_1_M11"), 
					variables.get("WS_1_N11"), variables.get("WS_1_O11"), variables.get("WS_1_P11"), variables.get("WS_1_Q11"))));
			formulaConstraints.put("S13", Choco.eq(variables.get("WS_1_S13") ,Choco.sum(variables.get("WS_1_F13"), variables.get("WS_1_G13"), variables.get("WS_1_H13"),
					variables.get("WS_1_I13"), variables.get("WS_1_J13"), variables.get("WS_1_K13"), variables.get("WS_1_L13"), variables.get("WS_1_M13"), 
					variables.get("WS_1_N13"), variables.get("WS_1_O13"), variables.get("WS_1_P13"), variables.get("WS_1_Q13"))));

			formulaConstraints.put("T20", Choco.eq(variables.get("WS_1_T20"), Choco.sum(Choco.minus(variables.get("WS_1_T9"), variables.get("WS_1_T10")), 
					variables.get("WS_1_T11"), variables.get("WS_1_T12"), variables.get("WS_1_T13"), variables.get("WS_1_T14"), variables.get("WS_1_T15"), 
					variables.get("WS_1_T16"))));
		}
		
		formulaConstraints = constraintsFactory.makeFormulae(formulas, formulaParser, variables);
		
		addConstraints(variableConstraints);
		addConstraints(formulaConstraints);
		
//		System.out.println(model.pretty());
	}
	
	/**
	 * Runs the test.
	 * @param useUnnecessaryConstraints Should unnecessary constraints and variables be created as in the original execution?
	 */
	public void run(boolean useUnnecessaryConstraints) {		
		this.useUnnecessaryConstraints = useUnnecessaryConstraints;
		
		System.out.println("Using unnecessary constraints: " + this.useUnnecessaryConstraints);
		
		System.out.print("Building model...");
		
//		buildLoopModel();
		buildBugModel();
		
		solver.read(model);
//		solver.addGoal(new AssignVar( new MinDomain(solver), new IncreasingDomain() ));
//		solver.addGoal(new ImpactBasedBranching(solver) );
//		solver.addGoal(BranchingFactory.incDomWDeg(solver));
//		solver.addGoal(BranchingFactory.domDegBin(solver));		
//		solver.addGoal(BranchingFactory.domWDegBin(solver));
		
		System.out.println(" Done.");
		
		System.out.print("Propagating...");
		
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(" Done.");
		
		System.out.print("Solving...");
		long start = System.currentTimeMillis();
		solver.solve();
		long end = System.currentTimeMillis();
		long duration = end - start;
		
		System.out.println(" Done.");
		
		System.out.println("IsFeasible: " + solver.isFeasible());
		System.out.println("Solved in " + duration + "ms.");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChocoLoopTest loopTest = new ChocoLoopTest();
		loopTest.run(false);
		
		loopTest = new ChocoLoopTest();
		loopTest.run(true);
	}

}
