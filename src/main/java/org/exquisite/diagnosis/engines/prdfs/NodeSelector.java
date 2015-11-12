package org.exquisite.diagnosis.engines.prdfs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.exquisite.diagnosis.engines.ParallelRandomDFSEngine;
import org.exquisite.diagnosis.engines.heuristic.ExtendedDAGNode;

import choco.kernel.model.constraints.Constraint;

public class NodeSelector {
	
	ParallelRandomDFSEngine engine;
	
	List<List<NodeWithConstraint>> nodesToExpand = new ArrayList<List<NodeWithConstraint>>();
	
	int count = 0;
	int maxLevel = -1;
	
	Random random = new Random();
	
	public NodeSelector(ParallelRandomDFSEngine engine) {
		this.engine = engine;
	}
	
	public synchronized NodeWithConstraint getNextNode() {
		if (hasMoreNodes()) {
//			System.out.println("NodesToExpand: Count: " + count + ", Levels: " + nodesToExpand.size() + ", maxLevel: " + maxLevel + ", Count of maxLevel: " + nodesToExpand.get(maxLevel).size());
			List<NodeWithConstraint> nodesOfLevel = nodesToExpand.get(maxLevel);
			int rnd = random.nextInt(nodesOfLevel.size());
			NodeWithConstraint node = nodesOfLevel.remove(rnd);
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
	
	public synchronized void addNodeWithConstraints(ExtendedDAGNode node) {
//		System.out.println("Adding " + node.constraintsToExplore.size());
		for (Constraint c: node.constraintsToExplore) {
			addNodeWithConstraint(new NodeWithConstraint(node, c));
//			System.out.println("Size: " + count);
		}
		this.notifyAll();
	}
	
	private void addNodeWithConstraint(NodeWithConstraint node) {
		int level = node.node.nodeLevel;
		
//		System.out.println("Node level: " + level);
		
		if (nodesToExpand.size() <= level) {
			nodesToExpand.add(new ArrayList<NodeWithConstraint>());
		}
		List<NodeWithConstraint> nodesOfLevel = nodesToExpand.get(level);
		if (level > maxLevel) {
			maxLevel = level;
		}
		nodesOfLevel.add(node);
		
		count++;
	}
}
