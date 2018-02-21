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
        // A-1, B-2, C-3, D-4
        // A. -A v B. -B v A. -D v C.
        HashSet<FCClause> clauses = getSet(new FCClause(1), new FCClause(-1, 2), new FCClause(-2, 1),
                new FCClause(-4, 3));
        SimpleFCSat fc = new SimpleFCSat(model);
        assertTrue(fc.isConsistent(clauses));
        assertEquals(fc.calculateEntailments(), getSet(new FCClause(1), new FCClause(2)));
    }

    @Test
    public void testFCUnSAT() {
        DiagnosisModel<FCClause> model = new DiagnosisModel<>();
        HashSet<FCClause> clauses = getSet(new FCClause(1), new FCClause(-1, 2),
                new FCClause(-1, 3), new FCClause(-2, -3));
        SimpleFCSat fc = new SimpleFCSat(model);
        assertFalse(fc.isConsistent(clauses));
    }

    @Test
    public void testFCEnt() {
        DiagnosisModel<FCClause> model = new DiagnosisModel<>();
        HashSet<FCClause> clauses = getSet(new FCClause(1), new FCClause(-1, 2),
                new FCClause(-1, 3));
        SimpleFCSat fc = new SimpleFCSat(model);
        assertTrue(fc.isConsistent(clauses));
        fc.isEntailed(getSet(new FCClause(-3, 2)));
    }
}
