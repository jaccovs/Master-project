package org.exquisite.diagnosis.models;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.tools.Utilities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A data structure for the tests.diagnosis elements.
 * Will add weights etc later on
 *
 * @author Dietmar
 */
public class Diagnosis<T> implements Comparable<Diagnosis<T>> {

    DiagnosisModel<T> model;

    List<T> elements;

    /**
     * A probability measure for the tests.diagnosis.
     */
    private BigDecimal measure = BigDecimal.ZERO; // BigDecimal.valueOf(random.nextDouble());

    /**
     * A list of constraints that follows from this tests.diagnosis.
     */
    private Set<T> entailments;

    /**
     * Temporary variant of entailments, so that the original set can always be restored.
     */
    private Set<T> tempEntailments;

    public Diagnosis() {
    }

    public Diagnosis(List<T> elements, DiagnosisModel<T> model) {
        this.elements = new ArrayList<T>(elements);
        this.model = model;
    }

    public Diagnosis(List<T> elements, DiagnosisModel<T> model, BigDecimal measure) {
        this.elements = new ArrayList<T>(elements);
        this.model = model;
        this.measure = measure;
    }

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }

    public BigDecimal getMeasure() {
        return measure;
    }

    public void setMeasure(BigDecimal measure) {
        this.measure = measure;
    }

    public Set<T> getEntailments() {
        return tempEntailments;
    }

    public void setEntailments(Set<T> entailments) {
        this.tempEntailments = entailments;
    }

    public void restoreEntailments() {
        this.tempEntailments = this.entailments;
    }

    public void changeEntailments(Set<T> entailments) {
        if (this.tempEntailments == this.entailments) {
            this.tempEntailments = entailments;
        }
        this.entailments = entailments;
    }

    public DiagnosisModel<T> getDiagnosisModel() {
        return model;
    }

    @Override
    public String toString() {
        return Utilities.printConstraintListOrderedByName(getElements(), model);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (Diagnosis.class.isInstance(o)) {
            Diagnosis<T> that = (Diagnosis<T>) o;
            return elements != null && elements.equals(that.elements);
        }
        return false;
    }

    @Override
    public int compareTo(Diagnosis<T> o) {
        if (this.equals(o)) {
            return 0;
        }
        int res = getMeasure().compareTo(o.getMeasure());
        if (res == 0) {
            res = Integer.compare(elements.size(), o.elements.size());
            for (int i = 0; i < elements.size(); i++) {
                res = Integer.compare(elements.get(i).hashCode(), o.elements.get(i).hashCode());
                if (res != 0) {
                    break;
                }
            }
        }
        return res;
    }

}
