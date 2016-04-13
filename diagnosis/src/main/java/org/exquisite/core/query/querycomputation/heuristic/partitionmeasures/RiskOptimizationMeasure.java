package org.exquisite.core.query.querycomputation.heuristic.partitionmeasures;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.scoring.RIOQSS;

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
     * RiskOptimizationMeasure constructor.
     *
     * @param entropyThreshold A threshold for entropy.
     * @param cardinalityThreshold A threshold for cardinality.
     * @param cautious A cautious parameter.
     */
    public RiskOptimizationMeasure(BigDecimal entropyThreshold, BigDecimal cardinalityThreshold, BigDecimal cautious) {
        this.tEnt = entropyThreshold;
        this.tCard = cardinalityThreshold;
        this.c = cautious;
    }

    @Override
    public QPartition<F> updateBest(QPartition<F> p, QPartition<F> pBest) {
        final int n = getN(p, c);
        final int pDxSize = p.dx.size();
        final int pBestDxSize = pBest.dx.size();

        if (pDxSize >= n) {
            final int pAbs = Math.abs(n - pDxSize);
            final int pBestAbs = Math.abs(n - pBestDxSize);

            if (pAbs < pBestAbs)
                return p;
            else if (pAbs == pBestAbs)
                if (HALF.subtract(p.probDx).abs().compareTo(HALF.subtract(pBest.probDx).abs()) < 0) // if (Math.abs(0.5 - p.probDx) < Math.abs(0.5 - pBest.probDx))
                    return p;
        }
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition<F> pBest) {
        final int n = getN(pBest, this.c);
        final int dxSize = pBest.dx.size();

        return (dxSize >= n)
                && (new BigDecimal(Double.toString(dxSize - n)).compareTo(this.tCard) <= 0) // && (dxSize - n <= this.tCard.doubleValue())
                && pBest.probDx.subtract(HALF).abs().compareTo(tEnt) <= 0; // (Math.abs(pBest.probDx - 0.5) <= this.tEnt.doubleValue());
    }

    @Override
    public boolean prune(QPartition<F> p, QPartition<F> pBest) {
        final int n = getN(p, this.c);

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
        final int n = getN(p, this.c); // to be accepted by RIO, D+ must include at least n diagnoses
        final int numDiagsToAdd = n - p.dx.size(); // #  of diagnoses to be added to D+ to achieve |D+| = n
        final double avgProb = p.probDnx.doubleValue() / p.dnx.size(); // average probability of diagnoses that might be added to D+ // TODO loss of precision

        return new BigDecimal(Math.abs(p.probDx.doubleValue() + numDiagsToAdd * avgProb - 0.5)); // TODO loss of precision
    }

    /**
     * Sum up all diagnoses in dx, dnx and dz and return the size of it and returns the value as double.
     *
     * @param p A q-parttion.
     * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
     * @return The size of diagnoses in dx, dnx and dz as double.
     */
    private static <F> double getSizeOfD(QPartition<F> p) {
        return ((double)(p.dx.size() + p.dnx.size() + p.dz.size()));
    }

    private static <F> int getN(QPartition<F> p, BigDecimal c) {
        return (int) Math.ceil(c.doubleValue() * getSizeOfD(p)); // TODO loss of precision
    }

    @Override
    public BigDecimal getScore(Query<F> query) {
        return new RIOQSS<F>(this.c).getScore(query);
    }

    @Override
    public void normalize(Set<Diagnosis<F>> diagnoses) {
        new RIOQSS<F>(this.c).normalize(diagnoses);
    }

    @Override
    public String toString() {
        return new StringBuilder("RIO(").append("ent:").append(tEnt).append(",card:").append(tCard).append(",cautious:").append(c).append(')').toString();
    }
}
