package tests.dj.othertests;

import java.io.File;

/**
 * Creates a mutated spreadsheet and some test cases with expected values
 * from a given XML file
 * 
 * @author dietmar
 *
 */
public class ScenarioGenerator {

	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		// Call the worker
		System.out.println("Starting generation of test");
		ScenarioGenerator main = new ScenarioGenerator();
		
		String appXMLFile = "experiments/apping-2013/karinscorpus_VDEPPreserve_TC_very_small.xml";
		
		try {
			main.createPositiveTestCase(appXMLFile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("XML Test generator finished");
	}

	/**
	 * Generates a mutation and a test case
	 * @param xmlfile
	 */
	private void createPositiveTestCase(String xmlfile) throws Exception {
		File f = new File(xmlfile);
		
	}

}
