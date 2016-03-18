package org.exquisite.core.query;

import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.costestimators.CostsEstimator;
import org.exquisite.core.model.Diagnosis;

import java.math.BigDecimal;
import java.util.*;

/**
 * A qPartiton object of constraints that splits the diagnoses into the 3 parts dx, dnx, and dz.
 *
 * Copy of Query and slightly adapted for requirements of NewQC.
 *
 * Created by pr8 and wolfi on 10.03.2015.
 */
public class QPartition<Formula> {
    /**
     * Diagnoses that are supported by the query
     */
    public Set<Diagnosis<Formula>> dx = new HashSet<>();

    /**
     * Diagnoses that are not supported by the query
     */
    public Set<Diagnosis<Formula>> dnx = new HashSet<>();

    /**
     * Diagnoses that are unaffected by the query
     */
    public Set<Diagnosis<Formula>> dz = new HashSet<>();

    /**
     * Uniquely defined query for a given Q-Partition, used in the search for Q-Partitions.
     */

    public Set<Formula> canonicalQuery;

    /**
     * Set of queries consisting of only explicit entailments (=formulas in KB) which have exactly this Q-Partition. Does not necessarily include all queries for this Q-Partition.
     */
    public Set<Set<Formula>> explicitEntailmentsQueries;

    /**
     * Set of queries consisting of not only explicit entailments (=formulas in KB) which have exactly this Q-Partition. Does not necessarily include all queries for this Q-Partition.
     */
    public Set<Set<Formula>>enrichedQueries;

    /**
     * Set of queries that the user rejected to answer.
     */
    public Set<Set<Formula>> rejectedQueries;

    /**
     * Traits, used in Algorithm 2 (Computing successor in D+-Partitioning)
     */
    public Map<Diagnosis<Formula>,Set<Formula>> diagsTraits = new HashMap<>();

    /**
     *
     */
    public BigDecimal score = BigDecimal.valueOf(Double.MAX_VALUE);

    /**
     *
     */
    public BigDecimal difference = new BigDecimal(Double.MAX_VALUE);

    /**
     *
     */
    public boolean isVerified = false;

    public CostsEstimator<Formula> costEstimator = null;

    public BigDecimal dxProbs;

    public BigDecimal dnxProbs;

    /**
     * A QPartition which
     *
     * @param dx Diagnoses that are supported by the query.
     * @param dnx Diagnoses that are not supported by the query.
     * @param dz Diagnoses that are unaffected by the query.
     * @param  costestimator Costestimator.
     */
    public QPartition(Set<Diagnosis<Formula>> dx, Set<Diagnosis<Formula>> dnx, Set<Diagnosis<Formula>> dz, CostsEstimator<Formula> costestimator) {
        this.dx = dx;
        this.dnx = dnx;
        this.dz = dz;
        this.costEstimator = costestimator;

        this.dxProbs = computeProbs(this.dx);
        this.dnxProbs = computeProbs(this.dnx);
    }


    private BigDecimal computeProbs(Set<Diagnosis<Formula>> diags) {
        BigDecimal sum = BigDecimal.ZERO;
        if (costEstimator!=null) {
            for (Diagnosis<Formula> d : diags) {
                sum = sum.add(costEstimator.getFormulasCosts(d.getFormulas()));
            }
        }
        return sum;
    }
    /**
     * Return the result of query computation.
     *
     * @return Set of Formulas
     */
    public Set<Formula> getQuery() {
        if (!enrichedQueries.isEmpty()) {
            return enrichedQueries.iterator().next();
        } else if (!explicitEntailmentsQueries.isEmpty()) {
            return explicitEntailmentsQueries.iterator().next();
        } else {
            return canonicalQuery;
        }
    }

    /**
     * Compute the successors in D+-Partitioning.
     *
     * This method represents the implementation of Algorithm 2 of the original paper.
     * In the method body we refer each statement to the line of the Algorithm 2 in the original paper.
     *
     * @return The set of all canonical QPartitions sucs that result from Pk by a minimal D+-transformation.
     */
    public Collection<QPartition<Formula>> computeSuccessors() {
        assert dz.isEmpty();

        Collection<QPartition<Formula>> sucs = new HashSet<>();                                                         // line 2: stores successors of Parition Pk by a minimal
        this.diagsTraits = new HashMap<>();                                                                             // line 3: stores tuples including a diagnosis and the trait of the eq. class w.r.t. it belongs to
        Set<Set<Diagnosis<Formula>>> eqClasses = new HashSet<>();                                                       // line 4: set of sets of diagnoses, each set is eq. class with set-minimal trait

        if (dx.isEmpty()) {                                                                                             // line 5: initial State, apply S_init
            sucs = generateInitialSuccessors();                                                                         // line 6-7:
        } else {
            diagsTraits = computeDiagsTraits();                                                                         // line 9-11: compute trait of eq. class
            Set<Diagnosis<Formula>> diags = new HashSet<>(dnx);                                                         // line 12: make a copy of dnx
            Set<Diagnosis<Formula>> minTraitDiags = new HashSet<>();                                                    // line 13: to store one representative of each eq. class with set-minimial trait
            boolean sucsExist = false;
                                                                                                                        // line 14: stores the currently found equivalence class
            while (!diags.isEmpty()) {                                                                                  // line 15:
                Diagnosis<Formula> Di = getFirst(diags);                                                                // line 15: first element (any element) in a set and remove it from diags
                Set<Diagnosis<Formula>> necFollowers = new HashSet<>();                                                 // line 16: to store all necessary followers of Di
                boolean diagOK = true;                                                                                  // line 17: will be set to false if Di is found to have a non-set-minimal trait
                Set<Diagnosis<Formula>> diagsAndMinTraitDiags = new HashSet<>(diags);
                diagsAndMinTraitDiags.addAll(minTraitDiags);
                Set<Formula> ti = diagsTraits.get(Di);
                for (Diagnosis<Formula> Dj : diagsAndMinTraitDiags) {                                                   // line 18:
                    Set<Formula> tj = diagsTraits.get(Dj);
                    if (ti.containsAll(tj)) {                                                                           // line 19:
                        if (ti.equals(tj)) {                                                                            // line 20: equal trait, Di and Dj are same eq. class
                            necFollowers.add(Dj);
                        } else {
                            diagOK = false;                                                                             // line 21:
                        }
                    }
                }

                Set<Diagnosis<Formula>> eqCls = new HashSet<>(necFollowers);
                eqCls.add(Di);

                if (!sucsExist && eqCls.equals(dnx)) {
                    return new HashSet<>();
                }
                sucsExist = true;

                if (diagOK) {
                    eqClasses.add(eqCls);
                    minTraitDiags.add(Di);                                                                              // line 26: add one representative for eq. class
                }

                diags.removeAll(necFollowers);                                                                          // line 27: delete all representatives for eq. class
            }

            for (Set<Diagnosis<Formula>> E : eqClasses) {                                                               // line 28-29: construct all canonical successor q-partitions by means of eq.class
                Set<Diagnosis<Formula>> newDx = new HashSet<>(dx);
                boolean hasBeenAdded = newDx.addAll(E);
                assert hasBeenAdded;

                Set<Diagnosis<Formula>> newDnx = new HashSet<>(dnx);
                boolean hasBeenRemoved = newDnx.removeAll(E);
                assert hasBeenRemoved;

                QPartition<Formula> sucsPartition = new QPartition<>(newDx, newDnx, new HashSet<>(), this.costEstimator);
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
    public Collection<QPartition<Formula>> generateInitialSuccessors() {
        assert dx.isEmpty();
        assert dz.isEmpty();

        Collection<QPartition<Formula>> sucs = new HashSet<>();
        for (Diagnosis<Formula> diagnosis : dnx) {
            Set<Diagnosis<Formula>> new_dx = new HashSet<>(); // create the new dx (diagnoses that are supported by the query)
            new_dx.add(diagnosis);

            Set<Diagnosis<Formula>> new_dnx = new HashSet<>(dnx); // make a copy of the original dnx set...
            boolean isRemoved = new_dnx.remove(diagnosis); // and remove the current diagnosis
            assert isRemoved;

            Set<Diagnosis<Formula>> new_dz = new HashSet<>(dz); // make a copy of the original dz

            sucs.add(new QPartition<>(new_dx, new_dnx, new_dz, this.costEstimator));
        }
        return sucs;
    }

    /**
     * Compute the traits for each diagnosis in dnx of qPartition partitionPk and save them as a mapping in partitionPk.
     * Traits for a diagnosis in dnx represent formulas that do not also occur as a formula in dx of qPartition partitionPk.
     *
     * @return Mapping from each diagnosis in dnx to it's traits. This mapping is stored in qPartition partitionPk.
     */
    public Map<Diagnosis<Formula>,Set<Formula>> computeDiagsTraits() {
        assert !dx.isEmpty();

        //  compute the union of formulas of diagnoses dx of partitionPk
        Set<Formula> unitedDxFormulas = new HashSet<>();
        for (Diagnosis<Formula> diag_dx : dx)
            unitedDxFormulas.addAll(diag_dx.getFormulas());

        // compute trait of using unionDxFormulas
        for (Diagnosis<Formula> diag_dnx : dnx) {
            Set<Formula> traits = new HashSet<>(diag_dnx.getFormulas());    // initialize traits with the formulas of diag_dnx ...
            traits.removeAll(unitedDxFormulas);                             // ... and remove all formulas that occurred in dx of partionPk
            diagsTraits.put(diag_dnx, traits);                              // enables to retrieve trait ti for diagnosis di in later operations
        }
        return diagsTraits;
    }

    /**
     * Removes and returns the first diagnosis from diags.
     *
     * @param diags Set of diagnosis.
     * @param <Formula> A formula from diagnosis.
     * @return The first diagnosis in the set (side effect: it will be removed from the set).
     */
    private static <Formula> Diagnosis<Formula> getFirst(Set<Diagnosis<Formula>> diags) {
        assert !diags.isEmpty();

        Diagnosis<Formula> diagnosis = diags.iterator().next();
        diags.remove(diagnosis);
        return diagnosis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QPartition<?> that = (QPartition<?>) o;

        if (isVerified != that.isVerified) return false;
        if (!dx.equals(that.dx)) return false;
        if (!dnx.equals(that.dnx)) return false;
        if (!dz.equals(that.dz)) return false;
        if (canonicalQuery != null ? !canonicalQuery.equals(that.canonicalQuery) : that.canonicalQuery != null)
            return false;
        if (explicitEntailmentsQueries != null ? !explicitEntailmentsQueries.equals(that.explicitEntailmentsQueries) : that.explicitEntailmentsQueries != null)
            return false;
        if (enrichedQueries != null ? !enrichedQueries.equals(that.enrichedQueries) : that.enrichedQueries != null)
            return false;
        if (rejectedQueries != null ? !rejectedQueries.equals(that.rejectedQueries) : that.rejectedQueries != null)
            return false;
        if (diagsTraits != null ? !diagsTraits.equals(that.diagsTraits) : that.diagsTraits != null) return false;
        if (score != null ? !score.equals(that.score) : that.score != null) return false;
        return difference != null ? difference.equals(that.difference) : that.difference == null;

    }

    @Override
    public int hashCode() {
        int result = dx.hashCode();
        result = 31 * result + dnx.hashCode();
        result = 31 * result + dz.hashCode();
        result = 31 * result + (canonicalQuery != null ? canonicalQuery.hashCode() : 0);
        result = 31 * result + (explicitEntailmentsQueries != null ? explicitEntailmentsQueries.hashCode() : 0);
        result = 31 * result + (enrichedQueries != null ? enrichedQueries.hashCode() : 0);
        result = 31 * result + (rejectedQueries != null ? rejectedQueries.hashCode() : 0);
        result = 31 * result + (diagsTraits != null ? diagsTraits.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (difference != null ? difference.hashCode() : 0);
        result = 31 * result + (isVerified ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QPartition{" +
                "dx=" + dx +
                ", dnx=" + dnx +
                ", dz=" + dz +
                ", canonicalQuery=" + canonicalQuery +
                ", explicitEntailmentsQueries=" + explicitEntailmentsQueries +
                ", enrichedQueries=" + enrichedQueries +
                ", rejectedQueries=" + rejectedQueries +
                ", diagsTraits=" + diagsTraits +
                ", score=" + score +
                ", difference=" + difference +
                ", isVerified=" + isVerified +
                '}';
    }
}
