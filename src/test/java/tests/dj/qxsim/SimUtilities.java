package tests.dj.qxsim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Some utilities here and there
 * @author dietmar
 *
 */
public class SimUtilities {

	/** 
	 * Return the constraint name
	 * @param name
	 * @return
	 */
	public static Constraint getConstraintByName(String name, Map<Constraint,String> constraintNames) {
		for (Constraint ct : constraintNames.keySet()) {
			if (constraintNames.get(ct).equals(name)){
				return ct;
			}
		}
		return null;
	}
	
	/**
	 * Writes stuff to the file system
	 * 
	 * @throws Exception
	 */
	public static void writeConstraintsToFile(String filename, Map<Constraint, String> constraintNames, List<List<Constraint>> conflicts) throws Exception {
		
		File f = new File(filename);
		BufferedWriter bw = new BufferedWriter (new FileWriter (f));
		try {
			StringBuffer buf = new StringBuffer();
			for (String cname : constraintNames.values()) {
				buf.append(cname).append(" ");
			}
			buf.append("\n");
			for (List<Constraint> ct : conflicts) {
				buf.append(conflictToString(constraintNames, ct)).append(" ");
			}
			bw.write(buf.toString());
		}
		finally {
			bw.close();
		}
	}
	
	/**
	 * Loads stuff
	 * @throws Exception
	 */
	public static void loadConstraintsFromFile(File f,  Map<Constraint, String> constraintNames, List<List<Constraint>> conflicts) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		try {
			String line = reader.readLine();
			String[] ctnames = line.split(" ");
			
			for (int i=0;i<ctnames.length;i++) {
				constraintNames.put(createDummyConstraint(), ctnames[i]);
			}
			line = reader.readLine();
	//		System.out.println("2nd line: " + line);
			String[] cfs = line.split(" ");
			for (String cf : cfs) {
	//			System.out.println("Found conflict" + cf );
				String cftrimmed = cf.substring(1,cf.length()-1);
				String[] cnames = cftrimmed.split(",");
				List<Constraint> conflict = new ArrayList<Constraint>();
				for (String cname : cnames) {
					// Get the constraint from the map
					conflict.add(getConstraintByName(cname, constraintNames));
				}
				conflicts.add(conflict);
			}
		}
		finally {
			reader.close();
		}

	}
	
	/**
	 * Creates a dummy constraint
	 */
	public static Constraint createDummyConstraint() {
		IntegerVariable v = Choco.makeIntVar("dummy", 0,1);
		return (Choco.eq(v,1));
	}
	
	/**
	 * returns a string rep of the conflict
	 * @param constraints
	 * @return
	 */
	public static String conflictToString(Map<Constraint,String> constraintNames, List<Constraint> constraints) {
		if (constraints == null) {
			return null;
		}
		List<String> names = new ArrayList<String>();
		for (Constraint c : constraints) {
			String name = constraintNames.get(c);
			if (name == null) {
				System.out.println("Name is null for " + c.getName());
				System.out.println("Size of constraints: " + constraints.size() + ", size of constraintNames: " + constraintNames.size());
			}
			names.add(name);
		}
		Collections.sort(names);
		
		return names.toString().replace(" ","");
	}
	
	

}
