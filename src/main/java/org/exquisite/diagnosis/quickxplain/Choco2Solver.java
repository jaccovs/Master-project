package org.exquisite.diagnosis.quickxplain;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.ContradictionException;
import org.exquisite.core.ISolver;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.exquisite.core.measurements.MeasurementManager.COUNTER_PROPAGATION;
import static org.exquisite.core.measurements.MeasurementManager.COUNTER_SOLVER_CALLS;
import static org.exquisite.core.measurements.MeasurementManager.incrementCounter;

/**
 * The standard Choco 2 solver implementation.
 *
 * @author Schmitz
 */
public class Choco2Solver implements ISolver<Constraint> {

    // Have to use a shared lock due to Choco's lack of thread safety..
    private static final Lock _mutex = new ReentrantLock(true);
    /**
     * An internal solver
     */
    protected CPSolver solver = new CPSolver();

    @Override
    public void createModel(ConstraintsQuickXPlain<Constraint> qx, List<Constraint> constraints) {

        solver = new CPSolver();

        // -------------------------------------------------------------------
        // Good old synchronized version
        // System.out.println("IsConsistent called " + constraints.size());
        // make a cpmodel to use.
        CPModel cpmodel = new CPModel();
//		long start = System.currentTimeMillis();
        _mutex.lock();
        // System.out.println("Starting with additions: " +
        // Thread.currentThread().getId() + " " + System.currentTimeMillis());
        // copy the variables to the cpmodel.
        copyVariablesToCPModel(cpmodel, qx.currentDiagnosisModel);
        // add the list of constraints
        for (Constraint c : constraints) {
            cpmodel.addConstraint(c);
        }
        // add the negative constraints as well
        for (Example<Constraint> nex : qx.currentDiagnosisModel.getNegativeExamples()) {
            Constraint constraintToAdd = generateConstraintFromNegExample(nex.constraints);
            cpmodel.addConstraint(constraintToAdd);
        }
        // System.out.println("Done with additions: " +
        // Thread.currentThread().getId() + " " + System.currentTimeMillis());
        try {
            // System.out.println("Calling read " +
            // Thread.currentThread().getId() + " " +
            // System.currentTimeMillis());
            solver.read(cpmodel);
            // System.out.println("Read done " + Thread.currentThread().getId()
            // + " " + System.currentTimeMillis());
        } catch (Exception e) {
            System.err.println("Error when reading model "
                    + Thread.currentThread().getId());
            e.printStackTrace();
            System.exit(1);
        }
        _mutex.unlock();

        if (!qx.currentDiagnosisModel.getNotEntailedExamples().isEmpty()) {
            throw new NotImplementedException();
        }
    }


    /**
     * copies the variables
     */
    protected void copyVariablesToCPModel(CPModel targetModel,
                                          DiagnosisModel<Constraint> diagnosisModel) {
        // copy the variables
        for (Variable v : diagnosisModel.getVariables()) {
            targetModel.addVariable(v);
        }
    }

    /**
     * A method that creates a choco constraint from a negative example
     *
     * @param exampleConstraints the constraints
     * @return an negated conjunction of the constraint elements
     */
    Constraint generateConstraintFromNegExample(
            List<Constraint> exampleConstraints) {
        Constraint constraintToAdd = Choco.TRUE;
        for (Constraint exConstraint : exampleConstraints) {
            constraintToAdd = Choco.and(constraintToAdd, exConstraint);
        }
        constraintToAdd = Choco.not(constraintToAdd);
        return constraintToAdd;
    }

    @Override
    public boolean isFeasible() {
        try {
            incrementCounter(COUNTER_PROPAGATION);
            solver.propagate();
        } catch (ContradictionException e) {
            return false;
        }

        incrementCounter(COUNTER_SOLVER_CALLS);
        solver.solve();
        return solver.isFeasible();
    }


    @Override
    public boolean isEntailed(Set<Constraint> entailments) {
        // Add negated entailments
        Constraint entailment = Choco.not(Choco.and(entailments.toArray(new Constraint[entailments.size()])));
        solver.addConstraint(entailment);

        return !isFeasible();
    }

    @Override
    public Set<Constraint> calculateEntailments() {
        throw new NotImplementedException();
    }

}
