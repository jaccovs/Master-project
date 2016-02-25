package tests.hierarchical;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.core.model.DiagnosisModel;

import java.util.HashSet;
import java.util.Set;

/**
 * A node in the diagnosis hierarchy
 * @author dietmar
 *
 */
public class HierarchyNode<T> {
	
	/**
	 * A pointer to the diagnosismodel
	 */
	public DiagnosisModel diagnosisModel;
	/**
	 * the current level
	 */
	public int level;
	/**
	 * the set of elements in the next level
	 */
	Set<HierarchyNode<T>> nextLevelElements = new HashSet<HierarchyNode<T>>();
	/**
	 * The constraint if it is a leaf node.
	 */
	Constraint constraint;
	String nodeName = "root";
	/**
	 * Pointer to the parent node for faster mapping
	 */
	HierarchyNode<T> parent;
	
	/**
	 * Creates a node with only leaf level elemnts
	 */
	public HierarchyNode(Set<Constraint> constraints, DiagnosisModel dm) {
		this.getDiagnosisModel() = dm;
		nextLevelElements = new HashSet<HierarchyNode<T>>();
		for (Constraint c: constraints){
			nextLevelElements.add(new HierarchyNode<T>(c, diagnosisModel.getConstraintName(c), dm, this));
		}
	}
	
	/**
	 * Creates a hierarchy node with a leaf elements
	 * @param c
	 */
	public HierarchyNode(Constraint c, String name, DiagnosisModel dm, HierarchyNode<T> parent) {
		this.getDiagnosisModel() = dm;
		this.constraint = c;
		this.nodeName = name;
		this.parent = parent;
	}
	
	/**
	 * Creates a new node
	 */
	public HierarchyNode(DiagnosisModel dm) {
		this.getDiagnosisModel() = dm;
		this.nextLevelElements = new HashSet<HierarchyNode<T>>();
	}
	
	// Add a successor node
	public void addSonNode(HierarchyNode<T> son, String name)
	{   this.nextLevelElements.add(son);
		son.nodeName = name;
		son.parent = this;
	}
	
	/**
	 * Getter for the constraints
	 */
	public Constraint getConstraint() { return constraint;}

	/**
	 * Getter for the next level elements
	 * @return
	 */
	public Set<HierarchyNode<T>> getNextLevelElements() {
		return nextLevelElements;
	}
	
	/**
	 * A string representation of the node
	 */
	public String toString() {
		String indent = "";
		for (int i=0;i<this.level;i++) {
			indent += "\t";
		}
		StringBuffer result = new StringBuffer();
		result.append(indent + "Node at level: " + this.level + "\n" + indent + "Name:");
		if (this.constraint != null) {
			result.append(indent + "Leaf: " + this.nodeName);
		}
		else {
			result.append(indent + "Non-Leaf " + this.nodeName + " (" + this.nextLevelElements.size() + " successors)");
		}
		
		return result.toString();
	}
	
}



