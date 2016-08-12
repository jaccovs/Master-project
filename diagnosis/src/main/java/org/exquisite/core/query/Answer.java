package org.exquisite.core.query;

import java.util.Set;
import java.util.TreeSet;

/**
 * Answer given by the user. An answer represents formulas that are supported, not supported and unaffected.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public class Answer<F> {

    /**
     * Formulas that are supported by the answer.
     */
    public Set<F> positive = new TreeSet<>();

    /**
     * Formulas that are not supported by the answer.
     */
    public Set<F> negative = new TreeSet<>();

    /**
     * Formulas that are unaffected by the answer.
     */
    public Set<F> undefined = new TreeSet<>();

    /**
     * The query given to the answer.
     */
    public Query<F> query;
}
