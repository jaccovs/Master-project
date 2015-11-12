package evaluations.dxc.synthetic;

/**
 * Holds the data of one TestScenario used in DXCSyntheticBenchmark
 * @author Thomas
 *
 */
public class DXCTestScenario {
	public String SystemDescriptionFile;
	public String ScenarioFile;
	public int MaxDiagSize = -1;
	public int MaxDiagnoses = -1;
	public int ScenarioStart = 0;
	public int ScenarioEnd = 0;
	
	public DXCTestScenario(String systemDescriptionFile, String scenarioFile) {
		this.SystemDescriptionFile = systemDescriptionFile;
		this.ScenarioFile = scenarioFile;
	}
	
	public DXCTestScenario(String systemDescriptionFile, String scenarioFile, int scenarioStart, int scenarioEnd) {
		this.SystemDescriptionFile = systemDescriptionFile;
		this.ScenarioFile = scenarioFile;
		this.ScenarioStart = scenarioStart;
		this.ScenarioEnd = scenarioEnd;
	}
	
	/**
	 * 
	 * @param systemDescriptionFile
	 * @param scenarioFile
	 * @param scenarioStart
	 * @param scenarioEnd
	 * @param maxDiagSize  With a value of -2 the maxDiagSize is set to the size of the actual error of the scenario
	 * @param maxDiagnoses
	 */
	public DXCTestScenario(String systemDescriptionFile, String scenarioFile, int scenarioStart, int scenarioEnd, int maxDiagSize, int maxDiagnoses) {
		this.SystemDescriptionFile = systemDescriptionFile;
		this.ScenarioFile = scenarioFile;
		this.ScenarioStart = scenarioStart;
		this.ScenarioEnd = scenarioEnd;
		this.MaxDiagSize = maxDiagSize;
		this.MaxDiagnoses = maxDiagnoses;
	}
}
