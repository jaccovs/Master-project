package org.exquisite.core.engines.query.querycomputation;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.perfmeasures.PerfMeasurementManager;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.SimpleQueryComputation;
import org.exquisite.core.query.querycomputation.SimpleRecursiveQueryComputation;
import org.exquisite.core.query.scoring.MinScoreQSS;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.exquisite.core.TestUtils.getSet;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.COUNTER_INTERACTIVE_PARTITIONS;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.getCounter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the SimpleQueryComputation class
 */
public class TestSimpleQueryComputation {
    @Before
    public void init() {
        PerfMeasurementManager.reset();
    }

    @Test
    public void testSimpleQueryComputation() throws DiagnosisException {
        SimpleQueryComputation<Integer> sqc = new SimpleQueryComputation<>(new MinScoreQSS<>());
        computeQueries(sqc);
    }

    @Test
    public void testSimpleRecursiveQueryComputation() throws DiagnosisException {
        SimpleQueryComputation<Integer> sqc = new SimpleRecursiveQueryComputation<>(new MinScoreQSS<>());
        computeQueries(sqc);
    }

    private void computeQueries(SimpleQueryComputation<Integer> sqc) throws DiagnosisException {
        // set a threshold that guarantee acceptance of all diagnoses
        sqc.setThreshold(2);
        sqc.initialize(getSet(getDiagnosis(1), getDiagnosis(2), getDiagnosis(3), getDiagnosis(4)));
        assertTrue(sqc.hasNext());
        Query<Integer> query = sqc.next();
        assertEquals(Collections.singleton(1), query.formulas);

        assertEquals(14, getCounter(COUNTER_INTERACTIVE_PARTITIONS).value());
    }

}
