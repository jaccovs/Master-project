package org.exquisite.core.solver;

import org.exquisite.core.model.DiagnosisModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a default implementation of a solver that provides methods for handling of test cases, etc. Extend this
 * class if you use standard test cases, as defined in the literature:
 * <ul>
 * <li>Felfernig, A., Friedrich, G., Jannach, D., & Stumptner, M. (2004). Consistency-based diagnosis of configuration
 * knowledge bases. Artificial Intelligence, 152</li>
 * <li>Shchekotykhin, K., Friedrich, G., Fleiss, P., & Rodler, P. (2012). Interactive ontology debugging : two query
 * strategies for efficient fault localization. Web Semantics: Science, Services and Agents on the World Wide Web,
 * 12-13, 88–103</li>
 * </ul>
 */
public abstract class AbstractSolver<T> implements ISolver<T>, Observer {

    private final DiagnosisModel<T> diagnosisModel;
    private Set<T> formulasCache = new HashSet<>();
    private Map<T, T> negationsCache = new HashMap<>();

    public AbstractSolver(DiagnosisModel<T> diagnosisModel) {
        this.diagnosisModel = diagnosisModel;
        this.diagnosisModel.addObserver(this);
    }

    /**
     * This method implements consistency checking in presence of the test examples: consistent, inconsistent,
     * entailed and not-entailed. The method implements caching and is suitable for incremental solvers. It saves the
     * state
     *
     * @param formulas set of formulas
     * @return <code>true</code> if the set of provided formulas is consistent and fulfills all the examples of the
     * diagnosis model.
     */
    @Override
    public boolean isConsistent(Collection<T> formulas) {
        synchronizeCache(formulas);

        if (!isConsistent())
            return false;

        // check non-entailed examples
        for (T example : diagnosisModel.getNotEntailedExamples()) {
            if (isEntailed(Collections.singleton(example))) {
                return false;
            }
        }

        // check consistent examples
        if (violatesExample(diagnosisModel.getConsistentExamples(), true))
            return false;

        // check negative examples
        if (!supportsNegation()) {
            if (violatesExample(diagnosisModel.getInconsistentExamples(), false))
                return false;
        }

        return true;
    }

    /**
     * Computes a set of formulas that must be added to the solver for consistency checking in the presence
     * of examples and background knowledge. Synchronizes the obtained set with the cache and sends the
     * difference sets to the underlying solvers by calling {@link AbstractSolver#sync(Set, Set)}
     *
     * @param formulas set of formulas
     */
    private void synchronizeCache(Collection<T> formulas) {
        Set<T> checkFormulas = new HashSet<>(this.formulasCache.size());
        checkFormulas.addAll(diagnosisModel.getCorrectStatements());
        checkFormulas.addAll(diagnosisModel.getEntailedExamples());
        if (supportsNegation()) {
            for (T example : diagnosisModel.getInconsistentExamples()) {
                T neg = negationsCache.get(example);
                if (neg == null) {
                    neg = negate(example);
                    checkFormulas.add(neg);
                    this.negationsCache.put(example, neg);
                } else
                    checkFormulas.add(neg);
            }
        }

        checkFormulas.addAll(formulas);

        // sync the formulas with the solver
        HashSet<T> remove = new HashSet<>(this.formulasCache);
        HashSet<T> add = checkFormulas.stream().filter(e -> e != null).collect(Collectors.toCollection(HashSet<T>::new));
        remove.removeAll(checkFormulas);
        add.removeAll(this.formulasCache);

        sync(Collections.unmodifiableSet(add), Collections.unmodifiableSet(remove));

        // save the current state
        this.formulasCache = checkFormulas;
    }

    private boolean violatesExample(Collection<T> examples, boolean expectedResult) {
        Set<T> prevEx = Collections.EMPTY_SET;
        for (T example : examples) {
            Set<T> ex = Collections.singleton(example);
            sync(ex, prevEx);
            if (isConsistent() != expectedResult) {
                sync(Collections.EMPTY_SET, ex);
                return true;
            }
        }
        return false;
    }

    /**
     * Synchronizes the set of formulas with the solver by calling
     * {@link AbstractSolver#synchronizeCache(Collection)} and checks whether the set of formulas
     * <code>alpha</code> is entailed by calling {@link AbstractSolver#isEntailed(Collection)}
     *
     * @param formulas set of formulas
     * @param alpha    set of formulas that must be verified
     * @return a set of entailed formulas
     */
    @Override
    public boolean isEntailed(Collection<T> formulas, Collection<T> alpha) {
        synchronizeCache(formulas);
        return isEntailed(alpha);
    }

    /**
     * Synchronizes the set of formulas with the solver by calling
     * {@link AbstractSolver#synchronizeCache(Collection)} and checks whether the set of formulas
     * <code>alpha</code> is entailed by calling {@link AbstractSolver#calculateEntailments()}
     *
     * @param formulas set of formulas
     * @return a set of entailments of the set of formulas given as input
     */
    @Override
    public Set<T> calculateEntailments(Collection<T> formulas) {
        synchronizeCache(formulas);
        return calculateEntailments();
    }

    /**
     * Computation of entailments is specific for every solver. Please consult documentation of used implementation
     * for mode details.
     *
     * @return a set of entailments of the set of formulas stored in the solver
     */
    protected abstract Set<T> calculateEntailments();

    /**
     * Checks if the set of formulas in the solver entails the set of axioms.
     *
     * @param entailments set of formulas
     * @return <code>true</code> if the set of formulas is entailed
     */
    protected abstract boolean isEntailed(Collection<T> entailments);

    /**
     * Returns a negation of the input formula. This operation might be undefined for some solver APIs.
     *
     * @param example input formula
     * @return negation of the input formula
     */
    protected abstract T negate(T example);

    /**
     * Not every solver ASIs supports negation of formulas. For instance, OWL API does not support negation of axioms
     * . This method indicated whether the operation is supported.
     *
     * @return <code>true</code> if the underlying solver supports negation of formulas and <code>false</code>
     * otherwise
     */
    protected abstract boolean supportsNegation();

    /**
     * Synchronizes the formulas with the solver.
     *
     * @param addFormulas    a set of formulas that must to be added to the solver
     * @param removeFormulas a set of formulas that must be removed from the solver
     */
    protected abstract void sync(Set<T> addFormulas, Set<T> removeFormulas);

    /**
     * @return <code>true</code> if the set of formulas in the solver is consistent and <code>false</code> otherwise.
     */
    protected abstract boolean isConsistent();

    @Override
    public DiagnosisModel<T> getDiagnosisModel() {
        return this.diagnosisModel;
    }
}