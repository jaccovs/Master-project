package org.exquisite.core.engines;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.solver.ISolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the FastDiag algorithm to find a diagnosis.
 * <ul>
 *     <li>
 *         A. Felfernig and M. Schubert. FastDiag: A Diagnosis Algorithm for Inconsistent Constraint Sets, 21st
 *         International Workshop on the Principles of Diagnosis, Portland, USA, pp. 31-38, 2010.
 *     </li>
 * </ul>
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 * @author patrick
 * @author wolfi
 */
public class InverseQuickXPlain<F> extends QuickXPlain<F> {

    public InverseQuickXPlain(ISolver<F> solver) {
        super(solver);
    }

    @Override
    protected boolean verifyConsistency(List<F> formulas) {
        List<F> list = new ArrayList<>(solver.getDiagnosisModel().getPossiblyFaultyFormulas());
        list.removeAll(formulas);
        return !super.verifyConsistency(list);
    }
}
