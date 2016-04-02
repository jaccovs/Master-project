package org.exquisite.core.engines.query.qc.heuristic.partitionmeasures;

import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.SplitInHalfMeasure;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.*;

/**
 * A test case for SplitInHalfMeasure Requirements measure.
 *
 * @author wolfi
 */
public class TestSplitInHalfMeasure extends AbstractTestPartitionRequirementsMeasure {

    private IQPartitionRequirementsMeasure<Integer> rm = getMeasure();

    @Override
    public IQPartitionRequirementsMeasure<Integer> getMeasure() {
        return new SplitInHalfMeasure<>(new BigDecimal("0.00"));
    }

    @Override
    public void testUpdateBest() {
        for (QPartition<Integer> p : sucs) {
            assertEquals(p, this.rm.updateBest(p, root));
            assertEquals(p, this.rm.updateBest(root, p));
        }
    }

    @Override
    public void testIsOptimal() {
        assertTrue(rm.isOptimal(new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null)));
        assertFalse(rm.isOptimal(new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5),getSet(),null)));
        assertFalse(rm.isOptimal(new QPartition<>(getSet(D1,D2),getSet(D3,D4,D5,D6),getSet(),null)));
        assertFalse(rm.isOptimal(new QPartition<>(getSet(),getSet(D1,D2,D3,D4,D5,D6),getSet(),null)));
        assertFalse(rm.isOptimal(new QPartition<>(getSet(D1,D2,D3,D4,D5,D6),getSet(),getSet(),null)));
        assertTrue(rm.isOptimal(new QPartition<>(getSet(),getSet(),getSet(),null)));
    }

    @Override
    public void testPrune() {
        assertTrue(rm.prune(new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null),null));
        assertTrue(rm.prune(new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5),getSet(),null),null));
        assertFalse(rm.prune(new QPartition<>(getSet(D1,D2),getSet(D3,D4,D5,D6),getSet(),null),null));
        assertFalse(rm.prune(new QPartition<>(getSet(),getSet(D1,D2,D3,D4,D5,D6),getSet(),null),null));
        assertTrue(rm.prune(new QPartition<>(getSet(D1,D2,D3,D4,D5,D6),getSet(),getSet(),null),null));
        assertTrue(rm.prune(new QPartition<>(getSet(),getSet(),getSet(),null),null));
    }

    @Override
    public void testGetHeuristics() {

        assertTrue(BigDecimal.ZERO.compareTo(rm.getHeuristics(new QPartition<>(getSet(),getSet(),getSet(),null))) == 0);

        for (QPartition<Integer> p : sucs)
            assertTrue(rm.getHeuristics(p).compareTo(rm.getHeuristics(root)) < 0);

        assertTrue(rm.getHeuristics(new QPartition<>(getSet(D1,D2),getSet(D3,D4,D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<>(getSet(D1),getSet(D2,D3,D4,D5,D6),getSet(),null))) < 0);

        assertTrue(rm.getHeuristics(new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<>(getSet(D1,D2),getSet(D3,D4,D5,D6),getSet(),null))) < 0);

        assertTrue(rm.getHeuristics(new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<>(getSet(D4,D5,D6),getSet(D1,D2,D3),getSet(),null))) == 0);

        assertTrue(rm.getHeuristics(new QPartition<>(getSet(D1,D2,D3,D4),getSet(D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null))) > 0);


        // uneven elements
        assertTrue(rm.getHeuristics(new QPartition<>(getSet(),getSet(D1,D2,D3,D4,D5),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<>(getSet(D1),getSet(D2,D3,D4,D5),getSet(),null))) > 0);

        assertTrue(rm.getHeuristics(new QPartition<>(getSet(D1,D2),getSet(D3,D4,D5),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<>(getSet(D1),getSet(D2,D3,D4,D5),getSet(),null))) < 0);

        assertTrue(rm.getHeuristics(new QPartition<>(getSet(D1,D2),getSet(D3,D4,D5),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<>(getSet(D1,D2,D3),getSet(D4,D5),getSet(),null))) == 0);

    }

}
