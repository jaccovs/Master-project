package org.exquisite.core.solver;

import org.exquisite.core.model.DiagnosisModel;
import org.junit.Test;

import java.util.HashSet;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.*;

/**
 * A test case showing different usages of the SimpleFCSat class.
 */
public class TestSimpleFCSat {

    @Test
    public void testFCSAT() {
        DiagnosisModel<FCClause> model = new DiagnosisModel<>();
        HashSet<FCClause> clauses = getSet(new FCClause(1), new FCClause(-1, 2), new FCClause(-2, 1),
                new FCClause(-4, 3));
        SimpleFCSat fc = new SimpleFCSat(model, clauses);
        assertTrue(fc.isConsistent());
        assertEquals(fc.calculateEntailments(), getSet(new FCClause(1), new FCClause(2)));
    }

    @Test
    public void testFCUnSAT() {
        DiagnosisModel<FCClause> model = new DiagnosisModel<>();
        HashSet<FCClause> clauses = getSet(new FCClause(1), new FCClause(-1, 2),
                new FCClause(-1, 3), new FCClause(-2, -3));
        SimpleFCSat fc = new SimpleFCSat(model, clauses);
        assertFalse(fc.isConsistent());
    }

    @Test
    public void testFCEnt() {
        DiagnosisModel<FCClause> model = new DiagnosisModel<>();
        HashSet<FCClause> clauses = getSet(new FCClause(1), new FCClause(-1, 2),
                new FCClause(-1, 3));
        SimpleFCSat fc = new SimpleFCSat(model, clauses);
        assertTrue(fc.isConsistent());
        fc.isEntailed(getSet(new FCClause(-3, 2)));
    }
}
