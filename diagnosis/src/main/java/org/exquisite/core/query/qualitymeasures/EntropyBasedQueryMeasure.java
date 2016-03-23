package org.exquisite.core.query.qualitymeasures;

import org.exquisite.core.query.QPartition;

/**
 * An entropy-based (ENT) measure for q-partition selection.
 *
 * @author patrick
 * @author wolfi
 */
public class EntropyBasedQueryMeasure<F> implements IQPartitionQualityMeasure<F> {

    /**
     * A threshold value.
     */
    private Double tm;

    /**
     *
     * @param threshold A threshold value tm.
     */
    public EntropyBasedQueryMeasure(Double threshold) {
        this.tm = threshold;
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
