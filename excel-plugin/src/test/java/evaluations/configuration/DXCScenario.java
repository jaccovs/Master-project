package evaluations.configuration;

public class DXCScenario extends StdScenario {
	public String ScenarioFile;
	public int ScenarioStart = 0;
	public int ScenarioEnd = 19;
	
	public DXCScenario(String systemDescriptionFile, String scenarioFile) {
		super(systemDescriptionFile);
		this.ScenarioFile = scenarioFile;
	}
	
	public DXCScenario(String systemDescriptionFile, String scenarioFile, int scenarioStart, int scenarioEnd) {
		this(systemDescriptionFile, scenarioFile);
		this.ScenarioStart = scenarioStart;
		this.ScenarioEnd = scenarioEnd;
	}
	
	public DXCScenario(String systemDescriptionFile, String scenarioFile, int scenarioStart, int scenarioEnd, int searchDepth, int maxDiags) {
		this(systemDescriptionFile, scenarioFile, scenarioStart, scenarioEnd);
		this.searchDepth = searchDepth;
		this.maxDiags = maxDiags;
	}
	
	@Override
	public int getSubScenarioStart() {
		return ScenarioStart;
	}
	
	@Override
	public int getSubScenarioEnd() {
		return ScenarioEnd;
	}
	
	@Override
	public String getName() {
		StringBuilder result = new StringBuilder();
		result.append(inputFileName);
		result.append("_");
		result.append(ScenarioStart);
		result.append("_");
		result.append(ScenarioEnd);
		result.append("_");
		result.append(searchDepth);
		result.append("_");
		result.append(maxDiags);
		result.append("_");
		result.append(waitTime);
		return result.toString();
	}
	
	@Override
	public String getConstraintOrderName() {
		StringBuilder result = new StringBuilder();
		result.append(inputFileName);
		return result.toString();
	}
}
