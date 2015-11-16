package tests.hierarchical;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.diagnosis.models.DiagnosisModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Contains a hierarchy of nodes
 * @author dietmar
 *
 */
public class Hierarchy<T> {
	/**
	 * My diagnosis model
	 */
	public DiagnosisModel<T> diagnosisModel;
	/**
	 * The list of leaf components
	 */
	public List<HierarchyNode<T>> leafNodes;
	HierarchyNode<T> rootNode;
	// Let's not do the work twice.
	boolean levelsFixed = false;
	

	/**
	 * Create a new hierarchy for a model
	 * @param model
	 */
	public Hierarchy(DiagnosisModel<T> model) {
		this.diagnosisModel = model;
	}
	
	/**
	 * returns the root note
	 * @return the root node of the hierarchy
	 */
	public HierarchyNode<T> getRootNode() {
		return rootNode;
	}
	
	/**
	 * sets the root note
	 * @param rootNode
	 */
	public void setRootNode(HierarchyNode<T> rootNode) {
		this.rootNode = rootNode;
	}

	/**
	 * Creates a new empty node
	 * @return
	 */
	HierarchyNode<T> createNode() {
		HierarchyNode<T> node = new HierarchyNode<T>(this.diagnosisModel);
		return node;
	}
	
	/**
	 * Create a new node from constraints
	 * @param constraints
	 * @return
	 */
	HierarchyNode<T> createNode(Set<Constraint> constraints) {
		HierarchyNode<T> node = new HierarchyNode<T>(constraints, this.diagnosisModel);
		return node;

	}
	
	/**
	 * Get all leaf components for a given node
	 *
	 */
	List<Constraint> getLeafConstraints(HierarchyNode<T> node) {
		List<Constraint> result = new ArrayList<Constraint>();
		getLeafConstraintsRecursively(node, result);
		return result;
	}

	/**
	 * Get the leaf components
	 */
	List<HierarchyNode<T>> getLeafNodes() {
		if (leafNodes == null) {
			leafNodes = new ArrayList<HierarchyNode<T>>();
			getLeafNodesRecursively(this.rootNode, leafNodes);
		}
		return leafNodes;
	}

	/**
	 * Recursively go through the nodes
	 * @param node
	 * @param result - a set of nodes
	 */
	private void getLeafNodesRecursively(HierarchyNode<T> node, List<HierarchyNode<T>> result) {
		if (node.constraint != null) {
			result.add(node);
		}
		else {
			for (HierarchyNode<T> n : node.nextLevelElements) {
				getLeafNodesRecursively(n, result);
			}
		}
	}
	
	/**
	 * Recursively go through the nodes
	 * @param node
	 * @param result - a set of constraints
	 */
	private void getLeafConstraintsRecursively(HierarchyNode<T> node, List<Constraint> result) {
		if (node.constraint != null) {
			result.add(node.constraint);
		}
		else {
			for (HierarchyNode<T> n : node.nextLevelElements) {
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
		HierarchyNode<T> correspondingLeafNode = null;
		for (HierarchyNode<T> node : this.getLeafNodes()) {
			  if (node.constraint != null && node.constraint == conflictElement) {
				  correspondingLeafNode = node;
//				  System.out.println("Found the leaf node - looking for parent of " + correspondingLeafNode + " " + correspondingLeafNode.getConstraint());
				  break;
			  }
		}
		// Look for the parent
		if (correspondingLeafNode != null) {
			boolean foundNode = false;
			HierarchyNode<T> parent = correspondingLeafNode.parent;
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
		List<HierarchyNode<T>> nodesToPrint = new ArrayList<HierarchyNode<T>>();
		HierarchyNode<T> currentNode = rootNode;
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
	public void fixLevelsForPrintingRecursive(HierarchyNode<T> node, int level) {
		node.level = level;
		if (node.constraint != null) {
			// last level - return;
			return;
		}
		
		if (node.nextLevelElements.size() > 0) {
			for (HierarchyNode<T> nextNode : node.nextLevelElements) {
				fixLevelsForPrintingRecursive(nextNode, level + 1);
			}
		}
	}
	
	
	
}
