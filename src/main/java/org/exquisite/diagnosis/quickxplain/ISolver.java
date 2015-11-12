package org.exquisite.diagnosis.quickxplain;

import java.util.List;
import java.util.Set;

import org.exquisite.diagnosis.IDiagnosisEngine;

import choco.kernel.model.constraints.Constraint;

/**
 * A common interface for the different solvers
 * @author Thomas
 *
 */
public interface ISolver {
	
	/**
	 * A method to post the constraints
	 * @param qx
	 * @param constraints
	 */
	void createModel(QuickXPlain qx, List<Constraint> constraints);
	
	/**
	 * Calls the solver
	 * @param diagnosisEngine
	 * @return
	 */
	boolean isFeasible(IDiagnosisEngine diagnosisEngine);
	
	/**
	 * Special version of isFeasible, that checks if the constraints passed to the Solver with createModel() entail the given entailments
	 * @param entailments
	 * @return
	 */
	boolean isEntailed(IDiagnosisEngine diagnosisEngine, Set<Constraint> entailments);

	/**
	 * Calculates the implicitly entailed constraints.
	 * @return
	 */
	Set<Constraint> calculateEntailments();
}
