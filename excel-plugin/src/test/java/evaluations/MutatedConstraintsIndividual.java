package evaluations;

import choco.cp.model.CPModel;
import choco.kernel.model.constraints.Constraint;
import evaluations.configuration.AbstractRunConfiguration;
import evaluations.configuration.AbstractScenario;
import evaluations.configuration.StdRunConfiguration;
import evaluations.configuration.StdRunConfiguration.ExecutionMode;
import evaluations.configuration.StdScenario;
import evaluations.plainconstraints.PlainConstraintsUtilities;
import evaluations.plainconstraints.TestCaseGenerator;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.engines.AbstractHSDagEngine.QuickXplainType;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain.SolverType;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Evaluation for the individual mutated constraint satisfaction problems.
 * For documentation of overridden methods, see AbstractEvaluation
 * @author Thomas
 */
public class MutatedConstraintsIndividual extends AbstractEvaluation<Constraint> {

	// ----------------------------------------------------
	// Directories
	static String inputFileDirectory = "experiments/mutatedconstraints/";
	static String logFileDirectory = "logs/mutatedconstraints/";
	// Number of runs
	static int nbInitRuns = 20;
	static int nbTestRuns = 100;
	// Settings for newly generated testcases
	static int T_cases = 10;
	static int T_input_vars = 5;
	static double T_pct_store = 5;
	// ----------------------------------------------------
	static int T_maxtries = 5000;
	// Run configurations
	static StdRunConfiguration[] runConfigurations = new StdRunConfiguration[] {
//		new StdRunConfiguration(ExecutionMode.singlethreaded, 1, true),
////		new StdRunConfiguration(ExecutionMode.levelparallel, 2, true),
////		new StdRunConfiguration(ExecutionMode.fullparallel, 2, true),
////		new StdRunConfiguration(ExecutionMode.heuristic, 2, true),
////		new StdRunConfiguration(ExecutionMode.hybrid, 2, true),
//		new StdRunConfiguration(ExecutionMode.levelparallel, 4, true),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 4, true),
//		new StdRunConfiguration(ExecutionMode.heuristic, 1, true),
//		new StdRunConfiguration(ExecutionMode.heuristic, 2, true),
//		new StdRunConfiguration(ExecutionMode.heuristic, 3, true),
//		new StdRunConfiguration(ExecutionMode.heuristic, 4, true),
//		new StdRunConfiguration(ExecutionMode.hybrid, 4, true),
//		new StdRunConfiguration(ExecutionMode.prdfs, 1, true),
//		new StdRunConfiguration(ExecutionMode.prdfs, 2, true),
//		new StdRunConfiguration(ExecutionMode.prdfs, 3, true),
//		new StdRunConfiguration(ExecutionMode.prdfs, 4, true),

		new StdRunConfiguration(ExecutionMode.singlethreaded, 1, true),
//		new StdRunConfiguration(ExecutionMode.mergexplain, 1, true),
//		new StdRunConfiguration(ExecutionMode.parallelmergexplain, 4, false),
//		new StdRunConfiguration(ExecutionMode.levelparallel, 2, false),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 2, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 1, false),
//		new StdRunConfiguration(ExecutionMode.hybrid, 2, false),
//		new StdRunConfiguration(ExecutionMode.levelparallel, 4, true),
		new StdRunConfiguration(ExecutionMode.fullparallel, 4, true),
		new StdRunConfiguration(ExecutionMode.fullparallel, 8, true),
		new StdRunConfiguration(ExecutionMode.fullparallel, 10, true),
		new StdRunConfiguration(ExecutionMode.fullparallel, 12, true),
		new StdRunConfiguration(ExecutionMode.fullparallel, 16, true),
		new StdRunConfiguration(ExecutionMode.fullparallel, 20, true),
//		new StdRunConfiguration(ExecutionMode.fpandmxp, 4, true),
//		new StdRunConfiguration(ExecutionMode.continuingfpandmxp, 4, true),
//		new StdRunConfiguration(ExecutionMode.heuristic, 1, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 2, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 3, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 4, false),
//		new StdRunConfiguration(ExecutionMode.hybrid, 4, false),
//		new StdRunConfiguration(ExecutionMode.prdfs, 4, false),
	};

	// Standard scenario settings
//	static int searchDepth = -1;
//	static int maxDiags = 1;
	// Scenarios
	static StdScenario[] scenarios = new StdScenario[] {
//		new StdScenario("normalized-c8.xml", -1, 1),
//		new StdScenario("normalized-domino-100-100.xml", -1, 1),
//		new StdScenario("normalized-graceful--K3-P2.xml", -1, 1),
//		new StdScenario("normalized-mknap-1-5.xml", -1, 1),
//		new StdScenario("normalized-queens-8.xml", -1, 1),
//		new StdScenario("normalized-costasArray-13.xml", -1, 1),

//		new StdScenario("normalized-c8.xml", -1, 5),
//		new StdScenario("normalized-domino-100-100.xml", -1, 5),
//		new StdScenario("normalized-graceful--K3-P2.xml", -1, 5),
//		new StdScenario("normalized-mknap-1-5.xml", -1, 5),
//		new StdScenario("normalized-queens-8.xml", -1, 5),
//		new StdScenario("normalized-costasArray-13.xml", 3, 2),

		new StdScenario("normalized-c8.xml", -1, -1),
		new StdScenario("normalized-domino-100-100.xml", -1, -1),
		new StdScenario("normalized-graceful--K3-P2.xml", 3, -1),
		new StdScenario("normalized-mknap-1-5.xml", -1, -1),
			new StdScenario("normalized-queens-8.xml", -1, -1),
		new StdScenario("normalized-costasArray-13.xml", 3, -1),


//		new StdScenario("normalized-aim-50-1-6-3.xml", -1, 5),
//		new StdScenario("normalized-aim-50-1-6-3.xml", -1, 6),
//		new StdScenario("normalized-aim-50-1-6-3.xml", -1, 7),
//		new StdScenario("normalized-aim-50-1-6-3.xml", -1, 8),
//		new StdScenario("normalized-aim-50-1-6-3.xml", -1, 9),
//		new StdScenario("normalized-aim-50-1-6-3.xml", -1, 10),
//
//		new StdScenario("normalized-costasArray-13.xml", -1, 5),
//		new StdScenario("normalized-costasArray-13.xml", -1, 6),
//		new StdScenario("normalized-costasArray-13.xml", -1, 7),
//		new StdScenario("normalized-costasArray-13.xml", -1, 8),
//		new StdScenario("normalized-costasArray-13.xml", -1, 9),
//		new StdScenario("normalized-costasArray-13.xml", -1, 10),

//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 5),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 6),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 7),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 8),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 9),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 10),
//
//		new StdScenario("normalized-graceful--K3-P2.xml", -1, 5),
//		new StdScenario("normalized-graceful--K3-P2.xml", -1, 6),
//		new StdScenario("normalized-graceful--K3-P2.xml", -1, 7),
//		new StdScenario("normalized-graceful--K3-P2.xml", -1, 8),
//		new StdScenario("normalized-graceful--K3-P2.xml", -1, 9),
//		new StdScenario("normalized-graceful--K3-P2.xml", -1, 10),

//		new StdScenario("normalized-series-13.xml", -1, 5),
//		new StdScenario("normalized-series-13.xml", -1, 6),
//		new StdScenario("normalized-series-13.xml", -1, 7),
//		new StdScenario("normalized-series-13.xml", -1, 8),
//		new StdScenario("normalized-series-13.xml", -1, 9),
//		new StdScenario("normalized-series-13.xml", -1, 10),
////		// other
//		new StdScenario("normalized-mknap-1-5.xml", -1, 5),
//		new StdScenario("normalized-mknap-1-5.xml", -1, 6),
//		new StdScenario("normalized-mknap-1-5.xml", -1, 7),
//		new StdScenario("normalized-mknap-1-5.xml", -1, 8),
//		new StdScenario("normalized-mknap-1-5.xml", -1, 9),
//		new StdScenario("normalized-mknap-1-5.xml", -1, 10),
//
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, 5),
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, 6),
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, 7),
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, 8),
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, 9),
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, 10),
//
//		new StdScenario("normalized-ruler-34-8-a3.xml", -1, 5),
//		new StdScenario("normalized-ruler-34-8-a3.xml", -1, 6),
//		new StdScenario("normalized-ruler-34-8-a3.xml", -1, 7),
//		new StdScenario("normalized-ruler-34-8-a3.xml", -1, 8),
//		new StdScenario("normalized-ruler-34-8-a3.xml", -1, 9),
//		new StdScenario("normalized-ruler-34-8-a3.xml", -1, 10),
//
//		new StdScenario("normalized-c8.xml", -1, 5),
//		new StdScenario("normalized-c8.xml", -1, 6),
//		new StdScenario("normalized-c8.xml", -1, 7),
//		new StdScenario("normalized-c8.xml", -1, 8),
//		new StdScenario("normalized-c8.xml", -1, 9),
//		new StdScenario("normalized-c8.xml", -1, 10),
//
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 5),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 6),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 7),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 8),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 9),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 10),
//
//		new StdScenario("normalized-ex5-pi.xml", -1, 5),
//		new StdScenario("normalized-ex5-pi.xml", -1, 6),
//		new StdScenario("normalized-ex5-pi.xml", -1, 7),
//		new StdScenario("normalized-ex5-pi.xml", -1, 8),
//		new StdScenario("normalized-ex5-pi.xml", -1, 9),
//		new StdScenario("normalized-ex5-pi.xml", -1, 10),
//
//		new StdScenario("normalized-queens-8.xml", -1, 5),
//		new StdScenario("normalized-queens-8.xml", -1, 6),
//		new StdScenario("normalized-queens-8.xml", -1, 7),
//		new StdScenario("normalized-queens-8.xml", -1, 8),
//		new StdScenario("normalized-queens-8.xml", -1, 9),
//		new StdScenario("normalized-queens-8.xml", -1, 10),
//
//		new StdScenario("normalized-protein.xml", -1, 5),
//		new StdScenario("normalized-protein.xml", -1, 6),
//		new StdScenario("normalized-protein.xml", -1, 7),
//		new StdScenario("normalized-protein.xml", -1, 8),
//		new StdScenario("normalized-protein.xml", -1, 9),
//		new StdScenario("normalized-protein.xml", -1, 10),
//
//		new StdScenario("normalized-renault-mod-13_ext.xml", -1, 5),
//		new StdScenario("normalized-renault-mod-13_ext.xml", -1, 6),
//		new StdScenario("normalized-renault-mod-13_ext.xml", -1, 7),
//		new StdScenario("normalized-renault-mod-13_ext.xml", -1, 8),
//		new StdScenario("normalized-renault-mod-13_ext.xml", -1, 9),
//		new StdScenario("normalized-renault-mod-13_ext.xml", -1, 10),
//
//		new StdScenario("normalized-domino-100-100.xml", -1, 5),
//		new StdScenario("normalized-domino-100-100.xml", -1, 6),
//		new StdScenario("normalized-domino-100-100.xml", -1, 7),
//		new StdScenario("normalized-domino-100-100.xml", -1, 8),
//		new StdScenario("normalized-domino-100-100.xml", -1, 9),
//		new StdScenario("normalized-domino-100-100.xml", -1, 10),
//
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, 5),
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, 6),
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, 7),
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, 8),
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, 9),
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, 10),
//
//		new StdScenario("normalized-prom2-pi.xml", -1, 5),
//		new StdScenario("normalized-prom2-pi.xml", -1, 6),
//		new StdScenario("normalized-prom2-pi.xml", -1, 7),
//		new StdScenario("normalized-prom2-pi.xml", -1, 8),
//		new StdScenario("normalized-prom2-pi.xml", -1, 9),
//		new StdScenario("normalized-prom2-pi.xml", -1, 10),
////		// long paper
//		new StdScenario("normalized-graph2.xml", -1, 5),
//		new StdScenario("normalized-graph2.xml", -1, 6),
//		new StdScenario("normalized-graph2.xml", -1, 7),
//		new StdScenario("normalized-graph2.xml", -1, 8),
//		new StdScenario("normalized-graph2.xml", -1, 9),
//		new StdScenario("normalized-graph2.xml", -1, 10),
//
//		new StdScenario("normalized-fischer-1-1-fair.xml", -1, 5),
//		new StdScenario("normalized-fischer-1-1-fair.xml", -1, 6),
//		new StdScenario("normalized-fischer-1-1-fair.xml", -1, 7),
//		new StdScenario("normalized-fischer-1-1-fair.xml", -1, 8),
//		new StdScenario("normalized-fischer-1-1-fair.xml", -1, 9),
//		new StdScenario("normalized-fischer-1-1-fair.xml", -1, 10),


			// Settings for finding 1 diagnosis (All engines except levelparallel)
			// paper
//		new StdScenario("normalized-aim-50-1-6-3.xml", -1, 1, 10),
//		new StdScenario("normalized-c8.xml", -1, 1, 10),
//		new StdScenario("normalized-costasArray-13.xml", -1, 1),
//		new StdScenario("normalized-domino-100-100.xml", -1, 1, 10),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 1),
//		new StdScenario("normalized-graceful--K3-P2.xml", -1, 1),	// INCONSISTENT RESULTS IN SEQUENTIAL MODE!!!!!!!!!!!!
//		new StdScenario("normalized-queens-8.xml", -1, 1, 10),
//		new StdScenario("normalized-series-13.xml", -1, 1),
////		// other
//		new StdScenario("normalized-mknap-1-5.xml", -1, 1, 10),
//		new StdScenario("normalized-mknap-1-5.xml", -1, 1, -1),
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, 1, 10),
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, 1, -1),
//		new StdScenario("normalized-ruler-34-8-a3.xml", -1, 1),
//		new StdScenario("normalized-c8.xml", -1, 1),
//		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, 1),
//		new StdScenario("normalized-ex5-pi.xml", -1, 1), // Choco 3 takes quite long per run (60 secs)
//		new StdScenario("normalized-queens-8.xml", -1, 1),
//		new StdScenario("normalized-protein.xml", -1, 1),
//		new StdScenario("normalized-renault-mod-13_ext.xml", -1, 1), // TODO _ext files not supported yet by choco 3
//		new StdScenario("normalized-domino-100-100.xml", -1, 1),
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, 1, 10),
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, 1, -1),
//		new StdScenario("normalized-prom2-pi.xml", -1, 1), // Choco 3 takes quite long per run (60 secs)
////		// long paper
//		new StdScenario("normalized-graph2.xml", -1, 1), // takes very long
//		new StdScenario("normalized-fischer-1-1-fair.xml", -1, 1), // takes very long
//		// to test
//		new StdScenario("normalized-e0ddr1-10-by-5-6.xml", -1, 1),
//		new StdScenario("normalized-ruler-34-8-a3.xml", -1, 1, 10),
//		// tested
//		new StdScenario("normalized-aim-50-1-6-3.xml", -1, 1, 10),
//		new StdScenario("normalized-mknap-1-5.xml", -1, 1),
//		new StdScenario("normalized-protein.xml", -1, 1, 10),
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, 1),
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, 1),
//		// long other
//		new StdScenario("normalized-graceful--K3-P2.xml", -1, 1), // takes very long
//		new StdScenario("normalized-e0ddr1-10-by-5-1.xml", -1, 1), // takes very long
//		new StdScenario("normalized-ruler-34-8-a3.xml", -1, 1), // takes too long
//		new StdScenario("normalized-patat-02-small-2.xml", -1, 1), // takes too long
////			new StdScenario("normalized-bibd-10-30-9-3-2_glb.xml", -1, 1), // takes forever at 10% of runs, should be last one


			// Settings for finding all diagnoses (No Heuristic / Hybrid engines!)
		// paper
////		new StdScenario("normalized-aim-50-1-6-3.xml", 3, -1, 10), // TODO Choco 3 solver takes ultra long sometimes
//		new StdScenario("normalized-c8.xml", -1, -1, 10),
//		new StdScenario("normalized-costasArray-13.xml", 3, -1), // TODO different results for Choco 2 and 3
//		new StdScenario("normalized-domino-100-100.xml", 3, -1, 10),
////		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", -1, -1), // TODO Choco 3 solver does not finish for a solve
//		new StdScenario("normalized-graceful--K3-P2.xml", 3, -1),	// INCONSISTENT RESULTS IN SEQUENTIAL MODE!!!!!!!!!!!!
//		new StdScenario("normalized-queens-8.xml", -1, -1, 10),
////		new StdScenario("normalized-series-13.xml", 3, -1), // TODO Choco 3 solver does not finish for the first solve
//////		// other
//		new StdScenario("normalized-mknap-1-5.xml", 3, -1, 10),
//		new StdScenario("normalized-mknap-1-5.xml", 3, -1, -1),
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, -1, 10),
//		new StdScenario("normalized-primes-15-20-3-1.xml", -1, -1, -1),
////		new StdScenario("normalized-ruler-34-8-a3.xml", 3, -1),
//		new StdScenario("normalized-mknap-1-5.xml", -1, -1, 10),
//		new StdScenario("normalized-c8.xml", -1, -1),
////		new StdScenario("normalized-e0ddr1-10-by-5-8.xml", 3, -1),
//		new StdScenario("normalized-ex5-pi.xml", 3, -1), // Choco 3 takes quite long per run (60 secs)
//		new StdScenario("normalized-queens-8.xml", -1, -1),
//		new StdScenario("normalized-protein.xml", -1, -1),
////		new StdScenario("normalized-renault-mod-13_ext.xml", -1, -1), // TODO _ext files not supported yet by choco 3
//		new StdScenario("normalized-domino-100-100.xml", -1, -1),
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, -1, 10),
//		new StdScenario("normalized-primes-10-20-2-3.xml", -1, -1, -1),
////		new StdScenario("normalized-prom2-pi.xml", 3, -1), // No diagnoses up to this level
//		new StdScenario("normalized-prom2-pi.xml", -1, -1), // Choco 3 takes quite long per run (60 secs)
//////		// long paper
////		new StdScenario("normalized-fischer-1-1-fair.xml", 3, -1), // takes very long // TODO Choco 3 solver does not finish
////		new StdScenario("normalized-graph2.xml", 3, -1), // takes very long // TODO different results for Choco 2 and 3 // TODO Choco 3 solver does not finish sometimes
////		// to test
////		new StdScenario("normalized-e0ddr1-10-by-5-6.xml", 3, -1), // TODO Choco 3 solver does not finish for a solve
//////		new StdScenario("normalized-ruler-34-8-a3.xml", 3, -1, 10),
////		// tested
//////		new StdScenario("normalized-aim-50-1-6-3.xml", 3, -1),
//////		new StdScenario("normalized-mknap-1-5.xml", -1, -1),
//////		new StdScenario("normalized-protein.xml", -1, -1, 10),
//////		new StdScenario("normalized-domino-100-100.xml", 3, -1),
////		new StdScenario("normalized-primes-15-20-3-1.xml", -1, -1),
//////		new StdScenario("normalized-primes-10-20-2-3.xml", -1, -1),
////		// long other
//////		new StdScenario("normalized-graceful--K3-P2.xml", 4, -1), // takes very long
//////		new StdScenario("normalized-e0ddr1-10-by-5-1.xml", 3, -1), // takes very long
//////		new StdScenario("normalized-ruler-34-8-a3.xml", 4, -1), // takes too long
//////		new StdScenario("normalized-patat-02-small-2.xml", 1, -1), // takes too long
//////		new StdScenario("normalized-bibd-10-30-9-3-2_glb.xml", 3, -1), // takes forever at 10% of runs, should be last one
	};
	List<Map<String, Integer>> posTestCases = null;

	public static void main(String[] args) {
		MutatedConstraintsIndividual mutatedConstraintsIndividual = new MutatedConstraintsIndividual();


		mutatedConstraintsIndividual.runTests(nbInitRuns, nbTestRuns, runConfigurations, scenarios);
	}

	@Override
	public String getEvaluationName() {
		return "MutatedConstraintsIndividual";
	}

	@Override
	public String getResultPath() {
		return logFileDirectory;
	}

	@Override
	public String getConstraintOrderPath() {
		return inputFileDirectory;
	}

	@Override
	protected boolean shouldShuffleConstraints() {
		return true;
	}

	@Override
	public boolean alwaysWriteDiagnoses() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean prepareScenario(AbstractScenario abstractScenario) {
		try
		{
			StdScenario scenario = (StdScenario)abstractScenario;

			// Load the file and get some test cases
			TestCaseGenerator tcg = new TestCaseGenerator(new File(inputFileDirectory, scenario.inputFileName).getAbsolutePath());

			File tcFile = new File(inputFileDirectory, scenario.inputFileName + "_testCases");
			if (tcFile.exists()) {
				// Load the file
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
						tcFile));
				posTestCases = (List<Map<String, Integer>>) ois.readObject();
				ois.close();
			} else {
				System.out.println(tcFile.getAbsolutePath() + " does not exist. Creating.");
				// posTestCases = tcg.createPositiveTestCases(3, 30, 5, 400);
				posTestCases = tcg.createPositiveTestCases(T_cases, T_pct_store, T_input_vars, T_maxtries);
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tcFile));
				oos.writeObject(posTestCases);
				oos.close();
			}
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public IDiagnosisEngine<Constraint> prepareRun(
			AbstractRunConfiguration abstractRunConfiguration,
			AbstractScenario abstractScenario, int subScenario, int iteration) {
		IDiagnosisEngine<Constraint> engine = null;
		StdRunConfiguration runConfiguration = (StdRunConfiguration)abstractRunConfiguration;
		StdScenario scenario = (StdScenario)abstractScenario;

		// Create the diagnosis model
		CPModel mutatedModel;
		try {
			String mutatedFile = PlainConstraintsUtilities.getMutatedFileName(scenario.inputFileName);

			HSDagEngine.USE_QXTYPE = QuickXplainType.QuickXplain;

			mutatedModel = PlainConstraintsUtilities
					.loadModel(new File(inputFileDirectory, mutatedFile).getAbsolutePath());
			DiagnosisModel<Constraint> diagModel = PlainConstraintsUtilities
					.createDiagnosisModel(mutatedModel, posTestCases);

			// Create the engine
			ExcelExquisiteSession<Constraint> sessionData = new ExcelExquisiteSession<>(null,
					null, new DiagnosisModel<>(diagModel));
			// Do not try to find a better strategy for the moment
			sessionData.getConfiguration().searchStrategy = SearchStrategies.Default;
			sessionData.getConfiguration().searchDepth = scenario.searchDepth;
			sessionData.getConfiguration().maxDiagnoses = scenario.maxDiags;

			EngineType engineType = chooseEngineType(scenario, runConfiguration);

			engine = EngineFactory.makeEngine(engineType, sessionData, runConfiguration.threads);

			ConstraintsQuickXPlain.ARTIFICIAL_WAIT_TIME = scenario.waitTime;
			if (runConfiguration.choco3) {
				ConstraintsQuickXPlain.SOLVERTYPE = SolverType.Choco3;
			} else {
				ConstraintsQuickXPlain.SOLVERTYPE = SolverType.Choco2;
			}

			return engine;

		} catch (Exception e) {
			addError(e.getMessage(), abstractRunConfiguration, subScenario, iteration);
			return null;
		}
	}

}
