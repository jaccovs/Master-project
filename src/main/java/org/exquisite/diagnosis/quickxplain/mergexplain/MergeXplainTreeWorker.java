package org.exquisite.diagnosis.quickxplain.mergexplain;

import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import java.util.ArrayList;
import java.util.List;

public class MergeXplainTreeWorker<T> extends MergeXplainWorker<T> {

    public static final int TREE_PRIORITY = 10000000;

    private List<T> possiblyFaultyStatements;
//	private CountDownLatch parentCountDownLatch = null;

    private List<T> correctStatements;

    private MergeXplainResult<T> result = new MergeXplainResult<>();

    private boolean finished = false;

    public MergeXplainTreeWorker(ParallelMergeXplain<T> mergeXplain,
                                 List<T> possiblyFaultyStatements,
                                 int depth)
//			CountDownLatch countDownLatch) 
    {
        super(mergeXplain, TREE_PRIORITY, depth);
        this.correctStatements = mergeXplain.currentDiagnosisModel.getCorrectStatements();
        this.possiblyFaultyStatements = possiblyFaultyStatements;
//		this.parentCountDownLatch = countDownLatch;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean value) {
        finished = value;
    }

    public MergeXplainResult<T> getResult() {
        return result;
    }

    @Override
    public void run() {
        try {
//			System.out.println("Tree worker started at level " + depth);

            // If all statements are consistent together, we cannot find a conflict here
            List<T> allStatements = new ArrayList<T>(possiblyFaultyStatements);
            allStatements.addAll(correctStatements);
            if (mergeXplain.isConsistent(allStatements)) {
                result.ConflictFreeStatements.addAll(possiblyFaultyStatements);
                finished = true;
                return;
            }

            // If we have only one possibly faulty statement, it has to be a conflict
            if (possiblyFaultyStatements.size() == 1) {
                result.Conflicts.add(new ArrayList<T>(possiblyFaultyStatements));
                finished = true;
                return;
            }

            // Split possibly faulty statements
            int split = mergeXplain.split(possiblyFaultyStatements);
            List<T> s1 = new ArrayList<T>(possiblyFaultyStatements.subList(0, split));
            List<T> s2 = new ArrayList<T>(
                    possiblyFaultyStatements.subList(split, possiblyFaultyStatements.size()));

//			CountDownLatch countDownLatch = new CountDownLatch(2);

            // Recursion
            MergeXplainTreeWorker<T> mxp1 = new MergeXplainTreeWorker<>(mergeXplain, s1, depth + 1);
            mergeXplain.execute(mxp1);
            MergeXplainTreeWorker<T> mxp2 = new MergeXplainTreeWorker<>(mergeXplain, s2, depth + 1);
//			mergeXplain.execute(mxp2);
            MergeXplainConflictWorker cw = new MergeXplainConflictWorker(mergeXplain, this, mxp1, mxp2, depth);
            mergeXplain.execute(cw);
            mxp2.run();

//			mxp1.start();
//			mxp2.run();

//			countDownLatch.await();


//			conflicts = result.Conflicts.size();

        } catch (DomainSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } //catch (InterruptedException e) {
        // TODO Auto-generated catch block
        //e.printStackTrace();
//		}
        finally {
//			System.out.println("Tree finished at level " + depth + ", finished: " + finished);
//			parentCountDownLatch.countDown();
        }
    }

}
