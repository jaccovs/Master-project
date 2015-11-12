package evaluations.tools;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.diagnosis.models.Diagnosis;

import choco.kernel.model.constraints.Constraint;


public class DiagnosisCheck {

	/**
	 * Check for non minimal diagnoses
	 * @param diagnoses the diagnoses to check
	 * @return a list of non minimal diagnoses, if some exist
	 */
	public List<Diagnosis> checkForNonMinimalDiagnoses(List<Diagnosis> diagnoses)
	{
		List<Diagnosis> nonMinimal = new ArrayList<Diagnosis>();

		List<Diagnosis> copyDiagnoses = new ArrayList<Diagnosis>(diagnoses);

		for (Diagnosis diag: diagnoses)
		{
			copyDiagnoses.remove(diag);
			Diagnosis notMinimalDiagnosis = isConstraintListContainedInDiagnoses(diag.getElements(), copyDiagnoses);
			if (notMinimalDiagnosis != null)
				nonMinimal.add(notMinimalDiagnosis);
			copyDiagnoses.add(diag);
		}

		return nonMinimal;
	}

	/**
	 * A method that checks if a given constraint list is part of a list of diagnoses
	 * (pointer comparison of elements)
	 * @param constraints a list of constraints to check
	 * @param diagnoses a list of diagnoses 
	 * @return true, if the constraint list is contained in the diagnoses
	 */
	public static Diagnosis isConstraintListContainedInDiagnoses(List<Constraint> constraints, List<Diagnosis> diagnoses)
	{
		boolean result = false;
		for (Diagnosis diag : diagnoses)  {
			if (diag.getElements().size() >= constraints.size()) {
				if (diag.getElements().containsAll(constraints)) {
					return diag;
				}
			}
		}
		return null;
	}

	// Check for differences between two sets of diagnoses

	private List<Diagnosis> lastDiagnoses = null;
	private List<Diagnosis> removedDiagnoses = new ArrayList<Diagnosis>();
	private List<Diagnosis> addedDiagnoses = new ArrayList<Diagnosis>();

	/**
	 * Sets the list of last diagnoses to compare to new diagnoses with function calculateDifferences()
	 * @param diagnoses the list of old diagnoses
	 */
	public void setLastDiagnoses(List<Diagnosis> diagnoses)
	{
		lastDiagnoses = diagnoses;
	}

	/**
	 * Get the list of removed diagnoses. setLastDiagnoses() and calculateDifferences() have to be called first.
	 * @return removed diagnoses
	 */
	public List<Diagnosis> getRemovedDiagnoses()
	{
		return removedDiagnoses;
	}

	/**
	 * Get the list of added diagnoses. setLastDiagnoses() and calculateDifferences() have to be called first.
	 * @return removed diagnoses
	 */
	public List<Diagnosis> getAddedDiagnoses()
	{
		return addedDiagnoses;
	}

	/**
	 * Calculates the differences between the last diagnoses, set with function setLastDiagnoses(), and the new diagnoses given with this function call.
	 * Use getRemovedDiagnoses() and getAddedDiagnoses to get the lists of differences.
	 * @param newDiagnoses the new diagnoses to compare with the old diagnoses
	 */
	public void calculateDifferences(List<Diagnosis> newDiagnoses)
	{
		removedDiagnoses.clear();
		addedDiagnoses.clear();

		if (lastDiagnoses != null && newDiagnoses != null)
		{
			List<Diagnosis> copyDiagnoses = new ArrayList<Diagnosis>(newDiagnoses);
			for (Diagnosis diag: lastDiagnoses)
			{
				Diagnosis sameDiag = containsEqualDiagnosis(copyDiagnoses, diag);
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

	private Diagnosis containsEqualDiagnosis(List<Diagnosis> diagnoses, Diagnosis diagnosisToContain)
	{
		for (Diagnosis diag: diagnoses)
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
	public void printNonMinimalDiagnoses(List<Diagnosis> diagnoses) {
		List<Diagnosis> nonMinimal = checkForNonMinimalDiagnoses(diagnoses);
		
		for (Diagnosis diag: nonMinimal)
		{
			System.err.println("Not minimal: " + diag.toString());
		}
	}

	/**
	 * Calculates the differences between given diagnoses and diagnoses of last function call and prints them
	 * @param diagnoses the list of new diagnoses
	 */
	public void printDifferentDiagnoses(List<Diagnosis> diagnoses) {
		calculateDifferences(diagnoses);
		
		for (Diagnosis diag: getAddedDiagnoses())
		{
			System.err.println("Added: " + diag.toString());
		}
		
		for (Diagnosis diag: getRemovedDiagnoses())
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
