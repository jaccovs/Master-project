package org.exquisite.core.engines;


import org.exquisite.core.DiagnosisException;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.solver.ISolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.exquisite.core.Utils.hasIntersection;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.*;

/**
 * A basic class that is extended by all tree-like conflictsearch engines
 */
public class HSTreeEngine<F> extends AbstractDiagnosisEngine<F> implements IDiagnosisEngine<F> {

    final Logger logger = LoggerFactory.getLogger(HSTreeEngine.class);

    private Node<F> root = null;

    private TreeSet<Node<F>> openNodes;

    public HSTreeEngine(ISolver<F> solver) {
        super(solver);
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
        // generate root if there is none
        if (!hasRoot()) {
            Set<Set<F>> conflicts = getSearcher().findConflicts(getDiagnosisModel().getPossiblyFaultyStatements());
            if (conflicts == null || conflicts.isEmpty()) {
                logger.debug("The provided diagnosis model is correct");
                return getDiagnoses();
            }
            getConflicts().addAll(conflicts);
            Node<F> root = new Node<F>(selectConflict(conflicts));

            incrementCounter(COUNTER_CONSTRUCTED_NODES);
            logger.debug("Initializing the tree with the root {}", root);

            expand(root);
        }

        while (hasNodesToExpand()) {
            Node<F> node = getNextNode();
            if (skipNode(node)) continue;
            logger.debug("Processing node {}", node);
            label(node);
            if (node.getStatus() == Node.Status.Open)
                expand(node);
            if (stopComputations()) return getDiagnoses();
        }
        stop(TIMER_DIAGNOSIS_SESSION);
        return getDiagnoses();
    }

    /**
     * Computes a label for a given node.
     *
     * @param node for which a label has to be computed
     */
    protected void label(Node<F> node) throws DiagnosisException {
        if (node.getNodeLabel() == null) {
            logger.debug("The node has no label yet, let's compute one!");
            Set<Set<F>> conflicts = getReusableConflicts(node);
            // compute conflicts if there are none to reuse
            if (conflicts.isEmpty()) {
                logger.debug("There is nothing to reuse, computing a new conflict");
                conflicts = computeLabel(node);
            }
            if (conflicts.isEmpty()) {
                logger.debug("A diagnosis is found: {}", node.getPathLabels());
                node.setStatus(Node.Status.Diagnosis);
                getDiagnoses().add(new Diagnosis<F>(node.getPathLabels(), node.getCosts()));
                return;
            }
            node.setNodeLabel(selectConflict(conflicts));
        }
    }

    /**
     * This method is called when a new label for a node, e.g. a conflict, must be computed
     *
     * @param node
     * @return a set of labels
     */
    protected Set<Set<F>> computeLabel(Node<F> node) throws DiagnosisException {
        Set<F> formulas = new HashSet<>(getDiagnosisModel().getPossiblyFaultyStatements());
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
        return getMaxNumberOfDiagnoses() != 0 && getMaxNumberOfDiagnoses() <= getDiagnoses().size();
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
        logger.debug("Generate the children nodes of a node {}", nodeToExpand);
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
}
