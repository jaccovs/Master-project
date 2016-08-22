package org.exquisite.core.solver;

import org.exquisite.core.model.DiagnosisModel;

import java.util.Collection;
import java.util.Set;

/**
 * A common interface for the different solvers.
 * Note that solvers must not modify any of the input parameters!
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author Thomas
 */
public interface ISolver<F> {

    /**
     * Checks consistency of a set of formulas
     *
     * @param formulas       set of formulas
     * @return <code>true</code> if formulas are consistent and <code>false</code> otherwise
     */
    boolean isConsistent(Collection<F> formulas);

    /**
     * Special version of isConsistent, that checks if the set of formulas entails the set of entailments
     *
     * @param formulas       set of formulas
     * @param alpha          set of formulas that must be verified
     * @return <code>true</code> if formulas entail entailments
     */
    boolean isEntailed(Collection<F> formulas, Collection<F> alpha);

    /**
     * Calculates the set of entailed formulas. See documentation of implementing classes for particular semantics
     * of this method.
     *
     * @param formulas       set of formulas
     * @return set of entailments
     */
    Set<F> calculateEntailments(Collection<F> formulas);

    /**
     *
     * @return the diagnosis model of the solver
     */
    DiagnosisModel<F> getDiagnosisModel();

    void dispose();
}
