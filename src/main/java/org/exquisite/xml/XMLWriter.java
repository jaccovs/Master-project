package org.exquisite.xml;

import java.io.ByteArrayOutputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteUserSettings;
import org.exquisite.datamodel.ExquisiteValueBound;
import org.exquisite.datamodel.TestCase;
import org.exquisite.tools.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import evaluations.models.JCKBSEModel;

/**
 * Class for saving ExquisiteAppXML structures to actual XML.
 * @author Thomas
 *
 */
public class XMLWriter {
	
	/**
	 * Converts given ExquisiteAppXML to an XML string.
	 * @param exquisiteAppXML
	 * @return
	 */
	public String writeXML(ExquisiteAppXML exquisiteAppXML) {
		try	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		
		Element root = doc.createElement("ExquisiteAppXML");
		doc.appendChild(root);
		
		appendTextNode(doc, root, "PathOriginal", exquisiteAppXML.getPathOriginal());
		appendTextNode(doc, root, "PathCopy", exquisiteAppXML.getPathCopy());
		appendTextNode(doc, root, "Flag", exquisiteAppXML.getFlag().toString());
		
		appendValueBound(doc, root, "DefaultValueBound", exquisiteAppXML.getDefaultValueBound());
		appendValueBounds(doc, root, "ValueBounds", exquisiteAppXML.getValueBounds());
		
		appendStringDictionary(doc, root, "FaultyValues", exquisiteAppXML.getFaultyValues());
		appendStringDictionary(doc, root, "Assertions", exquisiteAppXML.getAssertions());
		appendStringDictionary(doc, root, "Types", exquisiteAppXML.getTypes());
		appendStringDictionary(doc, root, "CorrectFormulas", exquisiteAppXML.getCorrectFormulas());
		
		appendStringList(doc, root, "Inputs", exquisiteAppXML.getInputs());
		appendStringList(doc, root, "Outputs", exquisiteAppXML.getOutputs());
		appendStringList(doc, root, "Interims", exquisiteAppXML.getInterims());
		
		appendFormulas(doc, root, "Formulas", exquisiteAppXML.getFormulas());
		
		appendUserSettings(doc, root, "UserSettings", exquisiteAppXML.getUserSettings());
		
		appendCellsInRange(doc, root, "CellsInRange", exquisiteAppXML.getCellsInRange());
		
		appendTestCases(doc, root, "TestCases", exquisiteAppXML.getTestCases());
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);		
		return out.toString("utf-8");
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private void appendTextNode(Document doc, Element parent, String name, String value) {
		Element element = doc.createElement(name);
		element.setTextContent(value);
		parent.appendChild(element);
	}
	
	private void appendValueBound(Document doc, Element parent, String name,
			ExquisiteValueBound valueBound) {
		Element element = doc.createElement(name);
		
		Element lower = doc.createElement("Lower");
		lower.setTextContent(Double.toString(valueBound.getLower()));
		element.appendChild(lower);
		Element upper = doc.createElement("Upper");
		upper.setTextContent(Double.toString(valueBound.getUpper()));
		element.appendChild(upper);
		Element step = doc.createElement("Step");
		step.setTextContent(Double.toString(valueBound.getStep()));
		element.appendChild(step);
		
		parent.appendChild(element);
	}
	
	private void appendValueBounds(Document doc, Element parent, String name,
			Dictionary<String, ExquisiteValueBound> valueBounds) {
		Element element = doc.createElement(name);
		
		for (Enumeration<String> keys = valueBounds.keys(); keys.hasMoreElements();)
		{
			String key = keys.nextElement();
			ExquisiteValueBound valueBound = valueBounds.get(key);
			
			Element dictElement = doc.createElement("KeyValue");
			
			appendTextNode(doc, dictElement, "Key", key);
			appendValueBound(doc, dictElement, "Value", valueBound);
			
			element.appendChild(dictElement);
		}
		
		parent.appendChild(element);
	}
	
	private void appendStringDictionary(Document doc, Element parent, String name,
			Dictionary<String, String> dictionary) {
		Element element = doc.createElement(name);
		
		for (Enumeration<String> keys = dictionary.keys(); keys.hasMoreElements();)
		{
			String key = keys.nextElement();
			String value = dictionary.get(key);
			
			Element dictElement = doc.createElement("KeyValue");
			
			appendTextNode(doc, dictElement, "Key", key);
			appendTextNode(doc, dictElement, "Value", value);
			
			element.appendChild(dictElement);
		}
		
		parent.appendChild(element);
	}
	
	private void appendFormulas(Document doc, Element parent, String name,
			Dictionary<String, String> formulas) {
		Element element = doc.createElement(name);
		
		for (Enumeration<String> keys = formulas.keys(); keys.hasMoreElements();)
		{
			String key = keys.nextElement();
			String value = formulas.get(key);
			
			Element dictElement = doc.createElement("KeyValue");
			
			appendTextNode(doc, dictElement, "Key", key);
			appendTextNode(doc, dictElement, "Value", Utilities.htmlspecialchars_encode_ENT_NOQUOTES(value));
			
			element.appendChild(dictElement);
		}
		
		parent.appendChild(element);
	}
	
	private void appendStringList(Document doc, Element parent, String name,
			List<String> list) {
		Element element = doc.createElement(name);
		
		for (Iterator<String> it = list.iterator(); it.hasNext();)
		{
			String value = it.next();
			
			appendTextNode(doc, element, "Value", value);
		}
		
		parent.appendChild(element);
	}
	
	private void appendUserSettings(Document doc, Element parent, String name,
			ExquisiteUserSettings userSettings) {
		Element element = doc.createElement(name);
		
		appendTextNode(doc, element, "LocaleFlag", userSettings.getLocaleFlag().toString());
		appendTextNode(doc, element, "MaxDiagnoses", Integer.toString(userSettings.getMaxDiagnoses()));
		appendTextNode(doc, element, "SearchDepth", Integer.toString(userSettings.getSearchDepth()));
		appendTextNode(doc, element, "ShowServerDebugMessages", Boolean.toString(userSettings.getShowServerDebugMessages()));
		
		parent.appendChild(element);
	}
	
	private void appendCellsInRange(Document doc, Element parent, String name,
			Dictionary<String, List<String>> cells) {
		Element element = doc.createElement(name);
		
		for (Enumeration<String> keys = cells.keys(); keys.hasMoreElements();)
		{
			String key = keys.nextElement();
			List<String> value = cells.get(key);
			
			Element dictElement = doc.createElement("KeyValues");
			
			appendTextNode(doc, dictElement, "Key", key);
			appendStringList(doc, dictElement, "Values", value);
			
			element.appendChild(dictElement);
		}
		
		parent.appendChild(element);
	}
	
	private void appendTestCases(Document doc, Element parent, String name, Dictionary<String, TestCase> testCases) {
		Element element = doc.createElement(name);
		
		for (Enumeration<String> keys = testCases.keys(); keys.hasMoreElements();)
		{
			String key = keys.nextElement();
			TestCase testCase = testCases.get(key);
			
			Element dictElement = doc.createElement("KeyValue");
			
			appendTextNode(doc, dictElement, "Key", key);
			appendTestCase(doc, dictElement, "Value", testCase);
			
			element.appendChild(dictElement);
		}
		
		parent.appendChild(element);
	}
	
	private void appendTestCase(Document doc, Element parent, String name, TestCase testCase) {
		Element element = doc.createElement(name);
		
		appendStringDictionary(doc, element, "d4p1:Assertions", testCase.getAssertions());
		appendTextNode(doc, element, "d4p1:CaseID", testCase.getCaseID());
		appendCellsInRange(doc, element, "d4p1:CellsInRange", testCase.getCellsInRange());
		appendStringDictionary(doc, element, "d4p1:CorrectValues", testCase.getCorrectValues());
		appendTextNode(doc, element, "d4p1:Description", testCase.getDescription());
		appendStringDictionary(doc, element, "d4p1:FaultyValues", testCase.getFaultyValues());
		appendTextNode(doc, element, "d4p1:Flag", testCase.getFlag().toString());
		appendTextNode(doc, element, "d4p1:ID", testCase.getID());
		appendStringDictionary(doc, element, "d4p1:Types", testCase.getTypes());
		appendValueBounds(doc, element, "d4p1:ValueBounds", testCase.getValueBounds());
		appendStringDictionary(doc, element, "d4p1:Values", testCase.getValues());
		
		parent.appendChild(element);
	}

	/*
	TODO commented out this method. This is an experimentation method that must not be here!

	public static void main(String[] args) {
		JCKBSEModel model = new JCKBSEModel();
		ExquisiteAppXML appXML = model.defineModel(10, 0, 20);
		appXML.setPathOriginal("text.xml");
		appXML.setPathCopy("text_copy.xml");
		XMLWriter writer = new XMLWriter();
		String xml = writer.writeXML(appXML);
		System.out.println(xml);


		XMLParser parser = new XMLParser(xml);
		parser.parse();
		ExquisiteAppXML parsedApp = parser.getExquisiteAppXML();
		System.out.println(parsedApp.getPathOriginal());
	}
	*/

}
