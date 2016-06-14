package org.exquisite.core.query;

import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.Utils;
import org.exquisite.core.costestimators.ICostsEstimator;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;

import java.math.BigDecimal;
import java.util.*;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.*;

/**
 * Class which offers operations for q-partition instances such as searching for a (nearly) optimal q-partition from a
 * set of diagnoses given some q-partition requirements measures and some cost estimators. Or computation of successor
 * q-partitions or computation of traits.
 *
 * @author patrick
 * @author wolfi
 */
public class QPartitionOperations {

    /**
     * Searches for an (nearly) optimal q-partition completely without reasoner support for some requirements rm,
     * a probability measure p and a set of leading diagnoses D as given input.
     *
     * @param diagnoses The leading diagnoses.
     * @param rm A partition requirements measure to find the (nearly) optimal q-partition.
     * @param costsEstimator A cost estimator used for computing the probabilities of dx and dnx.
     * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
     * @return A (nearly) optimal q-partition.
     * @throws DiagnosisRuntimeException Throws a DiagnosisRuntimeException, if not more than one leading diagnoses are given.
     */
    public static <F> QPartition<F> findQPartition(Set<Diagnosis<F>> diagnoses, IQPartitionRequirementsMeasure<F> rm, ICostsEstimator<F> costsEstimator) throws DiagnosisRuntimeException {
        if (diagnoses.size() < 2) throw new DiagnosisRuntimeException("For query computation at least 2 leading diagnoses are required. But only " + diagnoses.size() + " leading diagnoses are given.");

        QPartition<F> partition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), costsEstimator);
        QPartition<F> bestPartition = new QPartition<>(new HashSet<>(), diagnoses, new HashSet<>(), costsEstimator);

        OptimalPartition optimalPartition = findQPartitionRek(partition, bestPartition, rm, new HashSet<>());

        if (optimalPartition.partition.diagsTraits.isEmpty())
            optimalPartition.partition.diagsTraits = QPartitionOperations.computeDiagsTraits(optimalPartition.partition);

        return optimalPartition.partition;
    }

    private static <F> OptimalPartition<F> findQPartitionRek(QPartition<F> p, QPartition<F> pb, IQPartitionRequirementsMeasure<F> rm, Set<Diagnosis<F>> alreadyUsedDiagsOnLeft) {
        Set<Diagnosis<F>> alreadyUsedTraits = new HashSet<>(alreadyUsedDiagsOnLeft);
        QPartition<F> pBest = rm.updateBest(p,pb);
        if (rm.isOptimal(pBest) && pBest.dx.size() != 0) // do not accept root q-partititons (dx.size() == 0) as optimal partitions
            return new OptimalPartition<>(pBest, true);

        if (rm.prune(p,pBest)) {// ADD 1 to CNT_PRUNING_OPERATIONS; and; ADD 1 to CNT_BACKTRACKING_OPERATIONS
            incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_BACKTRACKINGS);
            incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_PRUNINGS);
            return new OptimalPartition<>(pBest, false);
        }

        incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_EXPANDED_QPARTITIONS); // add 1 to CNT_EXPANDED_QPARTITIONS (ALLE Ps, FÜR DIE NACHFOLGER GEBILDET WURDEN)
        Collection<QPartition<F>> sucs = computeSuccessors(p, alreadyUsedDiagsOnLeft);
        incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_GENERATED_QPARTITIONS, sucs.size()); // add sucs.size() (CNT_GENERATED_QPARTITIONS)
        while (!sucs.isEmpty()) {
            SuccessorPlusDiagRepresentativeForEqClass<F> successor = bestSuc(p, sucs, rm);
            QPartition<F> p1 = successor.qPartition;
            //QPartition<F> p1 = bestSuc(p, sucs, rm);
            OptimalPartition optimalPartition = findQPartitionRek(p1, pBest, rm, alreadyUsedTraits);
            alreadyUsedTraits.add(successor.diagnosis);

            if (!optimalPartition.isOptimal)
                pBest = optimalPartition.partition;
            else
                return optimalPartition;
            final boolean remove = sucs.remove(p1);
            assert remove;
        }
        incrementCounter(COUNTER_QUERYCOMPUTATION_HEURISTIC_BACKTRACKINGS); // ADD 1 to CNT_BACKTRACKING_OPERATIONS
        return new OptimalPartition<>(pBest, false);
    }

    private static <F> SuccessorPlusDiagRepresentativeForEqClass<F> bestSuc(QPartition<F> p, Collection<QPartition<F>> sucs, IQPartitionRequirementsMeasure<F> rm) {
        QPartition<F> sBest = Utils.getFirstElem(sucs, false);
        BigDecimal heurSBest = rm.getHeuristics(sBest);
        for (QPartition<F> s : sucs) {
            BigDecimal heurS = rm.getHeuristics(s);
            if (heurS.compareTo(heurSBest) < 0) { // (heurS < heurSBest)
                sBest = s;
                heurSBest = heurS;
            }
        }
        Set<Diagnosis<F>> dxDiff = new HashSet<>(sBest.dx);
        dxDiff.removeAll(p.dx);

        Diagnosis<F> representativeOfEqClass = Utils.getFirstElem(dxDiff, false);
        return new SuccessorPlusDiagRepresentativeForEqClass<F>(representativeOfEqClass,sBest);
    }

    /**
     * Compute the successors in D+-Partitioning.
     *
     * This method represents the implementation of Algorithm 7 of the original paper.
     * In the method body we refer each statement to the line of the Algorithm 7 in the original paper.
     *
     * @param qPartition The q-partition whose successors this method is computing.
     * @param alreadyUsedDiagsOnLeft The diagnoses already used on the left branch of the tree. We can omit those diags. Use empty set for root partitions.
     * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
     * @return The set of all canonical QPartitions sucs that result from Pk by a minimal D+-transformation.
     */
    public static <F> Collection<QPartition<F>> computeSuccessors(QPartition<F> qPartition, Set<Diagnosis<F>> alreadyUsedDiagsOnLeft) {
        assert qPartition.dz.isEmpty();

        Collection<QPartition<F>> sucs = new HashSet<>();                           // line 2: stores successors of Parition Pk by a minimal
        qPartition.diagsTraits = new HashMap<>();                                   // line 3: stores tuples including a diagnosis and the trait of the eq. class w.r.t. it belongs to
        Set<Set<Diagnosis<F>>> eqClasses = new HashSet<>();                         // line 4: set of sets of diagnoses, each set is eq. class with set-minimal trait

        if (qPartition.dx.isEmpty()) {                                              // line 5: initial State, apply S_init
            sucs = generateInitialSuccessors(qPartition);                           // line 6-7:
        } else {                                                                    // line 8: Pk is canonical q-partition, apply Snext
            qPartition.diagsTraits = computeDiagsTraits(qPartition);                // line 9-11: compute trait of eq. class, enables to retrive ti for Di in operations below
            Set<Diagnosis<F>> diags = new HashSet<>(qPartition.dnx);                // line 12: make a copy of dnx
            Set<Diagnosis<F>> minTraitDiags = new HashSet<>();                      // line 13: to store one representative of each eq. class with set-minimial trait
            boolean sucsExist = false;                                              // line 14: will be set to true if Pk is found to have some canonical successor q-partition

            int counter = 0;

            while (!diags.isEmpty()) {                                              // line 15:
                Diagnosis<F> Di = Utils.getFirstElem(diags, true);                  // line 16: Di is first (any) element in diags and diags := diags - Di (getFirstElem removes Di from diags)
                final Set<F> traitOfDi = qPartition.diagsTraits.get(Di);
                boolean diagAlreadyUsedAsSuccessor = false;

                for (Diagnosis<F> alreadyUsedDiag : alreadyUsedDiagsOnLeft) {
                    if (traitOfDi.equals(qPartition.diagsTraits.get(alreadyUsedDiag))) {
                        counter++;
                        assert counter <= alreadyUsedDiagsOnLeft.size();

                        diagAlreadyUsedAsSuccessor = true;
                        break; // stop for-loop
                    }
                }

                Set<Diagnosis<F>> necFollowers = new HashSet<>();                   // line 17: to store all necessary followers of Di
                boolean diagOK = true;                                              // line 18: will be set to false if Di is found to have a non-set-minimal trait
                Set<Diagnosis<F>> diagsAndMinTraitDiags = new HashSet<>(diags);     // prepare unification of diags with minTraitDiags
                diagsAndMinTraitDiags.addAll(minTraitDiags);
                Set<F> ti = qPartition.diagsTraits.get(Di);
                for (Diagnosis<F> Dj : diagsAndMinTraitDiags) {                     // line 19:
                    Set<F> tj = qPartition.diagsTraits.get(Dj);
                    if (ti.containsAll(tj))                                         // line 20:
                        if (ti.equals(tj))                                          // line 21: equal trait, Di and Dj are same eq. class
                            necFollowers.add(Dj);                                   // line 22:
                        else                                                        // line 23:
                            diagOK = false;                                         // line 24: eq. class of Di has a non-set-minimal trait
                }

                Set<Diagnosis<F>> eqCls = new HashSet<>(necFollowers);              // line 25:
                eqCls.add(Di);                                                      // line 25:

                if (!sucsExist && eqCls.equals(qPartition.dnx))                     // line 26: test only run in first iteration of while-loop
                    return new HashSet<>();                                         // line 27: Pk has single successor partition which is no q-partition due to dnx = empty set
                sucsExist = true;                                                   // line 28: existence of equal or more than 1 canonical successor q-partition in Pk guaranteed

                if (diagOK) {                                                       // line 29:
                    if (!diagAlreadyUsedAsSuccessor) {
                        eqClasses.add(eqCls);                                       // line 30:
                    }
                    minTraitDiags.add(Di);                                          // line 31: add one representative for eq. class
                }
                diags.removeAll(necFollowers);                                      // line 32: delete all representatives for eq. class
            }

            for (Set<Diagnosis<F>> E : eqClasses) {                                 // line 33-34: construct all canonical successor q-partitions by means of eq.class
                Set<Diagnosis<F>> newDx = new HashSet<>(qPartition.dx);
                boolean hasBeenAdded = newDx.addAll(E);
                assert hasBeenAdded;

                Set<Diagnosis<F>> newDnx = new HashSet<>(qPartition.dnx);
                boolean hasBeenRemoved = newDnx.removeAll(E);
                assert hasBeenRemoved;

                QPartition<F> sucsPartition = new QPartition<>(newDx, newDnx, new HashSet<>(), qPartition.costEstimator);
                sucs.add(sucsPartition);
            }
        }

        return sucs;
    }

    /**
     * Generates the initial state for the computation of successors of partitionPk.
     *
     * e.g. if the input is the QPartition <{},{D1,D2,D3},{}> then the result will be:
     * {<{D1},{D2,D3},{}>, <{D1},{D2,D3},{}>, <{D1},{D2,D3},{}>}
     *
     * @return The set of initial successors of the QPartition partitionPk.
     */
    private static <F> Collection<QPartition<F>> generateInitialSuccessors(QPartition<F> qPartition) {
        assert qPartition.dx.isEmpty();
        assert qPartition.dz.isEmpty();

        Collection<QPartition<F>> sucs = new HashSet<>();
        for (Diagnosis<F> diagnosis : qPartition.dnx) {
            Set<Diagnosis<F>> new_dx = new HashSet<>(); // create the new dx (diagnoses that are supported by the query)
            new_dx.add(diagnosis);

            Set<Diagnosis<F>> new_dnx = new HashSet<>(qPartition.dnx); // make a copy of the original dnx set...
            boolean isRemoved = new_dnx.remove(diagnosis); // and remove the current diagnosis
            assert isRemoved;

            Set<Diagnosis<F>> new_dz = new HashSet<>(qPartition.dz); // make a copy of the original dz

            sucs.add(new QPartition<>(new_dx, new_dnx, new_dz, qPartition.costEstimator));
        }
        return sucs;
    }

    /**
     * Compute the traits for each diagnosis in dnx of qPartition partitionPk and save them as a mapping in partitionPk.
     * Traits for a diagnosis in dnx represent formulas that do not also occur as a formula in dx of qPartition partitionPk.
     *
     * @return Mapping from each diagnosis in dnx to it's traits. This mapping is stored in qPartition partitionPk.
     */
    public static <F> Map<Diagnosis<F>,Set<F>> computeDiagsTraits(QPartition<F> qPartition) {
        assert !qPartition.dx.isEmpty();

        Map<Diagnosis<F>,Set<F>> diagsTraits = new HashMap<>();

        //  compute the union of formulas of diagnoses dx of partitionPk
        Set<F> unitedDxFormulas = new HashSet<>();
        for (Diagnosis<F> diag_dx : qPartition.dx)
            unitedDxFormulas.addAll(diag_dx.getFormulas());

        // compute trait of using unionDxFormulas
        for (Diagnosis<F> diag_dnx : qPartition.dnx) {
            Set<F> traits = new HashSet<>(diag_dnx.getFormulas());    // initialize traits with the formulas of diag_dnx ...
            traits.removeAll(unitedDxFormulas);                       // ... and remove all formulas that occurred in dx of partionPk
            diagsTraits.put(diag_dnx, traits);                        // enables to retrieve trait ti for diagnosis di in later operations
        }
        return diagsTraits;
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

    private static class SuccessorPlusDiagRepresentativeForEqClass<F> {
        private QPartition<F> qPartition;
        private Diagnosis<F> diagnosis;

        private SuccessorPlusDiagRepresentativeForEqClass(Diagnosis<F> diagnosis, QPartition<F> qPartition) {
            this.diagnosis = diagnosis;
            this.qPartition = qPartition;
        }
    }
}
