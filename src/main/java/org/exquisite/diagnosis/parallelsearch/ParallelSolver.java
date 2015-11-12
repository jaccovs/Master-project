/**
 * 
 */
package org.exquisite.diagnosis.parallelsearch;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.exquisite.data.ConstraintsFactory;
import org.exquisite.data.DiagnosisModelLoader;
import org.exquisite.data.VariablesFactory;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

/**
 * Parallel search for solutions
 * 
 * @author Arash 
 *
 */
public class ParallelSolver {
	
	// A pointer to the DiagnosisEngine
	public AbstractHSDagBuilder dagBuilder = null;
	
	// Sync variable (should be in the main thread)
	// Can be set to true when a solution is found
	// or an interrupt message is received by the main thread
	public static volatile boolean STOP_SEARCH = false;	
	//
	public boolean isFeasible = false;
	
	public boolean solvingFinished = false;
	// List of SolverThreads
	public List<SolverThread> workers;
	
//	//stores metrics object of last successful strategy.
//	public Metrics metrics = new Metrics();
	
	// We could remember the best strategy (or more statistics) and 
	// only start one thread once we know which strategy works for the 
	// current example
	public SearchStrategies lastSuccessfulStrategy = SearchStrategies.Default;
	
	public CPModel model;
	
	public ExquisiteSession sessionData;

//	/**
//	 * 
//	 */
//	public ParallelSolver(ExquisiteSession sessionData){
//		this.sessionData = sessionData;
//		this.workers = new ArrayList<SolverThread>();
//		this.model = new CPModel();		
//	}
	

	/**
	 * Creates a new parallel solver
	 * @param model the prepared cpmodel
	 * @param sessionData the session data
	 * @param dagbuilder a pointer to the dagbuilder
	 */
	public ParallelSolver(CPModel model, ExquisiteSession sessionData, AbstractHSDagBuilder dagbuilder){
		this.dagBuilder = dagbuilder;
		this.sessionData = sessionData;		
		this.model = model;
		workers = new ArrayList<SolverThread>();
	}
	
	public void run() throws Exception {
		int timeOut = this.sessionData.config.timeOut;
		this.isFeasible = false;
		this.solvingFinished = false;
		
		// create a parent ThreadGroup
        ThreadGroup pGroup = new ThreadGroup("Parent ThreadGroup");    
        pGroup.setDaemon(true);
		
		ExquisiteSolverThread mainThread = new ExquisiteSolverThread(pGroup, this.model, this);
		mainThread.start();
		
		long endTime = System.currentTimeMillis() + timeOut;
		long currentTime = System.currentTimeMillis();
		
		while (!solvingFinished)
		{
			if ((currentTime > endTime) || (ParallelSolver.STOP_SEARCH))
			{
				this.solvingFinished = true;
				for (SolverThread worker : workers) {
					try {
						// We should also catch all log stuff on system error etc.
						worker.getSolver().clear();
					}
					catch (Exception e) {
						System.out.println("Forcefully stopped my solver with an issue");
					}
				}
				this.isFeasible = false;
			}
			currentTime = System.currentTimeMillis();
			Thread.sleep(5);
		}
		// Kill all running threads after finish solving
		for (SolverThread worker : workers) {
			try {
				// We should also catch all log stuff on system error etc.
				worker.getSolver().clear();
			}
			catch (Exception e) {
				System.out.println("Forcefully stopped!");
			}
		}
		mainThread.interrupt();
	}
	
	/**
	 * Simulates solver.solve() and runs different parallel strategies - the fastest one wins
	 * @return true if feasible, false if not feasible or solving takes more time than time out
	 */
	public boolean solve()
	{
		try {
			// if we know what to do, no need for parallel solvers
			// otherwise run() parallel strategies until we know the fastest one
			// DJ: Switch that off for the moment
			if (false && sessionData.config.searchStrategy == null) {
//				System.out.println("using parallel search");
				run();
			}
			else {
				// Do what has to be done locally and fast.
//				System.out.println("Non-parallel solver used: " + sessionData.config.searchStrategy.Default);
				Solver solver = new CPSolver();
				solver.read(this.model);
				try {
					//this.exquisiteParallelSearch.nbSolves++;
					if (this.dagBuilder != null) {
						this.dagBuilder.incrementPropagationCount();
					}
					solver.propagate();
					// Catch a contradiction exception
				} catch (Exception e) {
//					System.out.println("Contradiction..");
					return false;
				}			
				// Call solve()
				try {
					if (this.dagBuilder != null) {
						this.dagBuilder.incrementSolverCalls();
					}
					
					solver.solve();
					boolean isFeasible =  solver.isFeasible();
					if (this.dagBuilder != null && isFeasible) {
						this.dagBuilder.incrementCSPSolutionCount();
					}
//					System.out.println("Feasible: " + isFeasible);
					return solver.isFeasible();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception here, " + e.getMessage());
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("Returning this.isFeasible");
		return this.isFeasible;
	}

	/**
	 * Testing parallel searching
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final String appXMLFile = ".\\experiments\\enase-2013\\karinscorpus_VDEPPreserve_TC_no_division.xml";
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(appXMLFile);
		
		ExquisiteSession sessionData = new ExquisiteSession(appXML);
		ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
		Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
		VariablesFactory varFactory = new VariablesFactory(variablesMap);
		DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
		modelLoader.loadDiagnosisModelFromXML();	
		
		ParallelSolver test = new ParallelSolver(null, sessionData, null);
		test.model = makeCPModel(sessionData.diagnosisModel);	
		long start = System.currentTimeMillis();
		
		
		
		System.out.println("Testing parallel search.");
		
		
		boolean hasSolution = test.solve();
		
		System.out.println("Calculation ended in " + (System.currentTimeMillis() - start) + " ms.");
		System.out.println("Test ended and has solution: " + hasSolution);
	}
	
	/**
	 * Makes a CP-Model from tests.diagnosis model (for testing)
	 * 
	 * @param diagModel
	 * @return
	 */
	private static CPModel makeCPModel(DiagnosisModel diagModel)
	{
		CPModel model = new CPModel();
		addVariables(model, diagModel.getVariables());
		addConstraints(model, diagModel.getPossiblyFaultyStatements());
		
		//value bounds!!!
		/*for(int i=0; i<diagModel.getCorrectStatements().size();i++)
		{
			Constraint c = diagModel.getCorrectStatements().get(i);
			cpmodel.addConstraint(c);
		}*/
		
		//example constraints
		Example example = diagModel.getPositiveExamples().get(0);
		for(int i=0; i< example.constraints.size();i++)
		{			
			Constraint c = example.constraints.get(i);
			model.addConstraint(c);
		}
		
		return model;
	}
	
	/**
	 * Adds a list of variables to the model (for testing)
	 * 
	 * @param model
	 * @param list
	 */
	private static void addVariables(CPModel model, List<Variable> list)
	{
		choco.kernel.model.variables.Variable[] variablesToAdd = new IntegerVariable[list.size()];
		int index=0;
		for(choco.kernel.model.variables.Variable variable : list)
		{
			
			variablesToAdd[index] = variable;
			index++;
		}
		model.addVariables(variablesToAdd);			
	}
	
	/**
	 * Adds a list of constraints to the model (for testing)
	 * 
	 * @param model
	 * @param list
	 */
	private static void addConstraints(CPModel model, List<Constraint> list)
	{
		for(int i=0; i< list.size();i++)
		{			
			Constraint c = list.get(i);
			model.addConstraint(c);
		}
	}

}
