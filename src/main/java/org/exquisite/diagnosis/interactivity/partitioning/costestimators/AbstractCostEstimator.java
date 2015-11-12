package org.exquisite.diagnosis.interactivity.partitioning.costestimators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import choco.kernel.model.constraints.Constraint;

/**
 * Created with IntelliJ IDEA.
 * User: kostya
 * Date: 28.11.12
 * Time: 09:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCostEstimator implements CostsEstimator {

    private final Set<Constraint> faultyFormulas;

    public AbstractCostEstimator(Set<Constraint> faultyFormulas){
        this.faultyFormulas = faultyFormulas;
    }

    protected Set<Constraint> getFaultyFormulas() {
        return faultyFormulas;
    }


    @Override
    public BigDecimal getFormulaSetCosts(List<Constraint> formulas) {
        BigDecimal probability = BigDecimal.ONE;
        if (formulas != null)
            for (Constraint axiom : formulas) {
                probability = probability.multiply(getFormulaCosts(axiom));
            }
        Collection<Constraint> activeFormulas = new ArrayList<Constraint>(faultyFormulas);
        activeFormulas.removeAll(formulas);
        for (Constraint axiom : activeFormulas) {
            probability = probability.multiply(BigDecimal.ONE.subtract(getFormulaCosts(axiom)));
        }
        return probability;
    }
}
