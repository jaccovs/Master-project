package org.exquisite.diagnosis.invquickxplain;

import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.datamodel.DiagnosisModel;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.models.Diagnosis;

import java.util.ArrayList;
import java.util.List;

import static org.exquisite.core.measurements.MeasurementManager.start;
import static org.exquisite.core.measurements.MeasurementManager.stop;

/**
 * A tests.diagnosis engine that uses InverseQuickXplain to calculate a single tests.diagnosis.
 *
 * @author Thomas
 */
public class InvQXDiagnosisEngine<T> implements IDiagnosisEngine<T> {

    // A list of all known constraints (without example constraints)
    public List<T> allConstraints = new ArrayList<>();
    org.exquisite.core.model.DiagnosisModel model;
    DiagnosisModel<T> sessionData;
//    int solverCalls = 0;
//    long solverTime = 0;
//    int propagationCount = 0;
//    int cspSolutionCount = 0;
//    int qxpCalls = 0;
//    int searchesForConflicts = 0;
//    int mxpConflicts = 0;
//    int mxpSplittingTechniqueConflicts = 0;
//    int constructedNodes = 0;
//    long finishedTime = 0;

    /**
     * Enforcing some dependencies in order for engine to work correctly.
     *
     * @param sessionData
     */
    public InvQXDiagnosisEngine(ExcelExquisiteSession<T> sessionData) {
        this.setDiagnosisModel(sessionData);
        this.model = new org.exquisite.core.model.DiagnosisModel(sessionData.getDiagnosisModel());

        // Remember the known constraints. This should never be changed afterwards when the
        // model is copied
        this.allConstraints.addAll(this.model.getPossiblyFaultyStatements());
    }

    @Override
    public void resetEngine() {

    }

    public DiagnosisModel<T> getDiagnosisModel() {
        return sessionData;
    }

    @Override
    public void setDiagnosisModel(DiagnosisModel<T> diagnosisModel) {
        this.sessionData = diagnosisModel;
    }

    @Override
    public List<Diagnosis<T>> calculateDiagnoses() throws DiagnosisException {
        start("calculate.diagnosis");
        InverseQuickXplain<T> invQX = new InverseQuickXplain<>(getDiagnosisModel());

        List<List<T>> conflicts = invQX.findConflicts();
        List<Diagnosis<T>> diagnoses = new ArrayList<>();

        for (List<T> conflict : conflicts) {
            Diagnosis<T> diag = new Diagnosis<>(conflict, model);
            diagnoses.add(diag);
        }

        stop("calculate.diagnosis");
        return diagnoses;
    }


//
//    @Override
//    public void incrementSolverTime(long amount) {
//        solverTime += amount;
//    }
//
//    @Override
//    public void incrementPropagationCount() {
//        incrementCounter("propagation.count");
//    }
//
//    @Override
//    public void incrementCSPSolutionCount() {
//        cspSolutionCount++;
//    }
//
//    @Override
//    public void incrementQXPCalls() {
//        qxpCalls++;
//    }
//
//    @Override
//    public void incrementSearchesForConflicts() {
//        searchesForConflicts++;
//    }
//
//    @Override
//    public void incrementMXPConflicts(int conflicts) {
//        mxpConflicts += conflicts;
//    }
//
//    @Override
//    public void incrementMXPSplittingTechniqueConflicts(int conflicts) {
//        mxpSplittingTechniqueConflicts += conflicts;
//    }
//
//    /**
//     * Returns the number of calls to the solver made by qx.
//     */
//    @Override
//    public int rgetSolverCalls() {
//        return solverCalls;
//    }
//
//    @Override
//    public long getSolverTime() {
//        return solverTime;
//    }
//
//    /**
//     * Returns number of propagations invoked in the solver.
//     */
//    @Override
//    public int getPropagationCount() {
//        return propagationCount;
//    }
//
//    /**
//     * Number of CSP solutions found by the solver.
//     */
//    @Override
//    public int getCspSolvedCount() {
//        return cspSolutionCount;
//    }
//
//    @Override
//    public int getTPCalls() {
//        return qxpCalls;
//    }
//
//    @Override
//    public int getSearchesForConflicts() {
//        return searchesForConflicts;
//    }
//
//    @Override
//    public int getMXPConflicts() {
//        return mxpConflicts;
//    }
//
//    @Override
//    public int getMXPSplittingTechniqueConflicts() {
//        return mxpSplittingTechniqueConflicts;
//    }
//
//    public long getFinishedTime() {
//        return finishedTime;
//    }

    @Override
    public org.exquisite.core.model.DiagnosisModel getDiagnosisModel() {
        return model;
    }

}
