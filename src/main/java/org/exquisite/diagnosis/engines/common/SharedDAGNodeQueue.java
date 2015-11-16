package org.exquisite.diagnosis.engines.common;

import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.DiagnosisModel;

import java.util.List;

/**
 * A specific sub class of a shared collection that implements a method for
 * managing the parallel insertion of DAG nodes for tree expansion
 *
 * @author dietmar
 */
public class SharedDAGNodeQueue<T> extends SharedCollection<T> {

    public static int addedNodes = 0;

    /**
     * This method makes the correct updates to the list of known dagnodes
     *
     * @param node
     */
    public void processNewDAGNodeFullParallel(DAGNode<T> newNode,
                                              AbstractHSDagBuilder<T> dagBuilder, DiagnosisModel<T> model)
            throws Exception {
        // Need to some work
        synchronized (getWriteLock()) {

            // TS: We can add this test to guarantee, that only maxDiagnoses will be found.
//			if (Thread.currentThread().isInterrupted()||dagBuilder.diagnosisNodes.getCollection().size() >= dagBuilder.sessionData.config.maxDiagnoses) {
//				return;
//			}


            if (newNode.nodeStatusP == DAGNode.nodeStatusParallel.cancelled) {
//				System.err.println("I have been cancelled in the meantime ...");
                // Do nothing actually
                return;
            }

            // Check if another tests.diagnosis superset was found in the meantime
//			System.out.println("My tests.diagnosis so far: ");
//			List<DAGNode> diags = dagBuilder.diagnosisNodes.getCollection();
//			for (DAGNode diag : diags) {
//				System.out.println(Utilities.printConstraintList(diag.tests.diagnosis, model));
//			}

//			System.out.println("New diag: " + Utilities.printConstraintList(newNode.tests.diagnosis, model));

            boolean closedBecauseOfSuperset = NodeUtilities
                    .applyNodeClosingRulesEQ(newNode, dagBuilder.diagnosisNodes.getCollection());
//			System.out.println("ClosedBecause: " + closedBecauseOfSuperset);
            /**
             * See if the node can be closed.
             */
            if (closedBecauseOfSuperset) {
                // Do nothing
                return;
            }

            // Check if a tests.diagnosis was already found.
            // IF diagnoses found
            if (newNode.diagnosis != null && newNode.diagnosis.size() > 0) {
                // Do something..
//				System.out.println("Adding a tests.diagnosis ..");
//				System.out.println(Utilities.printConstraintList(newNode.tests.diagnosis, model));
                newNode.closed = true;
//				dagBuilder.diagnoses.add(new Diagnosis(newNode.tests.diagnosis, model));
                dagBuilder.diagnosisNodes.add(newNode);
                // Go through all the nodes we have so far.
                // Is there another node that can be removed or pruned
                // DJ: This might be not thread-safe ... TODO!
                for (DAGNode<T> node : (List<DAGNode<T>>) this.getCollection()) {
                    if (NodeUtilities.isPathLabelSupersetOfOrEqualDiagnosis(node.diagnosis, newNode.diagnosis)) {
//						System.out.println("Found a super set in the expansion list: will remove the node, nodestatus: " + node.nodeStatusP);
                        dagBuilder.diagnosisNodes.getCollection().remove(node);
                        // Cancel processing
                        if (node.nodeStatusP == DAGNode.nodeStatusParallel.active
                                || node.nodeStatusP == DAGNode.nodeStatusParallel.scheduled) {
                            node.nodeStatusP = DAGNode.nodeStatusParallel.cancelled;
                        }
                    }
                }
            }

            // ---------------------------------------------------------------
            // IN ANY CASE
            dagBuilder.allConstructedNodes.add(newNode);
//			if (dagBuilder != null ) {
//				dagBuilder.incrementConstructedNodes();
//			}

            add((T) newNode);
            addedNodes++;

            // Check for duplicate paths
//			ParallelHSDagBuilder.checkForDuplicatePaths((List<DAGNode>) this.getCollection());
        }

    }

}
