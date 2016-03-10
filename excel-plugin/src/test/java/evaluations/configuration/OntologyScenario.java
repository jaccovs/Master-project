package evaluations.configuration;

public class OntologyScenario extends StdScenario {
	boolean moduleExtraction = true;
	boolean reduceToUnsatisfiability = true;
	boolean assertionsCorrect = true;
	
	public boolean isModuleExtraction() {
		return moduleExtraction;
	}
	
	public boolean isReduceToUnsatisfiability() {
		return reduceToUnsatisfiability;
	}
	
	public boolean isAssertionsCorrect() {
		return assertionsCorrect;
	}
	
	public OntologyScenario(String inputFileName, int searchDepth, int maxDiags) {
		super(inputFileName, searchDepth, maxDiags);
	}
	
	public OntologyScenario(String inputFileName, int searchDepth, int maxDiags, boolean moduleExtraction, boolean reduceToUnsatisfiability, boolean assertionsCorrect) {
		super(inputFileName, searchDepth, maxDiags);
		this.moduleExtraction = moduleExtraction;
		this.reduceToUnsatisfiability = reduceToUnsatisfiability;
		this.assertionsCorrect = assertionsCorrect;
	}
	
	@Override
	public String getName() {
		StringBuilder result = new StringBuilder();
		result.append(inputFileName);
		if (moduleExtraction) {
			result.append("_ModExt");
		}
		if (reduceToUnsatisfiability) {
			result.append("_RedUnsat");
		}
		if (assertionsCorrect) {
			result.append("_AssertCor");
		}
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
		if (moduleExtraction) {
			result.append("_ModExt");
		}
		if (reduceToUnsatisfiability) {
			result.append("_RedUnsat");
		}
		if (assertionsCorrect) {
			result.append("_AssertCor");
		}
		return result.toString();
	}

}
