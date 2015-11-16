package org.exquisite.diagnosis.engines.common;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.engines.FullParallelHSDagBuilder;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import java.util.concurrent.CountDownLatch;

/**
 * Used by ParallelHSDagBuilder to expand an edge label of a parent node.
 * Runs an instance of NodeExpander in its own thread.
 * Decrements the CountDownLatch counter.
 *
 * @author David
 */
public class EdgeWorker<T> extends Thread {

    public AbstractHSDagBuilder<T> dagBuilder = null;

    /**
     * The node to be expanded.
     */
    DAGNode<T> parent;
    /**
     * Which edge label of the parent node to expand.
     */
    T edgeLabel;
    /**
     * The count down barrier.
     */
    CountDownLatch cdl;

    /**
     * Session data
     */
    ExquisiteSession sessionData;

    /**
     * The original tests.diagnosis model
     */
    DiagnosisModel<T> model;

    /**
     * A shared collection of nodes to expand
     */
    SharedCollection<DAGNode<T>> newNodesToExpand;

    // Are we level-synchronized or not
    boolean inFullParallel = false;


    public EdgeWorker(
            AbstractHSDagBuilder<T> dagbuilder,
            DAGNode<T> parent,
            T edgeLabel,
            CountDownLatch cdl,
            ExquisiteSession<T> sessionData,
            SharedCollection<DAGNode<T>> newNodesToExpand,
            DiagnosisModel<T> model) {

        this.setDaemon(true);
        this.dagBuilder = dagbuilder;
        this.parent = parent;
        this.edgeLabel = edgeLabel;
        this.cdl = cdl;
        this.sessionData = sessionData;
        if (cdl == null) {
            this.inFullParallel = true;
        }

        this.model = model;
        this.newNodesToExpand = newNodesToExpand;

    }

    /**
     * Expand the node.
     */
    public void run() {
        try {
            // Set up the node expander in preparation to run.
            NodeExpander nodeExpander;
            if (this.inFullParallel) {
                nodeExpander = new FullParallelNodeExpander(this.dagBuilder, newNodesToExpand);
            } else {
                nodeExpander = new NodeExpander<>(this.dagBuilder, this.newNodesToExpand);
            }
            nodeExpander.setDiagnosisModel(model);

//			System.out.println("Expanding node (EdgeWorker.run())");
            // Remmeber that we are now working on it
            this.parent.nodeStatusP = DAGNode.nodeStatusParallel.active;
//			System.out.println("WORKER: Running on the expansion");
            nodeExpander.expandNode(this.parent, this.edgeLabel);
//			System.out.println("Done expansion: " + Thread.currentThread().getId());
            this.parent.nodeStatusP = DAGNode.nodeStatusParallel.finished;
        } catch (DomainSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//		System.out.println("Counting down from: " + cdl.getCount());
        // Only do this if we have such a barrier per level
        if (!inFullParallel) {
            cdl.countDown();
        } else {
            // We are in full parallel.
            // Count down - we have seen enough
            synchronized (dagBuilder) {
                ((FullParallelHSDagBuilder) dagBuilder).incrementJobsToDo(-1);
                dagBuilder.notify();
            }
            // TODO some cleanup

        }
    }
}
