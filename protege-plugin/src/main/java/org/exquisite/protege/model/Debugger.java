package org.exquisite.protege.model;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.model.exception.InconsistentTheoryException;
import org.exquisite.protege.model.exception.NoConflictException;
import org.exquisite.protege.model.exception.SolverException;

import java.util.Set;

public interface Debugger<T extends Diagnosis<Id>, Id> {

    public static int ALL_DIAGNOSES = -1;

    public void setMaxDiagnosesNumber(int number);

    public int getMaxDiagnosesNumber();

    public Set<T> start() throws SolverException, NoConflictException, InconsistentTheoryException;

    public void reset();

    public Set<T> getConflicts();

    public Set<T> getDiagnoses();

}
