package org.exquisite.datamodel;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.exquisite.datamodel.serialisation.ListStringXStreamConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("Fragment")
/**
 * 
 * @author Thomas
 *
 */
public class Fragment{

	
	
	@XStreamAlias("Inputs")
	@XStreamConverter(value=ListStringXStreamConverter.class, strings={"d4p1:string"})
	private List<String> Inputs;
	
	@XStreamAlias("Interims")
	@XStreamConverter(value=ListStringXStreamConverter.class, strings={"d4p1:string"})
	private List<String> Interims;
	
	@XStreamAlias("LinkedFragments")
	@XStreamConverter(value=ListStringXStreamConverter.class, strings={"d4p1:string"})
	private List<String> LinkedFragments;
	
    @XStreamAlias("Name")
	private String Name;
	
	@XStreamAlias("Outputs")
	@XStreamConverter(value=ListStringXStreamConverter.class, strings={"d4p1:string"})
	private List<String> Outputs;
	
	@XStreamAlias("Representative")
	private String Representative;
	
	@XStreamAlias("Complexity")
	private float Complexity;
	
	@XStreamOmitField
	private Dictionary<String, TestCase> TestCases;
	
	public Fragment() {
		Name = "";
		Inputs = new ArrayList<String>();
		Outputs = new ArrayList<String>();
		Interims = new ArrayList<String>();
		TestCases = new Hashtable<String, TestCase>();
		Representative = "";
		LinkedFragments = new ArrayList<String>();
		Complexity = 0f;
	}
	
	public Fragment(String name, 
			List<String> inputs, 
			List<String> outputs, 
			List<String> interims, 
			Dictionary<String, TestCase> testCases, 
			String representative, 
			List<String> linkedFragments,
			float complexity) {
		Name = name;
		Inputs = inputs;
		Outputs = outputs;
		Interims = interims;
		TestCases = testCases;
		Representative = representative;
		LinkedFragments = linkedFragments;
		Complexity = complexity;
	}
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		this.Name = name;
	}
	public List<String> getInputs() {
		return Inputs;
	}
	public void setInputs(List<String> inputs) {
		this.Inputs = inputs;
	}
	public List<String> getOutputs() {
		return Outputs;
	}
	public void setOutputs(List<String> outputs) {
		Outputs = outputs;
	}
	public List<String> getInterims() {
		return Interims;
	}
	public void setInterims(List<String> interims) {
		Interims = interims;
	}
	public Dictionary<String, TestCase> getTestCases() {
		return TestCases;
	}
	public void setTestCases(Dictionary<String, TestCase> testCases) {
		TestCases = testCases;
	}
	public String getRepresentative() {
		return Representative;
	}
	public void setRepresentative(String representative) {
		this.Representative = representative;
	}
	public List<String> getLinkedFragments() {
		return LinkedFragments;
	}
	public void setLinkedFragments(List<String> linkedFragments) {
		this.LinkedFragments = linkedFragments;
	}
	public float getComplexity() {
		return Complexity;
	}
	public void setComplexity(float complexity) {
		this.Complexity = complexity;
	}
	
	/**
	 * Serializes object with namespaces for ExquisiteClient...
	 * @return
	 */
	public String toXML(){		
		XStream xs = new XStream();		
		xs.autodetectAnnotations(true);
		xs.processAnnotations(Fragment.class);
		String result = xs.toXML(this);
		return result;
	}

}
