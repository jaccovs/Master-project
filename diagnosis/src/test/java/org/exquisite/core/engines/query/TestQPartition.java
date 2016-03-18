package org.exquisite.core.engines.query;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.junit.Test;

import java.util.*;

import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.*;

/**
 * A JUnit Testcase for QPartition.
 * <p>
 * Created by wolfi on 17.03.2016.
 */
public class TestQPartition<Formula> {

    @Test
    public void testComputeSuccessors() {
        Diagnosis<Integer> D1 = getDiagnosis(3, 4);
        Diagnosis<Integer> D2 = getDiagnosis(4, 5);
        Diagnosis<Integer> D3 = getDiagnosis(6, 7);
        Diagnosis<Integer> D4 = getDiagnosis(1, 4);
        Diagnosis<Integer> D5 = getDiagnosis(1, 2);
        Diagnosis<Integer> D6 = getDiagnosis(2, 3);

        // initial case: dx is empty -> we are at root, i.e. this must equal the generateInitialSuccessors
        QPartition<Integer> qPartition = new QPartition<>(getSet(), getSet(D1, D2, D3, D4, D5, D6), getSet(), null);
        Collection<QPartition<Integer>> successors = qPartition.computeSuccessors();
        checkSuccessors(qPartition, successors);

        // this must be the same result as calling initialSuccessors
        Collection<QPartition<Integer>> expectedSuccessors = qPartition.generateInitialSuccessors();
        assertEquals(expectedSuccessors, successors);

        // case 2
        qPartition = new QPartition<>(getSet(D5, D6), getSet(D1, D2, D3, D4), getSet(), null);
        successors = qPartition.computeSuccessors();
        //checkSuccessors(qPartition, successors);

        // create here the set of expected successors
        expectedSuccessors = new HashSet<>();
        expectedSuccessors.add(new QPartition<>(getSet(D1, D4, D5, D6), getSet(D2, D3), getSet(), null));
        expectedSuccessors.add(new QPartition<>(getSet(D3, D5, D6), getSet(D1, D2, D4), getSet(), null));

        assertEquals(expectedSuccessors, successors);
    }

    @Test
    public void testGenerateInitialSuccessors() {

        // check #1
        Diagnosis<Integer> D1 = getDiagnosis(3, 4);
        Diagnosis<Integer> D2 = getDiagnosis(4, 5);
        Diagnosis<Integer> D3 = getDiagnosis(6, 7);
        Diagnosis<Integer> D4 = getDiagnosis(1, 4);
        Diagnosis<Integer> D5 = getDiagnosis(1, 2);
        Diagnosis<Integer> D6 = getDiagnosis(2, 3);

        QPartition<Integer> qPartition = new QPartition<>(getSet(), getSet(D1, D2, D3, D4, D5, D6), getSet(), null);

        // check some preconditions
        assertTrue(qPartition.dx.isEmpty());
        assertFalse(qPartition.dnx.isEmpty());
        assertTrue(qPartition.dz.isEmpty());

        Collection<QPartition<Integer>> initialSuccessors = qPartition.generateInitialSuccessors();

        assertTrue(!initialSuccessors.isEmpty()); // since dnx is not empty, initial successors must also be not empty

        checkSuccessors(qPartition, initialSuccessors);

        // check #2: empty dx, dnx and dz
        qPartition = new QPartition<>(getSet(), getSet(), getSet(), null);

        // check some preconditions
        assertTrue(qPartition.dx.isEmpty());
        assertTrue(qPartition.dnx.isEmpty());
        assertTrue(qPartition.dz.isEmpty());

        initialSuccessors = qPartition.generateInitialSuccessors();
        assertTrue(initialSuccessors.isEmpty());
    }

    @Test
    public void testComputeDiagsTraits() {

        Diagnosis<Integer> D1 = getDiagnosis(3, 4);
        Diagnosis<Integer> D2 = getDiagnosis(4, 5);
        Diagnosis<Integer> D3 = getDiagnosis(6, 7);
        Diagnosis<Integer> D4 = getDiagnosis(1, 4);
        Diagnosis<Integer> D5 = getDiagnosis(1, 2);
        Diagnosis<Integer> D6 = getDiagnosis(2, 3);

        QPartition<Integer> qPartition = new QPartition<>(getSet(D5, D6), getSet(D1, D2, D3, D4), getSet(), null);

        // check some preconditions
        assertFalse(qPartition.dx.isEmpty());
        assertFalse(qPartition.dnx.isEmpty());
        assertTrue(qPartition.dz.isEmpty());

        // check 1:
        Map<Diagnosis<Integer>, Set<Integer>> diagsTraits = qPartition.computeDiagsTraits();

        // result should map diags in dnx to their traits, so the size of the result must match the size of dnx
        assertEquals(qPartition.dnx.size(), diagsTraits.keySet().size());

        Map<Diagnosis<Integer>, Set<Integer>> expectedTraits = new HashMap<>();
        expectedTraits.put(D1, getSet(4));
        expectedTraits.put(D2, getSet(4, 5));
        expectedTraits.put(D3, getSet(6, 7));
        expectedTraits.put(D4, getSet(4));

        assertEquals(expectedTraits, diagsTraits);

        // check 2: empty dnx
        qPartition = new QPartition<>(getSet(D1, D2, D3, D4, D5, D6), getSet(), getSet(), null);

        // check some preconditions
        assertFalse(qPartition.dx.isEmpty());
        assertTrue(qPartition.dnx.isEmpty());
        assertTrue(qPartition.dz.isEmpty());


        diagsTraits = qPartition.computeDiagsTraits();
        assertTrue(diagsTraits.isEmpty());

    }

    /**
     * Some conditions every result to computeSuccessors must have.
     * e.g. each Diagnosis in qPartition.dnx must occur ONCE as a SINGLE dx element in successors.
     *
     * @param qPartition q-Partition
     * @param successors The successors
     */
    private void checkSuccessors(QPartition<Integer> qPartition, Collection<QPartition<Integer>> successors) {
        assertTrue(successors != null);

        assertEquals(qPartition.dnx.size(), successors.size()); // the size of initialSuccessors must be the size of dnx

        // each Diagnosis in dnx must occur ONCE as a SINGLE dx element in successors
        Map<Diagnosis<Integer>, Boolean> wasDiagnosisInDnx = new HashMap<>();
        for (Diagnosis<Integer> d : qPartition.dnx) wasDiagnosisInDnx.put(d, Boolean.FALSE);

        for (QPartition<Integer> partition : successors) {
            assertEquals(1, partition.dx.size());
            assertEquals(qPartition.dnx.size() - 1, partition.dnx.size());
            assertTrue(partition.dz.isEmpty());

            qPartition.dnx.containsAll(partition.dnx);
            qPartition.dnx.containsAll(partition.dx);

            Set<Diagnosis<Integer>> testSet = new HashSet<>();
            testSet.addAll(partition.dnx);
            testSet.addAll(partition.dx);

            assertEquals(qPartition.dnx, testSet);

            Diagnosis<Integer> diagInDx = partition.dx.iterator().next();
            assertFalse(wasDiagnosisInDnx.get(diagInDx));
            wasDiagnosisInDnx.put(diagInDx, Boolean.TRUE);
        }
    }

}
