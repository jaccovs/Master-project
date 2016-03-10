package org.exquisite.diagnosis.engines.prdfs;

import org.exquisite.diagnosis.engines.ParallelRandomDFSEngine;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.engines.common.NodeUtilities;
import org.exquisite.diagnosis.engines.heuristic.ExtendedDAGNode;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;

import java.util.ArrayList;
import java.util.List;

import static org.exquisite.core.measurements.MeasurementManager.COUNTER_CONSTRUCTED_NODES;
import static org.exquisite.core.measurements.MeasurementManager.incrementCounter;

public class PRDFSNodeExpander<T> extends Thread {

    // A pointer to the calling class
    public ParallelRandomDFSEngine<T> engine;
    public boolean finished = false;

    // A pointer to the root
//	ExtendedDAGNode startNode;

    // The first constraint to expand the root node with
//	T startConstraint;
    // The list in which we collect the results
    List<List<T>> result = new ArrayList<>();
    // The node selector
    NodeSelector<T> nodeSelector;

    /**
     * Create a thread with a
     *
     * @param latch
     */
    public PRDFSNodeExpander(ParallelRandomDFSEngine<T> engine, NodeSelector<T> nodeSelector) {
        super();
        this.setDaemon(true);
        this.engine = engine;
//		this.startNode = root;
//		this.startConstraint = startConstraint;
        this.nodeSelector = nodeSelector;
    }

    /**
     * Recursively expands the node Expand a node recursively in depth-first
     * order. Depth first for the moment
     *
     * @param node
     * @param c    T, that the node should be extended by. If it is null, a constraint is chosen using the nodeSelector.
     */
    void expand() throws Exception {
//		System.out.println("more to do: " + engine.nodeSelector.hasMoreConstraints(node));
        // for all nodeLabel elements
        while (!Thread.currentThread()
                .isInterrupted() && !(engine.sessionData.getConfiguration().maxDiagnoses > 0 && engine.knownDiags.getCollection()
                .size() >= engine.sessionData.getConfiguration().maxDiagnoses)) {
            NodeWithConstraint<T> nodeWithConstraint;
            synchronized (nodeSelector) {
                nodeWithConstraint = nodeSelector.getNextNode();
                if (nodeWithConstraint == null) {
//					System.out.println("Nothing to expand.");
//					System.out.println("Interrupted: " + Thread.currentThread().isInterrupted());
                    try {
                        finished = true;
                        synchronized (engine) {
                            engine.notify();
                        }
                        nodeSelector.wait();
                        finished = false;
                    } catch (InterruptedException e) {
//						System.out.println("Interrupted exception");
                        return;
                    }
                }
            }

            if (nodeWithConstraint != null) {
//				System.out.println("Something to expand");
                ExtendedDAGNode<T> node = nodeWithConstraint.node;
                T c = nodeWithConstraint.constraint;

                // Do not search too deep
                if (engine.sessionData.getConfiguration().searchDepth > 0 && node.treeLevel >= engine.sessionData.getConfiguration().searchDepth) {
                    //				System.out.println("Max. search depth of " + engine.sessionData.getConfiguration().searchDepth + " reached. Not expanding this node");
                    return;
                }
                // Create a new node with a new nodeLabel or a tests.diagnosis
                // Get the path labels first
                List<T> labels = new ArrayList<>(node.pathLabels);
                labels.add(c);

                // get a new nodeLabel
                List<T> newConflict = NodeUtilities.getConflictToReuse(
                        labels, new ArrayList<>(engine.knownConflicts.getCollection()));
                if (newConflict != null) {
                    // System.out.println("Reusing nodeLabel");
                    // Create a new node
                    incrementCounter(COUNTER_CONSTRUCTED_NODES);

                    ExtendedDAGNode<T> newNode = new ExtendedDAGNode<>(newConflict);
                    newNode.nodeLevel = node.nodeLevel + 1;
                    // Actually not sure if we really should check everything ..
                    newNode.examplesToCheck = new ArrayList<>(node.examplesToCheck);
                    newNode.pathLabels = new ArrayList<T>(labels);
                    engine.allConstructedNodes.add(newNode);
                    nodeSelector.addNodeWithConstraints(newNode);
                    //				expand(newNode, null);
                } else {
                    // System.out.println("Have to check a new path: " + labels);
                    ConstraintsQuickXPlain<T> qxplain = NodeExpander.createQX(
                            this.engine.sessionData);
                    ConflictCheckingResult<T> checkingResult = qxplain.checkExamples(
                            node.examplesToCheck,
                            new ArrayList<>(labels), true);

                    // If qxplain was interrupted, checkingResult will be null, so we have to return here.
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }

                    if (checkingResult.conflictFound()) {
                        // System.out.println("Found a new nodeLabel " +
                        // checkingResult.conflicts.get(0));
                        // Add all conflicts to the known ones
                        synchronized (checkingResult.conflicts.getWriteLock()) {
                            for (List<T> conflict : checkingResult.conflicts.getCollection()) {
                                engine.knownConflicts.addItemListNoDups(conflict);
                            }
                        }
                        // Create a new node
                        incrementCounter(COUNTER_CONSTRUCTED_NODES);

                        ExtendedDAGNode<T> newNode = new ExtendedDAGNode<T>(
                                checkingResult.conflicts.get(0));
                        newNode.nodeLevel = node.nodeLevel + 1;
                        newNode.examplesToCheck = checkingResult.failedExamples;
                        newNode.pathLabels = new ArrayList<T>(labels);
                        engine.allConstructedNodes.add(newNode);
                        nodeSelector.addNodeWithConstraints(newNode);
                        //					expand(newNode, null);

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
//							System.out.println("Found diag");
                            engine.knownDiags.addItemListNoDups(labels);
                            // Pull the trigger
                            synchronized (engine) {
//								System.out.println("Notifying engine");
                                engine.notify();
                            }
                        } else {
                            // First: Check if this is not a superset of a known one anyway ..
                            // Check if this node will be a superset of an already known tests.diagnosis
                            List<List<T>> knownDiags = new ArrayList<>(engine.knownDiags.getCollection());
                            //						System.out.println("Known tests.diagnosis, (size " + knownDiags.size() + ")");
                            //						for (List<T> tests.diagnosis : knownDiags) {
                            //							System.out.println(Utilities.printConstraintList(tests.diagnosis, this.engine.model));
                            //						}


                            boolean foundSuperset = false;
                            for (List<T> diag : knownDiags) {
                                if (NodeUtilities.isPathLabelSupersetOfOrEqualDiagnosis(labels, diag)) {
                                    //								System.out.println("Node is superset of known tests.diagnosis; no insert");
                                    //								System.out.println(Utilities.printConstraintList(labels, this.engine.model));
                                    foundSuperset = true;
                                    break;
                                }
                            }
                            if (!foundSuperset) {
                                //							System.out.println("Doing minimization in thread.");
                                List<T> minimized = engine
                                        .minimizeDiagnosis(new ArrayList<T>(labels));
                                if (Thread.currentThread().isInterrupted()) {
                                    return;
                                }
//								System.out.println("Found diag");
                                engine.knownDiags.addItemListNoDups(minimized);
                                //							System.out.println("Adding minimized list: " + Utilities.printConstraintList(minimized, this.engine.model));
                                // Pull the trigger
                                //							System.out.println("Thread: " + this.getId() + " found a minimal tests.diagnosis");
                                synchronized (engine) {
//									System.out.println("Notifying engine");
                                    engine.notify();
                                }
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
            expand();
        } catch (Exception e) {
            e.printStackTrace();
//			System.out.println("Error expanding: " + e.getMessage());
            System.exit(1); // No point to continue here
        } finally {
            synchronized (engine) {
//				System.out.println("Notifying engine");
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
//		for (List<T> onediag: result) {
//			// No dups allowed.
//			List<T> added = this.knownDiags.addItemListNoDups(onediag);
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