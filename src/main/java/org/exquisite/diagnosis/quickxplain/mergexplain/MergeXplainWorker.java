package org.exquisite.diagnosis.quickxplain.mergexplain;

public class MergeXplainWorker<T> implements Runnable, Comparable<MergeXplainWorker> {

    protected ParallelMergeXplain<T> mergeXplain;
    protected int depth;
    private int priority;

    public MergeXplainWorker(ParallelMergeXplain<T> mergeXplain, int priority, int depth) {
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
