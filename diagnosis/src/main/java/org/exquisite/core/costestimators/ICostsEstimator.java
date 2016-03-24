package org.exquisite.core.costestimators;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Interface for cost estimators.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public interface ICostsEstimator<F> {

    BigDecimal getFormulasCosts(Collection<F> formulas);

    BigDecimal getFormulaCosts(F formula);
}
