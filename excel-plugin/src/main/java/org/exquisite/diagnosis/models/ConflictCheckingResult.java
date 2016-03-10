package org.exquisite.diagnosis.models;

import org.exquisite.diagnosis.engines.common.SharedCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * An  class used to return the results of a call at a node to check
 * if all examples are consistent
 *
 * @author Dietmar
 */
public class ConflictCheckingResult<T> {
    // A nodeLabel, if there were inconsistent examples / test cases
    // field is null if all examples were consistent.
    // if there is more than one nodeLabel we observe, remember it.
    public SharedCollection<List<T>> conflicts = new SharedCollection<>();
    // A list of failing test cases
    public List<Example<T>> failedExamples = new ArrayList<>();

    /**
     * Adds an example to the list of failed ones
     */
    public void addFailedExample(Example e) {
        failedExamples.add(e);
    }

    /**
     * Indicates if we have found a nodeLabel
     *
     * @return true, if the nodeLabel list is not empty
     */
    public boolean conflictFound() {
        return this.conflicts.size() > 0;
    }

    /**
     * Adds the nodeLabel if it was not already there
     *
     * @param conflict the nodeLabel to add
     */
    public void addConflict(List<T> conflict) {
        if (this.conflicts.size() == 0) {
            this.conflicts.add(conflict);
            return;
        }
        List<List<T>> existingConflicts = new ArrayList<List<T>>(this.conflicts.getCollection());
        for (List<T> existingConflict : existingConflicts) {
            if (areListsEqual(conflict, existingConflict)) {
                System.out.println("Already have this nodeLabel");
                return;
            }
        }
        this.conflicts.add(conflict);
    }

    /**
     * Returns true if both lists are equal
     *
     * @param list1
     * @param list2
     * @return true if the list contains the same elements
     */
    private boolean areListsEqual(List<T> list1, List<T> list2) {
        if (list1.size() == list2.size()) {
            return false;
        }
        List<T> copiedList1 = new ArrayList<T>(list1);
        copiedList1.removeAll(list2);
        return copiedList1.size() == 0;

    }

    // a string representation of the result
    public String toString() {
        String result = "";
        if (conflictFound()) {
            result += "Conflict check at node: Found conflicts: " + this.conflicts.size();
        } else {
            result += "Conflict check at node: all examples passed";
        }
        return result;
    }
}