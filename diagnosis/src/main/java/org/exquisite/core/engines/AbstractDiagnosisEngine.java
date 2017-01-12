package org.exquisite.core.engines;

import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ISolver;
import org.exquisite.core.conflictsearch.IConflictSearcher;
import org.exquisite.core.conflictsearch.QuickXPlain;
import org.exquisite.core.costestimators.ICostsEstimator;
import org.exquisite.core.costestimators.SimpleCostsEstimator;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract engine that implements some factories, like conflict searchers, etc. and stores basic configuration
 * properties.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 */
public abstract class AbstractDiagnosisEngine<F> implements IDiagnosisEngine<F> {

    private int maxNumberOfDiagnoses = 0;
    private int maxDepth = 0;

    private ISolver<F> solver;
    private IConflictSearcher<F> searcher;
    private ICostsEstimator<F> costsEstimator;
    private Set<Set<F>> conflicts = new HashSet<>();
    private Set<Diagnosis<F>> diagnoses = new HashSet<>();
    private IExquisiteProgressMonitor monitor;

    public AbstractDiagnosisEngine(ISolver<F> solver) {
        this(solver,null);
    }

    public AbstractDiagnosisEngine(ISolver<F> solver, IExquisiteProgressMonitor monitor) {
        this.solver = solver;
        searcher = new QuickXPlain<>(solver);
        this.costsEstimator = new SimpleCostsEstimator<>();
        this.monitor = monitor;
        if (monitor != null) monitor.setCancel(false);
    }

    /**
     * @return Returns a progress monitor if one has been defined, otherwise <code>null</code> is returned
     */
    public IExquisiteProgressMonitor getMonitor() {
        return monitor;
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

    @Override
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
        if (this.monitor != null) this.monitor.taskStopped();
    }

    @Override
    public void dispose() {
        this.solver.dispose();
        this.conflicts.clear();
        this.diagnoses.clear();
        if (this.monitor != null) this.monitor.taskStopped();
        this.monitor = null;
    }

    protected void notifyTaskStarted() {
        if (monitor != null) {
            monitor.taskStarted(IExquisiteProgressMonitor.DIAGNOSES_CALCULATION + " using " + this);
            monitor.taskBusy("Start searching diagnoses ... (max. " + getMaxNumberOfDiagnoses() + ")");
        }
    }

    protected void notifyTaskStopped() {
        if (monitor != null)
            monitor.taskStopped();
    }

    protected void notifyTaskProgress(int diagnosesSize) {
        if (monitor != null) {
            monitor.taskBusy("found diagnosis " + diagnosesSize + " of max. " + getMaxNumberOfDiagnoses());
            monitor.setCancel(diagnosesSize > 1); // when more than one diagnoses have been found, the user can continue
        }
    }

    protected boolean isCancelled() {
        return monitor != null && monitor.isCancelled();
    }

}
