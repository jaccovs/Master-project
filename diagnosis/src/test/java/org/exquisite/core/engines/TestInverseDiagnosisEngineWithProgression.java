package org.exquisite.core.engines;

import org.exquisite.core.conflictsearch.Progression;
import org.exquisite.core.solver.ISolver;

/**
 * @author wolfi
 */
public class TestInverseDiagnosisEngineWithProgression extends AbstractDiagnosisEngineTest {

    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        IDiagnosisEngine diagnosisEngine = new InverseDiagnosisEngine<>(solver, new Progression<>(solver));
        diagnosisEngine.setMaxNumberOfDiagnoses(10);
        return diagnosisEngine;
    }
}
