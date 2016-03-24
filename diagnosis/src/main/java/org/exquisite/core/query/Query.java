package org.exquisite.core.query;

import java.util.HashSet;
import java.util.Set;

/**
 * A query is a set of logical formulas presented to the user.
 * The query is represented by the set of formulas that splits the diagnoses into a pPartition containing 3 sets of
 * diagnoses: dx, dnx, and dz.
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
