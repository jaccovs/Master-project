package org.exquisite.diagnosis.engines.heuristic;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.diagnosis.engines.HeuristicDiagnosisEngine;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.engines.common.NodeUtilities;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;

import choco.kernel.model.constraints.Constraint;

/**
 * A class to run the expansion from a given node
 * 
 * @author dietmar
 * 
 */
public class HeuristicNodeExpander extends Thread {

	// The list in which we collect the results
	List<List<Constraint>> result = new ArrayList<List<Constraint>>();

	// A pointer to the calling class
	public HeuristicDiagnosisEngine engine;

	// A pointer to the root
	ExtendedDAGNode startNode;
	
	// The first constraint to expand the root node with
	Constraint startConstraint;
	
	// The node selector
	ConstraintSelector nodeSelector;
	
	public boolean finished = false;

	/**
	 * Create a thread with a
	 * 
	 * @param latch
	 */
	public HeuristicNodeExpander(ExtendedDAGNode root, Constraint startConstraint, HeuristicDiagnosisEngine engine, ConstraintSelector nodeSelector) {
		super();
		this.setDaemon(true);
		this.engine = engine;
		this.startNode = root;
		this.startConstraint = startConstraint;
		this.nodeSelector = nodeSelector;
	}

	/**
	 * Recursively expands the node Expand a node recursively in depth-first
	 * order. Depth first for the moment
	 * 
	 * @param node
	 * @param c Constraint, that the node should be extended by. If it is null, a constraint is chosen using the nodeSelector.
	 */
	void expand(ExtendedDAGNode node, Constraint c) throws Exception {
//		System.out.println("more to do: " + engine.nodeSelector.hasMoreConstraints(node));
		boolean firstRun = true;
		// for all conflict elements
		while (!Thread.currentThread().isInterrupted() && nodeSelector.hasMoreConstraints(node)) {
			if (c == null || !firstRun) {
				c = nodeSelector.getNextConstraint(node, engine);
			}
			firstRun = false;
			// Stop if we are done.
			if (engine.sessionData.config.maxDiagnoses > 0 && engine.knownDiags.getCollection().size() >= engine.sessionData.config.maxDiagnoses) {
				// System.out.println("There are enough tests.diagnosis already ..");
				return; // DJ was break. Actually, we can really give up here
			}
			// Do not search too deep
			if (engine.sessionData.config.searchDepth > 0 && node.treeLevel >= engine.sessionData.config.searchDepth) {
//				System.out.println("Max. search depth of " + engine.sessionData.config.searchDepth + " reached. Not expanding this node");
				break;
			}
			// Create a new node with a new conflict or a tests.diagnosis
			// Get the path labels first
			List<Constraint> labels = new ArrayList<Constraint>(node.pathLabels);
			labels.add(c);
			
			// get a new conflict
			List<Constraint> newConflict = NodeUtilities.getConflictToReuse(
					labels, new ArrayList<List<Constraint>>(engine.knownConflicts.getCollection()));
			if (newConflict != null) {
				// System.out.println("Reusing conflict");
				// Create a new node
				if (engine != null) {
					engine.incrementConstructedNodes();
				}
				ExtendedDAGNode newNode = new ExtendedDAGNode(newConflict);
				newNode.nodeLevel = node.nodeLevel+1;
				// Actually not sure if we really should check everything ..
				newNode.examplesToCheck = new ArrayList<Example>(node.examplesToCheck);
				newNode.pathLabels = new ArrayList<Constraint>(labels);
				engine.allConstructedNodes.add(newNode);
				expand(newNode, null);
			} else {
				// System.out.println("Have to check a new path: " + labels);
				QuickXPlain qxplain = NodeExpander.createQX(
						this.engine.sessionData, engine);
				ConflictCheckingResult checkingResult = qxplain.checkExamples(
																		node.examplesToCheck,
																		new ArrayList<Constraint>(labels), true);
				
				// If qxplain was interrupted, checkingResult will be null, so we have to return here.
				if (Thread.currentThread().isInterrupted()) {
					return;
				}

				if (checkingResult.conflictFound()) {
					// System.out.println("Found a new conflict " +
					// checkingResult.conflicts.get(0));
					// Add all conflicts to the known ones
					synchronized (checkingResult.conflicts.getWriteLock()) {
						for (List<Constraint> conflict : checkingResult.conflicts.getCollection()) {
							engine.knownConflicts.addItemListNoDups(conflict);
						}
					}
					// Create a new node
					if (engine != null) {
						engine.incrementConstructedNodes();
					}
					ExtendedDAGNode newNode = new ExtendedDAGNode(
							checkingResult.conflicts.get(0));
					newNode.nodeLevel = node.nodeLevel+1;
					newNode.examplesToCheck = checkingResult.failedExamples;
					newNode.pathLabels = new ArrayList<Constraint>(labels);
					engine.allConstructedNodes.add(newNode);
					expand(newNode, null);

				} else {
//					System.out.println("Found a non-minized tests.diagnosis of size: " + labels.size());
//					String constraints = Utilities.printConstraintList(labels, this.engine.model);
//					if (constraints.equals("[ WS_1_S15 WS_1_S9 WS_1_S16 WS_1_S14 WS_1_S13 WS_1_U13 ]"))
//					{
//						System.out.println("Found.");
//					}
//					System.out.println(constraints);
//					System.out.println();
					// Store the tests.diagnosis
					if (!engine.doMinimizationInThreads) {
						engine.knownDiags.addItemListNoDups(labels);
						// Pull the trigger
						synchronized (engine) {
							engine.notify();
						}
					} else {
						// First: Check if this is not a superset of a known one anyway ..
						// Check if this node will be a superset of an already known tests.diagnosis
						List<List<Constraint>> knownDiags = new ArrayList<>(engine.knownDiags.getCollection());
//						System.out.println("Known tests.diagnosis, (size " + knownDiags.size() + ")");
//						for (List<Constraint> tests.diagnosis : knownDiags) {
//							System.out.println(Utilities.printConstraintList(tests.diagnosis, this.engine.model));
//						}
						
						
						boolean foundSuperset = false;
						for (List<Constraint> diag : knownDiags) {
							if (NodeUtilities.isPathLabelSupersetOfOrEqualDiagnosis(labels, diag)) {
//								System.out.println("Node is superset of known tests.diagnosis; no insert");
//								System.out.println(Utilities.printConstraintList(labels, this.engine.model));
								foundSuperset = true;
								break;
							}
						}
						if (!foundSuperset) 
						{
//							System.out.println("Doing minimization in thread.");
							List<Constraint> minimized = engine.minimizeDiagnosis(new ArrayList<Constraint>(labels));
							if (Thread.currentThread().isInterrupted()) {
								return;
							}
							engine.knownDiags.addItemListNoDups(minimized);
//							System.out.println("Adding minimized list: " + Utilities.printConstraintList(minimized, this.engine.model));
							// Pull the trigger
//							System.out.println("Thread: " + this.getId() + " found a minimal tests.diagnosis");
							synchronized (engine) {
								engine.notify();
							}
						}
					}
				}
			}
		}
		// TODO: This does not work when we do not know how many we found ...
		
		// If there is still some thing to countdown do it.
//		if (node.treeLevel == 0) {
//			System.out.println("Doing final countdown");
//			while (engine.latch.getCount() > 0) {
//				engine.latch.countDown();
//			}
//		}
			

	}
	
	/**
	 * Does the work starting from a given node
	 */
	@Override
	public void run() {
		
//		System.out.println("Starting thread");
		try {
			expand(startNode, startConstraint);
		}
		catch (Exception e) {
			e.printStackTrace();
//			System.out.println("Error expanding: " + e.getMessage());
			System.exit(1); // No point to continue here
		}
		finally {
			synchronized (engine) {
				finished = true;
				engine.notify();
			}
		}
		
		// Expand the tree in breadth first mode
		
		// Some test code
		// Do the work
//		int cnt = 0;
//		while (latch.getCount() > 0) {
//			try {
//				System.out.println(this.getId() +  ": Going to sleep " + findit);
//				cnt++;
//				if (findit == true) {
//					if (cnt > 1) {
//						System.out.println(this.getId() + " Will find something and count down.");
//						latch.countDown();
//						break;
//					}
//				}
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				System.out.println(this.getId() + " I have been interraupted");
//				e.printStackTrace();
//				return;
//			}
//		}
		
		// Try to add the constraint..
//		for (List<Constraint> onediag: result) {
//			// No dups allowed.
//			List<Constraint> added = this.knownDiags.addItemListNoDups(onediag);
//			if (added != null) {
//				latch.countDown(); // did something successful. only needed when more than one tests.diagnosis is sought for
//			}
//		}
//		
		// TEST: Just to make sure we leave the thread while under development
		// Remove at the end. Perhaps we need a timeout function..
//		engine.latch.countDown(); // did something successful. only needed when more than one tests.diagnosis is sought for
//		System.out.println(" Leaving the thread");
	}

}
