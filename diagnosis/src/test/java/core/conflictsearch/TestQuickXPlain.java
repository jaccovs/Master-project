package core.conflictsearch;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.solver.ISolver;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Automated tests of the QuickXPlain class
 */
public class TestQuickXPlain extends TestConflictSearcher {

    @Override
    public IConflictSearcher<Integer> getSearcher(ISolver<Integer> solver) {
        return new QuickXPlain<>(solver);
    }

    @Test
    public void testQXP() throws DiagnosisException {
        super.testSearcher();
    }

    @Test
    public void testQXPBackgroundKnowledge() throws DiagnosisException {
        super.testSearcherBackgroundKnowledge();
    }

    @Test
    public void testQXPExamples() throws DiagnosisException {
        testSearcherExamples();
    }

    @Test
    public void testReplace() {
        List<Integer> b = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        List<Integer> c1 = new ArrayList<>(Arrays.asList(3, 4));
        List<Integer> c2 = new ArrayList<>(Arrays.asList(5, 6, 7));

        QuickXPlain<Integer> qx = (QuickXPlain<Integer>) getSearcher(null);
        qx.replace(b, c1, c2);
        assertTrue(b.equals(Arrays.asList(1, 2, 5, 6, 7)));

        qx.replace(b, c2, c1);
        assertTrue(b.equals(Arrays.asList(1, 2, 3, 4)));

        qx.replace(b, c1, Collections.emptyList());
        assertTrue(b.equals(Arrays.asList(1, 2)));
    }

}
