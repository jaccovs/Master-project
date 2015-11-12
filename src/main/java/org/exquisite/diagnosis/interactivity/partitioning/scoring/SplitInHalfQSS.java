package org.exquisite.diagnosis.interactivity.partitioning.scoring;

import java.math.BigDecimal;
import java.util.Set;

import org.exquisite.diagnosis.models.Diagnosis;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 10.02.12
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class SplitInHalfQSS extends MinScoreQSS {

    public String toString() {
        return "Split";
    }

    public void normalize(Set<Diagnosis> hittingSets) {
        BigDecimal size = new BigDecimal(Integer.toString(hittingSets.size()));
        if (size.compareTo(BigDecimal.ONE)>0)
            for (Diagnosis hs : hittingSets) {
                hs.setMeasure(BigDecimal.ONE.divide(size, Rounding.PRECISION,Rounding.ROUNDING_MODE));
            }
    }
}
