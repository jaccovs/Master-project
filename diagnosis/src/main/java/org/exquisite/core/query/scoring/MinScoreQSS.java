package org.exquisite.core.query.scoring;


import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Set;

/**
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author pfleiss
 */
public class MinScoreQSS<F> extends AbstractQSS<F> {

    private static Logger logger = LoggerFactory.getLogger(MinScoreQSS.class.getName());

    public BigDecimal getScore(Query<F> query) {
        if (query == null || query.qPartition.dx.isEmpty())
            return BigDecimal.valueOf(Double.MAX_VALUE);
        if (query.score.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) < 0)
            return query.score;
        BigDecimal pX = sum(query.qPartition.dx).add(sum(query.qPartition.dz).divide(BigDecimal.valueOf(2), DEFAULT_MC));
        BigDecimal pNX = sum(query.qPartition.dnx).add(sum(query.qPartition.dz).divide(BigDecimal.valueOf(2), DEFAULT_MC));

        BigDecimal t1 = pX.multiply(log2(pX));
        BigDecimal t2 = pNX.multiply(log2(pNX));

        BigDecimal sc = t1.add(t2.add(sum(query.qPartition.dz).add(BigDecimal.ONE)));

        if (sc.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Score is less than 0! sc=" + sc);
            sc = BigDecimal.ZERO;

        }

        query.score = sc;
        return sc;
    }

    private BigDecimal sum(Set<Diagnosis<F>> dx) {
        BigDecimal sum = new BigDecimal("0");
        for (Diagnosis<F> hs : dx)
            sum = sum.add(hs.getMeasure());
        return sum;
    }

    public String toString() {
        return "Entropy";
    }

    public void normalize(Set<Diagnosis<F>> diagnoses) {
        BigDecimal sum = sum(diagnoses);
        for (Diagnosis<F> hs : diagnoses) {
            BigDecimal value = hs.getMeasure().divide(sum, DEFAULT_MC);
            hs.setMeasure(value);
        }

    }

}
