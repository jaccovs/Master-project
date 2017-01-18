package org.exquisite.core.engines;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.solver.ISolver;

/**
 * Automated tests of the HSTreeEngine class using QuickXPlain as conflict searcher.
 *
 * @author wolfi
 */
public class TestHSTreeEngineWithQuickXPlain extends AbstractDiagnosisEngineTest {

    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        return new HSTreeEngine<>(solver, new QuickXPlain(solver));
    }
}
