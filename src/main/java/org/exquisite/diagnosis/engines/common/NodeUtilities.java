package org.exquisite.diagnosis.engines.common;

import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class NodeUtilities {
    /**
     * 3.) Pruning: 	Method of pruning re-implemented according to Greiner's correction paper...
     * <p>
     * i.
     * look for a node where newConflict is a subset of that nodes conflict.
     * then relabel that nodes conflict set with the newConflict set and remove any child nodes that do not
     * have edge labels contained in newConflict set - except for any child nodes that have additional parent nodes
     * attached to them (then just remove the reference to the node from the child)
     * <p>
     * ii. "Interchange the sets in the collection" effectively - delete the old conflict set from
     */
    public static <T> boolean applyPruningRules(List<T> newConflict,
                                                List<List<T>> knownConflicts,
                                                Map<List<T>, List<DAGNode<T>>> conflictNodeLookup) {
        boolean isPruned = false;
        List<List<T>> knownConflictsCopy = new ArrayList<>(knownConflicts);
        // work on a copy to be able to prune it
        for (List<T> conflict : knownConflictsCopy) {
            if (conflict.containsAll(newConflict) && conflict.size() > newConflict.size()) {
                List<DAGNode<T>> nodesToPrune = conflictNodeLookup.get(conflict);
                // Nothing to do
                if (nodesToPrune == null) {
                    return false;
                }
                for (DAGNode<T> nodeToPrune : nodesToPrune) {
                    if (nodeToPrune != null) { //node may have already been pruned.
                        pruneNode(nodeToPrune, newConflict, knownConflicts, conflictNodeLookup);
                        isPruned = true;
                    }
                }
                // remove the old conflict from the list of known conflicts
                knownConflicts.remove(conflict);
                // Update the conflict node lookup
                conflictNodeLookup.remove(conflict);
//				conflictNodeLookup.put(nodeToPrune.conflict, nodeToPrune);		


//				DAGNode nodeToPrune = conflictNodeLookup.get(conflict);
//				if (nodeToPrune != null){ //node may have already been pruned.
//					pruneNode(nodeToPrune, newConflict, knownConflicts, conflictNodeLookup);
//					isPruned = true;				
//					break;
//				}
            }
        }
        return isPruned;
    }

    /**
     * Replaces node conflict with a new conflict set (that should be a subset of original) and removes any child node references
     * for arcs that are not present in the new conflict set.
     * Sets the nodes pruned property to true.
     *
     * @param nodeToPrune - the node that is to be pruned.
     * @param newConflict - the new conflict set to be added to the noded to be pruned.
     */
    public static <T> void pruneNode(DAGNode<T> nodeToPrune,
                                     List<T> newConflict,
                                     List<List<T>> knownConflicts,
                                     Map<List<T>, List<DAGNode<T>>> conflictNodeLookup) {
        List<T> list = new ArrayList<>();
        list.addAll(nodeToPrune.conflict);
        list.removeAll(newConflict);

        for (T arc : list) {
            DAGNode<T> invalidChild = nodeToPrune.children.get(arc);
            nodeToPrune.children.remove(arc);
            if (invalidChild != null) {
                invalidChild.getParents().remove(nodeToPrune);

                // TS: Does not make sense!
//				if(invalidChild.getParents().size() == 0)
//				{				
//					invalidChild = null;					
//				}			
            }
        }
        // Update this node
        nodeToPrune.pruned = true;
        nodeToPrune.conflict = newConflict;

        // Do we already have lookup list for this new conflict already?
        List<DAGNode<T>> nodeList = conflictNodeLookup.get(newConflict);
        if (nodeList == null) {
            nodeList = new ArrayList<DAGNode<T>>();
            conflictNodeLookup.put(newConflict, nodeList);
        }
        nodeList.add(nodeToPrune);

        // Update to be done in the calling function
//		conflictNodeLookup.remove(nodeToPrune.conflict);
//		knownConflicts.remove(nodeToPrune.conflict);
//		nodeToPrune.conflict = newConflict;	
//		conflictNodeLookup.put(nodeToPrune.conflict, nodeToPrune);		


    }

    /**
     * Checks whether a node can be closed or not.
     *
     * @param targetNode     - the node to check.
     * @param diagnosisNodes - list of all nodes that point to a tests.diagnosis
     * @return true if node was closed, otherwise false.
     */
    public static <T> boolean applyNodeClosingRules(DAGNode<T> targetNode, List<DAGNode<T>> diagnosisNodes) {
        boolean isClosed = false;
        //this code is to check if the target nodes path is a superset of an existing tests.diagnosis
        List<DAGNode<T>> diagnosesCopy;
        synchronized (diagnosisNodes) {
            diagnosesCopy = new ArrayList<DAGNode<T>>(diagnosisNodes);
        }

        for (DAGNode<T> diagnosisNode : diagnosesCopy) {
            //filter out newNode from nodes collection -
            if (!diagnosisNode.equals(targetNode)) {
                isClosed = isPathLabelSupersetOfDiagnosis(targetNode.pathLabels, diagnosisNode.pathLabels);
                if (isClosed) {
                    break;
                }
            }
        }
        targetNode.closed = isClosed;
        return isClosed;
    }

    /**
     * Checks whether a node can be closed or not. Also closes identical sets (not only supersets)
     *
     * @param targetNode     - the node to check.
     * @param diagnosisNodes - list of all nodes that point to a tests.diagnosis
     * @return true if node was closed, otherwise false.
     */
    public static <T> boolean applyNodeClosingRulesEQ(DAGNode<T> targetNode, List<DAGNode<T>> diagnosisNodes) {
        boolean isClosed = false;
        //this code is to check if the target nodes path is a superset of an existing tests.diagnosis
        List<DAGNode<T>> diagnosesCopy;
        synchronized (diagnosisNodes) {
            diagnosesCopy = new ArrayList<DAGNode<T>>(diagnosisNodes);
        }

        for (DAGNode<T> diagnosisNode : diagnosesCopy) {
            //filter out newNode from nodes collection -
            if (!diagnosisNode.equals(targetNode)) {
                isClosed = isPathLabelSupersetOfOrEqualDiagnosis(targetNode.pathLabels, diagnosisNode.pathLabels);
                if (isClosed) {
                    break;
                }
            }
        }
        targetNode.closed = isClosed;
        return isClosed;
    }

    /**
     * Checks if the node path is a superset of a path to an existing tests.diagnosis.
     *
     * @param candidatePathLabels - the path to check.
     * @param diagnosis           - the tests.diagnosis to check the path against.
     * @return true if the path is a subset, otherwise false.
     */
    public static <T> boolean isPathLabelSupersetOfDiagnosis(List<T> candidatePathLabels,
                                                             List<T> diagnosis) {
        return candidatePathLabels.containsAll(diagnosis) && diagnosis.size() < candidatePathLabels.size();
    }

    /**
     * Checks if the node path is a superset or equal of a path to an existing tests.diagnosis.
     *
     * @param candidatePathLabels - the path to check.
     * @param diagnosis           - the tests.diagnosis to check the path against.
     * @return true if the path is a subset, otherwise false.
     */
    public static <T> boolean isPathLabelSupersetOfOrEqualDiagnosis(List<T> candidatePathLabels,
                                                                    List<T> diagnosis) {
        return candidatePathLabels.containsAll(diagnosis) && diagnosis.size() <= candidatePathLabels.size();
    }

    /**
     * Checks to see if a node is at the last level - to avoid calls to solver etc.
     *
     * @param newNode: the node to check to see if it is at the last level.
     * @param nodes:   a collection of nodes to perform the check against.
     * @return true if no further expansion at this node is needed, otherwise false.
     */
    public static <T> boolean checkIsLastLevel(DAGNode<T> newNode, List<T> allPossibleFaultyStatements,
                                               int searchDepth) {
        boolean isLastLevel = false;


        //if we are down to the last constraint to be removed from the model...
        boolean lookedAtAllConstraints = newNode.pathLabels.containsAll(
                allPossibleFaultyStatements);//this.model.possiblyFaultyStatements.containsAll(newNode.pathLabels);

        if (lookedAtAllConstraints) {
            Debug.msg("Path has all possibly faulty statements in it, therefore this node is at last level.");
            return true;
        }

        //if the search depth has been set explicitly then compare with results from
        //the loop above - prevents the tree from being expanded to depth greater than maximum.
        //if a tests.diagnosis has been found further up the path already.
        if (searchDepth != -1) {
            isLastLevel = (newNode.nodeLevel >= searchDepth);
        }

        if (isLastLevel) {
            Debug.msg("Node level is greater than or equal to max search depth, therefore this node is at last level.");
        } else {
            Debug.msg("Node is not at last level.");
        }

        return isLastLevel;
    }

//	/**
//	 * 2.) Check for Node conflict label reuse:
//	 * iterate through each node with a conflict
//	 * if set intersection of the new node path labels and existing conflict labels is empty then reuse labels from existing node n in new node n1.
//	 *
//	 * @param newNode: the node to check
//	 * @param nodes: the collection of nodes to check against. 
//	 * @return true if the node is given a set of labels from another node otherwise false.
//	 */
//	@SuppressWarnings("unchecked")
//	public static boolean checkForConflictLabelReuse(DAGNode newNode, List<DAGNode> nodes)
//	{
//		// Make a copy for the parallel version
//		List<DAGNode> nodesCopy = null;
//		synchronized(nodes) {
//			nodesCopy = new ArrayList<DAGNode>(nodes);
//		}
//		boolean isReusingLabels = false;
//	
//		for(DAGNode existingNode : nodesCopy)
//		{			
//			//filter out newNode from node list.
//			if (!existingNode.equals(newNode)){	
//				if (existingNode.conflict != null){
//					if (existingNode.conflict.size() != 0 && !existingNode.closed){
//						//Existing node has conflicts and not closed, check if label can be reused.
//						//make a copy of the conflict (because retainAll modifies the collection it works on
//						List<Constraint> set = new ArrayList<Constraint>(existingNode.conflict);
//						//use retain all to perform a set intersection
//						set.retainAll(newNode.pathLabels);
//						//if there is no overlap, then reuse the conflict labels
//						if (set.size() == 0){
//							newNode.conflict = new HashSet<Constraint>(existingNode.conflict);									
//							isReusingLabels = true;						
//							break;
//						}
//					}
//				}
//			}
//		}
//		return isReusingLabels;
//	}

    /**
     * Check if we can reuse a conflict for a new node
     *
     * @param pathLabelOfNewNode the path label of a new node
     * @param knownConflicts     the set of the known conflicts
     * @return a reusable conflict or null otherwise
     */
    public static <T> List<T> getConflictToReuse(List<T> pathLabelOfNewNode,
                                                 List<List<T>> knownConflicts) {
        List<T> temp = null;
        for (List<T> conflict : knownConflicts) {
            temp = new ArrayList<>();
            temp.addAll(conflict);
            int origSize = temp.size();
            temp.removeAll(pathLabelOfNewNode);
            if (origSize == temp.size()) {
                // no overlap
//				System.out.println("Can reuse a conflict ...");
                return conflict;
            }
        }
        return null;
    }


    /**
     * Searches existing nodes to find if any have same path as potential new node.
     *
     * @param pathLabels
     * @return null if no node was found otherwise DAGNode that could be pointed to...
     */
    public static <T> DAGNode<T> checkForExistingNode(List<T> pathLabels, List<DAGNode<T>> nodes) {
        DAGNode<T> result = null;
        // Get a snapshot
        List<DAGNode<T>> nodesCopy = null;
//		synchronized (nodes){
        nodesCopy = new ArrayList<>(nodes);
//		}
        for (DAGNode<T> node : nodesCopy) {
            if (node.pathLabels.size() == pathLabels.size() && node.pathLabels.containsAll(pathLabels)) {
                result = node;
                break;
            }
        }
        return result;
    }

    /**
     * Checks for existance of a node with the given pathLabels and creates a new node in nodeContainer, if no such exists.
     * Otherwise the existing node ist returned in nodeContainer.
     *
     * @param pathLabels    - the labels of the path to be checked for existence.
     * @param nodes         - a collection of nodes that could contain the given pathLabels.
     * @param parent        - the parent of the node that should be created.
     * @param arcLabel      - the label from the parent that immediately points to the new created node.
     * @param nodeContainer - a NodeContainer where the newly created node or the existing one is stored.
     * @return true if a new node was created, otherwise false.
     */
    public synchronized static <T> boolean checkAndAddNode(List<T> pathLabels, SharedCollection<DAGNode<T>>
            nodes,
                                                           DAGNode<T> parent, T arcLabel,
                                                           NodeContainer<T> nodeContainer) {
        DAGNode<T> newNode = NodeUtilities.checkForExistingNode(pathLabels, nodes.getCollection());

        //if none found then make one
        if (newNode == null) {
            //Debug.msg("Creating  new node with edge label: " + model.getConstraintName(edgeLabel));
//			System.err.println("Creating  new node with edge label: " + model.getConstraintName(edgeLabel));
            //Construct a new node and do any initial setup...
            newNode = new DAGNode<T>(parent, arcLabel);
            nodes.add(newNode);

            newNode.nodeLevel = parent.nodeLevel + 1;
            newNode.pathLabels = pathLabels;
            nodeContainer.node = newNode;
            return true;
        } else {
            try {
                //Point to this existing node instead of creating a new one.
                newNode.addParent(parent);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Debug.msg("    existing node has been used: " + newNode.nodeName);
            nodeContainer.node = newNode;
            return false;
        }
    }

    /**
     * Utility to recurse through a node and its children.
     *
     * @param targetNode - the node to traverse.
     */
    public static <T> void traverseNode(DAGNode<T> targetNode) {
        final String indent = "    ";
        String nodeMessage = "";

        for (int i = 0; i < targetNode.nodeLevel; i++) {
            nodeMessage += indent;
        }

        nodeMessage += targetNode.nodeName;
        System.out.println(nodeMessage);
        if (targetNode.conflict != null) {
            for (T c : targetNode.conflict) {
                DAGNode<T> child = targetNode.children.get(c);
                if (child != null) {
                    traverseNode(child);
                }
            }
        }
    }

    public static <T> void traverseNode(DAGNode<T> targetNode, Hashtable<String, T> constraintLookup) {
        final String indent = "    ";
        String nodeMessage = "";

        for (int i = 0; i < targetNode.nodeLevel; i++) {
            nodeMessage += indent;
        }

        nodeMessage += targetNode.nodeName;
        nodeMessage += " {";
        if (targetNode.conflict != null && !targetNode.conflict.isEmpty()) {
            for (T c : targetNode.conflict) {
                nodeMessage += Utilities.getKeyByValue(constraintLookup, c);
                nodeMessage += ", ";
            }
            nodeMessage = nodeMessage.substring(0, nodeMessage.length() - 2);
        }
        nodeMessage += "}";

        System.out.println(nodeMessage);
        if (targetNode.conflict != null) {
            for (T c : targetNode.conflict) {
                DAGNode<T> child = targetNode.children.get(c);
                if (child != null) {
                    traverseNode(child, constraintLookup);
                }
            }
        }
    }

    /**
     * Traverses children of a target node and displays results in the format of:
     * <p>
     * (comma delimited path label set) { comma delimited conflict set }
     * <p>
     * e.g.
     * (null) {a, b, c}     <- root node
     * (a) {e, f}		<- node from path a, with a conflict set e, f
     * (b) {}			<- node with empty conflict set.
     * (c) {a, e}		...
     *
     * @param targetNode
     * @param constraintLookup
     */
    public static <T> void traverseNode(DAGNode<T> targetNode, Map<T, String> constraintLookup) {
        final String indent = "    ";
        String nodeMessage = "";

        for (int i = 0; i < targetNode.nodeLevel; i++) {
            nodeMessage += indent;
        }

        //Path labels

        nodeMessage += " (";
        if (targetNode.pathLabels.isEmpty()) {
            nodeMessage += "root";
        } else {
            for (T label : targetNode.pathLabels) {
                nodeMessage += constraintLookup.get(label);
                nodeMessage += ", ";
            }
            nodeMessage = nodeMessage.substring(0, nodeMessage.length() - 2);
        }
        nodeMessage += ")";

        //Conflict set
        nodeMessage += " {";
        if (targetNode.conflict != null) {
            for (T c : targetNode.conflict) {
                nodeMessage += constraintLookup.get(c);
                nodeMessage += ", ";
            }
            nodeMessage = nodeMessage.substring(0, nodeMessage.length() - 2);
        }
        nodeMessage += "}";


        System.out.println(nodeMessage);
        if (targetNode.conflict != null) {
            for (T c : targetNode.conflict) {
                DAGNode<T> child = targetNode.children.get(c);
                if (child != null) {
                    traverseNode(child, constraintLookup);
                }
            }
        }
    }
}
