package org.exquisite.core.costestimators;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Cardinality costs estimator that returns the number of formulas.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public class CardinalityCostEstimator<F> implements ICostsEstimator<F> {

    /**
     * @param formulas set of formulas
     * @return 1 for any input set of formulas
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
