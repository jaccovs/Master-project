package org.exquisite.core.engines;

import org.exquisite.core.conflictsearch.Progression;
import org.exquisite.core.solver.ISolver;

/**
 * Test diagnosis search with HSDAG in combination with Progression as conflict searcher.
 *
 * @author wolfi
 */
public class TestHSDAGEngineWithProgression extends AbstractDiagnosisEngineTest {
    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        return new HSDAGEngine(solver, new Progression(solver));
    }
}
