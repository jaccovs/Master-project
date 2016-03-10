package tests.parallelsearch;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.core.engines.tree.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	 * Create a root node with some artificial nodeLabel.
	 * Expand the root node until max search depth is reached.
	 */
	public void run(){
		//create root node with an arbitrary nodeLabel from Mock qx call.
		MockQxSearch mockQx = new MockQxSearch();
		List<Constraint> rootConflict = mockQx.getRootConflict();
		Node<Constraint> root = new Node<Constraint>(rootConflict);
		root.nodeLevel = 0;
		root.nodeName = "root";
		
		//add root node to list of nodes to expand...
		List<Node<Constraint>> nodesToExpand = new ArrayList<Node<Constraint>>();
		nodesToExpand.add(root);		
		currentLevelEdgeCount = root.nodeLabel.size();
		
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
	public void expandNodes(List<Node<Constraint>> nodesToExpand) {
		System.out.println("START NODE EXPANSION...");
		//pool of available threads for node expansion workers
		ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREAD_POOL_SIZE);
		while (!nodesToExpand.isEmpty()){			
			//a barrier to enforce only nodes of a given level are expanded.
			CountDownLatch barrier = new CountDownLatch(currentLevelEdgeCount);
			//list caching each node that is expanded.
			List<Node<Constraint>> expandedNodes = new ArrayList<Node<Constraint>>();
			//do bfs...
			while (!nodesToExpand.isEmpty()){
				//remove node from queue
				Node<Constraint> node = nodesToExpand.remove(0);
				//expand the node
				if (node.nodeLevel < MAX_SEARCH_DEPTH){
					//threadPool.execute(new ExpansionWorker(node, barrier));				
					for (Constraint label : node.nodeLabel){
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

	private int calculateEdgeLevelCount(List<Node<Constraint>> nodes) {
		int result = 0;

		for (Node<Constraint> node : nodes) {
			result += node.nodeLabel.size();
		}
		
		return result;
	}
	
	/**
	 * Pushes newly generated child nodes onto the list of nodes to expand.
	 * @param visitedNodes - nodes which have been expanded and may have new child nodes.
	 * @param nodesToExpand - the shared list where nodes that are to be expanded are queued.
	 */
	private void queueNonVisitedNodes(List<Node<Constraint>> visitedNodes, List<Node<Constraint>> nodesToExpand) {
		for (Node<Constraint> node : visitedNodes) {
			if (!node.children.isEmpty()){
				Enumeration<Node<Constraint>> enumeration = node.children.elements();
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
	private void traverseNode(Node<Constraint> targetNode)
	{
		final String indent = "    ";
		String nodeMessage = "";
		
		for(int i=0; i<targetNode.nodeLevel; i++)
		{
			nodeMessage+=indent;			
		}
		
		nodeMessage+=targetNode.nodeName;
		System.out.println(nodeMessage);
		if (targetNode.nodeLabel != null)
		{
			for(Constraint c : targetNode.nodeLabel)
			{
				Node<Constraint> child = targetNode.children.get(c);
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
	Node<Constraint> parent;
	Constraint edgeLabel;
	CountDownLatch cdl;

	public EdgeWorker(Node<Constraint> parent, Constraint edgeLabel, CountDownLatch cdl) {
		this.parent = parent;
		this.edgeLabel = edgeLabel;
		this.cdl = cdl;
	}
	
	public void run(){
		List<Constraint> constraintsToIgnore = new ArrayList<Constraint>(parent.pathLabels);
		constraintsToIgnore.add(edgeLabel);
		List<Constraint> conflict = search.findConflict(constraintsToIgnore);			
		System.out.println("nodeLabel size = " + conflict.size());
		Node<Constraint> childNode = new Node<Constraint>(parent, edgeLabel);
		childNode.nodeLabel = conflict;
		parent.children.put(edgeLabel, childNode);
		childNode.nodeLevel = parent.nodeLevel + 1;
		childNode.nodeName = "n" + childNode.nodeLevel + "_" +  cdl.getCount();
		cdl.countDown();
		System.out.println("new child node " + childNode.nodeName + " instantiated.");
	}
}

/**
 * Expands a target node by creating a new child node for each label of the targe nodes
 * nodeLabel set.
 * 
 * @author David 
 */
class ExpansionWorker extends Thread{
	
	MockQxSearch search = new MockQxSearch();
	Node<Constraint> nodeToExpand;
	int nodeNamePostfix = 0;
	CountDownLatch cdl;

	public ExpansionWorker(Node<Constraint> node, CountDownLatch cdl) {
		this.nodeToExpand = node;
		this.cdl = cdl;				
	}
	
	@Override
	public void run(){		
		System.out.println("    Expanding node: " + nodeToExpand.nodeName);
		for (Constraint label : nodeToExpand.nodeLabel){
			List<Constraint> constraintsToIgnore = new ArrayList<Constraint>(nodeToExpand.pathLabels);
			constraintsToIgnore.add(label);
			List<Constraint> conflict = search.findConflict(constraintsToIgnore);

			Node<Constraint> childNode = new Node<Constraint>(nodeToExpand, label);
			
			childNode.nodeLabel = conflict;
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
 * Mocking a solver/qx search... generates fake nodeLabel set...
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
	 * Make an arbitrary nodeLabel set for root node.
	 * @return
	 */
	public List<Constraint> getRootConflict(){
		List<Constraint> result = new ArrayList<Constraint>();
		result.add(constraints.get(0));
		result.add(constraints.get(3));
		result.add(constraints.get(6));
		return result;		
	}
	
	//just returns two constraints from collection to simulate a nodeLabel set.
	public List<Constraint> findConflict(List<Constraint> pathLabels){
		//get a copy of the constraint list
		List<Constraint> copy = new ArrayList<Constraint>(constraints);
		//remove constraints in list that are in the nodes path
		copy.removeAll(pathLabels);
		
		//return 2 randomly selected constraints from remaining list to simulate nodeLabel set.
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

