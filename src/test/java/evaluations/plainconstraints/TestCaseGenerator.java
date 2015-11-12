package evaluations.plainconstraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.exquisite.diagnosis.quickxplain.SolutionReuse;
import org.exquisite.tools.Utilities;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * The super class of all test cases
 * @author dietmar
 *
 */
public class TestCaseGenerator {
	public String filename;
	public CPModel cpmodel;
	
	/**
	 * Creates a test case generator for a given problem
	 * @param filename
	 * @throws Exception
	 */
	public TestCaseGenerator(String filename) throws Exception {
		this.filename = filename;
		cpmodel = PlainConstraintsUtilities.loadModel(filename);
	}
	
	/**
	 * Creates a list of positive test cases 
	 * @param nb number of test cases to generated
	 * @param pct percent of variables to be stored (partial examples)
	 * @param nbVars number of variables to use as an input
	 * @param maxNotFeasible number of retries in case of problems
	 * @return a list of variable-value maps
	 */
	public List<Map<String,Integer>> createPositiveTestCases(int nb, double pct, int nbVars, int maxNotFeasible) {
		System.out.println("Creating Test Cases");
		List<Map<String, Integer>> result = new ArrayList<Map<String, Integer>>();
		// calculate the number of existing variables and those that should be remembered
		// Use a dummy solver to get the real number of variables
		// the number in the model and the solver might be different when there are single-value vars
		CPSolver tmpSolver = new CPSolver();
		tmpSolver.read(cpmodel);
		
		List<IntDomainVar> variables = new ArrayList<IntDomainVar>();
		Iterator<IntDomainVar> var_it = tmpSolver.getIntVarIterator();
		while (var_it.hasNext()) {
			IntDomainVar var = var_it.next();
//			System.out.println(var.getName());
			if (!var.getName().startsWith(PlainConstraintsUtilities.TMP_prefix)) {
				variables.add(var);
			}
		}
		int nbExistingVars = variables.size();
		System.out.println("Variables in the model: " + variables.size());
		
		int nbVarsToSet = ((int) (nbExistingVars  * ((double) pct / 100)));
		if (nbVarsToSet == 0) {
			nbVarsToSet = 1;
		}
		System.out.println("Will store " + nbVarsToSet + " variables in test case");
		
		
		// Create a number of random solutions
		int solCount = 0;
		int notFeasibleCount = 0;
		do {
			System.out.println("Trying a random solution (" + solCount + ")");
			CPSolver solver = new CPSolver();
			solver.read(cpmodel);
			setRandomInputs(solver, nbVars);
			long start = System.currentTimeMillis();
			solver.solve();
			
			if (solver.isFeasible()) {
				System.out.println("Solution found in " + (System.currentTimeMillis() - start) +  "  ms, nbbacktracks: " + solver.getBackTrackCount() );
//				System.out.println(Utilities.printSolution(solver));
				Map<String, Integer> solution = PlainConstraintsUtilities.storeSolution(solver, nbVarsToSet);
				if (!solutionAlreadyExists(result,solution)) {
					solCount++;
					result.add(solution);
				}
				else {
					System.err.println("Solution already existed..");
				}
			}
			else {
				System.err.println("Not feasible");
				notFeasibleCount++;
				if (notFeasibleCount > maxNotFeasible) {
					System.err.println("Too many tries.., giving up");
					System.exit(0);
				}
			}
			
		} while (solCount < nb);
		System.out.println("Found " + result.size() + " test cases");
		return result;
	}
	
	
	/**
	 * Sets some random inputs. Can be overwritten if necessary
	 * @param solver
	 */
	public void setRandomInputs(CPSolver solver, int nbVarsToFill) {
		List<IntDomainVar> variables = Utilities.getIntVars(solver);
		Collections.shuffle(variables);
		// Get some variables
//		System.out.println("Will set " + nbVarsToFill + " variables");
		for (int i=0;i<nbVarsToFill;i++) {
			IntDomainVar var = variables.get(i);
			int val = var.getDomain().getRandomValue();
//			System.out.println("Setting " + var.getName() + " " + val);
			try {
				var.setVal(val);
			} catch (ContradictionException e) {
				// TODO Auto-generated catch block
				System.err.println("Contradiction ... ");
			}
		}
		
	}
	
	
	/**
	 * Returns true if the current solution is already there
	 * @param solutions
	 * @return
	 */
	boolean solutionAlreadyExists(List<Map<String, Integer>> solutions, Map<String, Integer> newSolution) {
		for (Map<String, Integer> existingSolution: solutions) {
			if (SolutionReuse.identicalSolutions(existingSolution, newSolution)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	
}
