package org.exquisite.core;

import org.exquisite.core.model.DiagnosisModel;

import java.util.Collection;
import java.util.Set;

/**
 * A common interface for the different solvers.
 * Note that solvers must not modify any of the input parameters!
 *
 * @author Thomas
 */
public interface ISolver<T> {

    /**
     * Checks consistency of a set of formulas
     *
     * @param formulas       set of formulas
     * @return <code>true</code> if formulas are consistent and <code>false</code> otherwise
     */
    boolean isConsistent(Collection<T> formulas);

    /**
     * Special version of isConsistent, that checks if the set of formulas entails the set of entailments
     *
     * @param formulas       set of formulas
     * @param alpha          set of formulas that must be verified
     * @return <code>true</code> if formulas entail entailments
     */
    boolean isEntailed(Collection<T> formulas, Collection<T> alpha);

    /**
     * Calculates the set of entailed formulas. See documentation of implementing classes for particular semantics
     * of this method.
     *
     * @param formulas       set of formulas
     * @return set of entailments
     */
    Set<T> calculateEntailments(Collection<T> formulas);

    /**
     *
     * @return the diagnosis model of the solver
     */
    DiagnosisModel<T> getDiagnosisModel();
}
