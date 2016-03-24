package org.exquisite.core.costestimators;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Simple costs estimator that returns 1 for any input set of formulas.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public class SimpleCostsEstimator<F> implements ICostsEstimator<F> {

    /**
     * @param formulas set of formulas
     * @return 1 for any input set of formulas
     */
    @Override
    public BigDecimal getFormulasCosts(Collection<F> formulas) {
        return BigDecimal.ONE;
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
