package org.exquisite.core.engines.query.qc.heuristic.partitionmeasures;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Abstract class for implementing test classes of the requirements measures.
 *
 * @author wolfi
 */
public abstract class AbstractTestPartitionRequirementsMeasure {

    static QPartition<Integer> root;
    protected static Diagnosis<Integer> D1, D2, D3, D4, D5, D6;
    static Collection<QPartition<Integer>> sucs = new HashSet<>();
    private static final double DELTA = 0.000000000001;
    static BigDecimal HALF = new BigDecimal("0.5");
    IQPartitionRequirementsMeasure<Integer> rm = getMeasure();

    @BeforeClass
    public static void init() {
        D1 = getDiagnosis(1);
        D2 = getDiagnosis(2,3);
        D3 = getDiagnosis(2,5);
        D4 = getDiagnosis(2,4);
        D5 = getDiagnosis(3,4,5);
        D6 = getDiagnosis(6);

        D1.setMeasure(new BigDecimal("0.14"));
        D2.setMeasure(new BigDecimal("0.1"));
        D3.setMeasure(new BigDecimal("0.3"));
        D4.setMeasure(new BigDecimal("0.4"));
        D5.setMeasure(new BigDecimal("0.05"));
        D6.setMeasure(new BigDecimal("0.01"));

        assertEquals(0.14, D1.getMeasure().doubleValue(), DELTA);
        assertEquals(0.1,  D2.getMeasure().doubleValue(), DELTA);
        assertEquals(0.3,  D3.getMeasure().doubleValue(), DELTA);
        assertEquals(0.4,  D4.getMeasure().doubleValue(), DELTA);
        assertEquals(0.05, D5.getMeasure().doubleValue(), DELTA);
        assertEquals(0.01, D6.getMeasure().doubleValue(), DELTA);

        root = new QPartition<>(getSet(), getSet(D1,D2,D3,D4,D5,D6), getSet(), null);
        sucs = root.computeSuccessors();

        for (QPartition<Integer> p: sucs) {
            assertTrue(p.probDx.compareTo(BigDecimal.ZERO) > 0);
            assertTrue(p.probDnx.compareTo(BigDecimal.ZERO) > 0);

            assertTrue(BigDecimal.ONE.compareTo(p.probDx.add(p.probDnx)) == 0);
            assertEquals(1, p.dx.size());
            assertEquals(5, p.dnx.size());
        }
    }

    public abstract IQPartitionRequirementsMeasure<Integer> getMeasure();

    @Test
    public abstract void testUpdateBest();

    @Test
    public abstract void testIsOptimal();

    @Test
    public abstract void testPrune();

    @Test
    public abstract void testGetHeuristics();
}
