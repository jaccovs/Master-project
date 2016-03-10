package tests.dj.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.exquisite.diagnosis.engines.common.SharedCollection;

/**
 * Some test on parallel execution and monitoring when things are done..
 * @author dietmar
 *
 */
public class ParallelizationTest {

	ExecutorService threadPool;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ParallelizationTest test = new ParallelizationTest();
		System.out.println("Starting test");
		try {
			test.run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Test ended");
	}
	
	
	// How many jobs are open
	public static int jobsToDo = 1;
	
	static int NO_OP = -10;
	
	// avoid conflicts here
	// could also be a decrease
	synchronized static int incrementJobsToDo(int i) {
		if (i == NO_OP) {
			return jobsToDo;
		}
		else {
			jobsToDo = jobsToDo + i;
			System.out.println("Remaining jobs to do: " + jobsToDo);
		}
		return i;
	}
	

	
	// The list of jobs
	SharedCollection<Job>  jobs  = new SharedCollection<Job>();
	
	/**
	 * Does the work
	 */
	private void run() {
		long start = System.currentTimeMillis();
		
		// Create a thread pool
		threadPool = Executors.newFixedThreadPool(4);
	
		Job startJob = new Job("1",4);
		jobs.add(startJob);
		
		
		int lastJob = 0;
		int lastSize = 0;
		// Look if we have sth to do
		while (incrementJobsToDo(NO_OP) > 0) {
//			System.out.println("RUN: Assigning next job: " + nextJob + ", last job was: " + lastJob);
			int size = jobs.getCollection().size();
			if (size != lastSize) {
				// A number of new jobs arrived
				int newJobs = size - lastSize;
				lastSize = size;
				System.out.println("RUN: " + newJobs + " new jobs in queue");
				for (int i=0;i<newJobs;i++) {
					Job job = jobs.getCollection().get(lastJob);
					Worker worker = new Worker(lastJob,job);
					threadPool.execute(worker);
					System.out.println("RUN: New job to do at pos: " + lastJob + ": " + job);
					lastJob++;
				}
				
			}
			else {
//				System.out.println("RUN: No new job started");
			}
			try {
				// Wait a bit..
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("RUN: Shutting down the threadpool");
		System.out.println("RUN: Job pool size: " + jobs.getCollection().size());
		
		threadPool.shutdown();
		long stop = System.currentTimeMillis();
		System.out.println("Needed " + (stop - start) +  "ms" );
	
	}
	
	
	// A job to do. 
	class Job {
		String id;
		public int followupjobs;
		
		/**
		 * Create a new job
		 * @param id
		 * @param numFollow
		 */
		public Job (String id, int numFollow) {
			this.id = id;
			this.followupjobs = numFollow;
		}
		
		/**
		 * A string representation
		 */
		public String toString() {
			return "J: " + id + " (" + this.followupjobs + ")";
		}
		
	}
	
	
	// Does the job
	class Worker extends Thread {
		Job job;
		int id = -1;
		
		// Creates a new worker
		public Worker(int i, Job job) {
			id = i;
			this.job= job;
//			System.out.println("Created worker with " + job.followupjobs + " followupjobs (id: " + id + ")");
		}
		
		// What to do 
		public void run() {
			System.out.println("WORKER Starting:	" + id);
			try {
				long start = System.currentTimeMillis();
				
				while (System.currentTimeMillis() - start < 1000) {
					// do nothing
				}
				// active wait
				
//				Thread.sleep(500);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			// Create the follow-up jobs
			if (this.job.followupjobs >= 0) {
			
				System.out.println("WORKER: Creating " + this.job.followupjobs + " follow-ups with smaller count: " + (this.job.followupjobs - 1));
				for (int i=0;i<this.job.followupjobs;i++) {
					Job j = new Job(this.id + "_" + i ,this.job.followupjobs-1);
					// Push them into the list. No worries if others interfere her
					jobs.add(j);
				}
				// increment the job counter 
				ParallelizationTest.incrementJobsToDo(this.job.followupjobs-1);

				System.out.println("WORKER: Current jobs");
				System.out.println(jobs.getCollection());
			}
			else {
				System.out.println("WORKER: Not creating any follow-up jobs");
			}
			System.out.println("WORKER Ending:		" + id);
			
		}
		
	}
	
	

}
