package org.exquisite.core;

import org.exquisite.core.model.Diagnosis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

}
