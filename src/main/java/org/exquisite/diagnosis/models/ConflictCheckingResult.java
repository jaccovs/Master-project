package org.exquisite.diagnosis.models;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.diagnosis.engines.common.SharedCollection;

import choco.kernel.model.constraints.Constraint;

/**
 * An  class used to return the results of a call at a node to check
 * if all examples are consistent
 * @author Dietmar
 *
 */
public class ConflictCheckingResult {
	// A conflict, if there were inconsistent examples / test cases
	// field is null if all examples were consistent.
	// if there is more than one conflict we observe, remember it.
	public SharedCollection<List<Constraint>> conflicts = new SharedCollection<List<Constraint>>();
	// A list of failing test cases
	public List<Example> failedExamples = new ArrayList<Example>();
	
	/**
	 * Adds an example to the list of failed ones
	 */
	public void addFailedExample(Example e) {
		failedExamples.add(e);
	}
	
	/**
	 * Indicates if we have found a conflict
	 * @return true, if the conflict list is not empty
	 */
	public boolean conflictFound() {
		return this.conflicts.size() > 0;
	}
	
	/**
	 * Adds the conflict if it was not already there
	 * @param conflict the conflict to add
	 */
	public void addConflict(List<Constraint> conflict) {
		if (this.conflicts.size() == 0) {
			this.conflicts.add(conflict);
			return;
		}
		List<List<Constraint>> existingConflicts = new ArrayList<List<Constraint>>(this.conflicts.getCollection());
		for (List<Constraint> existingConflict : existingConflicts) {
			if (areListsEqual(conflict, existingConflict)) {
				System.out.println("Already have this conflict");
				return;
			}
		}
		this.conflicts.add(conflict);
	}
	
	/**
	 * Returns true if both lists are equal
	 * @param list1
	 * @param list2
	 * @return true if the list contains the same elements
	 */
	private boolean areListsEqual(List<Constraint> list1, List<Constraint> list2) {
		if (list1.size() == list2.size()) {
			return false;
		}
		List<Constraint> copiedList1 = new ArrayList<Constraint>(list1);
		copiedList1.removeAll(list2);
		if (copiedList1.size() == 0) {
			return true;
		}
		return false;
		
	}
	
	// a string representation of the result
	public String toString() {
		String result = "";
		if (conflictFound()) {
			result += "Conflict check at node: Found conflicts: " + this.conflicts.size();
		}
		else {
			result += "Conflict check at node: all examples passed";
		}
		return result;
	}
}