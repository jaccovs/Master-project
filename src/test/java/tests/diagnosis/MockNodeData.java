package tests.diagnosis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.exquisite.diagnosis.engines.common.ConstraintComparator;
import org.exquisite.diagnosis.models.DAGNode;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Reflects the unpruned DAG structure from the Greiner paper - used for testing revised pruning and node reuse methods.
 * @author David
 *
 */
public class MockNodeData {
		
	public Hashtable<String, Constraint> constraints = new Hashtable<String, Constraint>();
	public List<DAGNode> graph = new ArrayList<DAGNode>();
	public List<List<Constraint>> knownConflicts = new ArrayList<List<Constraint>>();
	public Hashtable<List<Constraint>, List<DAGNode>> conflictNodeLookup = new Hashtable<List<Constraint>, List<DAGNode>>();
	public DAGNode rootNode;
	
	/**
	 * @return a graph the same as used in Greiner's paper.
	 */
	public static MockNodeData greinerExample(){
		MockNodeData instance = new MockNodeData();
		instance.makeGreinerExample();
		return instance;
	}
	
	/**
	 * @return a small graph with a pair of nodes whose paths contain identical labels.
	 */
	public static MockNodeData duplicatePathsExample(){
		MockNodeData instance = new MockNodeData();
		instance.makeDuplicatePathExample();
		return instance;
	}
	
	public static MockNodeData nodeReuseExample(){
		MockNodeData instance = new MockNodeData();
		instance.makeReuseExample();
		return instance;
	}
	
	private void makeReuseExample(){
		IntegerVariable testVar = Choco.makeIntVar("testVar");
		Constraint a = Choco.eq(testVar, 1);
		Constraint b = Choco.eq(testVar, 2);
		Constraint c = Choco.eq(testVar, 3);
		Constraint d = Choco.eq(testVar, 4);
		
		constraints.put("a", a);
		constraints.put("b", b);
		constraints.put("c", c);
		constraints.put("d", d);
		
		Constraint[] rootArray = {a,b};
		DAGNode root = makeRoot(rootArray);
		
		Constraint[] n1Conflicts = {b, c};
		DAGNode n1 = makeNode("n1", n1Conflicts, root, a);	
		
		Constraint[] n2Conflicts = {a, c};
		DAGNode n2 = makeNode("n2", n2Conflicts, root, b);		
		
		Constraint[] n3Conflicts = {};
		DAGNode n3 = makeNode("n3", n3Conflicts, n1, b);	
				
		Constraint[] n4Conflicts = {b, d};
		DAGNode n4 = makeNode("n4", n4Conflicts, n1, c);	
	}
	
	/**
	 * Emulates unpruned graph example from pg.85 of 
	 * http://cs.ru.nl/~peterl/teaching/KeR/Theorist/greibers-correctiontoreiter.pdf
	 */
	private void makeGreinerExample(){
		IntegerVariable testVar = Choco.makeIntVar("testVar");
		Constraint a = Choco.eq(testVar, 1);
		Constraint b = Choco.eq(testVar, 2);
		Constraint c = Choco.eq(testVar, 3);
		Constraint d = Choco.eq(testVar, 4);
		
		constraints.put("a", a);
		constraints.put("b", b);
		constraints.put("c", c);
		constraints.put("d", d);
						
		Constraint[] rootArray = {a,b};
		DAGNode root = makeRoot(rootArray);
		
		Constraint[] n1Conflicts = {b, c};
		DAGNode n1 = makeNode("n1", n1Conflicts, root, a);	
		
		Constraint[] n2Conflicts = {a, c};
		DAGNode n2 = makeNode("n2", n2Conflicts, root, b);		
		
		Constraint[] n3Conflicts = {};
		DAGNode n3 = makeNode("n3", n3Conflicts, n1, b);	
		n2.addChild(n3, a);
		
		Constraint[] n4Conflicts = {b, d};
		DAGNode n4 = makeNode("n4", n4Conflicts, n1, c);	
				
		Constraint[] n5Conflicts = {};
		DAGNode n5 = makeNode("n5", n5Conflicts, n2, c);	
				
		Constraint[] n6Conflicts = {};
		DAGNode n6 = makeNode("n6", n6Conflicts, n4, b);
								
		Constraint[] n7Conflicts = {b};
		DAGNode n7 = makeNode("n7", n7Conflicts, n4, d);	
	}
	
	/**
	 * For testing parallel dag pre-check for duplicate node paths.
	 */
	private void makeDuplicatePathExample(){
		IntegerVariable testVar = Choco.makeIntVar("testVar");
		Constraint a = Choco.eq(testVar, 1);
		Constraint b = Choco.eq(testVar, 2);
		Constraint c = Choco.eq(testVar, 3);
		Constraint d = Choco.eq(testVar, 4);
		Constraint e = Choco.eq(testVar, 4);
		Constraint f = Choco.eq(testVar, 4);
		
		constraints.put("a", a);
		constraints.put("b", b);
		constraints.put("c", c);
		constraints.put("d", d);
		constraints.put("e", e);
		constraints.put("f", f);
						
		//level 0
		Constraint[] rootArray = {a,b,c};
		DAGNode root = makeRoot(rootArray);
		
		//level 1
		Constraint[] n1Conflicts = {b, e};
		DAGNode n1 = makeNode("n1", n1Conflicts, root, a);		
		
		Constraint[] n2Conflicts = {a, f};
		DAGNode n2 = makeNode("n2", n2Conflicts, root, b);
		
		Constraint[] n3Conflicts = {d, e};
		DAGNode n3 = makeNode("n3", n3Conflicts, root, c);		
	}
	
	/**
	 * Makes a DAGNode and configures that node as a root node.
	 * @param conflict the conflict set for this root node
	 * @return DAGNode set as root.
	 */
	private DAGNode makeRoot(Constraint[] conflict){
		DAGNode root = new DAGNode(new ArrayList<Constraint>(Arrays.asList(conflict)));
		root.nodeName = "root";
		graph.add(root);
		knownConflicts.add(root.conflict);
		List<DAGNode> nodes = new ArrayList<DAGNode>();
		nodes.add(root);
		conflictNodeLookup.put(root.conflict, nodes);	
		rootNode = root;
		return root;
	}
	
	/**
	 * Makes a (non-root) DAGNode
	 * @param name    A name for the node (can be useful for printing, debugging).
	 * @param conflict    The conflict set for this particular node. 
	 * @param parent    The parent of this node.
	 * @param edge    The path label from the parent that points to this node.
	 * @return A DAGNode that is the child of another node.
	 */
	private DAGNode makeNode(String name, Constraint[] conflict, DAGNode parent, Constraint edge){
		DAGNode node = new DAGNode(parent, edge);
		node.conflict = new ArrayList<Constraint>(Arrays.asList(conflict));
		//System.out.println("Making node " + name + " with conflict size of: " + node.conflict.size());
		node.nodeName = name;
		Set<Constraint> set = new TreeSet<Constraint>(new ConstraintComparator());
		set.addAll(parent.pathLabels);
		node.pathLabels.addAll(set);
		node.pathLabels.add(edge);
		graph.add(node);
		if (!node.conflict.isEmpty()){
			knownConflicts.add(node.conflict);
			
			List<DAGNode> nodes = conflictNodeLookup.get(node.conflict);
			if (nodes == null) {
				nodes = new ArrayList<DAGNode>();
			}
			nodes.add(node);
			conflictNodeLookup.put(node.conflict, nodes);
		}
		return node;
	}
}
