package org.exquisite.core.costestimators;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Set estimator returns the size of the input set of formulas as an estimation
 */
public class SetCostsEstimator<T> implements CostsEstimator<T> {

    /**
     * @param formulas set of formulas
     * @return value corresponding to the size of the input set of formulas
     */
    @Override
    public BigDecimal getFormulasCosts(Collection<T> formulas) {
        return BigDecimal.valueOf(formulas.size());
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
