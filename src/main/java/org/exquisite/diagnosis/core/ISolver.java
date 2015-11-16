package org.exquisite.diagnosis.core;

import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;

import java.util.List;
import java.util.Set;

/**
 * A common interface for the different solvers
 *
 * @author Thomas
 */
public interface ISolver<T> {

    /**
     * A method to post the constraints
     *
     * @param qx
     * @param constraints
     */
    void createModel(QuickXPlain<T> qx, List<T> constraints);

    /**
     * Calls the solver
     *
     * @param diagnosisEngine
     * @return
     */
    boolean isFeasible(IDiagnosisEngine<T> diagnosisEngine);

    /**
     * Special version of isFeasible, that checks if the constraints passed to the Solver with createModel() entail the given entailments
     *
     * @param entailments
     * @return
     */
    boolean isEntailed(IDiagnosisEngine<T> diagnosisEngine, Set<T> entailments);

    /**
     * Calculates the implicitly entailed constraints.
     *
     * @return
     */
    Set<T> calculateEntailments();
}
