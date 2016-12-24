package org.exquisite.core.engines;

import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.core.solver.ISolver;
import org.exquisite.core.DiagnosisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.COUNTER_CONSTRUCTED_NODES;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.incrementCounter;

/**
 * The class that calculates the HS-Dag.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author Dietmar
 */
public class HSDAGEngine<F> extends HSTreeEngine<F> {

    private final Logger logger = LoggerFactory.getLogger(HSDAGEngine.class);
    private Map<Set<F>, List<Node<F>>> conflicts = new HashMap<>();
    private Map<Set<F>, Node<F>> nodesLookup = new HashMap<>();

    public HSDAGEngine(ISolver<F> solver) {
        this(solver, null);
    }

    public HSDAGEngine(ISolver<F> solver, IExquisiteProgressMonitor monitor) {
        super(solver, monitor);
    }

    /**
     * This method is called when a new label for a node, e.g. a conflict, must be computed. In HS-DAG this method
     * checks also if the returned labels are proper subsets of already known labels. That is, it implements the
     * pruning rule 3. according to the paper of Greiner et al. The remaining pruning rule 2 is implemented in the
     * {@link HSTreeEngine#canPrune(Node)} Note also, that this pruning method is adapted to conflict searching
     * algorithms that can return more than one conflict.
     *
     * @param node for which the label must be computed
     * @return a set of labels
     */
    @Override
    protected Set<Set<F>> computeLabel(Node<F> node) throws DiagnosisException {
        Set<Set<F>> conflicts;
        ArrayList<F> formulas = new ArrayList<>(getDiagnosisModel().getPossiblyFaultyFormulas());
        formulas.removeAll(node.getPathLabels());
        conflicts = getSearcher().findConflicts(formulas);

        // check existing and obtained conflicts for subset-relations
        Set<Set<F>> nonMinConflicts = new HashSet<>(getConflicts().size());
        for (Set<F> fs : getConflicts()) {
            if (nonMinConflicts.contains(fs))
                continue;
            for (Set<F> conflict : conflicts) {
                if (nonMinConflicts.contains(conflict))
                    continue;
                Set<F> greater = (fs.size() > conflict.size()) ? fs : conflict;
                Set<F> smaller = (fs.size() <= conflict.size()) ? fs : conflict;
                if (greater.containsAll(smaller)) {
                    nonMinConflicts.add(greater);
                    // update the DAG
                    for (Node<F> nd : this.conflicts.get(greater)) {
                        nd.setNodeLabel(smaller);
                        nd.setCosts(getCostsEstimator().getFormulasCosts(smaller));
                        Set<F> delete = new HashSet<>(greater);
                        delete.removeAll(smaller);
                        for (F label : delete) {
                            Node<F> child = nd.getChildren().get(label);
                            child.getParents().remove(nd);
                            nd.getChildren().remove(label);
                            cleanUpNodes(nd);
                        }
                    }
                }
            }
        }

        // remove the known non-minimal conflicts
        conflicts.removeAll(nonMinConflicts);
        getConflicts().removeAll(nonMinConflicts);

        // add new conflicts to the map
        for (Set<F> conflict : conflicts) {
            this.conflicts.put(conflict, new LinkedList<>());
        }
        return conflicts;
    }

    /**
     * Removes the subtree from a lookup table starting from the given node.
     *
     * @param node from which the conflictsearch should start
     */
    private void cleanUpNodes(Node<F> node) {
        if (!node.getParents().isEmpty())
            return;
        nodesLookup.remove(node.getPathLabels());
        for (F label : node.getChildren().keySet()) {
            Node<F> child = node.getChildren().get(label);
            cleanUpNodes(child);
        }
    }

    @Override
    public void resetEngine() {
        super.resetEngine();
        this.conflicts.clear();
        this.nodesLookup.clear();
    }

    @Override
    protected void expand(Node<F> nodeToExpand) {
        logger.debug("Generate the children nodes of a node {}", nodeToExpand);
        for (F label : nodeToExpand.getNodeLabel()) {
            // rule 1.a - reuse node
            Node<F> node = getReusableNode(nodeToExpand.getPathLabels(), label);
            if (node != null) {
                node.addParent(nodeToExpand);
            }
            // rule 1.b - generate a new node
            else {
                node = new Node<>(nodeToExpand, label, getCostsEstimator());
                this.nodesLookup.put(node.getPathLabels(), node);
                incrementCounter(COUNTER_CONSTRUCTED_NODES);
                if (!canPrune(node))
                    getOpenNodes().add(node);
            }
        }
    }

    private Node<F> getReusableNode(Set<F> pathLabels, F label) {
        Set<F> h = new HashSet<>(pathLabels.size() + 1);
        h.addAll(pathLabels);
        h.add(label);
        return this.nodesLookup.get(h);
    }

    @Override
    public Set<Set<F>> getConflicts() {
        return conflicts.keySet();
    }

    @Override
    protected boolean addConflicts(Set<Set<F>> conflicts) {
        for (Set<F> conflict : conflicts) {
            this.conflicts.put(conflict, new LinkedList<>());
        }
        return true;
    }
}
