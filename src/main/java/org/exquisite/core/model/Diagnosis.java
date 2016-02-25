package org.exquisite.core.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A data structure for the tests.diagnosis formulas.
 * Will add weights etc later on
 *
 * @author Dietmar
 */
public class Diagnosis<T> implements Comparable<Diagnosis<T>> {

    private final Set<T> formulas;

    /**
     * A probability measure for the tests.diagnosis.
     */
    private BigDecimal measure = BigDecimal.ZERO; // BigDecimal.valueOf(random.nextDouble());

    /**
     * A list of constraints that follows from this tests.diagnosis.
     */
    private Set<T> entailments = null;


    public Diagnosis(Collection<T> formulas) {
        this(formulas, BigDecimal.ZERO);
    }

    public Diagnosis(Collection<T> formulas, BigDecimal measure) {
        this.formulas = new HashSet<>(formulas);
        this.measure = measure;
    }

    public Set<T> getFormulas() {
        return formulas;
    }

    public BigDecimal getMeasure() {
        return measure;
    }

    public void setMeasure(BigDecimal measure) {
        this.measure = measure;
    }

    public Set<T> getEntailments() {
        return this.entailments;
    }

    public void setEntailments(Set<T> entailments) {
        this.entailments = new HashSet<>(entailments);
    }

    @Override
    public String toString() {
        return formulas.toString();
    }

    @Override
    public int hashCode() {
        return formulas.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof Diagnosis) {
            Diagnosis<T> that = (Diagnosis<T>) o;
            return formulas.equals(that.formulas);
        }
        return false;
    }

    /**
     * Compares two diagnoses. The method first compares the measures of two diagnoses, then the cardinality and
     * then lexicographically.
     *
     * @param o a diagnosis to compare with
     * @return <code>-1</code> if the measure of current diagnosis is smaller or the cardinality is smaller or it
     * contains lexicographically smaller element in a lower order of iteration, <code>0</code> if the two diagnoses
     * are indistinguishable w.r.t. the three criteria and <code>1</code> otherwise.
     */
    @Override
    public int compareTo(Diagnosis<T> o) {
        if (this.equals(o)) {
            return 0;
        }
        int res = getMeasure().compareTo(o.getMeasure());
        if (res != 0) return res;
        res = Integer.compare(formulas.size(), o.formulas.size());
        if (res != 0) return res;
        Iterator<T> it1 = formulas.iterator();
        Iterator<T> it2 = o.formulas.iterator();
        for (; it1.hasNext(); ) {
            res = Integer.compare(it1.next().hashCode(), it2.next().hashCode());
            if (res != 0) return res;
        }
        return res;
    }

}
