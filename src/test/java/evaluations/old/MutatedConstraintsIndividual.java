package evaluations.old;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import evaluations.plainconstraints.PlainConstraintsUtilities;
import evaluations.plainconstraints.TestCaseGenerator;
import evaluations.tools.DiagnosisEvaluation;
import evaluations.tools.ResultsLogger;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.engines.common.NodeExpander;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.diagnosis.quickxplain.choco3.C3Runner;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;
import solver.constraints.IntConstraintFactory;

import java.io.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Doing tests with plain constraints.
 * 
 * @author dietmar
 * 
 */
public class MutatedConstraintsIndividual {
	static String directory = "experiments/mutatedconstraints/";
	static TestScenario[] testScenarios = new TestScenario[]{
			// paper
//		new TestScenario("normalized-aim-50-1-6-3.xml", 3, -1),
//		new TestScenario("normalized-c8.xml", -1, 10),
//		new TestScenario("normalized-costasArray-13.xml", 3, -1),
//		new TestScenario("normalized-domino-100-100.xml", 3, -1),
//		new TestScenario("normalized-e0ddr1-10-by-5-8.xml", -1, -1),
//		new TestScenario("normalized-graceful--K3-P2.xml", 3, -1),	// INCONSISTENT RESULTS IN SEQUENTIAL MODE!!!!!!!!!!!!
//		new TestScenario("normalized-mknap-1-5.xml", 3, 10),
//		new TestScenario("normalized-primes-15-20-3-1.xml", -1, 10),
//		new TestScenario("normalized-ruler-34-8-a3.xml", 3, -1),
//		new TestScenario("normalized-series-13.xml", 3, -1),
//
//		// other
//		new TestScenario("normalized-mknap-1-5.xml", -1, 10),
//		new TestScenario("normalized-c8.xml", -1, -1),
//		new TestScenario("normalized-c8.xml", -1, 10),
//		new TestScenario("normalized-e0ddr1-10-by-5-8.xml", 3, -1),
//		new TestScenario("normalized-ex5-pi.xml", 3, -1),
			new TestScenario("normalized-queens-8.xml", -1, -1),
//		new TestScenario("normalized-queens-8.xml", -1, 10),
//		new TestScenario("normalized-protein.xml", -1, -1),
//		new TestScenario("normalized-renault-mod-13_ext.xml", -1, -1),
//		new TestScenario("normalized-domino-100-100.xml", -1, -1),
//		new TestScenario("normalized-primes-10-20-2-3.xml", -1, 10),
//		new TestScenario("normalized-prom2-pi.xml", 3, -1),
//		new TestScenario("normalized-prom2-pi.xml", -1, -1),
//
//		// long paper
//		new TestScenario("normalized-graph2.xml", 3, -1), // takes very long
//		new TestScenario("normalized-fischer-1-1-fair.xml", 3, -1), // takes very long

			// to test
//		new TestScenario("normalized-e0ddr1-10-by-5-6.xml", 3, -1),
//		new TestScenario("normalized-ruler-34-8-a3.xml", 3, 10),

			// tested
//		new TestScenario("normalized-aim-50-1-6-3.xml", 3, 10),
//		new TestScenario("normalized-mknap-1-5.xml", -1, -1),
//		new TestScenario("normalized-protein.xml", -1, 10),
//		new TestScenario("normalized-domino-100-100.xml", 3, 10),
//		new TestScenario("normalized-primes-15-20-3-1.xml", -1, -1),
//		new TestScenario("normalized-primes-10-20-2-3.xml", -1, -1),

			// long other
//		new TestScenario("normalized-graceful--K3-P2.xml", 4, -1), // takes very long
//		new TestScenario("normalized-e0ddr1-10-by-5-1.xml", 3, -1), // takes very long
//		new TestScenario("normalized-ruler-34-8-a3.xml", 4, -1), // takes too long
//		new TestScenario("normalized-patat-02-small-2.xml", 1, -1), // takes too long
//		new TestScenario("normalized-bibd-10-30-9-3-2_glb.xml", 3, -1), // takes forever at 10% of runs, should be last one
	};
	static int maxDiagSize = -1;
	// MEASUREMENTS DONE AND IN PAPER (4 threads)
	// String filename = "normalized-graceful--K3-P2.xml"; // 3 inputs,
	// superfast. TS: seq: 3.5 lwpara: 2.0 fpara: 1.8
	// String filename = "normalized-costasArray-13.xml"; // will work with 5
	// inputs and a number of tries in 200ms. TS: seq: 1.9 lwpara: 1.1 fpara:
	// 0.9
	// String filename = "normalized-mknap-1-5.xml"; // TS: seq: 133ms lwpara:
	// 66ms fpara: 52ms
	// String filename = "normalized-graph2.xml"; // 10 vars, 300ms. TS: seq:
	// 79s lwpara: 70s fpara:62s
	// String filename = "normalized-c8.xml"; // works well with 20 inputs and
	// more , bin. TS: No mutated file!

	// OTHERS
	// TS: Did all tests with max diag size = 3!!!
	// String filename = "normalized-bibd-8-14-7-4-3_glb.xml"; // works well
	// with 25 inputs, bin. TS: No mutated file!
	// static String filename = "normalized-bibd-10-30-9-3-2_glb.xml"; // works
	// well with 25 inputs, bin. TS: seq: 2.8 lwpara: 3.4 fpara: 3.1
	// String filename = "normalized-e0ddr1-10-by-5-1.xml"; // some solutions
	// take very long? TS: seq: 143s lwpara: 332s fpara 256s
	// String filename = "normalized-e0ddr1-10-by-5-6.xml"; //will work with 5
	// inputs and a number of tries in 15 ms. TS: No mutated file!
	// String filename = "normalized-e0ddr1-10-by-5-8.xml"; // can take some
	// time with 5 inputs. about 2 secs for a solution. DJ: Does not crash
	// anymore, but no diag found..
	// String filename = "normalized-ewddr2-10-by-5-1.xml"; //// can take some
	// time with 5 inputs. about 2 secs for a solution. TS: No mutated file!
	// String filename = "normalized-ex5-pi.xml"; // 5 inputs are good (80ms),
	// binary. TS: seq: 12.1 lwpara: 12.0 fpara: 11.8

//	static String[] inputfiles = new String[] {
//	 	"normalized-graceful--K3-P2.xml",
//	 	"normalized-costasArray-13.xml",
//	 	"normalized-mknap-1-5.xml",
//	 	"normalized-graph2.xml",
//	 	"normalized-c8.xml",
//		"normalized-aim-50-1-6-3.xml"
//	 	"normalized-renault-mod-13_ext.xml" // Leads to 15% on level-w, and plus 10% on full. (4 diags of size 4)
//	 	"normalized-c8.xml",
//		"normalized-aim-50-1-6-3.xml",
//	 	"normalized-renault-mod-13_ext.xml", // Leads to 15% on level-w, and plus 10% on full. (4 diags of size 4)
//		"normalized-domino-100-100.xml"
//		"normalized-e0ddr1-10-by-5-6.xml", // This is slower ...
//		"normalized-ex5-pi.xml" // single element conflict
//		"normalized-fischer-1-1-fair.xml"
//		"normalized-pack.xml" // takes forever .. overnight?
//		"normalized-patat-02-small-2.xml" // takes forever.. overnight?
//		"normalized-pigeons-6.xml"
//		"normalized-primes-10-20-2-3.xml"
//		"normalized-primes-15-20-3-1.xml"
//		"normalized-prom2-pi.xml", // ok, this is an example for which the parallelization does not work. caching does not help either.
//		"normalized-queens-8.xml", // seq: 376 lwpara: 366 fpara: 475
//		"normalized-protein.xml", // almost no parallelization possible
//		"normalized-ruler-34-8-a3.xml", // maxDiagSize = 3 // Duplicates FOUND with diag size 3!!!
//		"normalized-e0ddr1-10-by-5-1.xml",
//		"normalized-e0ddr1-10-by-5-8.xml",
//		"normalized-bibd-10-30-9-3-2_glb.xml",
//		"normalized-bibd-8-14-7-4-3_glb.xml",
//		"normalized-series-13.xml",
//	};
// Runs will be added for initialization
static int nbInitizializationRuns = 20;



	// String filename = ""; //
	// String filename = ""; //
	// String filename = ""; //
	// String filename = ""; //
	// String filename = ""; //

	// String filename = "normalized-aim-50-1-6-3.xml"; // dj: no
	static int nbTestRuns = 100;
	static int K_lb = 0;
	static int K_ub = 2;
	static int T_cases = 10;
	static int T_input_vars = 5;
	static double T_pct_store = 5;
	static int T_maxtries = 5000;
	static int waittime;
	static int threadPoolSize = 4;
	static String separator = ";";
	static boolean storeTestCases = true;
	static boolean shuffleConstraints = true;
	static boolean useLastLevelCheck = true;
	static boolean useResultsDirectory = true;
	ResultsLogger resultsLogger = null;
	String resultsDirectory = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) {


//		MutatedConstraintsIndividual t = new MutatedConstraintsIndividual();
//		t.runSimpleExample();
//
//		if (true) {
//			return;
//		}

		// Overwrite this
		QuickXPlain.PRINT_SOLUTION_TIME = false;


		NodeExpander.USE_LAST_LEVEL_CHECK = useLastLevelCheck;

		MutatedConstraintsIndividual test = new MutatedConstraintsIndividual();

		if (useResultsDirectory) {
			Timestamp tstamp = new Timestamp(System.currentTimeMillis());
			String time = tstamp.toString();
			time = time.replace(':', '-').substring(0, time.length() - 4);

			test.resultsDirectory = time + "/";
		}

		System.out.println("Starting tests");
		try {

			Debug.DEBUGGING_ON = false;
			Debug.QX_DEBUGGING = false;

//			for (String filename : inputfiles)
//				test.runExampleFromFile(filename);

			for (TestScenario testScenario: testScenarios) {
				maxDiagSize = testScenario.MaxDiagSize;
				waittime = testScenario.Waittime;

				QuickXPlain.ARTIFICIAL_WAIT_TIME = waittime;

				test.runExampleFromFile(testScenario.Filename);
			}
			// test.runSimpleExample();
			// test.runXMLTests("src\\tests\\exquisite\\data\\exquisite_10_sf_if_b.xml",
			// nbRuns, 6, 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Tests end here");

	}

	/**
	 * A "manual" example..
	 */
	void runSimpleExample() {
		DiagnosisModel<Constraint> model = createSimpleDiagnosisModel();

		ExquisiteSession sessionData = new ExquisiteSession();

		sessionData.diagnosisModel = model;
		sessionData.config.searchStrategy = SearchStrategies.Default;
		sessionData.config.searchDepth = -1;

		Debug.DEBUGGING_ON = true;

		List<Diagnosis<Constraint>> diagnoses = null;
		IDiagnosisEngine<Constraint> engine = EngineFactory
				.makeDAGEngineStandardQx(sessionData);
		// IDiagnosisEngine engine =
		// EngineFactory.makeParaDagEngineStandardQx(sessionData, 2);

		long stop = 0;
		long start = System.currentTimeMillis();
		try {
			diagnoses = engine.calculateDiagnoses();
			stop = System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}

		 System.out.println("Found " + diagnoses.size() + " diagnoses in " + (stop-start) + " millisecs");
		// System.out.println("Constructed nodes: " + ((AbstractHSDagBuilder)
		// engine).allConstructedNodes.getCollection().size());
		// // TEST ONLY
		// System.out.println(Utilities.printSortedDiagnoses(diagnoses, '\n'));
		// System.out.println("Nb of solves : " + engine.getCspSolvedCount());
		// System.out.println("Nb of s calls: " + engine.getSolverCalls());
		// System.out.println("Nb of props  : " + engine.getPropagationCount());
		// System.out.println("Reuse count:   " + QuickXPlain.reuseCount);

//		NodeUtilities.traverseNode(engine.getRootNode(),
//				sessionData.diagnosisModel.getConstraintNames());

	}

	/**
	 * Runs a number of tests using a specified xml file (ignoring the first
	 * run) Both types of engines are used
	 *
	 * @param xmlFile
	 * @param nbRuns
	 * @param threadPoolSize
	 */
	public void runXMLTests(String xmlFile, int nbRuns, int threadPoolSize,
			int searchDepth) {
		double avgStandard = 0;
		double avgParallel = 0;

		long total = 0;
		List<Diagnosis<Constraint>> diagnoses = null;
		for (int k = 0; k <= 1; k++) {
			total = 0;
			// Give it an extra run (first one will not be counted
			for (int i = 0; i < nbRuns; i++) {
				IDiagnosisEngine<Constraint> engine = null;
				// Create the diagnosis model
				if (k == 0) {
					engine = EngineFactory
							.makeEngineFromXMLFile(EngineType.HSDagStandardQX,
									xmlFile, threadPoolSize);
				} else {
					engine = EngineFactory.makeEngineFromXMLFile(
							EngineType.ParaHSDagStandardQX, xmlFile,
							threadPoolSize);
				}
				engine.getSessionData().config.searchDepth = searchDepth;

				long start = System.currentTimeMillis();
				long end = -1;
				try {
					diagnoses = engine.calculateDiagnoses();
					end = System.currentTimeMillis();
				} catch (DiagnosisException e) {
					System.err.println("Domainsizeexpection.. fatal");
					System.exit(1);
				}
				if (i != 0) {
					total += (end - start);
					// System.out.println("Solver calls: " +
					// engine.getSolverCalls());
					// System.out.println("Nb of props  : " +
					// engine.getPropagationCount());
					// System.out.println("Diagnoses:    " + diagnoses.size());
					//
					// System.out.println("Possibly faulty: " +
					// engine.getSessionData().diagnosisModel.getPossiblyFaultyStatements().size());
					// System.out.println("Variables : " +
					// engine.getSessionData().diagnosisModel.getVariables().size());
					//
					//
					float diagSize = 0;
					for (Diagnosis<Constraint> d : diagnoses) {
						if (d.getElements() != null) {
							diagSize += d.getElements().size();
						}
					}
					System.out.println("Average diag size: " + diagSize
							/ (float) diagnoses.size());

				}
			}
			double avg = total / (double) nbRuns;
			if (k == 0) {
				avgStandard = avg;
			} else {
				avgParallel = avg;
			}
		}
		System.out.println("Standard: time needed for " + nbRuns
				+ " runs on avg: " + (avgStandard));
		System.out.println("Parallel: time needed for " + nbRuns
				+ " runs on avg: " + (avgParallel));

	}

	/**
	 * Create a small test model
	 *
	 * @return the diagnosis model
	 */
	DiagnosisModel<Constraint> createSimpleDiagnosisModel() {
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();

		int domainSize = 10;

		IntegerVariable v1 = Choco.makeIntVar("v1", 0, domainSize);
		IntegerVariable v2 = Choco.makeIntVar("v2", 0, domainSize);
		IntegerVariable v3 = Choco.makeIntVar("v3", 0, domainSize);
		IntegerVariable v4 = Choco.makeIntVar("v4", 0, domainSize);
		IntegerVariable v5 = Choco.makeIntVar("v5", 0, domainSize);
		IntegerVariable v6 = Choco.makeIntVar("v6", 0, domainSize);

		model.addIntegerVariable(v1);
		model.addIntegerVariable(v2);
		model.addIntegerVariable(v3);
		model.addIntegerVariable(v4);
		model.addIntegerVariable(v5);
		model.addIntegerVariable(v6);

		Constraint c1 = Choco.eq(v1, v2);
		Constraint c2 = Choco.times(v1, v2, v3);
		Constraint c3 = Choco.eq(v4, v5);
		Constraint c4 = Choco.times(v4, v5, v6);

		model.addPossiblyFaultyConstraint(c1, "c1");
		model.addPossiblyFaultyConstraint(c2, "c2"); // should
																			// be
																			// plus
		model.addPossiblyFaultyConstraint(c3, "c3");
		model.addPossiblyFaultyConstraint(c4, "c4"); // should
																			// be
																			// plus
		Map<Constraint,C3Runner> c3runners = new HashMap<Constraint, C3Runner>();

		C3Runner c3runner = new C3Runner() {
			public void postConstraint() {
				solver.constraints.Constraint ct = IntConstraintFactory.arithm(var("v1"), "=",var("v2"));
				solver.post(ct);
			}
		};

		c3runners.put(c1,c3runner);

		c3runner = new C3Runner() {
			public void postConstraint() {
				solver.constraints.Constraint ct = IntConstraintFactory.times(var("v1"),var("v2"),var("v3"));
				solver.post(ct);
			}
		};
		c3runners.put(c2,c3runner);

		c3runner = new C3Runner() {
			public void postConstraint() {
				solver.constraints.Constraint ct = IntConstraintFactory.arithm(var("v4"), "=", var("v5"));
				solver.post(ct);
			}
		};

		c3runners.put(c3,c3runner);

		c3runner = new C3Runner() {
			public void postConstraint() {
				solver.constraints.Constraint ct = IntConstraintFactory.times(var("v4"), var("v5"),var("v6"));
				solver.post(ct);

			}
		};

		c3runners.put(c4,c3runner);

		// Set in the model
		model.c3runners = new HashMap<Constraint, C3Runner>(c3runners);


		// model.addPossiblyFaultyConstraint(c2, "c2");
		// model.addPossiblyFaultyConstraint(c3, "c3");
		// model.addPossiblyFaultyConstraint(c4, "c4");
		// model.addPossiblyFaultyConstraint(c5, "c5");
		// model.addPossiblyFaultyConstraint(c6, "c6");
		// model.addPossiblyFaultyConstraint(c7, "c7");
		// model.addPossiblyFaultyConstraint(c8, "c8");

		Example pos1 = new Example();
		pos1.addConstraint(Choco.eq(v1, 3), "v1=3");
		pos1.addConstraint(Choco.eq(v3, 6), "v3=6");
		pos1.addConstraint(Choco.eq(v4, 3), "v4=3");
		pos1.addConstraint(Choco.eq(v6, 6), "v6=6");

		model.getPositiveExamples().add(pos1);

		// Create a runner for the example
		c3runner = new C3Runner() {
			public void postConstraint() {

				solver.constraints.Constraint ct = IntConstraintFactory.arithm(var("v1"),"=",3);
				solver.post(ct);
				ct = IntConstraintFactory.arithm(var("v3"),"=",6);
				solver.post(ct);
				ct = IntConstraintFactory.arithm(var("v4"),"=",3);
				solver.post(ct);
				ct = IntConstraintFactory.arithm(var("v6"),"=",6);
				solver.post(ct);
			}
		};

		model.c3examplerunners.put(pos1, c3runner);

		// Example pos2 = new Example();
		// pos2.addConstraint(Choco.eq(v1, 3), "v1=3");
		// pos1.addConstraint(Choco.eq(v6, 6), "v6=6");

		// model.getPositiveExamples().add(pos2);

		return model;

	}

	/**
	 * Worker, does the main work.
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void runExampleFromFile(String filename) throws Exception {
		int nbRuns = nbInitizializationRuns + nbTestRuns;

		boolean reusePosTestCases = MutatedConstraintsIndividual.storeTestCases;
		List<Map<String, Integer>> posTestCases = null;
		// Load the file and get some test cases
		TestCaseGenerator tcg = new TestCaseGenerator(directory + filename);

		String tcFilename = filename + "_testCases";
		File tcFile = new File(directory + tcFilename);
		if (reusePosTestCases && tcFile.exists()) {
			// Load the file
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					tcFile));
			posTestCases = (List<Map<String, Integer>>) ois.readObject();
			ois.close();
		} else {
			// posTestCases = tcg.createPositiveTestCases(3, 30, 5, 400);
			posTestCases = tcg.createPositiveTestCases(T_cases, T_pct_store,
					T_input_vars, T_maxtries);
			if (reusePosTestCases) {
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(tcFile));
				oos.writeObject(posTestCases);
				oos.close();
			}
		}

		System.out.println("----------- ENDED WITH TEST CASE GENERATION ------------------------");

		// Now look at the mutated model
		String mutatedFile = PlainConstraintsUtilities
				.getMutatedFileName(filename);
		System.out.println("Mutated file: " + mutatedFile);
//		CPModel mutatedModel = PlainConstraintsUtilities.loadModel(directory + mutatedFile);
		// Go through the test cases
		int nbConsistent = 0;
		int nbInconsistent = 0;
		CPModel mutatedModel = null;

		for (Map<String, Integer> testCase : posTestCases) {
			// DJ: RELOAD every time - not good. But we seem to lose some variables somewhere when using table constraints
			mutatedModel = PlainConstraintsUtilities.loadModel(directory
					+ mutatedFile);
			boolean consistent = PlainConstraintsUtilities
					.isTestCaseConsistent(testCase, mutatedModel);
			if (consistent) {
				System.out.println("Test case is consistent...");
				nbConsistent++;
			} else {
				System.out.println("Test case is not consistent");
				nbInconsistent++;
				// DEBUG STOP AFTER FIRST
//				break;
			}
		}

		System.out.println("Test case summary");
		System.out.println("Consistent  : " + nbConsistent);
		System.out.println("InConsistent: " + nbInconsistent);

		// Ok, there's a problem. Let's run the diagnosis.
		if (nbInconsistent > 0) {
			// Create the logger
			resultsLogger = new ResultsLogger(separator, directory + resultsDirectory, "", filename + maxDiagSize + "_" + waittime
					+ ".csv");
			StringBuilder summary = new StringBuilder();

			summary.append("\r\n");
			summary.append("File: " + filename + "\r\n");
			summary.append("MaxDiagSize: " + maxDiagSize + "\r\n");
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
					mutatedModel = PlainConstraintsUtilities
							.loadModel(directory + mutatedFile);
					DiagnosisModel<Constraint> diagModel = PlainConstraintsUtilities
							.createDiagnosisModel(mutatedModel, posTestCases);
					// System.out.println(diagModel.getVariables());

					if (shuffleConstraints)
						diagModel.shufflePossiblyFaulyConstraints();

					// Create the engine
					ExquisiteSession sessionData = new ExquisiteSession(null,
							null, new DiagnosisModel<Constraint>(diagModel));
					// Do not try to find a better strategy for the moment
					sessionData.config.searchStrategy = SearchStrategies.Default;
					sessionData.config.searchDepth = maxDiagSize;
					// sessionData.config.maxDiagnoses = 1;

					// DJ: Remove all but one of the inconsistent test cases for the moment (not mentioned in paper)
					// 23.12.2013
//					Example ex = sessionData.diagnosisModel.getPositiveExamples().get(0);
//					sessionData.diagnosisModel.getPositiveExamples().clear();
//					sessionData.diagnosisModel.getPositiveExamples().add(ex);
//					sessionData.diagnosisModel.getNegativeExamples().clear();


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
						int varCount = mutatedModel.getNbIntVars();
						loggingResult += varCount + separator;
						loggingResult += sessionData.diagnosisModel
								.getConstraintNames().size() + separator;
						loggingResult += engine.getPropagationCount()
								+ separator;
						loggingResult += engine.getCspSolvedCount() + separator;
						loggingResult += (end - start) + separator;
						loggingResult += sessionData.config.searchDepth
								+ separator;

						for (int j = 0; j < diagnoses.size(); j++) {
							// loggingResult+="(Diag # " + j + ": " +
							// Utilities.printConstraintList(diagnoses.get(j).getElements(),
							// sessionData.diagnosisModel) + ") ";
						}
						loggingResult += Utilities.printSortedDiagnoses(
								diagnoses, ' ') + separator;

						String threadPoolContent = "" + threadPoolSize;

						loggingResult += threadPoolContent + separator;

						loggingResult += diagnoses.size();

						resultsLogger.addRow(loggingResult);
					} else {
						System.out.print(".");
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

		// // Do not count the first one
		// if (i != 0) {
		//
		// total += (end - start);
		// System.out.println("Found " + diagnoses.size() + " diagnoses");
		// for (Diagnosis d: diagnoses) {
		// System.out.println("Diag:");
		// List<Constraint> diagElements =
		// Utilities.hashSetToList(d.getElements());
		// for (Constraint c : diagElements) {
		// System.out.print(c + " ");
		// }
		// System.out.println();
		// }
		// }
		//
		// }
		//
		//
		// }
	}


	enum ExecutionMode {fullparallel, levelparallel, singlethreaded, heuristic, hybrid}

	class RunConfiguration {
		public int threads;
		public ExecutionMode executionMode;

		public RunConfiguration(int threads, ExecutionMode executionMode) {
			this.threads = threads;
			this.executionMode = executionMode;
		}
	}

}
