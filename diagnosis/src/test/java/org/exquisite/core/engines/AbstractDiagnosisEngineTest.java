package org.exquisite.core.engines;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.FCClause;
import org.exquisite.core.solver.ISolver;
import org.exquisite.core.solver.SimpleConflictSubsetSolver;
import org.exquisite.core.solver.SimpleFCSat;
import org.junit.Test;

import java.util.*;

import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;

/**
 * @author wolfi
 */
public abstract class AbstractDiagnosisEngineTest {

    protected abstract IDiagnosisEngine getDiagnosisEngine(ISolver solver);

    @Test
    public void testEngine() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3, 4));

        Set<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));

        ISolver<Integer> solver = new SimpleConflictSubsetSolver(model, domain, conflicts);

        Set<Diagnosis<Integer>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();
        assertEquals(getSet(getDiagnosis(2), getDiagnosis(3), getDiagnosis(4)), diagnoses);
    }

    @Test
    public void testEngine0() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3, 6));

        Set<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));

        ISolver<Integer> solver = new SimpleConflictSubsetSolver(model, domain, conflicts);

        Set<Diagnosis<Integer>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();
        assertEquals(getSet(getDiagnosis(2), getDiagnosis(3)), diagnoses);
    }

    @Test
    public void testEngine1() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3, 6));
        conflicts.add(getSet(1, 3, 5));
        conflicts.add(getSet(2, 4));

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));

        ISolver<Integer> solver = new SimpleConflictSubsetSolver(model, domain, conflicts);
        Set<Diagnosis<Integer>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();
        assertEquals(getSet(getDiagnosis(1,2), getDiagnosis(2,3), getDiagnosis(3, 4), getDiagnosis(2, 5)), diagnoses);
    }

    @Test
    public void testEngine2() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3, 6));
        conflicts.add(getSet(3, 4));

        Set<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));

        ISolver<Integer> solver = new SimpleConflictSubsetSolver(model, domain, conflicts);
        Set<Diagnosis<Integer>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();
        assertEquals(getSet(getDiagnosis(3), getDiagnosis(2, 4)), diagnoses);
    }

    @Test
    public void testEngine3() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3));
        conflicts.add(getSet(3, 4));

        Set<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));
        // "5" must be in every diagnosis, as it must not be entailed
        model.setNotEntailedExamples(Arrays.asList(5));
        // hitting set "3" is not a diagnosis, since its KB violates the next example
        model.setEntailedExamples(Arrays.asList(3));

        ISolver<Integer> solver = new SimpleConflictSubsetSolver(model, domain, conflicts);
        Set<Diagnosis<Integer>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();
        // the only diagnosis is "2,4,5"
        assertEquals(getSet(getDiagnosis(2, 4, 5)), diagnoses);
    }

    @Test
    public void testEngine4() throws DiagnosisException {
        DiagnosisModel<FCClause> model = new DiagnosisModel<>();
        // A-1, B-2, C-3, D-4
        // A->B. B->C. C->D.
        Set<FCClause> clauses = getSet(new FCClause(-1,2), new FCClause(-2, 3), new FCClause(-3, 4));
        ISolver<FCClause> solver = new SimpleFCSat(model);

        model.setPossiblyFaultyFormulas(clauses);
        model.setCorrectFormulas(Collections.singletonList(new FCClause(1)));
        // "4" must not be entailed
        model.setNotEntailedExamples(Collections.singletonList(new FCClause(4)));
        // component 2 is correct
        model.setEntailedExamples(Collections.singletonList(new FCClause(2)));

        //HSTreeEngine<FCClause> hs = new HSTreeEngine<>(solver);
        Set<Diagnosis<FCClause>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();
        // the only diagnosis is "2,4,5"
        assertEquals(getSet(getDiagnosis(new FCClause(-2,3)), getDiagnosis(new FCClause(-3,4))), diagnoses);
    }

    @Test
    public void testEngine5() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3));
        conflicts.add(getSet(5, 6));

        Set<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5, 6));
        model.setCorrectFormulas(Arrays.asList(7));

        ISolver<Integer> solver = new SimpleConflictSubsetSolver(model, domain, conflicts);
        Set<Diagnosis<Integer>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();
        assertEquals(getSet(getDiagnosis(2, 5), getDiagnosis(2, 6), getDiagnosis(3, 5), getDiagnosis(3, 6)), diagnoses);
    }

    @Test
    public void testEngine6() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3));
        conflicts.add(getSet(5, 6));

        Set<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));

        ISolver<Integer> solver = new SimpleConflictSubsetSolver(model, domain, conflicts);
        Set<Diagnosis<Integer>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();
        assertEquals(getSet(getDiagnosis(2, 5), getDiagnosis(3, 5)), diagnoses);
    }

    @Test
    public void testEngine7() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3));
        conflicts.add(getSet(2, 5));

        Set<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));

        ISolver<Integer> solver = new SimpleConflictSubsetSolver(model, domain, conflicts);
        Set<Diagnosis<Integer>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();
        assertEquals(getSet(getDiagnosis(2), getDiagnosis(3, 5)), diagnoses);
    }
}
