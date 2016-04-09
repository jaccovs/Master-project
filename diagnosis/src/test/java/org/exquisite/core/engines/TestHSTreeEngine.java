package org.exquisite.core.engines;

import org.exquisite.core.solver.ISolver;

/**
 * Automated tests of the HSTreeEngine class
 */
public class TestHSTreeEngine extends AbstractDiagnosisEngineTest {

    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        return new HSTreeEngine<>(solver);
    }
}
