package org.exquisite.core.query.scoring;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;

import java.math.BigDecimal;
import java.util.Set;

/**
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author pfleiss
 */
public class SplitInHalf1QSS<F> implements IQuerySelection<F> {

    public String toString() {
        return "Split1";
    }

    @Override
    public BigDecimal getScore(Query<F> query) {
        return new BigDecimal("" + Math.abs(query.qPartition.dx.size()-query.qPartition.dnx.size()) + query.qPartition.dz.size());
    }

    @Override
    public void normalize(Set<Diagnosis<F>> diagnoses) {
        throw new RuntimeException();
    }

}
