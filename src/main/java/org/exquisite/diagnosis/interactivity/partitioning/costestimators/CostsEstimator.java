package org.exquisite.diagnosis.interactivity.partitioning.costestimators;


import java.math.BigDecimal;
import java.util.List;

import choco.kernel.model.constraints.Constraint;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 13.06.11
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public interface CostsEstimator {

    BigDecimal getFormulaSetCosts(List<Constraint> correctFormulas);

    BigDecimal getFormulaCosts(Constraint label);
}
