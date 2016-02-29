package org.exquisite.core.search;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.ISolver;

import java.util.*;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.COUNTER_QXP_CALLS;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.incrementCounter;

/**
 * A general interface for conflict searchers, like QuickXPlain, MergeXPlain or Progression
 */
public class QuickXPlain<F> implements IConflictSearcher<F> {


    protected final ISolver<F> solver;
    protected Split split = Split.Half;

    public QuickXPlain(ISolver<F> solver) {
        this.solver = solver;

    }

    @Override
    public Set<Set<F>> findConflicts(Collection<F> formulas) throws DiagnosisException {
        incrementCounter(COUNTER_QXP_CALLS);
        if (checkInput(formulas))
            return Collections.emptySet();

        ArrayList<F> b = new ArrayList<>(formulas.size());
        ArrayList<F> c = new ArrayList<>(formulas);
        List<F> conflict = quickXPlain(b, b, c);
        return Collections.singleton(new HashSet<>(conflict));
    }

    protected boolean checkInput(Collection<F> formulas) throws DiagnosisException {
        if (!solver.isConsistent(Collections.emptySet()))
            throw new DiagnosisException("Inconsistent diagnosis model! Conflict finding is not possible!");
        return solver.isConsistent(formulas);
    }

    protected List<F> quickXPlain(List<F> b, List<F> d, List<F> c) {
        if (!d.isEmpty() && !solver.isConsistent(b))
            return new ArrayList<>(0);
        if (c.size() == 1)
            return new ArrayList<>(c);
        //Collections.sort(c)
        int k = split(c);
        List<F> c1 = c.subList(0, k);
        List<F> c2 = c.subList(k, c.size());

        b.addAll(c1);
        List<F> d2 = quickXPlain(b, c1, c2);
        replace(b, c1, d2);

        List<F> d1 = quickXPlain(b, d2, c1);
        replace(b, d2, Collections.emptyList());

        d1.addAll(d2);
        return d1;
    }

    void replace(List<F> b, List<F> remove, List<F> add) {
        int len = (remove.size() > add.size()) ? remove.size() : add.size();

        for (int i = 0; i < len; i++) {
            int index = b.size() - remove.size() + i;
            if (i >= add.size())
                b.remove(index);
            else if (index < b.size())
                b.set(index, add.get(i));
            else
                b.add(add.get(i));
        }
    }

    protected int split(List<F> c) {
        switch (this.split) {
            case Half:
                return c.size() / 2;
            case One:
                return 0;
        }
        throw new IllegalArgumentException("Unknown split function");
    }

    public void setSplit(Split split) {
        this.split = split;
    }

    public enum Split {Half, One}
}
