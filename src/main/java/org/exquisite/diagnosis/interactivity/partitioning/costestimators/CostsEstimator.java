package org.exquisite.diagnosis.interactivity.partitioning.costestimators;


import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 13.06.11
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public interface CostsEstimator<T> {

    BigDecimal getFormulaSetCosts(List<T> correctFormulas);

    BigDecimal getFormulaCosts(T label);
}
