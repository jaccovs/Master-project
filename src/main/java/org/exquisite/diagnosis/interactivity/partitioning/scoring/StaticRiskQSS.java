package org.exquisite.diagnosis.interactivity.partitioning.scoring;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.interactivity.partitioning.Partition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pr8
 * Date: 13.02.12
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */


public class StaticRiskQSS<T> extends MinScoreQSS<T> {

    protected double c;


    public StaticRiskQSS(double c) {
        super();
        this.c = c;
    }


    protected Partition selectMinScorePartition(List<Partition<T>> partitions, Partition<T> currentBest)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        return super.runPostprocessor(partitions, currentBest);
    }

    protected int getMaxPossibleNumOfDiagsToEliminate() {
        return (int) Math.floor(numOfLeadingDiags / 2d);
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

    protected LinkedList<Partition<T>> getLeastCautiousNonHighRiskPartitions(int numOfDiagsToElim,
                                                                             List<Partition<T>> partitions) {
        LinkedList<Partition<T>> leastCautiousNonHighRiskQueries = new LinkedList<>();
        for (Partition<T> p : partitions) {
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

    public Partition<T> runPostprocessor(List<Partition<T>> partitions, Partition<T> currentBest)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        int count = 0;

        int numOfHittingSets = getPartitionSearcher().getNumOfHittingSets();
        preprocessBeforeRun(numOfHittingSets);
        int numOfDiagsToElim = convertCToNumOfDiags(c);
        for (Partition<T> partition : partitions) {
            if (count++ > partitions.size() * 0.05)
                break;
            if (!partition.isVerified && partition.dx.size() <= numOfHittingSets - numOfDiagsToElim)
                getPartitionSearcher().verifyPartition(partition);
        }


        Partition minScorePartition;

        if (getMinNumOfElimDiags(
                (minScorePartition = selectMinScorePartition(partitions, currentBest))) >= numOfDiagsToElim) {
            lastQuery = minScorePartition;
            return lastQuery;
        }

        for (; numOfDiagsToElim <= getMaxPossibleNumOfDiagsToEliminate(); numOfDiagsToElim++) {
            LinkedList<Partition<T>> leastCautiousNonHighRiskPartitions = getLeastCautiousNonHighRiskPartitions(
                    numOfDiagsToElim, partitions);     // candidateQueries = X_min,k
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










