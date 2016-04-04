package org.exquisite.core.engines.query.qc.heuristic.scenario1;

import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.qc.heuristic.HeuristicQC;
import org.exquisite.core.query.qc.heuristic.HeuristicQCConfiguration;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.SplitInHalfMeasure;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;

/**
 * @author wolfi
 */
public class SPLScenario1 extends TestScenario1 {

    private BigDecimal tEnt = BigDecimal.ZERO;

    @Override
    public HeuristicQC<Integer> getHeuristicQC() {
        return new HeuristicQC<>(new HeuristicQCConfiguration<>(getEngine(), new SplitInHalfMeasure<>(tEnt)));
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
