package evaluations.configuration;

/**
 * Abstract class for a run configuration.
 * A run configuration should contain all settings, that may be compared for different runs like thread count, parallelization type etc.
 * @author Thomas
 *
 */
public abstract class AbstractRunConfiguration {
	public int threads;
	
	public AbstractRunConfiguration(int threads) {
		this.threads = threads;
	}
	
	/**
	 * Should return a unique name for this run configuration.
	 * @return
	 */
	public abstract String getName();

}
