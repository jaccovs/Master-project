package org.exquisite.core.query.querycomputation.heuristic.partitionmeasures;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.Query;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Set;

/**
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 *
 * @author patrick
 * @author wolfi
 */
public class BMEMeasure<F> implements IQPartitionRequirementsMeasure<F> {

    private static final BigDecimal ONE_HALF = new BigDecimal("0.5");

    /**
     * A threshold value.
     */
    private BigDecimal tm;

    /**
     *
     * @param tm A threshold value.
     */
    public BMEMeasure(BigDecimal tm) {
        this.tm = tm;
    }

    @Override
    public QPartition<F> updateBest(QPartition<F> p, QPartition<F> pBest) {
        Set<Diagnosis<F>> setP = getSetWithMaxProbIn(p);
        Set<Diagnosis<F>> setPBest = getSetWithMaxProbIn(pBest);

        if (setP.size() < setPBest.size())
            return p;
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition<F> pBest) {
        if (pBest.probDx.compareTo(ONE_HALF) >= 0 && new BigDecimal(pBest.dx.size() - 1).compareTo(tm) <= 0)
            return true;
        if (pBest.probDnx.compareTo(ONE_HALF) >= 0 && new BigDecimal(pBest.dnx.size() - 1).compareTo(tm) <= 0)
            return true;
        return false;
    }

    @Override
    public boolean prune(QPartition<F> p, QPartition<F> pBest) {
        if (p.probDx.compareTo(ONE_HALF) > 0)
            return true;

        if (p.probDnx.compareTo(ONE_HALF) > 0) {
            if (p.dx.size() + 1 >= p.dnx.size()) {
                BigDecimal probMin = getMinimalProbOfADiagInDnx(p);
                if (p.probDnx.subtract(probMin).compareTo(ONE_HALF) <= 0)
                    return true;
            }
        }

        return false;
    }

    @Override
    public BigDecimal getHeuristics(QPartition<F> p) {
        if (p.probDx.compareTo(ONE_HALF) > 0)
            return new BigDecimal(p.dx.size());
        if (p.probDx.compareTo(ONE_HALF) < 0)
            return new BigDecimal(p.dnx.size());
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getScore(Query<F> query) {
        return BigDecimal.ZERO; // TODO maybe think about a valid score?
    }

    @Override
    public void normalize(Set<Diagnosis<F>> diagnoses) {
        throw new RuntimeException();
    }

    private Set<Diagnosis<F>> getSetWithMaxProbIn(QPartition<F> p) {
        Set<Diagnosis<F>> result;
        if (p.probDx.compareTo(p.probDnx) > 0) {
            result = p.dx;
        } else if (p.probDx.compareTo(p.probDnx) < 0) {
            result = p.dnx;
        } else {
            if (p.dx.size() <= p.dnx.size())
                result = p.dx;
            else
                result = p.dnx;
        }
        return result;
    }

    private BigDecimal getMinimalProbOfADiagInDnx(QPartition<F> p) {
        BigDecimal minMeasure = null;
        BigDecimal sum = BigDecimal.ZERO;

        for (Diagnosis<F> d : p.dx)
            sum = sum.add(d.getMeasure());

        for (Diagnosis<F> d : p.dnx) {
            sum = sum.add(d.getMeasure());
            if (minMeasure == null)
                minMeasure = d.getMeasure();
            else
                if (d.getMeasure().compareTo(minMeasure) < 0)
                    minMeasure = d.getMeasure();
        }

        for (Diagnosis<F> d : p.dz)
            sum = sum.add(d.getMeasure());

        final BigDecimal normalizedMinMeasure = minMeasure.divide(sum, MathContext.DECIMAL128);
        return normalizedMinMeasure;
    }

    @Override
    public String toString() {
        return new StringBuilder("BME(").append(tm).append(')').toString();
    }
}
