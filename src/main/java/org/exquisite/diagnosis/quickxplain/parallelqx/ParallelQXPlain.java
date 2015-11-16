package org.exquisite.diagnosis.quickxplain.parallelqx;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * A parallel version of QX
 *
 * @author dietmar
 */

public class ParallelQXPlain<T> extends QuickXPlain<T> {

    // The set of already known conflicts
    private final List<List<T>> results = new ArrayList<>();
    // Number of threads
    public int minThreads = 6;
    // Number of threads
    public int maxThreads = 6;
    // max number of conflicts to search
    public int maxConflicts = 3;
    // A global lock?
    ReentrantLock lock = new ReentrantLock();
    // A counter
    int count = 0;
    // A global lock to protect access to the result list
    private ReadWriteLock resultsLock = new ReentrantReadWriteLock(true);
    // The thread pool
    private ThreadPoolExecutor pool;

    // Superclass constructor
    public ParallelQXPlain(ExquisiteSession<T> sessionData,
                           AbstractHSDagBuilder<T> dagbuilder) {
        super(sessionData, dagbuilder);
//    	System.out.println(" -- Creating parallel QX");

    }

    /**
     * Create the next counter value
     *
     * @return the next counter value
     */
    public int incCount() {
        return ++this.count;
    }

    /**
     * Find a set of conflicts
     */
    @Override
    public List<List<T>> findConflicts() {
//    	System.out.println("---- FINDING CONFICTS ..");
        // Start the multi-threaded search ..
//    	System.out.println("Possibly faulty ones: " + this.currentDiagnosisModel.getPossiblyFaultyStatements().size());
//    	System.out.println("Examples:" + this.currentDiagnosisModel.getPositiveExamples().size());

        // Create the thread pool
        this.pool = new ThreadPoolExecutor(minThreads, maxThreads, 100,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(maxConflicts, true));

        findConflict(new ArrayList<>(this.currentDiagnosisModel.getPossiblyFaultyStatements()));
        // Wait until all threads are done
        /*try {
            while (!pool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
            	System.out.println("Looping ...");
            }
//              System.out.println("Pool terminated (size/tasks/conflicts): " + pool.getLargestPoolSize() + " / " +
//                        pool.getCompletedTaskCount() + " / " + results.size());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //System.out.println("PQXP finished with " + results.size() + " conflicts.");
        return results;
    }


    /**
     * Do the true work - find one conflict after the other
     */
    public List<T> findConflict(List<T> possiblyFaultyConstraints) {
//    	System.out.println("-Find conflict called at level:   , results so far: " + results.size() + " , maxconflicts: " + maxConflicts);
        try {
            // Increase the counter
            incCount();
            // If we have enough conflicts, stop
            if (results.size() >= maxConflicts) {
//	        	System.out.println("Enough conflicts - shutting down.");
                if (!this.pool.isShutdown()) {
                    this.pool.shutdownNow();
                }
                return null;
            }
//	        System.out.println("Have to find a conflict. ...");
            // Create a new thread
            QXThread qxThread = new QXThread();
            //qxThread.dagbuilder = this.dagBuilder;
            //qxThread.session = this.sessionData;
            qxThread.qx = new QuickXPlain<T>(this.sessionData, this.diagnosisEngine);

            // If we have been passed an updated list of constraints -> overwrite this thing.
            if (possiblyFaultyConstraints != null) {
//	        	System.out.println("Got a new model to check ..");
//	        	System.out.println(possiblyFaultyConstraints);
                // Create a copy of the tests.diagnosis model
                DiagnosisModel<T> model = new DiagnosisModel<T>(this.currentDiagnosisModel);
                model.setPossiblyFaultyStatements(new ArrayList<T>(possiblyFaultyConstraints));

//            	System.out.println("PFC: " + Utilities.printConstraintListOrderedByName(model.getPossiblyFaultyStatements(), model));

                // Hmmm.. we should add the positive example here?
//            	System.out.println("The correct ones: " + model.getCorrectStatements().size());

                qxThread.qx.setDiagnosisModel(model);
            }

            FormulaListener<T> listener = FormulaListener.newInstance();

            qxThread.qx.constraintListener = listener;
            Future<List<T>> fqx = pool.submit(qxThread);

//	        System.out.println("Submitted some work");

            while (!listener.isReleased() || listener.hasConstraints()) {
//	        	System.out.println("---looping ..");
//	        	System.out.println("---released: " + listener.isReleased() + ", constraints: " + listener.hasConstraints());
                T foundConstraint = listener.getFoundConstraint();
//                System.out.println("---found one constraint ..: " + foundConstraint);
                if (foundConstraint != null) {
//                    System.out.println("Level " + count + " - found axiom to ignore in next test: " + foundConstraint);

                    Set<T> cu = new HashSet<T>(
                            this.currentDiagnosisModel.getPossiblyFaultyStatements());
                    cu.remove(foundConstraint);
                    if (!isKnownConflict(cu)) {
//                    	System.out.println("Starting a new task. Active " + pool.getActiveCount() 
//                    				+ " complete " + pool.getCompletedTaskCount()
//                                    + " threads " + pool.getLargestPoolSize());

//                        Searchable<Id> ct = c.copy();
                        findConflict(new ArrayList<T>(cu));
                    } else {
//                        System.out.println("Duplicate conflict possible. The branch is ignored!");
                    }
                } else {
//                	System.out.println("No conflict found at level " + level);
                }
            }
//	        System.out.println("Done with the main loop at level " + level);

            if (!fqx.isCancelled()) {
//	        	System.out.println("Was cancelled...");
                fqx.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
//    		System.out.println("Counter: " + this.count);
            if (decCount() == 0) {
//    	    	System.out.println("Shutting down the pool");
                this.pool.shutdown();
            }
        }

        return null;
    }


    /**
     * Look if the results already contain this conflict..
     *
     * @param cu the updated conflict
     * @return true if it is already there; false otherwise
     */
    private boolean isKnownConflict(Collection<T> cu) {
        resultsLock.readLock().lock();
        try {
            for (List<T> ids : getResults()) {
                if (cu.containsAll(ids)) {
//                	System.out.println("Found an existing conflict..");
                    return true;
                }
            }
            return false;
        } finally {
//        	System.out.println("New conflict, unlocking in contains Conflict");
            resultsLock.readLock().unlock();
        }
    }

    /**
     * Adds a conflict to the global set of conflicts
     *
     * @param formulaSet
     */
    private void addConflict(List<T> conflict) {
//    	System.out.println("-->Adding a aconflcit");
        if (results.size() >= maxConflicts) {
//        	System.out.println("Already engouh conflicts ..");
            return;
        }
        resultsLock.writeLock().lock();
        try {
            if (conflict != null && !isKnownConflict(conflict)) {
                results.add(conflict);
            }
        } finally {
            resultsLock.writeLock().unlock();
        }

    }


    /**
     * Get a pointer to the results
     *
     * @return
     */
    public List<List<T>> getResults() {
        return results;
    }

    /**
     * Decrement the counter
     *
     * @return the decremented counter
     */
    public int decCount() {
//    	System.out.println("Dec count : " + this.count);
        return --this.count;
    }


    /**
     * A worker thread. Calls a regular QX instance but with a different configuration
     * The configuation has to be set before the call by the orchestrator (main thread)
     *
     * @author dietmar
     */
    private class QXThread implements Callable<List<T>> {
        // the current constellation to consider.
        // Has to be set from the coordinating thread.
        //ExquisiteSession session;
        //AbstractHSDagBuilder dagbuilder;
        // The local solver - works with a different situation every time
        QuickXPlain<T> qx = null; // new QuickXPlain(session, dagbuilder);

        @Override
        public List<T> call() {
            if (results.size() < maxConflicts) {
//        		long threadstart = System.currentTimeMillis();
//	        	System.out.println("Starting next thread after " + (threadstart - starttime) + " ms.");
//        		System.out.println("Starting thread ...");
                try {
                    // Find a conflict
                    List<T> conflict = qx.findConflict();
                    if (conflict.size() == 0) {
                        //                	System.out.println("It is not a conflict, done with the work..");
                        qx.constraintListener.release();
                        return null;
                    } else {
                        //                    System.out.println("Found one of size " + conflict.size());
                        //                    System.out.println(Utilities.printConstraintListOrderedByName(conflict, currentDiagnosisModel));
                        // Register the conflict
                        addConflict(conflict);
                        return conflict;
                    }

                } catch (Exception e) {
                    // give up in case of problems
                    e.printStackTrace();
                    return null;
                } /* finally {
                    long threadend = System.currentTimeMillis();
	            	System.out.println("Finished work after " + (threadend - starttime) + " ms (" + (threadend - threadstart) + " ms).");
	            }*/

            }
            qx.constraintListener.release();
            return null;
        }
    }

}
