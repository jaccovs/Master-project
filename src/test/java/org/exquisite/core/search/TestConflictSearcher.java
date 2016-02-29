package org.exquisite.core.search;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.ISolver;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.SimpleNumericSolver;

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
        HashSet<Integer> testConflict = getSet(2, 3, 4);
//        HashSet<Integer> testConflict = getSet(1, 2);

        conflicts.add(testConflict);

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyStatements(getSet(1, 2, 3, 4, 5));
        model.setCorrectStatements(Arrays.asList(6, 7));

        SimpleNumericSolver solver = new SimpleNumericSolver(model, domain, conflicts);

        IConflictSearcher<Integer> qx = getSearcher(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), 1);
        assertTrue(qxout.contains(testConflict));
    }

    public void testSearcherBackgroundKnowledge() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        HashSet<Integer> testConflict = getSet(2, 3, 6);
        conflicts.add(testConflict);

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyStatements(getSet(1, 2, 3, 4, 5));
        model.setCorrectStatements(Arrays.asList(6, 7));

        SimpleNumericSolver solver = new SimpleNumericSolver(model, domain, conflicts);

        IConflictSearcher<Integer> qx = getSearcher(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), 1);
        assertTrue(qxout.contains(getSet(2, 3)));
    }

    public void testSearcherExamples() throws DiagnosisException {

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyStatements(getSet(1, 2, 3, 4, 5));
        model.setCorrectStatements(Arrays.asList(6, 7));
        model.setNotEntailedExamples(Collections.singletonList(1));

        SimpleNumericSolver solver = new SimpleNumericSolver(model, domain, Collections.emptyList());

        IConflictSearcher<Integer> qx = getSearcher(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), 1);
        assertTrue(qxout.contains(getSet(1)));
    }

    protected abstract IConflictSearcher<Integer> getSearcher(ISolver<Integer> solver);
}
