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

    @Override
    public BigDecimal getScore(Query<F> query) {
        Integer absDxMinusDnx = Math.abs(query.qPartition.dx.size()-query.qPartition.dnx.size());
        Integer scoreInteger = absDxMinusDnx + query.qPartition.dz.size();
        BigDecimal score = new BigDecimal(scoreInteger.toString());
        return score;
    }

    @Override
    public void normalize(Set<Diagnosis<F>> diagnoses) {
        throw new RuntimeException();
    }

    public String toString() {
        return "SPL";
    }

}
