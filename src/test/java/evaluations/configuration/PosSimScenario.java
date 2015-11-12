package evaluations.configuration;

import evaluations.conflictposition.QXKCTools.WaitMode;

/**
 * Running the scenarios.
 * @author dietmar
 *
 */
public class PosSimScenario extends StdScenario {
	// How to distribute the things
	public enum ConflictPosition {left, right, distributed, leftAndRight, neighboring, leftAndRightNeighboring};
	
	// specific configuration settings
	public int kbSize;
	public int nbConflicts;
	public int avgConflictSize;
	public ConflictPosition conflictPosition;
	public WaitMode waitMode;
	public int maxWaitTime;

	
	/**
	 * Constructor to create a scenario
	 * @param kbSize
	 * @param nbConflicts
	 * @param avgConflictSize
	 * @param conflictPosition
	 * @param maxWaitTime
	 * @param nbDiagnoses
	 * @param qxtype
	 */
	public PosSimScenario(int kbSize, int nbConflicts, int avgConflictSize,
			ConflictPosition conflictPosition, WaitMode waitMode,
			int searchDepth, int maxDiagnoses, int maxWaitTime) {
		super("", searchDepth, maxDiagnoses);
		this.kbSize = kbSize;
		this.nbConflicts = nbConflicts;
		this.avgConflictSize = avgConflictSize;
		this.conflictPosition = conflictPosition;
		this.waitMode = waitMode;
		this.maxWaitTime = maxWaitTime;
	}
	
	@Override
	public String getName() {
		StringBuilder result = new StringBuilder();
		result.append(kbSize);
		result.append("_");
		result.append(nbConflicts);
		result.append("_");
		result.append(avgConflictSize);
		result.append("_");
		result.append(conflictPosition.toString());
		result.append("_");
		result.append(waitMode.toString());
		result.append("_");
		result.append(searchDepth);
		result.append("_");
		result.append(maxDiags);
		result.append("_");
		result.append(maxWaitTime);
		return result.toString();
	}
	
}