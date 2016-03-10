package org.exquisite.diagnosis.quickxplain.choco3;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.core.ISolver;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import solver.variables.VariableFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Does the plain constraints
 *
 * @author dietmar
 */
public class Choco3PlainConstraintSolver implements ISolver<Constraint> {

    // store the model
    public DiagnosisModel<Constraint> diagnosisModel;

    // the internal solver
    public solver.Solver solver;

    // the current example
    public Example currentExample;

    // The variable map
    Map<String, solver.variables.IntVar> theVariables = new HashMap<String, solver.variables.IntVar>();

    /**
     * Create the solver
     *
     * @param diagnosisModel
     */
    // Create the model
    @Override
    public void createModel(ConstraintsQuickXPlain<Constraint> qx, List<Constraint> constraints) {
        this.diagnosisModel = qx.currentDiagnosisModel;
        this.currentExample = qx.currentExample;
        this.solver = new solver.Solver();
        this.createVariables();
        this.postConstraints(constraints);

    }

    // Return a handle to the solver
    public solver.Solver getSolver() {
        return solver;
    }

    // Look for a solution
    @Override
    public boolean isFeasible() {
        if (this.solver == null) {
            System.err.println("No solver defined in Choco3PlainConstraintSolver");
            System.exit(0);
        }

        return this.solver.findSolution();
    }

    /**
     * Create the CSP problem to be solved
     */
    public void createVariables() {
        // Create the variables according to the model
        List<Variable> variables = diagnosisModel.getVariables();
        for (Variable v : variables) {
            IntegerVariable integervar = (IntegerVariable) v;
            // Create a solver variable
            solver.variables.IntVar intvar = VariableFactory
                    .bounded(v.getName(), integervar.getLowB(), integervar.getUppB(), solver);
            theVariables.put(v.getName(), intvar);
        }
    }

    /**
     * Post the correct constraints to the solver
     *
     * @param constraints
     */
    public void postConstraints(List<Constraint> constraints) {

        for (Constraint c : constraints) {
            // Get the executor class
            C3Runner runner = this.diagnosisModel.c3runners.get(c);
            if (runner != null) {
                // System.out.println("Found a runner");
                runner.setVariables(this.theVariables);
                runner.setSolver(this.solver);
                runner.postConstraint();
            } else {
                // System.out.println("Must be an example?");
            }
        }
        // Get the current example
        C3Runner examplerunner = this.diagnosisModel.c3examplerunners.get(this.currentExample);
        if (examplerunner != null) {
            // System.out.println("Found example runner ...");
            examplerunner.setVariables(this.theVariables);
            examplerunner.setSolver(this.solver);
            examplerunner.postConstraint();
        } else {
            // System.out.println("No example runner found");
        }

    }

    @Override
    public boolean isEntailed(Set<Constraint> entailments) {
        throw new NotImplementedException();
    }

    @Override
    public Set<Constraint> calculateEntailments() {
        throw new NotImplementedException();
    }

}
