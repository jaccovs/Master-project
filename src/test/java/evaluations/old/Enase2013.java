package evaluations.old;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

import org.exquisite.data.ConstraintsFactory;
import org.exquisite.data.DiagnosisModelLoader;
import org.exquisite.data.VariablesFactory;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.datamodel.TestCase;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.i8n.Culture;
import org.exquisite.i8n.CultureInfo;
import org.exquisite.logging.ExquisiteLogger;
import org.exquisite.tools.Utilities;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.ImpactBasedBranching;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import evaluations.models.LoggingData;
import evaluations.tools.PositiveTestCaseGenerator;

/**
 * For generating data for the Enase2013 paper.
 */
public class Enase2013 {
		
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
	
	
	private static EngineType engineConfig = EngineType.HSDagStandardQX;//default setting.
	private static int threadPoolSize = 4; //default threadpool size (for parallel dag engine).
	
	/**
	 * Runs a series of tests. 
	 * Test output is stored in logs located under logs\enase-2013\
	 * @param args
	 */
	public static void main(String[]args)
	{
		System.out.println("Starting ENASE 2013 tests.");
		
//		Enase2013.runSingleFaultParallelTests(1);		
//		Enase2013.runSingleFaultParallelTests(8);

//		Enase2013.runDoubleFaultParallelTests(1);
		
//		Enase2013.runDoubleFaultParallelTests(8);
		
//		Enase2013.runScenarios();
		
		System.out.print("    Running single fault tests... ");
		Enase2013.runSingleFaultTests();
		System.out.println("completed.");
		
		//System.out.print("    Running double fault tests... ");
		//Enase2013.runDoubleFaultTests();
		//System.out.println("completed.");
		
		//System.out.print("    Running small corpus examples... ");
//		Enase2013.runSmallCorpusExample();	
//		System.out.println("completed.");

		//System.out.print("    Running single fault parallel tests...");
		//Enase2013.runSingleFaultParallelTests(2);
//		//Enase2013.runSingleFaultParallelTests(3);
//		//Enase2013.runSingleFaultParallelTests(4);
//		//Enase2013.runSingleFaultParallelTests(5);
//		System.out.println("completed.");		
		
//		System.out.print("    Running double fault parallel tests...");
//		Enase2013.runDoubleFaultParallelTests(2);
//		Enase2013.runDoubleFaultParallelTests(3);
//		Enase2013.runDoubleFaultParallelTests(4);
//		Enase2013.runDoubleFaultParallelTests(5);
		System.out.println("completed.");
		
//		Enase2013.runBigExample();		
 	    System.out.println("Tests finished.");
	}
	
	
	public Enase2013(String logfileName, int maxSearchDepth)
	{		
		Culture.setCulture(Locale.GERMAN);
		this.currentCulture = Culture.getCurrentCulture();
		this.separator = currentCulture.CSV_DELIMITER();
		this.logfileName = logfileName;
		this.maxSearchDepth = maxSearchDepth;
		setupLogging();
	}
	
	/**
	 * Optional constructor to specify different dag engine configuration.
	 * @param logfileName
	 * @param maxSearchDepth
	 * @param config
	 */
	public Enase2013(String logfileName, int maxSearchDepth, int maxThreadPoolSize){
		Culture.setCulture(Locale.GERMAN);
		this.currentCulture = Culture.getCurrentCulture();
		this.separator = currentCulture.CSV_DELIMITER();
		this.logfileName = logfileName;
		this.maxSearchDepth = maxSearchDepth;
		engineConfig = EngineType.ParaHSDagStandardQX;
		threadPoolSize = maxThreadPoolSize;
		setupLogging();
	}
	
	
	
	
	private void setupLogging(){
		try {
			
			final boolean AppendContent = false;
			Logger loggerInstance;
			loggerInstance = ExquisiteLogger.setup(this.logfileName, AppendContent);
			this.loggingData = new LoggingData(loggerInstance);
			String logFileHeader = 	"#Prods" + separator + 
											"#Vars" + separator + 
											"#Constraints" + separator + 
											"#CSP props." + separator + 
											"#CSP solved" + separator + 
											"Diag. time (ms)" + separator + 
											"Max Search Depth" + separator + 
											"Mutations" + separator + 
											"Diagnoses" + separator +			
											"ThreadPoolSize";
						
			
			this.loggingData.addRow(logFileHeader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * Main running method
	 * @param xmlFilePath
	 * @param iterations
	 */
	public void run(String xmlFilePath, int iterations)
	{		
		for(int i=0; i<iterations; i++)
		{
			try{
				ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(xmlFilePath);
				ExquisiteSession sessionData = new ExquisiteSession(appXML);
				ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
				Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
				VariablesFactory varFactory = new VariablesFactory(variablesMap);
				DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
				modelLoader.loadDiagnosisModelFromXML();				
				
				IDiagnosisEngine diagnosisEngine;
				sessionData.config.searchStrategy = SearchStrategies.Default;
				System.out.println(" ----------> " + engineConfig);
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

				
				//Make a call to the diagnosis engine.
				long startTime = System.currentTimeMillis();		
				List<Diagnosis> diagnoses = diagnosisEngine.calculateDiagnoses();				
				long endTime = System.currentTimeMillis();
				long duration = endTime - startTime;
				System.out.println("Found " + diagnoses.size() + " diagnoses in " + duration + "ms");
				
				//record data for this run.
				String loggingResult = "";
				loggingResult+="N/A" + separator;
				int varCount = appXML.getInputs().size() + appXML.getInterims().size() + appXML.getOutputs().size();
				loggingResult+= varCount + separator;
				loggingResult+=sessionData.diagnosisModel.getConstraintNames().size() + separator;
				loggingResult+= diagnosisEngine.getPropagationCount() + separator;
				loggingResult+= diagnosisEngine.getCspSolvedCount() + separator;
				loggingResult+=duration + separator;
				loggingResult+=this.maxSearchDepth + separator;
				loggingResult+="N/A" + separator;
				
				for (int j = 0; j < diagnoses.size(); j++) 
				{
//					loggingResult+="(Diag # " + j + ": " + Utilities.printConstraintList(diagnoses.get(j).getElements(), sessionData.diagnosisModel) + ") ";
				}
				loggingResult += Utilities.printSortedDiagnoses(diagnoses, ' ');
				
				
				String threadPoolContent = separator + threadPoolSize;
				if (engineConfig != EngineType.ParaHSDagStandardQX){
					threadPoolContent = separator + "N/A";
				}
				loggingResult+=threadPoolContent;
				
				this.loggingData.addRow(loggingResult);
			}
			catch (DiagnosisException e)
			{
				this.loggingData.addRow("DomainException caught for this test run.");
			}
		}
		this.loggingData.writeToFile();
	}
	
	public static void runScenarios() {
		final int iterationCount = 3;
		final int searchDepth = 2;
		final String scenarioDir = "C:\\exquisite testing\\scenarios\\";
		final String logPath = "C:\\exquisite testing\\scenarios\\logs\\";
		
		Enase2013 testRunner = new Enase2013(logPath + "testRun.csv", searchDepth, 1);
		testRunner.run(scenarioDir + "test_13.xml", iterationCount);
	}
	
	/**
	 * Runs a single fault test case
	 */
	public static void runSingleFaultTests()
	{
		final int IterationCount = 1;
		final int SearchDepth = 1;
		final String SingleFaultDir = ".\\experiments\\spreadsheetsbenchmark\\singleFault\\";
		final String LogPath = ".\\logs\\spreadsheetsbenchmark\\";
		Enase2013 singleFaultTestRunner = null;
				
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault50.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_50.xml", IterationCount);
		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault30.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_30.xml", IterationCount);
		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault20.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_20.xml", IterationCount);
		
		singleFaultTestRunner = new Enase2013(LogPath + "singleFault10.csv", SearchDepth);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_10.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault09.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_09.xml", IterationCount);
//		
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault08.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_08.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault07.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_07.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault06.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_06.xml", IterationCount);
//
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault05.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_05.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault04.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_04.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault03.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_03.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault02.csv", SearchDepth);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_02.xml", IterationCount);
	}
	
	/**
	 * Runs the double fault test cases
	 */
	public static void runDoubleFaultTests()
	{
		final int IterationCount = 5;
		final int SearchDepth = 2;
		final String XmlPath = ".\\experiments\\spreadsheetsbenchmark\\doubleFault\\";
		final String LogPath = ".\\logs\\spreadsheetsbenchmark\\";
		Enase2013 doubleFaultTestRunner = null;
		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault50.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex50.xml", IterationCount);
////	
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault30.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex30.xml", IterationCount);
////		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault20.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex20.xml", IterationCount);
//		
		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault10.csv", SearchDepth);
		doubleFaultTestRunner.run(XmlPath + "ex10.xml", IterationCount);
		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault09.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex09.xml", IterationCount);
////		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault08.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex08.xml", IterationCount);
////		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault07.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex07.xml", IterationCount);
////		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault06.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex06.xml", IterationCount);
////		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault05.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex05.xml", IterationCount);
////		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault04.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex04.xml", IterationCount);
////		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault03.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex03.xml", IterationCount);
////		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault02.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex02.xml", IterationCount);
	}
	
	
	
	
	/**
	 * Runs a single fault test case
	 */
	public static void runSingleFaultParallelTests(int maxThreadSize)
	{
		final int IterationCount = 10;
		final int SearchDepth = 2;
		final String SingleFaultDir = ".\\experiments\\spreadsheetsbenchmark\\singleFault\\";
		final String LogPath = ".\\logs\\spreadsheetsbenchmark\\parallel\\" + maxThreadSize + "\\";
		
		Enase2013 singleFaultTestRunner = null;		
			
		singleFaultTestRunner = new Enase2013(LogPath + "singleFault50.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_50.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault30.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_30.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault20.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_20.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault10.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_10.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault09.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_09.xml", IterationCount);
//				
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault08.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_08.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault07.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_07.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault06.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_06.xml", IterationCount);
//
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault05.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_05.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault04.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_04.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault03.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_03.xml", IterationCount);
//		
//		singleFaultTestRunner = new Enase2013(LogPath + "singleFault02.csv", SearchDepth, maxThreadSize);
//		singleFaultTestRunner.run(SingleFaultDir + "exquisite_02.xml", IterationCount);
//		
		singleFaultTestRunner = new Enase2013(LogPath + "singleFault50.csv", SearchDepth, maxThreadSize);
		singleFaultTestRunner.run(SingleFaultDir + "exquisite_50.xml", IterationCount);
	}
	
	/**
	 * Runs the double fault test cases
	 */
	public static void runDoubleFaultParallelTests(int maxThreadSize)
	{
		final int IterationCount = 10;
		final int SearchDepth = 2;
		final String XmlPath = ".\\experiments\\spreadsheetsbenchmark\\doubleFault\\";
		final String LogPath = ".\\logs\\spreadsheetsbenchmark\\parallel\\" + maxThreadSize + "\\";
		threadPoolSize = maxThreadSize;
		
		Enase2013 doubleFaultTestRunner = null;
		
		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault50.csv", SearchDepth, maxThreadSize);
		doubleFaultTestRunner.run(XmlPath + "ex50.xml", IterationCount);
//	
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault30.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex30.xml", IterationCount);
//		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault20.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex20.xml", IterationCount);
//		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault10.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex10.xml", IterationCount);
//		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault09.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex09.xml", IterationCount);
//		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault08.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex08.xml", IterationCount);
//		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault07.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex07.xml", IterationCount);
//		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault06.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex06.xml", IterationCount);
//		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault05.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex05.xml", IterationCount);
//		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault04.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex04.xml", IterationCount);
//		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault03.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex03.xml", IterationCount);
//		
//		doubleFaultTestRunner = new Enase2013(LogPath + "doubleFault02.csv", SearchDepth);
//		doubleFaultTestRunner.run(XmlPath + "ex02.xml", IterationCount);
	}
	
	
	
	
	public static void runBigExample()
	{
		final int SearchDepth = 1;
		final String LogPath = ".\\logs\\spreadsheetsbenchmark\\singleFaultBigExample.csv";
		Enase2013 testRunner = new Enase2013(LogPath, SearchDepth);
		testRunner.runExample(1);
//		testRunner.runExample2(1);
	}
	
	public void runExample(int testIterations)
	{
		//load  correct xml so to build correct test cases.
		//final String appXMLFile = ".\\experiments\\enase-2013\\singleFault\\solvertest.xml";
		final String appXMLFile = ".\\experiments\\spreadsheetsbenchmark\\karinscorpus_VDEPPreserve_TC_no_division.xml";
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(appXMLFile);		
	
		ExquisiteSession sessionData = new ExquisiteSession(appXML);
		ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
		Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
		VariablesFactory varFactory = new VariablesFactory(variablesMap);
		DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
		modelLoader.loadDiagnosisModelFromXML();	
				
		CPSolver solver = new CPSolver();
		CPModel cpModel = makeCPModel(sessionData.diagnosisModel);
		
		
		
		solver.read(cpModel);
		solver.addGoal(new ImpactBasedBranching(solver));
		
		
		ChocoLogging.toVerbose();
		ChocoLogging.setVerbosity(Verbosity.FINEST);
		
//		try {
//			solver.propagate();
////			solver.solve();
//			
//		} catch (ContradictionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		solver.solve();
		
		ChocoLogging.flushLogs();
		solver.printRuntimeStatistics();
		
		//System.out.println(Utilities.printSolution(solver));
		boolean isFeasible = solver.isFeasible();
		System.out.println("IS FEASIBLE? " + isFeasible);
		
		System.out.println("======= solver state");
		//System.out.println(solver.pretty());
		System.out.println("======= solver state\n\n");
		//*/
		//System.out.println(solver.pretty());
		
		/*Iterator<IntDomainVar> it = solver.getIntVarIterator();
		Dictionary<String, String> calculatedFormulaValues = new Hashtable<String, String>();
		List<String> formulas = Collections.list(appXML.getFormulas().keys());
		while (it.hasNext()) {
			IntDomainVar var = it.next();
			if (formulas.contains(var.getName()))
			{				
				System.out.println("expected output for " + var.getName() + " = " + var.getVal());
				calculatedFormulaValues.put(var.getName(), String.valueOf(var.getVal()));								
			}			
		}*/		
	}	
	
	private CPModel makeCPModel(DiagnosisModel diagModel)
	{
		CPModel model = new CPModel();
		this.addVariables(model, diagModel.getVariables());
		this.addConstraints(model, diagModel.getPossiblyFaultyStatements());
		
		//value bounds!!!
		/*for(int i=0; i<diagModel.getCorrectStatements().size();i++)
		{
			Constraint c = diagModel.getCorrectStatements().get(i);
			cpmodel.addConstraint(c);
		}*/
		
		//example constraints
		Example example = diagModel.getPositiveExamples().get(0);
		for(int i=0; i< example.constraints.size();i++)
		{			
			Constraint c = example.constraints.get(i);
			model.addConstraint(c);
		}
		
		return model;
	}	
	
	private void addVariables(CPModel model, List<Variable> list)
	{
		choco.kernel.model.variables.Variable[] variablesToAdd = new IntegerVariable[list.size()];
		int index=0;
		for(choco.kernel.model.variables.Variable variable : list)
		{
			
			variablesToAdd[index] = variable;
			index++;
		}
		model.addVariables(variablesToAdd);			
	}
	
	private void addConstraints(CPModel model, List<Constraint> list)
	{
		for(int i=0; i< list.size();i++)
		{			
			Constraint c = list.get(i);
			model.addConstraint(c);
		}
	}
	
	public void runExample2(int testIterations)
	{
		//load  correct xml so to build correct test cases.
		//final String appXMLFile = ".\\experiments\\enase-2013\\singleFault\\solvertest.xml";
		final String appXMLFile = ".\\experiments\\enase-2013\\karinscorpus_VDEPPreserve_TC_small.xml";		
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(appXMLFile);
		
		//generate test cases
		PositiveTestCaseGenerator posGen = new PositiveTestCaseGenerator(appXML, new Random());
			
		final int TestCaseCount = 1;
		Dictionary<String, TestCase> testCases = posGen.generateTestCases(TestCaseCount, false);
		Enumeration<String> keys = testCases.keys();
		
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			TestCase testCase = testCases.get(key);
			System.out.println(testCase.toString());
		}
		
		/*
		//Add generated test cases to model.
		appXML.setTestCases(testCases);
		
		//add mutation and mark as faulty...		
		appXML.getFormulas().remove("WS_1_K28");
		appXML.getFormulas().put("WS_1_K28", "SUM(K5:K27)");//should be K4:K27
		
		//transform the model into a CSP model...
		VariablesFactory varFactory = new VariablesFactory(new Hashtable<String, IntegerExpressionVariable>());
		Example globalNegativeExample = new Example(true);
		Example globalPositiveExample = new Example();
		ConstraintsFactory conFactory = new ConstraintsFactory(globalNegativeExample, globalPositiveExample);
		DiagnosisModel model = new DiagnosisModel();
		DiagnosisModelLoader loader = new DiagnosisModelLoader(model, appXML, varFactory, conFactory);
		model = loader.startLoad();
		
		IDiagnosisEngine diagnosisEngine = EngineFactory.makeDAGEngineStandardQx();
		for (int i = 0; i < testIterations; i++) {		
			diagnosisEngine.resetEngine();
			final int MaxSearchDepth = -1;
			diagnosisEngine.setMaxSearchDepth(MaxSearchDepth);
			diagnosisEngine.setModel(model);		
			
			//Make a call to the diagnosis engine.
			//start time
			try{
				long startTime = System.currentTimeMillis();
				System.out.println("startTime: " + startTime);
				List<Diagnosis> diagnoses = diagnosisEngine.calculateDiagnoses();
				long endTime = System.currentTimeMillis();
				System.out.println("endTime: " + endTime);
				long duration = endTime - startTime;
				
				//record data for this run.
				String delimiter = Culture.getCurrentCulture().CSV_DELIMITER();
				String loggingResult = "";
				loggingResult+="N/A" + delimiter;
				int varCount = appXML.getInputs().size() + appXML.getInterims().size() + appXML.getOutputs().size();
				loggingResult+= varCount + delimiter;
				loggingResult+=model.getConstraintNames().size() + delimiter;
				loggingResult+= diagnosisEngine.getPropagationCount() + delimiter;
				loggingResult+= diagnosisEngine.getCspSolvedCount() + delimiter;
				loggingResult+=duration + delimiter;
				loggingResult+=MaxSearchDepth + delimiter;
				loggingResult+="N/A" + delimiter;
				
				for (int j = 0; j < diagnoses.size(); j++) 
				{
					loggingResult+="(Diag # " + j + ": " + Utilities.printConstraintList(diagnoses.get(j).getElements(), model) + ") ";
				}
				this.loggingData.addRow(loggingResult);
				
			}	catch (DomainSizeException e){
				this.loggingData.addRow("DomainSizeException caught at test iteration #" +i + ".");
			}
			this.loggingData.writeToFile();
		}*/
	}
	
	public static void runSmallCorpusExample()
	{
		final int IterationCount = 1;
		int searchDepth = 1;
		//final String XmlPath = "C:\\Users\\David\\workspace\\exquisite-service\\experiments\\enase-2013\\";
		//final String LogPath = "C:\\Users\\David\\workspace\\exquisite-service\\logs\\enase-2013\\";
		final String XmlPath = ".\\experiments\\enase-2013\\";
		final String LogPath = ".\\logs\\enase-2013\\";
				
//		Enase2013 salesForecast = new Enase2013(LogPath + "karinscorpus_salesforecast.csv", searchDepth);
//		salesForecast.run(XmlPath + "karinscorpus_salesforecast.xml", IterationCount);
//		salesForecast = null;
		
//		Enase2013 salesForecast1FaultOutput = new Enase2013(LogPath + "karinscorpus_salesforecast_1fault_output.csv", searchDepth);
//		salesForecast1FaultOutput.run(XmlPath + "karinscorpus_salesforecast_1fault_output.xml", IterationCount);
//		salesForecast1FaultOutput = null;
		
//		searchDepth = 2;
//		Enase2013 salesForecast2Faults = new Enase2013(LogPath + "karinscorpus_salesforecast_2faults.csv", searchDepth);
//		salesForecast2Faults.run(XmlPath + "karinscorpus_salesforecast_2faults.xml", IterationCount);
//		salesForecast2Faults = null;
		
//		Enase2013 salesForecast1FaultOutput = new Enase2013(LogPath + "salesforecast_TC_IBB_fullinputs.csv", searchDepth);
//		salesForecast1FaultOutput.run(XmlPath + "salesforecast_TC_IBB_fullinputs.xml", IterationCount);
//		salesForecast1FaultOutput = null;
		
		Enase2013 salesForecast1FaultOutput = new Enase2013(LogPath + "salesforecast_TC_IBB", searchDepth);
		salesForecast1FaultOutput.run(XmlPath + "salesforecast_TC_IBB.xml", IterationCount);
		salesForecast1FaultOutput = null;
		
//		Enase2013 salesForecast1FaultOutput = new Enase2013(LogPath + "salesforecast_TC_IBB_Small.csv", searchDepth);
//		salesForecast1FaultOutput.run(XmlPath + "salesforecast_TC_IBB_Small.xml", IterationCount);
//		salesForecast1FaultOutput = null;
		
//		Enase2013 salesForecast1FaultOutput = new Enase2013(LogPath + "salesforecast_TC_IBB_Small_Full.csv", searchDepth);
//		salesForecast1FaultOutput.run(XmlPath + "salesforecast_TC_IBB_Small_Full.xml", IterationCount);
//		salesForecast1FaultOutput = null;
		
//		Enase2013 salesForecast1FaultOutput = new Enase2013(LogPath + "salesforecast_TC_IBB_Smaller.csv", searchDepth);
//		salesForecast1FaultOutput.run(XmlPath + "salesforecast_TC_IBB_Smaller.xml", IterationCount);
//		salesForecast1FaultOutput = null;

	}
}
