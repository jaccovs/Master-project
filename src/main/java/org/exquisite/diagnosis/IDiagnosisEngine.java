package org.exquisite.diagnosis;

import java.util.List;
import java.util.Set;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import choco.kernel.model.constraints.Constraint;

/**
 * Defines an interface for tests.diagnosis engine implementations.
 * @author David
 */
public interface IDiagnosisEngine {
	
	/**
	 * Reverts the state of the engine to how it was when first instantiated.
	 */
	public void resetEngine();	
	
	/**
	 * Returns the constraint model
	 * @return
	 */
	public ExquisiteSession getSessionData();

	/**
	 * Setter for the constraint model
	 * @param model
	 */
	public void setSessionData(ExquisiteSession sessionData);	
	
	public DiagnosisModel getModel();

	/**
	 * Start the tests.diagnosis process.
	 * @return a list of diagnoses or an empty list if no diagnoses were found.
	 * @throws DomainSizeException 
	 */
	public List<Diagnosis> calculateDiagnoses() throws DiagnosisException;
	
	/**
	 * Returns number of calls made to the solver.
	 */
	public int getSolverCalls();	
	public void incrementSolverCalls();
	
	public long getSolverTime();	
	public void incrementSolverTime(long time);
	
	/**
	 * @return number of calls to solver with a model that proved to be solvable.
	 */
	public int getCspSolvedCount();	
	public void incrementCSPSolutionCount();
	
	/**
	 * @return number of propagations performed by the solver.
	 */
	public int getPropagationCount();	
	public void incrementPropagationCount();
	
	public int getTPCalls();	
	public void incrementQXPCalls();
	
	public int getSearchesForConflicts();	
	public void incrementSearchesForConflicts();
	
	public int getMXPConflicts();
	public void incrementMXPConflicts(int conflicts);
	
	public int getMXPSplittingTechniqueConflicts();
	public void incrementMXPSplittingTechniqueConflicts(int conflicts);
	
	/**
	 * returns the time where the tests.diagnosis task was finished
	 * (Not all threads have to be stopped at this point)
	 * @return
	 */
	public long getFinishedTime();
}
