package org.exquisite.core.query.scoring;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Query Selection interface
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public interface IQuerySelection<F> {

    BigDecimal getScore(Query<F> query);

    void normalize(Set<Diagnosis<F>> diagnoses);
}