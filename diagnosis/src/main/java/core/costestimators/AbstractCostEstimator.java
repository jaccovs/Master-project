package core.costestimators;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kostya
 * Date: 28.11.12
 * Time: 09:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCostEstimator<T> implements CostsEstimator<T> {

    private final Set<T> faultyFormulas;

    public AbstractCostEstimator(Set<T> faultyFormulas) {
        this.faultyFormulas = faultyFormulas;
    }

    protected Set<T> getFaultyFormulas() {
        return faultyFormulas;
    }


    @Override
    public BigDecimal getFormulasCosts(Collection<T> formulas) {
        BigDecimal probability = BigDecimal.ONE;
        if (formulas != null)
            for (T axiom : formulas) {
                probability = probability.multiply(getFormulaCosts(axiom));
            }
        Collection<T> activeFormulas = new ArrayList<>(faultyFormulas);
        assert formulas != null;
        activeFormulas.removeAll(formulas);
        for (T axiom : activeFormulas) {
            probability = probability.multiply(BigDecimal.ONE.subtract(getFormulaCosts(axiom)));
        }
        return probability;
    }
}
