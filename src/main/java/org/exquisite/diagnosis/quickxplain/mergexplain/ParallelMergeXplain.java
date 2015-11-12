package org.exquisite.diagnosis.quickxplain.mergexplain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.tools.Debug;
import org.exquisite.tools.ListComparator;

import choco.kernel.model.constraints.Constraint;

/**
 * Parallel version of MergeXplain
 * @author Schmitz
 *
 */
public class ParallelMergeXplain extends QuickXPlain {

	ListComparator<Constraint> listComparator = new ListComparator<Constraint>();
	
	private int splittingConflicts;
	private int conflicts;
	
	/**
	 * maximum number of threads to initialize the thread pool with.
	 */
	public static int maxThreadPoolSize = 4;
	
	ExecutorService threadPool;
	public PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<Runnable>();
	
	/**
	 * set currentDiagnosisModel as a copy of SessionData tests.diagnosis model.
	 * 
	 * @param sessionData
	 */
	public ParallelMergeXplain(ExquisiteSession sessionData, AbstractHSDagBuilder dagbuilder) {
		super(sessionData, dagbuilder);
		threadPool = new ThreadPoolExecutor(maxThreadPoolSize, maxThreadPoolSize, 1, TimeUnit.SECONDS, queue); //Executors.newFixedThreadPool(maxThreadPoolSize);
	}
	
	public void execute(MergeXplainWorker command) {
		threadPool.execute(command);
	}
	
	/**
	 * Main method that is called to find conflicts
	 */
	@Override
	public List<List<Constraint>> findConflicts() throws DomainSizeException {
		splittingConflicts = 0;
		conflicts = 0;
		
		List<List<Constraint>> result = new ArrayList<List<Constraint>>();
		
//		CountDownLatch countDownLatch = new CountDownLatch(1);
		
		if (checkConsistency() == true) {
			Debug.msg("checkConsistency = true.");
			return result;
		} else {
			MergeXplainTreeWorker mxp = new MergeXplainTreeWorker(this, 
					this.currentDiagnosisModel.getPossiblyFaultyStatements(), 0); //, countDownLatch);
			threadPool.execute(mxp);
//			mxp.run();
			
			try {
				threadPool.awaitTermination(1, TimeUnit.DAYS);
//				countDownLatch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
//			MergeXplainResult mxpResult = mergeXplain(this.currentDiagnosisModel.getPossiblyFaultyStatements(), 
//					this.currentDiagnosisModel.getCorrectStatements());
//			
			result.addAll(mxp.getResult().Conflicts);
//			System.out.println("Found " + result.size() + " conflict(s).");
		}
		
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
}
