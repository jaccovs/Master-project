package org.exquisite.core.query.partitionmeasures;

import org.exquisite.core.Utils;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;

import java.math.BigDecimal;
import java.util.Set;

/**
 * A risk optimization(RIO) -based requirements measure for q-partition selection.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 *
 * @author patrick
 * @author wolfi
 */
public class RiskOptimizationMeasure<F> implements IQPartitionRequirementsMeasure<F> {

    private BigDecimal c;

    private BigDecimal tCard;

    private BigDecimal tEnt;

    /**
     *
     *
     * @param entropyThreshold
     * @param cardinalityThreshold
     * @param cautious
     */
    public RiskOptimizationMeasure(BigDecimal entropyThreshold, BigDecimal cardinalityThreshold, BigDecimal cautious) {
        this.tEnt = entropyThreshold;
        this.tCard = cardinalityThreshold;
        this.c = cautious;
    }

    @Override
    public QPartition<F> updateBest(QPartition<F> p, QPartition<F> pBest) {
        final Set<Diagnosis<F>> D = Utils.union(p.dx, p.dnx, p.dz);
        final int n = (int) Math.ceil(this.c.doubleValue() * D.size()); // TODO test extensively that this works
        final int pDxSize = p.dx.size();
        final int pBestDxSize = pBest.dx.size();

        if (pDxSize >= n) {
            final int pAbs = Math.abs(n - pDxSize);
            final int pBestAbs = Math.abs(n - pBestDxSize);

            if (pAbs < pBestAbs)
                return p;
            else if (pAbs == pBestAbs)
                if (HALF.subtract(p.probDx).abs().compareTo(HALF.subtract(pBest.probDx).abs()) < 0) //if (Math.abs(0.5 - p.probDx) < Math.abs(0.5 - pBest.probDx))
                    return p;
        }
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition<F> pBest) {
        final Set<Diagnosis<F>> D = Utils.union(pBest.dx, pBest.dnx, pBest.dz);
        final int n = (int) Math.ceil(this.c.doubleValue() * D.size());
        final int pDxSize = pBest.dx.size();

        return (pDxSize >= n)
                && (pDxSize - n <= this.tCard.doubleValue())
                && pBest.probDx.subtract(HALF).abs().compareTo(tEnt) <= 0; // (Math.abs(pBest.probDx - 0.5) <= this.tEnt.doubleValue());
    }

    @Override
    public boolean prune(QPartition<F> p, QPartition<F> pBest) {
        final Set<Diagnosis<F>> D = Utils.union(p.dx, p.dnx, p.dz);
        final int n = (int) Math.ceil(this.c.doubleValue() * D.size());

        if (p.dx.size() == n)
            return true;
        if (pBest.dx.size() == n) {
            if (p.dx.size() > n)
                return true;
            if (p.probDx.compareTo(pBest.probDx) > 0 && p.probDx.compareTo(HALF) >= 0) // (p.probDx > pBest.probDx && p.probDx >= 0.5)
                return true;
        }

        return false;
    }

    @Override
    public BigDecimal getHeuristics(QPartition<F> p) {
        final Set<Diagnosis<F>> D = Utils.union(p.dx, p.dnx, p.dz);
        final int n = (int) Math.ceil(this.c.doubleValue() * D.size()); // to be accepted by RIO, D+ must include at least n diagnoses
        final int numDiagsToAdd = n - p.dx.size(); // #  of diagnoses to be added to D+ to achieve |D+| = n
        double avgProb = p.probDnx.doubleValue() / p.dnx.size(); // average probability of diagnoses that might be added to D+

        return new BigDecimal(Math.abs(p.probDx.doubleValue() + numDiagsToAdd * avgProb - 0.5));
    }
}
