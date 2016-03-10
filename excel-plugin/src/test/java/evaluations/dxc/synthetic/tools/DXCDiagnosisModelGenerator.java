package evaluations.dxc.synthetic.tools;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import evaluations.dxc.synthetic.model.DXCComponent;
import evaluations.dxc.synthetic.model.DXCConnection;
import evaluations.dxc.synthetic.model.DXCSystem;
import evaluations.dxc.synthetic.model.DXCSystemDescription;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Class to create a DiagnosisModel of a DXCSystem and a DXC scenario.
 * @author Thomas
 *
 */
public class DXCDiagnosisModelGenerator {
	private List<String> componentTypesToVariables = new ArrayList<String>();
	private List<String> componentTypesToIgnore = new ArrayList<String>();
	private Dictionary<String, IntegerVariable> variables = new Hashtable<String, IntegerVariable>();
	
	public DXCDiagnosisModelGenerator() {
		Initialize();
	}

	public static void main(String[] args) {
		// Parse a system description first
		String xmlFilePath = "experiments/DXCSynthetic/74182.xml";

		DXCSyntheticXMLParser parser = new DXCSyntheticXMLParser();

		try {
			System.out.println("Trying to load xml file: " + xmlFilePath);

			BufferedReader br = new BufferedReader(new FileReader(new File(xmlFilePath)));
			String line;
			StringBuilder sb = new StringBuilder();

			while ((line = br.readLine()) != null) {
				sb.append(line.trim());
			}
			br.close();
			parser.parse(sb.toString());

			DXCSystemDescription sd = parser.getSystemDescription();

			System.out.println("System name: " + sd.getSystems().get(0).getSystemName());

			System.out.println("FINISH");

			// Parse the scenario
			String scnFilePath = "experiments/DXCSynthetic/74182/74182.000.scn";


			DXCScenarioParser scnParser = new DXCScenarioParser();

			System.out.println("Trying to load scn file: " + scnFilePath);

			br = new BufferedReader(new FileReader(new File(scnFilePath)));
			sb = new StringBuilder();

			while ((line = br.readLine()) != null) {
				sb.append(line.trim() + "\n");
			}
			br.close();
			scnParser.parse(sb.toString(), sd.getSystems().get(0));

			Dictionary<DXCComponent, Boolean> scenario = scnParser.getScenario().getFaultyState();

			System.out.println("Scenario size: " + scenario.size());

			System.out.println("FINISH");


			// Now create the diagnosis model
			DXCDiagnosisModelGenerator dmg = new DXCDiagnosisModelGenerator();
			DiagnosisModel<Constraint> model = dmg.createDiagnosisModel(sd.getSystems().get(0), scenario);
			System.out.println("Model variables: " + model.getVariables().size());
			System.out.println("Model PF constraints: " + model.getPossiblyFaultyStatements().size());
			System.out.println("Model true constraints: " + model.getCorrectStatements().size());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void Initialize() {
		componentTypesToVariables.add("port");
		componentTypesToVariables.add("probe");

		componentTypesToIgnore.add("port");
		componentTypesToIgnore.add("probe");
		componentTypesToIgnore.add("wire");

//		componentTypesToConstraints.add("inverter");
//		componentTypesToConstraints.add("or");
//		componentTypesToConstraints.add("and");
//		componentTypesToConstraints.add("nand");
//		componentTypesToConstraints.add("nor");
//		componentTypesToConstraints.add("buffer");
	}

	/**
	 * Creates a diagnosis model for the given system and scenario
	 * @param system
	 * @param faultyScenario
	 * @return
	 * @throws Exception
	 */
	public DiagnosisModel<Constraint> createDiagnosisModel(DXCSystem system,
														   Dictionary<DXCComponent, Boolean> faultyScenario)
			throws Exception {
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();

		// create variables
		Enumeration<DXCComponent> components = system.getComponents().elements();
		while (components.hasMoreElements()) {
			DXCComponent component = components.nextElement();
			if (componentTypesToVariables.contains(component.getComponentType().getName())) {
				IntegerVariable var = makeBoolVar(component.getName());
				model.addIntegerVariable(var);
			}
		}

//		System.out.println("Variables created: " + variables.size());

		// create constraints
		components = system.getComponents().elements();
		while (components.hasMoreElements()) {
			DXCComponent component = components.nextElement();

//			if (!component.getName().contains(".")) {
				// split type name and number of inputs
			String typeWoNr;
			int nr = 1;
			String[] typeArr = component.getComponentType().getName().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
			if (typeArr.length > 2) {
				throw new Exception("Type " + component.getComponentType().getName() + " could not be handled.");
			} else if (typeArr.length == 2) {
				nr = Integer.parseInt(typeArr[1]);
			}
			typeWoNr = typeArr[0];

			if (!componentTypesToIgnore.contains(typeWoNr)) {
//				System.out.println("Type: " + typeWoNr + " Nr: " + nr + " Name: " + component.getName());
				String outputConnection = component.getName() + ".o";
				DXCComponent outputComp = SearchConnections(system.getComponents().get(outputConnection), system.getConnections());
				String output = outputComp.getName();
				String[] inputs = new String[nr];
				for (int i = 0; i < nr; i++) {
					String inputConnection = component.getName() + ".i" + (i + 1);
					DXCComponent inputComp = SearchConnections(system.getComponents().get(inputConnection), system.getConnections());
					inputs[i] = inputComp.getName();
				}
				switch (typeWoNr) {
				case "inverter":
					if (nr != 1) {
						throw new Exception("Found inverter with more than 1 input!");
					}
					model.addPossiblyFaultyConstraint(not(output, inputs[0]), component.getName());
					break;

					case "or":
					model.addPossiblyFaultyConstraint(or(output, inputs), component.getName());
					break;

					case "and":
					model.addPossiblyFaultyConstraint(and(output, inputs), component.getName());
					break;

					case "nor":
					model.addPossiblyFaultyConstraint(nor(output, inputs), component.getName());
					break;

					case "buffer":
					model.addPossiblyFaultyConstraint(eq(output, inputs[0]), component.getName());
					break;

					case "nand":
					model.addPossiblyFaultyConstraint(nand(output, inputs), component.getName());
					break;

					case "xor":
					if (nr != 2) {
						throw new Exception("Found xor with more or less than 2 inputs!");
					}
					model.addPossiblyFaultyConstraint(xor(output, inputs[0], inputs[1]), component.getName());
					break;

				default:
					if (!componentTypesToIgnore.contains(typeWoNr)) {
						throw new Exception("Component type " + typeWoNr + " is unhandled.");
					}
					break;
				}
			}
		}

		// Add scenario to correctConstraints (TODO: Better make an example out of it?)
		List<IntegerVariable> trueVars = new ArrayList<IntegerVariable>();
		List<IntegerVariable> falseVars = new ArrayList<IntegerVariable>();

		Enumeration<DXCComponent> scenarioComps = faultyScenario.keys();
		while (scenarioComps.hasMoreElements()) {
			DXCComponent scenarioComp = scenarioComps.nextElement();
			if (faultyScenario.get(scenarioComp)) {
				trueVars.add(variables.get(scenarioComp.getName()));
			} else {
				falseVars.add(variables.get(scenarioComp.getName()));
			}
		}
		IntegerVariable[] trueVarsArr = new IntegerVariable[trueVars.size()];
		for (int i = 0; i < trueVars.size(); i++) {
			trueVarsArr[i] = trueVars.get(i);
		}
		IntegerVariable[]falseVarsArr = new IntegerVariable[falseVars.size()];
		for (int i = 0; i < falseVars.size(); i++) {
			falseVarsArr[i] = falseVars.get(i);
		}

		model.addCorrectFormula(Choco.and(trueVarsArr), "true");
		model.addCorrectFormula(Choco.nor(falseVarsArr), "false");

		// add an empty example, because it is needed for diagnosis
		Example<Constraint> ex = new Example<>();
		List<Example<Constraint>> posExamples = new ArrayList<>();
		posExamples.add(ex);
		model.setConsistentExamples(posExamples);

		return model;
	}

	private DXCComponent SearchConnections(DXCComponent searchConnection, List<DXCConnection> connections) throws Exception {
		DXCComponent connected = null;
		for (Iterator<DXCConnection> it = connections.iterator(); it.hasNext();) {
			DXCConnection connection = it.next();
			if (connection.getC1() == searchConnection) {
				if (connected == null) {
					connected = connection.getC2();
				} else {
					throw new Exception("Duplicate connection found!");
				}
			} else if (connection.getC2() == searchConnection) {
				if (connected == null) {
					connected = connection.getC1();
				} else {
					throw new Exception("Duplicate connection found!");
				}
			}
		}
		return connected;
	}

	private Constraint not(String output, String input) {
		return Choco.reifiedNot(variables.get(output), variables.get(input));
	}
	
	private Constraint eq(String output, String input) {
		return Choco.eq(variables.get(output), variables.get(input));
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
	
	private Constraint xor(String output, String input1, String input2) {
		IntegerVariable inputVariable1 = variables.get(input1);
		IntegerVariable inputVariable2 = variables.get(input2);
		return Choco.reifiedXor(variables.get(output), inputVariable1, inputVariable2);
	}
	
	private Constraint nand(String output, String... inputs) {
		IntegerVariable[] inputVariables = new IntegerVariable[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			inputVariables[i] = variables.get(inputs[i]);
		}
		return Choco.reifiedNand(variables.get(output), inputVariables);
	}
	
	private IntegerVariable makeBoolVar(String name) {
		IntegerVariable var = Choco.makeBooleanVar(name);
		variables.put(name, var);
		return var;
	}
}
