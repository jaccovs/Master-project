package org.exquisite.core;

import org.exquisite.core.model.Diagnosis;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kostya on 10.12.2015.
 */
public class TestUtils {

    @SafeVarargs
    public static <T> HashSet<T> getSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    @SafeVarargs
    public static <T> Diagnosis<T> getDiagnosis(T... elements) {
        List<T> ts = Arrays.asList(elements);
        return new Diagnosis<T>(ts);
    }

    @Test
    public void TestSubList() {
        testSubList(new ArrayList<>(Collections.nCopies(10, 0)));
    }

    private void testSubList(List<Integer> list) {
        List<Integer> sub1 = list.subList(0, 5);
        List<Integer> sub2 = list.subList(5, 10);
        for (int i = 0; i < sub1.size(); i++)
            sub1.set(i, 2);

        System.out.println(sub1 + " - " + sub2);

        sub1.set(1, null);
        //HashSet<Integer> hs = new HashSet<>(list);
        Set<Integer> hs = list.parallelStream().filter(e -> e != null).collect(Collectors.toSet());
        System.out.println(sub1 + " - " + sub2 + " = " + hs);
        //List<Integer> sub2 = list.subList(6, 9);

    }
}
