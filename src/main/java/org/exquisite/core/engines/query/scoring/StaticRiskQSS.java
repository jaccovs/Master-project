package org.exquisite.core.engines.query.scoring;

import org.exquisite.core.engines.query.Query;

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
    protected double numOfLeadingDiags;
    protected double numOfEliminatedLeadingDiags;
    private int numOfHittingSets;

    public StaticRiskQSS(double c) {
        super();
        this.c = c;
    }


    protected Query<T> selectMinScorePartition(List<Query<T>> partitions, Query<T> currentBest) {
        //return super.runPostprocessor(partitions, currentBest);
        return null;
    }

    protected int getMaxPossibleNumOfDiagsToEliminate() {
        return (int) Math.floor(numOfLeadingDiags / 2d);
    }

    protected void preprocessC() {
        double maxPossibleC;
        if ((maxPossibleC = (double) this.getMaxPossibleNumOfDiagsToEliminate() /
                numOfLeadingDiags) < c) {
            c = maxPossibleC;
        } else if (c < 0d)
            c = 0;
    }

    protected int convertCToNumOfDiags(double c) {
        int num = (int) Math.ceil(numOfLeadingDiags * c);
        if (num > (numOfLeadingDiags / 2d)) {
            num--;
        }
        return num;
    }

    protected LinkedList<Query<T>> getLeastCautiousNonHighRiskPartitions(int numOfDiagsToElim, List<Query<T>> partitions) {
        LinkedList<Query<T>> leastCautiousNonHighRiskQueries = new LinkedList<Query<T>>();
        for (Query<T> p : partitions) {
            if (getMinNumOfElimDiags(p) == numOfDiagsToElim) {
                leastCautiousNonHighRiskQueries.add(p);
            }
        }
        return leastCautiousNonHighRiskQueries;
    }

    protected void preprocessBeforeRun(int numOfLeadingDiags) {
        // order of method calls IMPORTANT
        //updateNumOfLeadingDiags(numOfLeadingDiags);
        preprocessC();
    }

    public Query<T> runPostprocessor(List<Query<T>> partitions, Query<T> currentBest) {
        int count = 0;

        //int numOfHittingSets = getPartitionSearcher().getNumOfHittingSets();
        preprocessBeforeRun(numOfHittingSets);
        int numOfDiagsToElim = convertCToNumOfDiags(c);
        for (Query<T> partition : partitions) {
            if (count++ > partitions.size() * 0.05)
                break;
            if (!partition.isVerified && partition.dx.size() <= numOfHittingSets - numOfDiagsToElim)
                break;
            //      getPartitionSearcher().verifyPartition(partition);
        }


        Query<T> minScorePartition, lastQuery;

        if (getMinNumOfElimDiags((minScorePartition = selectMinScorePartition(partitions, currentBest))) >= numOfDiagsToElim) {
            lastQuery = minScorePartition;
            return lastQuery;
        }

        for (; numOfDiagsToElim <= getMaxPossibleNumOfDiagsToEliminate(); numOfDiagsToElim++) {
            LinkedList<Query<T>> leastCautiousNonHighRiskPartitions = getLeastCautiousNonHighRiskPartitions(numOfDiagsToElim, partitions);     // candidateQueries = X_min,k
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










