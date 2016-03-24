package org.exquisite.core.engines;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ISolver;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.conflictsearch.IConflictSearcher;
import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.costestimators.ICostsEstimator;
import org.exquisite.core.costestimators.SimpleCostsEstimator;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract engine that implements some factories, like conflict searchers, etc. and stores basic configuration
 * properties.
 */
public abstract class AbstractDiagnosisEngine<F> implements IDiagnosisEngine<F> {

    private int maxNumberOfDiagnoses = 0;
    private int maxDepth = 0;

    private ISolver<F> solver;
    private IConflictSearcher<F> searcher;
    private ICostsEstimator<F> costsEstimator;
    private Set<Set<F>> conflicts = new HashSet<>();
    private Set<Diagnosis<F>> diagnoses = new HashSet<>();

    public AbstractDiagnosisEngine(
            ISolver<F> solver) {
        this.solver = solver;
        searcher = new QuickXPlain<>(solver);
        this.costsEstimator = new SimpleCostsEstimator<F>();
    }


    public int getMaxNumberOfDiagnoses() {
        return maxNumberOfDiagnoses;
    }

    @Override
    public void setMaxNumberOfDiagnoses(int maxNumberOfDiagnoses) {
        this.maxNumberOfDiagnoses = maxNumberOfDiagnoses;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public AbstractDiagnosisEngine setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public ISolver<F> getSolver() {
        return this.solver;
    }

    protected DiagnosisModel<F> getDiagnosisModel() {
        return getSolver().getDiagnosisModel();
    }

    public IConflictSearcher<F> getSearcher() {
        return searcher;
    }

    public void setSearcher(IConflictSearcher<F> searcher) {
        this.searcher = searcher;
    }

    public Set<Set<F>> getConflicts() {
        return conflicts;
    }

    @Override
    public void setConflicts(Set<Set<F>> conflicts) {
        this.conflicts = conflicts;
    }

    public Set<Diagnosis<F>> getDiagnoses() {
        return diagnoses;
    }

    public ICostsEstimator<F> getCostsEstimator() {
        return costsEstimator;
    }

    public void setCostsEstimator(
            ICostsEstimator<F> costsEstimator) {
        this.costsEstimator = costsEstimator;
    }

    public void resetEngine() {
        this.conflicts.clear();
        this.diagnoses.clear();
    }
}
