package org.exquisite.diagnosis.interactivity.partitioning;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.models.Diagnosis;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kostya
 * Date: 02.05.11
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
public interface Partitioning<Formula> {
    Partition<Formula> generatePartition(Set<Diagnosis<Formula>> hittingSets)
            throws DiagnosisException; // throws SolverException, InconsistentTheoryException;

    Partition<Formula> nextPartition(Partition<Formula> partition)
            throws DiagnosisException; // throws SolverException, InconsistentTheoryException;

    double getThreshold();

    void setThreshold(double threshold);

    int getNumOfHittingSets();

    boolean verifyPartition(Partition<Formula> partition)
            throws DiagnosisException; // throws SolverException, InconsistentTheoryException;
}
