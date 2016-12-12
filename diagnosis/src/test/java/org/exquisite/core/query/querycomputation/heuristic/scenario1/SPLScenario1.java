package org.exquisite.core.query.querycomputation.heuristic.scenario1;

import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicConfiguration;
import org.exquisite.core.query.querycomputation.heuristic.HeuristicQueryComputation;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.SplitInHalfMeasure;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;

/**
 * @author wolfi
 */
public class SPLScenario1 extends TestScenario1 {

    private BigDecimal tEnt = BigDecimal.ZERO;

    @Override
    public HeuristicQueryComputation<Integer> getHeuristicQC() {
        return new HeuristicQueryComputation<>(new HeuristicConfiguration<>(getEngine(), new SplitInHalfMeasure<>(tEnt), logger));
    }

    @Override
    protected String getExpectedProbDnx() {
        return "0.19";
    }

    @Override
    protected String getExpectedProbDx() {
        return "0.81";
    }

    @Override
    protected QPartition<Integer> getExpectedQPartition() {
        return new QPartition<>(getSet(D2,D4,D5), getSet(D1,D3,D6), getSet(), getEngine().getCostsEstimator());
    }
}
