package org.exquisite.core.query;

/**
 * Interface for query answering.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public interface IQueryAnswering<F> {

    /**
     * Get an answer to a query.
     *
     * @param query the query object.
     * @return an answer.
     */
    Answer<F> getAnswer(Query<F> query);
}
