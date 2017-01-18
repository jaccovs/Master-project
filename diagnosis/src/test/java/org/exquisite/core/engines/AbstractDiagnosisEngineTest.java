package org.exquisite.core.engines;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.FCClause;
import org.exquisite.core.solver.ISolver;
import org.exquisite.core.solver.SimpleConflictSubsetSolver;
import org.exquisite.core.solver.SimpleFCSat;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        Set<Diagnosis<FCClause>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();
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

    @Test
    public void testEngineExamplePatrickWithSimpleFCSat() throws DiagnosisException {
        DiagnosisModel<FCClause> model = new DiagnosisModel<>();

        // Example elaborated by Patrick Rodler for his paper, however no diagnoses can be computed with used ISolver.
        // therefore we added a clause 1 into the KB.
        //
        // Domain: A=1, B=2, C=3, E=4, F=5, G=6, H=7, K=9, L=10
        //
        // -: not
        // KB (possibly faulty):                    H->-G. E->F. A->-F. K->E. C->B. H->C.
        // Background (correct formulas):           H->A. B->K.
        // EntailedExamples (user answered yes ):   H,C->L.
        // NotEntailedExamples (user answered no):  F->L. E->-G. H->F.
        Set<FCClause> clauses = getSet(
                new FCClause(-7,-6),    // H ->-G <=> -H v -G == -7 v -6
                new FCClause(-5, 7),    // F -> H <=> -F v  H == -5 v  7
                new FCClause(-4, 5),    // E -> F <=> -E v  F == -4 v  5
                new FCClause(-1, -5),   // A ->-F <=> -A v -F == -1 v -5
                new FCClause(-9, 4),    // K -> E <=> -K v  E == -9 v  4
                new FCClause(-3, 2),    // C -> B <=> -C v  B == -3 v  2
                new FCClause(-7, 3)     // H -> C <=> -H v  C == -7 v  3
        );
        ISolver<FCClause> solver = new SimpleFCSat(model);

        assertTrue(solver.isConsistent(clauses));

        // Knowledge Base
        model.setPossiblyFaultyFormulas(clauses);
        // Background
        model.setCorrectFormulas(getSet(new FCClause(-7,1), new FCClause(-2, 9))); // H->A, B->K
        // Positive
        model.setEntailedExamples(Collections.singletonList(new FCClause(-7, -3, 10))); // H,C->L
        // Negative
        model.setNotEntailedExamples(getSet(new FCClause(-5, 10), new FCClause(-4, -6), new FCClause(-7, 5))); // F->L, E->-G, H->F

        final Set<Diagnosis<FCClause>> diagnoses = getDiagnosisEngine(solver).calculateDiagnoses();

        assertEquals(getSet(), diagnoses); // no diagnoses since not atomic clause is given


        model.getPossiblyFaultyFormulas().add(new FCClause(1)); // add an atomic clause

        final Set<Diagnosis<FCClause>> diagnoses2 = getDiagnosisEngine(solver).calculateDiagnoses();
        assertEquals(getSet(getDiagnosis(new FCClause(1)),getDiagnosis(new FCClause(-1,-5))), diagnoses2);

    }
}
