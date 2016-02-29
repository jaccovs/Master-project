package org.exquisite.core.conflictsearch;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ISolver;
import org.exquisite.core.solver.SimpleNumericSolver;
import org.junit.Test;

import java.util.*;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Automated tests of the MergeXPlain class
 */
public class TestMergeXPlain extends TestConflictSearcher {

    @Test
    public void testMXP() throws DiagnosisException {
        super.testSearcher();
    }

    @Override
    public IConflictSearcher<Integer> getSearcher(ISolver<Integer> solver) {
        return new MergeXPlain<>(solver);
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

        DiagnosisModel<Integer> model = new DiagnosisModel<>();
        model.setPossiblyFaultyStatements(getSet(1, 2, 3, 4, 5));
        model.setCorrectStatements(Collections.singletonList(0));

        SimpleNumericSolver solver = new SimpleNumericSolver(model, domain, conflicts);

        IConflictSearcher<Integer> qx = getSearcher(solver);
        Set<Set<Integer>> qxout = qx.findConflicts(domain);
        assertEquals(qxout.size(), 3);
        assertTrue(qxout.containsAll(Arrays.asList(getSet(1, 3), getSet(5), getSet(2, 4))));
    }
}
