package org.exquisite.diagnosis.interactivity.partitioning;

import java.util.Set;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.models.Diagnosis;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 02.05.11
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
public interface Partitioning {
    Partition generatePartition(Set<Diagnosis> hittingSets) throws DiagnosisException; // throws SolverException, InconsistentTheoryException;

    Partition nextPartition(Partition partition) throws DiagnosisException; // throws SolverException, InconsistentTheoryException;

    public double getThreshold();

    public void setThreshold(double threshold);

    public int getNumOfHittingSets();

    boolean verifyPartition(Partition partition) throws DiagnosisException; // throws SolverException, InconsistentTheoryException;
}
