package org.exquisite.core.conflictsearch;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.InverseQuickXPlain;
import org.exquisite.core.solver.ISolver;
import org.junit.Test;

import java.util.Set;

import static org.exquisite.core.TestUtils.getSet;

/**
 * @author wolfi
 */
public class TestInverseQuickXPlain extends TestConflictSearcher {

    @Override
    protected IConflictSearcher<Integer> getSearcher(ISolver<Integer> solver) {
        return new InverseQuickXPlain<>(solver);
    }

    @Test
    public void testIQXP() throws DiagnosisException {
        super.testSearcher();
    }

    @Override
    protected Set<Integer> getExpectedSetTestSearcher() {
        return getSet(2);
    }

    @Test
    public void testIQXPBackgroundKnowledge() throws DiagnosisException {
        super.testSearcherBackgroundKnowledge();
    }

    @Override
    protected Set<Integer> getExpectedSetTestSearcherBackgroundKnowledge() {
        return getSet(2);
    }

    @Test
    public void testIQXPExamples() throws DiagnosisException {
        testSearcherExamples();
    }

}
