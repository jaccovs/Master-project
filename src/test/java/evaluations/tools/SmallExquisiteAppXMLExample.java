package evaluations.tools;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteEnums.ExquisiteTestcaseFlag;
import org.exquisite.datamodel.ExquisiteValueBound;
import org.exquisite.datamodel.TestCase;

public class SmallExquisiteAppXMLExample 
{
	public static ExquisiteAppXML getExample(){
		ExquisiteAppXML exquisiteAppXML = new ExquisiteAppXML();
		
		ArrayList<String> inputList = new ArrayList<String>();
		inputList.add("WS_1_A1");
		inputList.add("WS_1_A2");
		exquisiteAppXML.setInputs(inputList);
		
		ArrayList<String> interimList = new ArrayList<String>();
		interimList.add("WS_1_B1");
		interimList.add("WS_1_B2");
		exquisiteAppXML.setInterims(interimList);
		
		ArrayList<String> outputList = new ArrayList<String>();
		outputList.add("WS_1_C1");
		exquisiteAppXML.setOutputs(outputList);
		
		exquisiteAppXML.setDefaultValueBound(new ExquisiteValueBound(0, 100, 0.1));
		
		Dictionary<String, ExquisiteValueBound> globalValueBounds = new Hashtable<String, ExquisiteValueBound>();
		globalValueBounds.put("WS_1_A1", new ExquisiteValueBound(0, 10, 0.1));
		globalValueBounds.put("WS_1_A2", new ExquisiteValueBound(0, 10, 0.1));
		globalValueBounds.put("WS_1_C1", new ExquisiteValueBound(0, 200, 0.1));
		
		exquisiteAppXML.setValueBounds(globalValueBounds);
		
		Hashtable<String, String> formulae = new Hashtable<String, String>();
		formulae.put("WS_1_B2", "A2*3");
		formulae.put("WS_1_B1", "A1*2");
		formulae.put("WS_1_C1", "B1+B2");		
		exquisiteAppXML.setFormulas(formulae);
		
		TestCase testcase1 = new TestCase();
		testcase1.setFlag(ExquisiteTestcaseFlag.Normal);
		
		Hashtable<String, String> testValues = new Hashtable<String, String>();
		testValues.put("WS_1_A1", "2");
		testValues.put("WS_1_A2", "2");
		testcase1.setValues(testValues);
		
		Hashtable<String, String> testFaultyValues = new Hashtable<String, String>();
		testFaultyValues.put("WS_1_C1", "24");
		testcase1.setFaultyValues(testFaultyValues);				
		
		Hashtable<String, TestCase> testCases = new Hashtable<String, TestCase>();
		testCases.put("testcase1", testcase1);			
		
		exquisiteAppXML.setTestCases(testCases);
		
		return exquisiteAppXML;		
	}
}
