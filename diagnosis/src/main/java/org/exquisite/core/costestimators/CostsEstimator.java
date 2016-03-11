package org.exquisite.core.costestimators;


import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 13.06.11
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public interface CostsEstimator<T> {

    BigDecimal getFormulasCosts(Collection<T> formulas);

    BigDecimal getFormulaCosts(T formula);
}
