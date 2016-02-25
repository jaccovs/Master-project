package org.exquisite.core.engines.tree;

import org.exquisite.core.costestimators.CostsEstimator;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * A class representing a tree node
 *
 * @author Dietmar
 */
public class Node<T> {

    private static long generationOrderCounter = 0;
    public final long generationOrder = generationOrderCounter++;
    /**
     * The tree level
     */
    private int nodeLevel = 0;
    private BigDecimal costs = BigDecimal.ONE;
    /**
     * Quick reference for finding children for this node.
     */
    private Map<T, Node<T>> children = new HashMap<>();
    /**
     * The label of the arc leading here
     */
    private T arcLabel = null;
    /**
     * The nodeLabel at the node
     */
    private Set<T> nodeLabel = null;
    /**
     * Labels of the path to here
     */
    private Set<T> pathLabels;
    /**
     * Status of the node
     */
    private Status status;
    /**
     * The node/s that this node is a child of. (null if this node is the root node)
     * Changed this to a list as there may be cases where a node could be a child of multiple parent nodes.
     */
    private List<Node<T>> parents = null;

    /**
     * Constructor for the root node.
     *
     * @param conflict - the initial nodeLabel set returned from call to qx...
     */
    public Node(Set<T> conflict) {
        this.parents = null;
        this.arcLabel = null;
        this.nodeLabel = conflict;
        this.nodeLevel = 0;
        this.costs = BigDecimal.ZERO;
    }

    /**
     * Constructor for child nodes
     *
     * @param parent   - the parent for the newly constructed node.
     * @param arcLabel - the label from the parent that immediately points here.
     */
    public Node(Node<T> parent, T arcLabel, CostsEstimator<T> estimator) {
        this.parents = new ArrayList<>(1);
        this.parents.add(parent);
        this.arcLabel = arcLabel;
        this.nodeLevel = parent.nodeLevel + 1;
        this.status = Status.Open;
        parent.children.put(arcLabel, this);
        this.costs = estimator.getFormulasCosts(getPathLabels());
    }

    /**
     * A simple copy of the nodeLabel
     */
    public Node(Node<T> orig) {
        this.parents = null;
        this.arcLabel = null;
        this.nodeLabel = orig.nodeLabel;
        this.nodeLevel = orig.nodeLevel;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<T> getNodeLabel() {
        return nodeLabel;
    }

    public void setNodeLabel(Set<T> nodeLabel) {
        this.nodeLabel = nodeLabel;
    }

    public int getNodeLevel() {
        return nodeLevel;
    }

    public BigDecimal getCosts() {
        return costs;
    }

    public void setCosts(BigDecimal costs) {
        this.costs = costs;
    }

    /**
     * Adds a parent node to this node.
     *
     * @param parent node
     */
    public void addParent(Node<T> parent) {
        if (isRoot()) {
            throw new IllegalArgumentException("The root node cannot have parents.");
        } else {
            parents.add(parent);
        }
    }

    /**
     * Adds a child node to this node.
     *
     * @param child - the node that is to be a child.
     * @param arc   - the arc from this node to the child node.
     */
    public void addChild(Node<T> child, T arc) {
        this.children.put(arc, child);
        child.addParent(this);
    }


    /**
     * A simple string representation
     */
    public String toString() {
        return "NODE: nodeLabel: " + this.nodeLabel + "\nStatus?:" + this.status;
    }

    public String toString(Function<Set<T>, String> stringConverter) {
        return "NODE: nodeLabel: " + stringConverter.apply(this.nodeLabel) + "\nStatus?:" + this.status;
    }

    /**
     * Returns isRoot value
     *
     * @return true if this node is the root node, otherwise false.
     */
    public boolean isRoot() {
        return this.parents == null;
    }


    public List<Node<T>> getParents() {
        return this.parents;
    }

    /**
     * @return a set of labels on one of the paths from the current node to the root
     */
    public Set<T> getPathLabels() {
        if (pathLabels != null)
            return pathLabels;
        pathLabels = new HashSet<>(getNodeLevel());
        getPath(this, pathLabels);
        return pathLabels;
    }

    /**
     * Recursive method that computes a set of labels on the path from the given node to the root
     *
     * @param node for which a path has to be computed
     * @param path of labels to be computed
     */
    private void getPath(Node<T> node, Set<T> path) {
        List<Node<T>> parents = node.getParents();
        if (parents != null && !parents.isEmpty()) {
            getPath(parents.get(0), path);
            path.add(node.arcLabel);
        }
    }

    public Map<T, Node<T>> getChildren() {
        return children;
    }

    public enum Status {Open, Closed, Pruned, Diagnosis}
}
