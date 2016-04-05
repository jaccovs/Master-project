package org.exquisite.core.engines.query.querycomputation.heuristic.scenario1;

import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.RiskOptimizationMeasure;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;

/**
 * @author wolfi
 */
public class RIOScenario1 extends TestScenario1 {

    private BigDecimal tEnt = new BigDecimal("0.05");
    private BigDecimal tCard = BigDecimal.ZERO;
    private BigDecimal c = new BigDecimal("0.4");

    @Override
    public HeuristicQueryComputation<Integer> getHeuristicQC() {
        return new HeuristicQueryComputation<>(new HeuristicConfiguration<>(getEngine(), new RiskOptimizationMeasure(tEnt, tCard, c)));
    }

    @Override
    protected String getExpectedProbDnx() {
        return "0.46";
    }

    @Override
    protected String getExpectedProbDx() {
        return "0.54";
    }

    @Override
    protected QPartition<Integer> getExpectedQPartition() {
        return new QPartition<>(getSet(D3,D4,D2), getSet(D1,D5,D6), getSet(), getEngine().getCostsEstimator());
    }
}
