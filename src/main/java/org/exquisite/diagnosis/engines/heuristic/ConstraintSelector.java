package org.exquisite.diagnosis.engines.heuristic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.exquisite.diagnosis.engines.HeuristicDiagnosisEngine;

import choco.kernel.model.constraints.Constraint;

/**
 * A super class to select the next node (constraint to expand)
 * Defaults to a depth first search
 * @author dietmar
 *
 */
public class ConstraintSelector  {

	
	public enum strategy {left2right, right2left, random};
	
	
	// Remember we shuffled things of the node once
	Map<ExtendedDAGNode,Boolean> alreadyShuffled = new HashMap<ExtendedDAGNode,Boolean>(); 
	
	/**
	 * Do we have more to do
	 * @param node the node
	 * @return true, if another node exists
	 */
	public boolean hasMoreConstraints(ExtendedDAGNode node) {
		return node.constraintsToExplore.size() > 0;
	}
	
	/**
	 * Returns the next node (conflict element) to explore
	 * @param node the current node
	 * @param engine the handle to the engine
	 * @return the constraint or null if no more constraint is there ...
	 */
	public Constraint getNextConstraint(ExtendedDAGNode node, HeuristicDiagnosisEngine engine) {
		// Default - DFS
		if (node.constraintsToExplore.size() > 0) {
			Constraint result = null;
			if (currentStrategy == ConstraintSelector.strategy.right2left) {
				result = node.constraintsToExplore.get(node.constraintsToExplore.size()-1);
			}
			else if (currentStrategy == ConstraintSelector.strategy.left2right) {
				result = node.constraintsToExplore.get(0); 
			}
			else if (currentStrategy == ConstraintSelector.strategy.random) {
				// take a random one
				Boolean shuffled = alreadyShuffled.get(node);
				if (shuffled == null || shuffled == false) {
					Collections.shuffle(node.constraintsToExplore);
					alreadyShuffled.put(node, true);
				}
				result = node.constraintsToExplore.get(0); 
			}
			node.constraintsToExplore.remove(result);
			return result;
		}
		else {
			return null;
		}
 
	}
	

	/**
	 * Default constructor
	 */
	public ConstraintSelector() {
	}
	
	/**
	 * A parameterized constructor
	 * @param currentStrategy
	 */
	public ConstraintSelector(strategy currentStrategy) {
		super();
		this.currentStrategy = currentStrategy;
	}


	// should we go from right to left or some other strategy
	// default.
	strategy currentStrategy = ConstraintSelector.strategy.left2right;  
	
}
