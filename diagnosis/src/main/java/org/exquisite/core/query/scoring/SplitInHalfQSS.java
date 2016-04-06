package org.exquisite.core.query.scoring;

import org.exquisite.core.model.Diagnosis;

import java.math.BigDecimal;
import java.util.Set;

/**
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author pfleiss
 */
public class SplitInHalfQSS<F> extends MinScoreQSS<F> {

    public String toString() {
        return "Split";
    }

    public void normalize(Set<Diagnosis<F>> diagnoses) {
        BigDecimal size = new BigDecimal(Integer.toString(diagnoses.size()));
        if (size.compareTo(BigDecimal.ONE) > 0)
            for (Diagnosis<F> hs : diagnoses) {
                hs.setMeasure(BigDecimal.ONE.divide(size, DEFAULT_MC));
            }
    }
}
