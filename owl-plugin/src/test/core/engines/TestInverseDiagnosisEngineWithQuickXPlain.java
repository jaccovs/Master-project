package org.exquisite.core.engines;

import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.solver.ISolver;

/**
 * Test diagnosis search with Inverse Diagnosis Engine in combination with QuickXPlain as conflict searcher.
 *
 * @author wolfi
 */
public class TestInverseDiagnosisEngineWithQuickXPlain extends AbstractDiagnosisEngineTest {

    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        IDiagnosisEngine diagnosisEngine = new InverseDiagnosisEngine<>(solver, new QuickXPlain<>(solver));
        diagnosisEngine.setMaxNumberOfDiagnoses(10);
        return diagnosisEngine;
    }
}
