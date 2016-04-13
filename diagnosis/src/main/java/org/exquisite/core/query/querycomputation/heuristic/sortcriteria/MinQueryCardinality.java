package org.exquisite.core.query.querycomputation.heuristic.sortcriteria;

import java.util.Set;

/**
 * A sort criterion that prefers the set of formulas with minimal cardinality.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author patrick
 * @author wolfi
 */
public class MinQueryCardinality<F> implements ISortCriterion<Set<F>> {

    @Override
    public int compare(Set<F> o1, Set<F> o2) {
        return o1.size() - o2.size();
    }

    @Override
    public String toString() {
        return ("Min");
    }
}
