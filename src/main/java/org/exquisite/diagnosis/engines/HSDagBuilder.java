package org.exquisite.diagnosis.engines;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.engines.common.NodeUtilities;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.tools.Debug;

import choco.kernel.model.constraints.Constraint;

/**
 * The class that calculates the HS-Dag
 * @author Dietmar 
 */
public class HSDagBuilder extends AbstractHSDagBuilder
{		
	public HSDagBuilder(ExquisiteSession sessionData) {
		super(sessionData);
	}
	
	/**
	 * The main method that calculate the diagnoses
	 * @return a set of tests.diagnosis or an empty set, of there is no problem
	 * @throws DomainSizeException 
	 */
	public List<Diagnosis> calculateDiagnoses() throws DomainSizeException {		
		diagnoses = new ArrayList<Diagnosis>();
		if (rootNode == null) {
			Debug.msg("Empty root node - doing inital test");
			try{	
				// Initialize the quickxplain object
				QuickXPlain qxplain = NodeExpander.createQX(this.sessionData, this);
				
				ConflictCheckingResult checkingResult;
				
				// Reuse conflicts, if conflict set is not empty				
				if (knownConflicts.size() > 0) {
					checkingResult = new ConflictCheckingResult();
					checkingResult.conflicts.addAll(knownConflicts.getCollection());
					checkingResult.failedExamples.addAll(model.getPositiveExamples());
				} else {
				
					//otherwise use quickxplain to calculate conflict
					if (!QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {
						
						checkingResult = qxplain.checkExamples(model.getPositiveExamples(), new ArrayList<Constraint>(), true);
					}
					else {
						synchronized(QuickXPlain.ContinuingSync) {
							checkingResult = new ConflictCheckingResult();
	//						Debug.syncMsg("Start first checkExamplesParallel()");
							qxplain.checkExamplesParallel(model.getPositiveExamples(), new ArrayList<Constraint>(), true, checkingResult, knownConflicts);
	//						Debug.syncMsg("Main thread sleeping.");
							try {
								QuickXPlain.ContinuingSync.wait();
							} catch (InterruptedException e) {
								return diagnoses;
							}
	//						Debug.syncMsg("Main thread woke up.");
						}
					}
				}
								
				if (checkingResult != null)
				{
//					System.out.println("---> have some result: " + checkingResult);
					if (checkingResult.conflictFound()) {
						List<Constraint> conflictSet = new ArrayList<Constraint>();
						conflictSet.addAll(checkingResult.conflicts.get(0));
						incrementConstructedNodes();
						rootNode = new DAGNode(conflictSet);
						if (!QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {
							rootNode.examplesToCheck = new ArrayList<Example>(checkingResult.failedExamples);
							// DJ: Do not add the root node; it will be added on expansion.
	//						allConstructedNodes.addItem(rootNode);
	//						knownConflicts.add(rootNode.conflict);
							// DJ: we could actually add all conflicts we have to this list
							synchronized (checkingResult.conflicts.getWriteLock()) {
								for (List<Constraint> c : checkingResult.conflicts.getCollection()) {
		//							System.out.println("Adding a known conflit");
									knownConflicts.addItemListNoDups(c);
								}
							}
						}
						else {
							rootNode.examplesToCheck = model.getPositiveExamples();
						}
						
//						knownConflicts.addAll(checkingResult.conflicts);

						List<DAGNode> nodes = new ArrayList<DAGNode>();
						nodes.add(rootNode);
						conflictNodeLookup.put(rootNode.conflict, nodes);
						nodesToExpand = new ArrayList<DAGNode>();
						nodesToExpand.add(this.rootNode);
//						Debug.msg("Starting node expansion... ");
						expandNodes(nodesToExpand);
//						Debug.msg("node expansion finished.");
						
//						List<List<Constraint>> conflicts = knownConflicts.getCollection();
//						for (List<Constraint> conflict: conflicts)
//						{
//							System.out.println(Utilities.printConstraintList(conflict, model));
//						}
					}
					else {
						Debug.msg("No conflict/s found.");
					}
				}
				else {
					Debug.msg("Checking result returned null, Thread must have been interrupted.");
				}	
			}
			catch (DomainSizeException e)
			{
				throw e;
			}
		}
		
		Debug.msg("allConstructed nodes.size = " + this.allConstructedNodes.getCollection().size());
		
		addCertainlyFaultyStatements(diagnoses);
		
		finishedTime = System.nanoTime();
		
		return diagnoses;
	}
	
	/**
	 * The method that continuously expands the tree nodes in breadth first manner
	 * @throws DomainSizeException 
	 */
	public void expandNodes(List<DAGNode> nodesToExpand) throws DomainSizeException{
		// TEST TS ************
//		long start = System.currentTimeMillis();
//		long end = 0;
//		int currentLevel = 0;
		//*********************
		
		//a list to keep nodes where a tests.diagnosis was found.
//		List<DAGNode> diagnoses = new ArrayList<DAGNode>();
//		int nodeCounter = 0;		
//		System.out.println("All constructed nodes: " + this.allConstructedNodes.getCollection().size());
		
		//1.)Begin Breadth-first search of tree, constructing nodes when needed... 
		while (!nodesToExpand.isEmpty()){				
			//fetch a node from the front of the queue...
			DAGNode targetNode = nodesToExpand.remove(0);
			
			// DJ: We do not need to add this one here... It was put already there on construction
//			this.allConstructedNodes.addItem(targetNode);
			//Debug.DEBUGGING_ON = true;
			//Check first if the node is closed, otherwise proceed.
			if (!targetNode.closed && !targetNode.pruned){
				//check if search depth has been reached.			
				if(targetNode.nodeLevel < this.searchDepth || this.searchDepth == -1){					
					
					// TEST TS ************
//					if (currentLevel != targetNode.nodeLevel) {
//						end = System.currentTimeMillis();
//						System.out.println("Level " + currentLevel + " finished after " + (end - start) + "ms.");
//						currentLevel = targetNode.nodeLevel;
//						start = end;
//					}
					//*********************
					
					//prune conflict set.
					boolean isPruned = NodeUtilities.applyPruningRules(targetNode.conflict, knownConflicts.getCollection(), conflictNodeLookup);
//					if (isPruned) {
//						System.err.println("Pruning was done");
//					}
					if(!isPruned){
						//expand for every conflict item in conflict list 
						for (Constraint c : targetNode.conflict){
							NodeExpander expander = new NodeExpander(this);
//							expander.setQuickXPlain(new QuickXPlain(this.sessionData,this));
//							expander.setConflictNodeLookup(this.conflictNodeLookup);
//							expander.setConstructedNodes(this.allConstructedNodes);
//							expander.setNodesToExpand(nodesToExpand);
//							expander.setDiagnoses(diagnoses);
//							expander.setKnownConflicts(this.knownConflicts);
//							expander.setNodeCounter(nodeCounter);
							expander.setDiagnosisModel(this.model);
//							expander.setResults(this.diagnoses);
							expander.expandNode(targetNode, c);
							
							// Check, if enough diagnoses were found
							if (maxDiagnoses != -1 && diagnoses.size() >= maxDiagnoses) {
								return;
							}
						}
					}
					else {
						Debug.msg("Pruning node with label: " + targetNode.pathLabels);
					}
				}
			}
		}
		// TEST TS ************
//		end = System.currentTimeMillis();
//		System.out.println("Level " + currentLevel + " finished after " + (end - start) + "ms.");
		//*********************
	}	
}
