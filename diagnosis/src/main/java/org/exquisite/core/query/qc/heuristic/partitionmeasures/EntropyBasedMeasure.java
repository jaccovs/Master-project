package org.exquisite.core.query.qc.heuristic.partitionmeasures;

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
    public QPartition<F> updateBest(QPartition<F> p, QPartition<F> pBest) {
        if (p.probDx.subtract(HALF).abs().compareTo(pBest.probDx.subtract(HALF).abs()) < 0) // (Math.abs(p.probDx - 0.5) < Math.abs(pBest.probDx - 0.5))
            return p;
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition<F> pBest) {
        return pBest.probDx.subtract(HALF).abs().compareTo(tm) <= 0; // Math.abs(pBest.probDx - 0.5) <= this.tm;
    }

    @Override
    public boolean prune(QPartition<F> p, QPartition<F> pBest) {
        return p.probDx.compareTo(HALF) >= 0; // p.probDx >= 0.5;
    }

    @Override
    public BigDecimal getHeuristics(QPartition<F> p) {
        return p.probDx.subtract(HALF).abs(); // Math.abs(p.probDx - 0.5);
    }
}
