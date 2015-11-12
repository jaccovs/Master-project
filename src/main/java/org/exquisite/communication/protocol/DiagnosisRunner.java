package org.exquisite.communication.protocol;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.ranking.DiagnosisRanker;

/**
 * This is an adapter class which wraps a tests.diagnosis engine in
 * a runnable object so that it can be run in a <tt>NotifyingThread</tt> instance.
 * Take a look at <tt>ServerProtocol</tt> for an example of its use.
 * 
 * @author David
 * 
 * @see org.exquisite.communication.protocol.ServerProtocol
 * @see org.exquisite.threading.NotifyingThread
 */
public class DiagnosisRunner implements Runnable
{
	volatile IDiagnosisEngine engine;
	volatile List<Diagnosis> diagnoses = new ArrayList<Diagnosis>();
	volatile long startTime;
	volatile long endTime;
	
	/**
	 * @param engine    the tests.diagnosis engine to be run in a thread.
	 */
	public DiagnosisRunner(IDiagnosisEngine engine){
		this.engine = engine;
	}
	
	@Override
	public void run() {
		System.out.println("Running diagnoses");
		try{
			startTime = System.currentTimeMillis();
			diagnoses = engine.calculateDiagnoses();
			diagnoses = DiagnosisRanker.rankDiagnoses(diagnoses, engine.getSessionData());
			endTime = System.currentTimeMillis();
		}
		catch(DiagnosisException e){			
			System.err.println("    DiagnosisException caught when calling engine.calculateDiagnoses.");			
		}
	}
}
