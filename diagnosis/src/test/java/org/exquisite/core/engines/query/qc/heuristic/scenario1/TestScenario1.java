package org.exquisite.core.engines.query.qc.heuristic.scenario1;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.query.qc.heuristic.AbstractTestHeuristicQC;
import org.exquisite.core.model.Diagnosis;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;

/**
 * Created by wolfi on 25.03.2016.
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

}
