package org.exquisite.diagnosis.quickxplain.choco3;

import java.util.Map;

import solver.variables.IntVar;
/**
 * To run some stuff. Specific constraints overwrite the post constraint method.
 * The method can access the variables and the solver to actually post the constraint
 * @author dietmar
 *
 */
public abstract class C3Runner {
	// The solver to be injected
	public solver.Solver solver = null;
	// The variables to be injected
	public Map<String,IntVar> variables = null;
	
	// setters
	public void setVariables(Map<String,IntVar> variables) {this.variables = variables;};
	public void setSolver(solver.Solver solver) {this.solver = solver;}
	
	// a helper to get access to some variable
	public IntVar var(String name) {
		IntVar result = variables.get(name);
		if (result == null) {
			System.err.println("FATAL: Cannot find variable in Choco3Runner: " + name);
		}
		return result;
	}
	
	// override this one. 
	// TODO: Could be a function that returns a constraint which is then automatically
	// posted by the system
	abstract public void postConstraint();
	
}
 