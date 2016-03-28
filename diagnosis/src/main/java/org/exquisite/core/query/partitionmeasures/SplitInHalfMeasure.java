package org.exquisite.core.query.partitionmeasures;

import org.exquisite.core.query.QPartition;

import java.math.BigDecimal;

/**
 * A split-in-half (SPL) - based requirements measure for q-partition selection.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 *
 * @author patrick
 * @author wolfi
 */
public class SplitInHalfMeasure<F> implements IQPartitionRequirementsMeasure<F> {

    /**
     * A threshold value.
     */
    private BigDecimal tm;

    /**
     *
     * @param threshold A threshold value.
     */
    public SplitInHalfMeasure(BigDecimal threshold) {
        this.tm = threshold;
    }

    @Override
    public QPartition<F> updateBest(QPartition<F> p, QPartition<F> pBest) {
        final double halfSizeOfD = ((double)(p.dx.size() + p.dnx.size() + p.dz.size())) / 2.0d;
        if (Math.abs(((double)p.dx.size()) - halfSizeOfD) < Math.abs(((double)pBest.dx.size()) - halfSizeOfD))
            return p;
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition<F> pBest) {
        //final Set<Diagnosis<F>> D = Utils.union(pBest.dx, pBest.dnx, pBest.dz);
        final double halfSizeOfD = ((double)(pBest.dx.size() + pBest.dnx.size() + pBest.dz.size())) / 2.0d;


        return Math.abs(pBest.dx.size() - Math.floor(halfSizeOfD)) <= this.tm.doubleValue();
    }

    @Override
    public boolean prune(QPartition<F> p, QPartition<F> pBest) {
        //final Set<Diagnosis<F>> D = Utils.union(p.dx, p.dnx, p.dz);
        final double halfSizeOfD = ((double)(p.dx.size() + p.dnx.size() + p.dz.size())) / 2.0d;

        return p.dx.size() >= Math.floor(halfSizeOfD);
    }

    @Override
    public BigDecimal getHeuristics(QPartition<F> p) {
        //final double halfSizeOfD = ((double)Utils.union(p.dx, p.dnx, p.dz).size()) / 2.0d;
        final double halfSizeOfD = ((double)(p.dx.size() + p.dnx.size() + p.dz.size())) / 2.0d;

        return new BigDecimal(Math.abs(((double)p.dx.size()) - halfSizeOfD));
    }

}
