package tests.dj.othertests;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

/**
 * Some minor tests
 * @author dietmar
 *
 */
public class ThreadTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadTest test = new ThreadTest();
		test.doTest();
		while(true)
		{
			if(Thread.currentThread().isInterrupted())
				break;
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Program ended");
	}


	/**
	 * The main work
	 */
	public void doTest() {
		try {
			Solver solver = new CPSolver();
			WorkerThread t = new WorkerThread(solver);
			t.start();
			Thread.sleep(20);
			System.out.println("Main Woke up");
			try {
				// We should also catch all log stuff on system error etc.
				solver.clear();
			}
			catch (Exception e) {
				System.out.println("Forcefully stopped my solver with an issue");
			}
			System.out.println("Manipulated solver");
			t.join();
			System.out.println("Done");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	class WorkerThread extends Thread {
		Solver solver;
		
		/**
		 * Remember the thing
		 * @param solver
		 */
		WorkerThread(Solver solver) {
			this.solver = solver;
			try {
				CPModel model = defineCPModel();
				solver.read(model);
			} catch (Exception e) {
				System.out.println("Model generation issue");
//				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				long start = System.currentTimeMillis();
				System.out.println("Running my thread - going to solve");
				solver.solve();
				System.out.println("Solved, time: " + (System.currentTimeMillis() - start));
			} catch (Exception e) {
				System.out.println("Exception here, " + e.getMessage());
//				e.printStackTrace();
			}
		}
		
		/**
		 * Stopping the work
		 */
		@Override
		public void interrupt() {
			System.out.println("Interrupt called ....from outside" );
		}

	}
	
	/**
	 * Define a non-trivial problem, e.g. magic squares Taken from the Choco
	 * documentation
	 * 
	 * @throws Exception
	 */
	public static CPModel defineCPModel() throws Exception {
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
