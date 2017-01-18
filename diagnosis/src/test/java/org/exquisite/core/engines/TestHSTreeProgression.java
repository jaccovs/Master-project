package org.exquisite.core.engines;

import org.exquisite.core.conflictsearch.Progression;
import org.exquisite.core.solver.ISolver;

/**
 * Test diagnosis search with HSTree in combination with Progression as conflict searcher.
 *
 * @author wolfi
 */
public class TestHSTreeProgression extends AbstractDiagnosisEngineTest {
    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        return new HSTreeEngine(solver, new Progression(solver));
    }
}
