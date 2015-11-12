package org.exquisite.diagnosis.interactivity.partitioning.scoring;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.interactivity.partitioning.Partition;
import org.exquisite.diagnosis.models.Diagnosis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 10.02.12
 * Time: 11:19
 * To change this template use File | Settings | File Templates.
 */
public class MinScoreQSS extends AbstractQSS {

    private static Logger logger = LoggerFactory.getLogger(MinScoreQSS.class.getName());

    public BigDecimal getScore(Partition partition) {
        if (partition == null || partition.dx.isEmpty())
            return BigDecimal.valueOf(Double.MAX_VALUE);
        if (partition.score.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) < 0)
            return partition.score;
        BigDecimal pX = sum(partition.dx).add(sum(partition.dz).divide(BigDecimal.valueOf(2)));
        BigDecimal pNX = sum(partition.dnx).add(sum(partition.dz).divide(BigDecimal.valueOf(2)));

        BigDecimal t1 = pX.multiply(log(pX, BigDecimal.valueOf(2)));
        BigDecimal t2 = pNX.multiply(log(pNX, BigDecimal.valueOf(2)));

        BigDecimal sc = t1.add(t2.add(sum(partition.dz).add(BigDecimal.ONE)));

        if (sc.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Score is less than 0! sc=" + sc);
            sc = BigDecimal.ZERO;

        }

        partition.score = sc;
        return sc;
    }

    private BigDecimal sum(Set<Diagnosis> dx) {
        BigDecimal sum = new BigDecimal("0");
        for (Diagnosis hs : dx)
            sum = sum.add(hs.getMeasure());
        return sum;
    }

    public String toString() {
        return "Entropy";
    }

    public void normalize(Set<Diagnosis> hittingSets) {
        BigDecimal sum = sum(hittingSets);
        for (Diagnosis hs : hittingSets) {
            BigDecimal value = hs.getMeasure().divide(sum, Rounding.PRECISION, Rounding.ROUNDING_MODE);
            hs.setMeasure(value);
        }

    }
    
    public Partition runPostprocessor(List<Partition> partitions, Partition currentBest) throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        //if (partitions!=null && partitions.size() > 0) {
        //    lastQuery = Collections.min(partitions,new ScoreComparator());
        //    return lastQuery;
        //}

        return currentBest;
    }


}
