package org.exquisite.core.engines.query.qc.heuristic;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.costestimators.FormulaWeightsCostEstimator;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.HeuristicQC;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.partitionmeasures.EntropyBasedMeasure;
import org.exquisite.core.query.partitionmeasures.RiskOptimizationMeasure;
import org.exquisite.core.query.partitionmeasures.SplitInHalfMeasure;
import org.exquisite.core.solver.SimpleConflictSubsetSolver;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.*;

/**
 * A general test case for Heuristic query computation.
 * <p>
 * Created by wolfi on 15.03.2016.
 */
public class TestHeuristicQC {

    private AbstractDiagnosisEngine<Integer> engine;

    private static final BigDecimal THRESHOLD = new BigDecimal("0.15"); // TODO teste unterschiedliche Thresholds! 0.05 Endlosschleife, 0.5 bekommt man die Rootpartition

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
                    new Diagnosis<Integer>(getSet(1, 2, 5)),
                    new Diagnosis<Integer>(getSet(1, 3, 5)),
                    new Diagnosis<Integer>(getSet(3, 4, 5))
            );

            assertEquals(expectedDiagnoses, diagnoses);
        } catch (DiagnosisException e) {
            fail();
        }
    }

    @Test
    public void testFindQPartitionENTEqualWeights() {
        HeuristicQC<Integer> gc = new HeuristicQC<>(new EntropyBasedMeasure<>(THRESHOLD),engine);
        testFindQPartition(gc);
    }

    @Test
    public void testFindQPartitionENTUnEqualWeights() {
        Map<Integer, Float> formulaWeights = engine.getSolver().getDiagnosisModel().getFormulaWeights();
        for (Integer i: formulaWeights.keySet()) formulaWeights.put(i,(float)i);

        HeuristicQC<Integer> gc = new HeuristicQC<>(new EntropyBasedMeasure<>(THRESHOLD),(AbstractDiagnosisEngine) engine);
        testFindQPartition(gc);
    }

    @Test
    public void testFindQPartitionSPL() {
        HeuristicQC<Integer> gc = new HeuristicQC<>(new SplitInHalfMeasure<>(THRESHOLD),(AbstractDiagnosisEngine) engine);
        testFindQPartition(gc);
    }

    @Test
    public void testFindQPartitionRIO() {
        HeuristicQC<Integer> gc = new HeuristicQC<>(new RiskOptimizationMeasure<>(THRESHOLD, THRESHOLD, THRESHOLD),(AbstractDiagnosisEngine) engine);
        testFindQPartition(gc);
    }

    private void testFindQPartition(HeuristicQC<Integer> gc) {
        try {
            Set<Diagnosis<Integer>> diagnoses = gc.getDiagnosisEngine().calculateDiagnoses();
            QPartition rootPartition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), this.engine.getCostsEstimator());

            QPartition<Integer> qPartition = gc.findQPartition(diagnoses, gc.getPartitionRequirementsMeasure());

            assertNotNull(qPartition);
            assertTrue(BigDecimal.ONE.compareTo(qPartition.probDx.add(qPartition.probDnx)) == 0);
            assertFalse("optimal q-partition must not equal the root partition", qPartition.equals(rootPartition));

        } catch (DiagnosisException e) {
            fail();
        }
    }

}
