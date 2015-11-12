package evaluations.tools.modelReader;

/**
 * A wrapper for domain definitions
 * @author Dietmar
 *
 */
class Domain {
	public String name;
	public int lb;
	public int ub;
	public int[] values;
	public boolean isEnumeration;
	// constructor
	// Using lower and upper bounds
	public Domain(String name, int lb, int ub) {
		super();
		this.name = name;
		this.lb = lb;
		this.ub = ub;
	}
	
	/**
	 * Create a domain with a set of values
	 * @param name
	 * @param givenValues
	 */
	public Domain(String name, int[] givenValues) {
		super();
		this.name = name;
		this.values = givenValues.clone();
		this.isEnumeration = true;
	}
	
	
	
	// string rep.
	public String toString() {
		return name + " [" + lb + ".." + ub + "]"; 
	}
}
