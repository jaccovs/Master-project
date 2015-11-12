package tests.arash;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 */

/**
 * @author Arash
 *
 */
public class TPoolTest {

	/**
	 * 
	 */
	public TPoolTest() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExecutorService threadPool = Executors.newFixedThreadPool(2);
		
		CountDownLatch barrier = new CountDownLatch(10);
		
		TPoolTest poolTest = new TPoolTest();
		
		for (int i = 0; i < 10; i++) {
			threadPool.execute(poolTest.new WorkerThread(barrier));
		}
		
		try {
			barrier.await();
			System.out.println(barrier.toString() + "!");
			System.out.println("TP Terminated: " + threadPool.isTerminated());
//			threadPool.shutdown();
			Thread.sleep(1000);
			barrier = new CountDownLatch(3);
			for (int i = 0; i < 10; i++) {
				threadPool.execute(poolTest.new WorkerThread(barrier));
			}
			barrier.await();
			System.out.println(barrier.toString() + "!");
			System.out.println("TP Terminated: " + threadPool.isTerminated());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	public class WorkerThread extends Thread {
		CountDownLatch latch;
		WorkerThread(CountDownLatch latch) {
			this.latch = latch;

		}
		
		@Override
		public void run() {
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
			this.latch.countDown();
			System.out.println(this.getName() + " has finished!");
		}
		
		/**
		 * Stopping the work
		 */
		@Override
		public void interrupt() {
			System.out.println("Interrupt called ....from outside" );
		}
	}
	
}
