package tests.diagnosis;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.diagnosis.engines.common.NodeUtilities;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * For testing the various methods of the NodeUtilities class.
 * @author David
 * 
 * @see org.exquisite.diagnosis.engines.common.NodeUtilities
 *
 */
public class NodeUtilitiesTests {

	/**
	 * Switches on printing debug output to console window.<p>
	 * Starts running the tests.
	 * @param args
	 */
	public static void main(String[]args){
		Debug.DEBUGGING_ON = true;
		new NodeUtilitiesTests().run();
	}
	
	/**
	 * Runs all the tests.
	 */
	public void run(){
		applyNodeClosingRulesTest();
		applyPruningRulesTest();
		checkForExistingNodeTest();
		checkIsLastLevelTest();		
	}
	
	/**
	 * Testing refactored node closing rules.
	 */
	public void applyNodeClosingRulesTest()
	{
		Debug.msg("\napplyNodeClosingRulesTest");
		Debug.msg("---------------------");
		MockNodeData mockNodeData = MockNodeData.greinerExample();
		List<DAGNode<Constraint>> testGraph = mockNodeData.graph;
		DAGNode<Constraint> n6 = testGraph.get(6);
		List<DAGNode<Constraint>> diagnosisNodes = new ArrayList<>();
		diagnosisNodes.add(testGraph.get(3));
		diagnosisNodes.add(testGraph.get(5));
		Debug.msg("Node " + n6.nodeName + " should be closed after node closing rules are applied:");
		NodeUtilities.applyNodeClosingRules(n6, diagnosisNodes);
		Debug.msg(n6.nodeName + " closed = " + n6.closed);	
	}
	
	/**
	 * For testing DAG pruning method.
	 */
	public void applyPruningRulesTest(){		
		Debug.msg("\napplyPruningRulesTest");
		Debug.msg("---------------------");
		MockNodeData mockNodeData = MockNodeData.greinerExample();	
		
		//new conflict  { b } is a subset of root nodes set {a , b} so the root node should have its 
		//conflict set replaced and any subsequent redundant edges pruned.
		List<Constraint> newConflict = new ArrayList<Constraint>();
		newConflict.add(mockNodeData.constraints.get("b"));

		List<DAGNode<Constraint>> testGraph = mockNodeData.graph;
		Debug.msg("");
		Debug.msg("BEFORE PRUNE:");		
		NodeUtilities.traverseNode(testGraph.get(0), mockNodeData.constraints);
		NodeUtilities.applyPruningRules(newConflict, mockNodeData.knownConflicts, mockNodeData.conflictNodeLookup);
		Debug.msg("");
		Debug.msg("AFTER PRUNE:");
		NodeUtilities.traverseNode(testGraph.get(0), mockNodeData.constraints);
	}	
	
	/**
	 * For testing node reuse method.
	 */
	public void checkForExistingNodeTest()
	{		
		Debug.msg("\ncheckForExistingNodeTest");
		Debug.msg("---------------------");
		MockNodeData mockNodeData = MockNodeData.nodeReuseExample();
		List<DAGNode<Constraint>> testGraph = mockNodeData.graph;
		DAGNode<Constraint> n2 = testGraph.get(2);
		
		Debug.msg("Source graph to use:");
		NodeUtilities.traverseNode(testGraph.get(0), mockNodeData.constraints);
		
		Debug.msg("Checking for existing nodes to use as children for node: " + n2.nodeName);
		
		for (Constraint conflict : n2.conflict ){
			
			List<Constraint> newPathLabelSet = new ArrayList<Constraint>();
			newPathLabelSet.addAll(n2.pathLabels);
			newPathLabelSet.add(conflict);
			
			Iterator<Constraint> iterator = newPathLabelSet.iterator();
			System.out.print("new path label set [");
			while(iterator.hasNext()){
				System.out.print(Utilities.getKeyByValue(mockNodeData.constraints, iterator.next()) + " ");
			}
			System.out.println("]");

			DAGNode<Constraint> node = NodeUtilities.checkForExistingNode(newPathLabelSet, testGraph);
			String result = (node == null) ? "    No existing node found, will need to make a new one." : "    Node with name of " + node.nodeName + " can be reused.";
			Debug.msg(result);
		}	
	}
	
	/**
	 * Testing to check if a node contains all the possibly faulty statements on its path.	 
	 */
	public void checkIsLastLevelTest()
	{
		Debug.msg("\ncheckIsLastLevelTest");
		Debug.msg("---------------------");
		MockNodeData mockNodeData = MockNodeData.greinerExample();

		DAGNode<Constraint> testNode1 = new DAGNode<>(new ArrayList<>());
		testNode1.nodeName = "testNode1";
		testNode1.pathLabels = new ArrayList<>();
		testNode1.pathLabels.addAll(mockNodeData.constraints.values());
		testNode1.nodeLevel = testNode1.pathLabels.size();
		Debug.msg("Max path label size = " + testNode1.pathLabels.size());
		int maxSearchDepth = -1;
		
		Debug.msg("test #1:");
		Debug.msg("    max search depth = " + maxSearchDepth);
		Debug.msg("    test node path labels size: " + testNode1.pathLabels.size());
		boolean result = NodeUtilities
				.checkIsLastLevel(testNode1, new ArrayList<>(mockNodeData.constraints.values()), maxSearchDepth);
		Debug.msg("\n");
		
		testNode1.pathLabels.remove(mockNodeData.constraints.get("b"));		
		Debug.msg("test #2:");
		Debug.msg("    max search depth = " + maxSearchDepth);
		Debug.msg("    test node path labels size: " + testNode1.pathLabels.size());
		result = NodeUtilities
				.checkIsLastLevel(testNode1, new ArrayList<>(mockNodeData.constraints.values()), maxSearchDepth);
		Debug.msg("\n");
		
		maxSearchDepth = 2;
		Debug.msg("test #3:");
		Debug.msg("    max search depth = " + maxSearchDepth);
		Debug.msg("    test node path labels size: " + testNode1.pathLabels.size());
		result = NodeUtilities
				.checkIsLastLevel(testNode1, new ArrayList<>(mockNodeData.constraints.values()), maxSearchDepth);
		Debug.msg("\n");
		
		maxSearchDepth = 3;
		Debug.msg("test #4:");
		Debug.msg("    max search depth = " + maxSearchDepth);
		Debug.msg("    test node path labels size: " + testNode1.pathLabels.size());
		result = NodeUtilities
				.checkIsLastLevel(testNode1, new ArrayList<>(mockNodeData.constraints.values()), maxSearchDepth);
		Debug.msg("\n");
	
	}	
}
