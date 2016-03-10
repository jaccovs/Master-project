package org.exquisite.diagnosis.engines;

import org.exquisite.core.engines.tree.Node;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.engines.common.EdgeWorker;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.engines.common.SharedCollection;
import org.exquisite.diagnosis.engines.common.SharedDAGNodeQueue;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.tools.Debug;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.exquisite.core.measurements.MeasurementManager.COUNTER_CONSTRUCTED_NODES;
import static org.exquisite.core.measurements.MeasurementManager.incrementCounter;

/**
 * Implements a fully parallelized hs tree algorithm
 *
 * @author dietmar
 */
public class FullParallelHSDagEngine<T> extends ParallelHSDagEngine<T> {

    public static int runningThreads = 0;
    public static Object runningThreadsSync = new Object();
    // A dummy NO_OP to safely read the open jobs counter
    static int NO_OP = -10;
    public long start = 0;
    // Need a global thread pool
    ExecutorService threadPool;
    // Some synchronization stuff
    // How many jobs are open
    private int jobsToDo = 1;


    /*
     * Create a new full HSDagEngine
     */
    public FullParallelHSDagEngine(ExcelExquisiteSession sessionData, int threadPoolSize) {
        super(sessionData, threadPoolSize);
        this.maxThreadPoolSize = threadPoolSize;
        jobsToDo = 1;
    }

    // avoid conflicts here
    // could also be a decrease
    public synchronized int incrementJobsToDo(int i) {
        if (i == NO_OP) {
            return jobsToDo;
        } else {
            jobsToDo = jobsToDo + i;
//			System.out.println("INCREMENT: Remaining jobs to do: " + jobsToDo);
        }
        return i;
    }

    /**
     * Have to do things slightly different here
     */
    @Override
    public List<Diagnosis<T>> calculateDiagnoses() throws DomainSizeException {
        this.threadPool = Executors.newFixedThreadPool(maxThreadPoolSize);
        this.diagnoses.clear();
        this.diagnosisNodes.clear();

        if (rootNode == null) {
            Debug.msg("Empty root node - doing inital test");
            try {
                ConstraintsQuickXPlain<T> qx = NodeExpander.createQX(this.sessionData);
                ConflictCheckingResult<T> checkingResult;
                if (!ConstraintsQuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {

                    checkingResult = qx.checkExamples(model.getPositiveExamples(), new ArrayList<>(), true);
                } else {
                    synchronized (ConstraintsQuickXPlain.ContinuingSync) {
                        checkingResult = new ConflictCheckingResult<>();
//						Debug.syncMsg("Start first checkExamplesParallel()");
                        qx.checkExamplesParallel(model.getPositiveExamples(), new ArrayList<T>(), true,
                                checkingResult, knownConflicts);
//						Debug.syncMsg("Main thread sleeping.");
                        try {
                            ConstraintsQuickXPlain.ContinuingSync.wait();
                        } catch (InterruptedException e) {
                            return diagnoses;
                        }
//						Debug.syncMsg("Main thread woke up.");
                    }
                }

                if (checkingResult != null) {
                    if (checkingResult.conflictFound()) {
                        List<T> tempSet = new ArrayList<>();
                        tempSet.addAll(checkingResult.conflicts.get(0));
                        incrementCounter(COUNTER_CONSTRUCTED_NODES);
                        rootNode = new Node<>(tempSet);
                        if (!ConstraintsQuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT) {
                            rootNode.examplesToCheck = new ArrayList<>(checkingResult.failedExamples);
                            // DJ: Do not add the root node; it will be added on expansion.
                            //						allConstructedNodes.addItem(rootNode);
                            //						knownConflicts.add(rootNode.nodeLabel);
                            // DJ: we could actually add all conflicts we have to this list
                            synchronized (checkingResult.conflicts.getWriteLock()) {
                                for (List<T> c : checkingResult.conflicts.getCollection()) {
                                    //							System.out.println("Adding a known conflit");
                                    knownConflicts.addItemListNoDups(c);
                                }
                            }
                        } else {
                            rootNode.examplesToCheck = model.getPositiveExamples();
                        }

                        // Create a special shared list for this type of expansion
                        SharedCollection<Node<T>> nodesToExpand = new SharedDAGNodeQueue<Node<T>>();
                        nodesToExpand.add(rootNode);
                        this.currentLevelEdgeCount = rootNode.nodeLabel.size();
                        this.currentLevel = ROOT_LEVEL;
//						System.out.println("Starting node expansion... ");
                        expandNodes(nodesToExpand);
//						System.out.println("Node expansion finished.");

                    } else {
                        Debug.msg("No nodeLabel/s found.");
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

        // Create the diagnoses from the nodes
        for (Node<T> node : this.diagnosisNodes.getCollection()) {
            this.diagnoses.add(new Diagnosis<T>(node.diagnosis, model));
        }

        addCertainlyFaultyStatements(this.diagnoses);

        return this.diagnoses;
    }

    /**
     * Expands nodes in parallel. Pushes all nodes into the thread pool without checking things.
     * Will lead to duplicate nodes in the moment
     */
    public void expandNodes(SharedCollection<Node<T>> nodesToExpand) {

        start = System.nanoTime();

        boolean firstDone = false;

        int lastJob = 0;
        int lastSize = 0;
        int size = 0;
        // Look if we have something to do
        while ((incrementJobsToDo(NO_OP) > 0 || size != lastSize) &&
                (sessionData.getConfiguration().maxDiagnoses == -1 || diagnosisNodes.getCollection()
                        .size() < sessionData.getConfiguration().maxDiagnoses) &&
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
                for (int i = 0; i < newJobs; i++) {
                    // Get the node to expand
                    Node<T> node = nodesToExpand.getCollection().get(lastJob);

//					System.out.println("MAIN: Processing a new node: " + node);

                    // Only expand if the node is not closed..
                    if (node.closed == false && node.nodeStatusP != Node.nodeStatusParallel.cancelled && (node.nodeLevel < this.sessionData.getConfiguration().searchDepth || this.sessionData.getConfiguration().searchDepth == -1)) {
                        //check whether node has a reduced nodeLabel set and use this instead.
                        List<T> nodeConflictSet = (node.reducedConflict == null) ? node.nodeLabel : node.reducedConflict;
                        // Actually we know how many things we have to add
//						System.out.println("Will add a number of nodes to add: " + nodeConflictSet.size());
                        incrementJobsToDo(nodeConflictSet.size());
                        for (T label : nodeConflictSet) {
                            node.nodeStatusP = Node.nodeStatusParallel.scheduled;
                            threadPool.execute(new EdgeWorker(this,
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

            } else {
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

        //finishedTime = System.nanoTime();

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
