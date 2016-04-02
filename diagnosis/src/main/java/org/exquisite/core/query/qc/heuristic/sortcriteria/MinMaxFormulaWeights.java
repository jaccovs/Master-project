package org.exquisite.core.query.qc.heuristic.sortcriteria;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A sort criterion that prefers the set of formulas with the least maximal weighted formula.
 * In the case that two sets have equal max weighted formulas, the min cardinality formula set is preferred.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author patrick
 * @author wolfi
 */
public class MinMaxFormulaWeights<F> implements ISortCriterion<Set<F>> {

    /**
     * Weights of constraints.
     */
    private Map<F, Float> formulaWeights = new HashMap<>();

    /**
     * Formula weights are required for this sort criterion.
     *
     * @param formulaWeights A mapping of formulas to their weights.
     */
    public MinMaxFormulaWeights(Map<F, Float> formulaWeights) {
        this.formulaWeights = formulaWeights;
    }

    @Override
    public int compare(Set<F> o1, Set<F> o2) {
        Float maxWeighto1 = Float.MIN_VALUE;
        Float maxWeighto2 = Float.MIN_VALUE;

        for (F formula : o1)
            maxWeighto1 = Float.max(formulaWeights.get(formula), maxWeighto1);

        for (F formula : o2)
            maxWeighto2 = Float.max(formulaWeights.get(formula), maxWeighto2);

        int comparison = maxWeighto1.compareTo(maxWeighto2);
        if (comparison == 0)
            return o1.size() - o2.size(); // tie-breaker muss min cardinality sein !!!
        else
            return comparison;
    }
}
