package org.exquisite.diagnosis.engines;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.common.EdgeWorker;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.engines.common.SharedCollection;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.tools.Debug;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A level-wise parallelized version of the HS-DAG construction
 *
 * @author dietmar
 */
public class ParallelHSDagBuilder<T> extends AbstractHSDagBuilder<T> {

    protected final long ROOT_LEVEL = 0;
    /**
     * Indicating at what depth the graph search is at. eg. root = level 0, child of root = level 1 etc.
     * Used to ensure the search stops at that desired search depth (if one is specified)
     */
    protected long currentLevel = ROOT_LEVEL;

    /**
     * maximum number of threads to initialize the thread pool with.
     */
    protected int maxThreadPoolSize = 4;

    /**
     * number of edges to traverse - used by barrier.
     */
    protected int currentLevelEdgeCount = 0;

    /**
     * Pool of available threads for node expansion workers
     */
    protected ExecutorService threadPool;


    public ParallelHSDagBuilder(ExquisiteSession sessionData, int threadPoolSize) {
        super(sessionData);
        this.maxThreadPoolSize = threadPoolSize;
    }

    /**
     * Calculates the total number of edges for a set of nodes at a given level of the graph.
     * This number is then used by the CountDownLatch barrier to wait until each thread for a given
     * level of the graph has finished.
     *
     * @param nodes
     * @return
     */
    public static <T> int calculateEdgeLevelCount(List<DAGNode<T>> nodes) {
        int result = 0;
        //if the node has a reduced conflict set (resulting from duplicate path check) then use this size instead.
        //otherwise the barrier will have the incorrect number of threads to wait for and will wait indefinitely.
        for (DAGNode<T> node : nodes) {
            result += (node.reducedConflict == null) ? node.conflict.size() : node.reducedConflict.size();
        }
        return result;
    }

    /**
     * Checking for duplicates
     * Remove all elements from the node labels which do not need to be expanded anymore
     *
     * @param nodes the nodes to be expanded
     */
    public static <T> void checkForDuplicatePaths(List<DAGNode<T>> nodesToExpand) {
        // Create a list of things to do on the next level.
        List<List<T>> pathsToExplore = new ArrayList<List<T>>();
        // Make a copy to avoid co-modification
        List<DAGNode<T>> nodesToExpandCopy = new ArrayList<DAGNode<T>>(nodesToExpand);
        // Go through the nodes. Take the path so far and the potential extension
        // If this extension is already planned to do in a previous branch of the
        // DAG, update the conflict label of the node and remove the redundant
        // element. need to make sure that we do not change the cached conflict itself
        for (DAGNode<T> node : nodesToExpandCopy) {
            List<T> conflictAtNode = node.conflict;
            if (conflictAtNode == null) {
//				System.out.println("No conflict at node?");
                return;
            }
            for (T ct : conflictAtNode) {
                List<T> pathToExplore = new ArrayList<T>();
                pathToExplore.addAll(node.pathLabels);
                pathToExplore.add(ct);
                if (!isConstraintSetContainedInList(pathToExplore, pathsToExplore)) {
                    pathsToExplore.add(pathToExplore);
                } else {
                    // TS: Use reducedConflict, if it was created before.
                    if (node.reducedConflict == null) {
                        List<T> reducedConflict = new ArrayList<T>();
                        reducedConflict.addAll(conflictAtNode);
                        reducedConflict.remove(ct);
                        node.reducedConflict = reducedConflict;
                    } else {
//						System.err.println("Removed another constraint from reducedConflict set. Difference?");
                        node.reducedConflict.remove(ct);
                    }
                }
            }
        }
    }

    /**
     * A method that checks if a given constraint set is part of a list of constraint sets
     * (pointer comparison of elements)
     *
     * @param element an element to check
     * @param list    a list of other constraint sets
     * @return true, if the element is contained in the list
     */
    public static <T> boolean isConstraintSetContainedInList(List<T> element, List<List<T>> list) {
        for (List<T> cset : list) {
            if (cset.size() == element.size()) {
                if (cset.containsAll(element)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Diagnosis<T>> calculateDiagnoses() throws DomainSizeException {
        this.threadPool = Executors.newFixedThreadPool(maxThreadPoolSize);
        this.diagnoses.clear();
        this.diagnosisNodes.clear();

        if (rootNode == null) {
            Debug.msg("Empty root node - doing inital test");
            try {

                QuickXPlain<T> qx = NodeExpander.createQX(this.sessionData, this);
                ConflictCheckingResult<T> checkingResult = qx.checkExamples(
                        this.sessionData.diagnosisModel.getPositiveExamples(),
                        new ArrayList<T>(),
                        true);

                if (checkingResult != null) {
                    if (checkingResult.conflictFound()) {
                        List<T> tempSet = new ArrayList<T>();
                        tempSet.addAll(checkingResult.conflicts.get(0));
                        incrementConstructedNodes();
                        rootNode = new DAGNode<T>(tempSet);
                        rootNode.examplesToCheck = new ArrayList<>(checkingResult.failedExamples);
                        // DJ: Do not add the root node here.
//						allConstructedNodes.addItem(rootNode);
                        // DJ: we could actually add all conflicts we have to this list
                        synchronized (checkingResult.conflicts.getWriteLock()) {
                            for (List<T> c : checkingResult.conflicts.getCollection()) {
                                knownConflicts.add(c);
                            }
                        }

                        List<DAGNode<T>> nodesToExpand = new ArrayList<DAGNode<T>>();
                        nodesToExpand.add(rootNode);
                        this.currentLevelEdgeCount = rootNode.conflict.size();
                        this.currentLevel = ROOT_LEVEL;
//						System.out.println("Starting node expansion... ");
                        expandNodes(nodesToExpand);
//						System.out.println("Node expansion finished.");

//						List<List<T>> conflicts = knownConflicts.getCollection();
//						for (List<T> conflict: conflicts)
//						{
//							System.out.println(Utilities.printConstraintList(conflict, model));
//						}

                    } else {
                        Debug.msg("No conflict/s found.");
                    }
                } else {
                    Debug.msg("Checking result returned null, Thread must have been interrupted.");
                }
            } catch (DomainSizeException e) {
                throw e;
            }
        }
//		System.err.println("SHUTTING DOWN");
        this.threadPool.shutdownNow();

        // TODO: Level Wise Parallelization cannot efficiently search for a specified number of diagnoses, as the current level is always processed completely
        // finishedTime also needs to be at a different location for the efficient implementation

        addCertainlyFaultyStatements(this.diagnoses);

        finishedTime = System.nanoTime();

        return this.diagnoses;
    }

    /**
     * Expands each node at a given depth of the graph in a separate thread. Making use
     * of ExecutorService to manage thread reuse.
     * Uses CountDownLatch to block the program continuing until all nodes for a given level
     * have been expanded.
     */
    @Override
    public void expandNodes(List<DAGNode<T>> nodesToExpand) {
        // TEST TS ************
//		long start = System.currentTimeMillis();
//		long end = 0;
        //*********************

        //a barrier to enforce only nodes of a given level are expanded.
        CountDownLatch barrier = new CountDownLatch(currentLevelEdgeCount);
        //list caching each node that is expanded.
        SharedCollection<DAGNode<T>> newNodesToExpand = new SharedCollection<DAGNode<T>>();

//		System.out.println("CURRENT COUNT: " + currentLevelEdgeCount);
//		System.out.println("NODES TO EXPAND: " + nodesToExpand.size());

        //do bfs...
        while (!nodesToExpand.isEmpty()) {
//			System.out.println("NODES TO EXPAND: " + nodesToExpand.size() + " at level " + currentLevel);
            //remove node from queue
            DAGNode<T> node = nodesToExpand.remove(0);
//			System.out.println("Working on node with label: " + Utilities.printConstraintList(node.pathLabels, this.model));
            //for each edge extending from the node, create a new thread to run the expansion worker.
            // This check is actually redundant
            if (node.nodeLevel < this.sessionData.config.searchDepth || this.sessionData.config.searchDepth == -1) {
//				System.out.println("Will start workers");
                //check whether node has a reduced conflict set and use this instead.
                List<T> nodeConflictSet = (node.reducedConflict == null) ? node.conflict : node.reducedConflict;
//				System.out.println("Starting edge workers: " + nodeConflictSet.size());
                for (T label : nodeConflictSet) {
                    threadPool.execute(new EdgeWorker(this,
                            node,
                            label,
                            barrier,
                            this.sessionData,
                            newNodesToExpand,
                            this.model)
                    );
//					System.out.println("Pool size: " + ( ((ThreadPoolExecutor) threadPool).getActiveCount()));
                }
            } else {
//				System.out.println("Reached level, nodeLevel " + node.nodeLevel + ", searchdepth: " + this.sessionData.config.searchDepth);
            }
        }
//		System.out.println("Have started: " + totalEdgeWorkersStarted + " edgeworkers, MY LIST IS EMPTY, waiting for barrier: " + currentLevel  + ", cdl: " + barrier.getCount());
        try {
            //wait for node expansion workers to finish
            barrier.await();
            //add any new nodes to list of nodes to expand.
            nodesToExpand = new ArrayList<DAGNode<T>>(newNodesToExpand.getCollection());
//			System.out.println("NEW NODEs TO DO: " + nodesToExpand.size());

//			long start = System.currentTimeMillis();
            //remove duplicate node paths

            // TS: We dont need this check, if we synchronize the check in expand
//			checkForDuplicatePaths(nodesToExpand);

            //recalculate number of edges that need to be traversed at next level.
            this.currentLevelEdgeCount = calculateEdgeLevelCount(nodesToExpand);
//			System.out.println("Preprocessing next level needed: " + (System.currentTimeMillis() - start) + " ms.");

        } catch (InterruptedException e) {
//			e.printStackTrace();
            // TS: This exception tells us, that we should stop the work.
            return;
        }

        //start expanding nodes at next level (if any).
        if (!nodesToExpand.isEmpty()) {
            //expand next level of graph only if the current level is less than specified search depth and
            //the number of results are less than the specified max diagnoses count.
            if ((this.searchDepth == -1 || this.currentLevel < this.searchDepth) &&
                    (this.maxDiagnoses == -1 || this.diagnoses.size() < this.maxDiagnoses)) {
                this.currentLevel++;

                // Only expand nodes if we're not already there?
                if (currentLevel < this.sessionData.config.searchDepth || this.sessionData.config.searchDepth == -1) {
                    // TEST TS ************
//					end = System.currentTimeMillis();
//					System.out.println("Level " + currentLevel + " finished after " + (end - start) + "ms.");
                    //*********************

                    expandNodes(nodesToExpand);
                }
            }
        }
        // TEST TS ************
//		end = System.currentTimeMillis();
//		System.out.println("Level " + currentLevel + " finished after " + (end - start) + "ms.");
        //*********************
    }
}