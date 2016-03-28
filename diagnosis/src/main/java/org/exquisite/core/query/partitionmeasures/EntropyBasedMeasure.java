package org.exquisite.core.query.partitionmeasures;

import org.exquisite.core.query.QPartition;

import java.math.BigDecimal;

/**
 * An entropy-based (ENT) requirements measure for q-partition selection.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 *
 * @author patrick
 * @author wolfi
 */
public class EntropyBasedMeasure<F> implements IQPartitionRequirementsMeasure<F> {

    /**
     * A threshold value.
     */
    private BigDecimal tm;

    /**
     * Constructor of an entropy based requirements measure.
     *
     * @param optimalityThreshold Specify an optimality threshold for good enough optimality.
     */
    public EntropyBasedMeasure(BigDecimal optimalityThreshold) {
        this.tm = optimalityThreshold;
    }

    @Override
    public QPartition updateBest(QPartition p, QPartition pBest) {
        if (p.probDx.subtract(HALF).abs().compareTo(pBest.probDx.subtract(HALF).abs()) < 0) // (Math.abs(p.probDx - 0.5) < Math.abs(pBest.probDx - 0.5))
            return p;
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition pBest) {
        return pBest.probDx.subtract(HALF).abs().compareTo(tm) <= 0; // Math.abs(pBest.probDx - 0.5) <= this.tm;
    }

    @Override
    public boolean prune(QPartition p, QPartition pBest) {
        return p.probDx.compareTo(HALF) >= 0; // p.probDx >= 0.5;
    }

    @Override
    public BigDecimal getHeuristics(QPartition p) {
        return p.probDx.subtract(HALF).abs(); // Math.abs(p.probDx - 0.5);
    }
}
