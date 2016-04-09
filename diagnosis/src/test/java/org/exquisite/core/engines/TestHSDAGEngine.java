package org.exquisite.core.engines;

import org.exquisite.core.solver.ISolver;

/**
 *
 * @author wolfi
 */
public class TestHSDAGEngine extends AbstractDiagnosisEngineTest {

    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        return new HSDAGEngine<>(solver);
    }
}
