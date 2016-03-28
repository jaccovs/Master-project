package org.exquisite.core.engines.query.qc.heuristic.scenario1;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.query.HeuristicQC;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.partitionmeasures.SplitInHalfMeasure;
import org.junit.Test;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by wolfi on 25.03.2016.
 */
public class SPLScenario1 extends TestScenario1 {

    public BigDecimal tEnt = BigDecimal.ZERO;

    @Override
    public HeuristicQC<Integer> getHeuristicQC() {
        return new HeuristicQC<>(new SplitInHalfMeasure<>(tEnt), getEngine());
    }

    @Test
    public void testFindQPartition() {
        try {
            QPartition<Integer> qPartition = qc.findQPartition(calculateDiagnoses(), qc.getPartitionRequirementsMeasure());
            //QPartition<Integer> expectedP = new QPartition<>(getSet(D1,D2,D3), getSet(D4,D5,D6), getSet(), getEngine().getCostsEstimator());
            QPartition<Integer> expectedP = new QPartition<>(getSet(D3,D4,D5), getSet(D1,D2,D6), getSet(), getEngine().getCostsEstimator());
            assertEquals(expectedP, qPartition);
        } catch (DiagnosisException e) {
            fail();
        }
    }
}
