package org.exquisite.core.query.querycomputation.heuristic.partitionmeasures;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.scoring.MinScoreQSS;

import java.math.BigDecimal;
import java.util.Set;

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

    @Override
    public BigDecimal getScore(Query<F> query) {
        return new MinScoreQSS<F>().getScore(query);
    }

    @Override
    public void normalize(Set<Diagnosis<F>> diagnoses) {
        new MinScoreQSS<F>().normalize(diagnoses);
    }

    @Override
    public String toString() {
        return new StringBuilder("ENT(").append(tm).append(')').toString();
    }
}
