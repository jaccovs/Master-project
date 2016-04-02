package org.exquisite.core.engines.query.qc.heuristic.scenario1;

import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.qc.heuristic.HeuristicQC;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.EntropyBasedMeasure;

import java.math.BigDecimal;

import static org.exquisite.core.TestUtils.getSet;

/**
 * @author wolfi
 */
public class ENTScenario1 extends TestScenario1 {

    private BigDecimal tEnt = new BigDecimal("0.05");

    @Override
    public HeuristicQC<Integer> getHeuristicQC() {
        return new HeuristicQC<>(new EntropyBasedMeasure<>(tEnt), getEngine());
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
