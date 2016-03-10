package evaluations.configuration;

import evaluations.DiagnosisSimulator;

/**
 * Ah, no comments again
 * @author anonymous? dietmar
 *
 */
public class SimulatorScenario extends StdScenario{
	
	public int nbConstraints;
	public int nbConflicts;
	public int conflictSize;
	public DiagnosisSimulator.ConflictGenerationStrategy_VarDistribution varDistribution;
	
	public SimulatorScenario(int nbConstraints, int nbConflicts, int conflictSize, DiagnosisSimulator.ConflictGenerationStrategy_VarDistribution varDistribution) {
		super("");
		this.nbConstraints = nbConstraints;
		this.nbConflicts = nbConflicts;
		this.conflictSize = conflictSize;
		this.varDistribution = varDistribution;
	}
	
	public SimulatorScenario(int nbConstraints, int nbConflicts, int conflictSize, DiagnosisSimulator.ConflictGenerationStrategy_VarDistribution varDistribution, int searchDepth, int maxDiags, int waitTime) {
		super("", searchDepth, maxDiags, waitTime);
		this.nbConstraints = nbConstraints;
		this.nbConflicts = nbConflicts;
		this.conflictSize = conflictSize;
		this.varDistribution = varDistribution;
	}
	
	@Override
	public String getName() {
		StringBuilder result = new StringBuilder();
		result.append(nbConstraints);
		result.append("_");
		result.append(nbConflicts);
		result.append("_");
		result.append(conflictSize);
		result.append("_");
		result.append(searchDepth);
		result.append("_");
		result.append(maxDiags);
		result.append("_");
		result.append(waitTime);
		result.append("_");
		result.append(varDistribution.toString());
		return result.toString();
	}
}