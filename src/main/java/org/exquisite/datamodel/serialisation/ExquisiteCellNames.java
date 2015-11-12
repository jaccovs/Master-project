package org.exquisite.datamodel.serialisation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("ExquisiteCellNames")
/**
 * For serializing a list of cell names into a format compatible with the
 * Exquisite .NET client.
 * @author Thomas
 *
 */
public class ExquisiteCellNames implements Serializable {

	/**
	 * auto generated serial UID
	 */
	private static final long serialVersionUID = -1658094925925250367L;
	
	@XStreamAlias("CellNames")
	@XStreamConverter(value=ListStringXStreamConverter.class, strings={"d4p1:string"})
	public List<String> cellNames = new ArrayList<String>();
	
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
		xs.processAnnotations(ExquisiteCellNames.class);
		String result = xs.toXML(this);
		return result;
	}

}
