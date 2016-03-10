package tests.parallelsearch;

import java.util.ArrayList;
import java.util.List;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.ImpactBasedBranching;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Testing parallel search for solutions
 * 
 * @author dietmar
 * 
 */
public class ParallelSearchTest {

	// Sync variable (should be in the main thread)
	// Can be set to true when a solution is found
	// or an interrupt message is received by the main thread
	public static boolean STOP_SEARCH = false;
	List<SolverThread> workers = new ArrayList<SolverThread>();

	
	// We could remember the best strategy (or more statistics) and 
	// only start one thread once we know which strategy works for the 
	// current example
	public static int LAST_SUCCESSFUL_STRATEGY = -1;
	
	/**
	 * Main entry point. Starts everything
	 * @param args
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		System.out.println("Testing parallel search.");
		try {
			ParallelSearchTest test = new ParallelSearchTest();
			test.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Calculation ended in " + (System.currentTimeMillis() - start) + " ms.");
		System.out.println("Test ended");
	}

	/**
	 * Main working method. Creates a new thread, which in its run method
	 * starts the different solvers in demon thrads
	 * 
	 * When one solution is found, the thread is over and all demons
	 * are terminated
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception {
		CPModel model = defineCPModel();
		MainThread mainThread = new MainThread(model);
		mainThread.start();
	}
	
	
	/**
	 * A thread that starts the workers and terminates 
	 * after the first solution was found
	 * @author dietmar
	 *
	 */
	class MainThread extends Thread {
		
		/**
		 * The constructor takes the cp model to be handed to the workers
		 * @param model
		 */
		MainThread(CPModel model) {
			this.model = model;
		}
		
		CPModel model;
		
		/**
		 * Doing the work - start the worker threads with different strategies,
		 * mark them as demons and wait until one solution is found.
		 *
		 * The thread could also 
		 *
		 */
		public void run () {
			SolverThread worker1 = new SolverThread(model, 1, "w1", workers);
			SolverThread worker2 = new SolverThread(model, 2, "w2", workers);
			SolverThread worker3 = new SolverThread(model, 2, "w3", workers);
			
			worker1.setDaemon(true);
			worker2.setDaemon(true);
			worker3.setDaemon(true);
			
			workers.add(worker1);
			workers.add(worker2);
			workers.add(worker3);
			
			// Start the workers
			for (SolverThread worker : workers) {
				worker.start();
			}
			
			// Wait for a solution or an interrupt
			while (!ParallelSearchTest.STOP_SEARCH) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					System.out.println("Main thread interrupted");
					// End the thread
					ParallelSearchTest.STOP_SEARCH = true;
				}
			}
			System.out.println("Ending main thread");
		}
		
		/**
		 * Stopping the work
		 */
		@Override
		public void interrupt() {
			System.out.println("Interrupt called to main " );
			ParallelSearchTest.STOP_SEARCH = true;
		}

	}

	/**
	 * A worker class which implements one strategy
	 * @author dietmar
	 *
	 */
	class SolverThread extends Thread {
		int strategy = 1;
		String name;
		CPModel model;
		CPSolver solver;
		List<SolverThread> workers;
		
		/**
		 * Create the thread with the appropriate strategy
		 * @param m
		 * @param strategy
		 */
		public SolverThread(CPModel m, int strategy, String name, List<SolverThread> workers) {
			System.out.println("Created worker.." + name);
			this.workers = workers;
			this.name = name;
			this.strategy = strategy;
			this.model = m;
			solver = new CPSolver();
			// Switch the strategies here
			if (strategy == 1) {
				solver.addGoal(new ImpactBasedBranching(solver));
			}
			solver.read(m);
		}
		
		
		/**
		 * Does the main work
		 */
		@Override
		public void run() {
			System.out.println("Running run() method (worker: " + name + ")");
			// if someone else was not ready already before we could even start..
			if (ParallelSearchTest.STOP_SEARCH == false) {
				// Start the work
				// DEBUG Add some extra time to test and debug things
				/// -------------- DEBUG --------
				int sleeptime = 1000;
				if (strategy != 1) {
					// Let's countdown from 3
					int cnt = 3;
					while (cnt > 0) {
						try {
							Thread.sleep(sleeptime);
						} catch (InterruptedException e) {
							System.out.println("Interrupted: " + name);
						}
						System.out.println(name + ":" + cnt);
						cnt--;
					}
				} else {
					// Only sleep one sec.
					try {
						Thread.sleep(sleeptime);
					} catch (InterruptedException e) {
						System.out.println("Interrupted strategy 2");
					}
				}
				// -------------- THE REAL WORK
				System.out.println("Starting work " + name);
				solver.solve();
				// Found a solution
				if (solver.isFeasible()) {
					// Remember that this strategy was the best here?
					ParallelSearchTest.STOP_SEARCH  = true;
					System.out.println("Solver finished: " + name);
				}
			}

		}
	}
	
	/**
	 * Define a non-trivial problem, e.g. magic squares Taken from the Choco
	 * documentation
	 * 
	 * @throws Exception
	 */
	public CPModel defineCPModel() throws Exception {
		CPModel m = new CPModel();
		int n = 7; // Order of the magic square
		int magicSum = n * (n * n + 1) / 2; // Magic sum

		// Creation of an array of variables
		IntegerVariable[][] var = new IntegerVariable[n][n];
		// For each variable, we define its name and the boundaries of its
		// domain.
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				var[i][j] = Choco.makeIntVar("var_" + i + "_" + j, 1, n * n);
				// Associate the variable to the model.
				m.addVariable(var[i][j]);
			}
		}
		// The constraints
		// All cells of the matrix must be different
		for (int i = 0; i < n * n; i++) {
			for (int j = i + 1; j < n * n; j++) {
				Constraint c = (Choco.neq(var[i / n][i % n], var[j / n][j % n]));
				m.addConstraint(c);
			}
		}
		// All row's sum has to be equal to the magic sum
		for (int i = 0; i < n; i++) {
			m.addConstraint(Choco.eq(Choco.sum(var[i]), magicSum));
		}
		IntegerVariable[][] varCol = new IntegerVariable[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				// Copy of var in the column order
				varCol[i][j] = var[j][i];
			}
			// All column's sum is equal to the magic sum
			m.addConstraint(Choco.eq(Choco.sum(varCol[i]), magicSum));
		}

		IntegerVariable[] varDiag1 = new IntegerVariable[n];
		IntegerVariable[] varDiag2 = new IntegerVariable[n];
		for (int i = 0; i < n; i++) {
			varDiag1[i] = var[i][i]; // Copy of var in varDiag1
			varDiag2[i] = var[(n - 1) - i][i]; // Copy of var in varDiag2
		}
		// All diagonal's sum has to be equal to the magic sum
		m.addConstraint(Choco.eq(Choco.sum(varDiag1), magicSum));
		m.addConstraint(Choco.eq(Choco.sum(varDiag2), magicSum));
		return m;
	}
}
