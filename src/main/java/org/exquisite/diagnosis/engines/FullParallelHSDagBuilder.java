package org.exquisite.diagnosis.engines;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.common.EdgeWorker;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.engines.common.SharedCollection;
import org.exquisite.diagnosis.engines.common.SharedDAGNodeQueue;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.tools.Debug;

import choco.kernel.model.constraints.Constraint;

/**
 * Implements a fully parallelized hs dag algorithm
 * @author dietmar
 *
 */
public class FullParallelHSDagBuilder extends ParallelHSDagBuilder {

	// Need a global thread pool
	ExecutorService threadPool;
	
	public static int runningThreads = 0;
	public static Object runningThreadsSync = new Object();
	
	// Some synchronization stuff
	// How many jobs are open
	private int jobsToDo = 1;

	// A dummy NO_OP to safely read the open jobs counter
	static int NO_OP = -10;
	
	// avoid conflicts here
	// could also be a decrease
	public synchronized int incrementJobsToDo(int i) {
		if (i == NO_OP) {
			return jobsToDo;
		}
		else {
			jobsToDo = jobsToDo + i;
//			System.out.println("INCREMENT: Remaining jobs to do: " + jobsToDo);
		}
		return i;
	}
	
	
	/*
	 * Create a new full HSDagBuilder
	 */
	public FullParallelHSDagBuilder(ExquisiteSession sessionData, int threadPoolSize){
		super(sessionData, threadPoolSize);
		this.maxThreadPoolSize = threadPoolSize;
		jobsToDo = 1;
	}	

	/**
	 * Have to do things slightly different here
	 */
	@Override
	public List<Diagnosis> calculateDiagnoses() throws DomainSizeException {
		this.threadPool = Executors.newFixedThreadPool(maxThreadPoolSize);
		this.diagnoses.clear();
		this.diagnosisNodes.clear();
		
		if (rootNode == null) {
			Debug.msg("Empty root node - doing inital test");
			try{				
				QuickXPlain qx = NodeExpander.createQX(this.sessionData, this);
				ConflictCheckingResult checkingResult;
				if (!QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {
					
					checkingResult = qx.checkExamples(model.getPositiveExamples(), new ArrayList<Constraint>(), true);
				}
				else {
					synchronized(QuickXPlain.ContinuingSync) {
						checkingResult = new ConflictCheckingResult();
//						Debug.syncMsg("Start first checkExamplesParallel()");
						qx.checkExamplesParallel(model.getPositiveExamples(), new ArrayList<Constraint>(), true, checkingResult, knownConflicts);
//						Debug.syncMsg("Main thread sleeping.");
						try {
							QuickXPlain.ContinuingSync.wait();
						} catch (InterruptedException e) {
							return diagnoses;
						}
//						Debug.syncMsg("Main thread woke up.");
					}
				}
								
				if (checkingResult != null){
					if (checkingResult.conflictFound()) {
						List<Constraint> tempSet = new ArrayList<Constraint>();
						tempSet.addAll(checkingResult.conflicts.get(0));
						incrementConstructedNodes();
						rootNode = new DAGNode(tempSet);
						if (!QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {
							rootNode.examplesToCheck = new ArrayList<Example>(checkingResult.failedExamples);
							// DJ: Do not add the root node; it will be added on expansion.
	//						allConstructedNodes.addItem(rootNode);
	//						knownConflicts.add(rootNode.conflict);
							// DJ: we could actually add all conflicts we have to this list
							synchronized (checkingResult.conflicts.getWriteLock()) {
								for (List<Constraint> c : checkingResult.conflicts.getCollection()) {
		//							System.out.println("Adding a known conflit");
									knownConflicts.addItemListNoDups(c);
								}								
							}
						}
						else {
							rootNode.examplesToCheck = model.getPositiveExamples();
						}
						
						// Create a special shared list for this type of expansion
						SharedCollection<DAGNode>nodesToExpand = new SharedDAGNodeQueue<DAGNode>();
						nodesToExpand.add(rootNode);						
						this.currentLevelEdgeCount = rootNode.conflict.size();
						this.currentLevel = ROOT_LEVEL;
//						System.out.println("Starting node expansion... ");
						expandNodes(nodesToExpand);
//						System.out.println("Node expansion finished.");
						
					}
					else {
						Debug.msg("No conflict/s found.");
					}
				}
				else {
					Debug.msg("Checking result returned null, Thread must have been interrupted.");
				}	
			}
			catch (DomainSizeException e){
				throw e;
			}
		}	
//		System.err.println("SHUTTING DOWN");
		this.threadPool.shutdownNow();	
		
		// Create the diagnoses from the nodes
		for (DAGNode node : this.diagnosisNodes.getCollection()) {
			this.diagnoses.add(new Diagnosis(node.diagnosis, model));
		}
		
		addCertainlyFaultyStatements(this.diagnoses);
		
		return this.diagnoses;
	}
	
	public long start = 0;
	
	/**
	 * Expands nodes in parallel. Pushes all nodes into the thread pool without checking things.
	 * Will lead to duplicate nodes in the moment
	 *
	 */	
	public void expandNodes(SharedCollection <DAGNode>nodesToExpand){
		
		start = System.nanoTime();
		
		boolean firstDone = false;
		
		int lastJob = 0;
		int lastSize = 0;
		int size = 0;
		// Look if we have something to do
		while ((incrementJobsToDo(NO_OP) > 0 || size != lastSize) && 
				(sessionData.config.maxDiagnoses == -1 || diagnosisNodes.getCollection().size() < sessionData.config.maxDiagnoses) &&
				!Thread.currentThread().isInterrupted()) {
			
			// Remove the counter for the first job which we do not remove later on
			if (firstDone == false) {
				incrementJobsToDo(-1);
				firstDone = true;
			}
			
//			System.out.println("RUN: Assigning next job: " + nextJob + ", last job was: " + lastJob);
//			size = nodesToExpand.getCollection().size();
			if (size != lastSize) {
				// A number of new jobs arrived
				int newJobs = size - lastSize;
				lastSize = size;
//				System.out.println("RUN: " + newJobs + " new nodes in queue");
				for (int i=0;i<newJobs;i++) {
					// Get the node to expand
					DAGNode node = nodesToExpand.getCollection().get(lastJob);
					
//					System.out.println("MAIN: Processing a new node: " + node);
					
					// Only expand if the node is not closed..
					if (node.closed == false && node.nodeStatusP != DAGNode.nodeStatusParallel.cancelled && (node.nodeLevel < this.sessionData.config.searchDepth || this.sessionData.config.searchDepth == -1)){
						//check whether node has a reduced conflict set and use this instead.
						List<Constraint> nodeConflictSet = (node.reducedConflict == null) ? node.conflict : node.reducedConflict;
						// Actually we know how many things we have to add
//						System.out.println("Will add a number of nodes to add: " + nodeConflictSet.size());
						incrementJobsToDo(nodeConflictSet.size());
						for (Constraint label : nodeConflictSet){
							node.nodeStatusP = DAGNode.nodeStatusParallel.scheduled;
							threadPool.execute(new EdgeWorker(	this,
																node, 
																label, 
																null, 
																this.sessionData, 
																nodesToExpand,
																this.model)
							);
						}
					}
					lastJob++;
				}
				
			}
			else {
//				System.out.println("RUN: No new job started");
			}
			try {
				// Wait a bit..
//				Thread.sleep(1);
				// Wait for notification instead of just waiting a bit
				synchronized (this) {
					if (incrementJobsToDo(NO_OP) > 0) {
						if (Thread.currentThread().isInterrupted()) {
							break;
						}
						wait();
					}
				}
				// Look if there are new nodes
				size = nodesToExpand.getCollection().size();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
//				e.printStackTrace();
			}
		}
//		System.out.println("RUN: Shutting down the threadpool");
//		System.out.println("RUN: Job pool size: " + nodesToExpand.getCollection().size());
		
		threadPool.shutdownNow();
		
		finishedTime = System.nanoTime();
		
//		long waitStart = System.currentTimeMillis();
		try {
			threadPool.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
//		long waitEnd = System.currentTimeMillis();
//		System.out.println("Waited " + (waitEnd - waitStart) + " ms for threadpool termination.");
	}	

}
