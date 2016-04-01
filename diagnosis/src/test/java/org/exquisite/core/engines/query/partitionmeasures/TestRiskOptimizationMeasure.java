package org.exquisite.core.engines.query.partitionmeasures;

import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.partitionmeasures.IQPartitionRequirementsMeasure;
import org.exquisite.core.query.partitionmeasures.RiskOptimizationMeasure;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        QPartition p1 = new QPartition(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null);
        QPartition p2 = new QPartition(getSet(D1,D2,D3,D4),getSet(D5,D6),getSet(),null);
        assertTrue(rm.isOptimal(p1));
        assertFalse(rm.isOptimal(p2));
        assertFalse(rm.isOptimal(root));
        for (QPartition<Integer> p : sucs) assertFalse(rm.isOptimal(p));

    }

    @Override
    public void testPrune() {
        QPartition p1 = new QPartition(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null);
        QPartition p2 = new QPartition(getSet(D1,D2,D3,D4),getSet(D5,D6),getSet(),null);
        assertTrue(rm.prune(p1,p2));
        assertTrue(rm.prune(p2,p1));
        assertFalse(rm.prune(root,root));
        for (QPartition<Integer> p : sucs) assertFalse(rm.prune(p, root));

        assertTrue(rm.prune(p1,root));
        assertFalse(rm.prune(p2,root));
    }

    @Override
    public void getHeuristics() {

    }

    @Test
    public void testGetSizeOfD() {
        assertEquals(new Double(6), RiskOptimizationMeasure.getSizeOfD(root));
        for (QPartition<Integer> p : sucs) assertEquals(new Double(6), RiskOptimizationMeasure.getSizeOfD(p));
        assertEquals(new Double(0), RiskOptimizationMeasure.getSizeOfD(new QPartition<>(getSet(),getSet(),getSet(),null)));
        assertEquals(new Double(1), RiskOptimizationMeasure.getSizeOfD(new QPartition<>(getSet(D1),getSet(),getSet(),null)));
        assertEquals(new Double(1), RiskOptimizationMeasure.getSizeOfD(new QPartition<>(getSet(),getSet(D1),getSet(),null)));
        assertEquals(new Double(1), RiskOptimizationMeasure.getSizeOfD(new QPartition<>(getSet(),getSet(),getSet(D1),null)));
    }

    @Test
    public void testGetN() {
        int size = 6;
        for (int i = 0; i <= 10; i++) {
            double c = (double) i/10.0;
            assertEquals((int)Math.ceil(size*c), RiskOptimizationMeasure.getN(root,new BigDecimal(new Double(c).toString())));
        }
    }
}
