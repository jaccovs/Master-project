package org.exquisite.diagnosis.parallelsearch;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.BranchingFactory;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.branching.ImpactBasedBranching;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;

/**
 * A worker class which implements one strategy
 * 
 * @author Arash 
 *
 */
public class SolverThread extends Thread {

	private SearchStrategies strategy = SearchStrategies.Default;
	private CPModel model;
	private CPSolver solver;
	private ParallelSolver exquisiteParallelSearch;
	
	/**
	 * Create the thread with the appropriate strategy
	 * @param m
	 * @param strategy
	 */
	public SolverThread(	
			ThreadGroup cGroup, 
			CPSolver solver, 
			CPModel m, 
			SearchStrategies strategy, 
			ParallelSolver exquisiteParallelSearch) {
		// Set thread group and thread name
		super(cGroup, "SolverThread");
		
		this.strategy = strategy;
		this.model = m;
		this.exquisiteParallelSearch = exquisiteParallelSearch;		
		this.solver = solver;
		
//		System.out.println("------------------ TRYING STRATEGY: " + strategy);
		
		solver.read(this.model);
		// Switch the strategies here
		if(this.strategy != SearchStrategies.Default)
		{
			SetSearchStrategy(strategy);
		}
	}	
	
	/**
	 * Does the main work
	 */
	@Override
	public void run() {		
		// if someone else was not ready already before we could even start..
		if ((!ParallelSolver.STOP_SEARCH) && (!exquisiteParallelSearch.solvingFinished)){
			
			try {
				//this.exquisiteParallelSearch.nbSolves++;
				solver.propagate();				
			} catch (Exception e) {
				if(!ParallelSolver.STOP_SEARCH)
				{
					// synchronized access to the object exquisiteParallelSearch
					synchronized (exquisiteParallelSearch){
						exquisiteParallelSearch.isFeasible = false;
						exquisiteParallelSearch.solvingFinished = true;	
						//e.printStackTrace();
					}
				}
			}			
			// Call solve()
			try {
				//this.exquisiteParallelSearch.nbSolves++;
//				ChocoLogging.toVerbose();
				solver.solve();	
//				ChocoLogging.flushLogs();
//				System.out.println("STRATEGY: " + strategy + " ----- BACKTRACKING COUNT: " + solver.getBackTrackCount());
			} catch (Exception e) {
				if(!ParallelSolver.STOP_SEARCH)
				{
					// synchronized access to the object exquisiteParallelSearch
					synchronized (exquisiteParallelSearch){
						exquisiteParallelSearch.isFeasible = false;
						exquisiteParallelSearch.solvingFinished = true;	
						//e.printStackTrace();
					}
				}
				//System.out.println("Exception here, " + e.getMessage());
			}
			
			synchronized (exquisiteParallelSearch){
				// Found a solution
				try {
					if (solver.isFeasible()) 
					{
						if ((!ParallelSolver.STOP_SEARCH) && (!exquisiteParallelSearch.solvingFinished))
						{
							// Remember that this strategy was the best here?
							exquisiteParallelSearch.isFeasible = true;
							exquisiteParallelSearch.solvingFinished = true;
							exquisiteParallelSearch.lastSuccessfulStrategy = this.strategy;
							// System.out.println("Solver finished: " + this.strategy);
						}
					}
					else
					{
						if ((!ParallelSolver.STOP_SEARCH) && (!exquisiteParallelSearch.solvingFinished))
						{
							exquisiteParallelSearch.isFeasible = false;
							exquisiteParallelSearch.solvingFinished = true;
						}
					}
					
				} catch (Exception e) {
					if ((!ParallelSolver.STOP_SEARCH) && (!exquisiteParallelSearch.solvingFinished))
					{
						exquisiteParallelSearch.isFeasible = false;
						exquisiteParallelSearch.solvingFinished = true;
					}
				}				
			}
		}
	}
	
	/**
	 * @return the solver
	 */
	public CPSolver getSolver() {
		return solver;
	}

	/**
	 * @param solver the solver to set
	 */
	public void setSolver(CPSolver solver) {
		this.solver = solver;
	}
	

	
	private void SetSearchStrategy(SearchStrategies newStrategy){
		// Clear current goals in solver
		this.solver.clearGoals();
		
		// Switch the search strategies here
		// AssignOrForbidIntVarVal
		switch (newStrategy) {
		case AssignOrForbidIntVarVal_DomDegBin:
			solver.addGoal(BranchingFactory.domDegBin(solver));						
			break;
		case AssignOrForbidIntVarVal_DomDDegBin:
			solver.addGoal(BranchingFactory.domDDegBin(solver));					
			break;
		case AssignOrForbidIntVarVal_DomWDegBin:
			solver.addGoal(BranchingFactory.domWDegBin(solver));					
			break;
		case AssignOrForbidIntVarVal_RandomIntBinSearch:
			solver.addGoal(BranchingFactory.randomIntBinSearch(solver, Double.doubleToLongBits(Math.random())));						
			break;
		// AssignVar
		case AssignVar_DomDeg:
			solver.addGoal(BranchingFactory.domDeg(solver));						
			break;
		case AssignVar_DomDDeg:
			solver.addGoal(BranchingFactory.domDDeg(solver));						
			break;
		case AssignVar_DomWDeg:
			solver.addGoal(BranchingFactory.domWDeg(solver));						
			break;
		case AssignVar_Lexicographic:
			solver.addGoal(BranchingFactory.lexicographic(solver));						
			break;
		case AssignVar_MinDomIncDom:
			solver.addGoal(BranchingFactory.minDomIncDom(solver));					
			break;
		case AssignVar_MinDomDecDom:
			solver.addGoal(new AssignVar( new MinDomain(solver), new DecreasingDomain() ));
			break;
		case AssignVar_MinDomMinVal:
			solver.addGoal(BranchingFactory.minDomMinVal(solver));						
			break;
		case AssignVar_RandomIntSearch:
			solver.addGoal(BranchingFactory.randomIntSearch(solver, Double.doubleToLongBits(Math.random())));						
			break;
		// DomOverWDegBranchingNew
		case DomOverWDegBranchingNew_IncDomWDeg:
			solver.addGoal(BranchingFactory.incDomWDeg(solver));					
			break;
		// DomOverWDegBinBranchingNew
		case DomOverWDegBinBranchingNew_IncDomWDegBin:
			solver.addGoal(BranchingFactory.incDomWDegBin(solver));						
			break;
		// ImpactBasedBranching
		case ImpactBasedBranching:
			solver.addGoal(new ImpactBasedBranching(solver));						
			break;		

		default:
			break;
		}
	}
}
