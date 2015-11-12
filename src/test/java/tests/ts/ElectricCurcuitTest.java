package tests.ts;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.tools.Utilities;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import evaluations.dxc.synthetic.model.DXCScenarioData;
import evaluations.dxc.synthetic.model.DXCSystemDescription;
import evaluations.dxc.synthetic.tools.DXCDiagnosisModelGenerator;
import evaluations.dxc.synthetic.tools.DXCTools;

/**
 * Class to test the diagnosis of an electric curcuit
 * @author Thomas
 *
 */
public class ElectricCurcuitTest {
	
	private Dictionary<String, IntegerVariable> variables = new Hashtable<String, IntegerVariable>();
	
	static boolean shuffleConstraints = false;

	static int maxDiagSize = -1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ElectricCurcuitTest test = new ElectricCurcuitTest();
		test.runDiagnosisLoadedModel();
	}
	
	/**
	 * Creates a new diagnosis model with given constraints
	 * @param possiblyFaultyConstraints
	 * @param correctConstraints
	 * @return
	 */
	private DiagnosisModel createDiagnosisModel(Dictionary<String, Constraint> possiblyFaultyConstraints, Dictionary<String, Constraint> correctConstraints) {
		DiagnosisModel model = new DiagnosisModel();
		
		// add variables
		Enumeration<String> vars = variables.keys();
		while (vars.hasMoreElements()) {
			String varName = vars.nextElement();
			model.addIntegerVariable(variables.get(varName));
			
		}
		
		// add possibly fault constraints
		Enumeration<String> pfConstraints = possiblyFaultyConstraints.keys();
		while (pfConstraints.hasMoreElements()) {
			String constName = pfConstraints.nextElement();
			model.addPossiblyFaultyConstraint(possiblyFaultyConstraints.get(constName), constName);
		}
		
		// add correct constraints
		Enumeration<String> cConstraints = correctConstraints.keys();
		while (cConstraints.hasMoreElements()) {
			String constName = cConstraints.nextElement();
			model.addCorrectConstraint(correctConstraints.get(constName), constName);
		}

		// add an empty example, because it is needed for diagnosis
		Example ex = new Example();
		List<Example> posExamples = new ArrayList<Example>();
		posExamples.add(ex);
		model.setPositiveExamples(posExamples);
		
		return model;
	}
	
	/**
	 * Runs a diagnosis
	 */
	private void runDiagnosis() {
		try {
			System.out.println("Building model...");
			Dictionary<String, Constraint> formulaConstraints = buildModel74182();
			Dictionary<String, Constraint> correctConstraints = getDataScn000Faulty();
			
			DiagnosisModel diagModel = createDiagnosisModel(formulaConstraints, correctConstraints);
			
			if (shuffleConstraints)
				diagModel.shufflePossiblyFaulyConstraints();
	
			// Create the engine
			ExquisiteSession sessionData = new ExquisiteSession(null,
					null, new DiagnosisModel(diagModel));
			// Do not try to find a better strategy for the moment
			sessionData.config.searchStrategy = SearchStrategies.Default;
			sessionData.config.searchDepth = maxDiagSize;
			
			IDiagnosisEngine engine = EngineFactory
					.makeDAGEngineStandardQx(sessionData);
			
			System.out.println(" Done.");
			System.out.println("Calulating Diagnoses...");
			
			long start = System.currentTimeMillis();
			List<Diagnosis> diagnoses = engine.calculateDiagnoses();
			System.out.println(" Done.");
			long end = System.currentTimeMillis();
			long duration = end - start;
			
			System.out.println("Diagnoses count: " + diagnoses.size());
			System.out.println(Utilities.printSortedDiagnoses(diagnoses, ';'));

			System.out.println("Solved in " + duration + "ms.");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void runDiagnosisLoadedModel() {
		try {
			System.out.println("Loading model...");
			
			// Parse a system description first
//			String xmlFilePath = "experiments/DXCSynthetic/74L85.xml";
			String xmlFilePath = "experiments/DXCSynthetic/74181.xml";
//			String xmlFilePath = "experiments/DXCSynthetic/74182.xml";
//			String xmlFilePath = "experiments/DXCSynthetic/74283.xml";
//			String xmlFilePath = "experiments/DXCSynthetic/c432.xml";	// Takes too long. Search depth 5 is possible
//			String xmlFilePath = "experiments/DXCSynthetic/c499.xml";   // Takes too long
//			String xmlFilePath = "experiments/DXCSynthetic/c1355.xml";	// Takes too long
//			String xmlFilePath = "experiments/DXCSynthetic/c7552.xml";	// Takes too long
			
			
			DXCSystemDescription sd = DXCTools.readSystemDescription(xmlFilePath);
		
			// Parse the scenario
//			String scnFilePath = "experiments/DXCSynthetic/74L85/74L85.019.scn";
			String scnFilePath = "experiments/DXCSynthetic/74181/74181.001.scn";
//			String scnFilePath = "experiments/DXCSynthetic/74182/74182.000.scn";
//			String scnFilePath = "experiments/DXCSynthetic/74283/74283.000.scn";
//			String scnFilePath = "experiments/DXCSynthetic/c432/c432.000.scn";
//			String scnFilePath = "experiments/DXCSynthetic/c499/c499.000.scn";
//			String scnFilePath = "experiments/DXCSynthetic/c1355/c1355.019.scn";
//			String scnFilePath = "experiments/DXCSynthetic/c7552/c7552.000.scn";
			
			
			DXCScenarioData scenario = DXCTools.readScenario(scnFilePath, sd.getSystems().get(0));
			
			System.out.println(" Done.");
			System.out.println("Checking correct state...");
			boolean correct = DXCTools.checkCorrectState(sd.getSystems().get(0), scenario);
			if (correct) {
				System.out.println(" Correct.");
			} else {
				System.out.println(" Not Correct!");
			}
			
			
			
			// Now create the diagnosis model
			DXCDiagnosisModelGenerator dmg = new DXCDiagnosisModelGenerator();
			DiagnosisModel diagModel = dmg.createDiagnosisModel(sd.getSystems().get(0), scenario.getFaultyState());
			
			System.out.println("Model has " + diagModel.getPossiblyFaultyStatements().size() + " constraints.");
			
			if (shuffleConstraints)
				diagModel.shufflePossiblyFaulyConstraints();
	
			// Create the engine
			ExquisiteSession sessionData = new ExquisiteSession(null,
					null, new DiagnosisModel(diagModel));
			// Do not try to find a better strategy for the moment
			sessionData.config.searchStrategy = SearchStrategies.Default;
			sessionData.config.searchDepth = -1;
			sessionData.config.maxDiagnoses = -1;
			
			IDiagnosisEngine engine = EngineFactory
					.makeDAGEngineStandardQx(sessionData); //, 4);
			
			System.out.println("Calulating Diagnoses...");
			
			long start = System.currentTimeMillis();
			List<Diagnosis> diagnoses = engine.calculateDiagnoses();
			System.out.println(" Done.");
			long end = System.currentTimeMillis();
			long duration = end - start;
			
			System.out.println("Diagnoses count: " + diagnoses.size());
			System.out.println(Utilities.printSortedDiagnoses(diagnoses, ';'));

			System.out.println("Solved in " + duration + "ms.");
			
			Diagnosis correctDiag = DXCTools.checkFaultyComponentsinDiagnoses(diagModel, diagnoses, sd.getSystems().get(0), scenario);
			if (correctDiag != null) {
				System.out.println("Correct diagnosis found: " + correctDiag.toString());
			} else {
				System.out.println("No correct diagnosis found!");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Dictionary<String, Constraint> buildModel74182() {
		Dictionary<String, Constraint> formulaConstraints = new Hashtable<String, Constraint>();
		
		
		makeBoolVars("i1", "i2", "i3", "i4", "i5", "i6", "i7", "i8", "i9", "o1", "o2", "o3", "o4", "o5");
		makeBoolVars("z1", "z2", "z3", "z4", "z5", "z6", "z7", "z8", "z9", "z10", "z11", "z12", "z13", "z14");
		
		formulaConstraints.put("gate49", not("z1", "i9"));
		formulaConstraints.put("gate53", or("o1", "i1", "i3", "i5", "i7"));
		formulaConstraints.put("gate54", and("z2", "i2", "i4", "i6", "i8"));
		formulaConstraints.put("gate55", and("z3", "i5", "i2", "i4", "i6"));
		formulaConstraints.put("gate56", and("z4", "i3", "i2", "i4"));
		formulaConstraints.put("gate57", and("z5", "i1", "i2"));
		formulaConstraints.put("gate58", or("o2", "z2", "z3", "z4", "z5"));
		formulaConstraints.put("gate59", and("z6", "i4", "i6", "i8", "z1"));
		formulaConstraints.put("gate60", and("z7", "i7", "i4", "i6", "i8"));
		formulaConstraints.put("gate61", and("z8", "i5", "i4", "i6"));
		formulaConstraints.put("gate62", and("z9", "i3", "i4"));
		formulaConstraints.put("gate63", nor("o3", "z6", "z7", "z8", "z9"));
		formulaConstraints.put("gate64", and("z10", "i6", "i8", "z1"));
		formulaConstraints.put("gate65", and("z11", "i7", "i6", "i8"));
		formulaConstraints.put("gate66", and("z12", "i5", "i6"));
		formulaConstraints.put("gate67", nor("o4", "z10", "z11", "z12"));
		formulaConstraints.put("gate68", and("z13", "i8", "z1"));
		formulaConstraints.put("gate69", and("z14", "i7", "i8"));
		formulaConstraints.put("gate70", nor("o5", "z13", "z14"));
		
		return formulaConstraints;
	}
	
	private Dictionary<String, Constraint> addDataScn000() {
		Dictionary<String, Constraint> variableConstraints = new Hashtable<String, Constraint>();
		variableConstraints.put("true", Choco.and(variables.get("i2"), variables.get("i3"), variables.get("i5"), variables.get("i6"), 
				variables.get("i8"), variables.get("o1"), variables.get("o3")));
		variableConstraints.put("false", Choco.nor(variables.get("i1"), variables.get("i4"), variables.get("i7"), variables.get("i9"), 
				variables.get("o2"), variables.get("o4"), variables.get("o5")));
		
		return variableConstraints;
	}
	
	private Dictionary<String, Constraint> getDataScn000Faulty() {
		Dictionary<String, Constraint> constraints = new Hashtable<String, Constraint>();
		constraints.put("true", Choco.and(variables.get("i2"), variables.get("i3"), variables.get("i5"), variables.get("i6"), 
				variables.get("i8"), variables.get("o2"), variables.get("o4"), variables.get("o5")));
		constraints.put("false", Choco.nor(variables.get("i1"), variables.get("i4"), variables.get("i7"), variables.get("i9"), 
				variables.get("o1"), variables.get("o3")));
		
		return constraints;
	}
	
	private Constraint not(String output, String input) {
		return Choco.reifiedNot(variables.get(output), variables.get(input));
	}
	
	private Constraint or(String output, String... inputs) {
		IntegerVariable[] inputVariables = new IntegerVariable[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			inputVariables[i] = variables.get(inputs[i]);
		}
		return Choco.reifiedOr(variables.get(output), inputVariables);
	}
	
	private Constraint and(String output, String... inputs) {
		IntegerVariable[] inputVariables = new IntegerVariable[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			inputVariables[i] = variables.get(inputs[i]);
		}
		return Choco.reifiedAnd(variables.get(output), inputVariables);
	}
	
	private Constraint nor(String output, String... inputs) {
		IntegerVariable[] inputVariables = new IntegerVariable[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			inputVariables[i] = variables.get(inputs[i]);
		}
		return Choco.reifiedNor(variables.get(output), inputVariables);
	}

	private IntegerVariable makeBoolVar(String name) {
		IntegerVariable var = Choco.makeBooleanVar(name);
//		model.addVariable(var);
		variables.put(name, var);
//		graph.addVertex(name);		
		return var;
	}
	
	private void makeBoolVars(String... names) {
		for (int i = 0; i < names.length; i++) {
			makeBoolVar(names[i]);
		}
	}
}
