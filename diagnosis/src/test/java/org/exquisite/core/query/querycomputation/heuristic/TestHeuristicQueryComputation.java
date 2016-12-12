package org.exquisite.core.query.querycomputation.heuristic;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.costestimators.FormulaWeightsCostEstimator;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.QPartitionOperations;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.EntropyBasedMeasure;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.RiskOptimizationMeasure;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.SplitInHalfMeasure;
import org.exquisite.core.solver.SimpleConflictSubsetSolver;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.*;

/**
 * A general test case for Heuristic query computation.
 *
 * @author wolfi
 */
public class TestHeuristicQueryComputation {

    private AbstractDiagnosisEngine<Integer> engine;

    private final Logger logger = LoggerFactory.getLogger(TestHeuristicQueryComputation.class);

    /**
     * Standard threshold value for ENT, SPL and RIO
     */
    private static final BigDecimal THRESHOLD = new BigDecimal("0.05");

    /**
     * min/max threshold used in test for multiple thresholds for ENT,SPL and RIO
     *
     * TODO ENT: ab 0.5 bekommt man die Rootpartition! 0..49 OK
     * TODO SPL: bei 0.0 und 1.0 bekommt man die Rootpartition, 1..99 OK!
     * TODO RIO: bei 0.0 und ab 0.67 (cautious) bekommt man die Rootpartition!
     * TODO Threshold - Werte von 0.01 bis 0.49 sind für alle kein Problem
     */
    private static final int MIN = 1, MAX = 49;

    @Before
    public void initialize() {
        Set<Integer> domain = getSet(1, 2, 3, 4, 5);

        Map<Integer, Float> formulaWeights = new HashMap<>();
        for (int i = 1; i <= domain.size(); i++) formulaWeights.put(i,(float)1/domain.size());

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(domain);
        model.setFormulaWeights(formulaWeights);

        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(1, 3));
        conflicts.add(getSet(1, 4));
        conflicts.add(getSet(2, 3));
        conflicts.add(getSet(5));

        SimpleConflictSubsetSolver solver = new SimpleConflictSubsetSolver(model, domain, conflicts);
        engine = new HSTreeEngine<>(solver);
        engine.setCostsEstimator(new FormulaWeightsCostEstimator<>(model.getPossiblyFaultyFormulas(), model.getFormulaWeights()));
    }

    @Test
    public void testCalculateDiagnoses() {
        try {
            Set<Diagnosis<Integer>> diagnoses = engine.calculateDiagnoses();
            assertNotNull(diagnoses);
            assertTrue(!diagnoses.isEmpty());

            Set<Diagnosis<Integer>> expectedDiagnoses = getSet(
                    new Diagnosis<>(getSet(1, 2, 5)),
                    new Diagnosis<>(getSet(1, 3, 5)),
                    new Diagnosis<>(getSet(3, 4, 5))
            );

            assertEquals(expectedDiagnoses, diagnoses);
        } catch (DiagnosisException e) {
            fail();
        }
    }

    @Test
    public void testFindQPartitionENTEqualWeights() {
        HeuristicQueryComputation<Integer> gc = new HeuristicQueryComputation<>(new HeuristicConfiguration<>(engine, new EntropyBasedMeasure<>(THRESHOLD), logger));
        testFindQPartition(gc, THRESHOLD.toString());
    }

    @Test(timeout=2000)
    public void testFindQPartitionENTUnEqualWeights() {
        Map<Integer, Float> formulaWeights = engine.getSolver().getDiagnosisModel().getFormulaWeights();
        for (Integer i: formulaWeights.keySet()) formulaWeights.put(i,(1f/(float)(i+1)));

        HeuristicQueryComputation<Integer> gc = new HeuristicQueryComputation<>(new HeuristicConfiguration<>(engine, new EntropyBasedMeasure<>(THRESHOLD), logger));
        testFindQPartition(gc, THRESHOLD.toString());
    }

    @Test
    public void testFindQPartitionSPL() {
        HeuristicQueryComputation<Integer> gc = new HeuristicQueryComputation<>(new HeuristicConfiguration<>(engine, new SplitInHalfMeasure<>(THRESHOLD), logger));
        testFindQPartition(gc, THRESHOLD.toString());
    }

    @Test
    public void testFindQPartitionRIO() {
        HeuristicQueryComputation<Integer> gc = new HeuristicQueryComputation<>(new HeuristicConfiguration<>(engine, new RiskOptimizationMeasure<>(THRESHOLD, THRESHOLD, THRESHOLD), logger));
        testFindQPartition(gc, THRESHOLD.toString());
    }

    @Test
    public void testFindQPartitionENTManyThresholds() {
        for (int i = MIN; i <= MAX; i++) {
            BigDecimal tm = new BigDecimal(""+((((double) i)/100.0)*100)/100);
            HeuristicQueryComputation<Integer> gc = new HeuristicQueryComputation<>(new HeuristicConfiguration<>(engine, new EntropyBasedMeasure<>(tm), logger));
            testFindQPartition(gc, tm.toString());
        }
    }

    @Test
    public void testFindQPartitionSPLManyThresholds() {
        for (int i = MIN; i <= MAX; i++) {
            BigDecimal tm = new BigDecimal(""+((((double) i)/100.0)*100)/100);
            HeuristicQueryComputation<Integer> gc = new HeuristicQueryComputation<>(new HeuristicConfiguration<>(engine, new SplitInHalfMeasure<>(tm), logger));
            testFindQPartition(gc, tm.toString());
        }
    }

    @Test
    public void testFindQPartitionRIOManyThresholds() {
        for (int i = MIN; i <= MAX; i++) {
            for (int j = MIN; j <= MAX; j++) {
                for (int k = MIN; k <= MAX; k++) {
                    BigDecimal entropyThreshold = new BigDecimal(""+((((double) i)/100.0)*100)/100);
                    BigDecimal cardinalityThreshold = new BigDecimal(""+((((double) j)/100.0)*100)/100);
                    BigDecimal cautious = new BigDecimal(""+((((double) k)/100.0)*100)/100);
                    HeuristicQueryComputation<Integer> gc = new HeuristicQueryComputation<>(new HeuristicConfiguration<>(engine, new RiskOptimizationMeasure<>(entropyThreshold,cardinalityThreshold, cautious), logger));
                    testFindQPartition(gc, entropyThreshold.toString()+'/'+cardinalityThreshold.toString()+'/'+cautious.toString());
                }
            }
        }
    }

    private void testFindQPartition(HeuristicQueryComputation<Integer> gc, String threshold) {

        /**
         * do not calculate diagnoses again and again.
         */
        Diagnosis<Integer> D1 = new Diagnosis<>(getSet(1, 2, 5));
        Diagnosis<Integer> D2 = new Diagnosis<>(getSet(1, 3, 5));
        Diagnosis<Integer> D3 = new Diagnosis<>(getSet(3, 4, 5));

        /*
        D1.setMeasure(new BigDecimal("0.25"));
        D2.setMeasure(new BigDecimal("0.40"));
        D3.setMeasure(new BigDecimal("0.35"));
        */

        Set<Diagnosis<Integer>> diagnoses = getSet(D1,D2,D3);

        //Set<Diagnosis<Integer>> diagnoses = gc.getDiagnosisEngine().calculateDiagnoses();

        QPartition rootPartition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), this.engine.getCostsEstimator());

        QPartition<Integer> qPartition = QPartitionOperations.findQPartition(diagnoses, gc.getConfig().getRm(), this.engine.getCostsEstimator()); //gc.findQPartition(diagnoses, gc.getPartitionRequirementsMeasure());

        assertNotNull("qPartition is null for threshold " + threshold,qPartition);
        assertTrue("sum of probDx and probDnx does not equal one for threshold " + threshold, BigDecimal.ONE.compareTo(qPartition.probDx.add(qPartition.probDnx)) == 0);
        assertFalse("optimal q-partition must not equal the root partition for threshold " + threshold, qPartition.equals(rootPartition));

    }

}
