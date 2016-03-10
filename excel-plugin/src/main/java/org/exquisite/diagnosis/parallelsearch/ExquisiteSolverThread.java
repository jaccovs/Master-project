package org.exquisite.diagnosis.parallelsearch;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;

import java.util.Collections;
import java.util.List;

/**
 * A thread that starts the workers and terminates after the first solution was found
 *
 * @author Arash
 */
public class ExquisiteSolverThread extends Thread {

    private CPModel model;
    private List<SolverThread> workers;
    private ParallelSolver exquisiteParallelSearch;

    /**
     * The constructor takes the cp model to be handed to the workers and the list of workers
     *
     * @param model
     * @param workers
     */
    public ExquisiteSolverThread(ThreadGroup pGroup, CPModel model, ParallelSolver exquisiteParallelSearch) {
        // Set thread group and thread name
        super(pGroup, "ExquisiteSolverThread");

        this.model = model;
        this.exquisiteParallelSearch = exquisiteParallelSearch;
        this.workers = this.exquisiteParallelSearch.workers;
    }

    public void run() {

        // create a child ThreadGroup for parent ThreadGroup
        ThreadGroup cGroup = new ThreadGroup(this.getThreadGroup(), "Child ThreadGroup");
        // daemon status is set to true
        cGroup.setDaemon(true);

        if (this.exquisiteParallelSearch.sessionData.getConfiguration().searchStrategy == null) {
            // Default choco set
            workers.add(
                    new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.Default, exquisiteParallelSearch));
            // ImpactBasedBranching
            workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.ImpactBasedBranching,
                    exquisiteParallelSearch));
            // AssignOrForbidIntVarVal
            workers.add(
                    new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignOrForbidIntVarVal_DomDDegBin,
                            exquisiteParallelSearch));

//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignOrForbidIntVarVal_DomDegBin, exquisiteParallelSearch));
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignOrForbidIntVarVal_DomWDegBin, exquisiteParallelSearch));
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignOrForbidIntVarVal_RandomIntBinSearch, exquisiteParallelSearch));

            // AssignVar
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignVar_DomDDeg, exquisiteParallelSearch));
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignVar_DomDeg, exquisiteParallelSearch));
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignVar_DomWDeg, exquisiteParallelSearch));
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignVar_Lexicographic, exquisiteParallelSearch));
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignVar_MinDomIncDom, exquisiteParallelSearch));
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignVar_MinDomMinVal, exquisiteParallelSearch));
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.AssignVar_RandomIntSearch, exquisiteParallelSearch));
            // DomOverWDegBranchingNew
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.DomOverWDegBranchingNew_IncDomWDeg, exquisiteParallelSearch));
            // DomOverWDegBinBranchingNew
//        	workers.add(new SolverThread(cGroup, new CPSolver(), model, SearchStrategies.DomOverWDegBinBranchingNew_IncDomWDegBin, exquisiteParallelSearch));

            // Shuffle the collection to minimize the order influence
            Collections.shuffle(workers);
        } else {
//        	System.out.println("--------- STRATEGY TO USE: " + this.exquisiteParallelSearch.sessionData.getConfiguration().searchStrategy);
            SolverThread worker = new SolverThread(cGroup, new CPSolver(), model,
                    this.exquisiteParallelSearch.sessionData.getConfiguration().searchStrategy, exquisiteParallelSearch);
            workers.add(worker);
        }

        // Start the workers
        for (SolverThread worker : workers) {
            worker.setDaemon(true);
            worker.start();
        }
        // Wait for a solution or an interrupt
        while ((!ParallelSolver.STOP_SEARCH) && (!exquisiteParallelSearch.solvingFinished) && (!isInterrupted())) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                if (isInterrupted()) {
//					System.out.println("ExquisiteSolverThread (main thread) interrupted!");					
                }
            }
        }
//		System.out.println("Ending ExquisiteSolverThread (main thread)!");
    }

    /**
     * Stopping the work
     */
    @Override
    public void interrupt() {
        this.exquisiteParallelSearch.solvingFinished = true;
        super.interrupt();
    }
}
