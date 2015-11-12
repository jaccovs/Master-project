package evaluations.old;

import java.util.Random;

import org.exquisite.datamodel.ExquisiteEnums.EngineType;

import evaluations.scalability.Scalability;

public class TestRunner {
	
	public static Random random = new Random(22);
	
	/**
	 * Run the tests...
	 * @param args
	 */
	public static void main(String[]args)
	{
		TestRunner testRunner = new TestRunner();
		
		//run tests with standard quickxplain implementation...
		testRunner.runJCKBSESingleFault(EngineType.HSDagStandardQX, "JCKBSE_singleFault.csv");
		//testRunner.runJCKBSETwoFaults(EngineFactory.makeDAGEngineStandardQx(), "JCKBSE_twoFaults.csv");
		//final int MaxSearchDepth = 1;
		//testRunner.runJCKBSESingleFaultFixedSearchDepth(MaxSearchDepth, EngineFactory.makeDAGEngineStandardQx(), "JCKBSE_1Fault1SearchDepth.csv");	
		
		//run tests with extended quickxplain implementation...
		//testRunner.runJCKBSESingleFault(EngineFactory.makeDAGEngineExtendedQx(), "JCKBSE_singleFault_exQX.csv");
		//testRunner.runJCKBSETwoFaults(EngineFactory.makeDAGEngineExtendedQx(), "JCKBSE_twoFaults_exQX.csv");		
	}
	
	/*
	 * Run scalability test as described in JCKBSE paper
	 * Change the values of the constants below to alter test size, duration and model complexity.
	 */
	public void runJCKBSESingleFault(EngineType engineConfig, String logfileName)
	{
		final int MinInputRowCount = 2;				//last number of input rows to test.
		final int MaxInputRowCount = 2;				//initial start number of input rows to test
		final int MutationCount = 1; 				//how many faults to add.
		final int TestCaseCount = 1; 				//how many test cases to generate.
		final int IterationCount = 1; 				//number of times to repeat the test for a given number of input rows.
		final int MaxSearchDepth = 1;				//How deep to expand the DAG with -1 being unlimited.
		//final String LogfileName = "JCKBSE_singleFault.csv"; 	//name of log file to use - located in logs folder at project root.
		
		//The class the implements the evaluation test.
		Scalability scalability = new Scalability(logfileName, MaxSearchDepth, engineConfig, random); 
		scalability.run(MinInputRowCount, MaxInputRowCount, MutationCount, TestCaseCount, IterationCount);
		scalability.onRunComplete();
	}
	
	/*
	 * Run scalability test as described in JCKBSE paper but with fixed search depth of 1.
	 * Change the values of the constants below to alter test size, duration and model complexity.
	 */
	public void runJCKBSESingleFaultFixedSearchDepth(int maxSearchDepth, EngineType engineConfig, String logfileName)
	{
		final int MinInputRowCount = 2;				//last number of input rows to test.
		final int MaxInputRowCount = 2;				//initial start number of input rows to test
		final int MutationCount = 1; 				//how many faults to add.
		final int TestCaseCount = 1; 				//how many test cases to generate.
		final int IterationCount = 1; 				//number of times to repeat the test for a given number of input rows.
		//final String LogfileName = "JCKBSE_1Fault1SearchDepth.csv"; 	//name of log file to use - located in logs folder at project root.
		
		//The class the implements the evaluation test.
		Scalability scalability = new Scalability(logfileName, maxSearchDepth, engineConfig, random); 
		scalability.run(MinInputRowCount, MaxInputRowCount, MutationCount, TestCaseCount, IterationCount);
		scalability.onRunComplete();
	}
	
	/*
	 * Run scalability test as described in JCKBSE paper with TWO faults in spreadsheet.
	 * Change the values of the constants below to alter test size, duration and model complexity.
	 */
	public void runJCKBSETwoFaults(EngineType engineConfig, String logfileName)
	{
		final int MinInputRowCount = 2;				//last number of input rows to test.
		final int MaxInputRowCount = 8;				//initial start number of input rows to test
		final int MutationCount = 2; 				//how many faults to add.
		final int TestCaseCount = 1; 				//how many test cases to generate.
		final int IterationCount = 25; 				//number of times to repeat the test for a given number of input rows.
		final int MaxSearchDepth = -1;				//How deep to expand the DAG with -1 being unlimited.
		//final String LogfileName = "JCKBSE_twoFaults.csv"; 	//name of log file to use - located in logs folder at project root.
		
		//The class the implements the evaluation test.
		Scalability scalability = new Scalability(logfileName, MaxSearchDepth, engineConfig, random); 
		scalability.run(MinInputRowCount, MaxInputRowCount, MutationCount, TestCaseCount, IterationCount);
		scalability.onRunComplete();
	}
}
