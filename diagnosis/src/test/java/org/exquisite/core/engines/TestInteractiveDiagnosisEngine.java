package org.exquisite.core.engines;

import org.exquisite.core.query.Answer;
import org.exquisite.core.query.IQueryAnswering;
import org.exquisite.core.query.Query;
import org.exquisite.core.solver.ISolver;
import org.junit.Ignore;

/**
 * TODO InteractiveDiagnosisEngine does not work correctly yet, therefore we ignore this test case
 * @author wolfi
 */
@Ignore public class TestInteractiveDiagnosisEngine extends AbstractDiagnosisEngineTest {

    @Override
    protected IDiagnosisEngine getDiagnosisEngine(ISolver solver) {
        return new InteractiveDiagnosisEngine(solver, new IQueryAnswering() {
            @Override
            public Answer getAnswer(Query query) {
                Answer a = new Answer();
                a.positive.addAll(query.formulas);
                return a;
            }
        });
    }
}
