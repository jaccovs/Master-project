package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A query of constraints that splits the diagnoses into the 3 parts dx, dnx, and dz.
 *
 * @author Schmitz
 */
public class Query<Formula> {
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

    public Set<Formula> formulas;

    public BigDecimal score = BigDecimal.valueOf(Double.MAX_VALUE);

    public BigDecimal difference = new BigDecimal(Double.MAX_VALUE);

    public boolean isVerified = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query1 = (Query) o;

        if (!dnx.equals(query1.dnx)) return false;
        if (!dx.equals(query1.dx)) return false;
        if (!dz.equals(query1.dz)) return false;
        //if (!query.equals(query1.query)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dx.hashCode();
        result = 31 * result + dnx.hashCode();
        result = 31 * result + dz.hashCode();
        //result = 31 * result + query.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Query: " + formulas + ", dx:" + dx + ", dnx:" + dnx + ", dz:" + dz;
    }
}
