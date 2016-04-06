package org.exquisite.core.engines;

import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.solver.ISolver;

/**
 * @author wolfi
 */
public class TestInverseDiagnosisEngine extends AbstractDiagnosisEngineTest {

    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        IDiagnosisEngine diagnosisEngine = new InverseDiagnosisEngine<>(solver);
        diagnosisEngine.setMaxNumberOfDiagnoses(10);
        return diagnosisEngine;
    }
}
