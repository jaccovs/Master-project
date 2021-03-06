package org.exquisite.core.query;

import org.exquisite.core.costestimators.ICostsEstimator;
import org.exquisite.core.model.Diagnosis;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A qPartition is a partition of the diagnoses set D induced by a query w.r.t. D into the 3 parts dx, dnx, and dz.
 *
 * A q-partition is a helpful instrument in deciding whether a set of logical formulas is a query or not. It will
 * facilitate an estimation of the impact a query answer has in terms of invalidation of minimal diagnoses.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author wolfi
 * @author patrick
 */
public class QPartition<F> {

    /**
     * Diagnoses that are supported by the query
     */
    public Set<Diagnosis<F>> dx;

    /**
     * Diagnoses that are not supported by the query
     */
    public Set<Diagnosis<F>> dnx;

    /**
     * Diagnoses that are unaffected by the query
     */
    public Set<Diagnosis<F>> dz;

    /**
     * Traits, used in Algorithm 2 (Computing successor in D+-Partitioning)
     */
    public Map<Diagnosis<F>,Set<F>> diagsTraits;

    /**
     * A cost estimator for computation of probabilities.
     */
    public ICostsEstimator<F> costEstimator = null;

    /**
     * Sum of probabilities of all diagnoses (e.g. their measure or their formula weights) in dx.
     */
    public BigDecimal probDx;

    /**
     * Sum of probabilities of all diagnoses (e.g. their measure or their formula weights) in dnx.
     */
    public BigDecimal probDnx;

    /**
     * Empty constructor with empty dx, dnx, dz and no cost estimator.
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
     * @param costestimator A cost estimator used for computation of probabilities probDx and probDnx.
     */
    public QPartition(Set<Diagnosis<F>> dx, Set<Diagnosis<F>> dnx, Set<Diagnosis<F>> dz, ICostsEstimator<F> costestimator) {
        this.dx = dx;
        this.dnx = dnx;
        this.dz = dz;
        this.costEstimator = costestimator;
        this.diagsTraits = new HashMap<>();

        computeProbabilities();
    }

    /**
     * Computes the probabilities of dx and dnx.
     * Prefers the measure values of the diagnoses in dx and dnx. If these are not set or do not equal 1 and a
     * cost estimator has been set, we compute the probabilities by using the cost estimator.
     *
     * IMPORTANT: A modifier of dx, dnx and dz has to call this method to calculate new probabilities.
     */
    public void computeProbabilities() {
        // when measures of diagnosis are given, we prefer them
        BigDecimal sumDx = BigDecimal.ZERO;
        BigDecimal sumDnx = BigDecimal.ZERO;

        for (Diagnosis d: dx)
            sumDx = sumDx.add(d.getMeasure());

        for (Diagnosis d: dnx)
            sumDnx = sumDnx.add(d.getMeasure());

        BigDecimal sumDxAndDnx = sumDx.add(sumDnx);
        if (sumDxAndDnx.compareTo(BigDecimal.ZERO) == 0) { // prefer diagnoses measures, if defined, but
            sumDx = computeProbability(dx);                // when not defined, use formula weights, if possible
            sumDnx = computeProbability(dnx);
            sumDxAndDnx = sumDx.add(sumDnx);
        }

        if (sumDxAndDnx.compareTo(BigDecimal.ONE) == 0) {   // sum already equals ONE
            probDx = sumDx;
            probDnx = sumDnx;
        } else if (sumDxAndDnx.compareTo(BigDecimal.ZERO) > 0) { // normalize to the sum of ONE
            // check if one of the two sums equals ZERO, to prevent divide operation (possible loss of precision)
            if (sumDx.compareTo(BigDecimal.ZERO) == 0) {
                probDx = BigDecimal.ZERO;
                probDnx = BigDecimal.ONE;
            } else if (sumDnx.compareTo(BigDecimal.ZERO) == 0) {
                probDx = BigDecimal.ONE;
                probDnx = BigDecimal.ZERO;
            } else {
                probDx = sumDx.divide(sumDxAndDnx, MathContext.DECIMAL128);
                probDnx = BigDecimal.ONE.subtract(probDx);
            }
        } else {
            probDx = BigDecimal.ZERO;
            probDnx = BigDecimal.ZERO;
        }

        // assert that the sum probDx + probDnx is either ONE or ZERO (if neither measures of diags or formula weights are given)
        final boolean equalsOne = this.probDx.add(this.probDnx).compareTo(BigDecimal.ONE) == 0;
        final boolean equalsZero = this.probDx.add(this.probDnx).compareTo(BigDecimal.ZERO) == 0;
        assert equalsOne || equalsZero;
    }

    /**
     * Computes the probabilities for diagnoses diags using the costestimator.
     *
     * @param diags Set of diagnoses.
     * @return Probability value.
     */
    private BigDecimal computeProbability(Set<Diagnosis<F>> diags) {
        BigDecimal sum = BigDecimal.ZERO;
        if (costEstimator!=null)
            for (Diagnosis<F> d : diags)
                sum = sum.add(costEstimator.getFormulasCosts(d.getFormulas()));
        return sum;
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
