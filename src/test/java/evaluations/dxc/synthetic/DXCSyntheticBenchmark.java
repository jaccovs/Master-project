package evaluations.dxc.synthetic;

import choco.kernel.model.constraints.Constraint;
import evaluations.dxc.synthetic.model.DXCScenarioData;
import evaluations.dxc.synthetic.model.DXCSystem;
import evaluations.dxc.synthetic.model.DXCSystemDescription;
import evaluations.dxc.synthetic.tools.DXCDiagnosisModelGenerator;
import evaluations.dxc.synthetic.tools.DXCTools;
import evaluations.tools.DiagnosisEvaluation;
import evaluations.tools.ResultsLogger;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Benchmark tests for the DXC competition syntetic track
 * https://sites.google.com/site/dxcompetition2011/tracks/synthetic-track---diagnostic-problem-iii
 * @author Thomas
 *
 */
public class DXCSyntheticBenchmark {

	// Running modes
	static String separator = ";";
	
	// Runs will be added for initialization
	static int nbInitizializationRuns = 20;
	static int nbTestRuns = 100;
	static int K_lb = 0;
	static int K_ub = 2;

	static boolean shuffleConstraints = true;
	
	static boolean useLastLevelCheck = true;
	
	static boolean useResultsDirectory = true;
	
	static int threadPoolSize = 4;
	
	static int maxDiagSize = -1;
	static int maxDiagnoses = -1;
	static int waittime;
	
	
	
	// ----------------------------------------------------
	// Directories
	static String inputFileDirectory = "experiments/DXCSynthetic/";
	static String logFileDirectory = "logs/DXCSynthetic/";
	// ----------------------------------------------------
	static DXCTestScenario[] testScenarios = new DXCTestScenario[] {
		// maxDiagSize = -1
		new DXCTestScenario("74182.xml", "74182/74182.%03d.scn", 0, 19),
		new DXCTestScenario("74L85.xml", "74L85/74L85.%03d.scn", 0, 19),
		new DXCTestScenario("74283.xml", "74283/74283.%03d.scn", 0, 19),

		// maxDiagSize = 6/5
//		new DXCTestScenario("74181.xml", "74181/74181.%03d.scn", 0, 7, 6, -1),
//		new DXCTestScenario("74181.xml", "74181/74181.%03d.scn", 8, 9, 5, -1),
//		new DXCTestScenario("74181.xml", "74181/74181.%03d.scn", 10, 15, 6, -1),
//		new DXCTestScenario("74181.xml", "74181/74181.%03d.scn", 16, 16, 6, -1), // Maybe 6 is too slow
//		new DXCTestScenario("74181.xml", "74181/74181.%03d.scn", 17, 19, 6, -1),

		// maxDiagSize = faultSize
		new DXCTestScenario("74182.xml", "74182/74182.%03d.scn", 0, 19, -2, -1),
		new DXCTestScenario("74L85.xml", "74L85/74L85.%03d.scn", 0, 19, -2, -1),
		new DXCTestScenario("74283.xml", "74283/74283.%03d.scn", 0, 19, -2, -1),
		new DXCTestScenario("74181.xml", "74181/74181.%03d.scn", 0, 19, -2, -1), // Takes too long without search depth limitation
		new DXCTestScenario("c432.xml", "c432/c432.%03d.scn", 0, 19, -2, -1), // Takes too long without search depth limitation
	};
	ResultsLogger resultsLogger = null;
	String resultsDirectory = "";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Overwrite this
		QuickXPlain.PRINT_SOLUTION_TIME = false;
		

		NodeExpander.USE_LAST_LEVEL_CHECK = useLastLevelCheck;

		DXCSyntheticBenchmark test = new DXCSyntheticBenchmark();
		
		if (useResultsDirectory) {
			Timestamp tstamp = new Timestamp(System.currentTimeMillis());
			String time = tstamp.toString();
			time = time.replace(':', '-').substring(0, time.length() - 4);
					
			test.resultsDirectory = time + "/";
		}
		
		System.out.println("Starting tests");
		

		Debug.DEBUGGING_ON = false;
		Debug.QX_DEBUGGING = false;
		
		for (DXCTestScenario testScenario: testScenarios) {
			for (int i = testScenario.ScenarioStart; i <= testScenario.ScenarioEnd; i++) {
			
				try {
					maxDiagSize = testScenario.MaxDiagSize;
					maxDiagnoses = testScenario.MaxDiagnoses;
					waittime = -1;
					
					QuickXPlain.ARTIFICIAL_WAIT_TIME = -1;
						
					test.runExampleFromFile(testScenario.SystemDescriptionFile, String.format(testScenario.ScenarioFile, i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		
		System.out.println("Tests end here");
	}


	private void runExampleFromFile(String systemDescriptionFile,
			String scenarioFile) throws Exception {
		int nbRuns = nbInitizializationRuns + nbTestRuns;

		DXCSystemDescription sd = DXCTools.readSystemDescription(inputFileDirectory + systemDescriptionFile);
		DXCSystem system = sd.getSystems().get(0);
		DXCScenarioData scn = DXCTools.readScenario(inputFileDirectory + scenarioFile, system);
		
		// With a value of -2 the maxDiagSize is set to the size of the actual error of the scenario
		if (maxDiagSize == -2) {
			maxDiagSize = scn.getFaultyComponents().size();
			System.out.println("Set maxDiagSize to " + maxDiagSize + ".");
		}

		// Create the logger
		resultsLogger = new ResultsLogger(separator, logFileDirectory + resultsDirectory, "", scenarioFile + maxDiagSize + "_" + waittime
				+ ".csv");
		StringBuilder summary = new StringBuilder();
		
		summary.append("\r\n");
		summary.append("System description file: " + systemDescriptionFile + "\r\n");
		summary.append("Scenario file: " + scenarioFile + "\r\n");
		summary.append("MaxDiagSize: " + maxDiagSize + "\r\n");
		summary.append("MaxDiagnoses: " + maxDiagnoses + "\r\n");
		summary.append("WaitTime: " + waittime + "\r\n");
		summary.append("Initialization Runs: " + nbInitizializationRuns + "\r\n");
		summary.append("Test Runs: " + nbTestRuns + "\r\n");
		summary.append("Shuffle Constraints: " + shuffleConstraints + "\r\n");
		summary.append("Use LastLevelCheck: " + useLastLevelCheck + "\r\n");
		
		System.out.println("------------------------");
		System.out.println("Starting to diagnose");

		// Create the engine
		// ExquisiteSession sessionData = new ExquisiteSession(null, null,
		// diagModel);
		// Do not try to find a better strategy for the moment
		// sessionData.config.searchStrategy = SearchStrategies.Default;
		// sessionData.config.searchStrategy =
		// SearchStrategies.ImpactBasedBranching;
		// sessionData.config.searchStrategy =
		// SearchStrategies.AssignOrForbidIntVarVal_DomWDegBin;
		// sessionData.config.searchStrategy =
		// SearchStrategies.AssignVar_MinDomIncDom;
		// sessionData.config.searchStrategy =
		// SearchStrategies.AssignOrForbidIntVarVal_RandomIntBinSearch;
		// sessionData.config.searchStrategy =
		// SearchStrategies.AssignVar_MinDomIncDom;
		// sessionData.config.searchStrategy =
		// SearchStrategies.AssignVar_MinDomDecDom;

		// Debug.DEBUGGING_ON = true;
		// Debug.QX_DEBUGGING = true;
		
		// Remember the total times
		double totalSeq = 0;
		double totalLevelW = 0;
		double totalFullP = 0;

		List<Diagnosis<Constraint>> diagnoses = null;
		for (int k = K_lb; k <= K_ub; k++) {
			System.out.println("===================== k=" + k
					+ " ===================================");

			DiagnosisEvaluation<Constraint> diagEval = new DiagnosisEvaluation<Constraint>();

			boolean initFinished = false;
			System.out.println("Initialization");

			// Write a separator line
			resultsLogger.writeSeparatorLine("");
			resultsLogger.writeSeparatorLine("Version:" + k);

			// Give it an extra run (first one will not be counted
			for (int i = 0; i < nbRuns; i++) {
				if (i == nbInitizializationRuns) {
					initFinished = true;
					System.out.println();
					System.out.println("Initialization finished");
				}

				QuickXPlain.reuseCount = 0;

				IDiagnosisEngine<Constraint> engine = null;
				// Create the diagnosis model
				sd = DXCTools.readSystemDescription(inputFileDirectory + systemDescriptionFile);
				system = sd.getSystems().get(0);
				scn = DXCTools.readScenario(inputFileDirectory + scenarioFile, system);
				DiagnosisModel<Constraint> diagModel = new DXCDiagnosisModelGenerator()
						.createDiagnosisModel(system, scn.getFaultyState());
				// System.out.println(diagModel.getVariables());
				
				if (!DXCTools.checkCorrectState(system, scn)) {
					throw new Exception("Scenario is not correct!");
				}
				
				if (shuffleConstraints)
					diagModel.shufflePossiblyFaulyConstraints();

				// Create the engine
				ExquisiteSession sessionData = new ExquisiteSession(null,
						null, new DiagnosisModel<Constraint>(diagModel));
				// Do not try to find a better strategy for the moment
				sessionData.config.searchStrategy = SearchStrategies.Default;
				sessionData.config.searchDepth = maxDiagSize;
				sessionData.config.maxDiagnoses = maxDiagnoses;
				// sessionData.config.maxDiagnoses = 1;
				

				if (k == 0) {
					// System.out.println("Creating sequential engine");
					engine = EngineFactory
							.makeDAGEngineStandardQx(sessionData);
				} else if (k == 1) {
					// System.out.println("Creating level-wise parallel engine with "
					// + threadPoolSize + " threads");
					engine = EngineFactory.makeParaDagEngineStandardQx(
							sessionData, threadPoolSize);
				} else {
					// System.out.println("Creating full parallel engine with "
					// + threadPoolSize + " threads");
					engine = EngineFactory.makeFullParaDagEngineStandardQx(
							sessionData, threadPoolSize);
				}
				long start = System.currentTimeMillis();
				diagnoses = engine.calculateDiagnoses();
				long end = System.currentTimeMillis();
				


				// System.err.println("Solver calls: " +
				// engine.getSolverCalls());
				// System.err.println("Solved csp: " +
				// engine.getCspSolvedCount());
				// System.err.println("propag: " +
				// engine.getPropagationCount());
				// System.err.println("NB Diags: " + diagnoses.size());
				// System.out.println(diagnoses);
				//

				// System.out.println("Average diag size: " + (avgDiagSize /
				// (float) diagnoses.size()));

				// System.out.println("SIZE OF known solutions: " +
				// QuickXPlain.knownSolutions.size());
				// System.out.println("Reuse count:   " +
				// QuickXPlain.reuseCount);

				// //////////////////////////////////////////////////////////////

				if (initFinished) {
					diagEval.analyzeRun((AbstractHSDagBuilder)engine, end - start);

					// record data for this run.
					String loggingResult = "";
					loggingResult += sessionData.diagnosisModel.getVariables().size() + separator;
					loggingResult += sessionData.diagnosisModel
							.getConstraintNames().size() + separator;
					loggingResult += engine.getPropagationCount()
							+ separator;
					loggingResult += engine.getCspSolvedCount() + separator;
					loggingResult += (end - start) + separator;
					loggingResult += sessionData.config.searchDepth
							+ separator;

//					for (int j = 0; j < diagnoses.size(); j++) {
						// loggingResult+="(Diag # " + j + ": " +
						// Utilities.printConstraintList(diagnoses.get(j).getElements(),
						// sessionData.diagnosisModel) + ") ";
//					}
					if (i == nbInitizializationRuns)
					{
						loggingResult += Utilities.printSortedDiagnoses(
								diagnoses, ' ') + separator;
					} else {
						loggingResult += separator;
					}

					String threadPoolContent = "" + threadPoolSize;

					loggingResult += threadPoolContent + separator;

					loggingResult += diagnoses.size();

					resultsLogger.addRow(loggingResult);
				} else {
					System.out.print(".");
				}
				
				if (DXCTools.checkFaultyComponentsinDiagnoses(diagModel, diagnoses, system, scn) == null) {
					resultsLogger.addRow("Correct diagnosis not found!");
				}

				// //////////////////////////////////////////////////////////////

			}

			diagEval.finishCalculation();
			
			summary.append("\r\n");
			switch (k) {
			case 0:
				summary.append("Sequential version:\r\n");
				totalSeq = diagEval.AvgTime;
				break;
			case 1:
				summary.append("Level-wise parallel version:\r\n");
				totalLevelW = diagEval.AvgTime;
				break;
			case 2:
				summary.append("Full parallel version:\r\n");
				totalFullP = diagEval.AvgTime;
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
			summary.append("Average conflict reuse: " + diagEval.AvgConflictReuse + "\r\n");


		}

		// Calculate average improvements
		double winLWP = 1 - (totalLevelW / totalSeq);
		double winFullP = 1 - (totalFullP / totalSeq);
		
		DecimalFormat f = new DecimalFormat("#");
		
		// The most important thing  repeated
		summary.append("\r\n\r\nTotal Seq: " + f.format(totalSeq) +"\r\n");
		summary.append("Total Lev: " + f.format(totalLevelW) + " (" + f.format(winLWP * 100) + "%)" + "\r\n");
		summary.append("Total Full:" + f.format(totalFullP)  + " (" + f.format(winFullP * 100) + "%)" + "\r\n");
		
		
		/**
		 * Write the file
		 */
		resultsLogger.addRow(summary.toString());
		resultsLogger.writeFile();
	}
	

}
