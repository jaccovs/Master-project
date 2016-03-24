package org.exquisite.core.costestimators;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract cost estimator.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public abstract class AbstractCostEstimator<F> implements ICostsEstimator<F> {

    private final Collection<F> possiblyFaultyFormulas;

    public AbstractCostEstimator(Collection<F> possiblyFaultyFormulas) {
        this.possiblyFaultyFormulas = possiblyFaultyFormulas;
    }

    protected Collection<F> getPossiblyFaultyFormulas() {
        return possiblyFaultyFormulas;
    }


    @Override
    public BigDecimal getFormulasCosts(Collection<F> formulas) {
        BigDecimal probability = BigDecimal.ONE;
        if (formulas != null)
            for (F formula : formulas) {
                probability = probability.multiply(getFormulaCosts(formula));
            }
        Collection<F> activeFormulas = new ArrayList<>(possiblyFaultyFormulas);
        assert formulas != null;
        activeFormulas.removeAll(formulas);
        for (F formula : activeFormulas) {
            probability = probability.multiply(BigDecimal.ONE.subtract(getFormulaCosts(formula)));
        }
        return probability;
    }
}
