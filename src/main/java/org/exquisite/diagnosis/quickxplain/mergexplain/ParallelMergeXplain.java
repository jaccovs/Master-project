package org.exquisite.diagnosis.quickxplain.mergexplain;

import org.exquisite.datamodel.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.tools.Debug;
import org.exquisite.tools.ListComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.exquisite.core.measurements.MeasurementManager.*;

/**
 * Parallel version of MergeXplain
 *
 * @author Schmitz
 */
public class ParallelMergeXplain<T> extends ConstraintsQuickXPlain<T> {

    /**
     * maximum number of threads to initialize the thread pool with.
     */
    public static int maxThreadPoolSize = 4;
    public PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<Runnable>();
    ListComparator<T> listComparator = new ListComparator<T>();
    ExecutorService threadPool;

    /**
     * set currentDiagnosisModel as a copy of SessionData tests.diagnosis model.
     *
     * @param sessionData
     */
    public ParallelMergeXplain(DiagnosisModel<T> sessionData) {
        super(sessionData);//, dagbuilder);
        threadPool = new ThreadPoolExecutor(maxThreadPoolSize, maxThreadPoolSize, 1, TimeUnit.SECONDS,
                queue); //Executors.newFixedThreadPool(maxThreadPoolSize);
    }

    public void execute(MergeXplainWorker command) {
        threadPool.execute(command);
    }

    /**
     * Main method that is called to find conflicts
     */
    @Override
    public List<List<T>> findConflicts() throws DomainSizeException {

        List<List<T>> result = new ArrayList<List<T>>();

//		CountDownLatch countDownLatch = new CountDownLatch(1);

        if (checkConsistency()) {
            Debug.msg("checkConsistency = true.");
            return result;
        } else {
            MergeXplainTreeWorker<T> mxp = new MergeXplainTreeWorker<>(this,
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
//			System.out.println("Found " + result.size() + " nodeLabel(s).");
        }

        // Release this guy
        if (this.constraintListener != null) {
//		    System.out.println("Done with QX - release the listener");
            this.constraintListener.release();
        }

        incrementCounter(COUNTER_SEARCH_CONFLICTS);
        return result;
    }
}
