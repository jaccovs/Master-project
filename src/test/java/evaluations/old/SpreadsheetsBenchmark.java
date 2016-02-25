package evaluations.old;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import evaluations.models.LoggingData;
import org.exquisite.data.ConstraintsFactory;
import org.exquisite.data.DiagnosisModelLoader;
import org.exquisite.data.VariablesFactory;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.i8n.Culture;
import org.exquisite.i8n.CultureInfo;
import org.exquisite.logging.ExquisiteLogger;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import static org.exquisite.core.measurements.MeasurementManager.COUNTER_CSP_SOLUTIONS;
import static org.exquisite.core.measurements.MeasurementManager.COUNTER_PROPAGATION;
import static org.exquisite.core.measurements.MeasurementManager.getCounter;

/**
 * For generating data for the AppInt2013 paper.
 */
public class SpreadsheetsBenchmark {
	
	
	public static boolean SHUFFLE_POSSIBLY_FAULTY_CONSTRAINTS = false;
	
	public static boolean PRUNE_IRRELEVANT_CELLS = false;
	
	public static int ITERATION_COUNT = 100;
	
	public static String LOG_PATH = ".\\logs\\spreadsheetsbenchmark\\";
	
	private static String pruningPath = "";
	private static EngineType engineConfig = EngineType.HSDagStandardQX;//default setting.
	private static int threadPoolSize = 1; //default threadpool size (for parallel tree engine).
	/**
	 * Name of the logfile where test data will be written to.
	 */
	private String logfileName;
	private int maxSearchDepth = -1;
	/**
	 * Default csv delimter.
	 */
	private String separator = ";";
	/**
	 * CultreInfo object to alter csv delimter if needed.
	 */
	private CultureInfo currentCulture;
	/**
	 * Simple util class  hold data from test runs and writing data to log file.
	 */
	private LoggingData loggingData;

	public SpreadsheetsBenchmark(String logfileName, int maxSearchDepth) {
		Culture.setCulture(Locale.GERMAN);
		this.currentCulture = Culture.getCurrentCulture();
		this.separator = currentCulture.CSV_DELIMITER();
		this.logfileName = logfileName;
		this.maxSearchDepth = maxSearchDepth;
		engineConfig = EngineType.HSDagStandardQX;
		setupLogging();
	}
	
	
	/**
	 * Optional constructor to specify different tree engine configuration.
	 * @param logfileName
	 * @param maxSearchDepth
	 * @param config
	 */
	public SpreadsheetsBenchmark(String logfileName, int maxSearchDepth, int maxThreadPoolSize) {
		Culture.setCulture(Locale.GERMAN);
		this.currentCulture = Culture.getCurrentCulture();
		this.separator = currentCulture.CSV_DELIMITER();
		this.logfileName = logfileName;
		this.maxSearchDepth = maxSearchDepth;
		engineConfig = EngineType.ParaHSDagStandardQX;
		threadPoolSize = maxThreadPoolSize;
		setupLogging();
	}

	/**
	 * Runs a series of tests.
	 * Test output is stored in logs located under logs\enase-2013\
	 * @param args
	 */
	public static void main(String[]args)
	{

		// DJ: Check multiple QX
		//AbstractHSDagEngine.USE_QXTYPE = QuickXplainType.ParallelQuickXplain;

		Debug.DEBUGGING_ON = false;
//		Debug.QX_DEBUGGING = true;


		System.out.println("Starting Spreadsheet Benchmark tests.");

//		AppInt2013.runSingleFaultParallelTests(1);

//		AppInt2013.runSingleFaultParallelTests(2);

//		AppInt2013.runScenarios();

		for (int i = 1; i <= 1; i++)
		{
			PRUNE_IRRELEVANT_CELLS = i % 2 == 0;
			if (PRUNE_IRRELEVANT_CELLS)
			{
				System.out.println("Pruning irrelevant cells on.");
				pruningPath = "pruningOn\\";
			}
			else
			{
				System.out.println("Pruning irrelevant cells off.");
				pruningPath = "pruningOff\\";
			}

//			System.out.print("    Running single fault tests... ");
//			SpreadsheetsBenchmark.runSingleFaultTests(ITERATION_COUNT);
//			System.out.println("completed.");

			System.out.print("    Running double fault tests... ");
			SpreadsheetsBenchmark.runDoubleFaultTests(ITERATION_COUNT);
			System.out.println("completed.");

			//System.out.print("    Running small corpus examples... ");
			//		AppInt2013.runSmallCorpusExample();
			//		System.out.println("completed.");
//
//			for (int k = 1; k <= 5; k++)
//			{
//				int threads;
//
//				switch (k)
//				{
//					case 5:
//						threads = 8;
//						break;
//					case 4:
//						threads = 6;
//						break;
//					case 3:
//						threads = 4;
//						break;
//					default:
//						threads = k;
//						break;
//				}
//
//
//				System.out.print("    Running single fault parallel tests with " + threads + " threads... ");
//				SpreadsheetsBenchmark.runSingleFaultParallelTests(threads, ITERATION_COUNT);
//				System.out.println("completed.");
//
//				System.out.print("    Running double fault parallel tests with " + threads + " threads... ");
//				SpreadsheetsBenchmark.runDoubleFaultParallelTests(threads, ITERATION_COUNT);
//				System.out.println("completed.");
//			}
		}

//		AppInt2013.runBigExample();
		System.out.println("Tests finished.");
	}
	
	/**
	 * Runs a single fault test case
	 */
	public static void runSingleFaultTests(int iterationCount)
	{
		final int IterationCount = iterationCount;
		final int SearchDepth = 2;
		final String SingleFaultDir = ".\\experiments\\spreadsheetsbenchmark\\singleFault\\";
		final String LogPath = "sequential\\";
		SpreadsheetsBenchmark singleFaultTestRunner = null;

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault50.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_50.xml", 10, "50", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault30.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_30.xml", IterationCount, "30", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault20.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_20.xml", IterationCount, "20", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault10.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_10.xml", IterationCount, "10", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault09.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_09.xml", IterationCount, "9", "1");


		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault08.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_08.xml", IterationCount, "8", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault07.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_07.xml", IterationCount, "7", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault06.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_06.xml", IterationCount, "6", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault05.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_05.xml", IterationCount, "5", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault04.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_04.xml", IterationCount, "4", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault03.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_03.xml", IterationCount, "3", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault02.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_02.xml", IterationCount, "2", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault50.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_50.xml", IterationCount, "50", "1");
	}
	
	/**
	 * Runs the double fault test cases
	 */
	public static void runDoubleFaultTests(int iterationCount)
	{
		final int IterationCount = iterationCount;
		final int SearchDepth = 2;
		final String XmlPath = ".\\experiments\\spreadsheetsbenchmark\\doubleFault\\";
		final String LogPath = "sequential\\";
		SpreadsheetsBenchmark doubleFaultTestRunner = null;

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault50.csv", SearchDepth);
		doubleFaultTestRunner.run(XmlPath + "ex50.xml", IterationCount, "50", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault30.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex30.xml", IterationCount, "30", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault20.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex20.xml", IterationCount, "20", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault10.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex10.xml", IterationCount, "10", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault09.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex09.xml", IterationCount, "9", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault08.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex08.xml", IterationCount, "8", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault07.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex07.xml", IterationCount, "7", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault06.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex06.xml", IterationCount, "6", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault05.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex05.xml", IterationCount, "5", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault04.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex04.xml", IterationCount, "4", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault03.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex03.xml", IterationCount, "3", "2");
//
//		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault02.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex02.xml", IterationCount, "2", "2");
	}
	
	/**
	 * Runs a single fault test case
	 */
	public static void runSingleFaultParallelTests(int maxThreadSize, int iterationCount)
	{
		final int IterationCount = iterationCount;
		final int SearchDepth = 2;
		final String SingleFaultDir = ".\\experiments\\spreadsheetsbenchmark\\singleFault\\";
		final String LogPath = "parallel\\" + maxThreadSize + "\\";

		SpreadsheetsBenchmark singleFaultTestRunner = null;

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault50.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_50.xml", IterationCount, "50", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault30.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_30.xml", IterationCount, "30", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault20.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_20.xml", IterationCount, "20", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault10.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_10.xml", IterationCount, "10", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault09.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_09.xml", IterationCount, "9", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault08.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_08.xml", IterationCount, "8", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault07.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_07.xml", IterationCount, "7", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault06.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_06.xml", IterationCount, "6", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault05.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_05.xml", IterationCount, "5", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault04.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_04.xml", IterationCount, "4", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault03.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_03.xml", IterationCount, "3", "1");

		singleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "singleFault02.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_02.xml", IterationCount, "2", "1");

//		singleFaultTestRunner = new AppInt2013(LogPath + "singleFault50.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_50.xml", IterationCount, "50", "1");
	}
	
	/**
	 * Runs the double fault test cases
	 */
	public static void runDoubleFaultParallelTests(int maxThreadSize, int iterationCount)
	{
		final int IterationCount = iterationCount;
		final int SearchDepth = 2;
		final String XmlPath = ".\\experiments\\spreadsheetsbenchmark\\doubleFault\\";
		final String LogPath = "parallel\\" + maxThreadSize + "\\";

		SpreadsheetsBenchmark doubleFaultTestRunner = null;

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault50.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex50.xml", IterationCount, "50", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault30.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex30.xml", IterationCount, "30", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault20.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex20.xml", IterationCount, "20", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault10.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex10.xml", IterationCount, "10", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault09.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex09.xml", IterationCount, "9", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault08.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex08.xml", IterationCount, "8", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault07.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex07.xml", IterationCount, "7", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault06.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex06.xml", IterationCount, "6", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault05.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex05.xml", IterationCount, "5", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault04.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex04.xml", IterationCount, "4", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault03.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex03.xml", IterationCount, "3", "2");

		doubleFaultTestRunner = new SpreadsheetsBenchmark(LogPath + "doubleFault02.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex02.xml", IterationCount, "2", "2");
	}

	private void setupLogging() {
		try {

			final boolean AppendContent = false;
			Logger loggerInstance;
			loggerInstance = ExquisiteLogger.setup(LOG_PATH + pruningPath + this.logfileName, AppendContent);
			this.loggingData = new LoggingData(loggerInstance);
			String logFileHeader = "#Prods" + separator +
					"#Vars" + separator +
					"#Constraints" + separator +
					"#CSP props." + separator +
					"#CSP solved" + separator +
					"Diag. time (ms)" + separator +
					"Max Search Depth" + separator +
					"Mutations" + separator +
					"Diagnoses" + separator +
					"ThreadPoolSize" + separator +
					"#Diags";


			this.loggingData.addRow(logFileHeader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Main running method
	 *
	 * @param xmlFilePath
	 * @param iterations
	 */
	public void run(String xmlFilePath, int iterations, String products, String mutations) {
		long overallDuration = 0;
		for (int i = 0; i < iterations; i++) {
			try {
				ConstraintsFactory.PRUNE_IRRELEVANT_CELLS = PRUNE_IRRELEVANT_CELLS;
				ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(xmlFilePath);
				ExcelExquisiteSession sessionData = new ExcelExquisiteSession(appXML);
				ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
				Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
				VariablesFactory varFactory = new VariablesFactory(variablesMap);
				DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
				modelLoader.loadDiagnosisModelFromXML();

				IDiagnosisEngine<Constraint> diagnosisEngine;
				sessionData.getConfiguration().searchStrategy = SearchStrategies.Default;

//				if (SHUFFLE_POSSIBLY_FAULTY_CONSTRAINTS) {
//					sessionData.getDiagnosisModel().shufflePossiblyFaulyConstraints();
//				}

				diagnosisEngine = EngineFactory.makeEngine(engineConfig, sessionData, threadPoolSize);
//				diagnosisEngine.setMaxSearchDepth(this.maxSearchDepth);

				/*
				// Use the new sorting algorithm
				HashSet<String> inputs = new HashSet<String>(appXML.getInputs());
				List<Constraint> splitPoints = new ArrayList<Constraint>();
				// Some manual test

				String[] sortedVarnames = new String[] { "WS_1_S9", "WS_1_S8",
						"WS_1_S7", "WS_1_S6", "WS_1_S5", "WS_1_S4", "WS_1_S3",
						"WS_1_S2", "WS_1_S1", "WS_1_S10", "WS_1_T13",

						"WS_1_T14", "WS_1_T8", "WS_1_T7", "WS_1_T10",
						"WS_1_T9", "WS_1_T4", "WS_1_T3", "WS_1_T6", "WS_1_T5",
						"WS_1_T1", "WS_1_T2",

						"WS_1_U7", "WS_1_U6", "WS_1_U5", "WS_1_U4",
						"WS_1_U9", "WS_1_U8", "WS_1_U2", "WS_1_U3", "WS_1_U10",
						"WS_1_U1", "WS_1_T16", "WS_1_T15",};
//				// Get a reverse map
				Map<Constraint, String> ctNames = model.getConstraintNames();
				Map<String, Constraint> ctsByName = Utilities.reverse(ctNames);
				List<Constraint> sortedConstraints = new ArrayList<Constraint>();
				for (String ctname : sortedVarnames) {
					sortedConstraints.add(ctsByName.get(ctname));
				}
				splitPoints.add(ctsByName.get("WS_1_T13"));
				splitPoints.add(ctsByName.get("WS_1_T2"));
				splitPoints.add(ctsByName.get("WS_1_U1"));
				splitPoints.add(ctsByName.get("WS_1_T16"));
				List<Constraint> sortedPossiblyFaulty = BaseQuickXPlain.sortConstraintsByArityAndCalculuateSplittingPoints(model, inputs, splitPoints);
				System.out.println("Sorted constraints: " + Utilities.printConstraintList(sortedPossiblyFaulty, model));
				model.setPossiblyFaultyStatements(sortedPossiblyFaulty);
				model.setPossiblyFaultyStatements(sortedConstraints);
				model.setSplitPoints(splitPoints);
				*/
//				Debug.DEBUGGING_ON = true;

				//Make a call to the diagnosis engine.
				long startTime = System.currentTimeMillis();
				List<Diagnosis<Constraint>> diagnoses = diagnosisEngine.calculateDiagnoses();
				long endTime = System.currentTimeMillis();
				long duration = endTime - startTime;
				overallDuration += duration;
				System.out.println("Found " + diagnoses.size() + " diagnoses in " + duration + "ms");

//				Debug.DEBUGGING_ON = false;

				//record data for this run.
				String loggingResult = "";
				loggingResult += products + separator;
				int varCount = appXML.getInputs().size() + appXML.getInterims().size() + appXML.getOutputs().size();
				loggingResult += varCount + separator;
				loggingResult += sessionData.getDiagnosisModel().getConstraintNames().size() + separator;
				loggingResult += getCounter(COUNTER_PROPAGATION).value() + separator;
				loggingResult += getCounter(COUNTER_CSP_SOLUTIONS).value() + separator;
				loggingResult += duration + separator;
				loggingResult += this.maxSearchDepth + separator;
				loggingResult += mutations + separator;

				for (int j = 0; j < diagnoses.size(); j++) {
//					loggingResult+="(Diag # " + j + ": " + Utilities.printConstraintList(diagnoses.get(j).getElements(), sessionData.getDiagnosisModel()) + ") ";
				}
				loggingResult += Utilities.printSortedDiagnoses(diagnoses, ' ') + separator;

				System.out.println(
						"Number of known conflicts: " + ((AbstractHSDagEngine) diagnosisEngine).knownConflicts
								.getCollection().size());
				List<List<Constraint>> theknown = ((AbstractHSDagEngine) diagnosisEngine).knownConflicts
						.getCollection();
				for (List<Constraint> conflict : theknown) {
					System.out
							.println(Utilities.printConstraintListOrderedByName(conflict, sessionData.getDiagnosisModel()));
				}


				String threadPoolContent = "" + threadPoolSize;
				if (engineConfig != EngineType.ParaHSDagStandardQX) {
					threadPoolContent = "0";
				}
				loggingResult += threadPoolContent + separator;

				loggingResult += diagnoses.size();

				this.loggingData.addRow(loggingResult);
			} catch (DiagnosisException e) {
				this.loggingData.addRow("DomainException caught for this test run.");
			}
		}
		this.loggingData.writeToFile();
		System.out.println("Duration average: " + overallDuration / iterations);
	}
}
