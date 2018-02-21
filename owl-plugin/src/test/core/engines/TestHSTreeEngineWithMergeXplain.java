package org.exquisite.core.engines;

import org.exquisite.core.conflictsearch.MergeXPlain;
import org.exquisite.core.solver.ISolver;

/**
 * Test diagnosis search with HSTree in combination with MergeXPlain as conflict searcher.
 *
 * @author wolfi
 */
public class TestHSTreeEngineWithMergeXplain extends AbstractDiagnosisEngineTest {
    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        return new HSTreeEngine<>(solver, new MergeXPlain<>(solver));
    }
}
