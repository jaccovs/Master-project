package org.exquisite.core.engines.query.querycomputation.heuristic.scenario1;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.Utils;
import org.exquisite.core.engines.query.querycomputation.heuristic.AbstractTestHeuristicQC;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.QPartitionOperations;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author wolfi
 */
public abstract class TestScenario1 extends AbstractTestHeuristicQC {

    public Diagnosis<Integer> D1, D2, D3, D4, D5, D6;

    @Override
    public Set<Diagnosis<Integer>> calculateDiagnoses() throws DiagnosisException {
        D1 = getDiagnosis(2, 3);
        D2 = getDiagnosis(2, 5);
        D3 = getDiagnosis(2, 6);
        D4 = getDiagnosis(2, 7);
        D5 = getDiagnosis(1, 4, 7);
        D6 = getDiagnosis(3, 4, 7);

        // IMPORTANT you have to declare the measure numbers via string parameters, as double values you loose precision
        // (i.e. double 0.01 is transformed to 0.010000000001234323434 as BigDecimal)
        D1.setMeasure(new BigDecimal("0.01"));
        D2.setMeasure(new BigDecimal("0.33"));
        D3.setMeasure(new BigDecimal("0.14"));
        D4.setMeasure(new BigDecimal("0.07"));
        D5.setMeasure(new BigDecimal("0.41"));
        D6.setMeasure(new BigDecimal("0.04"));

        assertEquals(0.01, D1.getMeasure().doubleValue(), delta);
        assertEquals(0.33, D2.getMeasure().doubleValue(), delta);
        assertEquals(0.14, D3.getMeasure().doubleValue(), delta);
        assertEquals(0.07, D4.getMeasure().doubleValue(), delta);
        assertEquals(0.41, D5.getMeasure().doubleValue(), delta);
        assertEquals(0.04, D6.getMeasure().doubleValue(), delta);

        return getSet(D1, D2, D3, D4, D5, D6);
    }

    @Test
    public void testGetSetOfMinTraits() {
        try {
            calculateDiagnoses();

            testGetSetOfMinTraitsHelper(
                    new QPartition<>(getSet(D1), getSet(D2,D3,D4,D5,D6), getSet(), null),
                    getSet(getSet(5), getSet(6), getSet(7), getSet(1, 4, 7), getSet(4, 7)),
                    getSet(getSet(5), getSet(6), getSet(7)));

            testGetSetOfMinTraitsHelper(
                    new QPartition<>(getSet(D2), getSet(D1,D3,D4,D5,D6), getSet(), null),
                    getSet(getSet(3), getSet(6), getSet(7), getSet(1, 4, 7), getSet(3, 4, 7)),
                    getSet(getSet(3), getSet(6), getSet(7)));

            testGetSetOfMinTraitsHelper(
                    new QPartition<>(getSet(D1, D2), getSet(D3,D4,D5,D6), getSet(), null),
                    getSet(getSet(6), getSet(7), getSet(1, 4, 7), getSet(4, 7)),
                    getSet(getSet(6), getSet(7)));

            testGetSetOfMinTraitsHelper(
                    new QPartition<>(getSet(D6), getSet(D1,D2,D3,D4,D5), getSet(), null),
                    getSet(getSet(2), getSet(2,5), getSet(2,6), getSet(2), getSet(1)),
                    getSet(getSet(1), getSet(2)));

            testGetSetOfMinTraitsHelper(
                    null,
                    getSet(getSet(1,2,3), getSet(3,5), getSet(3,5,7), getSet(1,4), getSet(4,7)),
                    getSet(getSet(1,2,3), getSet(3,5), getSet(1,4), getSet(4,7)));

            testGetSetOfMinTraitsHelper(
                    null,
                    getSet(getSet(1,2,3), getSet(1,2,3), getSet(1,2,3), getSet(1,2,3), getSet(1,2,3)),
                    getSet(getSet(1,2,3)));

            testGetSetOfMinTraitsHelper(
                    null,
                    getSet(getSet(), getSet(), getSet(), getSet(), getSet()),
                    getSet(getSet()));

            testGetSetOfMinTraitsHelper(
                    null,
                    getSet(getSet(1,2,3), getSet(1,2,3), getSet(3,5,7), getSet(3,7), getSet(5,7),getSet(3,5),getSet(3,5,7)),
                    getSet(getSet(1,2,3), getSet(3,5), getSet(3,7), getSet(5,7)));

        } catch (DiagnosisException e) {
            fail();
        }

    }

    private void testGetSetOfMinTraitsHelper(QPartition<Integer> qPartition, Set<Set<Integer>> expectedDiagTraitValues, Set<Set<Integer>> expectedSetOfMinTraits) {
        if (qPartition != null) {
            qPartition.diagsTraits = QPartitionOperations.computeDiagsTraits(qPartition);
            Set<Set<Integer>> actualDiagTraitValues = new HashSet<>();
            actualDiagTraitValues.addAll(qPartition.diagsTraits.values());
            assertEquals(expectedDiagTraitValues, actualDiagTraitValues);
        }

        Set<Set<Integer>> setOfMinTraits = Utils.removeSuperSets(expectedDiagTraitValues);
        assertEquals(expectedSetOfMinTraits, setOfMinTraits);
    }

}
