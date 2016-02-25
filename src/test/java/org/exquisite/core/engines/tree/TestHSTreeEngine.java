package org.exquisite.core.engines.tree;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.SimpleNumericSolver;
import org.junit.Test;

import java.util.*;

import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;

/**
 * Automated tests of the HSTreeEngine class
 */
public class TestHSTreeEngine {

    @Test
    public void testEngine() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        HashSet<Integer> testConflict = getSet(2, 3, 6);
        conflicts.add(testConflict);

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyStatements(getSet(1, 2, 3, 4, 5));
        model.setCorrectStatements(Arrays.asList(6, 7));

        SimpleNumericSolver solver = new SimpleNumericSolver(model, domain, conflicts);
        HSTreeEngine<Integer> hs = new HSTreeEngine<>(solver);
        Set<Diagnosis<Integer>> diagnoses = hs.calculateDiagnoses();
        System.out.println(diagnoses);
        assertEquals(diagnoses.toString(), "[[2], [3]]");
    }

    @Test
    public void testEngine2() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3, 6));
        conflicts.add(getSet(3, 4));

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyStatements(getSet(1, 2, 3, 4, 5));
        model.setCorrectStatements(Arrays.asList(6, 7));

        SimpleNumericSolver solver = new SimpleNumericSolver(model, domain, conflicts);
        HSTreeEngine<Integer> hs = new HSTreeEngine<>(solver);
        Set<Diagnosis<Integer>> diagnoses = hs.calculateDiagnoses();
        assertEquals(diagnoses, getSet(getDiagnosis(3), getDiagnosis(2, 4)));
    }

    @Test
    public void testEngine3() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3));
        conflicts.add(getSet(3, 4));

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyStatements(getSet(1, 2, 3, 4, 5));
        model.setCorrectStatements(Arrays.asList(6, 7));
        // "5" must be in every diagnosis, as it must not be entailed
        model.setNotEntailedExamples(Arrays.asList(5));
        // hitting set "3" is not a diagnosis, since its KB violates the next example
        model.setEntailedExamples(Arrays.asList(3));

        SimpleNumericSolver solver = new SimpleNumericSolver(model, domain, conflicts);
        HSTreeEngine<Integer> hs = new HSTreeEngine<>(solver);
        Set<Diagnosis<Integer>> diagnoses = hs.calculateDiagnoses();
        // the only diagnosis is "2,4,5"
        assertEquals(diagnoses, getSet(getDiagnosis(2, 4, 5)));
    }
}
