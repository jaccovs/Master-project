package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;

import java.util.Set;

/**
 * Query Quality Measure interface.
 *
 * Created by wolfi, pr8 on 10.03.2016.
 */
public interface QPartitionQualityMeasure<F> {

    /**
     * TODO documentation
     *
     * @param partition1 Partition 1
     * @param partition2 Partition 2
     * @return QPartition
     */
    QPartition<F> updateBest(QPartition<F> partition1, QPartition<F> partition2, Set<Diagnosis<F>> hittingSets);

    /**
     * TODO documentation
     *
     * @param qPartition qPartition object
     * @return Double value
     */
    Double getHeuristics(QPartition<F> qPartition, Set<Diagnosis<F>> hittingSets);

    /**
     * TODO documentation
     *
     * @param qPartition qPartition object
     * @param hittingSets Diagnoses
     * @return Boolean value
     */
    Boolean isOptimal(QPartition<F> qPartition, Set<Diagnosis<F>> hittingSets);

    /**
     * TODO documentation
     *
     * @param qPartition qPartition
     * @param hittingSets Diagnoses
     */
    void prune(QPartition<F> qPartition, Set<Diagnosis<F>> hittingSets);

}
