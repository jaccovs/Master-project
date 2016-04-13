package org.exquisite.core.query.scoring;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author wolfi
 */
public class RIOQSS<F> implements IQuerySelection<F> {

    private BigDecimal c;

    public RIOQSS(BigDecimal c) {
        this.c = c;
    }

    @Override
    public BigDecimal getScore(Query<F> query) {

        int minNumOfDiagsInDx = convertCToNumOfDiags(this.c.doubleValue(), query);
        int sizeOfDx = query.qPartition.dx.size();

        Integer score1 = (sizeOfDx >= minNumOfDiagsInDx) ? sizeOfDx-minNumOfDiagsInDx : Integer.MAX_VALUE;
        if (score1.equals(Integer.MAX_VALUE))
            return new BigDecimal(score1.toString());

        double v = new MinScoreQSS<F>().getScore(query).doubleValue(); // expected range of v is [0..2) therefore take 3 :-)
        double score2 = v / 3; // [0..1)

        Double result = score1.doubleValue() + score2;

        return new BigDecimal(result.toString());

    }

    @Override
    public void normalize(Set<Diagnosis<F>> diagnoses) {
        throw new RuntimeException("not implemented!");
    }

    protected int convertCToNumOfDiags(double c, Query<F> query) {
        int numOfLeadingDiags = query.qPartition.dx.size() + query.qPartition.dnx.size() + query.qPartition.dz.size();

        int num = (int) Math.ceil(numOfLeadingDiags * c);
        if (num > (numOfLeadingDiags / 2d)) {
            num--;
        }
        return num;
    }

    @Override
    public String toString() {
        return "RIO(" + c + ')';
    }
}
