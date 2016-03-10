package org.exquisite.diagnosis.ranking.smell;

import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SmellComparator<T> implements Comparator<Diagnosis<T>> {

    private static final int multiplicatorForIntegerComparator = 10000;
    private Map<String, Float> smells;

    public SmellComparator(Map<String, Float> smells) {
        this.smells = smells;
        for (String key : smells.keySet()) {
            // Multiplication is necessary because compare can only work with ints
            this.smells.put(key, smells.get(key) * multiplicatorForIntegerComparator);
        }
    }

    /**
     * Compares a tests.diagnosis
     */
    @Override
    public int compare(Diagnosis<T> o2, Diagnosis<T> o1) {
        float sum1 = getSum(o1.getElements(), o1.getDiagnosisModel());
        float sum2 = getSum(o2.getElements(), o2.getDiagnosisModel());

        return (int) (sum1 - sum2);
    }

    private float getSum(List<T> constraints,
                         DiagnosisModel<T> diagnosisModel) {

        float value = 0;
        for (T c : constraints) {
            value = smells.get(diagnosisModel.getConstraintName(c));
        }
        return value;
    }
}