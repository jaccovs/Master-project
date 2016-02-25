package org.exquisite.core.search;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.SimpleNumericSolver;
import org.junit.Test;

import java.util.*;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Automated tests of the QuickXPlain class
 */
public class TestQuickXPlain {

    @Test
    public void testQXP() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        HashSet<Integer> testConflict = getSet(2, 3, 4);
//        HashSet<Integer> testConflict = getSet(1, 2);

        conflicts.add(testConflict);

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyStatements(getSet(1, 2, 3, 4, 5));
        model.setCorrectStatements(Arrays.asList(6, 7));

        SimpleNumericSolver solver = new SimpleNumericSolver(model, domain, conflicts);

        QuickXPlain<Integer> qx = new QuickXPlain<>(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), 1);
        assertTrue(qxout.contains(testConflict));
    }

    @Test
    public void testQXPBackgroundKnowledge() throws DiagnosisException {
        List<Set<Integer>> conflicts = new LinkedList<>();
        HashSet<Integer> testConflict = getSet(2, 3, 6);
        conflicts.add(testConflict);

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyStatements(getSet(1, 2, 3, 4, 5));
        model.setCorrectStatements(Arrays.asList(6, 7));

        SimpleNumericSolver solver = new SimpleNumericSolver(model, domain, conflicts);

        QuickXPlain<Integer> qx = new QuickXPlain<>(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), 1);
        assertTrue(qxout.contains(getSet(2, 3)));
    }

    @Test
    public void testQXPExamples() throws DiagnosisException {

        HashSet<Integer> domain = getSet(1, 2, 3, 4, 5, 6, 7);

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyStatements(getSet(1, 2, 3, 4, 5));
        model.setCorrectStatements(Arrays.asList(6, 7));
        model.setNotEntailedExamples(Collections.singletonList(1));

        SimpleNumericSolver solver = new SimpleNumericSolver(model, domain, Collections.emptyList());

        QuickXPlain<Integer> qx = new QuickXPlain<>(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), 1);
        assertTrue(qxout.contains(getSet(1)));
    }

    @Test
    public void testReplace() {
        List<Integer> b = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        List<Integer> c1 = new ArrayList<>(Arrays.asList(3, 4));
        List<Integer> c2 = new ArrayList<>(Arrays.asList(5, 6, 7));

        QuickXPlain<Integer> qx = new QuickXPlain<>(null);
        qx.replace(b, c1, c2);
        assertTrue(b.equals(Arrays.asList(1, 2, 5, 6, 7)));

        qx.replace(b, c2, c1);
        assertTrue(b.equals(Arrays.asList(1, 2, 3, 4)));

        qx.replace(b, c1, Collections.emptyList());
        assertTrue(b.equals(Arrays.asList(1, 2)));
    }

}
