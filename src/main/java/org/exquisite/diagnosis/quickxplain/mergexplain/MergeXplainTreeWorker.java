package org.exquisite.diagnosis.quickxplain.mergexplain;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import choco.kernel.model.constraints.Constraint;

public class MergeXplainTreeWorker extends MergeXplainWorker {
	
	public static final int TREE_PRIORITY = 10000000;
	
	private List<Constraint> possiblyFaultyStatements;
//	private CountDownLatch parentCountDownLatch = null;
	
	private List<Constraint> correctStatements;
	
	private MergeXplainResult result = new MergeXplainResult();
	
	private boolean finished = false;
	
	public MergeXplainTreeWorker(ParallelMergeXplain mergeXplain,
			List<Constraint> possiblyFaultyStatements,
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
	
	public MergeXplainResult getResult() {
		return result;
	}
	
	@Override
	public void run(){
		try
		{
//			System.out.println("Tree worker started at level " + depth);
			
			// If all statements are consistent together, we cannot find a conflict here
			List<Constraint> allStatements = new ArrayList<Constraint>(possiblyFaultyStatements);
			allStatements.addAll(correctStatements);
			if (mergeXplain.isConsistent(allStatements)) {
				result.ConflictFreeStatements.addAll(possiblyFaultyStatements);
				finished = true;
				return;
			}
			
			// If we have only one possibly faulty statement, it has to be a conflict
			if (possiblyFaultyStatements.size() == 1) {
				result.Conflicts.add(new ArrayList<Constraint>(possiblyFaultyStatements));
				finished = true;
				return;
			}
			
			// Split possibly faulty statements
			int split = mergeXplain.split(possiblyFaultyStatements);
			List<Constraint> s1 = new ArrayList<Constraint>(possiblyFaultyStatements.subList(0, split));
			List<Constraint> s2 = new ArrayList<Constraint>(possiblyFaultyStatements.subList(split, possiblyFaultyStatements.size()));
			
//			CountDownLatch countDownLatch = new CountDownLatch(2);
			
			// Recursion
			MergeXplainTreeWorker mxp1 = new MergeXplainTreeWorker(mergeXplain, s1, depth + 1);
			mergeXplain.execute(mxp1);
			MergeXplainTreeWorker mxp2 = new MergeXplainTreeWorker(mergeXplain, s2, depth + 1);
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
