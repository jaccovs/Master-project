package org.exquisite.diagnosis.engines;

import org.exquisite.core.engines.AbstractHSDagEngine;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.engines.common.SharedCollection;
import org.exquisite.diagnosis.engines.heuristic.ExtendedDAGNode;
import org.exquisite.diagnosis.engines.prdfs.NodeSelector;
import org.exquisite.diagnosis.engines.prdfs.NodeWithConstraint;
import org.exquisite.diagnosis.engines.prdfs.PRDFSNodeExpander;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.core.engines.tree.Node;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.tools.Debug;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.exquisite.core.measurements.MeasurementManager.COUNTER_CONSTRUCTED_NODES;
import static org.exquisite.core.measurements.MeasurementManager.incrementCounter;

/**
 * @author Thomas
 */
public class ParallelRandomDFSEngine<T> extends AbstractHSDagEngine<T> {

    public static boolean MINIMIZE_DIAGNOSES = true;
    public boolean doMinimizationInThreads = true;

    // Define two first strategies.
//	public enum strategy {
//		DepthFirst, IterativeDeepening
//	};
    public SortedSet<NodeWithConstraint<T>> nodesToExpand = Collections
            .synchronizedSortedSet(new TreeSet<NodeWithConstraint<T>>());
    /**
     * The list of already known conflicts (Has to be synchronized)
     */
//	public SharedCollection<List<T>> knownConflicts = new SharedCollection<List<T>>();

    // The global list where we can add diagnoses
    public SharedCollection<List<T>> knownDiags = new SharedCollection<List<T>>();
    // The root
    public ExtendedDAGNode<T> rootNode;
    int nbThreads;
    // Never return more than this number of diagnoses
    int DIAG_LIMIT = -1;
    List<PRDFSNodeExpander> expanders;

    /**
     * Create a new engine
     *
     * @param sessionData
     */
    public ParallelRandomDFSEngine(ExcelExquisiteSession sessionData, int threads) {
        super(sessionData);
//		System.out.println("Heuristic engine of size: " + threads);
        Debug.QX_DEBUGGING = false;
        this.nbThreads = threads;
    }

    @Override
    public void resetEngine() {
        rootNode = null;
        knownDiags.clear();
        super.resetEngine();
    }

    // How to calculate the diagnoses
    @Override
    public List<Diagnosis<T>> calculateDiagnoses() throws DomainSizeException {

        // Do an initial check
        if (this.rootNode == null) {
            // Initialize the quick explain object
            ConstraintsQuickXPlain<T> qxplain = NodeExpander.createQX(this.sessionData);
            ConflictCheckingResult<T> checkingResult = qxplain.checkExamples(
                    model.getPositiveExamples(), new ArrayList<>(),
                    true);

            if (Thread.currentThread().isInterrupted()) {
                return diagnoses;
            }

            // In fact, we can run parallelqx here and start with new search
            // threads whenever a new
            // nodeLabel is available

            if (checkingResult.conflictFound()) {
//				System.out.println("Have to do a tests.diagnosis");
                // Create the rootnode
                List<T> tempSet = new ArrayList<T>();
                tempSet.addAll(checkingResult.conflicts.get(0));
                incrementCounter(COUNTER_CONSTRUCTED_NODES);
                this.rootNode = new ExtendedDAGNode<T>(tempSet);
                this.rootNode.examplesToCheck = new ArrayList<>(
                        checkingResult.failedExamples);
                allConstructedNodes.add(rootNode);
                NodeSelector<T> nodeSelector = new NodeSelector<T>(this);
                nodeSelector.addNodeWithConstraints(this.rootNode);

//				System.out.println("Failed examples: " + checkingResult.failedExamples.size());

                // DJ: we could actually add all conflicts we have to this list
                synchronized (checkingResult.conflicts.getWriteLock()) {
                    for (List<T> c : checkingResult.conflicts.getCollection()) {
                        knownConflicts.add(c);
                    }
                }
                // Countdown until n solutions are found
                // Stop other threads when we found n diags
                ExecutorService threadPool = null;
                try {
                    /**
                     * Pool of available threads for node expansion workers
                     */
//					System.out.println("Starting " + nbThreads + " threads");
                    threadPool = Executors.newFixedThreadPool(nbThreads);
                    // Find out if we should stop early
                    int maxDiags = DIAG_LIMIT;
                    if (sessionData.getConfiguration().maxDiagnoses > 0) {
                        maxDiags = sessionData.getConfiguration().maxDiagnoses;
                    }

                    // TS: If we search for only a single tests.diagnosis, the main thread does the tests.diagnosis minimization
                    if (maxDiags == 1 && MINIMIZE_DIAGNOSES) {
                        doMinimizationInThreads = false;
//						System.out.println("Turning off minimization in threads.");
                    }

//					System.out.println("Running " + NB_THREADS + " node expanders in parallel");
                    expanders = new ArrayList<PRDFSNodeExpander>(nbThreads);
                    for (int i = 0; i < nbThreads; i++) {
//						NodeSelector.strategy theStrategy = NodeSelector.strategy.left2right;
//						FormulaSelector.strategy theStrategy = FormulaSelector.strategy.random;
//						if (i%3 == 0) {
//							theStrategy = NodeSelector.strategy.left2right;
//						}
//						else if (i%3 == 1) {
//							theStrategy = NodeSelector.strategy.right2left;
//						}

                        expanders.add(new PRDFSNodeExpander(this, nodeSelector));
                    }
//					Collections.shuffle(expanders);
                    for (PRDFSNodeExpander exp : expanders) {
                        threadPool.execute(exp);
                    }

                    // Wait until enough diags have been found or until all expanders have finished
                    synchronized (this) {
                        while ((maxDiags == -1 || knownDiags.getCollection().size() < maxDiags) &&
                                !allExpandersFinished(expanders)) {
//							System.out.println("Maxdiags: " + maxDiags + ", knownDiags: " + knownDiags.getCollection().size() + ", allExpandersFinished: " + allExpandersFinished(expanders));
                            wait();
//							System.out.println("Got notified.");
                        }
                    }

//					System.out.println("Shutting down all threads.");

                    // Send interrupted signals to threads
                    threadPool.shutdownNow();

                    // Do minimization now, if we only search for a single tests.diagnosis
                    if (!doMinimizationInThreads) {
//						System.out.println("Doing minimization after thread has finished.");
//						minimizeDiagnosis(tests.diagnosis)
                        List<List<T>> diags = this.knownDiags.getCollection();
                        if (diags != null && diags.size() > 0) {
                            diags.set(0, minimizeDiagnosis(diags.get(0)));
                        }
                    }

                    //finishedTime = System.nanoTime();

//					System.out.println("Diags found: " + knownDiags.getCollection().size());
//					System.out.println("MaxDiags was: " + maxDiags);
//					System.out.println("Expanders finished: " + allExpandersFinished(expanders));
//					System.out.println("Nb of expanders: " + expanders.size());

                    // Wait for their termination. We do not really have to wait here
                    threadPool.awaitTermination(10, TimeUnit.SECONDS);
//					long time = System.currentTimeMillis();


//					System.out.println("Main - Done with searching - latch countdown successful");
//					long time2 = System.currentTimeMillis();
//					System.out.println("Shutdown took " + (time2-time) + "ms.");

//					System.out.println("Expanders finished after shutdown: " + allExpandersFinished(expanders));

                } catch (InterruptedException e) {
//					e.printStackTrace();
                    return diagnoses;
                } finally {
                    if (threadPool != null && !threadPool.isShutdown())
                        threadPool.shutdownNow();
                }
            }
//			else {
//				System.out.println("Everything okay at root level  - no tests.diagnosis required.");
//			}
        }
        // Copy the stuff before we return it
        // System.out.println("Copying the list: " +
        // this.knownDiags.getCollection().size());
        for (List<T> diag : this.knownDiags.getCollection()) {
//			System.out.println("My collection of constraints: " + diag.size());
            Diagnosis<T> d = new Diagnosis<T>(new ArrayList<T>(diag), this.model);
//			System.out.println("Created a tests.diagnosis, the model is: " + this.model);
            diagnoses.add(d);
//			System.out.println("The nb constraints of my diag are : " 					+ d.getElements().size());
        }
//		System.out.println(Utilities.printSortedDiagnoses(diagsList, ' '));

        addCertainlyFaultyStatements(this.diagnoses);

        return diagnoses;
    }

    private boolean allExpandersFinished(List<PRDFSNodeExpander> expanders) {
        for (PRDFSNodeExpander expander : expanders) {
            if (!expander.finished) {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove redundant elements from the list. Need a clever algorithm
     *
     * @param diagnosis
     * @return the minimized tests.diagnosis
     */
    public List<T> minimizeDiagnosis(List<T> diagnosis) {
        // Remember the redundant constraints
        List<T> diagnosisToCheck = new ArrayList<T>(diagnosis);

        for (int i = 0; i < diagnosisToCheck.size(); i++) {
            T c = diagnosisToCheck.get(i);
//			System.out.println("Checking c: " + Utilities.printConstraintList(Arrays.asList(new T[]{c}), globalDiagnosisModel));
            List<T> shrinkedDiagnosis = new ArrayList<T>(diagnosisToCheck);
            shrinkedDiagnosis.remove(c);

            ConstraintsQuickXPlain<T> qx = NodeExpander.createQX(sessionData);
            ConflictCheckingResult result;
            try {
                result = qx.checkExamples(sessionData.getDiagnosisModel().getPositiveExamples(), shrinkedDiagnosis, false);

                if (Thread.currentThread().isInterrupted()) {
                    return null;
                }

//				System.out.println("Can remove constraint " + c + ": " + (result.failedExamples.size() == 0));
                if (result.failedExamples.size() == 0) {
                    diagnosisToCheck.remove(i);
                    i--;
                }

            } catch (DomainSizeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Return the result
        return diagnosisToCheck;

    }

    // Not needed here - remains empty for the moment.
    // Not sure it should be in the superclass anyway.
    @Override
    public void expandNodes(List<Node<T>> nodesToExpand)
            throws DomainSizeException {
    }
}
