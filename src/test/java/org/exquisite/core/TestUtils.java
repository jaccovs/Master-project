package org.exquisite.core;

import org.exquisite.core.model.Diagnosis;
import org.junit.Test;

import java.util.*;

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
        ArrayList<Integer> list = new ArrayList<>(Collections.nCopies(20, 0));

        List<Integer> subl = list.subList(10, 20);
        for (int i = 0; i < subl.size(); i++)
            subl.set(i, 2);

        System.out.println(subl);
    }
}
