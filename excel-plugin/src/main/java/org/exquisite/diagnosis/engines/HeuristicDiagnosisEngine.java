package org.exquisite.diagnosis.engines;

import org.exquisite.core.engines.AbstractHSDagEngine;
import org.exquisite.core.engines.tree.Node;
import org.exquisite.core.measurements.MeasurementManager;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.engines.common.SharedCollection;
import org.exquisite.diagnosis.engines.heuristic.ExtendedDAGNode;
import org.exquisite.diagnosis.engines.heuristic.FormulaSelector;
import org.exquisite.diagnosis.engines.heuristic.HeuristicNodeExpander;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.tools.Debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.exquisite.core.measurements.MeasurementManager.incrementCounter;

/**
 * A heuristic searcher (could be depth-first, iterative deepening..
 *
 * @author dietmar
 */
public class HeuristicDiagnosisEngine<T> extends AbstractHSDagEngine<T> {

    public static boolean MINIMIZE_DIAGNOSES = true;
    public boolean doMinimizationInThreads = true;

    // Define two first strategies.
//	public enum strategy {
//		DepthFirst, IterativeDeepening
//	};
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

    /**
     * Create a new engine
     *
     * @param sessionData
     */
    public HeuristicDiagnosisEngine(ExcelExquisiteSession sessionData, int threads) {
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
                    model.getPositiveExamples(), new ArrayList<T>(),
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
                incrementCounter(MeasurementManager.COUNTER_CONSTRUCTED_NODES);
                this.rootNode = new ExtendedDAGNode<>(tempSet);
                this.rootNode.examplesToCheck = new ArrayList<>(
                        checkingResult.failedExamples);
                allConstructedNodes.add(rootNode);

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

                    List<T> startConstraints = new ArrayList<T>();
                    T startConstraint = null;
//					System.out.println("Running " + NB_THREADS + " node expanders in parallel");
                    List<HeuristicNodeExpander> expanders = new ArrayList<HeuristicNodeExpander>();
                    for (int i = 0; i < nbThreads; i++) {
//						NodeSelector.strategy theStrategy = NodeSelector.strategy.left2right;
                        FormulaSelector.strategy theStrategy = FormulaSelector.strategy.random;
//						if (i%3 == 0) {
//							theStrategy = NodeSelector.strategy.left2right;
//						}
//						else if (i%3 == 1) {
//							theStrategy = NodeSelector.strategy.right2left;
//						}

                        if (theStrategy == FormulaSelector.strategy.random) {
                            // Find the first constraint to expand for this expander
                            if (startConstraints.size() == 0) {
                                startConstraints.addAll(rootNode.constraintsToExplore);
                                Collections.shuffle(startConstraints);
                            }
                            startConstraint = startConstraints.remove(0);
                        }

                        expanders.add(new HeuristicNodeExpander( // start them with a copy
                                new ExtendedDAGNode<T>(this.rootNode), startConstraint,
                                this,
                                new FormulaSelector(theStrategy)));
                    }
                    Collections.shuffle(expanders);
                    for (HeuristicNodeExpander exp : expanders) {
                        threadPool.execute(exp);
                    }

                    // Wait until enough diags have been found or until all expanders have finished
                    synchronized (this) {
                        while ((maxDiags == -1 || knownDiags.getCollection().size() < maxDiags) &&
                                !allExpandersFinished(expanders)) {
                            wait();
                        }
                    }

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
            if (diag != null) {
                //			System.out.println("My collection of constraints: " + diag.size());
                Diagnosis<T> d = new Diagnosis<T>(new ArrayList<T>(diag), this.model);
                //			System.out.println("Created a tests.diagnosis, the model is: " + this.model);
                diagnoses.add(d);
                //			System.out.println("The nb constraints of my diag are : " 					+ d.getElements().size());
            }
        }
//		System.out.println(Utilities.printSortedDiagnoses(diagsList, ' '));

        addCertainlyFaultyStatements(diagnoses);

        return diagnoses;
    }

    private boolean allExpandersFinished(List<HeuristicNodeExpander> expanders) {
        for (HeuristicNodeExpander expander : expanders) {
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

		
		/*

		// Check one constraint at a time and look if it is still a tests.diagnosis
		// (for all examples)
		for (int i = 0; i < diagnosisToCheck.size(); i++) {
			T c = diagnosisToCheck.get(i);
//			System.out.println("Checking c: " + Utilities.printConstraintList(Arrays.asList(new T[]{c}), globalDiagnosisModel));
			List<T> shrinkedDiagnosis = new ArrayList<T>(diagnosisToCheck);
			shrinkedDiagnosis.remove(c);
			// Remember for how many examples the constraint was redundant
			boolean removeConstraint = true;
			// Check all the examples
			for (Example ex : sessionData.getDiagnosisModel().getPositiveExamples()) {
				ConstraintsQuickXPlain qx =  NodeExpander.createQX(sessionData, null);
				qx.currentExample = ex;
				List<T> constraints2Check = new ArrayList<T>();
				
				// DJ: Have to differentiate between simulation or not
				if (AbstractHSDagEngine.USE_QXSIM) {
					// Only check the elements of the shrinked tests.diagnosis
					constraints2Check.addAll(shrinkedDiagnosis);
				}
				else { // standard case
					// Add the example constraints
					// Hmm.. If using CHOCO3 - the runners have to be defined or
					// things learned from the
					// XML.. TODO
					constraints2Check.addAll(ex.constraints);
					// Add all assumedly correct and possibly faulty constraints
					List<T> assumedlyCorrect = new ArrayList<T>(
									sessionData.getDiagnosisModel().getPossiblyFaultyStatements());
					assumedlyCorrect.addAll(sessionData.getDiagnosisModel()
							.getCorrectStatements());
					// Remove all elements from the current shrinked tests.diagnosis to be
					// examined (a subset will be added above)
					assumedlyCorrect.removeAll(shrinkedDiagnosis);
					// add them to the list of those to be examined
					constraints2Check.addAll(assumedlyCorrect);
				}					

				try {
					boolean consistent = qx.isConsistent(constraints2Check);
					// if the problem is consistent even though we removed a
					// certain constraint,
					// then the last element is not essential (for this example)
					if (consistent) {
//						 System.out.println("Problem is consistent even after removing element ...");
						// hmm.. redundant for this or all examples?
						// we have to test all examples
						
					} else {
//						 System.out.println("Not having " + c + " in the tests.diagnosis will make the problem inconsistent - have to keep it");
						removeConstraint = false;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out
							.println("Exception when checking consistency (domain sizes?) "
									+ e.getMessage());
				}
			}
			// We are through with the examples, let's see which constraint was
			// always redundant
			if (removeConstraint) {
				List<T> clist = new ArrayList<T>();
				clist.add(c);
//				System.out.println("The constraint " + Utilities.printConstraintList(clist, globalDiagnosisModel) + " is redundant");
				diagnosisToCheck.remove(c);
				i--;
			}

		}
		
		// Return the result
		return diagnosisToCheck;
		*/

    }

    // Not needed here - remains empty for the moment.
    // Not sure it should be in the superclass anyway.
    @Override
    public void expandNodes(List<Node<T>> nodesToExpand)
            throws DomainSizeException {
    }
}
