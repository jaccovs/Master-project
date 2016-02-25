package org.exquisite.core.engines.query.scoring;

import org.exquisite.core.engines.query.Query;
import org.exquisite.core.model.Diagnosis;
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
public abstract class AbstractQSS<Formula> implements QuerySelection<Formula> {

    public final MathContext DEFAULT_MC = MathContext.DECIMAL128;


    protected BigDecimal log2(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;

        return log(value).
                divide(BigDecimalMath.LOG2, DEFAULT_MC);
    }

    protected int getMinNumOfElimDiags(Query query) {
        return Math.min(query.dx.size(), query.dnx.size());
    }


    protected BigDecimal getQueryScore(Query<Formula> query) {
        BigDecimal sumDx = getSumProb(query.dx);
        BigDecimal sumDnx = getSumProb(query.dnx);
        BigDecimal sumD0 = getSumProb(query.dz);

        BigDecimal temp = sumDx.multiply(log2(sumDx));
        temp = temp.add(sumDnx.multiply(log2(sumDnx)));

        return temp.add(sumD0).add(BigDecimal.ONE);

    }

    protected BigDecimal getSumProb(Set<Diagnosis<Formula>> set) {
        BigDecimal pr = new BigDecimal("0");
        for (Diagnosis<Formula> diagnosis : set)
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

    public class ScoreComparator implements Comparator<Query<Formula>> {
        public int compare(Query<Formula> o1, Query<Formula> o2) {
            if (getQueryScore(o1).compareTo(getQueryScore(o2)) < 0)
                return -1;
            else if (getQueryScore(o1).compareTo(getQueryScore(o2)) > 0)
                return 1;
            else
                return -1 * ((Integer) o1.dx.size()).compareTo(o2.dx.size());

        }
    }

}
