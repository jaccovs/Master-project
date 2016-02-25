package evaluations.plainconstraints;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import evaluations.tools.modelReader.ModelReader;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.tools.Utilities;
import parser.absconparseur.tools.InstanceParser;
import parser.chocogen.ChocoFactory;

import java.io.File;
import java.util.*;

/**
 * More utilities for test cases
 * 
 * @author dietmar
 * 
 */
public class PlainConstraintsUtilities {
	public static int globalSolveCounter = 0;
	// The internal prefix
	static String TMP_prefix = "TMP_";
	static String mutatedFilePostFix = "_mutated";

	/**
	 * Creates a model from the file
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static CPModel loadModel(String filename) throws Exception {
		CPModel model = new CPModel();

		/**
		 * User our own loader
		 */
		boolean extensionalConstraints = false;
		if (filename.endsWith("_ext.xml")
			|| 	filename.endsWith("_ext_mutated.xml")
			) {
			extensionalConstraints = true;
		}

//		System.out.println("Loading model: " + filename);

		if (!extensionalConstraints) {

			// Use the default API for loading the files
			InstanceParser parser = new InstanceParser();
			parser.loadInstance(new File(filename).getAbsolutePath());
			parser.parse(false);

			ChocoFactory chocoFactory = new ChocoFactory(parser, model);
			chocoFactory.createVariables();
			chocoFactory.createConstraints(true);

		} else {
			// Read the file
			ModelReader mr = new ModelReader(filename);
			mr.loadDocument();
			model = mr.cpmodel;
		}
		
		// TS: TEST!!!!!!!!
//		System.out.println("Constraints: " + model.getNbConstraints());
//		System.out.println("Variables  : " + model.getNbIntVars());
//		
//		Utilities.printOutConstraintsOfModel(model);

		return model;
	}

	/**
	 * Creates a filename with a defined postfix before the file type
	 * 
	 * @param filename
	 * @return the filename of the mutated file
	 */
	public static String getMutatedFileName(String filename) {
		String result = "";
		if (filename.endsWith(".xml")) {
			String origFileName = filename.substring(0, filename.length() - 4);
//			System.out.println("New file name: " + origFileName);
			result = origFileName + mutatedFilePostFix + ".xml";
		} else {
			result = filename + mutatedFilePostFix;
		}

		return result;
	}

	/**
	 * Creates a solution from the solver
	 * 
	 * @param solver
	 * @param nbVarsToSet
	 *            the number of variables to store; set to -1 if all (non-temp)
	 *            vars should be stored
	 * @return a mapping of var names to integers (works for intvars only)
	 */
	public static Map<String, Integer> storeSolution(Solver solver,
			int nbVarsToSet) {
		// System.out.println("Storing a solution " + nbVarsToSet);
		Map<String, Integer> result = new HashMap<String, Integer>();
		Iterator<IntDomainVar> it = solver.getIntVarIterator();
		List<IntDomainVar> vars = new ArrayList<IntDomainVar>();
		// Get the variables and remember them
		while (it.hasNext()) {
			IntDomainVar var = it.next();
			if (!var.getName().startsWith(TMP_prefix)
					&& !"".equals(var.getName())) {
				vars.add(var);
			}
		}
		// System.out.println("nb variables: " + vars.size());
		// Shuffle them and store the first n
		if (nbVarsToSet != -1) {
			Collections.shuffle(vars);
		}
		if (nbVarsToSet == -1) {
			nbVarsToSet = vars.size();
		}
		for (int i = 0; i < nbVarsToSet; i++) {
			IntDomainVar v = vars.get(i);
			// System.out.println("Adding var: " + v.getName());
			result.put(v.getName(), v.getVal());
		}

		return result;
	}

	/*
	 * A method that creates a diagnosis model object from the given plain
	 * constraint example
	 */
	public static DiagnosisModel<Constraint> createDiagnosisModel(CPModel cpmodel,
																  List<Map<String, Integer>> testCases)
			throws Exception {
		DiagnosisModel<Constraint> diagModel = new DiagnosisModel<Constraint>();

		// Create the variables
		Iterator<IntegerVariable> var_it = cpmodel.getIntVarIterator();
		IntegerVariable var = null;
		while (var_it.hasNext()) {
			var = var_it.next();
			diagModel.addIntegerVariable(var);
		}
		// Prepare a list by name
		List<Variable> variables = diagModel.getVariables();
		Map<String, Variable> varByName = new HashMap<String, Variable>();
		for (Variable v : variables) {
			varByName.put(v.getName(), v);
		}
		// Create the possibly faulty constraints
		List<Constraint> constraints = Utilities.getConstraints(cpmodel);
		for (Constraint c : constraints) {
			// System.out.println("Adding constraint: " + c.getName());
			diagModel.addPossiblyFaultyConstraint(c, c.getName());
		}

		Example<Constraint> ex;
		List<Example<Constraint>> posExamples = new ArrayList<>();
		for (Map<String, Integer> posTestCase : testCases) {
			ex = new Example<>();
			for (String key : posTestCase.keySet()) {
				Variable v = varByName.get(key);
				Constraint ct = Choco.eq((IntegerVariable) v,
						posTestCase.get(key).intValue());
				String name = "TC: " + v.getName() + "=" + posTestCase.get(key);
				ex.addConstraint(ct, name);
				// System.out.println("Added test case constraint: " + name);
			}
			posExamples.add(ex);
		}
		diagModel.setConsistentExamples(posExamples);
		return diagModel;
	}

	/**
	 * Checks if a test case is consistent with the model
	 * 
	 * @return true if the model is consistent (a solution can be found)
	 */
	public static boolean isTestCaseConsistent(Map<String, Integer> testcase,
			CPModel model) {
		boolean result = false;
		List<Constraint> addedConstraints = new ArrayList<Constraint>();

//		System.out.println("VARS Test case: " + testcase.keySet());
//		System.out.print("Known vars:");
//		int cntv = 0;
//		Iterator<IntegerVariable> it = model.getIntVarIterator();
//		while (it.hasNext()) {
//			IntegerVariable var = it.next();
//			System.out.print(var + " ");
//			cntv++;
//		}
//		System.out.println("\nNb vars in model: " + cntv);
//		System.out.println("Now the test cases");
//		
		// Add inputs to the model
		int cnt = 0;
		for (String vname : testcase.keySet()) {
			IntegerVariable var = Utilities.getIntVariableByName(model, vname);
			if (var == null) {
				System.err.println("Test case variable not found in model: "
						+ vname);
				System.exit(1);
			} else {
				Constraint ct = Choco.eq(var, testcase.get(vname).intValue());
				// System.out.println("Setting input: " +
				// testcase.get(vname).intValue() + " for " + vname);
				model.addConstraint(ct);
				addedConstraints.add(ct);
				cnt++;
			}
		}
		System.out.println("Set " + cnt + " value constraints");
		// Try to solve

		CPSolver solver = new CPSolver();
		solver.read(model);

		// System.out.println("Got intvars in model:" + solver.getNbIntVars());
		// System.out.println(Utilities.printSolution(solver));

		solver.solve();
		if (solver.isFeasible()) {
			System.out.println("Found a solution - test case consistent ");
			// System.out.println(Utilities.printSolution(solver));
			result = true;
		} else {
			System.out.println("No solution - test case inconsistent");
		}
		// Remove all constraints again
		for (Constraint c : addedConstraints) {
			model.removeConstraint(c);
		}

		return result;
	}

}
