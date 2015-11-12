/**
 * 
 */
package org.exquisite.datamodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.exquisite.datamodel.ExquisiteEnums.ExquisiteFlag;
import org.exquisite.xml.XMLParser;

/**
 * @author Arash
 * 
 */
public class ExquisiteAppXML {
	private Dictionary<String, String> Formulas;
	private Dictionary<String, String> FormulasR1C1;
	private String PathOriginal;
	private String PathCopy;
	private ExquisiteFlag Flag;
	private ExquisiteValueBound DefaultValueBound;
	private Dictionary<String, TestCase> TestCases;
	private Dictionary<String, Fragment> Fragments;
	private List<String> Inputs;
	private List<String> Outputs;
	private List<String> Interims;
	private Dictionary<String, ExquisiteValueBound> ValueBounds;
	private Dictionary<String, List<String>> CellsInRange;
	private Dictionary<String, String> FaultyValues;
	private Dictionary<String, String> Assertions;
	private Dictionary<String, String> Types;
	private Dictionary<String, String> CorrectFormulas;
	private ExquisiteUserSettings UserSettings;
	
	public ExquisiteAppXML() {
		Formulas = new Hashtable<String, String>();
		FormulasR1C1 = new Hashtable<String, String>();
		PathOriginal = "";
		PathCopy = "";
		Flag = ExquisiteFlag.Original;
		DefaultValueBound = new ExquisiteValueBound();
		TestCases = new Hashtable<String, TestCase>();
		Fragments = new Hashtable<String, Fragment>();
		Inputs = new ArrayList<String>();
		Outputs = new ArrayList<String>();
		Interims = new ArrayList<String>();
		ValueBounds = new Hashtable<String, ExquisiteValueBound>();
		CellsInRange = new Hashtable<String, List<String>>();
		FaultyValues = new Hashtable<String, String>();
		Assertions = new Hashtable<String, String>();
		Types = new Hashtable<String, String>();
		CorrectFormulas = new Hashtable<String, String>();
		UserSettings = new ExquisiteUserSettings();
	}

	/**
	 * @param formulas
	 * @param pathOriginal
	 * @param pathCopy
	 * @param flag
	 * @param defaultValueBound
	 * @param testCases
	 * @param inputs
	 * @param outputs
	 * @param interims
	 * @param valueBounds
	 * @param cellsInRange
	 * @param faultyValues
	 * @param assertions
	 * @param types
	 * @param correctFormulas
	 * @param userSettings
	 */
	public ExquisiteAppXML(Dictionary<String, String> formulas,
			Dictionary<String, String> formulasR1C1,
			String pathOriginal,
			String pathCopy,
			ExquisiteFlag flag,
			ExquisiteValueBound defaultValueBound,
			Dictionary<String, TestCase> testCases,
			Dictionary<String, Fragment> fragments,
			List<String> inputs,
			List<String> outputs,
			List<String> interims,
			Dictionary<String, ExquisiteValueBound> valueBounds,
			Dictionary<String, List<String>> cellsInRange,
			Dictionary<String, String> faultyValues,
			Dictionary<String, String> assertions,
			Dictionary<String, String> types,
			Dictionary<String, String> correctFormulas,
			ExquisiteUserSettings userSettings) {
		super();
		Formulas = formulas;
		FormulasR1C1 = formulasR1C1;
		PathOriginal = pathOriginal;
		PathCopy = pathCopy;
		Flag = flag;
		DefaultValueBound = defaultValueBound;
		TestCases = testCases;
		Fragments = fragments;
		Inputs = inputs;
		Outputs = outputs;
		Interims = interims;
		ValueBounds = valueBounds;
		CellsInRange = cellsInRange;
		FaultyValues = faultyValues;
		Assertions = assertions;
		Types = types;
		CorrectFormulas = correctFormulas;
		UserSettings = userSettings;
	}

	/**
	 * @return the formulas
	 */
	public Dictionary<String, String> getFormulas() {
		return Formulas;
	}

	/**
	 * @param formulas
	 *            the formulas to set
	 */
	public void setFormulas(Dictionary<String, String> formulas) {
		Formulas = formulas;
	}
	
	/**
	 * @return the formulasR1C1
	 */
	public Dictionary<String, String> getFormulasR1C1() {
		return FormulasR1C1;
	}

	/**
	 * @param formulasR1C1
	 *            the formulas to set
	 */
	public void setFormulasR1C1(Dictionary<String, String> formulasR1C1) {
		FormulasR1C1 = formulasR1C1;
	}

	/**
	 * @return the pathOriginal
	 */
	public String getPathOriginal() {
		return PathOriginal;
	}

	/**
	 * @param pathOriginal
	 *            the pathOriginal to set
	 */
	public void setPathOriginal(String pathOriginal) {
		PathOriginal = pathOriginal;
	}

	/**
	 * @return the pathCopy
	 */
	public String getPathCopy() {
		return PathCopy;
	}

	/**
	 * @param pathCopy
	 *            the pathCopy to set
	 */
	public void setPathCopy(String pathCopy) {
		PathCopy = pathCopy;
	}

	/**
	 * @return the flag
	 */
	public ExquisiteFlag getFlag() {
		return Flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(ExquisiteFlag flag) {
		Flag = flag;
	}

	/**
	 * @return the defaultValueBound
	 */
	public ExquisiteValueBound getDefaultValueBound() {
		return DefaultValueBound;
	}

	/**
	 * @param defaultValueBound
	 *            the defaultValueBound to set
	 */
	public void setDefaultValueBound(ExquisiteValueBound defaultValueBound) {
		DefaultValueBound = defaultValueBound;
	}

	/**
	 * @return the testCases
	 */
	public Dictionary<String, TestCase> getTestCases() {
		return TestCases;
	}

	/**
	 * @param testCases
	 *            the testCases to set
	 */
	public void setTestCases(Dictionary<String, TestCase> testCases) {
		TestCases = testCases;
	}
	
	/**
	 * @return the fragments
	 */
	public Dictionary<String, Fragment> getFragments() {
		return Fragments;
	}

	/**
	 * @param fragments
	 *            the fragments to set
	 */
	public void setFragments(Dictionary<String, Fragment> fragments) {
		Fragments = fragments;
	}

	/**
	 * @return the inputs
	 */
	public List<String> getInputs() {
		return Inputs;
	}

	/**
	 * @param inputs
	 *            the inputs to set
	 */
	public void setInputs(List<String> inputs) {
		Inputs = inputs;
	}

	/**
	 * @return the outputs
	 */
	public List<String> getOutputs() {
		return Outputs;
	}

	/**
	 * @param outputs
	 *            the outputs to set
	 */
	public void setOutputs(List<String> outputs) {
		Outputs = outputs;
	}

	/**
	 * @return the interims
	 */
	public List<String> getInterims() {
		return Interims;
	}

	/**
	 * @param interims
	 *            the interims to set
	 */
	public void setInterims(List<String> interims) {
		Interims = interims;
	}
	
	/**
	 * @return the valueBounds
	 */
	public Dictionary<String, ExquisiteValueBound> getValueBounds() {
		return ValueBounds;
	}

	/**
	 * @param valueBounds
	 *            the valueBounds to set
	 */
	public void setValueBounds(
			Dictionary<String, ExquisiteValueBound> valueBounds) {
		ValueBounds = valueBounds;
	}

	/**
	 * @return the cellsInRange
	 */
	public Dictionary<String, List<String>> getCellsInRange() {
		return CellsInRange;
	}

	/**
	 * @param cellsInRange
	 *            the cellsInRange to set
	 */
	public void setCellsInRange(Dictionary<String, List<String>> cellsInRange) {
		CellsInRange = cellsInRange;
	}

	/**
	 * @return the faultyValues
	 */
	public Dictionary<String, String> getFaultyValues() {
		return FaultyValues;
	}

	/**
	 * @param faultyValues
	 *            the faultyValues to set
	 */
	public void setFaultyValues(Dictionary<String, String> faultyValues) {
		FaultyValues = faultyValues;
	}

	/**
	 * @return the assertions
	 */
	public Dictionary<String, String> getAssertions() {
		return Assertions;
	}

	/**
	 * @param assertions
	 *            the assertions to set
	 */
	public void setAssertions(Dictionary<String, String> assertions) {
		Assertions = assertions;
	}

	/**
	 * @return the types
	 */
	public Dictionary<String, String> getTypes() {
		return Types;
	}

	/**
	 * @param types
	 *            the types to set
	 */
	public void setTypes(Dictionary<String, String> types) {
		Types = types;
	}

	/**
	 * @return the correctFormulas
	 */
	public Dictionary<String, String> getCorrectFormulas() {
		return CorrectFormulas;
	}

	/**
	 * @param correctFormulas
	 *            the correctFormulas to set
	 */
	public void setCorrectFormulas(Dictionary<String, String> correctFormulas) {
		CorrectFormulas = correctFormulas;
	}
	
	/** 
	 * @return User settings
	 */
	public ExquisiteUserSettings getUserSettings(){
		return UserSettings;
	}
	/**
	 * @param value
	 * 			configuration settings for this debugging session.
	 */
	public void setUserSettings(ExquisiteUserSettings value){
		UserSettings = value;
	}
	
	/**
	 * Load appXML from source xml file.
	 * @param xmlFilePath - the full path to the xml file to be read.
	 */
	public static ExquisiteAppXML parseToAppXML(String xmlFilePath)
	{	
		XMLParser xmlParser = new XMLParser();
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(xmlFilePath)));
			String line;
			StringBuilder sb = new StringBuilder();
	
			while((line=br.readLine())!= null){
			    sb.append(line.trim());
			}
			br.close();
			xmlParser.parse(sb.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return xmlParser.getExquisiteAppXML();
	}
	
	/**
	 * for debugging...
	 */
	@Override public String toString() {
	    StringBuilder result = new StringBuilder();
	    String newLine = System.getProperty("line.separator");

	    result.append( this.getClass().getName() );
	    result.append( " {" );
	    result.append(newLine);

	    //determine fields declared in this class only (no fields of superclass)
	    Field[] fields = this.getClass().getDeclaredFields();

	    //print field names paired with their values
	    for ( Field field : fields  ) {
	      result.append("  ");
	      try {
	        result.append( field.getName() );
	        result.append(": ");
	        //requires access to private field:
	        result.append( field.get(this) );
	      }
	      catch ( IllegalAccessException ex ) {
	        System.out.println(ex);
	      }
	      result.append(newLine);
	    }
	    result.append("}");

	    return result.toString();
	  }

	/**
	 * Adds a vertex to the graph for each cell (input, interim and output) in appXML
	 */
	public void buildGraph(ExquisiteGraph<String> exquisiteGraph){
		for (String input : getInputs()) {
			exquisiteGraph.addVertex(input);			
		}	
		
		for (String interim : getInterims()) {
			exquisiteGraph.addVertex(interim);			
		}
		
		for (String output : getOutputs()) {
			exquisiteGraph.addVertex(output);			
		}
	}
}
