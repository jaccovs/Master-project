package tests.dj.qxsim;

import choco.kernel.model.constraints.Constraint;
import evaluations.tools.DiagnosisEvaluation;
import evaluations.tools.ResultsLogger;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.*;
import org.exquisite.core.engines.AbstractHSDagEngine.QuickXplainType;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.io.File;
import java.util.*;

import static org.exquisite.core.measurements.MeasurementManager.COUNTER_CSP_SOLUTIONS;
import static org.exquisite.core.measurements.MeasurementManager.COUNTER_PROPAGATION;
import static org.exquisite.core.measurements.MeasurementManager.getCounter;


/**
 * Do some tests with artificial examples
 * @author dietmar
 *
 */
public class DiagnosisSimulator  {

	// store to file system
	static boolean persistent = true;

	// ----------------------------------------
	// params
	// constraints
//	public int nbConstraints_lb = 50;
//	public int nbConstraints_ub = 200;
//	public int nbConstraints_step = 50;
//	// conflicts
//	public int nbConflicts_lb = 10;
//	public int nbConflicts_ub = 50;
//	public int nbConflicts_step = 10;
//	// nodeLabel size
//	public int conflictSize_lb = 3;
//	public int conflictSize_ub = 12;
//	public int conflictSize_step = 3;
//	// wait time
//	public int waitTime_lb = 10;
//	public int waitTime_ub = 110;
//	public int waitTime_step = 50;
	
	// TS: Settings, where FP and hybrid are faster than heuristic
//	public int nbConstraints_lb = 50;
//	public int nbConstraints_ub = 50;
//	public int nbConstraints_step = 10;
//	// conflicts
//	public int nbConflicts_lb = 5;
//	public int nbConflicts_ub = 5;
//	public int nbConflicts_step = 10;
//	// nodeLabel size
//	public int conflictSize_lb = 4;
//	public int conflictSize_ub = 4;
//	public int conflictSize_step = 3;
//	// wait time
//	public int waitTime_lb = 100;
//	public int waitTime_ub = 100;
//	public int waitTime_step = 50;

	// TS: Settings, where Hybrid is faster than FP and heuristic
//	public int nbConstraints_lb = 150;
//	public int nbConstraints_ub = 150;
//	public int nbConstraints_step = 10;
//	// conflicts
//	public int nbConflicts_lb = 25;
//	public int nbConflicts_ub = 25;
//	public int nbConflicts_step = 10;
//	// nodeLabel size
//	public int conflictSize_lb = 3;
//	public int conflictSize_ub = 3;
//	public int conflictSize_step = 3;
//	// wait time
//	public int waitTime_lb = 10;
//	public int waitTime_ub = 10;
//	public int waitTime_step = 50;
	// store different files for each run to have randomization, but better comparability from sequential to parallelized versions
	static boolean differentModelsForRuns = true;
	// Simulation params
	static int SEARCH_DEPTH = -1;
	static int MAX_DIAGS = 1;
	// Runs will be added for initialization
	static int nbInitizializationRuns = 5;
	static int nbTestRuns = 100;
	static int threadPoolSize = 2;
	// test different nb of threads for heuristic search
	static int[] threadsForHeuristicTest = new int[]{1, 2, 4, 6, 8, 12, 16};
	// how to generate
	static ConflictGenerationStrategy_Size genStrategySize = ConflictGenerationStrategy_Size.gaussian;
	static ConflictGenerationStrategy_VarDistribution genStrategyDist = ConflictGenerationStrategy_VarDistribution.gaussian;
	static String separator = ";";
	public int nbConstraints_lb = 150;
	public int nbConstraints_ub = 150;
	public int nbConstraints_step = 10;
	// conflicts
	public int nbConflicts_lb = 25;
	public int nbConflicts_ub = 25;
	public int nbConflicts_step = 10;
	// nodeLabel size
	public int conflictSize_lb = 3;
	public int conflictSize_ub = 3;
	public int conflictSize_step = 3;
	// wait time
	public int waitTime_lb = 10;
	public int waitTime_ub = 10;
	public int waitTime_step = 50;
	// where to store
	public String filename = "experiments/QXSim/storedconflicts";

	// ----------------------------------------
	public String resultsFilename = "experiments/QXSim/results";
	ResultsLogger resultsLogger = null;
	// Cache the constraint names
	Map<Constraint, String> constraintNames = new HashMap<Constraint, String>();
	List<List<Constraint>> conflicts = new ArrayList<List<Constraint>>();
	// the random gaussian
	RandomGaussian randomGaussian;
	// A copy of the constraints
	List<Constraint> shuffledConstraints = null;

	/**
	 * Main entry point
	 * @param args none
	 *
	 */
	public static void main(String[] args) {
		try {
			Debug.DEBUGGING_ON = false;
			System.out.println("-- Program starting");
			AbstractHSDagEngine.USE_QXTYPE = QuickXplainType.SimulatedQuickXplain;
			// Start the work
			DiagnosisSimulator sim = new DiagnosisSimulator();

//			sim.runSimulations(ExecutionMode.singlethreaded);
//			sim.runSimulations(ExecutionMode.levelparallel);
			sim.runSimulations(ExecutionMode.fullparallel);
			sim.runSimulations(ExecutionMode.heuristic);
			sim.runSimulations(ExecutionMode.hybrid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("-- Program terminated");

	}

	/**
	 * Clears all data so that a new model can be created.
	 */
	void clearModel() {
		conflicts.clear();
		constraintNames.clear();
		shuffledConstraints = null;
	}

	/**
	 * Creates a new diagnosis model
	 * @param nbConstraints   number of constraints to create
	 * @param nbConflicts   number of conflicts to create
	 * @param conflictSize   average size of conflicts
	 * @param run   the number of the run (for storage)
	 * @return   the defined model
	 * @throws Exception
	 */
	DiagnosisModel<Constraint> defineModel(int nbConstraints, int nbConflicts, int conflictSize, int run)
			throws Exception {
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();

		// Create a set of conflicts
		createConflicts(nbConstraints, nbConflicts, conflictSize, run);
		// Remember them globally
		QXSim.conflicts = this.conflicts;
		QXSim.constraintNames = this.constraintNames;

		// feed the diagnosis model
		for (Constraint c : this.constraintNames.keySet()) {
			model.addPossiblyFaultyConstraint(c, this.constraintNames.get(c));
		}

		// Add a dummy example
		Example exTestExample = new Example();
		exTestExample.addConstraint(SimUtilities.createDummyConstraint(), "dummyexample");
		model.getConsistentExamples().add(exTestExample);

//		System.out.println("Known conflicts");
//		for (List<Constraint> nodeLabel : conflicts) {
//			System.out.println(conflictToString(this.constraintNames, nodeLabel));
//		}
		return model;
	}

	/**
	 * Creates and returns a list of conflicts.
	 * @param nbConstraints   number of constraints to create
	 * @param nbConflicts   number of conflicts to create
	 * @param conflictSize   average size of conflicts
	 * @param run   the number of the run (for storage)
	 * @throws Exception
	 */
	void createConflicts(int nbConstraints, int nbConflicts, int conflictSize, int run) throws Exception {

		// Different strategies.
		// Strategy one: All constraints are of equal size
		// Strategy two: Vary the size of the conflicts -> make sure we have no subsets
		// Strategy three to 100: Vary the distribution of

		// Read from file system or not
		if (persistent) {
			this.constraintNames.clear();
			this.conflicts.clear();
			File f = new File(getFullFileName(nbConstraints, nbConflicts, conflictSize, run));
			if (f.exists()) {
				System.out.print("L ");
				SimUtilities.loadConstraintsFromFile(f, this.constraintNames, this.conflicts);
				return;
			}
		}


		System.out.print("C ");
		// Create a set of constraints (all dummy)
		createConstraints(nbConstraints);
//		System.out.println("Constraint names: " + this.constraintNames.values());
		//


		// Determine the size of each nodeLabel to be generated.
		int[] conflictSizes = new int[nbConflicts];
		if (genStrategySize == ConflictGenerationStrategy_Size.equal) {
			for (int i=0;i<nbConflicts;i++) {
				conflictSizes[i] = conflictSize;
			}
		}
		else {
			// Get some distribution of values
			RandomGaussian randomGaussian = new RandomGaussian(
					conflictSize, // The mean
					conflictSize / 2,  // half of the mean value
													2,  // double conflicts down there
													(conflictSize-2)+conflictSize); // upper bound at same distance than lower bound
			for (int i=0;i<nbConflicts;i++) {
				conflictSizes[i] = (int) randomGaussian.getGaussian();
			}

		}

		int cnt=0;
		// while not enough conflicts
		while (this.conflicts.size() < nbConflicts) {
			List<Constraint> oneConflict = new ArrayList<Constraint>();
			// Shuffle the list and pick the first n

			if (genStrategyDist == ConflictGenerationStrategy_VarDistribution.equal) {
				oneConflict = getConflictEqualVarDistribution(conflictSizes[cnt]);
			}
			else {
				oneConflict = getConflictGaussianVarDistribution(conflictSizes[cnt]);
			}

			if (!Utilities.isSubsetOfKnownConflict(oneConflict, this.conflicts)) {
				cnt++;
				this.conflicts.add(oneConflict);
			}
			else {
//				System.out.println("dup found..");
			}

					}

		// some testing
//		Map<Constraint, Integer> constraintCount = new HashMap<Constraint, Integer>();
//		System.out.println("Known conflicts");
//		for (List<Constraint> nodeLabel : conflicts) {
//			System.out.println(conflictToString(this.constraintNames, nodeLabel));
//			for (Constraint c : nodeLabel) {
//				Integer cCount = constraintCount.get(c);
//				if (cCount == null){
//					constraintCount.put(c, 1);
//				}
//				else {
//					constraintCount.put(c, cCount+1);
//
//				}
//			}
//		}
//		System.out.println("Constraint distribution " + constraintCount);

		// Write things to a text file
		if (persistent) {
			SimUtilities.writeConstraintsToFile(getFullFileName(nbConstraints, nbConflicts, conflictSize, run), this.constraintNames, this.conflicts);
		}
//		if (true) System.exit(0);

	}
	
	/**
	 * Returns a list of constraints of a given size randomly and uniformly from all constraints
	 * @return
	 */
	List<Constraint> getConflictEqualVarDistribution(int size) {
		List<Constraint> constraints = new ArrayList<Constraint>(this.constraintNames.keySet());
		Collections.shuffle(constraints);
		List<Constraint> result = new ArrayList<Constraint>();
		for (int i=0;i<size;i++) {
			result.add(constraints.get(i));
		}
		return result;
	}

	/**
	 * Returns a list of constraints of a given size randomly chosen from all constraints , gaussian
	 * @return
	 */
	List<Constraint> getConflictGaussianVarDistribution(int size) {
		if (shuffledConstraints == null) {
			shuffledConstraints = new ArrayList<Constraint>(this.constraintNames.keySet());
			// do an initial shuffle
			Collections.shuffle(shuffledConstraints);
			randomGaussian = new RandomGaussian(
					this.constraintNames.keySet().size(),
					this.constraintNames.keySet().size() / 3,
					0,
									this.constraintNames.keySet().size()-1);
		}
		List<Constraint> result = new ArrayList<Constraint>();
		while (result.size() < size) {
			int nextVar = (int) randomGaussian.getGaussian();
			Constraint c = shuffledConstraints.get(nextVar);
			if (!result.contains(c)) {
				result.add(c);
			}
		}

		return result;
	}
	
	/**
	 * Creates a file name with some details encoded into it.
	 * @param nbConstraints   number of constraints to create
	 * @param nbConflicts   number of conflicts to create
	 * @param conflictSize   average size of conflicts
	 * @param run   the number of the run
	 * @return filename + nbcts + nbconflits + avg-nodeLabel size + run
	 */
	String getFullFileName(int nbConstraints, int nbConflicts, int conflictSize, int run) {
		String result = this.filename;
		result += "-" + nbConstraints + "-" + nbConflicts + "-" + conflictSize;
		if (differentModelsForRuns) {
			result += "_" + run;
		}
		result += ".txt";
		return result;
	}
	
	/**
	 * Creates a result file name with some details encoded into it
	 * @param nbConstraints   number of constraints to create
	 * @param nbConflicts   number of conflicts to create
	 * @param conflictSize   average size of conflicts
	 * @param waitTime   the artificial wait time for QuickXplain
	 * @returnresultfilename + nbcts + nbconflits + avg-nodeLabel size + waittime
	 */
	String getFullResultFileName(int nbConstraints, int nbConflicts, int conflictSize, int waitTime) {
		String result = resultsFilename;
		result += "-" + nbConstraints + "-" + nbConflicts + "-" + conflictSize + "-" + waitTime + ".csv";
		return result;

	}

	/**
	 * Create and return a list of constraints based on the parameters of the instance.
	 *  Store the names somwehere in the instance
	 * @return a list of dummy constraints
	 */
	void createConstraints(int nbConstraints) {
		for (int i=0;i<nbConstraints;i++) {
			Constraint c = SimUtilities.createDummyConstraint();
			this.constraintNames.put(c,"C" + i);
		}

	}
	
	/**
	 * Worker, does the main work.
	 *
	 * @throws Exception
	 */
	private void runSimulations(ExecutionMode executionMode) throws Exception {
		int nbRuns = nbInitizializationRuns + nbTestRuns;

		for (int nbConstraints = nbConstraints_lb; nbConstraints <= nbConstraints_ub; nbConstraints += nbConstraints_step)
		{
			for (int nbConflicts = nbConflicts_lb; nbConflicts <= nbConflicts_ub; nbConflicts += nbConflicts_step)
			{
				for (int conflictSize = conflictSize_lb; conflictSize <= conflictSize_ub; conflictSize += conflictSize_step)
				{
					for (int waitTime = waitTime_lb; waitTime <= waitTime_ub; waitTime += waitTime_step) {
						System.out.println("= Running tests for " + executionMode.toString() + " with nbConstraints: " + nbConstraints + ", nbConflicts: " + nbConflicts + ", conflictSize: " + conflictSize + ", waitTime: " + waitTime +" =");

						// Create the logger
						resultsLogger = new ResultsLogger(";", "", "", getFullResultFileName(nbConstraints, nbConflicts, conflictSize, waitTime));
						StringBuilder summary = new StringBuilder();

						QXSim.ARTIFICIAL_WAIT_TIME = waitTime;


						System.out.println("------------------------");
						System.out.println("Starting to diagnose");

						// Remember the total times
//						double totalSeq = 0;
//						double totalLevelW = 0;
//						double totalFullP = 0;

						List<Diagnosis<Constraint>> diagnoses = null;

						DiagnosisEvaluation<Constraint> diagEval = new DiagnosisEvaluation<Constraint>();

						boolean initFinished = false;
						System.out.println("Initialization");

						// Write a separator line
						resultsLogger.writeSeparatorLine("");
						resultsLogger.writeSeparatorLine(executionMode.toString());

						// Give it an extra run (first one will not be counted
						for (int i = 0; i < nbRuns; i++) {
							if (i == nbInitizializationRuns) {
								initFinished = true;
								System.out.println("Initialization finished");
							}

							System.out.print(i);

							clearModel();

							ConstraintsQuickXPlain.reuseCount = 0;

							IDiagnosisEngine<Constraint> engine = null;
							// Create the diagnosis model
							ExcelExquisiteSession sessionData = new ExcelExquisiteSession();
							sessionData.getDiagnosisModel() = defineModel(nbConstraints, nbConflicts, conflictSize, i);
//							System.out.println("Created the model with examples");
//								AbstractHSDagEngine hsdag;

							// Do not try to find a better strategy for the moment
							sessionData.getConfiguration().searchStrategy = SearchStrategies.Default;

							switch (executionMode) {
							case singlethreaded:
								engine = new HSDagEngine(sessionData);
								break;

								case levelparallel:
								engine = new ParallelHSDagEngine(sessionData, threadPoolSize);
								break;

								case fullparallel:
								engine = new FullParallelHSDagEngine(sessionData, threadPoolSize);
								break;

								case heuristic:
								engine = new HeuristicDiagnosisEngine(sessionData, threadPoolSize);
								break;

								case hybrid:
								engine = new HybridEngine(sessionData, threadPoolSize);
								break;

							default:
								break;
							}


							((AbstractHSDagEngine)engine).setSearchDepth(SEARCH_DEPTH);
							engine.getDiagnosisModel().getConfiguration().searchDepth = SEARCH_DEPTH;
							engine.getDiagnosisModel().getConfiguration().maxDiagnoses = MAX_DIAGS;

							// Shuffle to factor out random effects
//							QXSim.shuffleConflicts();

							long start = System.currentTimeMillis();
							diagnoses = engine.calculateDiagnoses();
							long end = System.currentTimeMillis();


							if (initFinished) {
								diagEval.analyzeRun(end - start);
								diagEval.engineTest((AbstractHSDagEngine<Constraint>) engine);
								// record data for this run.
								String loggingResult = "";
								int varCount = 0;
								loggingResult += varCount + separator;
								loggingResult += sessionData.getDiagnosisModel()
										.getConstraintNames().size() + separator;
								loggingResult += getCounter(COUNTER_PROPAGATION).value();
								loggingResult += getCounter(COUNTER_CSP_SOLUTIONS).value() + separator;
								loggingResult += (end - start) + separator;
								loggingResult += sessionData.getConfiguration().searchDepth
										+ separator;

								for (int j = 0; j < diagnoses.size(); j++) {
									// loggingResult+="(Diag # " + j + ": " +
									// Utilities.printConstraintList(diagnoses.get(j).getElements(),
									// sessionData.getDiagnosisModel()) + ") ";
								}
								loggingResult += Utilities.printSortedDiagnoses(
										diagnoses, ' ') + separator;

								String threadPoolContent = "" + threadPoolSize;

								loggingResult += threadPoolContent + separator;

								loggingResult += diagnoses.size();

								resultsLogger.addRow(loggingResult);
							}

						}

						diagEval.finishCalculation();

						summary.append("\r\n");
						summary.append(executionMode.toString() + " version:\r\n");


						summary.append("Evaluated runs: " + nbTestRuns + "\r\n");
						summary.append("Average Diag Time (ms): " + diagEval.AvgTime + "\r\n");
						summary.append("Average Propagations: " + diagEval.AvgProps + "\r\n");
						summary.append("Average CSP Solves: " + diagEval.AvgSolves + "\r\n");
						summary.append("Average Constructed Nodes: " + diagEval.AvgNodes	+ "\r\n");
						summary.append("Average QXP Calls: " + diagEval.AvgQXPCalls + "\r\n");
						summary.append("Number of Diags: " + diagEval.AvgDiags + "\r\n");
						summary.append("Average Diag Size: " + diagEval.AvgDiagSize + "\r\n");
						summary.append("Average Solver Time (ms): " + diagEval.AvgSolverTime + "\r\n");
						summary.append("Average Conflict Size: " + diagEval.AvgConflictSize + "\r\n");
						summary.append("Cache reuse QX: " + diagEval.AvgTotalQXCacheReuse + "\r\n");
						summary.append("Average Tree Width: " + diagEval.AvgTreeWidth + "\r\n");


						/**
						 * Write the file
						 */
						resultsLogger.addRow(summary.toString());
						resultsLogger.writeFile();

					}

				}

			}

		}


	}

	enum ExecutionMode {fullparallel, levelparallel, singlethreaded, heuristic, hybrid}


	// Possible distribution strategies.
	enum ConflictGenerationStrategy_Size {
		equal, gaussian
	}


	enum ConflictGenerationStrategy_VarDistribution {equal, gaussian}


}
