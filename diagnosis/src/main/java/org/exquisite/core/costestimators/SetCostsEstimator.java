package org.exquisite.core.costestimators;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Set estimator returns the size of the input set of formulas as an estimation
 */
public class SetCostsEstimator<F> implements CostsEstimator<F> {

    /**
     * @param formulas set of formulas
     * @return value corresponding to the size of the input set of formulas
     */
    @Override
    public BigDecimal getFormulasCosts(Collection<F> formulas) {
        return BigDecimal.valueOf(formulas.size());
    }


    /**
     * @param formula input formula
     * @return 1 for any input formula
     */
    @Override
    public BigDecimal getFormulaCosts(F formula) {
        return BigDecimal.ONE;
    }
}
