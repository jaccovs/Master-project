package evaluations.scalability;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import evaluations.models.JCKBSEModel;
import evaluations.models.LoggingData;
import evaluations.mutation.Mutation;
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
import org.exquisite.i8n.Culture;
import org.exquisite.i8n.CultureInfo;
import org.exquisite.logging.ExquisiteLogger;
import org.exquisite.tools.Utilities;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static org.exquisite.core.measurements.MeasurementManager.COUNTER_CSP_SOLUTIONS;
import static org.exquisite.core.measurements.MeasurementManager.COUNTER_PROPAGATION;
import static org.exquisite.core.measurements.MeasurementManager.getCounter;

/**
 * Where the h/f*** are the comments, David?
 * @author dietmar
 *
 */
public class Scalability {
	
	/**
	 * Which type/configuration of diagnosis engine to use for test run.
	 */
	//IDiagnosisEngine diagnosisEngine;
	EngineType engineConfig;
	/**
	 * Name of the logfile where test data will be written to.
	 */
	private String logfileName;
	
	/**
	 * Enables/disables caching of the test cases. 
	 */
	//private final boolean CacheTestCases = true;
	/**
	 * Simple util class  hold data from test runs and writing data to log file.
	 */
	private LoggingData loggingData;
	
	/**
	 * Caches the number of input rows to used for the last test run. Can be used
	 * to compare with next test run - if no change then then just reuse the test cases.
	 */
	//private int inputRowCountCache;
	//private Dictionary<String, TestCase> positiveTestCaseCache;
	/**
	 * random generator to use.
	 */
	@SuppressWarnings("unused")
	private Random random;
	private int maxSearchDepth = -1;
	
	private String separator = ";";
	
	private CultureInfo currentCulture;
	/**
	 * Takes the name of the logfile as an input parameter.
	 * @param logfileName
	 */
	public Scalability(String logfileName, int maxSearchDepth, EngineType engineConfig, Random random) {
		this.currentCulture = Culture.getCurrentCulture();
		System.out.println("Culture: " + currentCulture);
		this.separator = currentCulture.CSV_DELIMITER();
		this.random = random;
		this.engineConfig = engineConfig;

		try {
			this.logfileName = logfileName;
			this.maxSearchDepth = maxSearchDepth;
			final boolean AppendContent = false;
			Logger loggerInstance;
			loggerInstance = ExquisiteLogger.setup(this.logfileName, AppendContent);
			this.loggingData = new LoggingData(loggerInstance);
			final String LogfileHeader = "#Prods" + separator +
					"#Vars" + separator +
					"#Constraints" + separator +
					"#CSP props." + separator +
					"#CSP solved" + separator +
					"Diag. time (ms)" + separator +
					"Max Search Depth" + separator +
					"Mutations" + separator +
					"Diagnoses";
			this.loggingData.addRow(LogfileHeader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Example use of Scalability class.
	 * @param args
	 */
	public static void main(String[] args) {
		Culture.setCulture(Locale.GERMAN);
		final int MaxInputRowCount = 8;
		final int MinInputRowCount = 8;
		final int MutationCount = 3;
		final int TestCaseCount = 10;
		final int IterationCount = 10;
		final String LogfileName = "ScalabilityMain.csv";
		Scalability scalability = new Scalability(LogfileName, -1, EngineType.HSDagStandardQX, new Random(22));
//		scalability.run(MinInputRowCount, MaxInputRowCount, MutationCount, TestCaseCount, IterationCount);
		scalability.run(MinInputRowCount, MutationCount, TestCaseCount);
		scalability.onRunComplete();
	}
	
	/**
	 * Runs a set of test iterations. For each iteration the number of input rows is decremented by one. Until
	 * specified minimum number of inputs is reached.
	 * 
	 * @param minInputCount - minimum number of input rows to test.
	 * @param maxInputCount - maximum (starting) number of input rows to test.
	 * @param mutationCount - number of mutations to make in the model.
	 * @param testCaseCount - number of test cases to use.
	 * @param iterationCount - number of iterations (calls to diagnosis engine) to make for a given model.
	 */
	public void run(int minInputCount, int maxInputCount, int mutationCount, int testCaseCount, int iterationCount)
	{
		int currentInputCount = maxInputCount;
		
		while(currentInputCount >= minInputCount)
		{
			run(currentInputCount, mutationCount, testCaseCount, iterationCount);
			currentInputCount--;
		}
	}
	
	/**
	 * Runs the test for a given number of iterations.
	 * 
	 * @param inputRowCount - number of rows to include in the model.
	 * @param mutationCount - number of faults to introduce to the model.
	 * @param testCaseCount - number of test cases to generate.
	 * @param iterationCount - number of times to repeat the procedure - e.g. for calculating average elapsed times etc...
	 */
	public void run(int inputRowCount, int mutationCount, int testCaseCount, int iterationCount)
	{				
		for(int j=0; j<iterationCount; j++)
		{
			run(inputRowCount, mutationCount, testCaseCount);
		}		
	}	
	
	/**
	 * Builds model with specified number of input rows, generates specified number of test cases,
	 * then mutates the model and begins running through the test cases, before making a call to the
	 * diagnosis engine.
	 * 
	 * @param inputRows
	 * @param mutationCount
	 * @param testCaseCount
	 */
	public void run(int inputRows, int mutationCount, int testCaseCount)
	{		
		try{
			final int MaxInputDomain = 10;   
			final int MinInputDomain = 1;
			
			//Generate a model with no faults in it.
			JCKBSEModel kb = new JCKBSEModel();
			ExquisiteAppXML appXML = kb.defineModel(inputRows, MinInputDomain, MaxInputDomain);
			
			//Generate some positive test cases.
//			PostitiveTestCaseGenerator positiveTestCaseGenerator = new PostitiveTestCaseGenerator(appXML, random);
//			positiveTestCaseCache = positiveTestCaseGenerator.generateTestCases(testCaseCount);
			
			//Introduce a mutation to the model
			ArrayList<String> supportedArithmeticOperators = new ArrayList<String>();
			supportedArithmeticOperators.add(currentCulture.MULTIPLY()); 
			supportedArithmeticOperators.add(currentCulture.PLUS());
			supportedArithmeticOperators.add(currentCulture.MINUS());			
			Mutation mutator = new Mutation(appXML);
			final int numOperatorsToChange = 1;
			mutator.mutate(mutationCount, numOperatorsToChange, supportedArithmeticOperators);
					
			//transform the model into a CSP model...			
			ExcelExquisiteSession sessionData = new ExcelExquisiteSession(appXML);
			ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
			Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
			VariablesFactory varFactory = new VariablesFactory(variablesMap);
			DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
			modelLoader.loadDiagnosisModelFromXML();

			IDiagnosisEngine<Constraint> diagnosisEngine;
			diagnosisEngine = EngineFactory.makeEngine(this.engineConfig, sessionData, 1);
//			diagnosisEngine.setMaxSearchDepth(this.maxSearchDepth);
									
			//Make a call to the diagnosis engine.
			//start time
			long startTime = System.currentTimeMillis();
			System.out.println("startTime: " + startTime);
			List<Diagnosis<Constraint>> diagnoses = diagnosisEngine.calculateDiagnoses();
			System.out.println("diagnoses: " + diagnoses.size());
			long endTime = System.currentTimeMillis();
			System.out.println("endTime: " + endTime);
			long duration = endTime - startTime;
			
			//record data for this run.
			String loggingResult = "";
			loggingResult+=inputRows + separator;
			loggingResult+=kb.getInputVarCount() + separator;
			loggingResult+=sessionData.getDiagnosisModel().getConstraintNames().size() + separator;
			loggingResult += getCounter(COUNTER_PROPAGATION).value();
			loggingResult += getCounter(COUNTER_CSP_SOLUTIONS).value() + separator;
			loggingResult+=duration + separator;
			loggingResult+=this.maxSearchDepth + separator;
			loggingResult+=mutator.getMutatationDescriptions() + separator;
			
			for (int i = 0; i < diagnoses.size(); i++) 
			{
				loggingResult+="(Diag # " + i + ": " + Utilities.printConstraintList(diagnoses.get(i).getElements(), sessionData.getDiagnosisModel()) + ") ";
			}		
			
			this.loggingData.addRow(loggingResult);
		} catch (DiagnosisException e)	{
			this.loggingData.addRow("DiagnosisException caught for this test run.");
		}
	}
	
	/**
	 * Tasks to do after the test run has been completed. Such as writing test data to a log file etc.
	 */
	public void onRunComplete()
	{
		this.loggingData.writeToFile();
		System.out.println("Run Complete, write results to " + this.logfileName);
	}
	
	
}


