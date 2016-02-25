package org.exquisite.core.engines.query;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.query.scoring.MinScoreQSS;
import org.exquisite.core.measurements.MeasurementManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.exquisite.core.TestUtils.getSet;
import static org.exquisite.core.measurements.MeasurementManager.COUNTER_INTERACTIVE_PARTITIONS;
import static org.exquisite.core.measurements.MeasurementManager.getCounter;
import static org.junit.Assert.assertTrue;

/**
 * Tests the SimpleQC class
 */
public class TestSimpleQueryComputation {
    @Before
    public void init() {
        MeasurementManager.reset();
    }

    @Test
    public void testSimpleQueryComputation() throws DiagnosisException {
        SimpleQC<Integer> sqc = new SimpleQC<>(new MinScoreQSS<>());
        computeQueries(sqc);
    }

    @Test
    public void testSimpleRecursiveQueryComputation() throws DiagnosisException {
        SimpleQC<Integer> sqc = new SimpleRecursiveQC<>(new MinScoreQSS<>());
        computeQueries(sqc);
    }

    private void computeQueries(SimpleQC<Integer> sqc) throws DiagnosisException {
        // set a threshold that guarantee acceptance of all diagnoses
        sqc.setThreshold(2);
        sqc.initialize(getSet(getDiagnosis(1), getDiagnosis(2), getDiagnosis(3), getDiagnosis(4)));
        assertTrue(sqc.hasNext());
        Query<Integer> query = sqc.next();
        assertEquals(Collections.singleton(1), query.formulas);

        assertEquals(14, getCounter(COUNTER_INTERACTIVE_PARTITIONS).value());
    }

}
