package org.exquisite.core.conflictsearch;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ISolver;
import org.exquisite.core.solver.SimpleConflictSubsetSolver;

import java.util.*;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by kostya on 28-Feb-16.
 */
public abstract class TestConflictSearcher {

    public void testSearcher() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        Set<Integer> testConflict = getSet(2, 3, 4);

        conflicts.add(testConflict);

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));

        SimpleConflictSubsetSolver solver = new SimpleConflictSubsetSolver(model, domain, conflicts);

        IConflictSearcher<Integer> qx = getSearcher(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), getExpectedSizeTestSearcher());
        assertTrue(qxout.contains(getExpectedSetTestSearcher()));
    }

    protected int getExpectedSizeTestSearcher() {
        return 1;
    }

    protected Set<Integer> getExpectedSetTestSearcher() {
        return getSet(2, 3, 4);
    }

    public void testSearcher2() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(2, 3));
        conflicts.add(getSet(5, 6));

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));

        SimpleConflictSubsetSolver solver = new SimpleConflictSubsetSolver(model, domain, conflicts);

        IConflictSearcher<Integer> qx = getSearcher(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), getExpectedSizeTestSearcher2());
        assertTrue(qxout.contains(getExpectedSetTestSearcher2()));
    }

    protected int getExpectedSizeTestSearcher2() {
        return 1;
    }

    protected Set<Integer> getExpectedSetTestSearcher2() {
        return getSet(2,3);
    }


    public void testSearcherBackgroundKnowledge() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        HashSet<Integer> testConflict = getSet(2, 3, 6);
        conflicts.add(testConflict);

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));

        SimpleConflictSubsetSolver solver = new SimpleConflictSubsetSolver(model, domain, conflicts);

        IConflictSearcher<Integer> qx = getSearcher(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), getExpectedSizeTestSearcherBackgroundKnowledge());
        assertTrue(qxout.contains(getExpectedSetTestSearcherBackgroundKnowledge()));
    }

    protected int getExpectedSizeTestSearcherBackgroundKnowledge() {
        return 1;
    }

    protected Set<Integer> getExpectedSetTestSearcherBackgroundKnowledge() {
        return getSet(2, 3);
    }


    public void testSearcherExamples() throws DiagnosisException {

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Arrays.asList(6, 7));
        model.setNotEntailedExamples(Collections.singletonList(1));

        SimpleConflictSubsetSolver solver = new SimpleConflictSubsetSolver(model, domain, Collections.emptyList());

        IConflictSearcher<Integer> qx = getSearcher(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), getExpectedSizeTestSearcherExamples());
        assertTrue(qxout.contains(getExpectedSetTestSearcherExamples()));
    }

    protected int getExpectedSizeTestSearcherExamples() {
        return 1;
    }

    protected Set<Integer> getExpectedSetTestSearcherExamples() {
        return getSet(1);
    }

    protected abstract IConflictSearcher<Integer> getSearcher(ISolver<Integer> solver);
}
