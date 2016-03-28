package org.exquisite.core.engines.query.partitionmeasures;

import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.partitionmeasures.IQPartitionRequirementsMeasure;
import org.exquisite.core.query.partitionmeasures.SplitInHalfMeasure;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by wolfi on 27.03.2016.
 */
public class TestSplitInHalfMeasure extends AbstractTestPartitionRequirementsMeasure {

    IQPartitionRequirementsMeasure rm = getMeasure();

    @Override
    public IQPartitionRequirementsMeasure getMeasure() {
        return new SplitInHalfMeasure<>(new BigDecimal("0.05"));
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

    }

    @Override
    public void testPrune() {

    }

    @Override
    public void getHeuristics() {

        assertTrue(BigDecimal.ZERO.compareTo(rm.getHeuristics(new QPartition(getSet(),getSet(),getSet(),null))) == 0);

        for (QPartition<Integer> p : sucs)
            assertTrue(rm.getHeuristics(p).compareTo(rm.getHeuristics(root)) < 0);

        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D1,D2),getSet(D3,D4,D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D1),getSet(D2,D3,D4,D5,D6),getSet(),null))) < 0);

        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D1,D2),getSet(D3,D4,D5,D6),getSet(),null))) < 0);

        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D4,D5,D6),getSet(D1,D2,D3),getSet(),null))) == 0);

        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D1,D2,D3,D4),getSet(D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D1,D2,D3),getSet(D4,D5,D6),getSet(),null))) > 0);


        // uneven elements
        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(),getSet(D1,D2,D3,D4,D5),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D1),getSet(D2,D3,D4,D5),getSet(),null))) > 0);

        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D1,D2),getSet(D3,D4,D5),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D1),getSet(D2,D3,D4,D5),getSet(),null))) < 0);

        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D1,D2),getSet(D3,D4,D5),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D1,D2,D3),getSet(D4,D5),getSet(),null))) == 0);

    }
}
