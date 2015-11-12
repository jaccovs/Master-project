package evaluations.tools.modelReader;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.extension.LargeRelation;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * This method helps us to read some of the XCSP-files which did not work
 * @author Dietmar
 *
 */
public class ModelReader extends DefaultHandler {

	/**
	 * The model to fill
	 */
	public CPModel cpmodel;
	
	
	/**
	 * A map from domain names to definitions
	 */
	Map<String, Domain> domainDefinitions = new HashMap<String,Domain>();

	
	/**
	 * Remembering types for variables
	 */
	Map<String, String> variableDefinitions = new HashMap<String, String>();
	
	/*
	 * The conflicts
	 */
	Map<String, Relation> conflicts = new HashMap<String, Relation>();
	
	/*
	 * The allowed tuples
	 * */
	public Map<String, Relation> supports = new HashMap<String, Relation>();

	// the constraints. the key is the relation reference
	public Map<String,MRConstraint> constraints = new HashMap<String, MRConstraint>();
	
	/**
	 * To read stuff
	 */
	StringBuffer currentValue = new StringBuffer();
	/**
	 * Remember the last domain name
	 */
	String currentDomainName;
	
	/**
	 * The relation in process
	 */
	Relation currentRelation;

	/**
	 * To remember the cp variables by name
	 */
	Map<String, IntegerVariable> cpvariables = new HashMap<String, IntegerVariable>();
	
	String filename;
	
	
	public ModelReader(String filename) {
		this.filename = filename;
	}


	/**
	 * Creates the model for Choco from the data 
	 */
	public void createCPModel() {
		cpmodel = new CPModel();
		
		// create the variables
		for (String varName : this.variableDefinitions.keySet()) {
			Domain d = this.domainDefinitions.get(this.variableDefinitions.get(varName));
			IntegerVariable v = null;
			if (d.isEnumeration) {
				v = Choco.makeIntVar(varName, d.values);
			}
			else {
//				System.out.println("Creating var: " + d.lb + " " +  d.ub);
				v = Choco.makeIntVar(varName, d.lb, d.ub);
			}
			cpmodel.addVariable(v);
			cpvariables.put(varName,v);
		}
		
		createConstraintsFromRelation(cpmodel, this.conflicts, false);
		createConstraintsFromRelation(cpmodel, this.supports, true);
		
//		System.out.println("Created the CP Model");
	}
	
	
	/**
	 * Creating a tabular constraint
	 * @param r the relation
	 * @return the conflict
	 */
	public void createConstraintsFromRelation(CPModel cpmodel, Map<String, Relation> rel, boolean feasiblePairs ){
		
		// create the feasible constraints.
		for (String relName : rel.keySet()) {
			Relation r = rel.get(relName);
			MRConstraint c = this.constraints.get(relName);
			List<int[]> values = r.values;
			
//			System.out.println("Found relation "  + relName + " and constraint with " + values.size() + " entries");
			
			// find min and max stuff.
			int min[] = new int[values.get(0).length];
			int max[] = new int[values.get(0).length];
			
			// init min to a large value, max will be at 0 and the lowest anyway
			for (int i=0;i<min.length;i++) {
				min[i] = 10000;
			}
			
//			
//			// go through all the entries and find the min and the max
//			for (int[] valueList : values) {
//				for (int i=0;i<valueList.length;i++) {
//					if (valueList[i] < min[i]) {
//						min[i] = valueList[i];
//					}
//					if (valueList[i] > max[i]) {
//						max[i] = valueList[i];
//					}
//				}
//			}
			
//			printArray(min);
//			printArray(max);
			
			// create the variable list
			IntegerVariable[] involvedVars = new IntegerVariable[c.variables.size()];
			// copy stuff from the variables
			int x = 0;
			for (String varName : c.variables) {
//				System.out.println("Adding variable: " + varName);
				involvedVars[x] = cpvariables.get(varName);
				// set the min and max values for the variable
				IntegerVariable v = cpvariables.get(varName);
				min[x] = v.getLowB();
				max[x] = v.getUppB();
				x++;
			}
			LargeRelation largeRelation = Choco.makeLargeRelation(min, max, values, feasiblePairs);
			cpmodel.addConstraint(Choco.relationTupleAC(involvedVars, largeRelation));
		}
		
	}
	
	
	/*
	 * The main worker method 
	 */
	public void loadDocument() throws Exception {
//		String filename = "data/normalized-renault-mod-0_ext.xml";
//		filename = "data/normalized-renault-mod-46_ext.xml";
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
//		DefaultHandler handler = new DefaultHandler();
		
		
		InputSource input = new InputSource(new FileInputStream(filename));
		xmlReader.setContentHandler(this);
		xmlReader.parse(input);
		
//		System.out.println("domains: " + domainDefinitions.values());
//		System.out.println("variables: " + variableDefinitions.keySet());
//		System.out.println("conflicts: " + conflicts.size());
//		System.out.println("supports: " + supports.size());
//		System.out.println("constraints: " + constraints.size());
	
		// Create the model. Use feasible and infeasible table constraints.
		createCPModel();
	}
	
	

	/**
	 * What to do when we find an element
	 */
	public void startElement(String uri, String localName,String qName, 
            Attributes attributes) throws SAXException {
//		System.out.println(qName);
		switch (qName) {
		case "domain":
			currentValue = new StringBuffer();
			currentDomainName = attributes.getValue(0);
			break;
		case "variable":
			currentValue = new StringBuffer();
			String name = attributes.getValue(0);
			String domain = attributes.getValue(1);
			variableDefinitions.put(name, domain);
			break;
		case "relation":
			currentValue = new StringBuffer();
			name = attributes.getValue(0);
			String type = attributes.getValue(3);
			currentRelation = new Relation(name);
			if (type.equals("conflicts")) {
				conflicts.put(name,currentRelation);
			}
			else {
				supports.put(name, currentRelation);
			}
 			break;
		case "constraint":
			name = attributes.getValue(0);
			String variables = attributes.getValue(2);
			String relation = attributes.getValue(3);
			MRConstraint c = new MRConstraint (name, relation);
			constraints.put(relation, c);
			String[] varnames = variables.split(" ");
			List<String> vars = new ArrayList<String>();
			for (String var : varnames) {
				vars.add(var);
			}
			c.setVariables(vars);
		}
		
	}

	/**
	 * Reading stuff
	 */
	  public void characters(char[] ch, int start, int length)
		      throws SAXException {
		    currentValue.append(ch, start, length);
//		    System.out.println(new String(currentValue));
	 }
	
	/**
	 * What to do when we find an end element
	 */
	public void endElement(String uri, String localName,
			String qName) throws SAXException {
	 
			switch (qName) {
			case "relation":
//				System.out.println("Found relation definition " + currentValue);

				// Copy the relations
				String[] lines = currentValue.toString().trim().split("\\|");
//				System.out.println("Found " + lines.length + " lines");
				for (String line : lines) {
					List<Integer> values = new ArrayList<Integer>();
					String[] tokens = line.split(" ");
					for (String token : tokens) {
						values.add(Integer.parseInt(token));
					}
					currentRelation.addValues(values);
				}
				break;
			case "domain":
//				System.out.println("-- current value: " + currentValue);
				int idx1 = currentValue.indexOf(".");
				int idx2 = currentValue.lastIndexOf(".");
				// there are problems with only one value
				int lb = 0;
				int ub = 0;
				int [] values = null;
//				System.out.println("Current value " + currentValue);
//				System.out.println("idx1: " + idx1);
				if (idx1 == -1) { // only one value or an enumerated domain
					// test for space as indicator for enumerated domains
					int idx3 = currentValue.indexOf(" ");
					if (idx3 == -1) { // must be defined by bounds
						int val = Integer.parseInt(currentValue.toString());
						lb = val; 
						ub = val;
					}
					else {
						String[] numbers = currentValue.toString().split(" ");
						values = new int[numbers.length];
						for (int i=0;i<numbers.length;i++) {
							values[i] = Integer.parseInt(numbers[i]);
						}
						System.out.println("Found enumerated values: ");
//						System.out.println(Util.printArray(values));
						
//						System.err.println("FATAL: Cannot handle enumerated domains so far (" + this.filename + ")");
//						System.exit(1);
//						TODO: Cannot handle mixed domains such as 1 2 3..5 so far..
						
					}
				}
				else {
					lb = Integer.parseInt(currentValue.substring(0,idx1));
					ub = Integer.parseInt(currentValue.substring(idx2+1,currentValue.length()));
					
//					System.out.println("Found domain: " + lb + " " + ub);
					
				}
				if (values == null) {
					domainDefinitions.put(currentDomainName, new Domain(currentDomainName, lb, ub));
				}
				else {
					domainDefinitions.put(currentDomainName, new Domain(currentDomainName, values));
				}
				break;
			}
	 
		}
	
	/**
	 * Test entry point
	 * @param args
	 */
	public static void mainx(String[] args) {
		try {

			ModelReader mr = new ModelReader("data/normalized-renault-mod-0_ext.xml");
			mr.loadDocument();
			
			// now solve the problem
			Solver solver = new CPSolver();
			solver.read(mr.cpmodel);
			
			long start = System.currentTimeMillis();
			long stop = 0;
			if (solver.solve()) {
				stop = System.currentTimeMillis();
				System.out.println("Found solution");
				Iterator<IntDomainVar> it = solver.getIntVarIterator();
				while (it.hasNext()) {
					IntDomainVar var = it.next();
					System.out.println(var);
				}
				System.out.println("Time: " + (System.currentTimeMillis() - stop)  + " ms");
				
				
			}
			else {
				stop = System.currentTimeMillis();
				System.out.println("No solution found");
				System.out.println("Time: " + (System.currentTimeMillis() - stop)  + " ms");
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	


	  

	
}
