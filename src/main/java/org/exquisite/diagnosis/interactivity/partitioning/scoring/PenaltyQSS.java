package org.exquisite.diagnosis.interactivity.partitioning.scoring;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.exquisite.diagnosis.interactivity.partitioning.Partition;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 13.02.12
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */
public class PenaltyQSS extends MinScoreQSS {

    double maxPenalty;
    double penalty;



    public PenaltyQSS(double maxPenalty) {
        this.maxPenalty = maxPenalty;
        penalty = 0d;
    }


    protected boolean canExceedMaxPenalty(Partition partition) {
        return penalty + getMaxPenaltyOfQuery(partition) > maxPenalty;
    }


    protected int getMaxPenaltyOfQuery(Partition partition){
        return (int)Math.floor((double) numOfLeadingDiags/(double)2) - getMinNumOfElimDiags(partition);
    }


    public void updateParameters(boolean answerToLastQuery){

        preprocessBeforeUpdate(answerToLastQuery);

        penalty += (int)Math.floor((double) numOfLeadingDiags/(double)2) - numOfEliminatedLeadingDiags;
    }


    private Partition bestNonCandidate(List<Partition> nonCandidates){

        List<Partition> nonCandidatesFiltered = new LinkedList<Partition>();

        for(Partition partition : nonCandidates){
            if(partition.dx.size() != partition.dnx.size())
                nonCandidatesFiltered.add(partition);
        }

        Partition bestNonCandidate = Collections.max(nonCandidatesFiltered, new MinNumOfElimDiagsComparator());

        for(Partition partition : nonCandidatesFiltered){
            if(getMinNumOfElimDiags(partition) == getMinNumOfElimDiags(bestNonCandidate) && partition.score.compareTo(bestNonCandidate.score) < 0)
                bestNonCandidate = partition;
        }

        return bestNonCandidate;
    }


    private Partition bestCandidate(List<Partition> candidates){
        Partition bestCandidate = Collections.min(candidates,new ScoreComparator());
        return bestCandidate;
    }


    public Partition runPostprocessor(List<Partition> partitions, Partition currentBest) {
        preprocessBeforeRun(getPartitionSearcher().getNumOfHittingSets());

        List<Partition> candidates = new LinkedList<Partition>();
        List<Partition> nonCandidates = new LinkedList<Partition>();


        for (Partition partition : partitions) {
            if (!canExceedMaxPenalty(partition))
                candidates.add(partition);
            else
                nonCandidates.add(partition);
        }
        

        Partition result;

        if (candidates.isEmpty()){
            result = bestNonCandidate(nonCandidates);
        }else{
            result = bestCandidate(candidates);
        }

        lastQuery = result;
        return result;
        
    }

}
