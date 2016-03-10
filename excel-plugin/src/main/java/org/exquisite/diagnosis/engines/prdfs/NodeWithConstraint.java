package org.exquisite.diagnosis.engines.prdfs;

import org.exquisite.diagnosis.engines.heuristic.ExtendedDAGNode;

public class NodeWithConstraint<T> implements Comparable<NodeWithConstraint<T>> {
    public ExtendedDAGNode<T> node;
    public T constraint;

    public NodeWithConstraint(ExtendedDAGNode<T> node, T constraint) {
        this.node = node;
        this.constraint = constraint;
    }

    @Override
    public int compareTo(NodeWithConstraint<T> o) {
        System.out.println("Compare: " + (node.nodeLevel - o.node.nodeLevel));
        return node.nodeLevel - o.node.nodeLevel;
    }

    @Override
    public boolean equals(Object other) {
        System.out.println("Equals?");
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof NodeWithConstraint)) return false;
        NodeWithConstraint<T> otherNode = (NodeWithConstraint<T>) other;

        return this.node == otherNode.node && this.constraint == otherNode.constraint;
//	    if (this.node == otherNode.node && this.constraint == otherNode.constraint) {
//	    	System.out.println("Equals");
//	    	return true;
//	    }
//	    return false;
    }
}
