package org.exquisite.core.engines;

import org.exquisite.core.conflictsearch.MergeXPlain;
import org.exquisite.core.solver.ISolver;

/**
 * @author wolfi
 */
public class TestInverseDiagnosisEngineWithMergeXPlain extends AbstractDiagnosisEngineTest {

    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        IDiagnosisEngine diagnosisEngine = new InverseDiagnosisEngine<>(solver, new MergeXPlain<>(solver));
        diagnosisEngine.setMaxNumberOfDiagnoses(10);
        return diagnosisEngine;
    }

}
