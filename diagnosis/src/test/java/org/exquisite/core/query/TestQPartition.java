package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;
import org.junit.Test;

import java.util.*;

import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.*;

/**
 * A JUnit Testcase for QPartition.
 *
 * @author wolfi
 */
public class TestQPartition {

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
        Collection<QPartition<Integer>> successors = QPartitionOperations.computeSuccessors(qPartition, new HashSet<>());
        checkRootSuccessors(qPartition, successors);

        // this must be the same result as calling initialSuccessors
        Collection<QPartition<Integer>> expectedSuccessors = getSet(
                new QPartition<>(getSet(D1), getSet(D2, D3, D4, D5, D6), getSet(), null),
                new QPartition<>(getSet(D2), getSet(D1, D3, D4, D5, D6), getSet(), null),
                new QPartition<>(getSet(D3), getSet(D1, D2, D4, D5, D6), getSet(), null),
                new QPartition<>(getSet(D4), getSet(D1, D2, D3, D5, D6), getSet(), null),
                new QPartition<>(getSet(D5), getSet(D1, D2, D3, D4, D6), getSet(), null),
                new QPartition<>(getSet(D6), getSet(D1, D2, D3, D4, D5), getSet(), null)
        );
        assertEquals(expectedSuccessors, successors);

        // case 2
        qPartition = new QPartition<>(getSet(D5, D6), getSet(D1, D2, D3, D4), getSet(), null);
        successors = QPartitionOperations.computeSuccessors(qPartition, new HashSet<>());

        // create here the set of expected successors
        expectedSuccessors = new HashSet<>();
        expectedSuccessors.add(new QPartition<>(getSet(D1, D4, D5, D6), getSet(D2, D3), getSet(), null));
        expectedSuccessors.add(new QPartition<>(getSet(D3, D5, D6), getSet(D1, D2, D4), getSet(), null));

        assertEquals(expectedSuccessors, successors);

        // case 3 : test example 1 from paper from root to the leafnodes
        D1 = getDiagnosis(1, 2, 5);
        D2 = getDiagnosis(1, 3, 5);
        D3 = getDiagnosis(3, 4 ,5);

        QPartition<Integer> rootPartition = new QPartition<>(getSet(), getSet(D1, D2, D3), getSet(), null);
        Collection<QPartition<Integer>> L1_successors = QPartitionOperations.computeSuccessors(rootPartition, new HashSet<>());

        Collection<QPartition<Integer>> expected_L1_Successors = getSet(
                new QPartition<>(getSet(D1), getSet(D2, D3), getSet(), null),
                new QPartition<>(getSet(D2), getSet(D1, D3), getSet(), null),
                new QPartition<>(getSet(D3), getSet(D1, D2), getSet(), null)
        );
        assertEquals(expected_L1_Successors, L1_successors); // check succesors of rootPartition

        Collection<QPartition<Integer>>[] expected_L2_Successors = new Collection[] {
                getSet(new QPartition<>(getSet(D1,D2), getSet(D3), getSet(), null)), // successors for q-partition {{D1},{D2,D3},{}}
                getSet(new QPartition<>(getSet(D1,D2), getSet(D3), getSet(), null), new QPartition<>(getSet(D2, D3), getSet(D1), getSet(), null)), // successors for q-partition {{D2},{D1,D3},{}}
                getSet(new QPartition<>(getSet(D2,D3), getSet(D1), getSet(), null)) // successors for q-partition {{D3},{D1,D2},{}}
        } ;

        int i = 0;
        for (QPartition<Integer> L1_successor : L1_successors) {
            Collection<QPartition<Integer>> L2_Successors = QPartitionOperations.computeSuccessors(L1_successor, new HashSet<>());
            assertEquals(expected_L2_Successors[i++], L2_Successors); // check successors of each L1 partition
        }

        // finally check each qPartition in nextExpectedSuccessors which are just these two partitions
        qPartition = new QPartition<>(getSet(D1,D2), getSet(D3), getSet(), null);
        successors = QPartitionOperations.computeSuccessors(qPartition, new HashSet<>());
        assertEquals(getSet(), successors); // no successor

        qPartition = new QPartition<>(getSet(D2,D3), getSet(D1), getSet(), null);
        successors = QPartitionOperations.computeSuccessors(qPartition, new HashSet<>());
        assertEquals(getSet(), successors); // no successor


        // check that calling computeSuccesors with a q-partition with non-empty dz fails
        try {
            qPartition = new QPartition<>(getSet(D2, D3), getSet(D1), getSet(D4), null);
            successors = QPartitionOperations.computeSuccessors(qPartition, new HashSet<>());
            fail();
        } catch (AssertionError e) {
            assertTrue(true);
        }
    }

    /**
     * Some conditions every result to computeSuccessors must have.
     * e.g. each Diagnosis in qPartition.dnx must occur ONCE as a SINGLE dx element in successors.
     *
     * @param qPartition q-Partition
     * @param successors The successors
     */
    private void checkRootSuccessors(QPartition<Integer> qPartition, Collection<QPartition<Integer>> successors) {
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
        Map<Diagnosis<Integer>, Set<Integer>> diagsTraits = QPartitionOperations.computeDiagsTraits(qPartition);

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


        diagsTraits = QPartitionOperations.computeDiagsTraits(qPartition);
        assertTrue(diagsTraits.isEmpty());

    }

}
