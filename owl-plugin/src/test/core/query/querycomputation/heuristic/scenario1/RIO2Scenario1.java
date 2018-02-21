package org.exquisite.core.query.querycomputation.heuristic.scenario1;

import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.RiskOptimizationMeasure;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;

/**
 * @author wolfi
 */
public class RIO2Scenario1 extends TestScenario1 {

    private BigDecimal tEnt = new BigDecimal("0.05");
    private BigDecimal tCard = BigDecimal.ZERO;
    private BigDecimal c = new BigDecimal("0.3");

    @Override
    public HeuristicQueryComputation<Integer> getHeuristicQC() {
        return new HeuristicQueryComputation<>(new HeuristicConfiguration<>(getEngine(), new RiskOptimizationMeasure(tEnt, tCard, c), monitor));
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
