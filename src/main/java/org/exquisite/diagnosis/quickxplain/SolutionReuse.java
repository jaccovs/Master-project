package org.exquisite.diagnosis.quickxplain;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.tools.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import evaluations.plainconstraints.PlainConstraintsUtilities;


/**
 * A class to store already known solutions (currently limited to integer variables only)
 *
 * @author dietmar
 */
public class SolutionReuse {


    /**
     * Checks if we can reuse a solution
     *
     * @param cpmodel
     * @param knownSolutions
     * @return true, if a solution was possible
     */
    public static boolean solutionReusePossible(CPModel cpmodel, List<Map<String, Integer>> knownSolutions) {
        boolean result = false;
        for (Map<String, Integer> knownSol : knownSolutions) {
            if (solutionConsistent(cpmodel, knownSol)) {
//				System.out.println("-reuse");
                QuickXPlain.reuseCount++;
                return true;
            }
        }
        return result;
    }


    /**
     * Non optimized version of trying to re-use an existing solution
     *
     * @param model
     * @param solution
     * @return true if the solution is consistent
     */
    public static boolean solutionConsistent(CPModel model, Map<String, Integer> solution) {
        boolean result = false;
        List<Constraint> addedConstraints = new ArrayList<Constraint>();
        IntegerVariable var;
        try {
            for (String key : solution.keySet()) {
                Integer value = solution.get(key);
                var = Utilities.getIntVariableByName(model, key);
                if (var != null) {
                    Constraint ct = Choco.eq(var, value);
                    model.addConstraint(ct);
                    addedConstraints.add(ct);
                }
            }
            CPSolver solver = new CPSolver();
            solver.read(model);
//			long start = System.currentTimeMillis();
            if (solver.solve()) {
//				System.err.println("--> Found a reusable solution");
                result = true;
            } else {
//				System.out.println("Reuse - Solution not consistent");
            }
//			System.out.println("Reuse-check needed: " + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            // Remove the constraints again..
            for (Constraint c : addedConstraints) {
                model.remove(c);
            }
            return false;
        }
        // Remove the constraints again..
        for (Constraint c : addedConstraints) {
            model.remove(c);
        }
        return result;
    }


    /**
     * Checks if two solutions are identical
     *
     * @return
     */
    public static boolean identicalSolutions(Map<String, Integer> m1, Map<String, Integer> m2) {
        boolean result = false;
        // Same size?
        if (m1.keySet().size() == m2.keySet().size()) {
            // same keys?
            if (m1.keySet().containsAll(m2.keySet())) {
                // same values?
                boolean foundDifference = false;
                for (String key : m1.keySet()) {
                    Integer val1 = m1.get(key);
                    Integer val2 = m2.get(key);
                    if (val1.intValue() != val2.intValue()) {
                        foundDifference = true;
                        break;
                    }
                }
                return !foundDifference;
            }
        }
        return result;
    }


    /**
     * TODO unused method commented out
     * Stores the solver's solution in a given list (without dupliates)
     * @param solver
     * @param knownSolutions
     */
    /*
    public static void storeSolutionInList(Solver solver, Vector<Map<String,Integer>> knownSolutions) {
		Map<String, Integer> newSolution = PlainConstraintsUtilities.storeSolution(solver, -1);
		boolean alreadyThere = false;
		for (Map<String, Integer> knownSol : knownSolutions) {
			if (SolutionReuse.identicalSolutions(knownSol, newSolution)) {
//				System.out.println("Store solution: Solution was already there");
				return;
			}
		}
		if (!alreadyThere) {
			//FIXME - the static property "knownSolutions" is missing from the copy AbstractHSDagBuilder currently in svn.
			knownSolutions.add(newSolution);
//			System.out.println("Store solution: Stored solution with number: " + knownSolutions.size());
//			System.out.println(newSolution);
		}
	}
	*/
}
