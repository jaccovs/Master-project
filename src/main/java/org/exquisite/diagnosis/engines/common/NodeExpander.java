package org.exquisite.diagnosis.engines.common;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.diagnosis.quickxplain.mergexplain.MergeXplain;
import org.exquisite.diagnosis.quickxplain.mergexplain.ParallelMergeXplain;
import org.exquisite.diagnosis.quickxplain.parallelqx.ParallelQXPlain;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.util.ArrayList;
import java.util.List;

//import tests.tests.dj.qxsim.QXSim;

//import tests.tests.dj.qxsim.QXSim;
/*
import evaluations.conflictposition.MergeXPlainKC;
import evaluations.conflictposition.ParallelMergeXPlainKC;
import evaluations.conflictposition.QuickExplainKC;
*/

/**
 * A class that expands a given node
 *
 * @author dietmar
 */
public class NodeExpander<T> {

    public static boolean USE_LAST_LEVEL_CHECK = true;
    //	private List<HashSet<T>> knownConflicts;
//	private Map<HashSet<T>, DAGNode> conflictNodeLookup;
//	private List<DAGNode> diagnosesNodes;
//	private SharedCollection<DAGNode> constructedNodes;
//	private List<DAGNode> nodesToExpand = new ArrayList<DAGNode>();
    public DiagnosisModel<T> model;
    //	private List<Diagnosis> results = new ArrayList<Diagnosis>();
    public QuickXPlain<T> qx;
    AbstractHSDagBuilder<T> dagBuilder = null;
    SharedCollection<DAGNode<T>> newNodesToExpand = null;
    // Remember how we work
    boolean inParallelMode = false;


    /**
     * The standard version - remember the caller and create a new quickxplain instance
     *
     * @param dagBuilder
     */
    public NodeExpander(AbstractHSDagBuilder<T> dagBuilder) {
        this.dagBuilder = dagBuilder;
        this.qx = createQX(dagBuilder.getSessionData(), dagBuilder);
    }

    /**
     * Parallel version - will collect all the newNodes before adding them to the list
     *
     * @param dagBuilder
     * @param newNodesToExpand
     */
    public NodeExpander(AbstractHSDagBuilder<T> dagBuilder, SharedCollection<DAGNode<T>> newNodesToExpand) {
        this.newNodesToExpand = newNodesToExpand;
        this.inParallelMode = true;
        this.dagBuilder = dagBuilder;
        this.qx = createQX(dagBuilder.getSessionData(), dagBuilder);
    }

    /**
     * Create a qx instance
     *
     * @param sessionData
     * @param dagbuilder
     * @return a new instance
     */
    public static <T> QuickXPlain<T> createQX(ExquisiteSession<T> sessionData,
                                              AbstractHSDagBuilder<T> dagbuilder) {
        switch (AbstractHSDagBuilder.USE_QXTYPE) {
            case QuickXplain:
                return new QuickXPlain<T>(sessionData, dagbuilder);
            case SimulatedQuickXplain:
                throw new RuntimeException("Removed dependency on the expriments");
                //return new QXSim(sessionData, dagbuilder);
            case ParallelQuickXplain:
                return new ParallelQXPlain<>(sessionData, dagbuilder);
            case MergeXplain:
                return new MergeXplain<>(sessionData, dagbuilder);
            case ParallelMergeXplain:
                return new ParallelMergeXplain<>(sessionData, dagbuilder);
            case QX_KC:
                throw new RuntimeException("Removed dependency on the expriments");
                //return new QuickExplainKC(sessionData, dagbuilder);
            case MX_KC:
                throw new RuntimeException("Removed dependency on the expriments");
                //return new MergeXPlainKC(sessionData, dagbuilder);
            case PMX_KC:
                throw new RuntimeException("Removed dependency on the expriments");
                //return new ParallelMergeXPlainKC(sessionData, dagbuilder);
            default:
                throw new IllegalArgumentException("QuickXplain Type not supported!");
        }

    }

    /**
     * The tests.diagnosis model to check.
     *
     * @param value
     */
    public void setDiagnosisModel(DiagnosisModel<T> value) {
        this.model = new DiagnosisModel<T>(value);
        this.qx.setDiagnosisModel(this.model);
    }

//	/**
//	 * Where tests.diagnosis results are stored.
//	 * @param value
//	 */
//	public void setResults(List<Diagnosis> value){this.results = value;}

    /**
     * Expands a node. If the list of newNodesToExpand is not null, we will directly store the new nodes in
     * a global list. Otherwise, we will add it to the list of expandable nodes of the current level
     *
     * @param targetNode
     * @param edgeLabel
     * @throws DomainSizeException
     */
    public void expandNode(DAGNode<T> targetNode, T edgeLabel) throws DomainSizeException {
        //generate path labels for new child node.
        List<T> newPathLabels = new ArrayList<>();
        newPathLabels.addAll(targetNode.pathLabels);
        newPathLabels.add(edgeLabel);

        Debug.msg("Expanding node with new path label: " + Utilities.printConstraintList(newPathLabels, model));

        //checks if path is a superset of an existing tests.diagnosis.
        boolean isSuperset = false;
        // Get a snapshot
        List<DAGNode<T>> diagnosesCopy = null;
        diagnosesCopy = new ArrayList<DAGNode<T>>(this.dagBuilder.diagnosisNodes.getCollection());

        for (DAGNode<T> diagnosisNode : diagnosesCopy) {
            isSuperset = NodeUtilities.isPathLabelSupersetOfDiagnosis(newPathLabels, diagnosisNode.pathLabels);
            if (isSuperset) {
//				System.err.println("Found superset - skipping node");
                Debug.msg("Found superset - skipping node");
                break;
            }
        }

        //if not a superset then carry on.
        if (!isSuperset) {
            //Greiner extension - look for existing nodes to reuse.

			/*
		    DAGNode newNode = NodeUtilities.checkForExistingNode(newPathLabels, this.dagBuilder.allConstructedNodes.getCollection());
		    
			//if none found then make one
			if (newNode == null){					
				Debug.msg("Creating  new node with edge label: " + model.getConstraintName(edgeLabel));
//				System.err.println("Creating  new node with edge label: " + model.getConstraintName(edgeLabel));
				//Construct a new node and do any initial setup...
				newNode = new DAGNode(targetNode, edgeLabel);
				this.dagBuilder.allConstructedNodes.addItem(newNode);
				
				newNode.nodeLevel = targetNode.nodeLevel + 1;
				newNode.pathLabels = newPathLabels;
				*/

            NodeContainer<T> nodeContainer = new NodeContainer<T>();
//			System.out.println("Will check and add now");
            if (NodeUtilities.checkAndAddNode(newPathLabels, this.dagBuilder.allConstructedNodes, targetNode, edgeLabel,
                    nodeContainer)) {
//				System.out.println("Proceeding ..");
                if (dagBuilder != null) {
                    dagBuilder.incrementConstructedNodes();
                }
                DAGNode<T> newNode = nodeContainer.node;


                Debug.msg("    newNode with pathLabel of: " + Utilities.printConstraintList(newNode.pathLabels, model));
                Debug.msg(Thread.currentThread() + "    newNode.nodeLevel: " + newNode.nodeLevel);

				/*
				 * 2.) Node label reuse:
				 */
                //set a flag indicating if a set of labels were found that could be reused for this new node.
//				boolean isReusingLabels = NodeUtilities.checkForConflictLabelReuse(newNode, allNodes);
                List<T> conflictToReuse = NodeUtilities.getConflictToReuse(
                        newNode.pathLabels,
                        new ArrayList<List<T>>(this.dagBuilder.knownConflicts.getCollection()));
//				List<T> conflictToReuse = null;

                if (conflictToReuse != null) {
                    Debug.msg("	Conflict to reuse: " + Utilities.printConstraintList(conflictToReuse, this.model));
                    this.dagBuilder.reuseCount++;
                }
                //check if this is the last node of this branch...
                boolean isLastLevel = AbstractHSDagBuilder.SINGLE_CONFLICT_SEARCH || (USE_LAST_LEVEL_CHECK && NodeUtilities
                        .checkIsLastLevel(
                                newNode,
                                this.model.getPossiblyFaultyStatements(),
                                dagBuilder.getSessionData().config.searchDepth));

//				System.out.println("	lastLevel? : " + isLastLevel);


                //if no labels were found to reuse, then make a call to quickxplain...
                if (conflictToReuse == null) {
                    try {
                        ConflictCheckingResult<T> checkingResult;
                        if (!QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {

                            checkingResult = this.qx.checkExamples(targetNode.examplesToCheck,
                                    new ArrayList<>(newNode.pathLabels), !isLastLevel);
                        } else {
                            synchronized (QuickXPlain.ContinuingSync) {
                                checkingResult = new ConflictCheckingResult<>();
//								Debug.syncMsg("Start checkExamplesParallel()");
                                this.qx.checkExamplesParallel(targetNode.examplesToCheck,
                                        new ArrayList<T>(newNode.pathLabels), !isLastLevel, checkingResult,
                                        dagBuilder.knownConflicts);
                                int lastConflictsCount = dagBuilder.knownConflicts.getCollection().size();
                                while (!checkingResult.conflictFound() && !qx.finished) {
                                    //								Debug.syncMsg("Main thread sleeping.");
                                    try {
                                        QuickXPlain.ContinuingSync.wait();
                                    } catch (InterruptedException e) {
                                        return;
                                    }
                                    if (!checkingResult.conflictFound()) {
                                        int newConflictsCount = dagBuilder.knownConflicts.getCollection().size();
                                        if (newConflictsCount > lastConflictsCount) {
                                            List<List<T>> conflicts = dagBuilder.knownConflicts.getCollection()
                                                    .subList(lastConflictsCount, newConflictsCount - 1);
                                            conflictToReuse = NodeUtilities.getConflictToReuse(
                                                    newNode.pathLabels,
                                                    new ArrayList<List<T>>(conflicts));

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
                        if (checkingResult.conflictFound()) {
//							System.out.println("Conflict FOUND for node [" + targetNode.nodeName + 
//												"] (node depth = " + targetNode.nodeLevel + 
//												")  with path label of: " + Utilities.printConstraintList(targetNode.pathLabels, this.model) +
//												"    node conflict = {" + Utilities.printConstraintList(targetNode.conflict, model) + "}");
                            //Conflict reported, therefore add conflict elements to node.
//							int index = checkingResult.conflicts.size() - 1;
                            newNode.conflict = new ArrayList<T>();
                            List<T> newConflict = checkingResult.conflicts.get(0);
//							boolean pruningRequired = Utilities.isSubsetOfKnownConflict(newConflict,dagBuilder.knownConflicts);
//							if (pruningRequired) {
//								System.err.println("Pruning required for " + Utilities.printConstraintListOrderedByName(newConflict, model));
//							}
//							System.out.println("NEW CONFLICT OF SIZE: " + newConflict.size());

                            newNode.conflict.addAll(newConflict);
                            List<T> handleOfKnown = dagBuilder.knownConflicts
                                    .addItemListNoDups(newNode.conflict);
                            // Reuse this one
                            if (handleOfKnown != null) {
//								System.out.println("Reusing a known conflict here ..");
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

                            // Remember where this conflict was used (Pruning later on)
                            List<DAGNode<T>> nodesForConflict = dagBuilder.conflictNodeLookup.get(newNode.conflict);
                            if (nodesForConflict == null) {
                                nodesForConflict = new ArrayList<DAGNode<T>>();
                            }
                            if (!nodesForConflict.contains(newNode)) {
                                nodesForConflict.add(newNode);
                            }

                            this.dagBuilder.conflictNodeLookup.put(newNode.conflict, nodesForConflict);
                            if (!QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {
                                newNode.examplesToCheck = new ArrayList<>(checkingResult.failedExamples);
                            } else {
                                newNode.examplesToCheck = new ArrayList<>(targetNode.examplesToCheck);
                            }
                            // Either here or there
                            if (!inParallelMode) {
                                this.dagBuilder.nodesToExpand.add(newNode);
                            } else {
                                this.newNodesToExpand.add(newNode);
                            }
                        } else {
                            //No conflict reported, therefore add new tests.diagnosis
                            //Check if there are any failed examples left (could be, if we did not calculate conflicts on the last level)
                            if (checkingResult.failedExamples.size() == 0) {
                                newNode.diagnosis = newNode.pathLabels;
                                //No need to expand this node further
                                newNode.closed = true;

//								System.out.println(" --> GOT A DIAGNOSIS: " + Utilities.printConstraintList(newNode.tests.diagnosis, model));


                                if (dagBuilder.getSessionData().config.maxDiagnoses != -1) {
                                    if (this.dagBuilder.diagnoses.size() >= dagBuilder
                                            .getSessionData().config.maxDiagnoses) {
                                        this.dagBuilder.nodesToExpand.clear();
                                        if (inParallelMode) {
                                            // DJ: TODO - what should we do here? Perhaps simply return from the work
//											System.out.println("Reached enough diagnoses...");
                                            return;
                                        }
                                    } else {
                                        this.dagBuilder.diagnoses.add(new Diagnosis<T>(newNode.diagnosis, this.model));
                                        this.dagBuilder.diagnosisNodes.getCollection().add(newNode);
                                    }
                                } else {
//									System.out.println("Adding a tests.diagnosis ..");
                                    this.dagBuilder.diagnoses.add(new Diagnosis<T>(newNode.diagnosis, this.model));
                                    this.dagBuilder.diagnosisNodes.getCollection().add(newNode);
                                }

                            } // Some debug msg
                            else {
                                Debug.msg(
                                        "Found an open node at the last level of search: ");//  + Utilities.printConstraintList(newNode.pathLabels, model));
                            }
                        }
                    } catch (DomainSizeException e) {
                        throw e;
                    }
                } else {
                    // DJ: If we are re-using stuff, we still need to create the new node...
//					System.out.println(" Adding node with reused conflict: "  + Utilities.printConstraintList(conflictToReuse, model));
                    newNode.conflict = new ArrayList<T>();
                    newNode.conflict.addAll(conflictToReuse);
                    newNode.examplesToCheck = new ArrayList<>(targetNode.examplesToCheck);
                    if (!inParallelMode) {
                        this.dagBuilder.nodesToExpand.add(newNode);
                    } else {
                        this.newNodesToExpand.add(newNode);
                    }
                }

                /**
                 * See if the node can be closed.
                 */
                if (!newNode.closed) {
                    NodeUtilities.applyNodeClosingRules(newNode, this.dagBuilder.diagnosisNodes.getCollection());
                }
            }
            /*else {
                try {
					//Point to this existing node instead of creating a new one.
					newNode.addParent(targetNode);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Debug.msg("    existing node has been used: " + newNode.nodeName);
			}*/
        }
    }
}
