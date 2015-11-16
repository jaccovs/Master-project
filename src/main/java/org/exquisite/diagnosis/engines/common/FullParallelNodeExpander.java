package org.exquisite.diagnosis.engines.common;

import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.engines.FullParallelHSDagBuilder;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends the usual node expander with a slightly different logic
 *
 * @author dietmar
 */
public class FullParallelNodeExpander<T> extends NodeExpander<T> {

    /**
     * Call the superclass constructor
     *
     * @param dagBuilder
     */
    public FullParallelNodeExpander(AbstractHSDagBuilder<T> dagBuilder,
                                    SharedCollection<DAGNode<T>> newNodesToExpand) {
        super(dagBuilder, newNodesToExpand);
    }

    /**
     * Expands a node. If the list of newNodesToExpand is not null, we will
     * directly store the new nodes in a global list. Otherwise, we will add it
     * to the list of expandable nodes of the current level
     *
     * @param knownConflicts
     * @param conflictNodeLookup
     * @param diagnosesNodes
     * @throws DomainSizeException
     */
    @Override
    public void expandNode(DAGNode<T> targetNode, T edgeLabel)
            throws DomainSizeException {

        synchronized (FullParallelHSDagBuilder.runningThreadsSync) {
            FullParallelHSDagBuilder.runningThreads++;
        }
        try {

            if (Thread.currentThread().isInterrupted()) {
                return;
            }

//		System.out.println("T: " + (System.nanoTime() - ((FullParallelHSDagBuilder)dagBuilder).start) + " FullParallel threads: " + FullParallelHSDagBuilder.runningThreads + " MergeXplain threads: " + QuickXPlain.runningThreads);

            // generate path labels for new child node.
            List<T> newPathLabels = new ArrayList<T>();
            newPathLabels.addAll(targetNode.pathLabels);
            newPathLabels.add(edgeLabel);
            Debug.msg("Expanding node with new path label: "
                    + Utilities.printConstraintList(newPathLabels, model));

            // checks if path is a superset of an existing tests.diagnosis.
            // At the beginning of the expansion, obtain a copy of the tests.diagnosis
            // nodes known so far
            // Also, use a copy of the list of all constructed nodes.
            // In full parallel mode, check everything at the end one more time and
            // eventually update the
            // corresponding lists in synchronized mode and in a sort of transaction
            boolean isSuperset = false;
            // Get a snapshot
            List<DAGNode<T>> diagnosesCopy = null;
            diagnosesCopy = new ArrayList<DAGNode<T>>(
                    this.dagBuilder.diagnosisNodes.getCollection());

            for (DAGNode<T> diagnosisNode : diagnosesCopy) {
                isSuperset = NodeUtilities.isPathLabelSupersetOfDiagnosis(
                        newPathLabels, diagnosisNode.pathLabels);
                if (isSuperset) {
                    // System.err.println("Found superset - skipping node");
                    Debug.msg("Found superset - skipping node");
                    break;
                }
            }
            // We will do nothing if there is a superset.
            List<T> conflictToReuse = null;
            // if not a superset then carry on.
            if (!isSuperset) {
                // Greiner extension - look for existing nodes to reuse.
                DAGNode<T> newNode = NodeUtilities.checkForExistingNode(newPathLabels,
                        this.dagBuilder.allConstructedNodes.getCollection());

                // if none found then make one
                if (newNode == null) {
                    Debug.msg("Creating  new node with edge label: "
                            + model.getConstraintName(edgeLabel));
                    // System.err.println("Creating  new node with edge label: " +
                    // model.getConstraintName(edgeLabel));
                    // Construct a new node and do any initial setup...

                    if (dagBuilder != null) {
                        dagBuilder.incrementConstructedNodes();
                    }
                    newNode = new DAGNode<T>(targetNode, edgeLabel);
                    // DJ: Full parallel -> Do not insert the new node prematurely.
                    // Perhaps we do not need to do this at
                    // the end
                    // this.dagBuilder.allConstructedNodes.addItem(newNode);

                    newNode.nodeLevel = targetNode.nodeLevel + 1;
                    newNode.pathLabels = newPathLabels;

                    // Debug.msg("    newNode with pathLabel of: " +
                    // Utilities.printConstraintList(newNode.pathLabels, model));
                    Debug.msg("    newNode.nodeLevel: " + newNode.nodeLevel);

				/*
                 * 2.) Node label reuse:
				 */
                    // set a flag indicating if a set of labels were found that
                    // could be reused for this new node.
                    // boolean isReusingLabels =
                    // NodeUtilities.checkForConflictLabelReuse(newNode, allNodes);
                    conflictToReuse = NodeUtilities
                            .getConflictToReuse(
                                    newNode.pathLabels,
                                    new ArrayList<List<T>>(
                                            this.dagBuilder.knownConflicts
                                                    .getCollection()));

//				System.out.println("Reusing: " + conflictToReuse);

                    if (conflictToReuse != null) {
                        Debug.msg("	Conflict to reuse: "
                                + Utilities.printConstraintList(conflictToReuse,
                                this.model));

                        this.dagBuilder.reuseCount++;
                        // DJ: If we are not re-using stuff, we still need to create
                        // the new node...
                        newNode.conflict = new ArrayList<T>();
                        newNode.conflict.addAll(conflictToReuse);
                        newNode.examplesToCheck = new ArrayList<>(targetNode.examplesToCheck);
                    }
                    // check if this is the last node of this branch...
                    boolean isLastLevel = USE_LAST_LEVEL_CHECK && NodeUtilities.checkIsLastLevel(newNode,
                            this.model.getPossiblyFaultyStatements(),
                            dagBuilder.getSessionData().config.searchDepth);

                    // System.out.println("	lastLevel? : " + isLastLevel);

                    // if no labels were found to reuse, then make a call to
                    // quickxplain...
                    if (conflictToReuse == null) {
                        try {
                            ConflictCheckingResult<T> checkingResult;
                            if (!QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {

                                checkingResult = this.qx.checkExamples(targetNode.examplesToCheck,
                                        new ArrayList<T>(newNode.pathLabels), !isLastLevel);
                            } else {
                                synchronized (QuickXPlain.ContinuingSync) {
                                    checkingResult = new ConflictCheckingResult();
//								Debug.syncMsg("Start checkExamplesParallel()");
                                    this.qx.checkExamplesParallel(targetNode.examplesToCheck,
                                            new ArrayList<T>(newNode.pathLabels), !isLastLevel, checkingResult,
                                            dagBuilder.knownConflicts);
                                    int lastConflictsCount = dagBuilder.knownConflicts.getCollection().size();
                                    while (!checkingResult.conflictFound() && !qx.finished) {
                                        //								Debug.syncMsg("Main thread sleeping.");
                                        try {
                                            synchronized (FullParallelHSDagBuilder.runningThreadsSync) {
                                                FullParallelHSDagBuilder.runningThreads--;
                                            }
                                            QuickXPlain.ContinuingSync.wait();
                                        } catch (InterruptedException e) {
                                            return;
                                        } finally {
                                            synchronized (FullParallelHSDagBuilder.runningThreadsSync) {
                                                FullParallelHSDagBuilder.runningThreads++;
                                            }
                                        }
                                        if (!checkingResult.conflictFound()) {
                                            int newConflictsCount = dagBuilder.knownConflicts.getCollection().size();
                                            if (newConflictsCount > lastConflictsCount) {
                                                List<List<T>> conflicts = dagBuilder.knownConflicts.getCopy()
                                                        .subList(lastConflictsCount, newConflictsCount - 1);
                                                conflictToReuse = NodeUtilities.getConflictToReuse(
                                                        newNode.pathLabels, conflicts);

                                                if (conflictToReuse != null) {
                                                    checkingResult.conflicts.add(conflictToReuse);
                                                    qx.cancel();
                                                }
                                            }
                                        }
                                    }
//								Debug.syncMsg("Main thread woke up.");
                                }
                            }

                            if (Thread.currentThread().isInterrupted()) {
                                return;
                            }

                            if (checkingResult.conflictFound()) {
                                //int index = checkingResult.conflicts.size() - 1;
                                newNode.conflict = new ArrayList<T>();
                                List<T> newConflict = checkingResult.conflicts.get(0);
//							System.out.println("Conflict: " + newConflict);
                                newNode.conflict.addAll(newConflict);
                                List<T> handleOfKnown = dagBuilder.knownConflicts
                                        .addItemListNoDups(newNode.conflict);
                                // Reuse this one
                                if (handleOfKnown != null) {
//								 System.out.println("Reusing a known conflict here .."  + handleOfKnown);
                                    newNode.conflict = handleOfKnown;
                                }
                                // We have already added the first conflict, so lets add the other ones.
                                if (!QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {
                                    for (int i = 1; i < checkingResult.conflicts.size(); i++) {
                                        List<T> c = checkingResult.conflicts.get(i);
                                        //								System.out.println("Adding a known conflit");
                                        dagBuilder.knownConflicts.addItemListNoDups(c);
                                    }
                                }

                                // Remember where this conflict was used (Pruning
                                // later on)
                                List<DAGNode<T>> nodesForConflict = dagBuilder.conflictNodeLookup.get(newNode.conflict);
                                if (nodesForConflict == null) {
                                    nodesForConflict = new ArrayList<DAGNode<T>>();
                                }
                                if (!nodesForConflict.contains(newNode)) {
                                    nodesForConflict.add(newNode);
                                }

//							System.out.println("Final conflict: " + newNode.conflict);
                                this.dagBuilder.conflictNodeLookup.put(newNode.conflict, nodesForConflict);
                                if (!QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {
                                    newNode.examplesToCheck = new ArrayList<>(checkingResult.failedExamples);
                                } else {
                                    newNode.examplesToCheck = new ArrayList<>(targetNode.examplesToCheck);
                                }
                                // Either here or there
                                // DJ: Do not do this here.. in full parallel
                                // this.newNodesToExpand.addItem(newNode);
                            } else {
                                // No conflict reported, therefore add new tests.diagnosis
                                // Check if there are any failed examples left
                                // (could be, if we did not calculate conflicts on
                                // the last level)
                                if (checkingResult.failedExamples.size() == 0) {
                                    newNode.diagnosis = newNode.pathLabels;
                                    // No need to expand this node further
                                    newNode.closed = true;

                                    if (dagBuilder.getSessionData().config.maxDiagnoses != -1) {
                                        if (this.dagBuilder.diagnoses.size() >= dagBuilder
                                                .getSessionData().config.maxDiagnoses) {
                                            // DJ: Do not clear this list..
//										this.dagBuilder.nodesToExpand.clear();
                                            return;
                                        }
                                    }
                                }

                            }
                        } catch (DomainSizeException e) {
                            throw e;
                        }
                    }
                    // ---------------------------------------------------------------
                    // We should do the following in a synchronized way as we are changing things in the global list.
                    // Pruning might be a problem...
                    // ---------------------------------------------------------------
                    // Here we are.
                    SharedDAGNodeQueue<T> thenodes = (SharedDAGNodeQueue<T>) this.newNodesToExpand;
                    try {
                        thenodes.processNewDAGNodeFullParallel(newNode, this.dagBuilder, this.model);
                    } catch (Exception e) {
                        System.out.println("Done ...");
                        e.printStackTrace();
                    }
                } else {
                    try {
                        // Point to this existing node instead of creating a new
                        // one.
                        newNode.addParent(targetNode);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Debug.msg("    existing node has been used: " + newNode.nodeName);
                }

            }
        } finally {
            synchronized (FullParallelHSDagBuilder.runningThreadsSync) {
                FullParallelHSDagBuilder.runningThreads--;
            }
        }
    }
}
