package org.exquisite.core.query.qc.heuristic;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.Utils;
import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.IQueryComputation;
import org.exquisite.core.query.QPartition;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;
import org.exquisite.core.query.qc.heuristic.sortcriteria.MinQueryCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Framework for a heuristic Query Computation Algorithm for knowledge base debugging.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 *
 * @author wolfi
 * @author patrick
 */
public class HeuristicQC<F> implements IQueryComputation<F> {

    private static Logger logger = LoggerFactory.getLogger(HeuristicQC.class);

    private IQPartitionRequirementsMeasure rm;

    private QPartition<F> qPartition = null;

    private AbstractDiagnosisEngine<F> diagnosisEngine;

    private DiagnosisModel<F>  diagnosisModel;

    public HeuristicQC(IQPartitionRequirementsMeasure qPartitionRequirementsMeasure, AbstractDiagnosisEngine<F> diagnosisEngine) {
        this.rm = qPartitionRequirementsMeasure;
        this.diagnosisEngine = diagnosisEngine;
        this.diagnosisModel = diagnosisEngine.getSolver().getDiagnosisModel();
    }

    @Override
    public void initialize(Set<Diagnosis<F>> diagnoses)
            throws DiagnosisException {
        calcQuery(this.diagnosisModel, diagnoses, this.rm);
    }

    @Override
    public Query<F> next() {
        return qPartition.getQuery();
    }

    @Override
    public boolean hasNext() {
        return qPartition != null;
    }

    @Override
    public void reset() {
        this.qPartition = null;
    }

    /**
     * Query computation algorithm.
     *
     * @param diagnosisModel
     * @param leadingDiagnoses
     * @param rm some requirements in order to guide the search faster towards a (nearly) optimal q-partitions.
     */
    public void calcQuery(DiagnosisModel<F> diagnosisModel, Set<Diagnosis<F>> leadingDiagnoses, IQPartitionRequirementsMeasure rm) {

        // we start with the search for an (nearly) optimal q-partition, such that a query associated with this
        // q-partition can be extracted in the next step by selectQueryForQPartition
        qPartition = findQPartition(leadingDiagnoses, rm); // (2)

        // after a suitable q-partition has been identified, q query Q with qPartition(Q) is calculated such
        // that Q is optimal as to some criterion such as minimum cardinality or maximum likeliness of being
        // answered correctly.
        Set<F> query = selectQueryForQPartition(qPartition); // (3)

        // then in order to come up with a query that is as simple and easy to answer as possible for the
        // respective user U, this query Q can optionally enriched by additional logical formulas by invoking
        // a reasoner for entailments calculation.
        enrichQuery(qPartition, diagnosisModel); // (4)

        // the previous step causes a larger pool of formulas to select from in the query optimization step
        // which constructs a set-minimal query where most complex sentences in terms of the logical construct
        // and term fault estimates are eliminated from Q and the most simple ones retained
        Query<F> q = optimizeQuery(qPartition); // (5)
    }

    /**
     * Searches for an (nearly) optimal q-partition completely without reasoner support for some requirements rm,
     * a probability measure p and a set of leading diagnoses D as given input.
     *
     * @param diagnoses The leading diagnoses.
     * @param rm A partition requirements measure to find the (nearly) optimal q-partition.
     * @return A (nearly) optimal q-partition.
     */
    public QPartition<F> findQPartition(Set<Diagnosis<F>> diagnoses, IQPartitionRequirementsMeasure rm) {
        assert diagnoses.size() >= 2;

        QPartition<F> partition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), diagnosisEngine.getCostsEstimator());
        QPartition<F> bestPartition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), diagnosisEngine.getCostsEstimator());

        OptimalPartition optimalPartition = findQPartitionRek(partition, bestPartition, rm);

        if (optimalPartition.partition.diagsTraits.isEmpty())
            optimalPartition.partition.computeDiagsTraits();

        return optimalPartition.partition;
    }

    private OptimalPartition findQPartitionRek(QPartition<F> p, QPartition<F> pb, IQPartitionRequirementsMeasure rm) {
        QPartition<F> pBest = rm.updateBest(p,pb);
        if (rm.isOptimal(pBest))
            return new OptimalPartition(pBest, true);
        if (rm.prune(p,pBest))
            return new OptimalPartition(pBest, false);

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
        return new OptimalPartition(pBest, false);
    }

    public QPartition<F> bestSuc(Collection<QPartition<F>> sucs, IQPartitionRequirementsMeasure rm) {
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
     * A q query Q with qPartition(Q) is calculated such that Q is optimal as to some criterion such as minimum
     * cardinality or maximum likeliness of being answered correctly.
     *
     * @param qPartition TODO documentation
     */
    public Set<F> selectQueryForQPartition(QPartition<F> qPartition) {

        Set<Set<F>> setOfMinTraits = getSetOfMinTraits(qPartition.diagsTraits.values());

        Set<Set<F>> result = HittingSet.hittingSet(setOfMinTraits,1000,1,1,new MinQueryCardinality());
        if (result.isEmpty()) return null;
        return result.iterator().next();
    }

    /**
     * Compute the set of set-minimal traits.
     *
     * @param setOfDiagTraits Set of traits that might contain supersets.
     * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
     * @return A set-minimal set of traits.
     */
    public static<F> Set<Set<F>> getSetOfMinTraits(Collection<Set<F>> setOfDiagTraits) {
        Set<Set<F>> minTraits = new HashSet<>(setOfDiagTraits);
        Set<Set<F>> setOfMinTraits = new HashSet<>();

        while (!minTraits.isEmpty()) {
            Set<F> trait = Utils.getFirstElem(minTraits, true);
            boolean isTraitMinimal = true;
            for (Iterator<Set<F>> it = minTraits.iterator(); isTraitMinimal && it.hasNext();) {
                Set<F> t = it.next();
                isTraitMinimal &= !trait.containsAll(t);
            }

            for (Iterator<Set<F>> it = setOfMinTraits.iterator(); isTraitMinimal && it.hasNext();) {
                Set<F> t = it.next();
                isTraitMinimal &= !trait.containsAll(t);
            }

            if (isTraitMinimal)
                setOfMinTraits.add(trait);
        }

        return setOfMinTraits;
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @param diagnosisModel TODO documentation
     */
    public void enrichQuery(QPartition<F> qPartition, DiagnosisModel diagnosisModel) {
        // TODO implement (4) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @return TODO documentation
     */
    public Query<F> optimizeQuery(QPartition<F> qPartition) {
        // TODO implement (5) of main algorithm
        return null;
    }

    public IQPartitionRequirementsMeasure getPartitionRequirementsMeasure() {
        return rm;
    }

    public AbstractDiagnosisEngine<F> getDiagnosisEngine() {
        return diagnosisEngine;
    }

    /**
     * Tuple mapping q-partition to info about optimality.
     */
    class OptimalPartition {
        QPartition partition;
        Boolean isOptimal;

        public OptimalPartition(QPartition partition, Boolean isOptimal) {
            this.partition = partition;
            this.isOptimal = isOptimal;
        }
    }

}
