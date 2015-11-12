package tests.hierarchical;

import choco.kernel.model.constraints.AbstractConstraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.variables.Variable;

/**
 * A class that extends Choco's constraint class. to be used in hierarchical diagnosis
 * An expandable constraint can stand for a set of constraints in the reasoning process
 * @author dietmar
 *
 */
public class ExpandableConstraint extends AbstractConstraint {

	/** 
	 * Should never be called..
	 * @param type
	 * @param variables
	 */
	public ExpandableConstraint(ConstraintType type, Variable[] variables) {
		super(type, variables);
	}
	
	HierarchyNode hierarchyNode;
	
	/**
	 * A constructor that helps us construct things
	 * @param node
	 */
	public ExpandableConstraint(HierarchyNode node) {
		super(ConstraintType.TRUE,new Variable[1]);
		this.hierarchyNode = node;
	}
	
	
	/**
	 * Make new to String method that returns the hierarchy node
	 */
	@Override
	public String toString(){
		return this.hierarchyNode.toString();
	}
}
