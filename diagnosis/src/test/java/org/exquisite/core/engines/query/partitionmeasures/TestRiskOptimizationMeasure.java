package org.exquisite.core.engines.query.partitionmeasures;

import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.partitionmeasures.IQPartitionRequirementsMeasure;
import org.exquisite.core.query.partitionmeasures.RiskOptimizationMeasure;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static org.exquisite.core.TestUtils.getSet;

/**
 * Created by wolfi on 27.03.2016.
 */
public class TestRiskOptimizationMeasure extends AbstractTestPartitionRequirementsMeasure {

    @Override
    public IQPartitionRequirementsMeasure getMeasure() {
        return new RiskOptimizationMeasure<>(new BigDecimal("0.05"), new BigDecimal("0.0"), new BigDecimal("0.4"));
    }

    @Override
    public void testUpdateBest() {
        // each child of root must be better than root
        /*
        for (QPartition<Integer> p : sucs) {
            assertEquals(p, this.rm.updateBest(p, root));
            assertEquals(p, this.rm.updateBest(root, p));
        }
        */

        QPartition p1 = new QPartition(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null);
        QPartition p2 = new QPartition(getSet(D1,D2,D3,D4),getSet(D5,D6),getSet(),null);

        assertEquals(p1, rm.updateBest(root,p1));
        assertEquals(p2, rm.updateBest(root,p2));
        assertEquals(p1, rm.updateBest(p1,p2));
        assertEquals(p1, rm.updateBest(p2,p1));
    }

    @Override
    public void testIsOptimal() {

    }

    @Override
    public void testPrune() {

    }

    @Override
    public void getHeuristics() {

    }
}
