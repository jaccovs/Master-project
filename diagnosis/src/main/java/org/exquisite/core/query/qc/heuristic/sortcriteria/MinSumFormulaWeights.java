package org.exquisite.core.query.qc.heuristic.sortcriteria;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A sort criterion that prefers the set of formulas with the smallest sum of their formula weights.
 * In the case that two sets have equal sums of formula weights, the min cardinality formula set is preferred.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author patrick
 * @author wolfi
 */
public class MinSumFormulaWeights<F> implements ISortCriterion<Set<F>> {

    /**
     * Weights of constraints
     */
    private Map<F, Float> formulaWeights = new HashMap<>();

    /**
     * Formula weights are required for this sort criterion.
     *
     * @param formulaWeights A mapping of formulas to their weights.
     */
    public MinSumFormulaWeights(Map<F, Float> formulaWeights) {
        this.formulaWeights = formulaWeights;
    }

    @Override
    public int compare(Set<F> o1, Set<F> o2) {
        Float sumo1 = 0.0f;
        Float sumo2 = 0.0f;

        for (F formula : o1)
            sumo1 += formulaWeights.get(formula);

        for (F formula : o2)
            sumo2 += formulaWeights.get(formula);

        int comparison = sumo1.compareTo(sumo2);
        if (comparison == 0)
            return o1.size() - o2.size(); // tie-breaker muss min cardinality sein !!!
        else
            return comparison;
    }
}
