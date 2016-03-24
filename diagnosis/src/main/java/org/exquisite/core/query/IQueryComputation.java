package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.DiagnosisException;

import java.util.Set;

/**
 * Interface for query computation (GC).
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public interface IQueryComputation<F> {

    /**
     * Initialize query computation.
     *
     * @param diagnoses Set of diagnoses to compute queries.
     * @throws DiagnosisException
     */
    void initialize(Set<Diagnosis<F>> diagnoses) throws DiagnosisException;

    /**
     * Get next query.
     *
     * @return the next query.
     */
    Query<F> next();

    /**
     * Is there a next query?
     *
     * @return <code>true</code> if there is another query, otherwise <code>false</code>.
     */
    boolean hasNext();

    /**
     * Reset.
     */
    void reset();
}
