package org.exquisite.diagnosis.engines.heuristic;

import org.exquisite.diagnosis.models.DAGNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a bit more information about the current node to be expanded
 *
 * @author dietmar
 */
public class ExtendedDAGNode<T> extends DAGNode<T> {


    // Remember the open list of constraints to explore
    public List<T> constraintsToExplore;
    // Remember the tree level
    public int treeLevel = 0;

    /**
     * Create a new node and make a copy of the agenda
     *
     * @param conflict
     */
    public ExtendedDAGNode(List<T> conflict) {
        super(conflict);
        this.constraintsToExplore = new ArrayList<T>(conflict);
    }

    /**
     * Create a new node and make a copy of the agenda
     *
     * @param conflict
     */
    public ExtendedDAGNode(ExtendedDAGNode<T> dagnode) {
        super(dagnode);
        this.constraintsToExplore = new ArrayList<>(dagnode.conflict);
        this.examplesToCheck = new ArrayList<>(dagnode.examplesToCheck);
//		System.out.println("Examples to check: " + this.examplesToCheck.size());
    }


}
