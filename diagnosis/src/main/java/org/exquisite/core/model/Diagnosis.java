package org.exquisite.core.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A data structure representing a diagnoses.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author Dietmar
 */
public class Diagnosis<F> implements Comparable<Diagnosis<F>> {

    private final Set<F> formulas;

    /**
     * A probability measure for the tests.diagnosis.
     */
    private BigDecimal measure = BigDecimal.ZERO; // BigDecimal.valueOf(random.nextDouble());

    /**
     * A list of constraints that follows from this tests.diagnosis.
     */
    private Set<F> entailments = null;


    public Diagnosis(Collection<F> formulas) {
        this(formulas, BigDecimal.ZERO);
    }

    public Diagnosis(Collection<F> formulas, BigDecimal measure) {
        this.formulas = new HashSet<>(formulas);
        this.measure = measure;
    }

    public Set<F> getFormulas() {
        return formulas;
    }

    public BigDecimal getMeasure() {
        return measure;
    }

    public void setMeasure(BigDecimal measure) {
        this.measure = measure;
    }

    public Set<F> getEntailments() {
        return this.entailments;
    }

    public void setEntailments(Set<F> entailments) {
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
            Diagnosis<F> that = (Diagnosis<F>) o;
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
    public int compareTo(Diagnosis<F> o) {
        if (this.equals(o)) {
            return 0;
        }
        int res = getMeasure().compareTo(o.getMeasure());
        if (res != 0) return res;
        res = Integer.compare(formulas.size(), o.formulas.size());
        if (res != 0) return res;
        Iterator<F> it1 = formulas.iterator();
        Iterator<F> it2 = o.formulas.iterator();
        for (; it1.hasNext(); ) {
            res = Integer.compare(it1.next().hashCode(), it2.next().hashCode());
            if (res != 0) return res;
        }
        return res;
    }

}
