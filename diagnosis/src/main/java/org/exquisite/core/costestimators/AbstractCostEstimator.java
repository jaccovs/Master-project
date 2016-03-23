package org.exquisite.core.costestimators;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kostya
 * Date: 28.11.12
 * Time: 09:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCostEstimator<F> implements CostsEstimator<F> {

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
