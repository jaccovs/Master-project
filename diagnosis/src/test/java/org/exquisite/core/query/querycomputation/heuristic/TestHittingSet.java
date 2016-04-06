package org.exquisite.core.query.querycomputation.heuristic;

import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinMaxFormulaWeights;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinQueryCardinality;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinSumFormulaWeights;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.exquisite.core.TestUtils.getSet;
import static org.junit.Assert.*;

/**
 * A test class for HittingSet.
 *
 * @author wolfi
 */
public class TestHittingSet {

    @Test
    public void testHittingSetWithZeroTimeout() {
        Map<Integer, Float> formulaWeights = new HashMap<>();
        formulaWeights.put(1,0.2f);
        formulaWeights.put(2,0.3f);
        formulaWeights.put(3,0.1f);
        formulaWeights.put(4,0.25f);
        formulaWeights.put(5,0.6f);
        formulaWeights.put(7,0.7f);


        final Set<Set<Integer>> setOfMinTraits = getSet(getSet(1,2,3), getSet(3,5), getSet(1,4), getSet(4,7));

        Set<Set<Integer>> hittingSetMinCardinality = HittingSet.hittingSet(setOfMinTraits, 0, 4, 5, new MinQueryCardinality<>());
        assertTrue(4 <= hittingSetMinCardinality.size());

        hittingSetMinCardinality = HittingSet.hittingSet(setOfMinTraits, 0, 4, 4, new MinQueryCardinality<>());
        assertTrue(4 == hittingSetMinCardinality.size());


        Set<Set<Integer>> hittingSetMinSumFormulaWeights = HittingSet.hittingSet(setOfMinTraits, 0, 3, 5, new MinSumFormulaWeights<>(formulaWeights));
        assertTrue(3 <= hittingSetMinSumFormulaWeights.size());

        Set<Set<Integer>> hittingSetMinMaxFormulaWeights = HittingSet.hittingSet(setOfMinTraits, 0, 2, 5, new MinMaxFormulaWeights<>(formulaWeights));
        assertTrue(2 <= hittingSetMinMaxFormulaWeights.size());

    }

    @Test
    public void testHittingSet() {
        Map<Integer, Float> formulaWeights = new HashMap<>();
        formulaWeights.put(1,0.2f);
        formulaWeights.put(2,0.3f);
        formulaWeights.put(3,0.1f);
        formulaWeights.put(4,0.25f);
        formulaWeights.put(5,0.6f);
        formulaWeights.put(7,0.7f);

        long timeout = 1000L; // one second timeout

        final Set<Set<Integer>> setOfMinTraits = getSet(getSet(1,2,3), getSet(3,5), getSet(1,4), getSet(4,7));

        Set<Set<Integer>> hittingSetMinCardinality = HittingSet.hittingSet(setOfMinTraits, timeout, 1, 1, new MinQueryCardinality<>());
        final Set<Set<Integer>> expectedSet = getSet(getSet(3, 4));

        generalTest(hittingSetMinCardinality, 1, expectedSet);

        Set<Set<Integer>> hittingSetMinSumFormulaWeights = HittingSet.hittingSet(setOfMinTraits, timeout, 1, 1, new MinSumFormulaWeights<>(formulaWeights));
        generalTest(hittingSetMinSumFormulaWeights, 1, expectedSet);

        Set<Set<Integer>> hittingSetMinMaxFormulaWeights = HittingSet.hittingSet(setOfMinTraits, timeout, 1, 1, new MinMaxFormulaWeights<>(formulaWeights));
        generalTest(hittingSetMinMaxFormulaWeights, 1, expectedSet);


        for (int max = 2; max <= 5; max++) { // there are 5 hitting sets
            hittingSetMinCardinality = HittingSet.hittingSet(setOfMinTraits, timeout, 1, max, new MinQueryCardinality<>());
            assertEquals(max, hittingSetMinCardinality.size());

            hittingSetMinSumFormulaWeights = HittingSet.hittingSet(setOfMinTraits, timeout, 1, max, new MinSumFormulaWeights<>(formulaWeights));
            assertEquals(max, hittingSetMinSumFormulaWeights.size());

            hittingSetMinMaxFormulaWeights = HittingSet.hittingSet(setOfMinTraits, timeout, 1, max, new MinMaxFormulaWeights<>(formulaWeights));
            assertEquals(max, hittingSetMinMaxFormulaWeights.size());
        }

        // there are not more than 5 hitting sets

        for (int min = 6; min <= 1000; min++) {
            hittingSetMinCardinality = HittingSet.hittingSet(setOfMinTraits, timeout, min, min, new MinQueryCardinality<>());
            assertEquals(5, hittingSetMinCardinality.size());

            hittingSetMinSumFormulaWeights = HittingSet.hittingSet(setOfMinTraits, timeout, min, min, new MinSumFormulaWeights<>(formulaWeights));
            assertEquals(5, hittingSetMinSumFormulaWeights.size());

            hittingSetMinMaxFormulaWeights = HittingSet.hittingSet(setOfMinTraits, timeout, min, min, new MinMaxFormulaWeights<>(formulaWeights));
            assertEquals(5, hittingSetMinMaxFormulaWeights.size());
        }


    }

    @Test
    public void testHittingSetWithMinQueryCardinality() {
        long timeout = 1000L; // one second timeout

        // Suppose conflict sets are {M1, M2, A1}, {M1, A1, A2, M3}. The minimal hitting sets (diagnosis) are {M1}, {A1}, {M2, A2}, {M2, M3}
        final Set<Set<String>> conflictSets = getSet(getSet("M1", "M2", "A1"), getSet("M1", "A1", "A2", "M3"));

        Set<Set<String>> minimalHittingSet = HittingSet.hittingSet(conflictSets, timeout, 1, 4, new MinQueryCardinality<>());
        Set<Set<String>> expectedResult = getSet(getSet("M1"), getSet("A1"), getSet("M2", "A2"), getSet("M2", "M3"));
        generalTest(minimalHittingSet, 4, expectedResult);

        // there are not more than 4
        minimalHittingSet = HittingSet.hittingSet(conflictSets, timeout, 5, 4000000, new MinQueryCardinality<>());
        generalTest(minimalHittingSet, 4, expectedResult);

        // test evolution
        // 1
        minimalHittingSet = HittingSet.hittingSet(conflictSets, timeout, 1, 1, new MinQueryCardinality<>());
        assertTrue(minimalHittingSet.equals(getSet(getSet("M1"))) || minimalHittingSet.equals(getSet(getSet("A1"))));

        // 2
        minimalHittingSet = HittingSet.hittingSet(conflictSets, timeout, 2, 2, new MinQueryCardinality<>());
        assertTrue(minimalHittingSet.equals(getSet(getSet("M1"),getSet("A1"))));

        // 3
        minimalHittingSet = HittingSet.hittingSet(conflictSets, timeout, 3, 3, new MinQueryCardinality<>());
        assertTrue(minimalHittingSet.equals(getSet(getSet("M1"),getSet("A1"), getSet("M2","A2"))) || minimalHittingSet.equals(getSet(getSet("M1"),getSet("A1"), getSet("M2","M3"))));
    }

    @Test
    public void testHittingSetWithMinSumFormulaWeights() {
        // Suppose conflict sets are {M1, M2, A1}, {M1, A1, A2, M3}. The minimal hitting sets (diagnosis) are {M1}, {A1}, {M2, A2}, {M2, M3}

        // sum weights:
        // {M1}  = 0.5
        // {A1}  = 1.3
        // {M2, A2} = 0.6
        // {M2, M3} = 0.35

        final Set<Set<String>> conflictSets = getSet(getSet("M1", "M2", "A1"), getSet("M1", "A1", "A2", "M3"));
        long timeout = 1000L; // one second timeout
        Map<String, Float> formulaWeights = new HashMap<>();
        formulaWeights.put("A1",1.3f);
        formulaWeights.put("A2",0.5f);
        formulaWeights.put("M1",0.5f);
        formulaWeights.put("M2",0.1f);
        formulaWeights.put("M3",0.25f);
        final MinSumFormulaWeights<String> sortCriterion = new MinSumFormulaWeights<>(formulaWeights);

        // there must be at most 4 expected hitting sets.
        generalTest(HittingSet.hittingSet(conflictSets, timeout, 1, 10, sortCriterion), 4, getSet(getSet("M1"), getSet("A1"), getSet("M2", "A2"), getSet("M2", "M3")));

        // test evolution
        // 1
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 1, 1, sortCriterion).equals(getSet(getSet("M2","M3"))));

        // 2
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 2, 2, sortCriterion).equals(getSet(getSet("M2","M3"),getSet("M1"))));

        // 3
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 3, 3, sortCriterion).equals(getSet(getSet("M2","M3"),getSet("M1"),getSet("M2","A2"))));

        // 4
        generalTest(HittingSet.hittingSet(conflictSets, timeout, 4, 4, sortCriterion), 4, getSet(getSet("M1"), getSet("A1"), getSet("M2", "A2"), getSet("M2", "M3")));

        // now lower weight of {"A1"} to 0.6 which equals the sum of weights of {"M2","A2"} -> the sort criterion must prefer {"A1"}
        formulaWeights.put("A1",0.6f);

        // test evolution
        // 1
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 1, 1, sortCriterion).equals(getSet(getSet("M2","M3"))));

        // 2
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 2, 2, sortCriterion).equals(getSet(getSet("M2","M3"),getSet("M1"))));

        // 3
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 3, 3, sortCriterion).equals(getSet(getSet("M2","M3"),getSet("M1"),getSet("A1"))));

        // 4
        generalTest(HittingSet.hittingSet(conflictSets, timeout, 4, 4, sortCriterion), 4, getSet(getSet("M1"), getSet("A1"), getSet("M2", "A2"), getSet("M2", "M3")));
    }

    @Test
    public void testHittingSetWithMinMaxFormulaWeights() {
        // Suppose conflict sets are {M1, M2, A1}, {M1, A1, A2, M3}. The minimal hitting sets (diagnosis) are {M1}, {A1}, {M2, A2}, {M2, M3}

        // Criterion: Max-Formula-Weights weights:
        // {M1}  = 0.5
        // {A1}  = 1.3
        // {M2, A2} = 0.5
        // {M2, M3} = 0.25

        final Set<Set<String>> conflictSets = getSet(getSet("M1", "M2", "A1"), getSet("M1", "A1", "A2", "M3"));
        long timeout = 1000L; // one second timeout
        Map<String, Float> formulaWeights = new HashMap<>();
        formulaWeights.put("A1",1.3f);
        formulaWeights.put("A2",0.5f);
        formulaWeights.put("M1",0.5f);
        formulaWeights.put("M2",0.1f);
        formulaWeights.put("M3",0.25f);
        final MinMaxFormulaWeights<String> sortCriterion = new MinMaxFormulaWeights<>(formulaWeights);

        // there must be at most 4 expected hitting sets.
        generalTest(HittingSet.hittingSet(conflictSets, timeout, 1, 10, sortCriterion), 4, getSet(getSet("M1"), getSet("A1"), getSet("M2", "A2"), getSet("M2", "M3")));

        // test evolution
        // 1
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 1, 1, sortCriterion).equals(getSet(getSet("M2","M3"))));

        // 2
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 2, 2, sortCriterion).equals(getSet(getSet("M2","M3"),getSet("M1"))));

        // 3
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 3, 3, sortCriterion).equals(getSet(getSet("M2","M3"),getSet("M1"),getSet("M2","A2"))));

        // 4
        generalTest(HittingSet.hittingSet(conflictSets, timeout, 4, 4, sortCriterion), 4, getSet(getSet("M1"), getSet("A1"), getSet("M2", "A2"), getSet("M2", "M3")));

        // now raisse weight of {"M1"} to 0.6 which results in the following:
        // {M1}  = 0.6
        // {A1}  = 1.3
        // {M2, A2} = 0.5
        // {M2, M3} = 0.25
        formulaWeights.put("M1",0.6f);

        // test evolution
        // 1
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 1, 1, sortCriterion).equals(getSet(getSet("M2","M3"))));

        // 2
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 2, 2, sortCriterion).equals(getSet(getSet("M2","M3"),getSet("M2","A2"))));

        // 3
        assertTrue(HittingSet.hittingSet(conflictSets, timeout, 3, 3, sortCriterion).equals(getSet(getSet("M2","M3"),getSet("M2","A2"),getSet("M1"))));

        // 4
        generalTest(HittingSet.hittingSet(conflictSets, timeout, 4, 4, sortCriterion), 4, getSet(getSet("M1"), getSet("A1"), getSet("M2", "A2"), getSet("M2", "M3")));
    }
/*
    @Test(timeout=72000)
    public void testExpensiveTestCase() {
        final Set<Set<Integer>> setOfMinTraits = getSet(getSet(1,2,3,4,5), getSet(6,7,8,9,10), getSet(11,12,13,14,15), getSet(16,17,18,19,20,21,22,23), getSet(24,25,26,27,28,29,30), getSet(31,32,33,34,35,36,37,38,39,40));

        Set<Set<Integer>> hittingSetMinCardinality = HittingSet.hittingSet(setOfMinTraits, 70000, 1, 500000, new MinQueryCardinality<>());
        System.out.println(hittingSetMinCardinality.size());
    }
*/
    private void generalTest(Set hittingSet, int expectedSize, Set expectedResult) {
        assertNotNull(hittingSet);
        assertEquals(expectedSize, hittingSet.size());
        assertEquals(expectedResult, hittingSet);
    }

}
