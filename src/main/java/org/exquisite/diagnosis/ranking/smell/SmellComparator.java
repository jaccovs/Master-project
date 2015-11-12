package org.exquisite.diagnosis.ranking.smell;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;

import choco.kernel.model.constraints.Constraint;

public class SmellComparator implements Comparator<Diagnosis> {

	private Map<String, Float> smells;
	private static final int multiplicatorForIntegerComparator = 10000;

	public SmellComparator(Map<String, Float> smells) {
		this.smells = smells;
		for(String key : smells.keySet()){
			// Multiplication is necessary because compare can only work with ints
			this.smells.put(key, smells.get(key)*multiplicatorForIntegerComparator);
		}
	}

	/**
	 * Compares a tests.diagnosis
	 */
	@Override
	public int compare(Diagnosis o2, Diagnosis o1) {
		float sum1 = getSum(o1.getElements(), o1.getDiagnosisModel());
		float sum2 = getSum(o2.getElements(), o2.getDiagnosisModel());

		return (int) (sum1 - sum2);
	}

	private float getSum(List<Constraint> constraints,
			DiagnosisModel diagnosisModel) {

		float value = 0;
		for (Constraint c : constraints) {
			value = smells.get(diagnosisModel.getConstraintName(c));
		}
		return value;
	}
}