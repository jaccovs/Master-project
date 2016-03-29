package org.exquisite.core;

import org.exquisite.core.model.Diagnosis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kostya on 03.12.2015.
 */
public class Utils {
    public static <F> boolean hasIntersection(Collection<F> col1, Collection<F> col2) {
        return col1.parallelStream().anyMatch(col2::contains);
        /*
        for (F f : col1) {
            if (col2.contains(f)) return true;
        }
        return false;
        */
    }

    public static <F> boolean hasIntersectionSet(Collection<Set<F>> col1, Collection<F> col2) {
        return col1.parallelStream().anyMatch((set) -> col2.stream().anyMatch(set::contains));
        /*
        for (Set<F> f : col1) {
            for (F f1 : col2) {
                if (f.contains(f1)) return true;
            }
        }
        return false;
        */
    }

    /**
     * Creates a new set of diagnoses representing the union of all sets given as parameter.
     *
     * @param sets Sets of diagnoses, they are unmodified after operation.
     * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
     * @return A new set of diagnoses representing the union of all sets of diagnoses.
     */
    @SafeVarargs
    public static <F> Set<Diagnosis<F>> union(Set<Diagnosis<F>>... sets) {
        // TODO add assertion that no intersection is allowed ?
        Set<Diagnosis<F>> union = new HashSet<>();
        for (Set<Diagnosis<F>> set: sets) union.addAll(set);
        return union;
    }

    /**
     * Returns the first element from a collection. If parameter removeIt is <code>true</code>, the element is also removed from the collection.
     * @param collection a collection.
     * @param removeIt If parameter removeIt is <code>true</code>, the element is also removed from the collection, otherwise the element will remain in collection.
     * @param <T> any type.
     * @return The first element in the collection.
     */
    public static <T> T getFirstElem(Collection<T> collection, boolean removeIt) {
        T t = collection.iterator().next();
        if (removeIt) collection.remove(t);
        return t;
    }
}
