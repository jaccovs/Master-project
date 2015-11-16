package org.exquisite.diagnosis.engines.prdfs;

import org.exquisite.diagnosis.engines.ParallelRandomDFSEngine;
import org.exquisite.diagnosis.engines.heuristic.ExtendedDAGNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NodeSelector<T> {

    ParallelRandomDFSEngine<T> engine;

    List<List<NodeWithConstraint<T>>> nodesToExpand = new ArrayList<>();

    int count = 0;
    int maxLevel = -1;

    Random random = new Random();

    public NodeSelector(ParallelRandomDFSEngine<T> engine) {
        this.engine = engine;
    }

    public synchronized NodeWithConstraint<T> getNextNode() {
        if (hasMoreNodes()) {
//			System.out.println("NodesToExpand: Count: " + count + ", Levels: " + nodesToExpand.size() + ", maxLevel: " + maxLevel + ", Count of maxLevel: " + nodesToExpand.get(maxLevel).size());
            List<NodeWithConstraint<T>> nodesOfLevel = nodesToExpand.get(maxLevel);
            int rnd = random.nextInt(nodesOfLevel.size());
            NodeWithConstraint<T> node = nodesOfLevel.remove(rnd);
            count--;
            while (maxLevel >= 0 && nodesToExpand.get(maxLevel).size() == 0) {
                maxLevel--;
            }
            return node;
        }
        return null;
    }

    public boolean hasMoreNodes() {
        return count > 0;
    }

    public synchronized void addNodeWithConstraints(ExtendedDAGNode<T> node) {
//		System.out.println("Adding " + node.constraintsToExplore.size());
        for (T c : node.constraintsToExplore) {
            addNodeWithConstraint(new NodeWithConstraint<T>(node, c));
//			System.out.println("Size: " + count);
        }
        this.notifyAll();
    }

    private void addNodeWithConstraint(NodeWithConstraint<T> node) {
        int level = node.node.nodeLevel;

//		System.out.println("Node level: " + level);

        if (nodesToExpand.size() <= level) {
            nodesToExpand.add(new ArrayList<NodeWithConstraint<T>>());
        }
        List<NodeWithConstraint<T>> nodesOfLevel = nodesToExpand.get(level);
        if (level > maxLevel) {
            maxLevel = level;
        }
        nodesOfLevel.add(node);

        count++;
    }
}
