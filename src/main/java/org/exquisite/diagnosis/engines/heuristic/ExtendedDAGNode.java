package org.exquisite.diagnosis.engines.heuristic;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Example;

import choco.kernel.model.constraints.Constraint;

/**
 * Holds a bit more information about the current node to be expanded
 * @author dietmar
 *
 */
public class ExtendedDAGNode extends DAGNode {

	
	/**
	 * Create a new node and make a copy of the agenda
	 * @param conflict
	 */
	public ExtendedDAGNode(List<Constraint> conflict) {
		super(conflict);
		this.constraintsToExplore = new ArrayList<Constraint>(conflict);
	}
	
	/**
	 * Create a new node and make a copy of the agenda
	 * @param conflict
	 */
	public ExtendedDAGNode(ExtendedDAGNode dagnode) {
		super(dagnode);
		this.constraintsToExplore = new ArrayList<Constraint>(dagnode.conflict);
		this.examplesToCheck = new ArrayList<Example>(dagnode.examplesToCheck);
//		System.out.println("Examples to check: " + this.examplesToCheck.size());
	}
	
	// Remember the open list of constraints to explore
	public List<Constraint> constraintsToExplore;
	
	// Remember the tree level
	public int treeLevel = 0;
	
	

}
