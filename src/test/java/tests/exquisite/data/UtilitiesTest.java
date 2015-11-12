package tests.exquisite.data;

import org.exquisite.data.ExampleTestData;
import org.exquisite.tools.Utilities;
import org.w3c.dom.Element;

public class UtilitiesTest 
{
	static final String EXAMPLE_DATA_FILE_PATH = "data/exampleData.xml";
	
	/**
	 * Main entry point into program - starts the test runner.
	 */
	public static void main(String[] args) 
	{
		new UtilitiesTest().run();
	}
	
	/**
	 * The test runner...
	 */
	public void run()
	{
		System.out.println("Running Utilities tests...");
		runXMLReaderTest();
		System.out.println("tests complete.");
	}
	
	/**
	 * Tests org.exquisite.data.Utilities.readXML()
	 */
	private void runXMLReaderTest()
	{
		boolean testPassed = false;
		String message = "runXMLReaderTest has failed.";
		Element root = Utilities.readXML(ExampleTestData.MAPPING_TEST_XML);		
		String rootName = root.getNodeName();		
		testPassed = rootName == "ExquisiteAppXML";		
				
		if (testPassed)
		{
			message = "runXMLReaderTest has passed.";
		}
		printResult(testPassed, message);
	}
	
	/**
	 * Just prints a message to the screen. Used by the test results. 
	 * If parameter hasTestPassed = true then message is printed as normal,
	 * if hasTestPassed = false then the message is printed to screen in red.
	 * @param hasTestPassed
	 * @param message
	 */
	private void printResult(boolean hasTestPassed, String message)
	{
		if (hasTestPassed){
			System.out.println(message);
		}
		else
		{
			System.err.println(message);
		}
	}
}
