package org.exquisite.diagnosis.quickxplain.choco3;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.core.ISolver;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import solver.variables.IntVar;
import solver.variables.VariableFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A wrapper for the Choco3 solver
 *
 * @author dietmar
 */
public class Choco3FormulaSolver implements ISolver<Constraint> {

    // A collection of variables objects that are created for the model to use.
    public Map<String, IntVar> variablesMap = new HashMap<String, IntVar>();

    // The real solver
    public solver.Solver solver;

    // The tests.diagnosis model
    DiagnosisModel<Constraint> diagnosisModel;

    // A handle to the calling QuickXPlain
    QuickXPlain<Constraint> qx = null;

    // Create things
    @Override
    public void createModel(QuickXPlain<Constraint> qx, List<Constraint> constraints) {
        this.solver = new solver.Solver();
        this.diagnosisModel = qx.currentDiagnosisModel;
        this.qx = qx;
        this.createModel(constraints);

    }

    @Override
    public boolean isFeasible(IDiagnosisEngine<Constraint> diagnosisEngine) {
        if (solver == null) {
            System.err.println("No solver defined in Choco3FormulaSolver");
            System.exit(0);
        }
        return solver.findSolution();
    }

    // Return a handle to the solver
    public solver.Solver getSolver() {
        return solver;
    }

    /**
     * Creates the model from the problem, variables, constraints, (also from neg examples)
     *
     * @param currentDiagnosisModel
     * @param constraints
     */
    public void createModel(List<Constraint> constraints) {

        createVariables();
        addConstraints(constraints);
        // TODO: Not yet implemented
        createNegativeConstraintsFromExamples();
        // TODO: The test data..
    }

    /**
     * Create the variables from the current tests.diagnosis model
     *
     * @param currentDiagnosisModel
     * @param choco3solver
     */
    void createVariables() {
        for (Variable v : diagnosisModel.getVariables()) {
            // System.out.println("Adding variable:" + v);
            IntegerVariable intv = (IntegerVariable) v;
            IntVar c3var = VariableFactory.bounded(v.getName(), intv.getLowB(), intv.getUppB(), solver);

            // System.out.println("Creating c3var: " + v.getName() + "(" + intv.getLowB() + "-" + intv.getUppB() + ")");

            this.variablesMap.put(intv.getName(), c3var);
        }
        // System.err.println("Added " + currentDiagnosisModel.getVariables().size() + " variables to choco3");
        // System.out.println(this.variablesMap.keySet().size());
    }

    /**
     * Create constraints from negative examples
     *
     * @param currentDiagnosisModel
     * @param choco3solver
     */
    void createNegativeConstraintsFromExamples() {
        // TODO
        // System.err.println("Constraints from negative examples (not yet implemented)s");
    }

    /**
     * Adds the constraints to the solver
     *
     * @param currentDiagnosisModel
     * @param constraint
     * @param c3solver
     */
    void addConstraints(List<choco.kernel.model.constraints.Constraint> constraints) {
        // Create a parser for this problem
        Choco3ConstraintFactory factory = new Choco3ConstraintFactory(this);
        for (choco.kernel.model.constraints.Constraint c : constraints) {
            // System.out.println("Doing constraint: " + c);
            factory.createConstraint(c);
        }
    }

    @Override
    public boolean isEntailed(IDiagnosisEngine<Constraint> diagnosisEngine, Set<Constraint> entailments) {
        throw new NotImplementedException();
    }

    @Override
    public Set<Constraint> calculateEntailments() {
        throw new NotImplementedException();
    }

}
