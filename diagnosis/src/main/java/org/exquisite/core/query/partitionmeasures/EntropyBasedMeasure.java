package org.exquisite.core.query.partitionmeasures;

import org.exquisite.core.query.QPartition;

/**
 * An entropy-based (ENT) measure for q-partition selection.
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
    private Double tm;

    /**
     * Constructor of an entropy based requirements measure.
     *
     * @param optimalityThreshold Specify an optimality threshold for good enough optimality.
     */
    public EntropyBasedMeasure(Double optimalityThreshold) {
        this.tm = optimalityThreshold;
    }

    @Override
    public QPartition updateBest(QPartition p, QPartition pBest) {
        if (Math.abs(p.probDx - 0.5) < Math.abs(pBest.probDx - 0.5))
            return p;
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition pBest) {
        return Math.abs(pBest.probDx - 0.5) <= this.tm;
    }

    @Override
    public boolean prune(QPartition p, QPartition pBest) {
        return p.probDx >= 0.5;
    }

    @Override
    public double getHeuristics(QPartition p) {
        return Math.abs(p.probDx - 0.5);
    }
}
