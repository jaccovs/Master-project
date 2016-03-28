package org.exquisite.core.engines.query.qc.heuristic.scenario1;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.query.HeuristicQC;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.partitionmeasures.EntropyBasedMeasure;
import org.junit.Test;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by wolfi on 25.03.2016.
 */
public class ENTScenario1 extends TestScenario1 {

    public BigDecimal tEnt = new BigDecimal("0.05");

    @Override
    public HeuristicQC<Integer> getHeuristicQC() {
        return new HeuristicQC<>(new EntropyBasedMeasure<>(tEnt), getEngine());
    }

    @Test
    public void testFindQPartition() {
        try {

            QPartition<Integer> qPartition = qc.findQPartition(calculateDiagnoses(), qc.getPartitionRequirementsMeasure());
            QPartition<Integer> expectedP = new QPartition<>(getSet(D5,D4), getSet(D1,D2,D3,D6), getSet(), getEngine().getCostsEstimator());
            assertEquals(expectedP, qPartition);
        } catch (DiagnosisException e) {
            fail();
        }
    }
}
