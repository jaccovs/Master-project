package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;

import java.util.Set;

/**
 * Query Quality Measure interface.
 *
 * Created by wolfi, pr8 on 10.03.2016.
 */
public interface QPartitionQualityMeasure<Formula> {

    /**
     * TODO documentation
     *
     * @param partition1 Partition 1
     * @param partition2 Partition 2
     * @return QPartition
     */
    QPartition<Formula> updateBest(QPartition<Formula> partition1, QPartition<Formula> partition2, Set<Diagnosis<Formula>> hittingSets);

    /**
     * TODO documentation
     *
     * @param qPartition qPartition object
     * @return Double value
     */
    Double getHeuristics(QPartition<Formula> qPartition, Set<Diagnosis<Formula>> hittingSets);

    /**
     * TODO documentation
     *
     * @param qPartition qPartition object
     * @param threshold Threshold
     * @param hittingSets Diagnoses
     * @return Boolean value
     */
    Boolean isOptimal(QPartition<Formula> qPartition, Double threshold, Set<Diagnosis<Formula>> hittingSets);

    /**
     * TODO documentation
     *
     * @param qPartition qPartition
     * @param hittingSets Diagnoses
     */
    void prune(QPartition<Formula> qPartition, Set<Diagnosis<Formula>> hittingSets);
}
