package tests.hierarchical;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.exquisite.diagnosis.models.DiagnosisModel;

import choco.kernel.model.constraints.Constraint;

/**
 * Contains a hierarchy of nodes
 * @author dietmar
 *
 */
public class Hierarchy {
	HierarchyNode rootNode;

	/**
	 * returns the root note
	 * @return the root node of the hierarchy
	 */
	public HierarchyNode getRootNode() {
		return rootNode;
	}

	/**
	 * My diagnosis model
	 */
	public DiagnosisModel diagnosisModel;
	
	/**
	 * Create a new hierarchy for a model
	 * @param model
	 */
	public Hierarchy(DiagnosisModel model) {
		this.diagnosisModel = model;
	}
	

	/**
	 * The list of leaf components
	 */
	public List<HierarchyNode> leafNodes;
	
	/**
	 * Creates a new empty node
	 * @return
	 */
	HierarchyNode createNode() {
		HierarchyNode node = new HierarchyNode(this.diagnosisModel);
		return node;
	}
	
	/**
	 * Create a new node from constraints
	 * @param constraints
	 * @return
	 */
	HierarchyNode createNode(Set<Constraint> constraints) {
		HierarchyNode node = new HierarchyNode(constraints, this.diagnosisModel);
		return node;
		
	}
	
	/**
	 * sets the root note
	 * @param rootNode
	 */
	public void setRootNode(HierarchyNode rootNode) {
		this.rootNode = rootNode;
	}
	
	
	/**
	 * Get all leaf components for a given node
	 *  
	 */
	List<Constraint> getLeafConstraints(HierarchyNode node) {
		List<Constraint> result = new ArrayList<Constraint>();
		getLeafConstraintsRecursively(node, result);
		return result;
	}
	
	/**
	 * Get the leaf components
	 */
	List<HierarchyNode> getLeafNodes() {
		if (leafNodes == null) {
			leafNodes = new ArrayList<HierarchyNode>();
			getLeafNodesRecursively(this.rootNode, leafNodes);
		}
		return leafNodes;
	}

	/**
	 * Recursively go through the nodes
	 * @param node
	 * @param result - a set of nodes
	 */
	private void getLeafNodesRecursively(HierarchyNode node, List<HierarchyNode> result) {
		if (node.constraint != null) {
			result.add(node);
		}
		else {
			for (HierarchyNode n : node.nextLevelElements) {
				getLeafNodesRecursively(n, result);
			}
		}
	}
	
	/**
	 * Recursively go through the nodes
	 * @param node
	 * @param result - a set of constraints
	 */
	private void getLeafConstraintsRecursively(HierarchyNode node, List<Constraint> result) {
		if (node.constraint != null) {
			result.add(node.constraint);
		}
		else {
			for (HierarchyNode n : node.nextLevelElements) {
				getLeafConstraintsRecursively(n, result);
			}
		}
	}
	
	/**
	 * Maps a detailed conflict to a higher abstraction layer
	 * @param conflict a given detailed conflict
	 * @param hierarchicalConflict the hierarchical context
	 * @return the result of the mapping process
	 */
	List<Constraint> mapDetailedConflictToAbstractionLevel(List<Constraint> conflict, List<ExpandableConstraint> hierarchicalContext){
		List<Constraint> result = new ArrayList<Constraint>();
		for (Constraint conflictElement: conflict) {
			// If the constraint is contained in the context, include it at this level
			if (hierarchicalContext.contains(conflictElement)){
				result.add(conflictElement);
			}
			else {
				Constraint mappedConstraint = mapConstraintToContext(conflictElement, hierarchicalContext);
//				System.out.println("Mapped constraint: " + mappedConstraint);
				result.add(mappedConstraint);
			}
		}
		return result;
		
	}
	
	
	/**
	 * Maps a constraint to an expandable component
	 * @param conflictElement
	 * @param hierarchicalContext
	 * @return
	 */
	Constraint mapConstraintToContext(Constraint conflictElement, List<ExpandableConstraint> hierarchicalContext) {
		Constraint result = null;
		// We know that we don't have to look in the leaf nodes here
		// Look for the leaf-level component with the constraint
		HierarchyNode correspondingLeafNode = null;
		for (HierarchyNode node : this.getLeafNodes()) {
			  if (node.constraint != null && node.constraint == conflictElement) {
				  correspondingLeafNode = node;
//				  System.out.println("Found the leaf node - looking for parent of " + correspondingLeafNode + " " + correspondingLeafNode.getConstraint());
				  break;
			  }
		}
		// Look for the parent
		if (correspondingLeafNode != null) {
			boolean foundNode = false;
			HierarchyNode parent = correspondingLeafNode.parent;
//			System.out.println("The parent in the hierarchy " + parent);
			while (foundNode == false && parent != null) {
				// Go through the expandable constraints and look if this corresponds to the node here
				for (ExpandableConstraint exCtr : hierarchicalContext) {
					if (exCtr.hierarchyNode == parent) {
//						System.out.println("Found the correct context - returning the correct contextual constraint");
						return exCtr;
					}
				}
				parent = parent.parent;
			}
			System.err.println("FATAL: Could not map constraint to expandable constraint");
		}
		
		
		return result;
	}
	
	
	// Let's not do the work twice.
	boolean levelsFixed = false;
	
	/**
	 * Prints out the hierarchy.
	 * The level numbering is stil faulty (API design). 
	 * Could simply calculate the levels once we are through with the construction
	 */
	public void printHierarchy() {
		if (levelsFixed == false) {
			fixLevelNumbering();
		}
		// The remaining list to print - wil do things in breadth first form
		List<HierarchyNode> nodesToPrint = new ArrayList<HierarchyNode>();
		HierarchyNode currentNode = rootNode;
		// fill in the elements of the first level
		nodesToPrint.addAll(rootNode.nextLevelElements);
		// While there is some work
		while (nodesToPrint.size() !=0) {
			// Get the first element
			currentNode = nodesToPrint.get(0);
			System.out.println(currentNode);
			if (currentNode.nextLevelElements != null && currentNode.nextLevelElements.size() > 0) {
				nodesToPrint.addAll(currentNode.nextLevelElements);
			}
			nodesToPrint.remove(currentNode);
		}
	}
	
	// Fix levels for printing and debugging
	// Gives the user the opportunity to use the api more freely
	// we do this at the end of the modeling process
	public void fixLevelNumbering() {
		this.levelsFixed = true;
		// go through the tree in order
		fixLevelsForPrintingRecursive(rootNode, 0);
	}
	
	// recursive work to be done
	public void fixLevelsForPrintingRecursive(HierarchyNode node, int level) {
		node.level = level;
		if (node.constraint != null) {
			// last level - return;
			return;
		}
		
		if (node.nextLevelElements.size() > 0) {
			for (HierarchyNode nextNode: node.nextLevelElements) {
				fixLevelsForPrintingRecursive(nextNode, level + 1);
			}
		}
	}
	
	
	
}
