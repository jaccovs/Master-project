package org.exquisite.core.query.qualitymeasures;

import org.exquisite.core.query.QPartition;

/**
 * Partition Quality Measure interface.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 *
 * @author wolfi
 * @author patrick
 */
public interface IQPartitionQualityMeasure<F> {

    /**
     * Update best q-Partition.
     *
     * Find the better partition among any partition (p) and hitherto best partition(pBest) w.r.t. leading diagnoses.
     *
     * @param p A partition p.
     * @param pBest A hitherto best partition (pBest).
     * @return The better partition among partition p and hitherto best partition pBest.
     */
    QPartition<F> updateBest(QPartition<F> p, QPartition<F> pBest);

    /**
     * Optimality check in D+-Partitioning.
     *
     * @param pBest a partition pBest.
     * @return <code>true</code> iff partition (pBest) is an optimal q-partition w.r.t. some threshold.
     */
    boolean isOptimal(QPartition<F> pBest);

    /**
     * Pruning in D+-Partitioning.
     *
     * @param p A partition p.
     * @param pBest A hitherto best partition (pBest).
     * @return <code>true</code> if exploring successor q-partitions of p cannot lead to the discovery of q-partitions
     * that are better than pBest.
     */
    boolean prune(QPartition<F> p, QPartition<F> pBest);

    /**
     * Heuristics in D+-Partitioning.
     *
     * @param p A partition p.
     * @return A heuristic measure.
     */
    double getHeuristics(QPartition<F> p);

}
