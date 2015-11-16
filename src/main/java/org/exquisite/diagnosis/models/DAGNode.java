package org.exquisite.diagnosis.models;

import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.tools.Utilities;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * A class representing a dag node
 *
 * @author Dietmar
 */
public class DAGNode<T> {

    /**
     * Semaphore indicating if this node is the root node or not.
     */
    private final boolean isRoot;
    /**
     * Quick reference for finding children for this node.
     */
    public Dictionary<T, DAGNode<T>> children = new Hashtable<T, DAGNode<T>>();
    /**
     * The examples to be checked at a node
     */
    public List<Example<T>> examplesToCheck = new ArrayList<>();
    /**
     * The label of the arc leading here
     */
    public T arcLabel = null;
    /**
     * The conflict at the node
     */
    public List<T> conflict = null;
    /**
     * Used by parallel HSDAG builder
     */
    public List<T> reducedConflict = null;
    /**
     * Labels of the path to here
     */
    public List<T> pathLabels = new ArrayList<T>();
    /**
     * Is the node closed
     */
    public boolean closed = false;
    /**
     * Is the node pruned
     */
    public boolean pruned = false;
    /**
     * The tests.diagnosis at this node
     */
    public List<T> diagnosis = new ArrayList<T>();
    /**
     * The actual status for parallel processing (full parallelization only)
     */
    public nodeStatusParallel nodeStatusP = nodeStatusParallel.scheduled;
    /**
     * The tree level
     */
    public int nodeLevel = 0;

    /**
     * used for testing, an easy way to identify which node one is looking at.
     */
    public String nodeName;
    /**
     * The node/s that this node is a child of. (null if this node is the root node)
     * Changed this to a list as there may be cases where a node could be a child of multiple parent nodes.
     */
    private List<DAGNode<T>> parents = new ArrayList<DAGNode<T>>();

    /**
     * Constructor for the root node.
     *
     * @param conflict - the initial conflict set returned from call to qx...
     */
    public DAGNode(List<T> conflict) {
        this.parents = null;
        this.arcLabel = null;
        this.conflict = conflict;
        this.isRoot = true;
        this.nodeLevel = 0;
        this.nodeName = "root";
    }

    /**
     * Constructor for child nodes
     *
     * @param parent   - the parent for the newly constructed node.
     * @param arcLabel - the label from the parent that immediately points here.
     */
    public DAGNode(DAGNode<T> parent, T arcLabel) {
        this.parents.add(parent);
        this.arcLabel = arcLabel;
        this.isRoot = false;
        this.nodeLevel = parent.nodeLevel + 1;
        parent.children.put(arcLabel, this);
    }


    /**
     * A simple copy of the conflict
     */
    public DAGNode(DAGNode<T> orig) {
        this.parents = null;
        this.arcLabel = null;
        this.conflict = conflict;
        this.isRoot = orig.isRoot;
        this.nodeLevel = orig.nodeLevel;
        this.nodeName = orig.nodeName;
    }

    /**
     * Adds a parent node to this node.
     *
     * @param parent
     * @throws Exception
     */
    public void addParent(DAGNode<T> parent) throws Exception {
        if (this.isRoot) {
            throw new Exception("The root node cannot have parents.");
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
    public void addChild(DAGNode<T> child, T arc) {
        this.children.put(arc, child);
        try {
            child.addParent(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * A simple string representation
     */
    public String toString() {
        String result = "";
        String conflict = "";
        if (this.conflict != null) {
            conflict = "" + Utilities.printConstraintList(this.conflict, AbstractHSDagBuilder.globalDiagnosisModel);
        } else {
            conflict = "no conflict?";
        }
        result = "DAGNODE: conflict: " + conflict + "\nClosed?:" + this.closed + "\nParallelStatus: " + this.nodeStatusP;

        return result;


    }


    /**
     * Returns isRoot value
     *
     * @return true if this node is the root node, otherwise false.
     */
    public boolean getIsRoot() {
        return this.isRoot;
    }


    public List<DAGNode<T>> getParents() {
        return this.parents;
    }

    /**
     * Remember an additional node status for parallel processing
     * Defining a type
     */
    public enum nodeStatusParallel {
        finished, active, scheduled, cancelled
    }
}
