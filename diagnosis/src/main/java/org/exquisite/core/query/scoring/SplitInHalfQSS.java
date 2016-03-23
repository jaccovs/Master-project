package org.exquisite.core.query.scoring;


import org.exquisite.core.model.Diagnosis;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 10.02.12
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class SplitInHalfQSS<F> extends MinScoreQSS<F> {

    public String toString() {
        return "Split";
    }

    public void normalize(Set<Diagnosis<F>> diagnosises) {
        BigDecimal size = new BigDecimal(Integer.toString(diagnosises.size()));
        if (size.compareTo(BigDecimal.ONE) > 0)
            for (Diagnosis<F> hs : diagnosises) {
                hs.setMeasure(BigDecimal.ONE.divide(size, DEFAULT_MC));
            }
    }
}
