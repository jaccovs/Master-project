package org.exquisite.core.engines;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.solver.ISolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Todo documentation
 *
 * @author kostya
 * @author patrick
 * @author wolfi
 */
public class InverseQuickXPlain<F> extends QuickXPlain<F> {

    private Set<F> possiblyFaultyFormulas;

    public InverseQuickXPlain(ISolver<F> solver) {
        super(solver);
        possiblyFaultyFormulas = new HashSet<>(solver.getDiagnosisModel().getPossiblyFaultyFormulas());
    }

    @Override
    protected boolean verifyConsistency(List<F> formulas) {
        List<F> list = new ArrayList<>(this.possiblyFaultyFormulas);
        list.removeAll(formulas);
        return !super.verifyConsistency(list);
    }
}
