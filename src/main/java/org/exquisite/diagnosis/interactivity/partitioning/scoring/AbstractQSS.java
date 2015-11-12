package org.exquisite.diagnosis.interactivity.partitioning.scoring;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Set;

import org.exquisite.diagnosis.interactivity.partitioning.Partition;
import org.exquisite.diagnosis.interactivity.partitioning.Partitioning;
import org.exquisite.diagnosis.models.Diagnosis;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 10.02.12
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractQSS implements QSS {

    protected int numOfLeadingDiags;
    protected Partition lastQuery = null;
    protected boolean answerToLastQuery;
    protected int numOfEliminatedLeadingDiags = 0;
    private Partitioning partitionSearcher;


    protected BigDecimal log(BigDecimal value, BigDecimal base) {
        if (value.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;

        return BigFunctions.ln(value,value.scale()).
                divide(BigFunctions.ln(base, value.scale()), value.scale(), BigDecimal.ROUND_HALF_EVEN);
    }

    protected int getMinNumOfElimDiags(Partition partition) {
        return Math.min(partition.dx.size(), partition.dnx.size());
    }

    public void updateNumOfLeadingDiags(int numOfLeadingDiags) {
        this.numOfLeadingDiags = numOfLeadingDiags;
    }

    public void updateAnswerToLastQuery(boolean answer) {
        answerToLastQuery = answer;
    }

    protected void updateNumOfEliminatedLeadingDiags(boolean answer){
        if(lastQuery != null)
            numOfEliminatedLeadingDiags = getNumOfEliminatedLeadingDiags(answer);
    }

    protected void preprocessBeforeUpdate(boolean answer){
        updateAnswerToLastQuery(answer);
        updateNumOfEliminatedLeadingDiags(answer);
    }

    protected void preprocessBeforeRun(int numOfLeadingDiags) {
        updateNumOfLeadingDiags(numOfLeadingDiags);
    }

    protected int getNumOfLeadingDiags(Partition partition){
        return partition.dx.size() + partition.dnx.size() + partition.dz.size();
    }

    protected int getNumOfEliminatedLeadingDiags(boolean answer){
        if (answer)
            return lastQuery.dnx.size();
        else
            return lastQuery.dx.size();
    }

    public void updateParameters(boolean answer) {
        preprocessBeforeUpdate(answer);
    }


    public class MinNumOfElimDiagsComparator implements Comparator<Partition> {
        public int compare(Partition o1, Partition o2) {
            if (getMinNumOfElimDiags(o1) < getMinNumOfElimDiags(o2))
                return -1;
            else if (getMinNumOfElimDiags(o1) > getMinNumOfElimDiags(o2))
                return 1;
            else
                return 0;

        }
    }



    protected BigDecimal getPartitionScore(Partition partition) {
        BigDecimal sumDx = getSumProb(partition.dx);
        BigDecimal sumDnx = getSumProb(partition.dnx);
        BigDecimal sumD0 = getSumProb(partition.dz);


        BigDecimal temp = sumDx.multiply(log(sumDx, new BigDecimal("2")));
        temp = temp.add(sumDnx.multiply(log(sumDnx, new BigDecimal("2"))));


        return temp.add(sumD0).add(BigDecimal.ONE);

    }

    protected BigDecimal getSumProb(Set<Diagnosis> set) {
        BigDecimal pr = new BigDecimal("0");
        for (Diagnosis diagnosis : set)
            pr = pr.add(diagnosis.getMeasure());

        return pr;
    }

    protected Partitioning getPartitionSearcher() {
        return partitionSearcher;
    }

    public void setPartitionSearcher(Partitioning partitioning) {
        this.partitionSearcher = partitioning;
    }

    public class ScoreComparator implements Comparator<Partition> {
        public int compare(Partition o1, Partition o2) {
            if (getPartitionScore(o1).compareTo(getPartitionScore(o2)) < 0)
                return -1;
            else if (getPartitionScore(o1).compareTo(getPartitionScore(o2)) > 0)
                return 1;
            else
                return -1*((Integer)o1.dx.size()).compareTo(o2.dx.size());

        }
    }
    
}
