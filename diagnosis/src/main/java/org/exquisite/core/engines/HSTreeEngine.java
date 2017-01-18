package org.exquisite.core.engines;

import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.core.conflictsearch.IConflictSearcher;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.solver.ISolver;
import org.exquisite.core.DiagnosisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.exquisite.core.Utils.hasIntersection;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.*;

/**
 * A basic class that is extended by all tree-like conflict search engines.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 */
public class HSTreeEngine<F> extends AbstractDiagnosisEngine<F> implements IDiagnosisEngine<F> {

    protected Logger logger;

    private Node<F> root = null;

    private TreeSet<Node<F>> openNodes;

    /**
     * Creates a HSTree diagnosis engine with a specific solver. No progress monitor and QuickXPlain as conflict searcher will be
     * applied.
     * @param solver Applies a given solver to this engine. <strong>Must not be <code>null</code></strong>.
     */
    public HSTreeEngine(ISolver<F> solver) {
        this(solver, null, null);
    }

    /**
     * Creates a HSTree diagnosis engine with a specific solver and conflict searcher.
     *
     * @param solver Applies a given solver to this engine. <strong>Must not be <code>null</code></strong>.
     * @param conflictSearcher A conflict searcher. If <code>null</code> QuickXPlain will be used as conflict searcher.
     */
    public HSTreeEngine(ISolver<F> solver, IConflictSearcher<F> conflictSearcher) {
        this(solver, conflictSearcher, null);
    }

    /**
     * Creates a HSTree diagnosis engine with a specific solver and progress monitor. As conflict searcher QuickXPlain will be
     * applied.
     *
     * @param solver Applies a given solver to this engine. <strong>Must not be <code>null</code></strong>.
     * @param monitor A progress monitor which can be <code>null</code> if no progress monitoring is necessary/required.
     */
    public HSTreeEngine(ISolver<F> solver, IExquisiteProgressMonitor monitor) {
        this(solver, null, monitor);
    }

    /**
     * Creates a HSTree diagnosis engine with a specific solver, conflict searcher and progress monitor.
     *
     * @param solver Applies a given solver to this engine. <strong>Must not be <code>null</code></strong>.
     * @param conflictSearcher A conflict searcher. If <code>null</code> QuickXPlain will be used as conflict searcher.
     * @param monitor A progress monitor which can be <code>null</code> if no progress monitoring is necessary/required.
     */
    public HSTreeEngine(ISolver<F> solver, IConflictSearcher<F> conflictSearcher, IExquisiteProgressMonitor monitor) {
        super(solver, conflictSearcher, monitor);
        this.logger = LoggerFactory.getLogger(HSTreeEngine.class);
        openNodes = new TreeSet<>(getNodeComparator());
    }

    /**
     * Creates a comparator that compares nodes according to their costs and generation order. The method is made so
     * that after sorting the leftmost node of the sorted collection will be the one with the smallest costs. If two
     * nodes have the same costs, then the one with the lover generation is greater.
     * <p>
     * Override this method to get other orderings of the list of open nodes. The current one implements the uniform
     * cost conflictsearch ordering.
     */
    public static <F> Comparator<Node<F>> getNodeComparator() {
        return (o1, o2) -> {
            int res = o1.getCosts().compareTo(o2.getCosts());
            if (res == 0)
                return (o1.generationOrder < o2.generationOrder) ? 1 : -1;
            return res;
        };
    }

    @Override
    public void resetEngine() {
        super.resetEngine();
        this.root = null;
        openNodes.clear();
    }

    /**
     * The main method that calculates the diagnoses
     *
     * @return a set of diagnoses or an empty set, of there no faults were found.
     */
    @Override
    public Set<Diagnosis<F>> calculateDiagnoses() throws DiagnosisException {
        start(TIMER_DIAGNOSIS_SESSION);
        notifyTaskStarted(); // progress
        try {
            // generate root if there is none
            if (!hasRoot()) {
                Set<Set<F>> conflicts = getSearcher().findConflicts(getDiagnosisModel().getPossiblyFaultyFormulas());
                if (conflicts == null || conflicts.isEmpty()) {
                    //logger.debug("The provided diagnosis model is correct");
                    return getDiagnoses();
                }
                addConflicts(conflicts);
                Node<F> root = Node.createRoot(selectConflict(conflicts));

                incrementCounter(COUNTER_CONSTRUCTED_NODES);
                //logger.debug("Initializing the tree with the root {}", root);

                expand(root);
            }

            while (hasNodesToExpand()) {
                Node<F> node = getNextNode();
                if (skipNode(node)) continue;
                //logger.debug("Processing node {}", node);
                label(node);
                if (node.getStatus() == Node.Status.Open)
                    expand(node);
                if (stopComputations()) return getDiagnoses();
            }
            return getDiagnoses();
        } finally {
            notifyTaskStopped(); // progress
            stop(TIMER_DIAGNOSIS_SESSION);
        }
    }

    /**
     * Computes a label for a given node.
     *
     * @param node for which a label has to be computed
     */
    protected void label(Node<F> node) throws DiagnosisException {
        if (node.getNodeLabel() == null) {
            //logger.debug("The node has no label yet, let's compute one!");
            Set<Set<F>> conflicts = getReusableConflicts(node);
            // compute conflicts if there are none to reuse
            if (conflicts.isEmpty()) {
                //logger.debug("There is nothing to reuse, computing a new conflict");
                conflicts = computeLabel(node);
            }
            if (conflicts.isEmpty()) {
                logger.debug("Diagnosis #{} is found: {}", (getDiagnoses().size()+1), node.getPathLabels());
                node.setStatus(Node.Status.Diagnosis);
                getDiagnoses().add(new Diagnosis<F>(node.getPathLabels(), node.getCosts()));
                notifyTaskProgress(getDiagnoses().size());
                return;
            }
            node.setNodeLabel(selectConflict(conflicts));
        }
    }

    /**
     * This method is called when a new label for a node, e.g. a conflict, must be computed
     *
     * @param node A node.
     * @return a set of labels
     */
    protected Set<Set<F>> computeLabel(Node<F> node) throws DiagnosisException {
        Set<F> formulas = new HashSet<>(getDiagnosisModel().getPossiblyFaultyFormulas());
        formulas.removeAll(node.getPathLabels());
        Set<Set<F>> conflicts = getSearcher().findConflicts(formulas);
        getConflicts().addAll(conflicts);
        return conflicts;
    }

    /**
     * Returns <code>true</code> if the goals of the diagnosis computation are achieved. Override this method to add
     * more stopping criteria.
     *
     * @return <code>true</code> if the required number of diagnoses is found.
     */

    private boolean stopComputations() {
        return isCancelled() || (getMaxNumberOfDiagnoses() != 0 && getMaxNumberOfDiagnoses() <= getDiagnoses().size());
    }

    /**
     * Returns <code>true</code> if the node must not be expanded, i.e. the node is not open or the maximum depth is
     * reached.
     *
     * @param node to be verified
     * @return <code>true</code> if the node must be skipped and <code>false</code> otherwise.
     */
    protected boolean skipNode(Node<F> node) {
        boolean condition1 = getMaxDepth() != 0 && getMaxDepth() <= node.getNodeLevel();
        return node.getStatus() != Node.Status.Open || condition1 || canPrune(node);
    }

    /**
     * Returns <code>true</code> if the list of open nodes is not empty.
     *
     * @return <code>true</code> is the list of open nodes is not empty and <code>false</code> otherwise
     */
    protected boolean hasNodesToExpand() {
        return !openNodes.isEmpty();
    }

    /**
     * Selects a conflict to lable a node from a set of conflicts. This implementation simply returns the first
     * conflict from the given set of conflicts
     *
     * @param conflicts input set of conflict
     * @return node label
     */
    protected Set<F> selectConflict(Set<Set<F>> conflicts) {
        return conflicts.iterator().next();
    }


    /**
     * Finds a reusable conflict. Override this method if more than one conflicts must be returned for the node label.
     *
     * @param node for which a reusable conflict must be found
     * @return a set with 1 conflict to be used as a node label
     */
    protected Set<Set<F>> getReusableConflicts(Node<F> node) {
        Set<Set<F>> conflicts = new HashSet<>(1);
        for (Set<F> conflict : getConflicts()) {
            if (!hasIntersection(node.getPathLabels(), conflict)) {
                conflicts.add(conflict);
                incrementCounter(COUNTER_REUSE);
                return conflicts;
            }
        }
        return conflicts;
    }


    /**
     * The method that expands a tree node.
     */
    protected void expand(Node<F> nodeToExpand) {
        //logger.debug("Generate the children nodes of a node {}", nodeToExpand);
        for (F label : nodeToExpand.getNodeLabel()) {
            Node<F> node = new Node<>(nodeToExpand, label, getCostsEstimator());
            incrementCounter(COUNTER_CONSTRUCTED_NODES);
            if (!canPrune(node))
                getOpenNodes().add(node);
        }
    }

    /**
     * Note that this implementation suggests that all conflicts returned by a
     * conflict searcher are minimal. Therefore only the first rule of Reiter is implemented. The second rule is
     * omitted as the number of open nodes that must be verified in vain might be large.
     *
     * @param node to check
     * @return <code>true</code> is a node can be pruned
     */

    protected boolean canPrune(Node<F> node) {
        for (Diagnosis<F> diagnosis : getDiagnoses()) {
            if (node.getPathLabels().containsAll(diagnosis.getFormulas())) {
                node.setStatus(Node.Status.Closed);
                return true;
            }
        }
        return false;
    }

    protected Node<F> getRoot() {
        return root;
    }

    protected void setRoot(Node<F> root) {
        this.root = root;
    }

    protected boolean hasRoot() {
        return this.root != null;
    }

    /**
     * Retrieves and removes the next node from the list of open nodes
     *
     * @return and removes the last node of the list
     */
    protected Node<F> getNextNode() {
        return getOpenNodes().pollLast();
    }

    protected TreeSet<Node<F>> getOpenNodes() {
        return openNodes;
    }

    protected boolean addConflicts(Set<Set<F>> conflicts) {
        return getConflicts().addAll(conflicts);
    }

    @Override
    public String toString() {
        return "HSTreeEngine("+getSearcher()+")";
    }
}
