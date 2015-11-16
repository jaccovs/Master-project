package org.exquisite.diagnosis.invquickxplain;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A tests.diagnosis engine that uses InverseQuickXplain to calculate a single tests.diagnosis.
 *
 * @author Thomas
 */
public class InvQXDiagnosisEngine<T> implements IDiagnosisEngine<T> {

    // A list of all known constraints (without example constraints)
    public List<T> allConstraints = new ArrayList<>();
    DiagnosisModel<T> model;
    ExquisiteSession<T> sessionData;
    int solverCalls = 0;
    long solverTime = 0;
    int propagationCount = 0;
    int cspSolutionCount = 0;
    int qxpCalls = 0;
    int searchesForConflicts = 0;
    int mxpConflicts = 0;
    int mxpSplittingTechniqueConflicts = 0;
    int constructedNodes = 0;
    long finishedTime = 0;

    /**
     * Enforcing some dependencies in order for engine to work correctly.
     *
     * @param sessionData
     */
    public InvQXDiagnosisEngine(ExquisiteSession<T> sessionData) {
        this.setSessionData(sessionData);
        this.model = new DiagnosisModel<>(sessionData.diagnosisModel);

        // Remember the known constraints. This should never be changed afterwards when the
        // model is copied
        this.allConstraints.addAll(this.model.getPossiblyFaultyStatements());
    }

    @Override
    public void resetEngine() {

    }

    @Override
    public ExquisiteSession<T> getSessionData() {
        return sessionData;
    }

    @Override
    public void setSessionData(ExquisiteSession<T> sessionData) {
        this.sessionData = sessionData;
    }

    @Override
    public List<Diagnosis<T>> calculateDiagnoses() throws DiagnosisException {
        InverseQuickXplain<T> invQX = new InverseQuickXplain<>(getSessionData(), this);

        List<List<T>> conflicts = invQX.findConflicts();
        List<Diagnosis<T>> diagnoses = new ArrayList<>();

        for (List<T> conflict : conflicts) {
            Diagnosis<T> diag = new Diagnosis<>(conflict, model);
            diagnoses.add(diag);
        }

        finishedTime = System.nanoTime();

        return diagnoses;
    }

    @Override
    public void incrementSolverCalls() {
        solverCalls++;
    }

    @Override
    public void incrementSolverTime(long amount) {
        solverTime += amount;
    }

    @Override
    public void incrementPropagationCount() {
        propagationCount++;
    }

    @Override
    public void incrementCSPSolutionCount() {
        cspSolutionCount++;
    }

    @Override
    public void incrementQXPCalls() {
        qxpCalls++;
    }

    @Override
    public void incrementSearchesForConflicts() {
        searchesForConflicts++;
    }

    @Override
    public void incrementMXPConflicts(int conflicts) {
        mxpConflicts += conflicts;
    }

    @Override
    public void incrementMXPSplittingTechniqueConflicts(int conflicts) {
        mxpSplittingTechniqueConflicts += conflicts;
    }

    /**
     * Returns the number of calls to the solver made by qx.
     */
    @Override
    public int getSolverCalls() {
        return solverCalls;
    }

    @Override
    public long getSolverTime() {
        return solverTime;
    }

    /**
     * Returns number of propagations invoked in the solver.
     */
    @Override
    public int getPropagationCount() {
        return propagationCount;
    }

    /**
     * Number of CSP solutions found by the solver.
     */
    @Override
    public int getCspSolvedCount() {
        return cspSolutionCount;
    }

    @Override
    public int getTPCalls() {
        return qxpCalls;
    }

    @Override
    public int getSearchesForConflicts() {
        return searchesForConflicts;
    }

    @Override
    public int getMXPConflicts() {
        return mxpConflicts;
    }

    @Override
    public int getMXPSplittingTechniqueConflicts() {
        return mxpSplittingTechniqueConflicts;
    }

    @Override
    public long getFinishedTime() {
        return finishedTime;
    }

    @Override
    public DiagnosisModel<T> getModel() {
        return model;
    }

}
