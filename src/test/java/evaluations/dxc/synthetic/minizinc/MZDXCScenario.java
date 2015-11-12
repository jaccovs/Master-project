package evaluations.dxc.synthetic.minizinc;

import evaluations.configuration.DXCScenario;
import evaluations.dxc.synthetic.minizinc.MZDiagnosisEngine.SearchType;

public class MZDXCScenario extends DXCScenario {
	
	public SearchType searchType;
	
	public MZDXCScenario(String systemDescriptionFile, String scenarioFile,
			int scenarioStart, int scenarioEnd) {
		super(systemDescriptionFile, scenarioFile, scenarioStart, scenarioEnd);
	}
	
	public MZDXCScenario(String systemDescriptionFile, String scenarioFile,
			int scenarioStart, int scenarioEnd, SearchType searchType) {
		super(systemDescriptionFile, scenarioFile, scenarioStart, scenarioEnd);
		this.searchType = searchType;
	}
	
	public MZDXCScenario(String systemDescriptionFile, String scenarioFile,
			int scenarioStart, int scenarioEnd, SearchType searchType, int searchDepth) {
		super(systemDescriptionFile, scenarioFile, scenarioStart, scenarioEnd);
		this.searchType = searchType;
		this.searchDepth = searchDepth;
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
		result.append(waitTime);
		result.append("_");
		result.append(searchType);
		return result.toString();
	}

}
