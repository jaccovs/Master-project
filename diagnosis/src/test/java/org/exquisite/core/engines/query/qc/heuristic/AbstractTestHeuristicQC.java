package org.exquisite.core.engines.query.qc.heuristic;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.costestimators.FormulaWeightsCostEstimator;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.engines.HSTreeEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.HeuristicQC;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.solver.SimpleConflictSubsetSolver;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.*;

/**
 * Created by wolfi on 25.03.2016.
 */
public abstract class AbstractTestHeuristicQC {

    public HeuristicQC<Integer> qc;

    protected final static double delta = 0.000001;

    @Before
    public void initialize() {
        qc = getHeuristicQC();
    }

    public abstract Set<Diagnosis<Integer>> calculateDiagnoses() throws DiagnosisException;


    public AbstractDiagnosisEngine<Integer> getEngine() {
        Set<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        Map<Integer, Float> formulaWeights = new HashMap<>();
        for (int i = 1; i <= domain.size(); i++) formulaWeights.put(i,(float)1/domain.size());

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(domain);
        model.setFormulaWeights(formulaWeights);

        List<Set<Integer>> conflicts = new LinkedList<>();

        SimpleConflictSubsetSolver solver = new SimpleConflictSubsetSolver(model, domain, conflicts);
        AbstractDiagnosisEngine<Integer> engine = new HSTreeEngine<>(solver);
        engine.setCostsEstimator(new FormulaWeightsCostEstimator<>(model.getPossiblyFaultyFormulas(), model.getFormulaWeights()));
        return engine;
    }

    public abstract HeuristicQC<Integer> getHeuristicQC();

    @Test
    public void testGeneralConditionsForFindQPartition() {
        try {
            Set<Diagnosis<Integer>> diagnoses = calculateDiagnoses();

            // sum of measures of all diagnoses must equal 1
            BigDecimal sum = BigDecimal.ZERO;
            for (Diagnosis<Integer> d : diagnoses) {
                sum = sum.add(d.getMeasure());
            }
            assertTrue(BigDecimal.ONE.compareTo(sum) == 0);

            QPartition rootPartition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), null);

            QPartition<Integer> qPartition = qc.findQPartition(diagnoses, qc.getPartitionRequirementsMeasure());

            assertNotNull(qPartition);
            assertTrue(BigDecimal.ONE.compareTo(qPartition.probDx.add(qPartition.probDnx)) == 0); // , qPartition.probDx + qPartition.probDnx, 0.001);
            assertFalse("optimal q-partition must not equal the root partition", qPartition.equals(rootPartition));
        } catch (DiagnosisException e) {
            fail();
        }
    }

    @Test
    public void testFindQPartition() {
        try {
            QPartition<Integer> qPartition = qc.findQPartition(calculateDiagnoses(), qc.getPartitionRequirementsMeasure());
            QPartition<Integer> expectedP = getExpectedQPartition();

            assertTrue("expected probdx " + getExpectedProbDx() + " does not match " + qPartition.probDx, new BigDecimal(getExpectedProbDx()).compareTo(qPartition.probDx) == 0);
            assertTrue("expected probdx " + getExpectedProbDnx() + " does not match " + qPartition.probDnx, new BigDecimal(getExpectedProbDnx()).compareTo(qPartition.probDnx) == 0);
            assertTrue(expectedP.probDx.compareTo(qPartition.probDx) == 0);
            assertTrue(expectedP.probDnx.compareTo(qPartition.probDnx) == 0);
            assertEquals(expectedP, qPartition);

            assertFalse(qPartition.diagsTraits.isEmpty());
            assertEquals(qPartition.dnx.size(), qPartition.diagsTraits.keySet().size());
        } catch (DiagnosisException e) {
            fail();
        }
    }

    protected abstract String getExpectedProbDnx();

    protected abstract String getExpectedProbDx();

    protected abstract QPartition<Integer> getExpectedQPartition();

}
