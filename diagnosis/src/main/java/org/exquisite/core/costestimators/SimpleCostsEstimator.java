package org.exquisite.core.costestimators;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Simple costs estimator that returns 1 for any input set of formulas
 */
public class SimpleCostsEstimator<T> implements CostsEstimator<T> {

    /**
     * @param formulas set of formulas
     * @return 1 for any input set of formulas
     */
    @Override
    public BigDecimal getFormulasCosts(Collection<T> formulas) {
        return BigDecimal.ONE;
    }


    /**
     * @param formula input formula
     * @return 1 for any input formula
     */
    @Override
    public BigDecimal getFormulaCosts(T formula) {
        return BigDecimal.ONE;
    }
}
