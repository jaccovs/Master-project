package org.exquisite.core;


import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.solver.ISolver;

import java.util.Set;

/**
 * Defines an interface for tests.diagnosis engine implementations.
 *
 * @author David
 */
public interface IDiagnosisEngine<F> {

    /**
     * Reverts the state of the engine to how it was when first instantiated.
     */
    void resetEngine();

    void setMaxNumberOfDiagnoses(int maxNumberOfDiagnoses);

    /**
     * Returns the solver of the engine
     *
     * @return solver of the engine
     */
    ISolver<F> getSolver();

    /**
     * Start the tests.diagnosis process.
     *
     * @return a list of diagnoses or an empty list if no diagnoses were found.
     * @throws DiagnosisException
     */
    Set<Diagnosis<F>> calculateDiagnoses() throws DiagnosisException;


    /**
     * Use this method to preset known conflicts to the diagnosis engine
     * @param conflicts a set of known conflicts
     */
    void setConflicts(Set<Set<F>> conflicts);
}
