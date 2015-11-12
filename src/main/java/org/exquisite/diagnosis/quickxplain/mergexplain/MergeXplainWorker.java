package org.exquisite.diagnosis.quickxplain.mergexplain;

public class MergeXplainWorker implements Runnable, Comparable<MergeXplainWorker>  {

	protected ParallelMergeXplain mergeXplain;
	private int priority;
	protected int depth;
	
	public MergeXplainWorker(ParallelMergeXplain mergeXplain, int priority, int depth) {
		this.mergeXplain = mergeXplain;
		this.priority = priority;
		this.depth = depth;
	}
	
	@Override
	public int compareTo(MergeXplainWorker o) {
		return o.priority - priority;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
