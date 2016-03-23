package org.exquisite.core.query.scoring;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;
import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.Set;

import static org.nevec.rjm.BigDecimalMath.log;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 10.02.12
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractQSS<F> implements QuerySelection<F> {

    public final MathContext DEFAULT_MC = MathContext.DECIMAL128;


    protected BigDecimal log2(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;

        return log(value).
                divide(BigDecimalMath.LOG2, DEFAULT_MC);
    }

    protected int getMinNumOfElimDiags(Query query) {
        return Math.min(query.qPartition.dx.size(), query.qPartition.dnx.size());
    }


    protected BigDecimal getQueryScore(Query<F> query) {
        BigDecimal sumDx = getSumProb(query.qPartition.dx);
        BigDecimal sumDnx = getSumProb(query.qPartition.dnx);
        BigDecimal sumD0 = getSumProb(query.qPartition.dz);

        BigDecimal temp = sumDx.multiply(log2(sumDx));
        temp = temp.add(sumDnx.multiply(log2(sumDnx)));

        return temp.add(sumD0).add(BigDecimal.ONE);

    }

    protected BigDecimal getSumProb(Set<Diagnosis<F>> set) {
        BigDecimal pr = new BigDecimal("0");
        for (Diagnosis<F> diagnosis : set)
            pr = pr.add(diagnosis.getMeasure());

        return pr;
    }

    public class MinNumOfElimDiagsComparator implements Comparator<Query> {
        public int compare(Query o1, Query o2) {
            if (getMinNumOfElimDiags(o1) < getMinNumOfElimDiags(o2))
                return -1;
            else if (getMinNumOfElimDiags(o1) > getMinNumOfElimDiags(o2))
                return 1;
            else
                return 0;

        }
    }

    public class ScoreComparator implements Comparator<Query<F>> {
        public int compare(Query<F> o1, Query<F> o2) {
            if (getQueryScore(o1).compareTo(getQueryScore(o2)) < 0)
                return -1;
            else if (getQueryScore(o1).compareTo(getQueryScore(o2)) > 0)
                return 1;
            else
                return -1 * ((Integer) o1.qPartition.dx.size()).compareTo(o2.qPartition.dx.size());

        }
    }

}
