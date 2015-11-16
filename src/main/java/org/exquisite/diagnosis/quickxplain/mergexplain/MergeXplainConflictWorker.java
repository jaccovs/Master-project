package org.exquisite.diagnosis.quickxplain.mergexplain;

import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import java.util.ArrayList;
import java.util.List;

public class MergeXplainConflictWorker<T> extends MergeXplainWorker<T> {

    public static final int CONFLICT_PRIORITY = 0;

    private MergeXplainTreeWorker<T> parent, tw1, tw2;
    private MergeXplainResult<T> result;

    private List<T> correctStatements;

    public MergeXplainConflictWorker(ParallelMergeXplain<T> mergeXplain, MergeXplainTreeWorker<T> parent,
                                     MergeXplainTreeWorker<T> tw1, MergeXplainTreeWorker<T> tw2, int depth) {
        super(mergeXplain, CONFLICT_PRIORITY + depth, depth);
        this.parent = parent;
        this.tw1 = tw1;
        this.tw2 = tw2;
        this.correctStatements = mergeXplain.currentDiagnosisModel.getCorrectStatements();
    }

    @Override
    public void run() {
        if (!tw1.isFinished() || !tw2.isFinished()) {
//			System.out.println("Conflict aborted at level " + depth + ", tree1 f: " + tw1.isFinished() + ", tree2 f: " + tw2.isFinished() + ", Q: "+ mergeXplain.queue.size());
            mergeXplain.execute(this);
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

            return;
        }

        try {
//			System.out.println("Conflict worker started at level " + depth);

            //Do the work
            MergeXplainResult<T> r1 = tw1.getResult();
            MergeXplainResult<T> r2 = tw2.getResult();
            result = parent.getResult();

            // Add found conflicts
            result.Conflicts.addAll(r1.Conflicts);
            result.Conflicts.addAll(r2.Conflicts);

            //		splittingConflicts = result.Conflicts.size();

            //		System.out.println("Found " + result.Conflicts.size() + " conflict(s) with splitting technique.");

            // Search for conflicts in remaining constraints
            List<T> remainingConstraints = new ArrayList<T>(r1.ConflictFreeStatements);
            remainingConstraints.addAll(r2.ConflictFreeStatements);
            List<T> allTestStatements = new ArrayList<T>(remainingConstraints);
            allTestStatements.addAll(correctStatements);
            List<T> kb1 = new ArrayList<T>(r1.ConflictFreeStatements);
            List<T> kb2 = new ArrayList<T>(r2.ConflictFreeStatements);
            List<T> conflictToRemove = new ArrayList<T>();
            while (!mergeXplain.isConsistent(allTestStatements)) {
                List<T> conflict = findSingleConflict(correctStatements, kb2, kb2, kb1);
                conflict.addAll(findSingleConflict(correctStatements, conflict, conflict, kb2));
                //kb1.removeAll(conflict);

                result.Conflicts.add(conflict);

                switch (MergeXplain.ConflictSearchMode) {
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
            }
            result.ConflictFreeStatements.addAll(remainingConstraints);

        } catch (DomainSizeException e) {

        } finally {
//			System.out.println("Conflict finished at level " + depth + ", Q: " + mergeXplain.queue.size());
            parent.setFinished(true);
            if (depth == 0) {
                mergeXplain.threadPool.shutdown();
            }
        }
    }

    private List<T> findSingleConflict(
            List<T> correctStatements,
            List<T> kb,
            List<T> delta,
            List<T> c) throws DomainSizeException {

        List<T> result = new ArrayList<T>();

        List<T> kbAndCorrectStatements = new ArrayList<T>(kb);
        kbAndCorrectStatements.addAll(correctStatements);
        if (delta.size() != 0 && !mergeXplain.isConsistent(kbAndCorrectStatements)) {
            return result;
        }

        if (c.size() == 1) {
            result.addAll(c);
            return result;
        }

        // Split
        int split = mergeXplain.split(c);
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

}
