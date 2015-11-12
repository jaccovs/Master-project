package org.exquisite.datamodel.serialisation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("ExquisiteDiagnosisResults")
/**
 * For serializing tests.diagnosis results into a format compatible with the
 * Exquisite .NET client.
 * @author David
 *
 */
public class ExquisiteDiagnosisResults implements Serializable {

	/**
	 * auto generated serial UID
	 */
	private static final long serialVersionUID = -7471290207796603325L;
	
	/**
	 * Total calculation (i.e. running) time.
	 */
	@XStreamAlias("CalculationTime")
	public String calculationTime;
	
	/**
	 * The collection of diagnoses.
	 */
	@XStreamAlias("Results")
	public List<ExquisiteDiagnosisResult> results = new ArrayList<ExquisiteDiagnosisResult>();
	
	/**
	 * Total test case count
	 */
	@XStreamAlias("TestCaseCount")
	public String testCaseCount;	
		
	/**
	 * A namespace required for the ExquisiteClient 
	 */
    @XStreamAsAttribute 
    @XStreamAlias("xmlns:i")
    final String ilink="http://www.w3.org/2001/XMLSchema-instance";
	
    /**
	 * Another namespace required for the ExquisiteClient 
	 */
    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    final String xmlns = "http://schemas.datacontract.org/2004/07/ExquisiteAddIn.Sources.DataModels";
	
    /**
	 * Serializes object with namespaces for ExquisiteClient...
	 * @return
	 */
	public String toXML(){		
		XStream xs = new XStream();		
		xs.autodetectAnnotations(true);
		xs.processAnnotations(ExquisiteDiagnosisResults.class);
		xs.processAnnotations(ExquisiteDiagnosisResult.class);
		String result = xs.toXML(this);
		return result;	
	}
		
	public void sortCandidateList(){
		//sort the list
		//TODO
		//Collections.sort(this.results);
	}
}
