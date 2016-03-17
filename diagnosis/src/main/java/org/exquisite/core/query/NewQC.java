package org.exquisite.core.query;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Framework for New Query Computation Algorithm for interactive debugging.
 *
 * Created by pr8 and wolfi on 10.03.2015.
 */
public class NewQC<Formula> implements NewQueryComputation<Formula> {

    private static Logger logger = LoggerFactory.getLogger(NewQC.class);

    private QPartitionQualityMeasure qPartitionQualityMeasure;

    private QPartition<Formula> qPartition = null;

    private DiagnosisModel diagnosisModel;

    public NewQC() {
    }

    public NewQC(QPartitionQualityMeasure qPartitionQualityMeasure, DiagnosisModel diagnosisModel) {
        this.qPartitionQualityMeasure = qPartitionQualityMeasure;
        this.diagnosisModel = diagnosisModel;
    }

    @Override
    public void initialize(Set<Diagnosis<Formula>> diagnoses)
            throws DiagnosisException {

        List<Formula> kb = diagnosisModel.getPossiblyFaultyStatements();

        qPartition = findQPartition(diagnoses, diagnosisModel.getStatementWeights(), qPartitionQualityMeasure); // (2)

        selectQueryForQPartition(diagnoses, qPartition); // (3)

        enrichQuery(qPartition, diagnosisModel); // (4)

        Query<Formula> q = optimizeQuery(qPartition); // (5)

    }

    @Override
    public Set<Formula> next() {
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
     * TODO implement step (2) of Main Algorithm
     *
     * @param diagnoses TODO documentation
     * @param statementWeights TODO documentation
     * @param qPartitionQualityMeasure TODO documentation
     * @return TODO documentation
     */
    private QPartition<Formula> findQPartition(Set<Diagnosis<Formula>> diagnoses, Map statementWeights, QPartitionQualityMeasure qPartitionQualityMeasure) {

        // TODO implement (2) of Main Algorithm


        return null;
    }

    /**
     * TODO documentation
     *
     * @param diagnoses TODO documentation
     * @param qPartition TODO documentation
     */
    private void selectQueryForQPartition(Set<Diagnosis<Formula>> diagnoses, QPartition<Formula> qPartition) {
        // TODO implement (3) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @param diagnosisModel TODO documentation
     */
    private void enrichQuery(QPartition<Formula> qPartition, DiagnosisModel diagnosisModel) {
        // TODO implement (4) of main algorithm
    }

    /**
     * TODO documentation
     *
     * @param qPartition TODO documentation
     * @return TODO documentation
     */
    private Query<Formula> optimizeQuery(QPartition<Formula> qPartition) {
        // TODO implement (5) of main algorithm
        return null;
    }


    /**
     * Compute the successors in D+-Partitioning.
     *
     * This method represents the implementation of Algorithm 2 of the original paper.
     * In the method body we refer each statement to the line of the Algorithm 2 in the original paper.
     *
     * @param partitionPk A Partition Pk (containing dx, dnx, dz) with regard to D (the leading diagnoses).
     * @return The set of all canonical QPartitions sucs that result from Pk by a minimal D+-transformation.
     */
    public Collection<QPartition<Formula>> computeSuccessors(QPartition<Formula> partitionPk) {
        assert partitionPk.dx.isEmpty();
        assert partitionPk.dz.isEmpty();

        Collection<QPartition<Formula>> sucs = null;                        // line 2: stores successors of Parition Pk by a minimal
        Map<Diagnosis<Formula>,Set<Formula>> diagsTraits = null;            // line 3: stores tuples including a diagnosis and the trait of the eq. class w.r.t. it belongs to
        Set<Diagnosis<Formula>> eqClasses = new HashSet<>();                // line 4: set of sets of diagnoses, each set is eq. class with set-minimal trait

        if (partitionPk.dx.isEmpty()) {                                     // line 5: initial State, apply S_init
            sucs = generateInitialSuccessors(partitionPk);                  // line 6-7:
        } else {
            diagsTraits = computeDiagsTraits(partitionPk);                  // line 9-11: compute trait of eq. class
            Set<Diagnosis<Formula>> diags = new HashSet<>(partitionPk.dnx); // line 12: make a copy of dnx
            Set<Diagnosis<Formula>> minTraitDiags = new HashSet<>();        // line 13: to store one representative of each eq. class with set-minimial trait
            while (!diags.isEmpty()) {                                      // line 14:
                Diagnosis<Formula> Di = getFirst(diags);                    // line 15: TODO: clearify what is the first element in a set and clearify remove or not?
                Set<Diagnosis<Formula>> necFollowers = new HashSet<>();     // line 16: to store all necessary followers of Di
                boolean diagOK = true;                                      // line 17: will be set to false if Di is found to have a non-set-minimal trait
                Set<Diagnosis<Formula>> diagsAndMinTraitDiags = new HashSet<>(diags);
                diagsAndMinTraitDiags.addAll(minTraitDiags);
                Set<Formula> ti = diagsTraits.get(Di);
                for (Diagnosis<Formula> Dj : diagsAndMinTraitDiags) {        // line 18:
                    Set<Formula> tj = diagsTraits.get(Dj);
                    if (ti.containsAll(tj)) {                               // line 19:
                        if (ti.equals(tj)) {                                // line 20: equal trait, Di and Dj are same eq. class
                            necFollowers.add(Dj);
                        } else {
                            diagOK = false;                                 // line 21: TODO: clearify stop for loop on first false?
                        }
                    }
                }
                Set<Diagnosis<Formula>> diagDiAndNecFollowers = new HashSet<>(necFollowers);
                diagDiAndNecFollowers.add(Di);

                if (diagOK) {
                    eqClasses.addAll(diagDiAndNecFollowers);                // line 25:
                    minTraitDiags.add(Di);                                  // line 26: add one representative for eq. class
                }

                diags.removeAll(diagDiAndNecFollowers);                     // line 27: delete all representatives for eq. class
            }

            for (Diagnosis<Formula> E : eqClasses) {                        // line 28-29: construct all canonical successor q-partitions by means of eq.class
                Set<Diagnosis<Formula>> newDx = new HashSet<>(partitionPk.dx);
                boolean hasBeenAdded = newDx.add(E);
                assert hasBeenAdded;

                Set<Diagnosis<Formula>> newDnx = new HashSet<>(partitionPk.dnx);
                boolean hasBeenRemoved = newDnx.remove(E);
                assert hasBeenRemoved;

                QPartition<Formula> sucsPartition = new QPartition<>(newDx, newDnx, new HashSet<>());
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
     * @param partitionPk A Partition Pk (containing dx, dnx, dz) with regard to D (the leading diagnoses).
     * @return The set of initial successors of the QPartition partitionPk.
     */
    public static <Formula> Collection<QPartition<Formula>> generateInitialSuccessors(QPartition<Formula> partitionPk) {
        assert partitionPk.dx.isEmpty();
        assert partitionPk.dz.isEmpty();

        Collection<QPartition<Formula>> sucs = new HashSet<>();
        for (Diagnosis<Formula> diagnosis : partitionPk.dnx) {
            Set<Diagnosis<Formula>> new_dx = new HashSet<>(); // create the new dx (diagnoses that are supported by the query)
            new_dx.add(diagnosis);

            Set<Diagnosis<Formula>> new_dnx = new HashSet<>(partitionPk.dnx); // make a copy of the original dnx set...
            boolean isRemoved = new_dnx.remove(diagnosis); // and remove the current diagnosis
            assert isRemoved;

            Set<Diagnosis<Formula>> new_dz = new HashSet<>(partitionPk.dz); // make a copy of the original dz

            sucs.add(new QPartition<>(new_dx, new_dnx, new_dz));
        }
        return sucs;
    }

    /**
     * Compute the traits for each diagnosis in dnx of qPartition partitionPk and save them as a mapping in partitionPk.
     * Traits for a diagnosis in dnx represent formulas that do not also occur as a formula in dx of qPartition partitionPk.
     *
     * @param partitionPk A Partition Pk (containing dx, dnx, dz) with regard to D (the leading diagnoses).
     * @param <Formula> A formula from diagnosis.
     * @return Mapping from each diagnosis in dnx to it's traits. This mapping is stored in qPartition partitionPk.
     */
    public static <Formula> Map<Diagnosis<Formula>,Set<Formula>> computeDiagsTraits(QPartition<Formula> partitionPk) {

        //  compute the union of formulas of diagnoses dx of partitionPk
        Set<Formula> unitedDxFormulas = new HashSet<>();
        for (Diagnosis<Formula> diag_dx : partitionPk.dx)
            unitedDxFormulas.addAll(diag_dx.getFormulas());

        // compute trait of using unionDxFormulas
        for (Diagnosis<Formula> diag_dnx : partitionPk.dnx) {
            Set<Formula> traits = new HashSet<>(diag_dnx.getFormulas());    // initialize traits with the formulas of diag_dnx ...
            traits.removeAll(unitedDxFormulas);                             // ... and remove all formulas that occurred in dx of partionPk
            partitionPk.diagsTraits.put(diag_dnx, traits);                  // enables to retrieve trait ti for diagnosis di in later operations
        }
        return partitionPk.diagsTraits;
    }

    /**
     * Removes and returns the first diagnosis from diags.
     *
     * @param diags Set of diagnosis.
     * @param <Formula> A formula from diagnosis.
     * @return The first diagnosis in the set (side effect: it will be removed from the set).
     */
    public static <Formula> Diagnosis<Formula> getFirst(Set<Diagnosis<Formula>> diags) {
        assert !diags.isEmpty();

        Diagnosis<Formula> diagnosis = diags.iterator().next();
        diags.remove(diagnosis);
        return diagnosis;
    }
}
