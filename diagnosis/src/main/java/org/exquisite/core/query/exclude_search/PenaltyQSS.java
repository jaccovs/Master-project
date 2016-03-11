package org.exquisite.core.query.exclude_search;

import org.exquisite.core.engines.query.Query;
import org.exquisite.core.engines.query.scoring.MinScoreQSS;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 13.02.12
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */
public class PenaltyQSS<T> extends MinScoreQSS<T> {

    double maxPenalty;
    double penalty;


    public PenaltyQSS(double maxPenalty) {
        this.maxPenalty = maxPenalty;
        penalty = 0d;
    }


    protected boolean canExceedMaxPenalty(Query query) {
        return penalty + getMaxPenaltyOfQuery(query) > maxPenalty;
    }


    protected int getMaxPenaltyOfQuery(Query query) {
        return (int) Math.floor((double) numOfLeadingDiags / (double) 2) - getMinNumOfElimDiags(query);
    }


    public void updateParameters(boolean answerToLastQuery) {

        preprocessBeforeUpdate(answerToLastQuery);

        penalty += (int) Math.floor((double) numOfLeadingDiags / (double) 2) - numOfEliminatedLeadingDiags;
    }


    private Query<T> bestNonCandidate(List<Query<T>> nonCandidates) {

        List<Query<T>> nonCandidatesFiltered = new LinkedList<>();

        for (Query<T> query : nonCandidates) {
            if (query.dx.size() != query.dnx.size())
                nonCandidatesFiltered.add(query);
        }

        Query<T> bestNonCandidate = Collections.max(nonCandidatesFiltered, new MinNumOfElimDiagsComparator());

        for (Query<T> query : nonCandidatesFiltered) {
            if (getMinNumOfElimDiags(query) == getMinNumOfElimDiags(bestNonCandidate) && query.score
                    .compareTo(bestNonCandidate.score) < 0)
                bestNonCandidate = query;
        }

        return bestNonCandidate;
    }


    private Query<T> bestCandidate(List<Query<T>> candidates) {
        return Collections.min(candidates, new ScoreComparator());
    }


    public Query<T> runPostprocessor(List<Query<T>> queries, Query<T> currentBest) {
        preprocessBeforeRun(getPartitionSearcher().getNumOfHittingSets());

        List<Query<T>> candidates = new LinkedList<>();
        List<Query<T>> nonCandidates = new LinkedList<>();


        for (Query<T> query : queries) {
            if (!canExceedMaxPenalty(query))
                candidates.add(query);
            else
                nonCandidates.add(query);
        }


        Query<T> result;

        if (candidates.isEmpty()) {
            result = bestNonCandidate(nonCandidates);
        } else {
            result = bestCandidate(candidates);
        }

        lastQuery = result;
        return result;

    }

}
