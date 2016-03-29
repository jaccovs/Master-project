package org.exquisite.core.query;

import org.exquisite.core.Utils;
import org.exquisite.core.costestimators.ICostsEstimator;
import org.exquisite.core.model.Diagnosis;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

/**
 * A qPartition is a partition of the diagnoses set D induced by a query w.r.t. D into the 3 parts dx, dnx, and dz.
 * <p>
 *     A q-partition is a helpful instrument in deciding whether a set of logical formulas is a query or not. I will
 *     facilitate an estimation of the impact a query answer has in terms of invalidation of minimal diagnoses.
 * </p>
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author wolfi
 * @author patrick
 */
public class QPartition<F> {

    /**
     * Diagnoses that are supported by the query
     */
    public Set<Diagnosis<F>> dx = new HashSet<>();

    /**
     * Diagnoses that are not supported by the query
     */
    public Set<Diagnosis<F>> dnx = new HashSet<>();

    /**
     * Diagnoses that are unaffected by the query
     */
    public Set<Diagnosis<F>> dz = new HashSet<>();

    /**
     * Uniquely defined query for a given Q-Partition, used in the search for Q-Partitions.
     */
    private Set<F> canonicalQuery;

    /**
     * Set of queries consisting of only explicit entailments (=formulas in KB) which have exactly this Q-Partition. Does not necessarily include all queries for this Q-Partition.
     */
    private Set<Set<F>> explicitEntailmentsQueries;

    /**
     * Set of queries consisting of not only explicit entailments (=formulas in KB) which have exactly this Q-Partition. Does not necessarily include all queries for this Q-Partition.
     */
    private Set<Set<F>>enrichedQueries;

    /**
     * Set of queries that the user rejected to answer.
     */
    private Set<Set<F>> rejectedQueries;

    /**
     * Traits, used in Algorithm 2 (Computing successor in D+-Partitioning)
     */
    public Map<Diagnosis<F>,Set<F>> diagsTraits = new HashMap<>();

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

    /**
     *
     */
    public ICostsEstimator<F> costEstimator = null;

    /**
     *
     */
    public BigDecimal probDx;

    /**
     *
     */
    public BigDecimal probDnx;

    /**
     *
     */
    public QPartition() {
        this(new HashSet<>(),new HashSet<>(),new HashSet<>(),null);
    }

    /**
     * A QPartition which
     *
     * @param dx Diagnoses that are supported by the query.
     * @param dnx Diagnoses that are not supported by the query.
     * @param dz Diagnoses that are unaffected by the query.
     * @param  costestimator Costestimator.
     */
    public QPartition(Set<Diagnosis<F>> dx, Set<Diagnosis<F>> dnx, Set<Diagnosis<F>> dz, ICostsEstimator<F> costestimator) {
        this.dx = dx;
        this.dnx = dnx;
        this.dz = dz;
        this.costEstimator = costestimator;

        computeProbabilities();
    }

    private void computeProbabilities() {

        // when the measures of diagnosis are correctly set (sum must equal 1) for all diagnoses, we prefer them
        BigDecimal sumDx = BigDecimal.ZERO;
        BigDecimal sumDnx = BigDecimal.ZERO;

        for (Diagnosis d: dx)
            sumDx = sumDx.add(d.getMeasure());

        for (Diagnosis d: dnx)
            sumDnx = sumDnx.add(d.getMeasure());

        //if (sumDx.add(sumDnx).doubleValue() == 1){
        if (sumDx.add(sumDnx).compareTo(BigDecimal.ONE) == 0) {
            probDx = sumDx;
            probDnx = sumDnx;
            return;
        }

        this.probDx = computeProbability(dx);
        this.probDnx = computeProbability(dnx);

        // otherwise we set the probabilities of diagnoses using the formula weights (if possible)
        final BigDecimal s = probDx.add(probDnx);
        if (s.compareTo(BigDecimal.ZERO) != 0) {
            this.probDx = this.probDx.divide(s, MathContext.DECIMAL128);
            this.probDnx = this.probDnx.divide(s, MathContext.DECIMAL128);
        }
    }

    /**
     * Computes the probabilities for diagnoses diags using the costestimator.
     *
     * @param diags
     * @return
     */
    private BigDecimal computeProbability(Set<Diagnosis<F>> diags) {
        BigDecimal sum = BigDecimal.ZERO;
        if (costEstimator!=null)
            for (Diagnosis<F> d : diags)
                sum = sum.add(costEstimator.getFormulasCosts(d.getFormulas()));
        return sum;
    }

    /**
     * Return the result of query computation.
     *
     * @return Set of Formulas
     */
    public Query<F> getQuery() {
        if (!enrichedQueries.isEmpty()) {
            return new Query(enrichedQueries.iterator().next(),this);
        } else if (!explicitEntailmentsQueries.isEmpty()) {
            return new Query(explicitEntailmentsQueries.iterator().next(),this);
        } else {
            return new Query(canonicalQuery,this);
        }
    }

    /**
     * Compute the successors in D+-Partitioning.
     *
     * This method represents the implementation of Algorithm 7 of the original paper.
     * In the method body we refer each statement to the line of the Algorithm 7 in the original paper.
     *
     * @return The set of all canonical QPartitions sucs that result from Pk by a minimal D+-transformation.
     */
    public Collection<QPartition<F>> computeSuccessors() {
        assert dz.isEmpty();

        Collection<QPartition<F>> sucs = new HashSet<>();                                                               // line 2: stores successors of Parition Pk by a minimal
        this.diagsTraits = new HashMap<>();                                                                             // line 3: stores tuples including a diagnosis and the trait of the eq. class w.r.t. it belongs to
        Set<Set<Diagnosis<F>>> eqClasses = new HashSet<>();                                                             // line 4: set of sets of diagnoses, each set is eq. class with set-minimal trait

        if (dx.isEmpty()) {                                                                                             // line 5: initial State, apply S_init
            sucs = generateInitialSuccessors();                                                                         // line 6-7:
        } else {                                                                                                        // line 8: Pk is canonical q-partition, apply Snext
            diagsTraits = computeDiagsTraits();                                                                         // line 9-11: compute trait of eq. class, enables to retrive ti for Di in operations below
            Set<Diagnosis<F>> diags = new HashSet<>(dnx);                                                               // line 12: make a copy of dnx
            Set<Diagnosis<F>> minTraitDiags = new HashSet<>();                                                          // line 13: to store one representative of each eq. class with set-minimial trait
            boolean sucsExist = false;                                                                                  // line 14: will be set to true if Pk is found to have some canonical successor q-partition

            while (!diags.isEmpty()) {                                                                                  // line 15:
                Diagnosis<F> Di = Utils.getFirstElem(diags, true);                                                      // line 16: Di is first (any) element in diags and diags := diags - Di (getFirstElem removes Di from diags)
                Set<Diagnosis<F>> necFollowers = new HashSet<>();                                                       // line 17: to store all necessary followers of Di
                boolean diagOK = true;                                                                                  // line 18: will be set to false if Di is found to have a non-set-minimal trait
                Set<Diagnosis<F>> diagsAndMinTraitDiags = new HashSet<>(diags);                                         // prepare unification of diags with minTraitDiags
                diagsAndMinTraitDiags.addAll(minTraitDiags);
                Set<F> ti = diagsTraits.get(Di);
                for (Diagnosis<F> Dj : diagsAndMinTraitDiags) {                                                         // line 19:
                    Set<F> tj = diagsTraits.get(Dj);
                    if (ti.containsAll(tj))                                                                             // line 20:
                        if (ti.equals(tj))                                                                              // line 21: equal trait, Di and Dj are same eq. class
                            necFollowers.add(Dj);                                                                       // line 22:
                        else                                                                                            // line 23:
                            diagOK = false;                                                                             // line 24: eq. class of Di has a non-set-minimal trait
                }

                Set<Diagnosis<F>> eqCls = new HashSet<>(necFollowers);                                                  // line 25:
                eqCls.add(Di);                                                                                          // line 25:

                if (!sucsExist && eqCls.equals(dnx))                                                                    // line 26: test only run in first iteration of while-loop
                    return new HashSet<>();                                                                             // line 27: Pk has single successor partition which is no q-partition due to dnx = empty set
                sucsExist = true;                                                                                       // line 28: existence of equal or more than 1 canonical successor q-partition in Pk guaranteed

                if (diagOK) {                                                                                           // line 29:
                    eqClasses.add(eqCls);                                                                               // line 30:
                    minTraitDiags.add(Di);                                                                              // line 31: add one representative for eq. class
                }
                diags.removeAll(necFollowers);                                                                          // line 32: delete all representatives for eq. class
            }

            for (Set<Diagnosis<F>> E : eqClasses) {                                                                     // line 33-34: construct all canonical successor q-partitions by means of eq.class
                Set<Diagnosis<F>> newDx = new HashSet<>(dx);
                boolean hasBeenAdded = newDx.addAll(E);
                assert hasBeenAdded;

                Set<Diagnosis<F>> newDnx = new HashSet<>(dnx);
                boolean hasBeenRemoved = newDnx.removeAll(E);
                assert hasBeenRemoved;

                QPartition<F> sucsPartition = new QPartition<>(newDx, newDnx, new HashSet<>(), this.costEstimator);
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
    private Collection<QPartition<F>> generateInitialSuccessors() {
        assert dx.isEmpty();
        assert dz.isEmpty();

        Collection<QPartition<F>> sucs = new HashSet<>();
        for (Diagnosis<F> diagnosis : dnx) {
            Set<Diagnosis<F>> new_dx = new HashSet<>(); // create the new dx (diagnoses that are supported by the query)
            new_dx.add(diagnosis);

            Set<Diagnosis<F>> new_dnx = new HashSet<>(dnx); // make a copy of the original dnx set...
            boolean isRemoved = new_dnx.remove(diagnosis); // and remove the current diagnosis
            assert isRemoved;

            Set<Diagnosis<F>> new_dz = new HashSet<>(dz); // make a copy of the original dz

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
    private Map<Diagnosis<F>,Set<F>> computeDiagsTraits() {
        assert !dx.isEmpty();

        //  compute the union of formulas of diagnoses dx of partitionPk
        Set<F> unitedDxFormulas = new HashSet<>();
        for (Diagnosis<F> diag_dx : dx)
            unitedDxFormulas.addAll(diag_dx.getFormulas());

        // compute trait of using unionDxFormulas
        for (Diagnosis<F> diag_dnx : dnx) {
            Set<F> traits = new HashSet<>(diag_dnx.getFormulas());    // initialize traits with the formulas of diag_dnx ...
            traits.removeAll(unitedDxFormulas);                             // ... and remove all formulas that occurred in dx of partionPk
            diagsTraits.put(diag_dnx, traits);                              // enables to retrieve trait ti for diagnosis di in later operations
        }
        return diagsTraits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QPartition<?> that = (QPartition<?>) o;

        if (dx != null ? !dx.equals(that.dx) : that.dx != null) return false;
        if (dnx != null ? !dnx.equals(that.dnx) : that.dnx != null) return false;
        return dz != null ? dz.equals(that.dz) : that.dz == null;

    }

    @Override
    public int hashCode() {
        int result = dx != null ? dx.hashCode() : 0;
        result = 31 * result + (dnx != null ? dnx.hashCode() : 0);
        result = 31 * result + (dz != null ? dz.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QPartition{" +
                "dx=" + dx +
                ", dnx=" + dnx +
                ", dz=" + dz +
                ", probDx=" + probDx +
                ", probDnx=" + probDnx +
                '}';
    }
}
