package org.exquisite.core.costestimators;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Collection;

/**
 * A cost estimator used to calculate the formula costs depending on the formula weights.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author patrick
 * @author wolfi
 */
public class FormulaWeightsCostEstimator<F> extends AbstractCostEstimator<F> {

    private Map<F, Float> formulaWeights;

    public FormulaWeightsCostEstimator(Collection<F> possiblyFaultyFormulas, Map<F, Float> formulaWeights) {
        super(possiblyFaultyFormulas);
        this.formulaWeights = formulaWeights;
    }

    public BigDecimal getFormulaCosts(F formula) {
        return new BigDecimal(this.formulaWeights.get(formula));
    }
}
