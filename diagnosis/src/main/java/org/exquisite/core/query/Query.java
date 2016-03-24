package org.exquisite.core.query;

import java.util.HashSet;
import java.util.Set;

/**
 * A query is a set of logical formulas presented to the user.
 * <p>
 * The query is represented by the set of formulas that splits the diagnoses into a pPartition containing 3 sets of
 * (minimal) diagnoses: dx, dnx, and dz.
 * </p>
 * <p>
 * In interactive KB debugging, a set of logical formulas Q is presented to the user who should decide whether to
 * assign Q to the set of positive (P) or negative (N) test cases w.r.t. a given diagnosis problem instance (DPI)
 * <K, B, P, N> (@see DiagnosisModel, K ).
 * </p>
 * <p>
 * We call a set of logical formulas Q a query when it has these properties:
 * <ul>
 *     <li>it invalidates at least one minimal diagnosis and (see QPartition.dnx)</li>
 *     <li>it preserves validity of at least one minimal diagnosis. (see QPartition.dx)</li>
 * </ul>
 * </p>
 * <p>
 *     So, w.r.t. a set of minimal diagnoses D, a query Q is a set of logical formulas that rules out at least
 *     one diagnosis in D as a candidate to formulate a solution KB, regardless of whether Q is defined as a
 *     positive ore negative test case (or example).
 * </p>
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 *
 * @author Schmitz
 * @author wolfi
 * @author patrick
 */
public class Query<F> {

    /**
     * A set of formulas representing the query.
     */
    public Set<F> formulas;

    /**
     * A partition representing the diagnoses split into the 3 parts dx, dnx and dz.
     */
    public QPartition<F> qPartition;

    public Query() {
        this.qPartition = new QPartition<>();
        this.formulas = new HashSet<>();
    }

    public Query(Set<F> formulas, QPartition<F> qPartition) {
        this.qPartition = qPartition;
        this.formulas = formulas;
    }
}
