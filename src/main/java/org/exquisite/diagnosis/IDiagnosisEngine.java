package org.exquisite.diagnosis;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import java.util.List;

/**
 * Defines an interface for tests.diagnosis engine implementations.
 *
 * @author David
 */
public interface IDiagnosisEngine<T> {

    /**
     * Reverts the state of the engine to how it was when first instantiated.
     */
    void resetEngine();

    /**
     * Returns the constraint model
     *
     * @return
     */
    ExquisiteSession<T> getSessionData();

    /**
     * Setter for the constraint model
     *
     * @param model
     */
    void setSessionData(ExquisiteSession<T> sessionData);

    DiagnosisModel<T> getModel();

    /**
     * Start the tests.diagnosis process.
     *
     * @return a list of diagnoses or an empty list if no diagnoses were found.
     * @throws DomainSizeException
     */
    List<Diagnosis<T>> calculateDiagnoses() throws DiagnosisException;

    /**
     * Returns number of calls made to the solver.
     */
    int getSolverCalls();

    void incrementSolverCalls();

    long getSolverTime();

    void incrementSolverTime(long time);

    /**
     * @return number of calls to solver with a model that proved to be solvable.
     */
    int getCspSolvedCount();

    void incrementCSPSolutionCount();

    /**
     * @return number of propagations performed by the solver.
     */
    int getPropagationCount();

    void incrementPropagationCount();

    int getTPCalls();

    void incrementQXPCalls();

    int getSearchesForConflicts();

    void incrementSearchesForConflicts();

    int getMXPConflicts();

    void incrementMXPConflicts(int conflicts);

    int getMXPSplittingTechniqueConflicts();

    void incrementMXPSplittingTechniqueConflicts(int conflicts);

    /**
     * returns the time where the tests.diagnosis task was finished
     * (Not all threads have to be stopped at this point)
     *
     * @return
     */
    long getFinishedTime();
}
