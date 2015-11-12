package evaluations.tools;

import java.util.HashSet;
import java.util.List;

import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.engines.common.SharedDAGNodeQueue;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.tools.Utilities;

import choco.kernel.model.constraints.Constraint;

/**
 * Evaluation of different diagnosis runs to calculate average values.
 * @author Thomas
 */
public class DiagnosisEvaluation {
	
	int totalRuns = 0;
	
	double totalTime = 0;
	int nbDiags = 0;
	int totalProps = 0;
	int totalSolves = 0;
	long totalSolverTime = 0;
	int totalNodes = 0;
	int totalQXPCalls = 0;
	int totalQXCacheReuse = 0;
	int totalConflictCount = 0;
	double totalConflictSizes = 0;
	double totalDiagSizes = 0;
	int totalDiags = 0;
	double totalTreeWidths = 0;
	int totalSearchesForConflicts = 0;
	int totalMXPConflicts = 0;
	int totalMXPSplittingTechniqueConflicts = 0;
	
	// DJ TESt
	int conflictReuse = 0;
	
	public double AvgTime = 0;
	public double AvgProps = 0;
	public double AvgSolves = 0;
	public double AvgSolverTime = 0;
	public double AvgNodes = 0;
	public double AvgQXPCalls = 0;
	public double AvgTotalQXCacheReuse = 0;
	public double AvgConflictCount = 0;
	public double AvgConflictSize = 0;
	public double AvgDiagSize = 0;
	public double AvgDiags = 0;
	public double AvgTreeWidth = 0;
	public double AvgSearchesForConflicts = 0;
	public double AvgMXPConflicts = 0;
	public double AvgMXPSplittingTechniqueConflicts = 0;
	
	// DJ TEST
	public double AvgConflictReuse = 0;
	
	/**
	 * Analyzes one run. Analysis needs to be finished with finishCalculation(), before average values can be retrieved.
	 * @param engine   the engine that made the diagnosis
	 * @param start   the start time of the diagnosis
	 * @param end   the end time of the diagnosis
	 */
	public void analyzeRun(IDiagnosisEngine engine, double ms) {
//		System.err.println("Analyzing");
		if (engine != null) {
			totalRuns++;
			
			totalTime += ms;
			totalProps += engine.getPropagationCount();
			totalSolves += engine.getCspSolvedCount();
			totalSolverTime += engine.getSolverTime();
			totalQXPCalls += engine.getTPCalls();
			totalSearchesForConflicts += engine.getSearchesForConflicts();
			totalMXPConflicts += engine.getMXPConflicts();
			totalMXPSplittingTechniqueConflicts += engine.getMXPSplittingTechniqueConflicts();
			
			if (engine instanceof AbstractHSDagBuilder) {
				AbstractHSDagBuilder dagEngine = (AbstractHSDagBuilder)engine;
				
//				printConflicts(dagEngine.knownConflicts.getCollection(), dagEngine.model);
				
			
				// DJ TEST
				conflictReuse += dagEngine.reuseCount;
				
				totalNodes += dagEngine.getConstructedNodeCount();
				
				int conflictSizes = 0;
				List<List<Constraint>> conflicts = dagEngine.knownConflicts.getCollection();
				for (List<Constraint> conflict: conflicts) {
					conflictSizes += conflict.size();
				}
				totalConflictSizes += conflictSizes / (double) conflicts.size();
				totalConflictCount += conflicts.size();
				
				int diagSizes = 0;
				for (Diagnosis d : dagEngine.diagnoses) {
					// System.out.println(d.getElements());
					if (d.getElements() != null) {
						diagSizes += d.getElements().size();
					} else {
						System.err.println("No elements? ");
					}
				}
				totalDiagSizes += (double) diagSizes; // / engine.diagnoses.size();
				totalDiags += dagEngine.diagnoses.size();
				
				List<DAGNode> nodes = dagEngine.allConstructedNodes.getCollection();
				calculateAverageTreeWidth(nodes);
				
				checkForDuplicateNodes(dagEngine.allConstructedNodes.getCollection());
			}
		}
		
	}
	
	private void printConflicts(List<List<Constraint>> conflicts, DiagnosisModel model) {
		int i = 0;
		for(List<Constraint> conflict: conflicts) {
			System.out.print(i + ": ");
			System.out.println(Utilities.printConstraintListOrderedByName(conflict, model));
			i++;
		}
	}
	
	/**
	 * Checks a list of DAGNodes for duplicates. Prints out the number of found duplicates.
	 * @param nodes   the list to check
	 */
	private void checkForDuplicateNodes(List<DAGNode> nodes) {
		int duplicateNodes = 0;
		HashSet<DAGNode> testSet = new HashSet<DAGNode>();
		for (DAGNode node: nodes) {
			if (testSet.contains(node)) {
				System.err.println("Duplicate at level: " + node.nodeLevel);
				duplicateNodes++;
			}
			else {
				testSet.add(node);
			}
		}
		if (duplicateNodes > 0) {
			System.err.println("Duplicate nodes found: " + duplicateNodes);
			System.err.println("Added nodes: " + SharedDAGNodeQueue.addedNodes);
			System.err.println();
		}
		else {
//			System.out.println("-- no dups found");
		}
	}
	


	/**
	 * Calculates the average tree width of constructed nodes.
	 * @param nodes   the nodes of the tree
	 */
	private void calculateAverageTreeWidth(List<DAGNode> nodes) {
		int maxLevel = 0;
		for (DAGNode node: nodes) {
			if (node.nodeLevel > maxLevel) {
				maxLevel = node.nodeLevel;
			}
		}
		
		double treeWidth = nodes.size() / ((double)maxLevel + 1);
		totalTreeWidths += treeWidth;
	}
	
	/**
	 * Finishes the calculation of average values and stores them in the public variables.
	 * @param testRuns   the number of conducted test runs 
	 */
	public void finishCalculation() {
		AvgTime = totalTime / (double) totalRuns;
		AvgProps = totalProps / (double) totalRuns;
		AvgSolves = totalSolves / (double) totalRuns;
		AvgSolverTime = totalSolverTime / (double) totalSolves / 1000000d;
		AvgNodes = totalNodes / (double) totalRuns;
		AvgQXPCalls = totalQXPCalls / (double) totalRuns;
		AvgTotalQXCacheReuse = totalQXCacheReuse / (double) totalRuns;
		AvgConflictCount = totalConflictCount / (double) totalRuns;
		AvgConflictSize = totalConflictSizes / (double) totalRuns;
		AvgDiagSize = totalDiagSizes / (double) totalDiags;
		AvgDiags = totalDiags / (double) totalRuns;
		AvgTreeWidth = totalTreeWidths / (double) totalRuns;
		AvgSearchesForConflicts = totalSearchesForConflicts / (double) totalRuns;
		AvgMXPConflicts = totalMXPConflicts / (double) totalSearchesForConflicts;
		AvgMXPSplittingTechniqueConflicts = totalMXPSplittingTechniqueConflicts / (double) totalSearchesForConflicts;
		// DJ TESTs
		AvgConflictReuse = conflictReuse / (double) totalRuns;
	}

	public String getResults() {
		StringBuilder result = new StringBuilder();
		result.append("Evaluated runs: " + totalRuns + "\r\n");
		result.append("Average Diag Time (ms): " + AvgTime + "\r\n");
		result.append("Average Propagations: " + AvgProps + "\r\n");
		result.append("Average CSP Solves: " + AvgSolves + "\r\n");
		result.append("Average Constructed Nodes: " + AvgNodes	+ "\r\n");
		result.append("Average QXP Calls: " + AvgQXPCalls + "\r\n");
		result.append("Number of Diags: " + AvgDiags + "\r\n");
		result.append("Average Diag Size: " + AvgDiagSize + "\r\n");
		result.append("Average Solver Time (ms): " + AvgSolverTime + "\r\n");
		result.append("Average Conflict Count: " + AvgConflictCount+ "\r\n");
		result.append("Average Conflict Size: " + AvgConflictSize + "\r\n");
		result.append("Cache reuse QX: " + AvgTotalQXCacheReuse + "\r\n");
		result.append("Average Tree Width: " + AvgTreeWidth + "\r\n");
		
		// DJ TEST
		result.append("Average conflict reuse: " + AvgConflictReuse + "\r\n");
		
		result.append("Average searches for conflicts: " + AvgSearchesForConflicts + "\r\n");
		result.append("Average conflicts per search: " + AvgMXPConflicts + "\r\n");
		result.append("Average conflicts found with splitting technique: " + AvgMXPSplittingTechniqueConflicts + "\r\n");
		
		return result.toString();
	}
}
