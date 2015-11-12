package evaluations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder.QuickXplainType;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.diagnosis.quickxplain.choco3.choco2tochoco3.Choco2ToChoco3Solver;
import org.exquisite.diagnosis.quickxplain.mergexplain.MergeXplain;
import org.exquisite.diagnosis.quickxplain.mergexplain.MergeXplain.ConflictSearchModes;
import org.exquisite.diagnosis.quickxplain.mergexplain.ParallelMergeXplain;
import org.exquisite.tools.Utilities;

import choco.kernel.model.constraints.Constraint;
import evaluations.configuration.AbstractRunConfiguration;
import evaluations.configuration.AbstractScenario;
import evaluations.configuration.StdRunConfiguration;
import evaluations.configuration.StdScenario;
import evaluations.dxc.synthetic.minizinc.MZDiagnosisEngine;
import evaluations.tools.DiagnosisEvaluation;

/**
 * Abstract class that handles an evaluation and all of its common setup.
 * @author Thomas
 *
 */
public abstract class AbstractEvaluation {
	
	private static final String CONSTRAINT_ORDER_EXTENSION = "_order.csv";
	
	/**
	 * Should return a unique name for this evaluation.
	 * @return
	 */
	public abstract String getEvaluationName();

	/**
	 * Should return a path where the log files should be saved.
	 * @return
	 */
	public abstract String getResultPath();
	
	/**
	 * Should return a path where the constraint orders should be saved.
	 * @return
	 */
	public abstract String getConstraintOrderPath();
	
	/**
	 * Should the constraints be shuffled for this evaluation?
	 * @return
	 */
	protected abstract boolean shouldShuffleConstraints();

	/**
	 * Should prepare a diagnosis engine, so that calculateDiagnoses() can be called at the next step.
	 * @param abstractRunConfiguration
	 * @param abstractScenario
	 * @param subScenario
	 * @param iteration
	 * @return
	 */
	public abstract IDiagnosisEngine prepareRun(AbstractRunConfiguration abstractRunConfiguration, AbstractScenario abstractScenario, int subScenario, int iteration);
	
	/**
	 * This function is called once at the beginning of each scenario to allow initial preparation.
	 * @param abstractScenario
	 * @return true, if preparation was successful
	 */
	public boolean prepareScenario(AbstractScenario abstractScenario)
	{
		return true;
	}
	
	/**
	 * Should the system create a result dir with the time stamp for this evaluation?
	 * @return
	 */
	public boolean useTimeStampForResultDir() {
		return true;
	}
	
	/**
	 * Should the shuffled constraint order be saved to a file and loaded from it?
	 * @return
	 */
	public boolean usePersistentConstraintOrder() {
		return true;
	}
	
	/**
	 * Should the diagnoses be written to the log file for every single run? This can lead to huge file sizes.
	 * Default: false
	 * @return
	 */
	public boolean alwaysWriteDiagnoses() {
		return false;
	}
	
	/**
	 * Should the conflicts be written to the log file for every single run? This can lead to huge file sizes.
	 * Default: false
	 * @return
	 */
	public boolean alwaysWriteConflicts() {
		return false;
	}
	
	// Loggers
	public StringBuilder log = new StringBuilder();
	public StringBuilder results = new StringBuilder();
	public StringBuilder errors = new StringBuilder();
	static String separator = ";";
	static boolean directlyShowErrors = true;
	
	/**
	 * Adds an error message to the errors logger.
	 * @param message
	 * @param runConfiguration
	 * @param iteration
	 */
	public void addError(String message, AbstractRunConfiguration runConfiguration, int subScenario, int iteration) {
		StringBuilder text = new StringBuilder();
		text.append("ERROR: ");
		if (runConfiguration != null) {
			text.append(runConfiguration.getName());
			text.append(": ");
		}
		text.append(subScenario);
		text.append("-");
		text.append(iteration);
		text.append(": ");
		text.append(message);
		text.append("\n");
		errors.append(text.toString());
		log.append(text.toString());
		if (directlyShowErrors) {
			System.err.println(text.toString());
		}
	}
	
	/**
	 * Adds a standard log message for a diagnosis run.
	 * @param message
	 */
	public void addLog(String message) {
		log.append(message);
		log.append("\n");
	}
	
	/**
	 * Adds a result row for the end of the log file.
	 * @param message
	 */
	public void addResult(String message) {
		results.append(message);
		results.append("\n");
	}

	// Directory string for timestamp
	private String resultsDirectory = "";
	boolean constraintsOrderChanged = false;
	
	/**
	 * The main method that runs all tests. Should not be overwritten.
	 * @param nbInitRuns
	 * @param nbTestRuns
	 * @param runConfigurations
	 * @param scenarios
	 */
	public void runTests(int nbInitRuns, int nbTestRuns, AbstractRunConfiguration[] runConfigurations, AbstractScenario[] scenarios) {
		System.out.println("Running evaluation: " + getEvaluationName());
		
		long maxBytes = Runtime.getRuntime().maxMemory();
		System.out.println("Max memory: " + maxBytes / 1024 / 1024 + "M");

		if (useTimeStampForResultDir()) {
			Timestamp tstamp = new Timestamp(System.currentTimeMillis());
			String time = tstamp.toString();
			time = time.replace(':', '-').substring(0, time.length() - 4);
					
			resultsDirectory = time + "/";
		}
		
		// Run through all scenarios (files)
		for (int iScenario = 0; iScenario < scenarios.length; iScenario++) {
			
			// preapareScenario is called once per scenario
			if (prepareScenario(scenarios[iScenario])) {
				// running times are saved for the different configurations in order to compare them
				Map<AbstractRunConfiguration, Double> runTimesMap = new LinkedHashMap<AbstractRunConfiguration, Double>();
				
				int nbSubScenarios = (scenarios[iScenario].getSubScenarioEnd()-scenarios[iScenario].getSubScenarioStart()) + 1;
				int nbTotalRuns = (nbInitRuns + nbTestRuns * nbSubScenarios) * runConfigurations.length;
				int actRun = 0;
				
				// Shuffling is done for each scenario
				List<List<Integer>> constraintsOrder = new ArrayList<List<Integer>>();
				if (shouldShuffleConstraints() && usePersistentConstraintOrder()) {
					loadConstraintsOrder(constraintsOrder, scenarios[iScenario]);
				}
				constraintsOrderChanged = false;
				
				log = new StringBuilder();
				results = new StringBuilder();
				errors = new StringBuilder();
				oldErrorLength = 0;
				
				System.out.println(scenarios[iScenario].getName() + " (" + nbTotalRuns + " runs)");
				
				startPercentage();
				newRunConfiguration = true;
				
				// Run through all configurations (execution modes etc.)
				for (int iRunConfiguration = 0; iRunConfiguration < runConfigurations.length; iRunConfiguration++) {
					// every run configuration gets its own evaluation
					DiagnosisEvaluation diagEval = new DiagnosisEvaluation();
					addLog(runConfigurations[iRunConfiguration].getName() + ":");

					boolean initialized = false;
					
					// Run through all sub scenarios (e.g. different scenarios in DXCSyntheticBenchmark)
					for (int iSubScenario = scenarios[iScenario].getSubScenarioStart(); iSubScenario <= scenarios[iScenario].getSubScenarioEnd(); iSubScenario++) {						
						if (nbSubScenarios > 1) {
							addLog("Subscenario " + iSubScenario);
						}
						addLog(getRowHeader());
						
						int starti, endi;
						
						if (!initialized) {
							starti = 0;
						}
						else {
							starti = nbInitRuns;
						}
						endi = nbInitRuns + nbTestRuns;
					
						// Do the runs (initialization runs + test runs)
						for (int i = starti; i < endi; i++) {
							// prepareRun is called once per run and returns the ready diagnosis engine
							IDiagnosisEngine engine = null;
							try
							{
								engine = prepareRun(runConfigurations[iRunConfiguration], scenarios[iScenario], iSubScenario, i);
							} catch (Exception e) {
								addError(e.getMessage(), runConfigurations[iRunConfiguration], iSubScenario, i);
							}
							

							if (i == nbInitRuns) {
								initialized = true;
							}
							
							if (engine != null) {
//								if (engine instanceof AbstractHSDagBuilder) {
									// shuffling constraints
									if (shouldShuffleConstraints()) {
										shuffleConstraints(engine, constraintsOrder, i);
									}
									try {
										// Do the diagnosis
										long start = System.nanoTime();
										List<Diagnosis> diagnoses = engine.calculateDiagnoses();
										long end = engine.getFinishedTime();
										
										double ms = (end-start)/1000000d;
										
										// Continuing quickxplain threadpool has to be stopped and restarted
										QuickXPlain.restartThreadpool();
										
										// diagnosis evaluation
										if (diagnoses.size() == 0) {
											addError("No diagnosis found after " + ((int)ms) + "ms.", runConfigurations[iRunConfiguration], iSubScenario, i);
											// Special debug info for MZDiagnosisEngine
											if (engine instanceof MZDiagnosisEngine) {
												addError("Input: " + ((MZDiagnosisEngine)engine).lastInput, runConfigurations[iRunConfiguration], iSubScenario, i);
												addError("Output: " + ((MZDiagnosisEngine)engine).lastOutput, runConfigurations[iRunConfiguration], iSubScenario, i);
											}
										}
										if (initialized) {
											diagEval.analyzeRun(engine, ms);
											
											addLog(getResultRow(engine, ms, diagnoses, runConfigurations[iRunConfiguration], i==nbInitRuns));
										}
									} catch (DiagnosisException e) {
//										e.printStackTrace();
										addError(e.getMessage(), runConfigurations[iRunConfiguration], iSubScenario, i);
									}
//								}
//								else {
//									addError("Engine " + engine.getClass().toString() + " not supported.", runConfigurations[iRunConfiguration], i);
//								}
							}
							else {
								addError("DiagnosisEngine is null", runConfigurations[iRunConfiguration], iSubScenario, i);
							}
							actRun++;
							if (i == endi - 1 && iSubScenario == scenarios[iScenario].getSubScenarioEnd()) {
								newRunConfiguration = true;
							}
							printPercentage(actRun, nbTotalRuns);
							
							System.gc();
						}
					}
					addLog("");
					
					diagEval.finishCalculation();
					
					addResult(runConfigurations[iRunConfiguration].getName() + ":");
					
					addResult(diagEval.getResults());
	
					runTimesMap.put(runConfigurations[iRunConfiguration], diagEval.AvgTime);
				}
				System.out.println("");
				
				// Print out the runtimes for all run configurations
				runTimesMap = sortByValue(runTimesMap);
				double run0Time = runTimesMap.get(runConfigurations[0]);
				for (AbstractRunConfiguration scn: runTimesMap.keySet()) {
					double time = runTimesMap.get(scn);
					StringBuilder out = new StringBuilder();
					out.append(scn.getName());
					out.append(": ");
					out.append(String.format("%1$,.2f ms", time));
					if (runConfigurations.length > 1 && scn != runConfigurations[0]) {
						double dif = run0Time / time;
						double percent = (1 - (time / run0Time)) * 100d;
						out.append(String.format(" (S%d: %.2f, E%d: %.2f, %.0f%%)", scn.threads, dif, scn.threads, dif / scn.threads, percent));
					}
					addResult(out.toString());
					System.out.println(out.toString());
				}
				System.out.println();
				
				if (shouldShuffleConstraints() && usePersistentConstraintOrder() && constraintsOrderChanged) {
					saveConstraintsOrder(constraintsOrder, scenarios[iScenario]);
				}
			}
			else {
				addError("Scenario preparation failed.", null, -1, -1);
			}
			
			// Write logs to file
			printLogFile(scenarios[iScenario].getName());

//			System.out.println(results.toString());
			
			if (errors.length() > 0) {
				System.err.println(errors.toString());
			}
		}
		
		System.out.println("Tests finished.");
	}
	
	private void saveConstraintsOrder(List<List<Integer>> constraintsOrder, AbstractScenario scenario) {
		File path = new File(getConstraintOrderPath());
		path.mkdirs();
		
		File f = new File(path, scenario.getConstraintOrderName()+CONSTRAINT_ORDER_EXTENSION);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter (new FileWriter (f));
			StringBuffer buf = new StringBuffer();
			for (List<Integer> order: constraintsOrder) {
				for (Integer nr: order) {
					buf.append(nr).append(" ");
				}
				buf.append("\n");
			}
			bw.write(buf.toString());
		}
		catch(IOException e) {
			addError("Could not write constraintOrders", null, -1, -1);
		}
		finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					addError("Could not close constraintOrders file", null, -1, -1);
				}
			}
		}
	}

	private void loadConstraintsOrder(List<List<Integer>> constraintsOrder, AbstractScenario scenario) {
		constraintsOrder.clear();
		File f = new File(getConstraintOrderPath(), scenario.getConstraintOrderName()+CONSTRAINT_ORDER_EXTENSION);
		if (!f.exists()) {
			return;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			String line = reader.readLine();
			while (line != null) {
				String[] ctnrs = line.split(" ");
				List<Integer> order = new ArrayList<Integer>();
				for (int i=0;i<ctnrs.length;i++) {
					order.add(Integer.parseInt(ctnrs[i]));
				}
				constraintsOrder.add(order);
				
				line = reader.readLine();
			}
		} catch (IOException e) {
			addError("Could not read constraintOrders", null, -1, -1);
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					addError("Could not close constraintOrders file", null, -1, -1);
				}
			}
		}
	}

	/**
	 * Chooses the engineType based on runConfiguration.executionMode and does some settings for MergeXplain
	 * @param scenario
	 * @param runConfiguration
	 * @return
	 */
	public EngineType chooseEngineType(StdScenario scenario, StdRunConfiguration runConfiguration) {
		EngineType engineType = null;
		AbstractHSDagBuilder.SINGLE_CONFLICT_SEARCH = false;
		AbstractHSDagBuilder.USE_QXTYPE = QuickXplainType.QuickXplain;
		QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT = false;
		switch (runConfiguration.executionMode) {
		case fullparallel:
			engineType = EngineType.FullParaHSDagStandardQX;
			break;
		case levelparallel:
			engineType = EngineType.ParaHSDagStandardQX;
			break;
		case heuristic:
			engineType = EngineType.HeuristicSearch;
			break;
		case hybrid:
			engineType = EngineType.Hybrid;
			break;
		case singlethreaded:
			engineType = EngineType.HSDagStandardQX;
			break;
		case minizinc:
			engineType = EngineType.MiniZinc;
			break;
		case prdfs:
			engineType = EngineType.PRDFS;
			break;
		case mergexplain:
			engineType = EngineType.HSDagStandardQX;
			AbstractHSDagBuilder.USE_QXTYPE = QuickXplainType.MergeXplain;
			if (scenario.maxDiags == -1) {
				MergeXplain.ConflictSearchMode = ConflictSearchModes.Some;
			// This is not better for bigger problems
//			} else if (scenario.maxDiags == 1) {
//				MergeXplain.ConflictSearchMode = ConflictSearchModes.EnoughFor1Diag;
//				AbstractHSDagBuilder.SINGLE_CONFLICT_SEARCH = true;
			} else {
				MergeXplain.ConflictSearchMode = ConflictSearchModes.Least;
				
			}
			break;
		case continuingmergexplain:
			QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT = true;
			engineType = EngineType.HSDagStandardQX;
			AbstractHSDagBuilder.USE_QXTYPE = QuickXplainType.MergeXplain;
			if (scenario.maxDiags == -1) {
				MergeXplain.ConflictSearchMode = ConflictSearchModes.Some;
			// This is not better for bigger problems
//			} else if (scenario.maxDiags == 1) {
//				MergeXplain.ConflictSearchMode = ConflictSearchModes.EnoughFor1Diag;
//				AbstractHSDagBuilder.SINGLE_CONFLICT_SEARCH = true;
			} else {
				MergeXplain.ConflictSearchMode = ConflictSearchModes.Least;
				
			}
			break;
		case fpandmxp:
			engineType = EngineType.FullParaHSDagStandardQX;
			AbstractHSDagBuilder.USE_QXTYPE = QuickXplainType.MergeXplain;
			MergeXplain.ConflictSearchMode = ConflictSearchModes.Least;
			break;
		case continuingfpandmxp:
			QuickXPlain.CONTINUE_AFTER_FIRST_CONFLICT = true;
			engineType = EngineType.FullParaHSDagStandardQX;
			AbstractHSDagBuilder.USE_QXTYPE = QuickXplainType.MergeXplain;
			MergeXplain.ConflictSearchMode = ConflictSearchModes.Least;
			break;
		case parallelmergexplain:
			engineType = EngineType.HSDagStandardQX;
			AbstractHSDagBuilder.USE_QXTYPE = QuickXplainType.ParallelMergeXplain;
			ParallelMergeXplain.maxThreadPoolSize = runConfiguration.threads;
			if (scenario.maxDiags == -1) {
				MergeXplain.ConflictSearchMode = ConflictSearchModes.Some;
			} else {
				MergeXplain.ConflictSearchMode = ConflictSearchModes.Least;
			}
			break;
		case inversequickxplain:
			engineType = EngineType.InverseQuickXplain;
			break;
		case mxpandinvqxp:
			engineType = EngineType.MXPandInvQXP;
			break;
		case sfl:
			engineType = EngineType.SFL;
			break;
		default:
			break;
		}
		
		Choco2ToChoco3Solver.USE_ACTIVITY_STRATEGY = false;
		if (scenario.getName().toLowerCase().contains("c432") && runConfiguration.choco3) {
//			System.out.println("Using activity strategy for c432.");
			Choco2ToChoco3Solver.USE_ACTIVITY_STRATEGY = true;
		}
		Choco2ToChoco3Solver.USE_MINDOM_STRATEGY = false;
		if (scenario.getName().toLowerCase().contains("hospital_payment") && runConfiguration.choco3) {
//			System.out.println("Using mindom strategy for hospital_payment.");
			Choco2ToChoco3Solver.USE_MINDOM_STRATEGY = true;
		}
		
		return engineType;
	}
	
	/**
	 * Prints the different loggers to a log file
	 * @param scenarioName
	 */
	private void printLogFile(String scenarioName) {
		PrintWriter file = null;
		try {
//			File dir = new File(FilenameUtils.getFullPathNoEndSeparator(getResultPath()), resultsDirectory);
//			if (!dir.exists())
//				dir.mkdirs();
			File p = new File(getResultPath(), resultsDirectory + scenarioName + ".csv");
			
			new File(p.getParent()).mkdirs();
			file = new PrintWriter(p);
			
			if (errors.length() > 0) {
				file.println("Errors:");
				file.print(errors.toString());
				file.println();
			}
			file.print(log.toString());
			file.println();
			file.println("Results:");
			file.print(results.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (file != null) {
				file.close();
			}
		}
	}

	/**
	 * Returns the result header for the log file
	 * @return
	 */
	public String getRowHeader() {
		String logFileHeader = 	"#Vars" + separator + 
				"#Constraints" + separator + 
				"#CSP props." + separator + 
				"#CSP solved" + separator + 
				"Diag. time (ms)" + separator + 
				"Max Search Depth" + separator + 
				"Max Diags" + separator +
				"Diagnoses" + separator +			
				"ThreadPoolSize" + separator +
				"#Diags" + separator +
				"#Conflicts" + separator +
				"Conflicts";
		
		return logFileHeader;
	}

	/**
	 * Returns a result row for the given data
	 * @param engine
	 * @param start
	 * @param end
	 * @param diagnoses
	 * @param runConfiguration
	 * @return
	 */
	public String getResultRow(IDiagnosisEngine engine, double ms, List<Diagnosis> diagnoses,
			AbstractRunConfiguration runConfiguration, boolean firstRun) {
		// record data for this run.
		StringBuilder loggingResult = new StringBuilder();
		if (engine instanceof AbstractHSDagBuilder) {
			int varCount = ((AbstractHSDagBuilder)engine).sessionData.diagnosisModel.getVariables().size();
			loggingResult.append(varCount);
		}
		loggingResult.append(separator);
		if (engine instanceof AbstractHSDagBuilder) {
			loggingResult.append(((AbstractHSDagBuilder)engine).sessionData.diagnosisModel.getConstraintNames().size());
		}
		loggingResult.append(separator);
		loggingResult.append(engine.getPropagationCount() + separator);
		loggingResult.append(engine.getCspSolvedCount() + separator);
		loggingResult.append((int)Math.round(ms) + separator);
		if (engine instanceof AbstractHSDagBuilder) {
			loggingResult.append(((AbstractHSDagBuilder)engine).sessionData.config.searchDepth
					+ separator);
			loggingResult.append(((AbstractHSDagBuilder)engine).sessionData.config.maxDiagnoses
					+ separator);
		}
		else {
			loggingResult.append(separator);
			loggingResult.append(separator);
		}
		
		if (alwaysWriteDiagnoses() || firstRun)
		{
			loggingResult.append(Utilities.printSortedDiagnoses(diagnoses, ' ') + separator);
		} else {
			loggingResult.append(separator);
		}

		loggingResult.append(runConfiguration.threads + separator);

		loggingResult.append(diagnoses.size() + separator);
		
		if (engine instanceof AbstractHSDagBuilder) {
			loggingResult.append(((AbstractHSDagBuilder)engine).knownConflicts.getCollection().size());
		}
		loggingResult.append(separator);
		
		if ((alwaysWriteConflicts() || firstRun) && engine instanceof AbstractHSDagBuilder) {
			loggingResult.append(Utilities.printSortedConflicts(((AbstractHSDagBuilder)engine).knownConflicts.getCollection(), ((AbstractHSDagBuilder)engine).sessionData.diagnosisModel, ' '));
		}
		
		return loggingResult.toString();
	}

	/**
	 * Shuffles the constraints of the given engine.
	 * Model loading needs to be deterministic for this to work.
	 * @param engine
	 * @param constraintOrders A list of orders, that is filled for the first run and then reused for the other runs.
	 * @param iteration
	 */
	protected void shuffleConstraints(IDiagnosisEngine engine, List<List<Integer>> constraintOrders, int iteration) {
		List<Constraint> constraints = new ArrayList<Constraint>(engine.getSessionData().diagnosisModel.getPossiblyFaultyStatements());
		
		// if no order was defined for this iteration yet, we create a new random order
		List<Integer> order;
		if (!usePersistentConstraintOrder() || constraintOrders.size() <= iteration) {
			order = new ArrayList<Integer>(constraints.size());
			for (int i = 0; i < constraints.size(); i++) {
				order.add(i);
			}
			Collections.shuffle(order);
			if (usePersistentConstraintOrder()) {
				constraintsOrderChanged = true;
				constraintOrders.add(order);
			}
		}
		else {
			order = constraintOrders.get(iteration);
		}
		
		// clear and readd to the original list, so that all references to this original list get the new order
		List<Constraint> orderedConstraints = engine.getSessionData().diagnosisModel.getPossiblyFaultyStatements();
		orderedConstraints.clear();
		for (int i = 0; i < constraints.size(); i++) {
			int o = order.get(i);
			orderedConstraints.add(constraints.get(o));
		}
		
//		System.out.println(Utilities.printConstraintList(orderedConstraints, engine.model));
	}

	/**
	 * Sorts a map by its values.
	 * @param map
	 * @return
	 */
	private <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
	{
	    List<Map.Entry<K, V>> list =
	        new LinkedList<>( map.entrySet() );
	    Collections.sort( list, new Comparator<Map.Entry<K, V>>()
	    {
	        @Override
	        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	        {
	            return (o1.getValue()).compareTo( o2.getValue() );
	        }
	    } );
	
	    Map<K, V> result = new LinkedHashMap<>();
	    for (Map.Entry<K, V> entry : list)
	    {
	        result.put( entry.getKey(), entry.getValue() );
	    }
	    return result;
	}

	int oldPercentageWidth = 0;
	int percentageWidth = 200;
	
	/**
	 * Initializes and prints out the first line of the percentage bar.
	 */
	private void startPercentage() {
		oldPercentageWidth = 0;
		System.out.print("|");
		for (int i = 0; i < percentageWidth - 2; i++) {
			System.out.print(" ");
		}
		System.out.println("|");
	}
	
	private boolean newRunConfiguration = false;
	
	/**
	 * Prints out the actual percentage bar.
	 * If there was an error in a run, an 'E' will be printed instead of the standard '-'.
	 * @param actRun
	 * @param nbTotalRuns
	 */
	private void printPercentage(int actRun, int nbTotalRuns) {
		float newPercentage = (float)actRun / nbTotalRuns;
		int newPercentageWidth = Math.round(newPercentage * percentageWidth);
		if (newPercentageWidth > oldPercentageWidth) {
			boolean printE = hasNewError();
			while (newPercentageWidth > oldPercentageWidth) {
				if (printE) {
					System.out.print("E");
				}
				else {
					if (newRunConfiguration) {
						System.out.print("|");
					} else {
						System.out.print("-");
					}
				}
				oldPercentageWidth++;
			}
			newRunConfiguration = false;
		}
	}
	
	private int oldErrorLength = 0;

	/**
	 * Determines if there was an error since last check.
	 * @return
	 */
	private boolean hasNewError() {
		if (errors.length() > oldErrorLength) {
			oldErrorLength = errors.length();
			return true;
		}
		return false;
	}
}
