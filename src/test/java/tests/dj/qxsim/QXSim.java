package tests.dj.qxsim;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;

import java.util.*;

/**
 * This class acts like quickxplain but works on a predefined set of known and artificial conflicts
 * @author dietmar
 *
 */
public class QXSim extends QuickXPlain<Constraint> {

	// Information about the reasoning..
	public static Map<Constraint, String> constraintNames = new HashMap<Constraint, String>();
	public static List<List<Constraint>> conflicts = new ArrayList<List<Constraint>>();
	
	public static int ARTIFICIAL_WAIT_TIME = -1;
	
	// To create the waiting times
	RandomGaussian randomGaussian;
	
	/**
	 * Constructor
	 * @param sessionData
	 * @param dagbuilder
	 */
	public 	QXSim (ExquisiteSession sessionData, AbstractHSDagBuilder dagbuilder) {
		super(sessionData, dagbuilder);
	}

	/**
	 * Shuffles the order of the conflicts and the elements in the conflicts
	 */
	public static void shuffleConflicts() {
		Collections.shuffle(QXSim.conflicts);
		for (List<Constraint> conflict : QXSim.conflicts) {
			Collections.shuffle(conflict);
		}

	}

	/**
	 * Overwrite this method with a more "clever function"
	 */
	public ConflictCheckingResult<Constraint> checkExamples(List<Example<Constraint>> examples,
			List<Constraint> constraintsToIgnore, boolean createConflicts)
			throws DomainSizeException {
		if (diagnosisEngine != null) {
			diagnosisEngine.incrementQXPCalls();
		}
		//Ignore the examples;
//		DiagnosisModel model = dagBuilder.getSessionData().diagnosisModel;
		// Simply look into the conflicts.
		// getConflict only uses waitingTime, if we want to create conflicts
		List<Constraint> conflict = getConflict(constraintsToIgnore, createConflicts);
//		System.out.println("Constraints to ignore: " + DiagnosisSimulation.conflictToString(constraintNames, constraintsToIgnore));
//		System.out.println("Found conflict in QYSIM: " + DiagnosisSimulation.conflictToString(constraintNames, conflict));
		ConflictCheckingResult<Constraint> result = new ConflictCheckingResult<>();

		if (createConflicts && conflict.size() > 0) {
			result.conflicts.add(conflict);
		}
		if (conflict.size() > 0) {
			result.failedExamples.add(examples.get(0));
		}

		return result;
	}
	
	/**
	 * Checks if the current set of constraints is a diagnosis, i.e., if it has an overlap
	 * with all known conflicts (hitting set)
	 */
	@Override
	public boolean isConsistent(List<Constraint> constraints) {
//		System.out.println("Checking consistency of " + Utilities.printConstraintList(constraints, this.currentDiagnosisModel));
		// Go through the list of known conflicts and return false
		// if there's a conflict without an overlap
		for (List<Constraint> conflict : QXSim.conflicts) {
			Set<Constraint> toTest = new HashSet<Constraint>(constraints);
			Set<Constraint> conflictSet = new HashSet<Constraint>(conflict);
			toTest.retainAll(conflictSet);
			if (toTest.size() == 0) {
//				System.out.println("Empty overlap, not consistent");
				return false;
			}
		}
		// There was an overlap of the constraint set with all conflicts. Should be consistent..
		return true;
	}
	
	/**
	 * Returns a random conflict from the set of conflicts which contain none of the ignorable constraints
	 * @param constraintsToIgnore
	 * @param useWaitingTime
	 * @return
	 */
	List<Constraint> getConflict(List<Constraint> constraintsToIgnore, boolean useWaitingTime) {

		// Simulate some more computation time, before we return the conflict
//		if (useWaitingTime) {
			long start = System.currentTimeMillis();
			if (ARTIFICIAL_WAIT_TIME > 0) {
				if (randomGaussian == null) {
					randomGaussian = new RandomGaussian(ARTIFICIAL_WAIT_TIME,ARTIFICIAL_WAIT_TIME/3, 1, ARTIFICIAL_WAIT_TIME + (ARTIFICIAL_WAIT_TIME-1));
				}
				int wait = (int) randomGaussian.getGaussian();
				while (System.currentTimeMillis() - start < wait) {
					// do nothing..
				}
			}
//		}


		List<List<Constraint>> conflicts = new ArrayList<List<Constraint>>(QXSim.conflicts);
		Collections.shuffle(conflicts);

		// Look for the first element in the shuffled list that has no overlap
		// with the constraints to ignore
		// If none is found, return an empty list
		for (List<Constraint> conflict : conflicts) {
			List<Constraint> copiedConflict = new ArrayList<Constraint> (conflict);
			copiedConflict.removeAll(constraintsToIgnore);
			if (copiedConflict.size() == conflict.size()) {

				// Found a conflict

				return conflict;
			}
		}
		return new ArrayList<Constraint>();
	}
	

}
