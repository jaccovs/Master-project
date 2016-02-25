package tests.dj.othertests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Some test of multiple conflicts and continuation once the first is here.
 * @author dietmar
 *
 */
public class MultiConflictTest extends Thread {

	
	private static ExecutorService service = Executors.newCachedThreadPool();
	
	/**
	 * The global list of known entries
	 */
	List<String> entries = new ArrayList<String>();
	String[] firstConflict = new String[1];
	
	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting");
		try {
			service.execute(new MultiConflictTest());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Program ended");
	}


	/**
	 * The main work
	 */
	@Override public void run() {
		System.out.println("Main start");
		synchronized (this) {
			service.execute(new ConflictWorker(this, entries, firstConflict));
			try {
				this.wait();
				
				System.out.println("wait done, let's give the other a chance. first nodeLabel: " + this.firstConflict[0]);
				Thread.sleep(3000);
				System.out.println("Entries: " + entries);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Main ended");
			service.shutdown();
		}
	}


	/**
	 * Do the work, return once you have a first set and then fill the rest
	 * @author dietmar
	 *
	 */
	class ConflictWorker implements Runnable {

		Thread parent = null;
		List<String> pointerToEntries = null; 
		String[] firstConflict = null;
		
		@Override
		public void run() {
			try {
				System.out.println("Thread sleeping");
				Thread.sleep(1000);
				firstConflict[0] = "a";
				System.out.println("Found first ");
				synchronized (parent) {
					parent.notify();
				} 
				Thread.sleep(1000);
				pointerToEntries.add("a");
				pointerToEntries.add("b");
				pointerToEntries.add("c");
				
				System.out.println("Added elements while the other was done");
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception here, " + e.getMessage());
			}
			System.out.println("Ended thread");
		}		
		/**
		 * Create the worker
		 * @param entries the global list
		 */
		public ConflictWorker(Thread p, List<String> entries, String[] first) {
			parent = p;
			pointerToEntries = entries;
			firstConflict = first;
		}
		
	}

}
