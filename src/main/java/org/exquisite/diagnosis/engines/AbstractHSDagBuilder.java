package org.exquisite.diagnosis.engines;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.common.SharedCollection;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import choco.kernel.model.constraints.Constraint;

public abstract class AbstractHSDagBuilder extends Observable implements IDiagnosisEngine {
	
	// DJ add two more types for simulations
	public enum QuickXplainType {QuickXplain, ParallelQuickXplain, SimulatedQuickXplain, MergeXplain, ParallelMergeXplain, QX_KC, MX_KC, PMX_KC};
	
	public static QuickXplainType USE_QXTYPE = QuickXplainType.QuickXplain;
	public static boolean SINGLE_CONFLICT_SEARCH = false;
	
	/**
	 * search depth  limit
	 */
	protected int searchDepth = -1;
	
	/**
	 * tests.diagnosis limit
	 */
	protected int maxDiagnoses = -1;

	/**
	* The root node of the HS DAG
	*/
	protected DAGNode rootNode = null; 
	
	/**
	 * Object that contains tests.diagnosis model, appXML and graph object instances for this session.
	 */
	public ExquisiteSession sessionData = null;
	
	/**
	 * the model containing the constraints, variables and the test cases
	 */
	public DiagnosisModel model = null;

	/**
	 * The list to expand
	 */
	public List<DAGNode> nodesToExpand = new ArrayList<DAGNode>();
	
	/**
	 * The list of already known conflicts (Has to be synchronized
	 */
	public SharedCollection<List<Constraint>> knownConflicts = new SharedCollection<List<Constraint>>();
		
	/**
	 * A lookup to search for a node by conflict set.
	 * That has to be  list?
	 */
	public Map<List<Constraint>, List<DAGNode>> conflictNodeLookup = new Hashtable<List<Constraint>, List<DAGNode>>();
	
	// TODO TS: The synchronization could pe a possible slowdown for benchmarks.
	/**
	 * A list of diagnoses returned from node expansion
	 */
	public List<Diagnosis> diagnoses = Collections.synchronizedList(new ArrayList<Diagnosis>());	
	
	/**
	 * A collection containing every DAG node instantiated during graph construction.
	 */
	public SharedCollection<DAGNode> allConstructedNodes = new SharedCollection<DAGNode>();
	
	/**
	 * A collection nodes that contain no conflict set (i.e. a tests.diagnosis).
	 */
	public SharedCollection<DAGNode> diagnosisNodes = new SharedCollection<DAGNode>();
	
	// To have a reference .. Test only (DJ)
	public static DiagnosisModel globalDiagnosisModel = null;
	
	// A list of all known constraints (without example constraints)
	public List<Constraint> allConstraints = new ArrayList<Constraint>();
	
	// The consistent subsets of constraints per test case 
	public Map<Example, SharedCollection<BitSet>> consistentConstraintSets  = new HashMap<Example, SharedCollection<BitSet>>();
	
	// The consistent subsets of constraints per test case
	public Map<Example, SharedCollection<BitSet>> inconsistentConstraintSets  = new HashMap<Example, SharedCollection<BitSet>>();
		
	
	/**
	 * TEST: ALL CONFLICTS EVER CONSTRUCTED and Conflicts Reused
	 */
	public int reuseCount = 0;

	
	/**
	 * Enforcing some dependencies in order for engine to work correctly.
	 * @param sessionData
	 */
	public AbstractHSDagBuilder(ExquisiteSession sessionData){
		this.setSessionData(sessionData);
		this.model = new DiagnosisModel(sessionData.diagnosisModel);
		globalDiagnosisModel = this.model;
		this.searchDepth = this.sessionData.config.searchDepth;
		this.maxDiagnoses = this.sessionData.config.maxDiagnoses;
		
		// Remember the known constraints. This should never be changed afterwards when the
		// model is copied
		this.allConstraints.addAll(this.model.getPossiblyFaultyStatements());
		
		this.resetCachedConflicts();
	}

	
	public void resetCachedConflicts() {
		// Create the consistent and inconsistent example lists
		for (Example ex : sessionData.diagnosisModel.getPositiveExamples()) {
			this.consistentConstraintSets.put(ex, new SharedCollection<BitSet>());
			this.inconsistentConstraintSets.put(ex, new SharedCollection<BitSet>());
		}

	}
	
	/**
	 * Returns the tests.diagnosis engine to original state when first instantiated.
	 */
	public void resetEngine(){
		rootNode = null;
		diagnoses.clear(); 
		knownConflicts.clear();
		conflictNodeLookup.clear();
		allConstructedNodes.clear();
		diagnosisNodes.clear();
//		model = null;
		this.searchDepth = this.sessionData.config.searchDepth;
		this.maxDiagnoses = this.sessionData.config.maxDiagnoses;
		this.resetCachedConflicts();
	}
	
	/**
	 * The main method that calculates the diagnoses
	 * @return a set of diagnoses or an empty set, of there no faults were found.
	 * @throws DiagnosisException 
	 */
	abstract public List<Diagnosis> calculateDiagnoses() throws DiagnosisException;
	
	/**
	 * The method that continuously expands the tree nodes.
	 * @throws DomainSizeException 
	 */
	abstract public void expandNodes(List<DAGNode> nodesToExpand) throws DomainSizeException;
	
	
	/**
	 * Set the max searchdepth
	 * @param searchDepth
	 */
	public void setSearchDepth(int searchDepth) {
		this.searchDepth = searchDepth;
		// TODO: This should have been all moved to somewhere else
		this.sessionData.config.searchDepth = searchDepth;
	}
	
	/**
	 * Set the max number of diagnoses to search for. 
	 * @param maxDiagnosis
	 */
	public void setMaxDiagnosis(int maxDiagnosis) {
		this.maxDiagnoses = maxDiagnosis;
	}		
	
	
	/// *******************
	// Some stats
	int solverCalls = 0;
	long solverTime = 0;
	int propagationCount = 0;
	int cspSolutionCount = 0;
	int qxpCalls = 0;
	int searchesForConflicts = 0;
	int mxpConflicts = 0;
	int mxpSplittingTechniqueConflicts = 0;
	int constructedNodes = 0;
	
	protected long finishedTime = 0;
	
	public void incrementSolverCalls() {solverCalls++;}
	public void incrementSolverTime(long amount) {solverTime += amount;}
	public void incrementPropagationCount() {propagationCount++;}
	public void incrementCSPSolutionCount() {cspSolutionCount++;}
	public void incrementQXPCalls() {qxpCalls++;}
	public void incrementSearchesForConflicts() {searchesForConflicts++;}
	public void incrementMXPConflicts(int conflicts) {mxpConflicts+=conflicts;}
	public void incrementMXPSplittingTechniqueConflicts(int conflicts) {mxpSplittingTechniqueConflicts+=conflicts;}
	public void incrementConstructedNodes() {constructedNodes++;}
	
	/**
	 * Returns the number of calls to the solver made by qx.
	 */
	public int getSolverCalls(){
		return solverCalls;
	}

	public long getSolverTime() {
		return solverTime;
	}
	
	/**
	 * Returns number of propagations invoked in the solver.
	 */
	public int getPropagationCount(){
		return propagationCount;
	}	
	
	/**
	 * Number of CSP solutions found by the solver.
	 */
	public int getCspSolvedCount(){
		return cspSolutionCount;
	}
	
	public int getTPCalls() {
		return qxpCalls;
	}
	
	public int getSearchesForConflicts() {
		return searchesForConflicts;
	}


	public int getMXPConflicts() {
		return mxpConflicts;
	}


	public int getMXPSplittingTechniqueConflicts() {
		return mxpSplittingTechniqueConflicts;
	}
	
	public int getConstructedNodeCount() {
		return constructedNodes;
	}

//	/**
//	 * Update the maximum search depth that the graph can expand to.
//	 */
//	public void setMaxSearchDepth(int maxDepth) {
//		this.searchDepth = maxDepth;
//	}

//	/**
//	 * Update the maximum number of diagnoses that should be returned.
//	 * Set to -1 for unlimited number to collect.
//	 */
//	public void setMaxDiagnoses(int maxDiagnoses) {
//		this.maxDiagnoses = maxDiagnoses;		
//	}	

	/**
	 * Returns the root node of the HSDAG graph that is built during search.
	 */
	public DAGNode getRootNode() {
		return rootNode;
	}

	/**
	 * The session data object that is being used with this tests.diagnosis engine instance.
	 */
	public ExquisiteSession getSessionData() {
		return this.sessionData;
	}

	/**
	 * Updates the session data object used with this tests.diagnosis engine instance.
	 * Also makes update to quickxplain instance as well.
	 */
	public void setSessionData(ExquisiteSession sessionData) {
		this.sessionData = sessionData;		
		
//		this.model = this.sessionData.diagnosisModel;
//		if (model.getSplitPoints() != null) {
//			this.qxplain.setSplitPoints(model.getSplitPoints());
//		}
	}

	public long getFinishedTime() {
		return finishedTime;
	}
	
	@Override
	public DiagnosisModel getModel() {
		return model;
	}
	
	/**
	 * Adds the certainly faulty statements to all diagnoses or adds a new tests.diagnosis including the certainly faulty statements,
	 * if the list of diagnoses is empty and the list of certainly faulty statements is not empty.
	 * @param diagnoses
	 */
	protected void addCertainlyFaultyStatements(List<Diagnosis> diagnoses) {
		List<Constraint> certainlyFaultyStatements = sessionData.diagnosisModel.getCertainlyFaultyStatements();
		if (certainlyFaultyStatements.size() > 0) {
			if (diagnoses.size() == 0) {
				diagnoses.add(new Diagnosis(new ArrayList<Constraint>(), sessionData.diagnosisModel));
			}
			for (Diagnosis diag: diagnoses) {
				diag.getElements().addAll(certainlyFaultyStatements);
			}
		}
	}
}
