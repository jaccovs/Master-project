package org.exquisite.core.query.partitionmeasures;

import org.exquisite.core.Utils;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;

import java.util.Set;

/**
 * A split-in-half (SPL) measure for q-partition selection.
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
    private Double tm;

    /**
     *
     * @param threshold A threshold value.
     */
    public SplitInHalfMeasure(Double threshold) {
        this.tm = threshold;
    }

    @Override
    public QPartition<F> updateBest(QPartition<F> p, QPartition<F> pBest) {
        final Set<Diagnosis<F>> D = Utils.union(p.dx, p.dnx, p.dz);
        final int halfSizeOfD = D.size() / 2;

        if (Math.abs(p.dx.size() - halfSizeOfD) < Math.abs(pBest.dx.size() - halfSizeOfD)) // TODO check second halfSizeOfD
            return p;
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition<F> pBest) {
        final Set<Diagnosis<F>> D = Utils.union(pBest.dx, pBest.dnx, pBest.dz);

        return Math.abs(pBest.dx.size() - Math.floor(D.size()/2)) <= this.tm;
    }

    @Override
    public boolean prune(QPartition<F> p, QPartition<F> pBest) {
        final Set<Diagnosis<F>> D = Utils.union(p.dx, p.dnx, p.dz);

        return p.dx.size() >= Math.floor(D.size()/2);
    }

    @Override
    public double getHeuristics(QPartition<F> p) {
        final Set<Diagnosis<F>> D = Utils.union(p.dx, p.dnx, p.dz);

        return Math.abs(p.dx.size() - (D.size()/2));
    }
}
