package evaluations.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.TestCase;
import org.exquisite.tools.FileUtilities;

public class SpreadsheetMBDTools {
	
	
	public static void main(String[]args)
	{
		String inputRoot = ".\\experiments\\enase-2013\\doubleFault\\";
		String outputPath = inputRoot + "testCaseCSVs\\";
		
		List<String> fileNames = new ArrayList<String>();
		fileNames.add("ex02");
		fileNames.add("ex03");
		fileNames.add("ex04");
		fileNames.add("ex05");
		fileNames.add("ex06");
		fileNames.add("ex07");
		fileNames.add("ex08");
		fileNames.add("ex09");
		fileNames.add("ex10");
		
		for(String fileName : fileNames)
		{
			ExportTestCasesToCSV(inputRoot + fileName + ".xml", outputPath + fileName + "PosEx.txt");
		}
		
	}
	
	
	/**
	 * A class containing utilities for porting exquisite data (test cases etc.) to 
	 * a format compatible with the Engler implimentation.
	 */	
	public static void ExportTestCasesToCSV(String xmlFilePath, String outputCSVPath)
	{
		//parse xml to appXML object
		//iterate through each test case and write to target csv file.
		
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(xmlFilePath);
		Enumeration<String> keys = appXML.getTestCases().keys();
		while(keys.hasMoreElements())
		{
			String key = keys.nextElement();
			TestCase testCase = appXML.getTestCases().get(key);
			String csvLine = testCase.toCSV(appXML);
			try {
				FileUtilities.writeToFile(csvLine, outputCSVPath, false);
			} catch (IOException e) {
				System.out.println("Unable to write test case " + testCase.getID() + " to file.");
				e.printStackTrace();
			}
		}
	}

}
