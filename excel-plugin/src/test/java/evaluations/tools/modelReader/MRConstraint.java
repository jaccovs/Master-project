package evaluations.tools.modelReader;

import java.util.ArrayList;
import java.util.List;

/**
 * A constraint
 * in the xml file
 *
 */
public class MRConstraint {
	public String name;
	public String relationReference;
	public List<String> variables;
	
	// constructor
	public MRConstraint(String name, String relationReference) {
		super();
		this.name = name;
		this.relationReference = relationReference;
		this.variables = new ArrayList<String>();
	}
	
	// setting the vals
	public void setVariables(List<String> vals) {
		this.variables = vals;
	}
}

