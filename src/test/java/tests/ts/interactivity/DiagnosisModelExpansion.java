package tests.ts.interactivity;

import java.util.ArrayList;
import java.util.List;

import choco.kernel.model.constraints.Constraint;

/**
 * Class to store changes that should be done to the diagnosis model because of the simulated user interaction.
 * 
 * @author Schmitz
 *
 */
public class DiagnosisModelExpansion {
	/**
	 * List of constraints that should be removed from the possibly faulty constraints
	 */
	private List<Constraint> possiblyFaultyConstraintsToRemove = new ArrayList<Constraint>();

	/**
	 * List of constraints that should be added to the correct constraints
	 */
	private List<Constraint> correctConstraintsToAdd = new ArrayList<Constraint>();

	/**
	 * List of constraints that should be added to the certainly faulty constraints
	 */
	private List<Constraint> certainlyFaultyConstraintsToAdd = new ArrayList<Constraint>();

	public List<Constraint> getPossiblyFaultyConstraintsToRemove() {
		return possiblyFaultyConstraintsToRemove;
	}

	public List<Constraint> getCorrectConstraintsToAdd() {
		return correctConstraintsToAdd;
	}

	public List<Constraint> getCertainlyFaultyConstraintsToAdd() {
		return certainlyFaultyConstraintsToAdd;
	}
}
