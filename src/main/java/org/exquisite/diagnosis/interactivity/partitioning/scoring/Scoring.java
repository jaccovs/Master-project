package org.exquisite.diagnosis.interactivity.partitioning.scoring;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.interactivity.partitioning.Partition;
import org.exquisite.diagnosis.interactivity.partitioning.Partitioning;
import org.exquisite.diagnosis.models.Diagnosis;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 01.06.11
 * Time: 08:44
 * To change this template use File | Settings | File Templates.
 */
public interface Scoring<Formula> {
    Partition runPostprocessor(List<Partition<Formula>> partitions, Partition<Formula> currentBest)
            throws DiagnosisException; // throws SolverException, InconsistentTheoryException;

    void setPartitionSearcher(Partitioning<Formula> partitioning);

    BigDecimal getScore(Partition<Formula> part);

    void normalize(Set<Diagnosis<Formula>> hittingSets);
}