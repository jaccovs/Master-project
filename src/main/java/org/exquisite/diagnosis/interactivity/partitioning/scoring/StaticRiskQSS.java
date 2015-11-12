package org.exquisite.diagnosis.interactivity.partitioning.scoring;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.interactivity.partitioning.Partition;

/**
 * Created by IntelliJ IDEA.
 * User: pr8
 * Date: 13.02.12
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */


public class StaticRiskQSS extends MinScoreQSS {

    protected double c;


    public StaticRiskQSS(double c) {
        super();
        this.c = c;
    }


    protected Partition selectMinScorePartition(List<Partition> partitions, Partition currentBest) throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        return super.runPostprocessor(partitions, currentBest);
    }

    protected int getMaxPossibleNumOfDiagsToEliminate() {
        return (int) Math.floor((double) (numOfLeadingDiags / 2d));
    }

    protected void preprocessC() {
        double maxPossibleC;
        if ((maxPossibleC = (double) this.getMaxPossibleNumOfDiagsToEliminate() / (double) numOfLeadingDiags) < c) {
            c = maxPossibleC;
        } else if (c < 0d)
            c = 0;
    }

    protected int convertCToNumOfDiags(double c) {
        int num = (int) Math.ceil((double) numOfLeadingDiags * c);
        if (num > ((double) numOfLeadingDiags / 2d)) {
            num--;
        }
        return num;
    }

    protected LinkedList<Partition> getLeastCautiousNonHighRiskPartitions(int numOfDiagsToElim, List<Partition> partitions) {
        LinkedList<Partition> leastCautiousNonHighRiskQueries = new LinkedList<Partition>();
        for (Partition p : partitions) {
            if (getMinNumOfElimDiags(p) == numOfDiagsToElim) {
                leastCautiousNonHighRiskQueries.add(p);
            }
        }
        return leastCautiousNonHighRiskQueries;
    }

    protected void preprocessBeforeRun(int numOfLeadingDiags) {
        // order of method calls IMPORTANT
        updateNumOfLeadingDiags(numOfLeadingDiags);
        preprocessC();
    }

    public Partition runPostprocessor(List<Partition> partitions, Partition currentBest) throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        int count = 0;

        int numOfHittingSets = getPartitionSearcher().getNumOfHittingSets();
        preprocessBeforeRun(numOfHittingSets);
        int numOfDiagsToElim = convertCToNumOfDiags(c);
        for (Partition partition : partitions) {
            if (count++ > partitions.size()*0.05)
                break;
            if (!partition.isVerified && partition.dx.size() <= numOfHittingSets - numOfDiagsToElim)
                getPartitionSearcher().verifyPartition(partition);
        }


        Partition minScorePartition;

        if (getMinNumOfElimDiags((minScorePartition = selectMinScorePartition(partitions, currentBest))) >= numOfDiagsToElim) {
            lastQuery = minScorePartition;
            return lastQuery;
        }

        for (; numOfDiagsToElim <= getMaxPossibleNumOfDiagsToEliminate(); numOfDiagsToElim++) {
            LinkedList<Partition> leastCautiousNonHighRiskPartitions = getLeastCautiousNonHighRiskPartitions(numOfDiagsToElim, partitions);     // candidateQueries = X_min,k
            if (leastCautiousNonHighRiskPartitions.isEmpty()) {
                continue;
            }
            lastQuery = Collections.min(leastCautiousNonHighRiskPartitions, new ScoreComparator());
            return lastQuery;
        }

        lastQuery = Collections.min(partitions, new ScoreComparator());
        return lastQuery;

    }


}










