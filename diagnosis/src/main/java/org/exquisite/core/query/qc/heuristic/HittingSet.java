package org.exquisite.core.query.qc.heuristic;

import org.exquisite.core.Utils;
import org.exquisite.core.query.qc.heuristic.sortcriteria.ISortCriterion;

import java.util.*;

/**
 * Hitting set algorithm.
 *
 * @author patrick
 * @author wolfi
 */
public class HittingSet {

    private static Label CLOSED_LABEL = new Label<>(Label.L.CLOSED);
    private static Label VALID_LABEL = new Label<>(Label.L.VALID);

    /**
     * A set S that has a non-empty intersection with every set in a collection of sets C is called a hitting set of C.
     * If no element can be removed from S without violating the hitting set property, S is considered to be minimal.
     *
     * Find at least min and at most max hitting sets for set of minimum traits.
     * The algorithm stops if timeout threshold (given in milliseconds) exceeds.
     * If maximum is large enough this algorithm finds all existing hitting sets.
     *
     * @param collectionOfSets set of minimum traits.
     * @param timeout timeout in milliseconds, the algorithm shall search for.
     * @param min minimal count of hitting sets.
     * @param max maximal count of hitting sets.
     * @param sortCriterion A sort criterion which hitting sets are preferred.
     * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
     * @return Set of hitting sets.
     */
    public static <F> Set<Set<F>> hittingSet(final Set<Set<F>> collectionOfSets, final long timeout, final int min, final int max, final ISortCriterion<Set<F>> sortCriterion) {
        assert (min <= max);
        assert (timeout >= 0);

        long tStart = System.currentTimeMillis();

        Set<Set<F>> setMinimalQueries = new HashSet<>(); // bereits berechnete set-minimale queries
        Queue<Set<F>> queue = new PriorityQueue<>(sortCriterion);
        queue.add(new HashSet<>());

        do {
            Set<F> node = Utils.getFirstElem(queue, true);
            Label<F> L = label(node, collectionOfSets, setMinimalQueries, queue);

            if (L.label == Label.L.VALID)
                setMinimalQueries.add(node);
            else if (L.label == Label.L.FORMULAS)
                for (F formula : L.formulas) {
                    Set<F> newSet = new HashSet<>(node);
                    newSet.add(formula);
                    queue.add(newSet);
                }
        } while(!(queue.isEmpty() || (setMinimalQueries.size() >= min && (setMinimalQueries.size() == max || System.currentTimeMillis() - tStart > timeout))));

        return setMinimalQueries;
    }

    private static <F> Label<F> label(final Set<F> node, final Set<Set<F>> setOfMinTraits, final Set<Set<F>> setMinimalQueries, final Queue<Set<F>> queue) {
        for (Set<F> nd : setMinimalQueries)
            if (node.containsAll(nd))
                return CLOSED_LABEL;
        for (Set<F> nd : queue)
            if (node.equals(nd))
                return CLOSED_LABEL;
        for (Set<F> C : setOfMinTraits)
            if (!Utils.hasIntersection(C, node))
                return new Label<F>(C);
        return VALID_LABEL;
    }

    /**
     * Helper class for label method.
     *
     * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
     */
    private static class Label<F> {

        enum L {CLOSED, VALID, FORMULAS}
        Set<F> formulas;
        L label;

        /**
         * Constructor for label CLOSED or VALID.
         *
         * @param label CLOSED or VALID
         */
        Label(L label) {
            this.label = label;
        }

        /**
         * Constructor for label FORMULAS.
         *
         * @param formulas Set of Formulas, Statements, Axioms, Logical Sentences, Constraints etc, label becomes FORMULAS.
         */
        Label(Set<F> formulas) {
            this(Label.L.FORMULAS);
            this.formulas = formulas;
        }
    }
}