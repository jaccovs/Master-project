package org.exquisite.core.solver;

import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.model.DiagnosisModel;

import java.util.*;
import java.util.stream.Collectors;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.*;

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
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 */
public abstract class AbstractSolver<F> implements ISolver<F>, Observer {

    private DiagnosisModel<F> diagnosisModel;
    private Set<F> formulasCache = new HashSet<>();
    private Map<F, F> negationsCache = new HashMap<>();

    public AbstractSolver(DiagnosisModel<F> diagnosisModel) {
        this.diagnosisModel = diagnosisModel;
        this.diagnosisModel.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof DiagnosisModel)
            checkDiagnosisModel();
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
    public boolean isConsistent(Collection<F> formulas) {
        start(TIMER_SOLVER_ISCONSISTENT_FORMULAS);
        incrementCounter(COUNTER_SOLVER_ISCONSISTENT_FORMULAS);
        try {
            synchronizeCache(formulas);

            if (!isConsistent())
                return false;

            // check non-entailed examples
            for (F example : diagnosisModel.getNotEntailedExamples()) {

                // some reasoners do not recognize such a trivial entailment
                // this fixes the 2nd example in issue #79
                if (formulas.contains(example)) {
                    return false;
                }

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
        } finally {
            stop(TIMER_SOLVER_ISCONSISTENT_FORMULAS);
        }

    }

    /**
     * Computes a set of formulas that must be added to the solver for consistency checking in the presence
     * of examples and background knowledge. Synchronizes the obtained set with the cache and sends the
     * difference sets to the underlying solvers by calling {@link AbstractSolver#sync(Set, Set)}
     *
     * @param formulas set of formulas
     */
    private void synchronizeCache(Collection<F> formulas) {
        Set<F> checkFormulas = new HashSet<>(this.formulasCache.size());
        checkFormulas.addAll(diagnosisModel.getCorrectFormulas());
        checkFormulas.addAll(diagnosisModel.getEntailedExamples());
        if (supportsNegation()) {
            for (F example : diagnosisModel.getInconsistentExamples()) {
                F neg = negationsCache.get(example);
                if (neg == null) {
                    neg = negate(example);
                    checkFormulas.add(neg);
                    this.negationsCache.put(example, neg);
                } else
                    checkFormulas.add(neg);
            }
        }

        // filter out all null-axioms to avoid assertion exception in OWL-API (MergeXPlain.mergeXPlain() adds null axioms)
        formulas.stream().filter(e -> e != null).collect(Collectors.toCollection(() -> checkFormulas));

        // sync the formulas with the solver
        HashSet<F> remove = new HashSet<>(this.formulasCache);
        HashSet<F> add = new HashSet<>(checkFormulas);

        remove.removeAll(add);
        add.removeAll(this.formulasCache);

        sync(Collections.unmodifiableSet(add), Collections.unmodifiableSet(remove));

        // save the current state
        this.formulasCache = checkFormulas;
    }

    /**
     * Verifies requirements to the diagnosis model such as (i) correct formulas with entailed test cases is consistent
     * and correct formulas with entailed tests do not entail any of the not-entailed tests.
     *
     * @throws DiagnosisRuntimeException if the diagnosis model violates some of the requirements. We throw a
     * {@link RuntimeException} since the observer pattern in java does not allow us to throw a normal exprection
     * or return a value.
     */
    void checkDiagnosisModel() {
        // The following 3 assertions have been deactivated because InverseDiagnosisEngine.recDepthFirstSearch causes
        // a call of this method on each model.getPossiblyFaultyFormulas().remove(formula) which might cause
        // that these assertions might fail.
        /*assert(this.diagnosisModel.getCorrectFormulas().stream().
                noneMatch(o -> this.diagnosisModel.getPossiblyFaultyFormulas().contains(o)));
        assert(this.diagnosisModel.getEntailedExamples().stream().
                noneMatch(o -> o!=null && this.diagnosisModel.getPossiblyFaultyFormulas().contains(o)));
        assert(this.diagnosisModel.getNotEntailedExamples().stream().
                noneMatch(o -> this.diagnosisModel.getPossiblyFaultyFormulas().contains(o)));*/
        if (!isConsistent(Collections.emptySet()))
            throw new DiagnosisRuntimeException("Inconsistent diagnosis model!");
    }

    private boolean violatesExample(Collection<F> examples, boolean expectedResult) {
        Set<F> prevEx = Collections.emptySet();
        for (F example : examples) {
            Set<F> ex = Collections.singleton(example);
            sync(ex, prevEx);
            if (isConsistent() != expectedResult) {
                sync(Collections.emptySet(), ex);
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
    public boolean isEntailed(Collection<F> formulas, Collection<F> alpha) {
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
    public Set<F> calculateEntailments(Collection<F> formulas) {
        synchronizeCache(formulas);
        return calculateEntailments();
    }

    /**
     * Computation of entailments is specific for every solver. Please consult documentation of used implementation
     * for more details.
     *
     * @return a set of entailments of the set of formulas stored in the solver
     */
    protected abstract Set<F> calculateEntailments();

    /**
     * Checks if the set of formulas in the solver entails the set of axioms.
     *
     * @param entailments set of formulas
     * @return <code>true</code> if the set of formulas is entailed
     */
    protected abstract boolean isEntailed(Collection<F> entailments);

    /**
     * Returns a negation of the input formula. This operation might be undefined for some solver APIs.
     *
     * @param example input formula
     * @return negation of the input formula
     */
    protected abstract F negate(F example);

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
    protected abstract void sync(Set<F> addFormulas, Set<F> removeFormulas);

    /**
     * @return <code>true</code> if the set of formulas in the solver is consistent and <code>false</code> otherwise.
     */
    protected abstract boolean isConsistent();

    @Override
    public void dispose() {
        this.formulasCache.clear();
        this.negationsCache.clear();
        if (diagnosisModel!=null) {
            this.diagnosisModel.deleteObserver(this);
            this.diagnosisModel = null;
        }
    }

    @Override
    public DiagnosisModel<F> getDiagnosisModel() {
        return this.diagnosisModel;
    }
}
