package org.exquisite.core.conflictsearch;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ISolver;
import org.exquisite.core.solver.SimpleConflictSubsetSolver;
import org.junit.Test;

import java.util.*;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Automated tests of the MergeXPlain class
 */
public class TestMergeXPlain extends TestConflictSearcher {

    @Override
    public IConflictSearcher<Integer> getSearcher(ISolver<Integer> solver) {
        return new MergeXPlain<>(solver);
    }

    @Test
    public void testMXP() throws DiagnosisException {
        super.testSearcher();
    }

    @Test
    public void testMXP2() throws DiagnosisException {
        super.testSearcher2();
    }

    @Override
    protected int getExpectedSizeTestSearcher2() {
        return 2;
    }

    @Override
    protected Set<Integer> getExpectedSetTestSearcher2() {
        return getSet(5); // also getSet(2,3) is ok
    }

    @Test
    public void testMXPBackgroundKnowledge() throws DiagnosisException {
        super.testSearcherBackgroundKnowledge();
    }

    @Test
    public void testMXPExamples() throws DiagnosisException {
        testSearcherExamples();
    }

    @Test
    public void testMXPPaper() throws DiagnosisException {

        HashSet<Integer> domain = getSet(0, 1, 2, 3, 4, 5);

        List<Set<Integer>> conflicts = new LinkedList<>();
        conflicts.add(getSet(0, 1, 3));
        conflicts.add(getSet(0, 5));
        conflicts.add(getSet(2, 4));

        // invocation of the framework

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyFormulas(getSet(1, 2, 3, 4, 5));
        model.setCorrectFormulas(Collections.singletonList(0));

        SimpleConflictSubsetSolver solver = new SimpleConflictSubsetSolver(model, domain, conflicts);

        IConflictSearcher<Integer> qx = getSearcher(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), 3);
        assertTrue(qxout.containsAll(Arrays.asList(getSet(1, 3), getSet(5), getSet(2, 4))));
    }
}
