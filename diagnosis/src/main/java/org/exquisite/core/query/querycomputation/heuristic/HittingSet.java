package org.exquisite.core.query.querycomputation.heuristic;

import org.exquisite.core.Utils;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.ISortCriterion;

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
     * Find at least min and at most max hitting sets for a collection of sets of formulas.
     * The algorithm stops if timeout threshold (given in milliseconds) exceeds.
     * If maximum and timeout are large enough this algorithm finds all existing minimal hitting sets.
     *
     * @param collectionOfSets A collection of sets (e.g. diag traits). The collection has to be free of super sets!
     * @param timeout The timeout in milliseconds, the algorithm has time to search for.
     * @param min minimal count of hitting sets.
     * @param max maximal count of hitting sets.
     * @param sortCriterion A sort criterion which hitting sets are preferred.
     * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
     * @return Set of at least min minimal hitting sets and at most max minimal hitting sets if timeout does not exceed.
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
                    // TODO add 1 to CNT_GENERATED_HS_NODES
                    Set<F> newSet = new HashSet<>(node);
                    newSet.add(formula);
                    queue.add(newSet);
                }
        } while(!(queue.isEmpty() || (setMinimalQueries.size() >= min && (setMinimalQueries.size() == max || System.currentTimeMillis() - tStart > timeout))));

        return setMinimalQueries;
    }

    private static <F> Label<F> label(final Set<F> node, final Set<Set<F>> setOfMinTraits, final Set<Set<F>> setMinimalQueries, final Queue<Set<F>> queue) {
        // TODO add 1 to CNT_EXPANDED_HS_NODES
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