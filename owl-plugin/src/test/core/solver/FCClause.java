package org.exquisite.core.solver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Stream;

/**
 * Created by kostya on 25-Feb-16.
 */
public class FCClause extends HashSet<Integer> {

    public int maxSymbol;

    public FCClause(Collection<Integer> clause) {
        super(clause);
        Stream<Integer> stream = clause.stream().filter(i -> i > 0);
        if (clause.size() == 0 || stream.count() > 1)
            throw new IllegalArgumentException(clause.toString() + " is not a horn clause!");

        if (clause.stream().filter(i -> i == 0).count() > 0)
            throw new IllegalArgumentException(clause.toString() + " has an invalid symbol 0");
        maxSymbol = clause.stream().map(Math::abs).max(Comparator.naturalOrder()).get();
    }

    public FCClause(Integer... symbol) {
        this(Arrays.asList(symbol));
    }

    public boolean isUnit() {
        return size() == 1;
    }

}
