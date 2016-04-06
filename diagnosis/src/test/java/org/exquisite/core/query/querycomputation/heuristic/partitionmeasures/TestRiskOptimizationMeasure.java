package org.exquisite.core.query.querycomputation.heuristic.partitionmeasures;

import org.exquisite.core.query.QPartition;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A test case for RiskOptimizationMeasure.
 *
 * @author wolfi
 */
public class TestRiskOptimizationMeasure extends AbstractTestPartitionRequirementsMeasure {

    @Override
    public IQPartitionRequirementsMeasure<Integer> getMeasure() {
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

        QPartition<Integer> p1 = new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null);
        QPartition<Integer> p2 = new QPartition<>(getSet(D1,D2,D3,D4),getSet(D5,D6),getSet(),null);

        assertEquals(p1, rm.updateBest(root,p1));
        assertEquals(p2, rm.updateBest(root,p2));
        assertEquals(p1, rm.updateBest(p1,p2));
        assertEquals(p1, rm.updateBest(p2,p1));
    }

    @Override
    public void testIsOptimal() {
        QPartition<Integer> p1 = new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null);
        QPartition<Integer> p2 = new QPartition<>(getSet(D1,D2,D3,D4),getSet(D5,D6),getSet(),null);
        assertTrue(rm.isOptimal(p1));
        assertFalse(rm.isOptimal(p2));
        assertFalse(rm.isOptimal(root));
        for (QPartition<Integer> p : sucs) assertFalse(rm.isOptimal(p));

    }

    @Override
    public void testPrune() {
        QPartition<Integer> p1 = new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null);
        QPartition<Integer> p2 = new QPartition<>(getSet(D1,D2,D3,D4),getSet(D5,D6),getSet(),null);
        assertTrue(rm.prune(p1,p2));
        assertTrue(rm.prune(p2,p1));
        assertFalse(rm.prune(root,root));
        for (QPartition<Integer> p : sucs) assertFalse(rm.prune(p, root));

        assertTrue(rm.prune(p1,root));
        assertFalse(rm.prune(p2,root));
    }

    @Override
    public void testGetHeuristics() {

    }

}
