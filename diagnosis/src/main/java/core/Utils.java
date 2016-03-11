package core;

import java.util.Collection;
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
}
