package org.exquisite.diagnosis.engines.prdfs;

import org.exquisite.diagnosis.engines.heuristic.ExtendedDAGNode;

import choco.kernel.model.constraints.Constraint;

public class NodeWithConstraint implements Comparable<NodeWithConstraint> {
	public ExtendedDAGNode node;
	public Constraint constraint;
	
	public NodeWithConstraint(ExtendedDAGNode node, Constraint constraint) {
		this.node = node;
		this.constraint = constraint;
	}

	@Override
	public int compareTo(NodeWithConstraint o) {
		System.out.println("Compare: " + (node.nodeLevel - o.node.nodeLevel));
		return node.nodeLevel - o.node.nodeLevel;
	}
	
	@Override
	public boolean equals(Object other) {
		System.out.println("Equals?");
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof NodeWithConstraint))return false;
	    NodeWithConstraint otherNode = (NodeWithConstraint)other;
		
	    return this.node == otherNode.node && this.constraint == otherNode.constraint;
//	    if (this.node == otherNode.node && this.constraint == otherNode.constraint) {
//	    	System.out.println("Equals");
//	    	return true;
//	    }
//	    return false;
	}
}
