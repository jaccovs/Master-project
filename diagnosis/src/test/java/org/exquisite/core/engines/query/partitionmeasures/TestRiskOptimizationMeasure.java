package org.exquisite.core.engines.query.partitionmeasures;

import org.exquisite.core.query.partitionmeasures.IQPartitionRequirementsMeasure;
import org.exquisite.core.query.partitionmeasures.RiskOptimizationMeasure;

import java.math.BigDecimal;

/**
 * Created by wolfi on 27.03.2016.
 */
public class TestRiskOptimizationMeasure extends AbstractTestPartitionRequirementsMeasure {

    @Override
    public IQPartitionRequirementsMeasure getMeasure() {
        return new RiskOptimizationMeasure<>(new BigDecimal(0.05), new BigDecimal(0.05), new BigDecimal(0.05));
    }

    @Override
    public void testUpdateBest() {

    }

    @Override
    public void testIsOptimal() {

    }

    @Override
    public void testPrune() {

    }

    @Override
    public void getHeuristics() {

    }
}
