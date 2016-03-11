package org.exquisite.core.query.exclude_search;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.query.Query;
import org.exquisite.core.engines.query.scoring.MinScoreQSS;

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


    protected Query selectMinScorePartition(List<Query<T>> queries, Query<T> currentBest)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        return super.runPostprocessor(queries, currentBest);
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

    protected LinkedList<Query<T>> getLeastCautiousNonHighRiskPartitions(int numOfDiagsToElim,
                                                                         List<Query<T>> queries) {
        LinkedList<Query<T>> leastCautiousNonHighRiskQueries = new LinkedList<>();
        for (Query<T> p : queries) {
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

    public Query<T> runPostprocessor(List<Query<T>> queries, Query<T> currentBest)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        int count = 0;

        int numOfHittingSets = getPartitionSearcher().getNumOfHittingSets();
        preprocessBeforeRun(numOfHittingSets);
        int numOfDiagsToElim = convertCToNumOfDiags(c);
        for (Query<T> query : queries) {
            if (count++ > queries.size() * 0.05)
                break;
            if (!query.isVerified && query.dx.size() <= numOfHittingSets - numOfDiagsToElim)
                getPartitionSearcher().verifyPartition(query);
        }


        Query minScoreQuery;

        if (getMinNumOfElimDiags(
                (minScoreQuery = selectMinScorePartition(queries, currentBest))) >= numOfDiagsToElim) {
            lastQuery = minScoreQuery;
            return lastQuery;
        }

        for (; numOfDiagsToElim <= getMaxPossibleNumOfDiagsToEliminate(); numOfDiagsToElim++) {
            LinkedList<Query<T>> leastCautiousNonHighRiskQueries = getLeastCautiousNonHighRiskPartitions(
                    numOfDiagsToElim, queries);     // candidateQueries = X_min,k
            if (leastCautiousNonHighRiskQueries.isEmpty()) {
                continue;
            }
            lastQuery = Collections.min(leastCautiousNonHighRiskQueries, new ScoreComparator());
            return lastQuery;
        }

        lastQuery = Collections.min(queries, new ScoreComparator());
        return lastQuery;

    }


    @Override
    public void updateParameters(boolean answerToLastQuery) {

    }
}










