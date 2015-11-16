package org.exquisite.communication.protocol;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.ranking.DiagnosisRanker;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an adapter class which wraps a tests.diagnosis engine in
 * a runnable object so that it can be run in a <tt>NotifyingThread</tt> instance.
 * Take a look at <tt>ServerProtocol</tt> for an example of its use.
 *
 * @author David
 * @see org.exquisite.communication.protocol.ServerProtocol
 * @see org.exquisite.threading.NotifyingThread
 */
public class DiagnosisRunner<T> implements Runnable {
    volatile IDiagnosisEngine<T> engine;
    volatile List<Diagnosis<T>> diagnoses = new ArrayList<>();
    volatile long startTime;
    volatile long endTime;

    /**
     * @param engine the tests.diagnosis engine to be run in a thread.
     */
    public DiagnosisRunner(IDiagnosisEngine<T> engine) {
        this.engine = engine;
    }

    @Override
    public void run() {
        System.out.println("Running diagnoses");
        try {
            startTime = System.currentTimeMillis();
            diagnoses = engine.calculateDiagnoses();
            diagnoses = DiagnosisRanker.rankDiagnoses(diagnoses, engine.getSessionData());
            endTime = System.currentTimeMillis();
        } catch (DiagnosisException e) {
            System.err.println("    DiagnosisException caught when calling engine.calculateDiagnoses.");
        }
    }
}
