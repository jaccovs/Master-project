package org.exquisite.diagnosis.engines.heuristic;

import org.exquisite.diagnosis.engines.HeuristicDiagnosisEngine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A super class to select the next node (constraint to expand)
 * Defaults to a depth first search
 *
 * @author dietmar
 */
public class FormulaSelector<T> {


    // Remember we shuffled things of the node once
    Map<ExtendedDAGNode<T>, Boolean> alreadyShuffled = new HashMap<>();

    // should we go from right to left or some other strategy
    // default.
    strategy currentStrategy = FormulaSelector.strategy.left2right;

    /**
     * Default constructor
     */
    public FormulaSelector() {
    }

    /**
     * A parameterized constructor
     *
     * @param currentStrategy
     */
    public FormulaSelector(strategy currentStrategy) {
        super();
        this.currentStrategy = currentStrategy;
    }

    /**
     * Do we have more to do
     *
     * @param node the node
     * @return true, if another node exists
     */
    public boolean hasMoreConstraints(ExtendedDAGNode<T> node) {
        return node.constraintsToExplore.size() > 0;
    }

    /**
     * Returns the next node (nodeLabel element) to explore
     *
     * @param node   the current node
     * @param engine the handle to the engine
     * @return the constraint or null if no more constraint is there ...
     */
    public T getNextConstraint(ExtendedDAGNode<T> node, HeuristicDiagnosisEngine<T> engine) {
        // Default - DFS
        if (node.constraintsToExplore.size() > 0) {
            T result = null;
            if (currentStrategy == FormulaSelector.strategy.right2left) {
                result = node.constraintsToExplore.get(node.constraintsToExplore.size() - 1);
            } else if (currentStrategy == FormulaSelector.strategy.left2right) {
                result = node.constraintsToExplore.get(0);
            } else if (currentStrategy == FormulaSelector.strategy.random) {
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
        } else {
            return null;
        }

    }


    public enum strategy {left2right, right2left, random}

}
