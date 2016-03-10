package tests.ts.interactivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store changes that should be done to the diagnosis model because of the simulated user interaction.
 * 
 * @author Schmitz
 *
 */
public class DiagnosisModelExpansion<T> {
	/**
	 * List of constraints that should be removed from the possibly faulty constraints
	 */
	private List<T> possiblyFaultyConstraintsToRemove = new ArrayList<T>();

	/**
	 * List of constraints that should be added to the correct constraints
	 */
	private List<T> correctConstraintsToAdd = new ArrayList<T>();

	/**
	 * List of constraints that should be added to the certainly faulty constraints
	 */
	private List<T> certainlyFaultyConstraintsToAdd = new ArrayList<T>();

	public List<T> getPossiblyFaultyConstraintsToRemove() {
		return possiblyFaultyConstraintsToRemove;
	}

	public List<T> getCorrectConstraintsToAdd() {
		return correctConstraintsToAdd;
	}

	public List<T> getCertainlyFaultyConstraintsToAdd() {
		return certainlyFaultyConstraintsToAdd;
	}
}
