package org.exquisite.core.query.qc.heuristic;

import org.exquisite.core.Utils;
import org.exquisite.core.costestimators.ICostsEstimator;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class which searches for the optimal q-partition from a set of diagnoses given some q-partition requirements
 * measures and some cost estimators.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author patrick
 * @author wolfi
 */
public class OptimalQPartitionFinder<F> {

    /**
     * Searches for an (nearly) optimal q-partition completely without reasoner support for some requirements rm,
     * a probability measure p and a set of leading diagnoses D as given input.
     *
     * @param diagnoses The leading diagnoses.
     * @param rm A partition requirements measure to find the (nearly) optimal q-partition.
     * @return A (nearly) optimal q-partition.
     */
    public static <F> QPartition<F> findQPartition(Set<Diagnosis<F>> diagnoses, IQPartitionRequirementsMeasure<F> rm, ICostsEstimator<F> costsEstimator) {
        assert diagnoses.size() >= 2;

        QPartition<F> partition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), costsEstimator);
        QPartition<F> bestPartition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), costsEstimator);

        OptimalPartition optimalPartition = findQPartitionRek(partition, bestPartition, rm);

        if (optimalPartition.partition.diagsTraits.isEmpty())
            optimalPartition.partition.computeDiagsTraits();

        return optimalPartition.partition;
    }

    private static <F> OptimalPartition<F> findQPartitionRek(QPartition<F> p, QPartition<F> pb, IQPartitionRequirementsMeasure<F> rm) {
        QPartition<F> pBest = rm.updateBest(p,pb);
        if (rm.isOptimal(pBest))
            return new OptimalPartition<>(pBest, true);
        if (rm.prune(p,pBest))
            return new OptimalPartition<>(pBest, false);

        Collection<QPartition<F>> sucs = p.computeSuccessors();
        while (!sucs.isEmpty()) {
            QPartition<F> p1 = bestSuc(sucs, rm);
            OptimalPartition optimalPartition = findQPartitionRek(p1, pBest, rm);
            if (!optimalPartition.isOptimal)
                pBest = optimalPartition.partition;
            else
                return optimalPartition;
            assert sucs.remove(p1);
        }
        return new OptimalPartition<>(pBest, false);
    }

    private static <F> QPartition<F> bestSuc(Collection<QPartition<F>> sucs, IQPartitionRequirementsMeasure<F> rm) {
        QPartition<F> sBest = Utils.getFirstElem(sucs, false);
        BigDecimal heurSBest = rm.getHeuristics(sBest);
        for (QPartition<F> s : sucs) {
            BigDecimal heurS = rm.getHeuristics(s);
            if (heurS.compareTo(heurSBest) < 0) { // (heurS < heurSBest)
                sBest = s;
                heurSBest = heurS;
            }
        }
        return sBest;
    }

    /**
     * Tuple mapping q-partition to info about optimality.
     */
    private static class OptimalPartition<F> {
        private QPartition<F> partition;
        private Boolean isOptimal;

        OptimalPartition(QPartition<F> partition, Boolean isOptimal) {
            this.partition = partition;
            this.isOptimal = isOptimal;
        }
    }
}
