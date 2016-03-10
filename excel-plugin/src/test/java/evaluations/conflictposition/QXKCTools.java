package evaluations.conflictposition;

import choco.kernel.model.constraints.Constraint;

import java.util.List;

/*+
 * Holds shared code for the simulated QXs
 */
public class QXKCTools {

    public static WaitMode WAIT_MODE = WaitMode.quadratic;
    public static double MAX_WAIT_TIME = 100d;
    // globally known conflicts, will be set on construction
    public static List<List<Constraint>> knownConflicts = null;

    // Look in the global list
    public static boolean isConsistent(List<Constraint> constraints, int numberOfConstraints) {
//		System.out.println("QX Called with: " + Utilities.printConstraintList(constraints, dagbuilder.model));

        //double maxCount = diagnosisEngine.getDiagnosisModel().getPossiblyFaultyStatements().size() + diagnosisEngine.getDiagnosisModel				().getCorrectStatements().size();
//		System.out.println("MaxCount is " + maxCount + " and constraint count is " + constraints.size());

        switch (WAIT_MODE) {
            case constant:
                if (MAX_WAIT_TIME > 0) {
                    waitActive(MAX_WAIT_TIME);
                }
                break;
            case linear:
                if (MAX_WAIT_TIME > 0 && constraints.size() > 0) {
                    double wait = (constraints.size() / numberOfConstraints) * MAX_WAIT_TIME;
                    waitActive(wait);
                }
                break;
            case quadratic:
                if (MAX_WAIT_TIME > 0 && constraints.size() > 0) {
                    double wait = (Math.pow(constraints.size(), 2) / Math.pow(numberOfConstraints, 2)) * MAX_WAIT_TIME;
                    waitActive(wait);
                }
                break;
        }


        boolean result = true;
        // A set of constraints is consistent if it is not a superset of any of the known conflicts
        // Iterate over all the known conflicts
        for (List<Constraint> conflict : QXKCTools.knownConflicts) {
            if (constraints.containsAll(conflict)) {
                return false;
            }
        }
        return result;
    }

    public static void waitActive(double ms) {
//		System.out.println("Waiting for " + ms + "ms.");
        long start = System.nanoTime();
        while ((System.nanoTime() - start) / 1000000d < ms) {
            // do nothing..
        }
    }

    public enum WaitMode {constant, linear, quadratic}

}
