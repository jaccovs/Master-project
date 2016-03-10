package evaluations.configuration;

/**
 * A standard scenario class.
 * @author Thomas
 */
public class StdScenario extends AbstractScenario {

	public String inputFileName;
	public int searchDepth = -1;
	public int maxDiags = -1;
	public int waitTime = -1;
	
	public StdScenario(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	
	public StdScenario(String inputFileName, int searchDepth, int maxDiags) {
		this(inputFileName);
		this.searchDepth = searchDepth;
		this.maxDiags = maxDiags;
	}
	
	public StdScenario(String inputFileName, int searchDepth, int maxDiags, int waitTime) {
		this(inputFileName, searchDepth, maxDiags);
		this.waitTime = waitTime;
	}
	
	@Override
	public String getName() {
		StringBuilder result = new StringBuilder();
		result.append(inputFileName);
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
