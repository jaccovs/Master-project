package org.exquisite.core.query.querycomputation.heuristic.partitionmeasures;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.scoring.SplitInHalf1QSS;

import java.math.BigDecimal;
import java.util.Set;

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
        final double halfSizeOfD = getHalfSizeOfD(p);
        if (Math.abs(((double)p.dx.size()) - halfSizeOfD) < Math.abs(((double)pBest.dx.size()) - halfSizeOfD))
            return p;
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition<F> pBest) {
        final double halfSizeOfD = getHalfSizeOfD(pBest);
        return Math.abs(pBest.dx.size() - Math.floor(halfSizeOfD)) <= this.tm.doubleValue();
    }

    @Override
    public boolean prune(QPartition<F> p, QPartition<F> pBest) {
        final double halfSizeOfD = getHalfSizeOfD(p);
        return p.dx.size() >= Math.floor(halfSizeOfD);
    }

    @Override
    public BigDecimal getHeuristics(QPartition<F> p) {
        final double halfSizeOfD = getHalfSizeOfD(p);
        return new BigDecimal(Math.abs(((double)p.dx.size()) - halfSizeOfD));
    }

    /**
     * Sum up all diagnoses in dx, dnx and dz and return the half of it.
     *
     * @param p A q-parttion.
     * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
     * @return Half of the size of diagnoses in dx, dnx and dz.
     */
    private static <F> double getHalfSizeOfD(QPartition<F> p) {
        return ((double)(p.dx.size() + p.dnx.size() + p.dz.size())) / 2.0d;
    }

    @Override
    public BigDecimal getScore(Query<F> query) {
        return new SplitInHalf1QSS<F>().getScore(query);
    }

    @Override
    public void normalize(Set<Diagnosis<F>> diagnoses) {
        new SplitInHalf1QSS<F>().normalize(diagnoses);
    }

    @Override
    public String toString() {
        return new StringBuilder("SPL(").append(tm).append(')').toString();
    }
}
