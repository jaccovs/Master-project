package tests.dj.othertests;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.exquisite.data.ConstraintsFactory;
import org.exquisite.data.DiagnosisModelLoader;
import org.exquisite.data.VariablesFactory;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.i8n.Culture;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import choco.cp.model.CPModel;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;

/**
 * A firs test for the constraint graphs...
 * @author dietmar
 *
 */
public class ConstraintGraphTest {
	
//	 Do some small test
	String xmlFilePath = ".\\experiments\\enase-2013\\singleFault\\exquisite_20.xml";
//	String xmlFilePath = ".\\experiments\\enase-2013\\doubleFault\\ex20.xml";

	ExquisiteSession sessionData;
	
	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		ConstraintGraphTest cgt = new ConstraintGraphTest();
		
		try {
			cgt.run();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		System.out.println(" -- Constraint graph test ended --");
	}
	
	
	/**
	 * Does the main work
	 * @throws Exception
	 */
	void run() throws Exception  {
		System.out.println(" -- Running CGT -- ");
		// Setting up the reader
		Culture.setCulture(Locale.GERMAN);
		ExquisiteAppXML appXML = ExquisiteAppXML.parseToAppXML(xmlFilePath);
		//transform the model into a CSP model...
		this.sessionData = new ExquisiteSession(appXML);
		ConstraintsFactory conFactory = new ConstraintsFactory(sessionData);
		Dictionary<String, IntegerExpressionVariable> variablesMap = new Hashtable<String, IntegerExpressionVariable>();
		VariablesFactory varFactory = new VariablesFactory(variablesMap);
		DiagnosisModelLoader modelLoader = new DiagnosisModelLoader(sessionData, varFactory, conFactory);
		modelLoader.loadDiagnosisModelFromXML();	
		System.out.println("Loading to diagnosis model was done");
		
		
		// Let's try to construct a cp model
		CPModel cpmodel = new CPModel();
		// Copy stuff there.
		// copy the variables
		for (Variable v : sessionData.diagnosisModel.getVariables()) {
			cpmodel.addVariable(v);
		}
		// Let's get the input variables
		Set<String> inputs = new HashSet(appXML.getInputs());

		
//		System.out.println("nb of correct ones: " + model.getCorrectStatements().size());
		
		// add the other constraints from the model
		for(Constraint constraint : this.sessionData.diagnosisModel.getPossiblyFaultyStatements()) 
		{
			cpmodel.addConstraint(constraint);
		}
		Map<String, Integer> constraintArities = new HashMap<String,Integer>();
		// Remember where we created new entries
		List<Constraint> splitPoints = new ArrayList<Constraint>(); 

		//  Lets get some better sorting
		List<Constraint> sortedConstraints = 
				QuickXPlain.sortConstraintsByArityAndCalculuateSplittingPoints(
						this.sessionData.diagnosisModel, inputs, splitPoints);
//		
//		
//		// Get a reverse map
//		Map<Constraint, String> ctNames = model.getConstraintNames();
//		Map<String, Constraint> ctsByName = Utilities.reverse(ctNames);
//
//		
//		Iterator<Constraint> ct_it = cpmodel.getConstraintIterator();
//		Constraint ct = null;
//		
//		
//		Map<String, Set<String>> varsOfConstraints = new HashMap<String, Set<String>>();
//		
//		System.out.println("Inputs: " + inputs);
//		
//		Set<String> constraintNames = new HashSet<String>();
//		
//		while (ct_it.hasNext()) {
//			ct = ct_it.next();
//			String name = model.getConstraintName(ct);
//			constraintNames.add(name);
////			System.out.println("Constraint " + name);
//			Set<Variable> allVarNamesOfConstraint = Utilities.getAllVariablesOfConstraint(ct);
////			System.out.println("Got the following variables: " + vars);
//			Set<String> relevantVarNamesOfConstraint = new HashSet<String>();
//			for (Variable var: allVarNamesOfConstraint) {
//				if (!var.getName().equals(name) && !inputs.contains(var.getName())) {
//					relevantVarNamesOfConstraint.add(var.getName());
//				}
//			} 
//			varsOfConstraints.put(name, relevantVarNamesOfConstraint);
////			System.out.println("Vars of " + name);
////			System.out.println(relevantVarNamesOfConstraint);
//			
//			constraintArities.put(name,relevantVarNamesOfConstraint.size());
//			
//		}
//		
//		System.out.println("Arities of constraints: ");
//		
//		// Let's sort them according to their arity
//		constraintArities = Utilities.sortByValueDescending(constraintArities);
//		System.out.println(constraintArities);
//		// Here's the list of constraints to group
//		// Allocate them in a list
//		List<String> sortedConstraints = new ArrayList<String>();
//		System.out.println("Got " + constraintNames.size() + " constraints");
//		
//		
//		while (constraintNames.size() > 0) {
//			// get the first of the list
//			List<String> constraintKeys = new ArrayList<String>(constraintArities.keySet());
//			String first = constraintKeys.get(0);
////			System.out.println("First: " + first);
//			// put it in the list
//			if (!sortedConstraints.contains(first)) {
////				System.out.println("Adding first");
//				sortedConstraints.add(first);
//			}
//			
//			
//			// remove it from the the map
//			constraintArities.remove(first);
//			// Remove the name of the list to be worked on
//			constraintNames.remove(first);
//			// Get the constraints of the first
//			Set<String> constraintsOfFirst = varsOfConstraints.get(first);
//			// Put them all into the list, remove the names to be worked on and reduce the counter for the rest in the
//			// arities map.
//			for (String c : constraintsOfFirst) {
//				if (!sortedConstraints.contains(c)) {
//					sortedConstraints.add(c);
//				}
//				constraintNames.remove(c);
//				for (String key : constraintArities.keySet()) {
//					Set<String> theVars = varsOfConstraints.get(key);
////					System.out.println("Have to check vars of first: " + key + ": "+ theVars);
//					if (theVars.contains(key)) {
////						System.out.println("Found var, have to reduce the aritiy of " + key);
//						Integer count = constraintArities.get(key);
//						if (count != null && count > 0) {
//							constraintArities.put(key, count-1);
//						}
//						else {
//							constraintArities.remove(key);
//						}
//					}
//				}
//			}
//			// Remember where we added a new constraint
////			System.out.println("Split point after some elements at "  + (sortedConstraints.size()-1));
//			String splitname = sortedConstraints.get(sortedConstraints.size()-1);
////			System.out.println("Constraint is " + splitname);
//			if (!splitPoints.contains(ctsByName.get(splitname))) {
//				splitPoints.add(ctsByName.get(splitname));
//			}
////			System.out.println("Current list after iteration: " + sortedConstraints + " (" + sortedConstraints.size() + " elements)");
//			constraintArities = Utilities.sortByValueDescending(constraintArities);
////			System.out.println("Current arities: " + constraintArities);
//		}
//		System.out.println("Sorted list of size: " + sortedConstraints.size() + "\n" + sortedConstraints);
//		System.out.println("\nSplit points: " + Utilities.printConstraintList(splitPoints, model));
//		
//		// Get the list of possibly faulty constraints
//		List<Constraint> orderedPossiblyFaultyConstraints = new ArrayList<Constraint>();
		
		
//		for (String ctName : sortedConstraints) {
//			orderedPossiblyFaultyConstraints.add(ctsByName.get(ctName));
//		}
		
		// single test
		Example example = this.sessionData.diagnosisModel.getPositiveExamples().get(0);
		DiagnosisModel copiedModel = new DiagnosisModel(this.sessionData.diagnosisModel);
		
		// add the example to the set of correct constraints
		for(Constraint constraint : example.constraints)
		{
			copiedModel.addCorrectConstraint(constraint, example.constraintNames.get(constraint));
		}
		
		copiedModel.removeConstraintsToIgnore(new ArrayList<Constraint>());
		
//		QuickXPlain qx = new QuickXPlain();
//		qx.setConstraintModel(copiedModel);
//		ExampleChecker exChecker = new ExampleChecker(qx);
//		exChecker.setModel(model);
//		
//		// Get the first example
//		example = model.getPositiveExamples().get(0);
//		copiedModel = new DiagnosisModel(model);
//		copiedModel.setPossiblyFaultyStatements(orderedPossiblyFaultyConstraints);
//		// Set the splitpoints
////		qx.splitPoints = splitPoints;
//
//		// add the example to the set of correct constraints
//		for(Constraint constraint : example.constraints)
//		{
//			copiedModel.addCorrectConstraint(constraint, example.constraintNames.get(constraint));
//		}
//		
//		copiedModel.removeConstraintsToIgnore(new ArrayList<Constraint>());
//		qx.setConstraintModel(copiedModel);
//		
//		long start = System.currentTimeMillis();
//		List<Constraint> conflict = qx.findConflict();
//		long stop = System.currentTimeMillis();
//		long duration = stop - start;
//
//		System.out.println("Found a conflict: " + Utilities.printConstraintList(conflict, model));
//		System.out.println("Solves: " + qx.getSolverCalls());
//		System.out.println("Time: " + duration);
//		
//		
		
		
//		// multiple tests
//		
//		
//	
		// Try to find one first conflict.
		Debug.DEBUGGING_ON = true;

		long totalTimeOrig = 0;
		long totalTimeSorted = 0;
		int iterations = 1;
		for (int i=0;i<=iterations;i++) {
			// Skip the first run, ramp up ..
			QuickXPlain qx = new QuickXPlain(sessionData, null);
									
			// Get the first example
			example = this.sessionData.diagnosisModel.getPositiveExamples().get(0);
			copiedModel = new DiagnosisModel(this.sessionData.diagnosisModel);
			
			// add the example to the set of correct constraints
			for(Constraint constraint : example.constraints){
				copiedModel.addCorrectConstraint(constraint, example.constraintNames.get(constraint));
			}
			
			copiedModel.removeConstraintsToIgnore(new ArrayList<Constraint>());
			qx.setDiagnosisModel(copiedModel);

			System.out.println(" -------------- ");
			//qx.resetSearcher();
			long start = System.currentTimeMillis();
			List<Constraint> conflict = qx.findConflict();
			long stop = System.currentTimeMillis();
			long duration1 = stop - start;
		
			if (i>0) {
				System.out.println("Found a conflict: " + Utilities.printConstraintList(conflict, this.sessionData.diagnosisModel));
//				System.out.println("Solves: " + qx.getSolverCalls());
				System.out.println("Time: " + duration1);
				System.out.println();
				totalTimeOrig+= duration1;
			}
			// Get the first example
			example = this.sessionData.diagnosisModel.getPositiveExamples().get(0);
			copiedModel = new DiagnosisModel(this.sessionData.diagnosisModel);
			
			// add the example to the set of correct constraints
			for(Constraint constraint : example.constraints)
			{
				copiedModel.addCorrectConstraint(constraint, example.constraintNames.get(constraint));
			}
			
			copiedModel.removeConstraintsToIgnore(new ArrayList<Constraint>());
			qx.setDiagnosisModel(copiedModel);
		
			
			qx = new QuickXPlain(sessionData, null);
									
			// Get the first example
			example = this.sessionData.diagnosisModel.getPositiveExamples().get(0);
			copiedModel = new DiagnosisModel(this.sessionData.diagnosisModel);
			copiedModel.setPossiblyFaultyStatements(sortedConstraints);

			// add the example to the set of correct constraints
			for(Constraint constraint : example.constraints)
			{
				copiedModel.addCorrectConstraint(constraint, example.constraintNames.get(constraint));
			}
			
			copiedModel.removeConstraintsToIgnore(new ArrayList<Constraint>());
			qx.setDiagnosisModel(copiedModel);
			
			start = System.currentTimeMillis();
			conflict = qx.findConflict();
			stop = System.currentTimeMillis();
			long duration2 = stop - start;

			if (i>0) {
				System.out.println("Found a conflict: " + Utilities.printConstraintList(conflict, this.sessionData.diagnosisModel));
//				System.out.println("Solves: " + qx.getSolverCalls());
				System.out.println("Time: " + duration2);
				totalTimeSorted+= duration2;
			}
		}
		System.out.println("avg orig: " + (totalTimeOrig / iterations));
		System.out.println("avg sorted: " + (totalTimeSorted/ iterations));
		
	}
}
