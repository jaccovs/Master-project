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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Answer<?> answer = (Answer<?>) o;

        if (positive != null ? !positive.equals(answer.positive) : answer.positive != null) return false;
        if (negative != null ? !negative.equals(answer.negative) : answer.negative != null) return false;
        if (undefined != null ? !undefined.equals(answer.undefined) : answer.undefined != null) return false;
        return query != null ? query.equals(answer.query) : answer.query == null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Answer{");
        sb.append("query=").append(query);
        sb.append(", positive=").append(positive);
        sb.append(", negative=").append(negative);
        sb.append(", undefined=").append(undefined);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = positive != null ? positive.hashCode() : 0;
        result = 31 * result + (negative != null ? negative.hashCode() : 0);
        result = 31 * result + (undefined != null ? undefined.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        return result;
    }
}
