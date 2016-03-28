package org.exquisite.core.engines.query.partitionmeasures;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.partitionmeasures.IQPartitionRequirementsMeasure;
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
 * Created by wolfi on 26.03.2016.
 */
public abstract class AbstractTestPartitionRequirementsMeasure {

    protected static QPartition<Integer> root;
    protected static Diagnosis<Integer> D1, D2, D3, D4, D5, D6;
    protected static Collection<QPartition<Integer>> sucs = new HashSet<>();
    protected static double delta = 0.000000000001;
    protected static BigDecimal HALF = new BigDecimal("0.5");
    IQPartitionRequirementsMeasure rm = getMeasure();

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

        double d1Measure = D1.getMeasure().doubleValue();
        double d2Measure = D2.getMeasure().doubleValue();
        double d3Measure = D3.getMeasure().doubleValue();
        double d4Measure = D4.getMeasure().doubleValue();
        double d5Measure = D5.getMeasure().doubleValue();
        double d6Measure = D6.getMeasure().doubleValue();

        assertEquals(0.14, d1Measure, delta);

        root = new QPartition(getSet(), getSet(D1,D2,D3,D4,D5,D6), getSet(), null);
        sucs = root.computeSuccessors();

        for (QPartition<Integer> p: sucs) {
            assertTrue(p.probDx.compareTo(BigDecimal.ZERO) > 0);
            assertTrue(p.probDnx.compareTo(BigDecimal.ZERO) > 0);

            assertTrue(BigDecimal.ONE.compareTo(p.probDx.add(p.probDnx)) == 0);
            assertEquals(1, p.dx.size());
            assertEquals(5, p.dnx.size());
        }
    }

    public abstract IQPartitionRequirementsMeasure getMeasure();

    @Test
    public abstract void testUpdateBest();

    @Test
    public abstract void testIsOptimal();

    @Test
    public abstract void testPrune();

    @Test
    public abstract void getHeuristics();
}
