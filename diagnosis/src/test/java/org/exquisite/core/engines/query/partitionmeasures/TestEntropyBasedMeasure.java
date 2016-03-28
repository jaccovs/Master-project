package org.exquisite.core.engines.query.partitionmeasures;

import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.partitionmeasures.EntropyBasedMeasure;
import org.exquisite.core.query.partitionmeasures.IQPartitionRequirementsMeasure;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by wolfi on 26.03.2016.
 */
public class TestEntropyBasedMeasure extends AbstractTestPartitionRequirementsMeasure {

    @Override
    public IQPartitionRequirementsMeasure getMeasure() {
        return new EntropyBasedMeasure<>(new BigDecimal("0.05"));
    }

    @Override
    public void testUpdateBest() {
        /*
        D1.setMeasure(new BigDecimal(0.14));
        D2.setMeasure(new BigDecimal(0.1));
        D3.setMeasure(new BigDecimal(0.3));
        D4.setMeasure(new BigDecimal(0.4));
        D5.setMeasure(new BigDecimal(0.05));
        D6.setMeasure(new BigDecimal(0.01));
        */

        // test empty
        QPartition<Integer> emptyP = new QPartition<>(getSet(),getSet(),getSet(),null);
        assertEquals(emptyP, this.rm.updateBest(emptyP,emptyP));

        // each child of root must be better than root
        for (QPartition<Integer> p : sucs) {
            assertEquals(p, this.rm.updateBest(p, root));
            assertEquals(p, this.rm.updateBest(root, p));
        }

        QPartition<Integer> pBest = root;
        for (QPartition<Integer> p : sucs)
            pBest = this.rm.updateBest(p, pBest);

        assertEquals(new QPartition<>(getSet(D4),getSet(D1,D2,D3,D5,D6),getSet(),null),pBest);
        assertTrue(new BigDecimal("0.4").compareTo(pBest.probDx) == 0);
        assertTrue(new BigDecimal("0.6").compareTo(pBest.probDnx) == 0);

        QPartition<Integer> betterP = new QPartition<>(getSet(D4,D6),getSet(D1,D2,D3,D5),getSet(),null);
        assertTrue(new BigDecimal("0.41").compareTo(betterP.probDx) == 0);
        assertTrue(new BigDecimal("0.59").compareTo(betterP.probDnx) == 0);
        pBest = this.rm.updateBest(pBest, betterP);
        assertEquals(betterP, pBest);

        betterP = new QPartition<>(getSet(D1,D4),getSet(D2,D3,D5,D6),getSet(),null);
        assertTrue(new BigDecimal("0.54").compareTo(betterP.probDx) == 0);
        assertTrue(new BigDecimal("0.46").compareTo(betterP.probDnx) == 0);
        pBest = this.rm.updateBest(pBest, betterP);
        assertEquals(betterP, pBest);

        betterP = new QPartition<>(getSet(D1,D3,D5,D6),getSet(D2,D4),getSet(),null);
        assertTrue(HALF.compareTo(betterP.probDx) == 0);
        assertTrue(HALF.compareTo(betterP.probDnx) == 0);
        pBest = this.rm.updateBest(pBest, betterP);
        assertEquals(betterP, pBest);

        QPartition<Integer> equalP = new QPartition<>(getSet(D2,D4),getSet(D1,D3,D5,D6),getSet(),null);

        // check these two have same entropy
        assertTrue(HALF.compareTo(equalP.probDx) == 0);
        assertTrue(HALF.compareTo(equalP.probDnx) == 0);
        assertTrue(HALF.compareTo(pBest.probDx) == 0);
        assertTrue(HALF.compareTo(pBest.probDnx) == 0);

        // check that 2nd parameter partition is preferred on equal partitions
        assertEquals(pBest, this.rm.updateBest(equalP, pBest));
        assertEquals(equalP, this.rm.updateBest(pBest, equalP));


        QPartition<Integer> worseP = new QPartition<>(getSet(D2),getSet(D1,D3,D4,D5,D6),getSet(),null);
        assertTrue(worseP.probDx.compareTo(pBest.probDx) < 0);
        assertEquals(pBest, this.rm.updateBest(worseP, pBest));
        assertEquals(pBest, this.rm.updateBest(pBest, worseP));
    }

    @Override
    public void testIsOptimal() {

        /*
        D1.setMeasure(new BigDecimal(0.14));
        D2.setMeasure(new BigDecimal(0.1));
        D3.setMeasure(new BigDecimal(0.3));
        D4.setMeasure(new BigDecimal(0.4));
        D5.setMeasure(new BigDecimal(0.05));
        D6.setMeasure(new BigDecimal(0.01));
        */

        QPartition<Integer> emptyP = new QPartition<>(getSet(),getSet(),getSet(),null);
        assertFalse(rm.isOptimal(emptyP));

        assertFalse(rm.isOptimal(root));

        // each child of root must be better than root
        for (QPartition<Integer> p : sucs)
            assertFalse(rm.isOptimal(p));

        // threshold 0.05
        assertFalse(rm.isOptimal(new QPartition(getSet(D4,D6),getSet(D1,D2,D3,D5),getSet(),null)));
        assertTrue(rm.isOptimal(new QPartition(getSet(D4,D5),getSet(D1,D2,D3,D6),getSet(),null)));
        assertTrue(rm.isOptimal(new QPartition(getSet(D4,D5,D6),getSet(D1,D2,D3),getSet(),null)));
        assertTrue(rm.isOptimal(new QPartition(getSet(D2,D4,D5),getSet(D1,D3,D6),getSet(),null)));
    }

    @Override
    public void testPrune() {
        /*
        D1.setMeasure(new BigDecimal(0.14));
        D2.setMeasure(new BigDecimal(0.1));
        D3.setMeasure(new BigDecimal(0.3));
        D4.setMeasure(new BigDecimal(0.4));
        D5.setMeasure(new BigDecimal(0.05));
        D6.setMeasure(new BigDecimal(0.01));
        */

        assertFalse(rm.prune(root,null));

        for (QPartition<Integer> p : sucs)
            assertFalse(rm.prune(p, null));

        assertFalse(rm.prune(new QPartition(getSet(D4,D6),getSet(D1,D2,D3,D5),getSet(),null),null));
        assertFalse(rm.prune(new QPartition(getSet(D4,D5),getSet(D1,D2,D3,D6),getSet(),null),null));
        assertFalse(rm.prune(new QPartition(getSet(D4,D5,D6),getSet(D1,D2,D3),getSet(),null),null));
        assertTrue(rm.prune(new QPartition(getSet(D2,D4),getSet(D1,D3,D5,D6),getSet(),null),null));
        assertTrue(rm.prune(new QPartition(getSet(D2,D4,D5),getSet(D1,D3,D6),getSet(),null),null));

        assertTrue(rm.prune(new QPartition(getSet(D1,D2,D3,D4,D5,D6),getSet(),getSet(),null),null));


    }

    @Override
    public void getHeuristics() {

        /*
        D1.setMeasure(new BigDecimal(0.14));
        D2.setMeasure(new BigDecimal(0.1));
        D3.setMeasure(new BigDecimal(0.3));
        D4.setMeasure(new BigDecimal(0.4));
        D5.setMeasure(new BigDecimal(0.05));
        D6.setMeasure(new BigDecimal(0.01));
        */

        // each child of root must be better than root
        for (QPartition<Integer> p : sucs)
            assertTrue(rm.getHeuristics(p).compareTo(rm.getHeuristics(root)) < 0);

        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D1),getSet(D2,D3,D4,D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D2),getSet(D1,D3,D4,D5,D6),getSet(),null))) < 0);
        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D2),getSet(D1,D3,D4,D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D3),getSet(D1,D2,D4,D5,D6),getSet(),null))) > 0);
        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D3),getSet(D1,D2,D4,D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D4),getSet(D1,D2,D3,D5,D6),getSet(),null))) > 0);
        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D4),getSet(D1,D2,D3,D5,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D5),getSet(D1,D2,D3,D4,D6),getSet(),null))) < 0);
        assertTrue(rm.getHeuristics(new QPartition<Integer>(getSet(D5),getSet(D1,D2,D3,D4,D6),getSet(),null))
                .compareTo(rm.getHeuristics(new QPartition<Integer>(getSet(D6),getSet(D1,D2,D3,D4,D5),getSet(),null))) < 0);

    }

}
