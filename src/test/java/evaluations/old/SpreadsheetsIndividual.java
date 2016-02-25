package evaluations.old;

import choco.kernel.model.constraints.Constraint;
import evaluations.tools.DiagnosisCheck;
import evaluations.tools.DiagnosisEvaluation;
import evaluations.tools.ResultsLogger;
import org.exquisite.data.ConstraintsFactory;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain.SolverType;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.util.*;

import static org.exquisite.core.measurements.MeasurementManager.COUNTER_CSP_SOLUTIONS;
import static org.exquisite.core.measurements.MeasurementManager.COUNTER_PROPAGATION;
import static org.exquisite.core.measurements.MeasurementManager.getCounter;

/**
 * A class to run individual tests in different configurations
 * @author dietmar
 *
 */
public class SpreadsheetsIndividual {
	
	static String separator = ";";
	// Some constants and global settings
	// ----------------------------------------------------
	static int PARALLEL_THREADS = 4;
	static boolean CHOCO3 = true;
	static int nbInitizializationRuns = 5;
	static int nbTestRuns = 10;
	static int SEARCH_DEPTH = -1;
	static int MAX_DIAGS = 1;
	static int ARTIFICIAL_WAIT_TIME = -1;
//	static boolean useFullParallelMode = false;
	static boolean SHUFFLE_CONSTRAINTS = false;
	// ----------------------------------------------------
	// Directories
	static String inputFileDirectory = "experiments/spreadsheetsindividual/";
	static String logFileDirectory = "logs/spreadsheetsindividual/";
	// ----------------------------------------------------
	// Handle to the logger
//	private LoggingData loggingData;
	ResultsLogger resultsLogger = null;
	// A cache for the shuffled constraints
	// ensure everMap<K, V>e solves the same set of problems
	Map<Integer, Map<String,Integer>> shuffledConstraintsPerIteration = new HashMap<Integer, Map<String,Integer>>();
	// The list of scenarios
	List<Scenario> scenarios = new ArrayList<Scenario>();
	
	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		ConstraintsQuickXPlain.ARTIFICIAL_WAIT_TIME = ARTIFICIAL_WAIT_TIME;

		Debug.DEBUGGING_ON = false;
		Debug.QX_DEBUGGING = false;

		System.out.println("Starting Individual Spreadsheet tests.");
		new SpreadsheetsIndividual().runIndividualSpreadsheetTests();
 	    System.out.println("Tests finished.");
	}

	public static <K, V extends Comparable<? super V>> Map<K, V>
	sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list =
				new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * runs additional tests for a number of mutated real-world spreadsheets.
	 */
	public void runIndividualSpreadsheetTests() {

		// Make a list of files to be analyzed
		String[] inputFiles = new String[] {
//				"salesforecast_TC_IBB.xml",
//				"salesforecast_TC_2Faults.xml",
//							"salesforecast_TC_2FaultsHeavy.xml",
				"SemUnitEx1_DJ.xml",
				"SemUnitEx2_1fault.xml",
				"SemUnitEx2_2fault.xml",
//				"VDEPPreserve_1fault.xml",
//				"VDEPPreserve_2fault.xml",
//							"VDEPPreserve_3fault.xml",
//				"AZA4.xml",
//				"Consultant_form.xml",
//				"Hospital_Payment_Calculation.xml",
//				"Hospital_Payment_Calculation_v2.xml",
//				"Hospital_Payment_Calculation_C3.xml",
//				"11_or_12_diagnoses.xml",
//				"choco_loop.xml",
//				"Paper2.xml",
//				"Test_If.xml",
//				"Test_If2.xml",

		};

		scenarios.add(new Scenario(executionMode.singlethreaded, pruningMode.on, PARALLEL_THREADS, CHOCO3));
//		scenarios.add(new Scenario(executionMode.levelparallel, pruningMode.on, 2, CHOCO3));
//		scenarios.add(new Scenario(executionMode.fullparallel, pruningMode.on, 1, CHOCO3));
		scenarios.add(new Scenario(executionMode.fullparallel, pruningMode.on, 2, CHOCO3));
//		scenarios.add(new Scenario(executionMode.fullparallel, pruningMode.on, 4, CHOCO3));
//		scenarios.add(new Scenario(executionMode.heuristic, pruningMode.on, 1, CHOCO3));
		scenarios.add(new Scenario(executionMode.heuristic, pruningMode.on, 2, CHOCO3));
//		scenarios.add(new Scenario(executionMode.heuristic, pruningMode.on, 4, CHOCO3));
//		scenarios.add(new Scenario(executionMode.heuristic, pruningMode.on, PARALLEL_THREADS, true));
//		scenarios.add(new Scenario(executionMode.hybrid, pruningMode.on, 1, CHOCO3));
		scenarios.add(new Scenario(executionMode.hybrid, pruningMode.on, 2, CHOCO3));
//		scenarios.add(new Scenario(executionMode.hybrid, pruningMode.on, 4, CHOCO3));
//		scenarios.add(new Scenario(executionMode.levelparallel, pruningMode.on, PARALLEL_THREADS, false));
//		scenarios.add(new Scenario(executionMode.fullparallel, pruningMode.on, PARALLEL_THREADS, false));
//		scenarios.add(new Scenario(executionMode.fullparallel, pruningMode.on, PARALLEL_THREADS, false));
//		scenarios.add(new Scenario(executionMode.heuristic,pruningMode.on,PARALLEL_THREADS*2));
//		scenarios.add(new Scenario(executionMode.heuristic,pruningMode.on,0));

//		inputFiles = new String[]{"VDEPPreserve_3fault.xml"};


		// Go through the files and run them in different scenarios
		for (String inputfilename : inputFiles) {
			runScenarios(inputFileDirectory, inputfilename, logFileDirectory, scenarios);
		}
	}
	
	/**
	 * Runs a scenario and writes the output
	 * @param inputFile
	 */
	public void runScenarios(String inputFileDirectory,
							 String inputFilename,
							 String logDirectory,
							 List<Scenario> scenarios
										) {

		// , executionMode.heuristic, pruningMode.on, NB_ITERATIONS, 0


		shuffledConstraintsPerIteration.clear();

		double[] scenarioTimes = new double[scenarios.size()];
		Map<Scenario, Double> scenarioTimesMap = new LinkedHashMap<Scenario, Double>();

		for (int k = 0; k < scenarios.size(); k++) {
			Scenario scenario = scenarios.get(k);

			// Let's create an output directory first. We will place the output files for one test case
			// in this directory using some naming conventions
			/*
			File outputDirectory = new File(logDirectory + inputFilename);
			if (!outputDirectory.exists()) {
				boolean success = outputDirectory.mkdir();
				if (success) {
					System.out.println("Created output directory for " + inputFilename + " " + success);
				}
				else {
					System.err.println("Failed to create output directory " + outputDirectory);
					return;
				}
			}
			*/
			// Prepare the log file
			// Use some naming convention
			// Remove the .xml ending
			String logFileName = inputFilename.substring(0,inputFilename.lastIndexOf('.'));
			// Append some stuff
			logFileName += getScenarioDescription(scenario);

			if (scenario.choco3) {
    			ConstraintsQuickXPlain.SOLVERTYPE = SolverType.Choco3;
    		} else {
    			ConstraintsQuickXPlain.SOLVERTYPE = SolverType.Choco2;
    		}

//			if (threads == 0) threads = 1;

			logFileName += ".csv";

			resultsLogger = new ResultsLogger(separator, logFileDirectory, "", logFileName);

			StringBuilder summary = new StringBuilder();

			summary.append("\r\n");
			summary.append("File: " + inputFilename + "\r\n");
			summary.append("MaxDiagSize: " + MAX_DIAGS + "\r\n");
			summary.append("WaitTime: " + ARTIFICIAL_WAIT_TIME + "\r\n");
			summary.append("Initialization Runs: " + nbInitizializationRuns + "\r\n");
			summary.append("Test Runs: " + nbTestRuns + "\r\n");
			summary.append("Shuffle Constraints: " + SHUFFLE_CONSTRAINTS + "\r\n");
			summary.append("Use LastLevelCheck: " + true + "\r\n");

			System.out.println("Setup log file with name " + logFileName);

			resultsLogger.writeSeparatorLine("");
			resultsLogger.writeSeparatorLine("Version:" + scenario.executionMode);

			DiagnosisCheck check = new DiagnosisCheck();

			// Run the test with the specified mode
			// Set the XML file name
			String fullInputFilename = inputFileDirectory + inputFilename;
			// Set the correct engine type
			EngineType engineType = EngineType.HSDagStandardQX;
			if (scenario.executionMode  == SpreadsheetsIndividual.executionMode.fullparallel) {
				engineType = EngineType.FullParaHSDagStandardQX;
			}
			else if (scenario.executionMode  == SpreadsheetsIndividual.executionMode.levelparallel) {
				engineType = EngineType.ParaHSDagStandardQX;
			}
			else if (scenario.executionMode  == SpreadsheetsIndividual.executionMode.heuristic) {
				engineType = EngineType.HeuristicSearch;
			}
			else if (scenario.executionMode  == SpreadsheetsIndividual.executionMode.hybrid) {
				engineType = EngineType.Hybrid;
			}

			// Set the pruning mode
			ConstraintsFactory.PRUNE_IRRELEVANT_CELLS = scenario.pruningMode != pruningMode.off;

			// Allow us to pass 0 as a parameter and test dependencies of execution orders (file suffix will be 0)
			if (scenario.threads == 0) {
				scenario.threads = 1;
			}

			// directly print some stuff
//			long totalTime = 0;
//			int counter = 0;

			int nbRuns = nbInitizializationRuns + nbTestRuns;
			boolean initFinished = false;

			DiagnosisEvaluation<Constraint> diagEval = new DiagnosisEvaluation<Constraint>();

			// Do an appropriate number of iterations
			for (int i=0;i<nbRuns;i++) {

				if (i == nbInitizializationRuns) {
					initFinished = true;
					System.out.println();
					System.out.println("Initialization finished");
				}

//				System.out.println("Diagnosis iteration: " + (i+1));
				// Create the engine
				AbstractHSDagEngine<Constraint> diagnosisEngine = (AbstractHSDagEngine)
												  EngineFactory.makeEngineFromXMLFile(
														  engineType,
														  fullInputFilename,
															scenario.threads);
//				System.out.println("Created an engine for " + fullInputFilename);
				// Set the search depth
				diagnosisEngine.setSearchDepth(SpreadsheetsIndividual.SEARCH_DEPTH);
				diagnosisEngine.getDiagnosisModel().getConfiguration().searchDepth = SpreadsheetsIndividual.SEARCH_DEPTH;
				diagnosisEngine.getDiagnosisModel().getConfiguration().maxDiagnoses = SpreadsheetsIndividual.MAX_DIAGS;

//				System.out.println("Max diags: " + diagnosisEngine.getDiagnosisModel().getConfiguration().maxDiagnoses);

//				System.out.println(diagnosisEngine.getDiagnosisModel().appXML.getFormulas());
//				System.err.println("Loaded - ending");

				// ===================================================================================================
				// Trying to have the same problem all the time
				// ===================================================================================================
				if (SHUFFLE_CONSTRAINTS)
				{
					Map<String, Integer> positions = shuffledConstraintsPerIteration.get(i);
					if (positions == null) {
	//					System.out.println("Will create a new sequence for this iteration ..");
						// Get the constraints
						List<Constraint> theConstraints = new ArrayList<Constraint>(diagnosisEngine.sessionData.getDiagnosisModel().getPossiblyFaultyStatements());
						Collections.shuffle(theConstraints);
						diagnosisEngine.sessionData.getDiagnosisModel().setPossiblyFaultyStatements(theConstraints);
						// Put them into a list by constraint expression (as text to reuse the order across engines)
						// If two are the same this should not be a problem ... as they have the same semantics
	//					System.out.println("First element in iteration " + i + ": "  + diagnosisEngine.sessionData.getDiagnosisModel().getPossiblyFaultyStatements().get(0));

						positions = new HashMap<String, Integer>();
						int cnt = 0;
						for (Constraint c : theConstraints) {
							positions.put(c.toString(),cnt);
							cnt++;
						}
						shuffledConstraintsPerIteration.put(i, new HashMap<String, Integer>(positions));
	//					System.out.println("Stored a shuffled sequence for iteration " + i);
						int mapsize = positions.keySet().size();
						if (mapsize != theConstraints.size()) {
							System.err.println("Encoding of constraints was not unique ..");
							System.exit(1);
						}

						//					System.out.println("map size: " + positions.keySet().size() + ", constraints were " + theConstraints.size());
					}
					else {
	//					System.out.println("Will reuse a sequence for iteration " + i );
						List<Constraint> theConstraints = new ArrayList<Constraint>(
									diagnosisEngine.sessionData.getDiagnosisModel().getPossiblyFaultyStatements());
						// Make an array and fill it at correct positions. Finally, create an array list from this one
						Constraint[] constrArray = new Constraint[theConstraints.size()];
						for (Constraint c : theConstraints) {
							String name = c.toString();
							Integer pos = positions.get(name);
							constrArray[pos] = c;
							if (pos == null) {
								System.err.println("Could not find constraint anymore !!" + name);
								System.exit(1);
							}
						}
						List<Constraint> finalConstraints = Arrays.asList(constrArray);
						diagnosisEngine.sessionData.getDiagnosisModel().setPossiblyFaultyStatements(finalConstraints);
	//					System.out.println("First element: " + diagnosisEngine.sessionData.getDiagnosisModel().getPossiblyFaultyStatements().get(0));


					}
				}
				// ===================================================================================================


				// Shuffle the constraints
//				diagnosisEngine.sessionData.getDiagnosisModel().setPossiblyFaultyStatements(shuffledConstraints);
				//Make a call to the diagnosis engine.
				long startTime = 0;
				List<Diagnosis<Constraint>> diagnoses = null;
				long endTime;
				try {
					startTime = System.currentTimeMillis();
					diagnoses = diagnosisEngine.calculateDiagnoses();
					endTime = System.currentTimeMillis();
//					System.err.println("Number of diagnoses this run: " + diagnoses.size());

//					System.out.println("Constructed nodes: " + diagnosisEngine.allConstructedNodes.getCollection().size());
//					System.out.println("Number of conflicts: " + diagnosisEngine.knownConflicts.getCollection().size());

				}
				catch (Exception e) {
					System.err.println("Error when calculating diagnosis for " + fullInputFilename + " : " + e.getMessage());
					e.printStackTrace();
					return;
				}
				// Anfang
//				try {
//					Collections.sort(
//							diagnoses,
//							new evaluations.smell.SmellComparator(
//									SmellIdentification
//											.getSmells(fullInputFilename)));
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				// Ende

//				long duration = endTime - startTime;
//				System.out.println("Number of diagnoses on run " + (i + 1) + ": " + diagnoses.size());
//				System.out.println("Found in " + duration + "ms.");
//				System.out.println(Utilities.printSortedDiagnoses(diagnoses, ' '));


				if (initFinished) {
					diagEval.analyzeRun(endTime - startTime);
					diagEval.engineTest(diagnosisEngine);

					// record data for this run.
					String loggingResult = "";
					int varCount = diagnosisEngine.sessionData.getDiagnosisModel().getVariables().size();
					loggingResult += varCount + separator;
					loggingResult += diagnosisEngine.sessionData.getDiagnosisModel()
							.getConstraintNames().size() + separator;
					loggingResult += getCounter(COUNTER_PROPAGATION).value();
					loggingResult += getCounter(COUNTER_CSP_SOLUTIONS).value() + separator;
					loggingResult += (endTime - startTime) + separator;
					loggingResult += diagnosisEngine.sessionData.getConfiguration().searchDepth
							+ separator;

					for (int j = 0; j < diagnoses.size(); j++) {
						// loggingResult+="(Diag # " + j + ": " +
						// Utilities.printConstraintList(diagnoses.get(j).getElements(),
						// sessionData.getDiagnosisModel()) + ") ";
					}
					loggingResult += Utilities.printSortedDiagnoses(
							diagnoses, ' ') + separator;

					String threadPoolContent = "" + scenario.threads;

					loggingResult += threadPoolContent + separator;

					loggingResult += diagnoses.size();

					resultsLogger.addRow(loggingResult);
				} else {
//					System.out.println(endTime - startTime);
					System.out.print(".");
				}

//				appendResultToLog(diagnosisEngine, duration, diagnoses, scenario.threads);

				check.printNonMinimalDiagnoses(diagnoses);
				if (MAX_DIAGS < 0 || !SHUFFLE_CONSTRAINTS)
				{
					check.printDifferentDiagnoses(diagnoses);
				}

			}

			diagEval.finishCalculation();

			summary.append("\r\n");
			switch (scenario.executionMode) {
			case singlethreaded:
				summary.append("Sequential version:\r\n");
//				totalSeq = diagEval.AvgTime;
				break;
			case levelparallel:
				summary.append("Level-wise parallel version:\r\n");
//				totalLevelW = diagEval.AvgTime;
				break;
			case fullparallel:
				summary.append("Full parallel version:\r\n");
//				totalFullP = diagEval.AvgTime;
				break;
			case heuristic:
				summary.append("Heuristic version:\r\n");
				break;
			case hybrid:
				summary.append("Hybrid version:\r\n");
				break;
			default:
				break;
			}
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

			// DJ TEST
			summary.append("Average nodeLabel reuse: " + diagEval.AvgConflictReuse + "\r\n");

			// Calculate average improvements
//			double winLWP = 1 - (totalLevelW / totalSeq);
//			double winFullP = 1 - (totalFullP / totalSeq);

//			DecimalFormat f = new DecimalFormat("#");

			// The most important thing  repeated
//			summary.append("\r\n\r\nTotal Seq: " + f.format(totalSeq) +"\r\n");
//			summary.append("Total Lev: " + f.format(totalLevelW) + " (" + f.format(winLWP * 100) + "%)" + "\r\n");
//			summary.append("Total Full:" + f.format(totalFullP)  + " (" + f.format(winFullP * 100) + "%)" + "\r\n");

			scenarioTimes[k] = diagEval.AvgTime;
			scenarioTimesMap.put(scenario, diagEval.AvgTime);

			/**
			 * Write the file
			 */
			resultsLogger.addRow(summary.toString());
			resultsLogger.writeFile();
		}

		System.out.println("Overall results for " + inputFilename);

//		for (int i = 0; i < scenarios.size(); i++) {
//			for (int k = i + 1; k < scenarios.size(); k++) {
//				Scenario scn1 = scenarios.get(i);
//				Scenario scn2 = scenarios.get(k);
//				String scn1Name = getScenarioDescription(scn1);
//				String scn2Name = getScenarioDescription(scn2);
//
//				System.out.println("Speedup from " + scn1Name + " to " + scn2Name + ": " + (scenarioTimes[i] / scenarioTimes[k]));
//				System.out.println("Speedup from " + scn2Name + " to " + scn1Name + ": " + (scenarioTimes[k] / scenarioTimes[i]));
//				System.out.println();
//			}
//		}

		scenarioTimesMap = sortByValue(scenarioTimesMap);
		for (Scenario scn: scenarioTimesMap.keySet()) {
			double time = scenarioTimesMap.get(scn);
			System.out.println(getScenarioDescription(scn) + ": " + time);
		}

	}
	
	private String getScenarioDescription(Scenario scenario) {
		StringBuilder sb = new StringBuilder();
		if (scenario.pruningMode == pruningMode.on) {
			sb.append("PRUNING_ON");
		}
		else {
			sb.append("PRUNING_OFF");
		}
		if (scenario.executionMode == SpreadsheetsIndividual.executionMode.fullparallel) {
			sb.append("_PARALLEL_FULL_");
			sb.append(scenario.threads);
		}
		else if (scenario.executionMode == SpreadsheetsIndividual.executionMode.levelparallel)  {
			sb.append("_PARALLEL_LEVEL_");
			sb.append(scenario.threads);
		}
		else if (scenario.executionMode == SpreadsheetsIndividual.executionMode.heuristic)  {
			sb.append("_HEURISTIC_");
			sb.append(scenario.threads);
		}
		else if (scenario.executionMode == SpreadsheetsIndividual.executionMode.hybrid)  {
			sb.append("_HYBRID_");
			sb.append(scenario.threads);
		}
		else {
			sb.append("_SINGLE_THREADED");
		}
		if (scenario.choco3) {
			sb.append("_CHOCO3");
		}
		else {
			sb.append("_CHOCO2");
		}
		return sb.toString();
	}


	// Running modes
	enum pruningMode {
		on, off
	}

	enum executionMode {fullparallel, levelparallel, singlethreaded, heuristic, hybrid}

	/**
	 * A scenario wrapper
	 *
	 * @author dietmar
	 */
	class Scenario {
		public int threads;
		public executionMode executionMode;
		public pruningMode pruningMode;
		public boolean choco3;

		public Scenario(
				evaluations.old.SpreadsheetsIndividual.executionMode executionMode,
				evaluations.old.SpreadsheetsIndividual.pruningMode pruningMode,
				int threads,
				boolean choco3) {
			super();
			this.threads = threads;
			this.executionMode = executionMode;
			this.pruningMode = pruningMode;
			this.choco3 = choco3;
		}


	}

	/**
	 * Writes the result of one diagnosis process to the log data structure
	 * @param diagnosisEngine
	 */
//	void appendResultToLog(AbstractHSDagEngine diagnosisEngine, long duration, List<Diagnosis> diagnoses, int threads){
//		//record data for this run.
//		ExquisiteAppXML appXML = diagnosisEngine.getDiagnosisModel().appXML;
//		ExcelExquisiteSession sessionData = diagnosisEngine.getDiagnosisModel();
//		
//		
//		String loggingResult = "";
//		int varCount = appXML.getInputs().size() + appXML.getInterims().size() + appXML.getOutputs().size();
//		loggingResult += "" + 				varCount + separator;
//		loggingResult += "" + 				sessionData.getDiagnosisModel().getConstraintNames().size() + separator;
//		loggingResult += "" + 				diagnosisEngine.getPropagationCount() + separator;
//		loggingResult += "" + 				diagnosisEngine.getCspSolvedCount() + separator;
//		loggingResult += "" + 				duration + separator;
//		loggingResult += "" + 				diagnosisEngine.getDiagnosisModel().getConfiguration().searchDepth + separator;
//		
//		loggingResult += "" + 				Utilities.printSortedDiagnoses(diagnoses, ' ') + separator;
//		
//		String threadPoolContent = "" + threads;
//		// The default engine is single threaded
//		if (diagnosisEngine instanceof HSDagEngine){
//			threadPoolContent = "0";
//		}
//		loggingResult += "" + 				threadPoolContent + separator;
//		
//		loggingResult += "" + 				diagnoses.size();
//		
//		this.loggingData.addRow(loggingResult);
//		
//	}
	

	
	/**
	 * Set up the logging for an individual file
	 */
//	void setupLogging(String logFileName){
//		try {
//			final boolean AppendContent = false;
//			Logger loggerInstance;
//			loggerInstance = ExquisiteLogger.setup(logFileName, AppendContent);
//			this.loggingData = new LoggingData(loggerInstance);
//			String logFileHeader = 			"#Vars" + separator + 
//											"#Constraints" + separator + 
//											"#CSP props." + separator + 
//											"#CSP solved" + separator + 
//											"Diag. time (ms)" + separator + 
//											"Max Search Depth" + separator + 
//											"Diagnoses" + separator +			
//											"ThreadPoolSize" + separator +
//											"#Diags";
//						
//			this.loggingData.addRow(logFileHeader);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//	}

	
	
}
