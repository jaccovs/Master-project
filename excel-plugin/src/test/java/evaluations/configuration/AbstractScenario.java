package evaluations.configuration;

/**
 * Abstract class for a scenario.
 * A scenario should contain all settings, that will not get compared in an evaluation, like different files or search depths etc.
 * @author Thomas
 *
 */
public abstract class AbstractScenario {
	
	/**
	 * Should return a unique name for this scenario.
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Should return a name that is the same for all scenarios which should use the same constraint orderings.
	 * Things like maxDiags and searchDepth should be omitted.
	 * @return
	 */
	public abstract String getConstraintOrderName();
	
	/**
	 * Returns the start of sub scenarios in this scenario.
	 * Sub scenarios can be used to average the run times over different scenarios.
	 * @return
	 */
	public int getSubScenarioStart() {
		return 0;
	}
	
	/**
	 * Returns the start of sub scenarios in this scenario.
	 * Sub scenarios can be used to average the run times over different scenarios.
	 * @return
	 */
	public int getSubScenarioEnd() {
		return 0;
	}

}
