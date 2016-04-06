package org.exquisite.core.engines;

import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.solver.ISolver;
import org.junit.Ignore;

/**
 *
 * @author wolfi
 */
@Ignore public class TestHSDAGEngine extends AbstractDiagnosisEngineTest {

    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        return new HSDAGEngine<>(solver);
    }
}
