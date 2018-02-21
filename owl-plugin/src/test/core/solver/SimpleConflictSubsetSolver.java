package org.exquisite.core.solver;


import org.exquisite.core.model.DiagnosisModel;

import java.util.*;

/**
 * This is a simple solver for testing and exemplification purposes. The solver gets a set of integers as a domain (i
 * .e. a knowledge base) and a set of conflict sets, where each conflict is a subset of the domain. The solver
 * returns that any input set of integers from the domain is consistent only if none of the conflicts is a subset of
 * the input set.
 */
public class SimpleConflictSubsetSolver extends AbstractSolver<Integer> {

    private final Set<Integer> domain;
    private final List<Set<Integer>> conflicts;

    private final Set<Integer> currentState = new HashSet<>();

    public SimpleConflictSubsetSolver(DiagnosisModel<Integer> model, Set<Integer> domain, List<Set<Integer>> conflicts) {
        super(model);
        this.domain = new HashSet<>(domain);
        this.conflicts = new LinkedList<>(conflicts);
    }

    @Override
    protected Set<Integer> calculateEntailments() {
        return this.currentState;
    }

    @Override
    protected boolean isEntailed(Collection<Integer> entailments) {
        return this.currentState.containsAll(entailments);
    }

    @Override
    protected Integer negate(Integer example) {
        throw new RuntimeException("Negation is not supported");
    }

    @Override
    protected boolean supportsNegation() {
        return false;
    }

    @Override
    protected void sync(Set<Integer> addFormulas, Set<Integer> removeFormulas) {
        if (addFormulas.stream().anyMatch(removeFormulas::contains))
            throw new RuntimeException("add formulas have intersection with remove!");
        if (removeFormulas.stream().anyMatch(addFormulas::contains))
            throw new RuntimeException("remove formulas have intersection with add!");

        this.currentState.addAll(addFormulas);
        this.currentState.removeAll(removeFormulas);
    }

    @Override
    protected boolean isConsistent() {
        for (Set<Integer> conflict : this.conflicts) {
            if (this.currentState.containsAll(conflict))
                return false;
        }
        return true;
    }
}
