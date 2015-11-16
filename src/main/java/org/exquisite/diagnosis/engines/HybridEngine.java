package org.exquisite.diagnosis.engines;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.common.SharedCollection;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A hybrid engine that runs both the depth-first and the parallelized breadth-first search
 * Will only returns the first minimal tests.diagnosis
 *
 * @author dietmar
 */
public class HybridEngine<T> extends AbstractHSDagBuilder<T> {
    // The internal engines
    // breadth first
    AbstractHSDagBuilder bfsEngine;
    // depth first
    AbstractHSDagBuilder dfsEngine;

    // create a threadpool of size two to run the engines
    ExecutorService threadPool;

    // The result
    SharedCollection<List<T>> result = new SharedCollection<List<T>>();

    // Engine runners
    EngineRunner bfs = null;
    EngineRunner dfs = null;

    // A countdownlatch
    CountDownLatch latch = null;

    /**
     * Create the engine for a session and accepts a number of threads
     *
     * @param sessionData
     * @param nbThreads
     */
    public HybridEngine(ExquisiteSession sessionData, int nbThreads) {
        super(sessionData);
        // limit the search
        sessionData.config.maxDiagnoses = 1;
        // also the depth for the bfs-search?
        // initialize the engines
        // give them half of the threads at the moment
        int threadsForDFS = nbThreads;
        if (nbThreads > 1) {
            this.bfsEngine = new FullParallelHSDagBuilder(sessionData, nbThreads / 2);
            threadsForDFS = nbThreads / 2 + (nbThreads % 2);
        } else {
            this.bfsEngine = null;
        }

        this.dfsEngine = new HeuristicDiagnosisEngine(sessionData, threadsForDFS);
        // init the threadpool
        this.threadPool = Executors.newFixedThreadPool(2);

        latch = new CountDownLatch(1);
        // Create the runners
        bfs = new EngineRunner(this.bfsEngine, this.result, latch);
        dfs = new EngineRunner(this.dfsEngine, this.result, latch);


    }


    /**
     * Run the two strategies
     */
    @Override
    public List<Diagnosis<T>> calculateDiagnoses() {
//		System.out.println("Starting hybrid");
        // run them
        if (this.bfsEngine != null) {
            this.threadPool.execute(bfs);
        }
        this.threadPool.execute(dfs);

        // Wait until one runner is done

        try {
//			System.out.println("Waiting ");
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("Interrupted when calculating diagnoses (hybrid)");
            e.printStackTrace();
        }

//		System.out.println("Finished one, dfs: " + dfs.finished + " bfs: " + bfs.finished);
        // get the results
        List<List<T>> diagsFromRunners = new ArrayList<List<T>>(result.getCollection());
        // get the first one
        List<T> oneDiag = null;
        List<Diagnosis<T>> finalResult = new ArrayList<>();
        if (diagsFromRunners.size() > 0) {
            oneDiag = new ArrayList<T>(diagsFromRunners.get(0));
            Diagnosis<T> d = new Diagnosis<>(oneDiag, sessionData.diagnosisModel);
            finalResult.add(d);
        }
//		else {
//			System.out.println("No tests.diagnosis found in hybrid!");
//		}
        // try to interrupt the other
        this.threadPool.shutdownNow();

        // Use minimum of both engines as own finishedTime, because both engines also wait for their threads to be terminated.
        long bfsFinished = bfs.getFinishedTime();
        long dfsFinished = dfs.getFinishedTime();
        if (bfsFinished != 0 && dfsFinished != 0) {
            finishedTime = Math.min(bfsFinished, dfsFinished);
        } else if (bfsFinished != 0) {
            finishedTime = bfsFinished;
        } else {
            finishedTime = dfsFinished;
        }

//		System.out.println("Shutdown before wait: " + this.threadPool.isShutdown());
//		System.out.println("BFS finished: " + bfs.finished);
//		System.out.println("DFS finished: " + dfs.finished);
//		System.out.println("Returning result of size: " + finalResult);
        try {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
//			e.printStackTrace();
        }
//		System.out.println("Shutdown after wait: " + this.threadPool.isShutdown());
//		System.out.println("Terminated after wait: " + this.threadPool.isTerminated());
//		System.out.println("BFS finished: " + bfs.finished);
//		System.out.println("DFS finished: " + dfs.finished);
        // could be empty
        return finalResult;
    }


    @Override
    public void expandNodes(List<DAGNode<T>> nodesToExpand)
            throws DomainSizeException {
    }

    /**
     * A class to run an engine
     *
     * @author dietmar
     */
    class EngineRunner extends Thread {
        // Am i finished?
        public boolean finished = false;
        // the handle to the engine
        AbstractHSDagBuilder engine;
        // a pointer to the result
        SharedCollection<List<T>> result;
        // The latch
        CountDownLatch latch = null;

        /**
         * Create a new runner
         *
         * @param engine
         * @param result
         */
        public EngineRunner(AbstractHSDagBuilder engine,
                            SharedCollection<List<T>> result, CountDownLatch latch) {
            super();
            this.engine = engine;
            this.result = result;
            this.latch = latch;
        }

        /**
         * Start the computations
         */
        public void run() {
            try {
//				System.out.println("Running engine " + engine.getClass().getName());
                // Get a tests.diagnosis
                List<Diagnosis<T>> diagnoses = engine.calculateDiagnoses();
//				System.out.println("Got a result " + engine.getClass().getName() + " of size " + diagnoses.size());
                if (diagnoses != null && diagnoses.size() > 0) {
                    this.result.add(diagnoses.get(0).getElements());
                }
                this.finished = true;
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error calcualting diagnoses " + e.getMessage());
            }
        }

        public long getFinishedTime() {
            return engine.getFinishedTime();
        }
    }

}
