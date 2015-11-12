package org.exquisite.diagnosis.interactivity.partitioning.scoring;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.interactivity.partitioning.Partition;
import org.exquisite.diagnosis.interactivity.partitioning.Partitioning;
import org.exquisite.diagnosis.models.Diagnosis;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 01.06.11
 * Time: 08:44
 * To change this template use File | Settings | File Templates.
 */
public interface Scoring {
    Partition runPostprocessor(List<Partition> partitions, Partition currentBest) throws DiagnosisException; // throws SolverException, InconsistentTheoryException;

    void setPartitionSearcher(Partitioning partitioning);

    BigDecimal getScore(Partition part);

    void normalize(Set<Diagnosis> hittingSets);
}