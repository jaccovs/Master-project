package evaluations.tools;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.diagnosis.models.Diagnosis;

import java.util.ArrayList;
import java.util.List;


public class DiagnosisCheck {

	private List<Diagnosis<Constraint>> lastDiagnoses = null;
	private List<Diagnosis<Constraint>> removedDiagnoses = new ArrayList<Diagnosis<Constraint>>();

	// Check for differences between two sets of diagnoses
	private List<Diagnosis<Constraint>> addedDiagnoses = new ArrayList<Diagnosis<Constraint>>();

	/**
	 * A method that checks if a given constraint list is part of a list of diagnoses
	 * (pointer comparison of elements)
	 * @param constraints a list of constraints to check
	 * @param diagnoses a list of diagnoses
	 * @return true, if the constraint list is contained in the diagnoses
	 */
	public static Diagnosis<Constraint> isConstraintListContainedInDiagnoses(List<Constraint> constraints,
																			 List<Diagnosis<Constraint>> diagnoses)
	{
		boolean result = false;
		for (Diagnosis<Constraint> diag : diagnoses) {
			if (diag.getElements().size() >= constraints.size()) {
				if (diag.getElements().containsAll(constraints)) {
					return diag;
				}
			}
		}
		return null;
	}

	/**
	 * Check for non minimal diagnoses
	 *
	 * @param diagnoses the diagnoses to check
	 * @return a list of non minimal diagnoses, if some exist
	 */
	public List<Diagnosis<Constraint>> checkForNonMinimalDiagnoses(List<Diagnosis<Constraint>> diagnoses) {
		List<Diagnosis<Constraint>> nonMinimal = new ArrayList<Diagnosis<Constraint>>();

		List<Diagnosis<Constraint>> copyDiagnoses = new ArrayList<Diagnosis<Constraint>>(diagnoses);

		for (Diagnosis<Constraint> diag : diagnoses) {
			copyDiagnoses.remove(diag);
			Diagnosis<Constraint> notMinimalDiagnosis = isConstraintListContainedInDiagnoses(diag.getElements(),
					copyDiagnoses);
			if (notMinimalDiagnosis != null)
				nonMinimal.add(notMinimalDiagnosis);
			copyDiagnoses.add(diag);
		}

		return nonMinimal;
	}

	/**
	 * Sets the list of last diagnoses to compare to new diagnoses with function calculateDifferences()
	 * @param diagnoses the list of old diagnoses
	 */
	public void setLastDiagnoses(List<Diagnosis<Constraint>> diagnoses)
	{
		lastDiagnoses = diagnoses;
	}

	/**
	 * Get the list of removed diagnoses. setLastDiagnoses() and calculateDifferences() have to be called first.
	 * @return removed diagnoses
	 */
	public List<Diagnosis<Constraint>> getRemovedDiagnoses()
	{
		return removedDiagnoses;
	}

	/**
	 * Get the list of added diagnoses. setLastDiagnoses() and calculateDifferences() have to be called first.
	 * @return removed diagnoses
	 */
	public List<Diagnosis<Constraint>> getAddedDiagnoses()
	{
		return addedDiagnoses;
	}

	/**
	 * Calculates the differences between the last diagnoses, set with function setLastDiagnoses(), and the new diagnoses given with this function call.
	 * Use getRemovedDiagnoses() and getAddedDiagnoses to get the lists of differences.
	 * @param newDiagnoses the new diagnoses to compare with the old diagnoses
	 */
	public void calculateDifferences(List<Diagnosis<Constraint>> newDiagnoses)
	{
		removedDiagnoses.clear();
		addedDiagnoses.clear();

		if (lastDiagnoses != null && newDiagnoses != null) {
			List<Diagnosis<Constraint>> copyDiagnoses = new ArrayList<Diagnosis<Constraint>>(newDiagnoses);
			for (Diagnosis<Constraint> diag : lastDiagnoses) {
				Diagnosis<Constraint> sameDiag = containsEqualDiagnosis(copyDiagnoses, diag);
				if (sameDiag != null)
				{
					copyDiagnoses.remove(sameDiag);
				}
				else
				{
					removedDiagnoses.add(diag);
				}
			}
			addedDiagnoses.addAll(copyDiagnoses);
		}
	}

	private Diagnosis<Constraint> containsEqualDiagnosis(List<Diagnosis<Constraint>> diagnoses,
														 Diagnosis<Constraint> diagnosisToContain) {
		for (Diagnosis<Constraint> diag: diagnoses)
		{
			boolean equal = true;
			if (diag.getElements().size() != diagnosisToContain.getElements().size())
			{
				equal = false;
			}
			else
			{
				if (!diag.toString().equals(diagnosisToContain.toString()))
				{
					equal = false;
				}
//				for (Constraint constraint: diag.getElements())
//				{
//					if (!constraintsContaintConstraint(diagnosisToContain.getElements(), constraint))
//					{
//						equal = false;
//						break;
//					}
//				}
			}
			if (equal)
			{
				return diag;
			}
		}
		return null;
	}

	/**
	 * Checks for non minimal diagnoses and prints them
	 * @param diagnoses the diagnoses to check
	 */
	public void printNonMinimalDiagnoses(List<Diagnosis<Constraint>> diagnoses) {
		List<Diagnosis<Constraint>> nonMinimal = checkForNonMinimalDiagnoses(diagnoses);

		for (Diagnosis<Constraint> diag: nonMinimal)
		{
			System.err.println("Not minimal: " + diag.toString());
		}
	}

	/**
	 * Calculates the differences between given diagnoses and diagnoses of last function call and prints them
	 * @param diagnoses the list of new diagnoses
	 */
	public void printDifferentDiagnoses(List<Diagnosis<Constraint>> diagnoses) {
		calculateDifferences(diagnoses);

		for (Diagnosis<Constraint> diag : getAddedDiagnoses())
		{
			System.err.println("Added: " + diag.toString());
		}

		for (Diagnosis<Constraint> diag : getRemovedDiagnoses())
		{
			System.err.println("Removed: " + diag.toString());
		}
		
		setLastDiagnoses(diagnoses);
	}
	
//	private boolean constraintsContaintConstraint(List<Constraint> constraints, Constraint constraint)
//	{
//		boolean found = false;
//		for (Constraint c2: constraints)
//		{
//			if (constraintsEqual(constraint, c2))
//			{
//				found = true;
//				break;
//			}
//		}
//		return found;
//	}
//
//	private boolean constraintsEqual(Constraint c1, Constraint c2) {
//		return c1.pretty().equals(c2.pretty());
//	}

}
