package org.exquisite.core.engines.query;

import org.exquisite.core.TestUtils;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.QPartition;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.exquisite.core.TestUtils.getDiagnosis;
import static org.junit.Assert.*;

/**
 * A JUnit Testcase for QPartition.
 *
 * Created by wolfi on 17.03.2016.
 */
public class TestQPartition {

        @Test
        public void testComputeSuccessors() {
            Assert.assertTrue(true);
        }

        @Test
        public void testGenerateInitialSuccessors() {

            Diagnosis<Integer> D1 = getDiagnosis(3,4);
            Diagnosis<Integer> D2 = getDiagnosis(4,5);
            Diagnosis<Integer> D3 = getDiagnosis(6,7);
            Diagnosis<Integer> D4 = getDiagnosis(1,4);
            Diagnosis<Integer> D5 = getDiagnosis(1,2);
            Diagnosis<Integer> D6 = getDiagnosis(2,3);

            Set<Diagnosis<Integer>> dx = TestUtils.getSet();
            Set<Diagnosis<Integer>> dnx = TestUtils.getSet(D1,D2,D3,D4,D5,D6);
            Set<Diagnosis<Integer>> dz = TestUtils.getSet();

            // check some preconditions
            assertTrue(dx.isEmpty());
            assertFalse(dnx.isEmpty());
            assertTrue(dz.isEmpty());

            QPartition<Integer> qPartition = new QPartition<>(dx,dnx,dz);

            Collection<QPartition<Integer>> initialSuccessors = qPartition.generateInitialSuccessors();

            assertTrue(!initialSuccessors.isEmpty()); // since dnx is not empty, initial successors must also be not empty

            assertEquals(dnx.size(),initialSuccessors.size()); // in fact the size of initialSuccessors must be the size of dnx


            // each Diagnosis in dnx must occur ONCE as a SINGLE dx element in initialSuccessors
            // boolean[] flags = new boolean[dnx.size()];
            for (QPartition<Integer> partition : initialSuccessors) {
                assertEquals(1,partition.dx.size());
                assertEquals(dnx.size()-1,partition.dnx.size());
                assertTrue(partition.dz.isEmpty());

                dnx.containsAll(partition.dnx);
                dnx.containsAll(partition.dx);

                Set<Diagnosis<Integer>> testSet = new HashSet<>();
                testSet.addAll(partition.dnx);
                testSet.addAll(partition.dx);

                assertEquals(dnx,testSet);
            }
        }
}
