package org.exquisite.diagnosis.quickxplain.mergexplain;

import org.exquisite.datamodel.DiagnosisModel;
import org.exquisite.diagnosis.engines.common.SharedCollection;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;

import java.util.ArrayList;
import java.util.List;

import static org.exquisite.core.measurements.MeasurementManager.*;

/**
 * Implementation of MergeXplain
 *
 * @author Thomas
 */
public class MergeXplain<T> extends ConstraintsQuickXPlain<T> {

    public static ConflictSearchModes ConflictSearchMode = ConflictSearchModes.Least;

    /**
     * set currentDiagnosisModel as a copy of SessionData tests.diagnosis model.
     *
     * @param sessionData
     */
    public MergeXplain(DiagnosisModel<T> sessionData) {
        super(sessionData);
    }

    /**
     * Main method that is called to find conflicts
     */
    @Override
    public List<List<T>> findConflicts() throws DomainSizeException {
//		System.out.println("findConflicts()");

        List<List<T>> result = new ArrayList<>();

//		if (checkConsistency() == true) {
//			Debug.msg("checkConsistency = true.");
//			return result;
//		} else {
        MergeXplainResult<T> mxpResult = mergeXplain(this.currentDiagnosisModel.getPossiblyFaultyStatements(),
                this.currentDiagnosisModel.getCorrectStatements());

        result.addAll(mxpResult.Conflicts);
//			System.out.println("Found " + result.size() + " nodeLabel(s).");
//		}

        // Release this guy
        if (this.constraintListener != null) {
//		    System.out.println("Done with QX - release the listener");
            this.constraintListener.release();
        }

        incrementCounter(COUNTER_SEARCH_CONFLICTS);

        return result;
    }

    /**
     * Variant of findConflicts() that notifies parent thread, when the first nodeLabel was found
     */
    @Override
    public void findConflictsParallel(ConflictCheckingResult<T> result,
                                      SharedCollection<List<T>> knownConflicts)
            throws DomainSizeException {
//		System.out.println("findConflictsParallel()");



//		if (checkConsistency() == true) {
//			Debug.msg("checkConsistency = true.");
//			synchronized (parent) {
//				parent.notify();
//			}
//			return;
//		} else {
        MergeXplainResult mxpResult = mergeXplainParallel(this.currentDiagnosisModel.getPossiblyFaultyStatements(),
                this.currentDiagnosisModel.getCorrectStatements(), result,
                knownConflicts);


//			result.addAll(mxpResult.Conflicts);
//			System.out.println("Found " + result.size() + " nodeLabel(s).");
//		}

        // Release this guy
        if (this.constraintListener != null) {
//		    System.out.println("Done with QX - release the listener");
            this.constraintListener.release();
        }

        incrementCounter(COUNTER_SEARCH_CONFLICTS);


//		Debug.syncMsg("MXP: Finished.");
    }

    private MergeXplainResult<T> mergeXplain(
            List<T> possiblyFaultyStatements,
            List<T> correctStatements) throws DomainSizeException {

        MergeXplainResult<T> result = new MergeXplainResult<>();

        // If all statements are consistent together, we cannot find a nodeLabel here
        List<T> allStatements = new ArrayList<T>(possiblyFaultyStatements);
        allStatements.addAll(correctStatements);
        if (isConsistent(allStatements)) {
            result.ConflictFreeStatements.addAll(possiblyFaultyStatements);
            return result;
        }

        // If we have only one possibly faulty statement, it has to be a nodeLabel
        if (possiblyFaultyStatements.size() == 1) {
            result.Conflicts.add(new ArrayList<T>(possiblyFaultyStatements));
            return result;
        }

        // Split possibly faulty statements
        int split = split(possiblyFaultyStatements);
        List<T> s1 = new ArrayList<T>(possiblyFaultyStatements.subList(0, split));
        List<T> s2 = new ArrayList<T>(
                possiblyFaultyStatements.subList(split, possiblyFaultyStatements.size()));

        // Recursion
        MergeXplainResult<T> r1 = mergeXplain(s1, correctStatements);
        MergeXplainResult<T> r2 = mergeXplain(s2, correctStatements);

        // Add found conflicts
        result.Conflicts.addAll(r1.Conflicts);
        result.Conflicts.addAll(r2.Conflicts);


        incrementCounter(COUNTER_MXP_SPLITTING, result.Conflicts.size());

//		System.out.println("Found " + result.Conflicts.size() + " nodeLabel(s) with splitting technique.");

        // Search for conflicts in remaining constraints
        List<T> remainingConstraints = new ArrayList<T>(r1.ConflictFreeStatements);
        remainingConstraints.addAll(r2.ConflictFreeStatements);
        List<T> allTestStatements = new ArrayList<T>(remainingConstraints);
        allTestStatements.addAll(correctStatements);
        List<T> kb1 = new ArrayList<T>(r1.ConflictFreeStatements);
        List<T> kb2 = new ArrayList<T>(r2.ConflictFreeStatements);
        List<T> conflictToRemove = new ArrayList<T>();
        while (!isConsistent(allTestStatements)) {
            try {
                List<T> conflict = findSingleConflict(correctStatements, kb2, kb2, kb1);
                conflict.addAll(findSingleConflict(correctStatements, conflict, conflict, kb2));
                //kb1.removeAll(nodeLabel);

                result.Conflicts.add(conflict);

                switch (ConflictSearchMode) {
                    case Least:
                        kb1.removeAll(conflict);
                        kb2.removeAll(conflict);
                        remainingConstraints.removeAll(conflict);
                        allTestStatements.removeAll(conflict);
                        break;
                    case Some:
                        kb1.removeAll(conflict);
                        conflictToRemove.clear();
                        conflictToRemove.addAll(conflict);
                        conflictToRemove.removeAll(kb2);

                        remainingConstraints.removeAll(conflictToRemove);
                        allTestStatements.removeAll(conflictToRemove);
                        break;
                    case EnoughFor1Diag:
                        List<T> copy = new ArrayList<T>(conflict);
                        copy.retainAll(kb1);
                        kb1.remove(copy.get(0));
                        remainingConstraints.remove(copy.get(0));
                        allTestStatements.remove(copy.get(0));
                        break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return result;
            }
        }
        result.ConflictFreeStatements.addAll(remainingConstraints);
        incrementCounter(COUNTER_MXP_CONFLICTS,result.Conflicts.size());
        return result;
    }

    /**
     * Variant of mergeXplain() that notifies parent thread, when the first nodeLabel was found
     */
    private MergeXplainResult<T> mergeXplainParallel(
            List<T> possiblyFaultyStatements,
            List<T> correctStatements, ConflictCheckingResult<T> endResult,
            SharedCollection<List<T>> knownConflicts) throws DomainSizeException {

        MergeXplainResult<T> result = new MergeXplainResult<>();

        // If all statements are consistent together, we cannot find a nodeLabel here
        List<T> allStatements = new ArrayList<T>(possiblyFaultyStatements);
        allStatements.addAll(correctStatements);
        if (Thread.currentThread().isInterrupted()) {
//			Debug.syncMsg("MergeXplain stopped.");
            return result;
        }
        if (isConsistent(allStatements)) {
            result.ConflictFreeStatements.addAll(possiblyFaultyStatements);
            return result;
        }

        // If we have only one possibly faulty statement, it has to be a nodeLabel
        if (possiblyFaultyStatements.size() == 1) {
            List<T> conflict = new ArrayList<T>(possiblyFaultyStatements);
            result.Conflicts.add(conflict);
            endResult.addConflict(conflict);
//			if (endResult.conflicts.size() == 1) {
//				Debug.syncMsg("MXP: Found first nodeLabel.");
            synchronized (ConstraintsQuickXPlain.ContinuingSync) {
                knownConflicts.addItemListNoDups(conflict);
                ConstraintsQuickXPlain.ContinuingSync.notifyAll();
            }
//			} else {
//				knownConflicts.addItemListNoDups(nodeLabel);
//			}
            return result;
        }

        // Split possibly faulty statements
        int split = split(possiblyFaultyStatements);
        List<T> s1 = new ArrayList<T>(possiblyFaultyStatements.subList(0, split));
        List<T> s2 = new ArrayList<T>(
                possiblyFaultyStatements.subList(split, possiblyFaultyStatements.size()));

        // Recursion
        MergeXplainResult<T> r1 = mergeXplainParallel(s1, correctStatements, endResult, knownConflicts);
        MergeXplainResult<T> r2 = mergeXplainParallel(s2, correctStatements, endResult, knownConflicts);

        // Add found conflicts
        result.Conflicts.addAll(r1.Conflicts);
        result.Conflicts.addAll(r2.Conflicts);

        incrementCounter(COUNTER_MXP_SPLITTING, result.Conflicts.size());

//		System.out.println("Found " + result.Conflicts.size() + " nodeLabel(s) with splitting technique.");

        // Search for conflicts in remaining constraints
        List<T> remainingConstraints = new ArrayList<T>(r1.ConflictFreeStatements);
        remainingConstraints.addAll(r2.ConflictFreeStatements);
        List<T> allTestStatements = new ArrayList<T>(remainingConstraints);
        allTestStatements.addAll(correctStatements);
        List<T> kb1 = new ArrayList<T>(r1.ConflictFreeStatements);
        List<T> kb2 = new ArrayList<T>(r2.ConflictFreeStatements);
        List<T> conflictToRemove = new ArrayList<T>();
        while (!isConsistent(allTestStatements)) {
            if (Thread.currentThread().isInterrupted()) {
//				Debug.syncMsg("MergeXplain stopped.");
                return result;
            }
            try {
                List<T> conflict = findSingleConflict(correctStatements, kb2, kb2, kb1);
                conflict.addAll(findSingleConflict(correctStatements, conflict, conflict, kb2));

                //kb1.removeAll(nodeLabel);

                result.Conflicts.add(conflict);
                endResult.addConflict(conflict);
//				if (endResult.conflicts.size() == 1) {
                //				Debug.syncMsg("MXP: Found First nodeLabel.");
                synchronized (ConstraintsQuickXPlain.ContinuingSync) {
                    knownConflicts.addItemListNoDups(conflict);
                    ConstraintsQuickXPlain.ContinuingSync.notifyAll();
                }
//				} else {
//					knownConflicts.addItemListNoDups(nodeLabel);
//				}

                switch (ConflictSearchMode) {
                    case Least:
                        kb1.removeAll(conflict);
                        kb2.removeAll(conflict);
                        remainingConstraints.removeAll(conflict);
                        allTestStatements.removeAll(conflict);
                        break;
                    case Some:
                        kb1.removeAll(conflict);
                        conflictToRemove.clear();
                        conflictToRemove.addAll(conflict);
                        conflictToRemove.removeAll(kb2);

                        remainingConstraints.removeAll(conflictToRemove);
                        allTestStatements.removeAll(conflictToRemove);
                        break;
                    case EnoughFor1Diag:
                        List<T> copy = new ArrayList<T>(conflict);
                        copy.retainAll(kb1);
                        kb1.remove(copy.get(0));
                        remainingConstraints.remove(copy.get(0));
                        allTestStatements.remove(copy.get(0));
                        break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return result;
            }
        }
        result.ConflictFreeStatements.addAll(remainingConstraints);
        incrementCounter(COUNTER_MXP_CONFLICTS, result.Conflicts.size());

        return result;
    }

    private List<T> findSingleConflict(
            List<T> correctStatements,
            List<T> kb,
            List<T> delta,
            List<T> c) throws DomainSizeException, InterruptedException {

        List<T> result = new ArrayList<T>();

        List<T> kbAndCorrectStatements = new ArrayList<T>(kb);
        kbAndCorrectStatements.addAll(correctStatements);

        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }

        if (delta.size() != 0 && !isConsistent(kbAndCorrectStatements)) {
            return result;
        }

        if (c.size() == 1) {
            result.addAll(c);
            return result;
        }

        // Split
        int split = split(c);
        List<T> s1 = new ArrayList<T>(c.subList(0, split));
        List<T> s2 = new ArrayList<T>(c.subList(split, c.size()));

        List<T> kbs1 = new ArrayList<T>(kb);
        kbs1.addAll(s1);
        result.addAll(findSingleConflict(correctStatements, kbs1, s1, s2));

        List<T> kbresult = new ArrayList<T>(kb);
        kbresult.addAll(result);
        result.addAll(findSingleConflict(correctStatements, kbresult, result, s1));

//		if (listComparator.compare(kb, delta) == 0) {
//			result.addAll(findSingleConflict(correctStatements, result, result, kb));
//		}

        return result;

    }

    public enum ConflictSearchModes {Least, Some, EnoughFor1Diag}
}
