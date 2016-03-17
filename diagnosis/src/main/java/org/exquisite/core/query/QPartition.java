package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public QPartition(Set<Diagnosis<Formula>> dx, Set<Diagnosis<Formula>> dnx, Set<Diagnosis<Formula>> dz) {
        this.dx = dx;
        this.dnx = dnx;
        this.dz = dz;
    }

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
     *
     */
    public BigDecimal score = BigDecimal.valueOf(Double.MAX_VALUE);

    public BigDecimal difference = new BigDecimal(Double.MAX_VALUE);

    public boolean isVerified = false;

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
                ", score=" + score +
                ", difference=" + difference +
                ", isVerified=" + isVerified +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QPartition<?> that = (QPartition<?>) o;

        if (isVerified != that.isVerified) return false;
        if (dx != null ? !dx.equals(that.dx) : that.dx != null) return false;
        if (dnx != null ? !dnx.equals(that.dnx) : that.dnx != null) return false;
        if (dz != null ? !dz.equals(that.dz) : that.dz != null) return false;
        if (canonicalQuery != null ? !canonicalQuery.equals(that.canonicalQuery) : that.canonicalQuery != null)
            return false;
        if (explicitEntailmentsQueries != null ? !explicitEntailmentsQueries.equals(that.explicitEntailmentsQueries) : that.explicitEntailmentsQueries != null)
            return false;
        if (enrichedQueries != null ? !enrichedQueries.equals(that.enrichedQueries) : that.enrichedQueries != null)
            return false;
        if (rejectedQueries != null ? !rejectedQueries.equals(that.rejectedQueries) : that.rejectedQueries != null)
            return false;
        if (score != null ? !score.equals(that.score) : that.score != null) return false;
        return difference != null ? difference.equals(that.difference) : that.difference == null;

    }

    @Override
    public int hashCode() {
        int result = dx != null ? dx.hashCode() : 0;
        result = 31 * result + (dnx != null ? dnx.hashCode() : 0);
        result = 31 * result + (dz != null ? dz.hashCode() : 0);
        result = 31 * result + (canonicalQuery != null ? canonicalQuery.hashCode() : 0);
        result = 31 * result + (explicitEntailmentsQueries != null ? explicitEntailmentsQueries.hashCode() : 0);
        result = 31 * result + (enrichedQueries != null ? enrichedQueries.hashCode() : 0);
        result = 31 * result + (rejectedQueries != null ? rejectedQueries.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (difference != null ? difference.hashCode() : 0);
        result = 31 * result + (isVerified ? 1 : 0);
        return result;
    }
}
