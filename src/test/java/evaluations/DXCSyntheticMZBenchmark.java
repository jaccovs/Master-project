package evaluations;

import choco.kernel.model.constraints.Constraint;
import evaluations.configuration.AbstractRunConfiguration;
import evaluations.configuration.AbstractScenario;
import evaluations.dxc.synthetic.minizinc.DXCDiagnosisMZModelGenerator;
import evaluations.dxc.synthetic.minizinc.MZDXCScenario;
import evaluations.dxc.synthetic.minizinc.MZDiagnosisEngine;
import evaluations.dxc.synthetic.minizinc.MZDiagnosisEngine.SearchType;
import evaluations.dxc.synthetic.minizinc.MZRunConfiguration;
import evaluations.dxc.synthetic.model.DXCComponent;
import evaluations.dxc.synthetic.model.DXCSystemDescription;
import evaluations.dxc.synthetic.tools.DXCScenarioParser;
import evaluations.dxc.synthetic.tools.DXCSyntheticXMLParser;
import org.exquisite.diagnosis.IDiagnosisEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Dictionary;
import java.util.List;

/**
 * Tests of DXC Synthetic Track using Gecode solver
 * For documentation of overridden methods, see AbstractEvaluation
 * @author Thomas
 *
 */
public class DXCSyntheticMZBenchmark extends AbstractEvaluation<Constraint> {

	// ----------------------------------------------------
	// Directories
	static String inputFileDirectory = "experiments/DXCSynthetic/";
	static String logFileDirectory = "logs/DXCSyntheticMZ/";
	// Number of runs
	static int nbInitRuns = 0;
	static int nbTestRuns = 1;
	static MZRunConfiguration[] runConfigurations = new MZRunConfiguration[] {
		new MZRunConfiguration(1),
//		new MZRunConfiguration(2),
//		new MZRunConfiguration(4),
	};
	static MZDXCScenario[] scenarios = new MZDXCScenario[] {
		// Settings for finding 1 diagnosis (All engines except level-wise)
		// maxDiagSize = -1
		new MZDXCScenario("74182.xml", "74182/74182.%03d.scn", 0, 0, SearchType.OneMinCardinality),
//		new MZDXCScenario("74L85.xml", "74L85/74L85.%03d.scn", 0, 19, SearchType.OneMinCardinality),
//		new MZDXCScenario("74283.xml", "74283/74283.%03d.scn", 0, 19, SearchType.OneMinCardinality),
//		new MZDXCScenario("74181.xml", "74181/74181.%03d.scn", 0, 19, SearchType.OneMinCardinality),
//		new MZDXCScenario("c432.xml", "c432/c432.%03d.scn", 0, 19, SearchType.OneMinCardinality),

		// Settings for finding all diagnoses (No Heuristic / Hybrid engines!)
		// maxDiagSize = 6
//		new MZDXCScenario("74182.xml", "74182/74182.%03d.scn", 0, 19, SearchType.FindAll, 6),
//		new MZDXCScenario("74L85.xml", "74L85/74L85.%03d.scn", 0, 19, SearchType.FindAll, 6),
//		new MZDXCScenario("74283.xml", "74283/74283.%03d.scn", 0, 19, SearchType.FindAll, 6),
//		new MZDXCScenario("74181.xml", "74181/74181.%03d.scn", 0, 19, SearchType.FindAll, 6),
//		new MZDXCScenario("c432.xml", "c432/c432.%03d.scn", 0, 19, SearchType.FindAll, 6),
		// maxDiagSize = faultSize
//		new MZDXCScenario("74182.xml", "74182/74182.%03d.scn", 0, 19, SearchType.FindAll, -2),
//		new MZDXCScenario("74L85.xml", "74L85/74L85.%03d.scn", 0, 19, SearchType.FindAll, -2),
//		new MZDXCScenario("74283.xml", "74283/74283.%03d.scn", 0, 19, SearchType.FindAll, -2),
//		new MZDXCScenario("74181.xml", "74181/74181.%03d.scn", 0, 19, SearchType.FindAll, -2),
//		new MZDXCScenario("c432.xml", "c432/c432.%03d.scn", 0, 19, SearchType.FindAll, -2),
	};
	// ----------------------------------------------------

	public static void main(String[] args) {
		DXCSyntheticMZBenchmark dxcSyntheticMZBenchmark = new DXCSyntheticMZBenchmark();
		dxcSyntheticMZBenchmark.runTests(nbInitRuns, nbTestRuns, runConfigurations, scenarios);
	}

	@Override
	public String getEvaluationName() {
		return "DXCSyntheticMZ";
	}

	// Standard scenario settings
//		static int searchDepth = -1;
//		static int maxDiags = -1;
	
	@Override
	public String getResultPath() {
		return logFileDirectory;
	}

	@Override
	public String getConstraintOrderPath() {
		return inputFileDirectory;
	}

	@Override
	protected boolean shouldShuffleConstraints() {
		return false;
	}

	@Override
	public IDiagnosisEngine prepareRun(
			AbstractRunConfiguration abstractRunConfiguration,
			AbstractScenario abstractScenario, int subScenario, int iteration) {

		MZRunConfiguration runConfiguration = (MZRunConfiguration)abstractRunConfiguration;
		MZDXCScenario scenario = (MZDXCScenario)abstractScenario;

		DXCSyntheticXMLParser parser = new DXCSyntheticXMLParser();

		String xmlFilePath = inputFileDirectory + scenario.inputFileName;
		String scnFilePath = inputFileDirectory + String.format(scenario.ScenarioFile, subScenario);

//        System.out.println("Trying to load xml file: " + xmlFilePath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(xmlFilePath)));
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            br.close();
            parser.parse(sb.toString());

            DXCSystemDescription sd = parser.getSystemDescription();

//            System.out.println("System name: " + sd.getSystems().get(0).getSystemName());

//            System.out.println("FINISH");

            DXCScenarioParser scnParser = new DXCScenarioParser();

//            System.out.println("Trying to load scn file: " + scnFilePath);

            br = new BufferedReader(new FileReader(new File(scnFilePath)));
            sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line.trim() + "\n");
            }
            br.close();
            scnParser.parse(sb.toString(), sd.getSystems().get(0));

            Dictionary<DXCComponent, Boolean> faultyState = scnParser.getScenario().getFaultyState();

//            System.out.println("Scenario size: " + faultyState.size());

//            System.out.println("FINISH");


            // Now create the diagnosis model
            DXCDiagnosisMZModelGenerator dmg = new DXCDiagnosisMZModelGenerator();
            List<String> model = dmg.createDiagnosisModel(sd.getSystems().get(0), faultyState);

			MZDiagnosisEngine engine = new MZDiagnosisEngine(model, dmg.getAbnormalCounter(), scenario.searchType,
					runConfiguration.threads);

			// With a value of -2 the maxDiagSize is set to the size of the actual error of the scenario
			if (scenario.searchDepth == -2) {
    			engine.setDiagnosesMaxCard(scnParser.getScenario().getFaultyComponents().size());
    		}
    		else {
    			engine.setDiagnosesMaxCard(scenario.searchDepth);
    		}

            return engine;
        }
        catch (IOException e) {
        	addError(e.getMessage(), runConfiguration, subScenario, iteration);
        	return null;
        }
	}
	
	@Override
	protected void shuffleConstraints(IDiagnosisEngine engine,
			List<List<Integer>> constraintOrders, int iteration) {
		// TODO Auto-generated method stub
//		super.shuffleConstraints(engine, constraintOrders, iteration);
	}

}
