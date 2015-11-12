package tests.parallelsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.exquisite.diagnosis.models.DAGNode;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Example implementation of Level Synchronous Breadth-First Search.
 * Shared array queue
 * int currentDepth
 * graph[0] += rootNode
 * currentDepth = 0
 * parallel{
 * 		while not empty(queue[currentDepth]) do{
 * 			levelToSearch = graph[currentDepth].get()
 * 			make any child nodes...
 * 			queueNonVisited(childNodes, graph[currentDepth + 1])		
 * 		}
 * 		wait until all nodes at his level have been visited...
 * 		currentDepth++
 * } 
 */
public class LevelSyncBFS {
	
	//maximum number of threads to initialize the threadpool with.
	final int MAX_THREAD_POOL_SIZE = 3;
	//how deep to search...
	final int MAX_SEARCH_DEPTH = 3;	
	
	private int currentLevelEdgeCount = 0;
	
	public static void main(String[]args){		
		new LevelSyncBFS().run();		
	}	
	
	/**
	 * Run the demo...
	 * 
	 * Create a root node with some artificial conflict.
	 * Expand the root node until max search depth is reached.
	 */
	public void run(){
		//create root node with an arbitrary conflict from Mock qx call.
		MockQxSearch mockQx = new MockQxSearch();
		List<Constraint> rootConflict = (List<Constraint>)mockQx.getRootConflict();
		DAGNode root = new DAGNode(rootConflict);
		root.nodeLevel = 0;
		root.nodeName = "root";
		
		//add root node to list of nodes to expand...
		List<DAGNode>nodesToExpand = new ArrayList<DAGNode>();
		nodesToExpand.add(root);		
		currentLevelEdgeCount = root.conflict.size();
		
		//begin node expansion				
		this.expandNodes(nodesToExpand);
		
		//traverse graph after nodes have been generated.
		System.out.println("\n\nResulting graph:");
		traverseNode(root);
	}
	
	/**
	 * Expands each node at a given depth of the graph in a separate thread. Making use
	 * of ExecutorService to manage thread reuse.
	 * Uses CountDownLatch to block the program continuing until all nodes for a given level
	 * have been expanded.
	 */	
	public void expandNodes(List<DAGNode>nodesToExpand){
		System.out.println("START NODE EXPANSION...");
		//pool of available threads for node expansion workers
		ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREAD_POOL_SIZE);
		while (!nodesToExpand.isEmpty()){			
			//a barrier to enforce only nodes of a given level are expanded.
			CountDownLatch barrier = new CountDownLatch(currentLevelEdgeCount);
			//list caching each node that is expanded.
			List<DAGNode> expandedNodes = new ArrayList<DAGNode>();
			//do bfs...
			while (!nodesToExpand.isEmpty()){
				//remove node from queue
				DAGNode node = nodesToExpand.remove(0);
				//expand the node
				if (node.nodeLevel < MAX_SEARCH_DEPTH){
					//threadPool.execute(new ExpansionWorker(node, barrier));				
					for (Constraint label : node.conflict){		
						threadPool.execute(new EdgeWorker(node, label, barrier));
					}
					expandedNodes.add(node);
				}else{
					//if node was not expanded, still need to countdown.
					System.out.println("node " + node.nodeName + " was not expanded.");
					barrier.countDown();
				}				
			}					
			try{
				System.out.println("==== Waiting at barrier for threads to at this level finish. ====");
				//wait for node expansion workers to finish
				barrier.await();
				//add any new nodes to list of nodes to expand.
				queueNonVisitedNodes(expandedNodes, nodesToExpand);
				this.currentLevelEdgeCount = calculateEdgeLevelCount(expandedNodes);
			} catch (InterruptedException e){
				e.printStackTrace();
				break;
			}
		}
		threadPool.shutdown();	
		System.out.println("END OF NODE EXPANSION.");
	}
	
	private int calculateEdgeLevelCount(List<DAGNode> nodes){
		int result = 0; 
		
		for (DAGNode node : nodes){
			result += node.conflict.size();
		}
		
		return result;
	}
	
	/**
	 * Pushes newly generated child nodes onto the list of nodes to expand.
	 * @param visitedNodes - nodes which have been expanded and may have new child nodes.
	 * @param nodesToExpand - the shared list where nodes that are to be expanded are queued.
	 */
	private void queueNonVisitedNodes(List<DAGNode>visitedNodes, List<DAGNode>nodesToExpand){
		for (DAGNode node : visitedNodes){
			if (!node.children.isEmpty()){
				Enumeration<DAGNode> enumeration = node.children.elements();
				while(enumeration.hasMoreElements()){
					nodesToExpand.add(enumeration.nextElement());
				}
			}
		}
	}
	
	/**
	 * just a util for printing the resulting node graph to console window.
	 * @param targetNode
	 */
	private void traverseNode(DAGNode targetNode)
	{
		final String indent = "    ";
		String nodeMessage = "";
		
		for(int i=0; i<targetNode.nodeLevel; i++)
		{
			nodeMessage+=indent;			
		}
		
		nodeMessage+=targetNode.nodeName;
		System.out.println(nodeMessage);
		if (targetNode.conflict != null)
		{
			for(Constraint c : targetNode.conflict)
			{
				DAGNode child = targetNode.children.get(c);
				if(child!=null)
				{
					traverseNode(child);
				}
			}
		}
	}
}


class EdgeWorker extends Thread{
	MockQxSearch search = new MockQxSearch();
	DAGNode parent;
	Constraint edgeLabel;
	CountDownLatch cdl;
	
	public EdgeWorker(DAGNode parent, Constraint edgeLabel, CountDownLatch cdl){
		this.parent = parent;
		this.edgeLabel = edgeLabel;
		this.cdl = cdl;
	}
	
	public void run(){
		List<Constraint> constraintsToIgnore = new ArrayList<Constraint>(parent.pathLabels);
		constraintsToIgnore.add(edgeLabel);
		List<Constraint> conflict = search.findConflict(constraintsToIgnore);			
		System.out.println("conflict size = " + conflict.size());
		DAGNode childNode = new DAGNode(parent, edgeLabel);
		childNode.conflict = conflict;			
		parent.children.put(edgeLabel, childNode);
		childNode.nodeLevel = parent.nodeLevel + 1;
		childNode.nodeName = "n" + childNode.nodeLevel + "_" +  cdl.getCount();
		cdl.countDown();
		System.out.println("new child node " + childNode.nodeName + " instantiated.");
	}
}

/**
 * Expands a target node by creating a new child node for each label of the targe nodes
 * conflict set.
 * 
 * @author David 
 */
class ExpansionWorker extends Thread{
	
	MockQxSearch search = new MockQxSearch();
	DAGNode nodeToExpand;
	int nodeNamePostfix = 0;
	CountDownLatch cdl;
		
	public ExpansionWorker(DAGNode node, CountDownLatch cdl){
		this.nodeToExpand = node;
		this.cdl = cdl;				
	}
	
	@Override
	public void run(){		
		System.out.println("    Expanding node: " + nodeToExpand.nodeName);
		for (Constraint label : nodeToExpand.conflict){				
			List<Constraint> constraintsToIgnore = new ArrayList<Constraint>(nodeToExpand.pathLabels);
			constraintsToIgnore.add(label);
			List<Constraint> conflict = search.findConflict(constraintsToIgnore);			
			
			DAGNode childNode = new DAGNode(nodeToExpand, label);
			
			childNode.conflict = conflict;			
			nodeToExpand.children.put(label, childNode);
			childNode.nodeLevel = nodeToExpand.nodeLevel + 1;
			childNode.nodeName = "n" + childNode.nodeLevel + "_" +  nodeNamePostfix;
			nodeNamePostfix++;				
			
			String indent = "    ";
			String debugMessage = "";
			for(int i=0; i<childNode.nodeLevel; i++){
				debugMessage += indent;
			}
			debugMessage += "Child node " + childNode.nodeName + " created for parent " + nodeToExpand.nodeName;
			System.out.println(debugMessage) ;		
		}	
		cdl.countDown();
	}	
}



/**
 * Mocking a solver/qx search... generates fake conflict set...
 * @author David
 */
class MockQxSearch{	
	List<Constraint> constraints = new ArrayList<Constraint>(); 	
	
	public MockQxSearch(){
		IntegerVariable testVar = Choco.makeIntVar("testVar");
		Constraint a = Choco.eq(testVar, 0);
		Constraint b = Choco.eq(testVar, 1);
		Constraint c = Choco.eq(testVar, 2);
		Constraint d = Choco.eq(testVar, 3);
		Constraint e = Choco.eq(testVar, 4);
		Constraint f = Choco.eq(testVar, 5);
		Constraint g = Choco.eq(testVar, 6);
		Constraint h = Choco.eq(testVar, 7);
		Constraint i = Choco.eq(testVar, 8);
		Constraint j = Choco.eq(testVar, 9);			
		constraints.add(a);
		constraints.add(b);
		constraints.add(c);
		constraints.add(d);
		constraints.add(e);
		constraints.add(f);
		constraints.add(g);
		constraints.add(h);
		constraints.add(i);
		constraints.add(j);
	}
	
	/**
	 * Make an arbitrary conflict set for root node.
	 * @return
	 */
	public List<Constraint> getRootConflict(){
		List<Constraint> result = new ArrayList<Constraint>();
		result.add(constraints.get(0));
		result.add(constraints.get(3));
		result.add(constraints.get(6));
		return result;		
	}
	
	//just returns two constraints from collection to simulate a conflict set.
	public List<Constraint> findConflict(List<Constraint> pathLabels){
		//get a copy of the constraint list
		List<Constraint> copy = new ArrayList<Constraint>(constraints);
		//remove constraints in list that are in the nodes path
		copy.removeAll(pathLabels);
		
		//return 2 randomly selected constraints from remaining list to simulate conflict set.
		Collections.shuffle(copy);		
		List<Constraint> result = new ArrayList<Constraint>();
		result.add(copy.get(0));
		result.add(copy.get(1));
		
		//delay for a random amount of time...
		try {
			long sleepTime = (int) (Math.random() * 1000);
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return result;
	}
}

