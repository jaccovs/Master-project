package org.exquisite.diagnosis.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.exquisite.tools.Utilities;

import choco.kernel.model.constraints.Constraint;

/**
 * A data structure for the tests.diagnosis elements.
 * Will add weights etc later on
 * @author Dietmar
 *
 */
public class Diagnosis implements Comparable<Diagnosis> {

	DiagnosisModel model;
	
	List<Constraint> elements;
	
	/**
	 * A probability measure for the tests.diagnosis.
	 */
	private BigDecimal measure = BigDecimal.ZERO; // BigDecimal.valueOf(random.nextDouble());
	
	/**
	 * A list of constraints that follows from this tests.diagnosis.
	 */
	private Set<Constraint> entailments;
	
	/**
	 * Temporary variant of entailments, so that the original set can always be restored.
	 */
	private Set<Constraint> tempEntailments;

	public List<Constraint> getElements() {
		return elements;
	}
	
	public BigDecimal getMeasure() {
		return measure;
	}
	
	public void setMeasure(BigDecimal measure) {
		this.measure = measure;
	}
	
	public Set<Constraint> getEntailments() {
		return tempEntailments;
	}
	
	public void setEntailments(Set<Constraint> entailments) {
		this.tempEntailments = entailments;
	}
	
	public void restoreEntailments() {
		this.tempEntailments = this.entailments;
	}
	
	public void changeEntailments(Set<Constraint> entailments) {
		if (this.tempEntailments == this.entailments) {
			this.tempEntailments = entailments;
		}
		this.entailments = entailments;
	}
	
	public Diagnosis() {		
	}

	public Diagnosis(List<Constraint> elements, DiagnosisModel model) {
		this.elements = new ArrayList<Constraint>(elements);
		this.model = model;
	}
	
	public Diagnosis(List<Constraint> elements, DiagnosisModel model, BigDecimal measure) {
		this.elements = new ArrayList<Constraint>(elements);
		this.model = model;
		this.measure = measure;
	}

	public void setElements(List<Constraint> elements) {
		this.elements = elements;
	}
	
	public DiagnosisModel getDiagnosisModel(){
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

        if ( Diagnosis.class.isInstance(o)) {
        	Diagnosis that = (Diagnosis) o;
            return elements != null && elements.equals(that.elements);
        }
        return false;
	}

	@Override
	public int compareTo(Diagnosis o) {
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
