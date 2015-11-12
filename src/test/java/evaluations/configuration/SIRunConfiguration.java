package evaluations.configuration;


/**
 * Special run configuration for SpreadsheetsIndividual.
 * @author Thomas
 */
public class SIRunConfiguration extends StdRunConfiguration {
	public static enum PruningMode {on, off};
	
	public PruningMode pruningMode;
	
	public SIRunConfiguration(
			ExecutionMode executionMode,
			int threads,
			PruningMode pruningMode,
			boolean choco3) {
		super(executionMode, threads, choco3);
		this.pruningMode = pruningMode;
	}
	
	@Override
	public String getName() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.getName());
		if (pruningMode == PruningMode.on) {
			sb.append("_PRUNING_ON");
		}
		else {
			sb.append("_PRUNING_OFF");
		}
		return sb.toString();
	}

}
