package org.exquisite.core.engines.query.qc.heuristic.scenario1;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.query.HeuristicQC;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.partitionmeasures.RiskOptimizationMeasure;
import org.junit.Test;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.*;

/**
 * Created by wolfi on 25.03.2016.
 */
public class RIO2Scenario1 extends TestScenario1 {

    public BigDecimal tEnt = new BigDecimal("0.05");
    public BigDecimal tCard = BigDecimal.ZERO;
    public BigDecimal c = new BigDecimal("0.3");

    @Override
    public HeuristicQC<Integer> getHeuristicQC() {
        return new HeuristicQC<>(new RiskOptimizationMeasure(tEnt, tCard, c), getEngine());
    }

    @Override
    protected String getExpectedProbDnx() {
        return "0.52";
    }

    @Override
    protected String getExpectedProbDx() {
        return "0.48";
    }

    @Override
    protected QPartition<Integer> getExpectedQPartition() {
        return new QPartition<>(getSet(D5,D4), getSet(D1,D2,D3,D6), getSet(), getEngine().getCostsEstimator());
    }
}
