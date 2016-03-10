package tests.dj.parallelsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.solver.Solver;
import evaluations.plainconstraints.PlainConstraintsUtilities;

/**
 * To test the parallel execution of Choco in two threads
 * @author dietmar
 *
 */
public class ParallelSolveTester {

	/**
	 * Main program
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ParallelSolveTester tester = new ParallelSolveTester();
			tester.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String inputFileDirectory = "experiments/mutatedconstraints/";
	static String filename = "normalized-ex5-pi.xml";

	/**
	 * Do the work -> Test two solves
	 */
	private void run() throws Exception  {
		System.out.println("Running ..");
		
//		List<Map<String, Integer>> posTestCases = null;
		int runs = 20;
		int maxThreads = 4;
	
		List<CPWorker> workerList = new ArrayList<CPWorker>();
		
		for (int i=0;i<runs;i++) {
			CPWorker worker = new CPWorker();
			workerList.add(worker);
		}
		/**
		 * EXEcUTOR VERSION
		 */

//		ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);
//		threadPool.invokeAll(workerList);
		/**
		 * Non-eXEcUTOR VERSION
		 */
//		for (CPWorker worker : workerList) {
//			worker.start();
//		}
		
		
		// Now outside the thread
		for (int i=0;i<runs;i++) {
			CPModel cpmodel = null;
			try {
				cpmodel = PlainConstraintsUtilities.loadModel(inputFileDirectory + filename);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Solver solver = new CPSolver();
			solver.read(cpmodel);
			
			long start = System.nanoTime();
			solver.solve();
			long stop = System.nanoTime();
			double result = (stop - start)/(double)1000000;
			System.out.println("Time needed outside thread: " + (result));
		}

		System.out.println("Done with all measurements");
//		System.exit(1);
		
	}
	
	
	/**
	 * A worker thread
	 * @author dietmar
	 *
	 */
	class CPWorker extends Thread  implements Callable<String> {
				
		
		@Override
		public void run() {
			for (int i=0;i<5;i++) {
				call();
			}
		}
		
		/*
		 * Do the work
		 */
		@Override
		public String call() {
			return "";
//			System.out.println("Starting thread");
//			// Just solve the problem
//			CPModel cpmodel = null;
//			try {
//				cpmodel = PlainConstraintsUtilities.loadModel(inputFileDirectory + filename);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			boolean CHOCO3 = false;
//			if (CHOCO3) {
//				System.out.println("Solving C3");
//				solver.Solver solver = new solver.Solver();
//				Choco2ToChoco3Solver converter = new Choco2ToChoco3Solver();
//				converter.solver = solver;
//				// Create the variables
//				Iterator<IntegerVariable> varit = cpmodel.getIntVarIterator();
//				while (varit.hasNext()){
//					IntegerVariable v = varit.next();
//					solver.variables.IntVar intvar = VariableFactory.bounded(
//								v.getName(), 
//								v.getLowB(), 
//								v.getUppB(), solver);
//					converter.theVariables.put(v, intvar);
//				}
//				
//				// Create the constraints
//				Iterator<Constraint> ct = cpmodel.getConstraintIterator();
//				while (ct.hasNext()){
//					Constraint c = ct.next();
//					try {
//						solver.constraints.Constraint c3Constraint = converter.buildConstraint(c);
//						solver.post(c3Constraint);
//					} catch (Exception e) {
//						System.out.println(e.getMessage());
//						e.printStackTrace();
//						System.exit(1);
//					}
//				}
				// Maybe use a different heuristic today
//				IntVar[] intvars = new IntVar[converter.theVariables.size()];
				
//				intvars = converter.theVariables.values().toArray(new IntVar[]{});
//				System.out.println("Intavrs size: "  + intvars.length);	
//				AbstractStrategy<IntVar> strategy = null;
//				strategy = IntStrategyFactory.impact(intvars, 100); // 1.5 secs vs 2 
//				strategy = IntStrategyFactory.domOverWDeg(intvars, 100); // endless
//				strategy = IntStrategyFactory.minDom_LB(intvars); // /400

//				IntValueSelector valSelector = null;
//				VariableSelector<IntVar> varSelector = null;
//				
//				valSelector = IntStrategyFactory.min_value_selector();
//				varSelector = IntStrategyFactory.minDomainSize_var_selector();
//				valSelector = IntStrategyFactory.max_value_selector();
//				varSelector = IntStrategyFactory.maxDomainSize_var_selector();
				
//				AbstractStrategy<IntVar> customStrategy = IntStrategyFactory.custom(varSelector, valSelector, intvars);				
//				solver.set(customStrategy);
				
//				solver.set(strategy);
//				
//				long start = System.nanoTime();
//				// Solve
//				boolean solved = solver.findSolution();
//				long stop = System.nanoTime();
//				double result = (stop - start)/(double)1000000;
//				System.out.println("Time needed: " + (result) + " solved: " + solved);
//				System.out.println("Done solving C3");
//			}
//			else {
//				/// CHOCO2 Version
//				Solver solver = new CPSolver();
//				solver.read(cpmodel);
//				
//				long start = System.nanoTime();
//				boolean solved = solver.solve();
//				long stop = System.nanoTime();
//				double result = (stop - start)/(double)1000000;
//				System.out.println("Time needed: " + (result) + " solved: " + solved);
//				
//			}
//			
//			return "";

//			System.out.println("Thread " + this.getId() + " done");
		}
	}

}
