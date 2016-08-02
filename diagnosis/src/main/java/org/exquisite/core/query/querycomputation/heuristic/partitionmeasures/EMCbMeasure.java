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
public class EMCbMeasure<F> implements IQPartitionRequirementsMeasure<F> {

    @Override
    public QPartition<F> updateBest(QPartition<F> p, QPartition<F> pBest) {
        if (p.dx.size() == 1 && p.probDx.compareTo(pBest.probDx) > 0)
            return p;
        return pBest;
    }

    @Override
    public boolean isOptimal(QPartition<F> pBest) {
        if (pBest.dx.size() == 1) {
            BigDecimal probMax = getMaximalProbOfADiagIn(pBest);
            if (pBest.probDx.compareTo(probMax) == 0)
                return true;
        }
        return false;
    }

    @Override
    public boolean prune(QPartition<F> p, QPartition<F> pBest) {
        return p.dx.size() >= 1;
    }

    @Override
    public BigDecimal getHeuristics(QPartition<F> p) {
        return new BigDecimal("-1").multiply(p.probDx);
    }

    @Override
    public BigDecimal getScore(Query<F> query) {
        return BigDecimal.ZERO; // TODO maybe think about a valid score?
    }

    @Override
    public void normalize(Set<Diagnosis<F>> diagnoses) {
        throw new RuntimeException();
    }

    private BigDecimal getMaximalProbOfADiagIn(QPartition<F> pBest) {
        BigDecimal maxMeasure = BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;

        for (Diagnosis<F> d : pBest.dx) {
            sum = sum.add(d.getMeasure());
            if (d.getMeasure().compareTo(maxMeasure) > 0)
                maxMeasure = d.getMeasure();
        }
        for (Diagnosis<F> d : pBest.dnx) {
            sum = sum.add(d.getMeasure());
            if (d.getMeasure().compareTo(maxMeasure) > 0)
                maxMeasure = d.getMeasure();
        }
        for (Diagnosis<F> d : pBest.dz) {
            sum = sum.add(d.getMeasure());
            if (d.getMeasure().compareTo(maxMeasure) > 0)
                maxMeasure = d.getMeasure();
        }

        final BigDecimal normalizedMaxMeasure = maxMeasure.divide(sum, MathContext.DECIMAL128);
        return normalizedMaxMeasure;
    }
}
