package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A query of constraints that splits the diagnoses into the 3 parts dx, dnx, and dz.
 *
 * @author Schmitz
 */
public class Query<F> {

    public Set<F> formulas;

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
