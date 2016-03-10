package evaluations.tools.modelReader;

import java.util.ArrayList;
import java.util.List;


/**
 * Relations
 * @author Dietmar
 *
 */
public class Relation {
	public String name;
	public List<int[]> values;
	public int arity = 0;
	public Relation(String name) {
		this.name = name;
		this.values = new ArrayList<int[]>();
	}
	
	/**
	 * Stting the values
	 * @param values
	 */
	public void addValues(List<Integer> values) {
		Object[] obs = values.toArray();
		
		int[] vals = new int[obs.length];
		arity = obs.length;
		for (int i=0;i<vals.length;i++) {
			vals[i] = (int) obs[i];
		}
		
		this.values.add(vals);
	}
	
	
	
	
	
	// string rep
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Relation " + name + " (" + this.values.size() + ") tuples\n");
//		for (int[] tuple : this.values) {
//			result.append(tuple + "\n");
//		}
		return result.toString();
	}
}

