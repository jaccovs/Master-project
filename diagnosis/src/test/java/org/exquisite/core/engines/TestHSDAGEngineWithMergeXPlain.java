package org.exquisite.core.engines;

import org.exquisite.core.conflictsearch.MergeXPlain;
import org.exquisite.core.solver.ISolver;

/**
 * Test diagnosis search with HSDAG in combination with MergeXPlain as conflict searcher.
 *
 * @author wolfi
 */
public class TestHSDAGEngineWithMergeXPlain extends AbstractDiagnosisEngineTest {
    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        return new HSDAGEngine(solver, new MergeXPlain(solver));
    }
}
