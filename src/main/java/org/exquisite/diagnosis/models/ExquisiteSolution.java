package org.exquisite.diagnosis.models;

import choco.cp.model.CPModel;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a solution for an example
 * Empty for the moment
 *
 * @author dietmar
 */
public class ExquisiteSolution {

    static String TMPVAR_PREFIX = "TMP";

    public Map<String, Integer> intValues = new HashMap<String, Integer>();


    /**
     * Creates a cached solution object for an example. We only store the missing
     * fields
     *
     * @param example the current example
     * @param cpmodel the constraint model
     * @param solver  the solver holding the current solution
     * @return a solution object
     */
    public static ExquisiteSolution recordSolution(Example example, CPModel cpmodel, Solver solver) {
        ExquisiteSolution solution = new ExquisiteSolution();

        // Iterate over integer variables
        Iterator<IntDomainVar> intvarIt = solver.getIntVarIterator();
        IntDomainVar var = null;
        while (intvarIt.hasNext()) {
            var = intvarIt.next();
            if (!var.getName().startsWith(TMPVAR_PREFIX)) {
//				System.out.println("Found variable: " + var);
                if (var.isInstantiated()) {
                    Integer val = var.getVal();
                    solution.intValues.put(var.getName(), val);
                }
            }
        }
        System.out.println("Stored solution: " + solution.intValues);


        // Iterate over real variables
        // to be done
        return solution;
    }

    /**
     * Check if a solution from the past worked.
     *
     * @param knownSolutions
     * @return
     */
    public static boolean pastSolutionWorked(List<ExquisiteSolution> knownSolutions) {
        System.out.println("Checking my solutions now..");

//		// Go through all the known solutions
        for (ExquisiteSolution solution : knownSolutions) {
//			// Let's see what we have in the model
//			Iterator<Constraint> ct_it = copiedModel.getConstraintIterator();
//			Constraint ct = null;
//			while (ct_it.hasNext()) {
//				ct = ct_it.next();
//				System.out.println(HSDagBuilder.globalDiagnosisModel.getConstraintName(ct) +  ", " + ct);
//			}
//
//			CPSolver solver = new CPSolver();
//			solver.read(copiedModel);
//			if (solver.solve()) {
//				System.out.println("Could solve the solution");
//			}
//			else {
//				System.out.println("Could not solve the solution");
//			}
        }
        return false;
    }


}

