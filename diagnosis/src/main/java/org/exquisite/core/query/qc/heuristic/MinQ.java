package org.exquisite.core.query.qc.heuristic;

import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.QPartition;

import java.util.*;

/**
 * QuickXPlain algorithm used for calculation of optimized query in HeuristicQC. TODO: TEST
 *
 * @author patrick
 * @author wolfi
 */
public class MinQ<F>  {

    protected Split split = Split.Half;

    // Mapping from the technical report Algorithm 8 Query Optimization
    //                             QB         X          Q
    protected List<F> minQ(List<F> b, List<F> d, List<F> c, QPartition qpartition, AbstractDiagnosisEngine<F> dpi) {
        if (!d.isEmpty() && isQPartConst(b, qpartition, dpi))
            return new ArrayList<>(0);
        if (c.size() == 1)
            return new ArrayList<>(c);
        //Collections.sort(c)
        int k = split(c);
        List<F> c1 = c.subList(0, k);
        List<F> c2 = c.subList(k, c.size());

        b.addAll(c1);
        List<F> d2 = minQ(b, c1, c2, qpartition, dpi);
        replace(b, c1, d2);

        List<F> d1 = minQ(b, d2, c1, qpartition, dpi);
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

    private boolean isQPartConst(List<F> q, QPartition<F> qpartition, AbstractDiagnosisEngine<F> dpi) {
        DiagnosisModel<F> diagnosisModel = dpi.getSolver().getDiagnosisModel();

        for (Diagnosis<F> dr : qpartition.dnx) {
            Set<F> kr1 = new HashSet<>(diagnosisModel.getPossiblyFaultyFormulas());
            kr1.removeAll(dr.getFormulas());
            kr1.addAll(diagnosisModel.getCorrectFormulas());
            kr1.addAll(diagnosisModel.getEntailedExamples());

            kr1.addAll(q);

            if (dpi.getSolver().isConsistent(kr1)) {
                return false;
            }
        }

        for (Diagnosis<F> dr : qpartition.dz) {
            Set<F> kr2 = new HashSet<>(diagnosisModel.getPossiblyFaultyFormulas());
            kr2.removeAll(dr.getFormulas());
            kr2.addAll(diagnosisModel.getCorrectFormulas());
            kr2.addAll(diagnosisModel.getEntailedExamples());
            if (dpi.getSolver().isEntailed(kr2, q))
                return false;
        }
        return true;
    }

}
