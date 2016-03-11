package org.exquisite.core.query.scoring;


import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 10.02.12
 * Time: 11:19
 * To change this template use File | Settings | File Templates.
 */
public class MinScoreQSS<Formula> extends AbstractQSS<Formula> {

    private static Logger logger = LoggerFactory.getLogger(MinScoreQSS.class.getName());

    public BigDecimal getScore(Query<Formula> query) {
        if (query == null || query.dx.isEmpty())
            return BigDecimal.valueOf(Double.MAX_VALUE);
        if (query.score.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) < 0)
            return query.score;
        BigDecimal pX = sum(query.dx).add(sum(query.dz).divide(BigDecimal.valueOf(2), DEFAULT_MC));
        BigDecimal pNX = sum(query.dnx).add(sum(query.dz).divide(BigDecimal.valueOf(2), DEFAULT_MC));

        BigDecimal t1 = pX.multiply(log2(pX));
        BigDecimal t2 = pNX.multiply(log2(pNX));

        BigDecimal sc = t1.add(t2.add(sum(query.dz).add(BigDecimal.ONE)));

        if (sc.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Score is less than 0! sc=" + sc);
            sc = BigDecimal.ZERO;

        }

        query.score = sc;
        return sc;
    }

    private BigDecimal sum(Set<Diagnosis<Formula>> dx) {
        BigDecimal sum = new BigDecimal("0");
        for (Diagnosis<Formula> hs : dx)
            sum = sum.add(hs.getMeasure());
        return sum;
    }

    public String toString() {
        return "Entropy";
    }

    public void normalize(Set<Diagnosis<Formula>> diagnosises) {
        BigDecimal sum = sum(diagnosises);
        for (Diagnosis<Formula> hs : diagnosises) {
            BigDecimal value = hs.getMeasure().divide(sum, DEFAULT_MC);
            hs.setMeasure(value);
        }

    }

}
