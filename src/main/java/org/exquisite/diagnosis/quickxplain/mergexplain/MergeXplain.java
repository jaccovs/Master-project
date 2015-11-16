package org.exquisite.diagnosis.quickxplain.mergexplain;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.common.SharedCollection;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.tools.ListComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of MergeXplain
 *
 * @author Thomas
 */
public class MergeXplain<T> extends QuickXPlain<T> {

    public static ConflictSearchModes ConflictSearchMode = ConflictSearchModes.Least;

    ListComparator<T> listComparator = new ListComparator<T>();
    private int splittingConflicts;
    private int conflicts;

    /**
     * set currentDiagnosisModel as a copy of SessionData tests.diagnosis model.
     *
     * @param sessionData
     */
    public MergeXplain(ExquisiteSession sessionData, IDiagnosisEngine<T> diagnosisEngine) {
        super(sessionData, diagnosisEngine);
    }

    /**
     * Main method that is called to find conflicts
     */
    @Override
    public List<List<T>> findConflicts() throws DomainSizeException {
//		System.out.println("findConflicts()");
        splittingConflicts = 0;
        conflicts = 0;

        List<List<T>> result = new ArrayList<>();

//		if (checkConsistency() == true) {
//			Debug.msg("checkConsistency = true.");
//			return result;
//		} else {
        MergeXplainResult mxpResult = mergeXplain(this.currentDiagnosisModel.getPossiblyFaultyStatements(),
                this.currentDiagnosisModel.getCorrectStatements());

        result.addAll(mxpResult.Conflicts);
//			System.out.println("Found " + result.size() + " conflict(s).");
//		}

        // Release this guy
        if (this.constraintListener != null) {
//		    System.out.println("Done with QX - release the listener");
            this.constraintListener.release();
        }

        if (diagnosisEngine != null) {
            diagnosisEngine.incrementSearchesForConflicts();
            diagnosisEngine.incrementMXPConflicts(conflicts);
            diagnosisEngine.incrementMXPSplittingTechniqueConflicts(splittingConflicts);
        }

        return result;
    }

    /**
     * Variant of findConflicts() that notifies parent thread, when the first conflict was found
     */
    @Override
    public void findConflictsParallel(ConflictCheckingResult<T> result,
                                      SharedCollection<List<T>> knownConflicts)
            throws DomainSizeException {
//		System.out.println("findConflictsParallel()");
        splittingConflicts = 0;
        conflicts = 0;


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
//			System.out.println("Found " + result.size() + " conflict(s).");
//		}

        // Release this guy
        if (this.constraintListener != null) {
//		    System.out.println("Done with QX - release the listener");
            this.constraintListener.release();
        }

        if (diagnosisEngine != null) {
            diagnosisEngine.incrementSearchesForConflicts();
            diagnosisEngine.incrementMXPConflicts(conflicts);
            diagnosisEngine.incrementMXPSplittingTechniqueConflicts(splittingConflicts);
        }

//		Debug.syncMsg("MXP: Finished.");
    }

    private MergeXplainResult mergeXplain(
            List<T> possiblyFaultyStatements,
            List<T> correctStatements) throws DomainSizeException {

        MergeXplainResult result = new MergeXplainResult();

        // If all statements are consistent together, we cannot find a conflict here
        List<T> allStatements = new ArrayList<T>(possiblyFaultyStatements);
        allStatements.addAll(correctStatements);
        if (isConsistent(allStatements)) {
            result.ConflictFreeStatements.addAll(possiblyFaultyStatements);
            return result;
        }

        // If we have only one possibly faulty statement, it has to be a conflict
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
        MergeXplainResult r1 = mergeXplain(s1, correctStatements);
        MergeXplainResult r2 = mergeXplain(s2, correctStatements);

        // Add found conflicts
        result.Conflicts.addAll(r1.Conflicts);
        result.Conflicts.addAll(r2.Conflicts);

        splittingConflicts = result.Conflicts.size();

//		System.out.println("Found " + result.Conflicts.size() + " conflict(s) with splitting technique.");

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
                //kb1.removeAll(conflict);

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

        conflicts = result.Conflicts.size();

        return result;
    }

    /**
     * Variant of mergeXplain() that notifies parent thread, when the first conflict was found
     */
    private MergeXplainResult mergeXplainParallel(
            List<T> possiblyFaultyStatements,
            List<T> correctStatements, ConflictCheckingResult endResult,
            SharedCollection<List<T>> knownConflicts) throws DomainSizeException {

        MergeXplainResult result = new MergeXplainResult();

        // If all statements are consistent together, we cannot find a conflict here
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

        // If we have only one possibly faulty statement, it has to be a conflict
        if (possiblyFaultyStatements.size() == 1) {
            List<T> conflict = new ArrayList<T>(possiblyFaultyStatements);
            result.Conflicts.add(conflict);
            endResult.addConflict(conflict);
//			if (endResult.conflicts.size() == 1) {
//				Debug.syncMsg("MXP: Found first conflict.");
            synchronized (QuickXPlain.ContinuingSync) {
                knownConflicts.addItemListNoDups(conflict);
                QuickXPlain.ContinuingSync.notifyAll();
            }
//			} else {
//				knownConflicts.addItemListNoDups(conflict);
//			}
            return result;
        }

        // Split possibly faulty statements
        int split = split(possiblyFaultyStatements);
        List<T> s1 = new ArrayList<T>(possiblyFaultyStatements.subList(0, split));
        List<T> s2 = new ArrayList<T>(
                possiblyFaultyStatements.subList(split, possiblyFaultyStatements.size()));

        // Recursion
        MergeXplainResult r1 = mergeXplainParallel(s1, correctStatements, endResult, knownConflicts);
        MergeXplainResult r2 = mergeXplainParallel(s2, correctStatements, endResult, knownConflicts);

        // Add found conflicts
        result.Conflicts.addAll(r1.Conflicts);
        result.Conflicts.addAll(r2.Conflicts);

        splittingConflicts = result.Conflicts.size();

//		System.out.println("Found " + result.Conflicts.size() + " conflict(s) with splitting technique.");

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

                //kb1.removeAll(conflict);

                result.Conflicts.add(conflict);
                endResult.addConflict(conflict);
//				if (endResult.conflicts.size() == 1) {
                //				Debug.syncMsg("MXP: Found First conflict.");
                synchronized (QuickXPlain.ContinuingSync) {
                    knownConflicts.addItemListNoDups(conflict);
                    QuickXPlain.ContinuingSync.notifyAll();
                }
//				} else {
//					knownConflicts.addItemListNoDups(conflict);
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

        conflicts = result.Conflicts.size();

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
