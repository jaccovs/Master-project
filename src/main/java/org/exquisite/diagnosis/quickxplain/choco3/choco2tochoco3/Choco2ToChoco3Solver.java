package org.exquisite.diagnosis.quickxplain.choco3.choco2tochoco3;

import choco.Choco;
import choco.kernel.model.IVariableArray;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.MetaConstraint;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.core.ISolver;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import solver.exception.ContradictionException;
import solver.search.strategy.IntStrategyFactory;
import solver.variables.BoolVar;
import solver.variables.IntVar;
import solver.variables.VariableFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Choco3 solver that translates the choco2 constraints directly to choco3 constraints
 *
 * @author Thomas
 */
public class Choco2ToChoco3Solver implements ISolver<Constraint> {

    public static boolean USE_ACTIVITY_STRATEGY = false;
    public static boolean USE_MINDOM_STRATEGY = false;
    // counter for temp variable names
    static int tmpID = 0;
    // store the model
    public DiagnosisModel<Constraint> diagnosisModel;
    // the current example
    public Example currentExample;

    // The variable map
    public Map<Variable, IntVar> theVariables = new HashMap<>();
    // the internal solver
    protected solver.Solver solver;
    // Min and max for temp variables
    int min = -1000000;
    int max = 1000000;

    // create the model
    @Override
    public void createModel(QuickXPlain<Constraint> qx, List<Constraint> constraints) {
        this.diagnosisModel = qx.currentDiagnosisModel;
        this.currentExample = qx.currentExample;
        this.solver = new solver.Solver();
        this.createVariables();
        try {
            this.postConstraints(constraints);
        } catch (Choco2ToChoco3Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // For c432 problem only!
        if (USE_ACTIVITY_STRATEGY) {
            solver.set(IntStrategyFactory.activity(theVariables.values().toArray(new IntVar[theVariables.size()]), 0));
        }
        // For spreadsheet new/Hospital_Payment only!
        if (USE_MINDOM_STRATEGY) {
            solver.set(IntStrategyFactory.minDom_UB(theVariables.values().toArray(new IntVar[theVariables.size()])));
        }

        if (!qx.currentDiagnosisModel.getNotEntailedExamples().isEmpty()) {
            throw new NotImplementedException();
        }
    }

    // check feasibility
    @Override
    public boolean isFeasible(IDiagnosisEngine<Constraint> diagnosisEngine) {
        if (diagnosisEngine != null) {
            // System.out.println("Propagating..");
            diagnosisEngine.incrementPropagationCount();
        }

        try {
            this.solver.propagate();
        } catch (ContradictionException e) {
            return false;
        }

        if (diagnosisEngine != null) {
            diagnosisEngine.incrementSolverCalls();
        }

        return this.solver.findSolution();
    }

    @Override
    public boolean isEntailed(IDiagnosisEngine<Constraint> diagnosisEngine, Set<Constraint> entailments) {
        // Add negated entailments
        Constraint entailment = Choco.not(Choco.and(entailments.toArray(new Constraint[entailments.size()])));
        try {
            postConstraint(entailment);
        } catch (Choco2ToChoco3Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return !isFeasible(diagnosisEngine);
    }

//	@Override
//	public Solver getSolver() {
//		return solver;
//	}

    /**
     * Builds Choco3 constraints of the given Choco2 constraints and posts them to the solver.
     *
     * @param constraints
     * @throws Choco2ToChoco3Exception
     */
    private void postConstraints(List<Constraint> constraints) throws Choco2ToChoco3Exception {
        for (Constraint c : constraints) {
            postConstraint(c);
        }
    }

    /**
     * Builds a Choco3 constraint of the given Choco2 constraint and posts it to the solver.
     *
     * @param constraint
     * @throws Choco2ToChoco3Exception
     */
    private void postConstraint(Constraint constraint) throws Choco2ToChoco3Exception {
        solver.constraints.Constraint c3Constraint = buildConstraint(constraint);
        solver.post(c3Constraint);
    }

    /**
     * Builds and returns a Choco3 constraint of the given Choco2 constraint.
     *
     * @param constraint
     * @return
     * @throws Choco2ToChoco3Exception
     */
    public solver.constraints.Constraint buildConstraint(Constraint constraint) throws Choco2ToChoco3Exception {
        solver.constraints.Constraint c3Constraint;
        switch (constraint.getConstraintType()) {
            case EQ:
                checkForNbVars(constraint, 2);
                c3Constraint = buildArithm(constraint.getVariable(0), "=", constraint.getVariable(1));
                break;
            case NEQ:
                checkForNbVars(constraint, 2);
                c3Constraint = buildArithm(constraint.getVariable(0), "!=", constraint.getVariable(1));
                break;
            case LT:
                checkForNbVars(constraint, 2);
                c3Constraint = buildArithm(constraint.getVariable(0), "<", constraint.getVariable(1));
                break;
            case LEQ:
                checkForNbVars(constraint, 2);
                c3Constraint = buildArithm(constraint.getVariable(0), "<=", constraint.getVariable(1));
                break;
            case GT:
                checkForNbVars(constraint, 2);
                c3Constraint = buildArithm(constraint.getVariable(0), ">", constraint.getVariable(1));
                break;
            case GEQ:
                checkForNbVars(constraint, 2);
                c3Constraint = buildArithm(constraint.getVariable(0), ">=", constraint.getVariable(1));
                break;
            case AND:
                if (constraint instanceof MetaConstraint<?>) {
                    solver.constraints.Constraint[] c3andcons = buildConstraintsOfMetaConstraint(
                            (MetaConstraint<?>) constraint);
                    c3Constraint = LogicalConstraintFactory.and(c3andcons);
                } else { //if (constraint instanceof ComponentConstraint) {
                    BoolVar[] c3vars = getBoolVars(constraint.getVariables());
                    c3Constraint = LogicalConstraintFactory.and(c3vars);
                }
                break;
            case OR:
                if (constraint instanceof MetaConstraint<?>) {
                    solver.constraints.Constraint[] c3orcons = buildConstraintsOfMetaConstraint(
                            (MetaConstraint<?>) constraint);
                    c3Constraint = LogicalConstraintFactory.or(c3orcons);
                } else { //if (constraint instanceof ComponentConstraint) {
                    BoolVar[] c3vars = getBoolVars(constraint.getVariables());
                    c3Constraint = LogicalConstraintFactory.or(c3vars);
                }
                break;
            case NOR:
                if (constraint instanceof MetaConstraint<?>) {
                    solver.constraints.Constraint[] c3orcons = buildConstraintsOfMetaConstraint(
                            (MetaConstraint<?>) constraint);
                    c3Constraint = LogicalConstraintFactory.not(LogicalConstraintFactory.or(c3orcons));
                } else { //if (constraint instanceof ComponentConstraint) {
                    BoolVar[] c3vars = getBoolVars(constraint.getVariables());
                    c3Constraint = LogicalConstraintFactory.not(LogicalConstraintFactory.or(c3vars));
                }
                break;
            case XOR:
                if (constraint instanceof MetaConstraint<?>) {
                    throw new OperatorTypeNotKnownException("XOR as MetaConstraint");
//				solver.constraints.Constraint[] c3orcons = buildConstraintsOfMetaConstraint((MetaConstraint<?>)constraint);
//				c3Constraint = LogicalConstraintFactory.or(c3orcons);
                } else { //if (constraint instanceof ComponentConstraint) {
                    BoolVar[] c3vars = getBoolVars(constraint.getVariables());
                    c3Constraint = IntConstraintFactory.sum(c3vars, VariableFactory.fixed(1, solver));
                }
                break;
            case NOT:
                solver.constraints.Constraint[] c3notcons = buildConstraintsOfMetaConstraint(
                        (MetaConstraint<?>) constraint);
                if (c3notcons.length != 1) {
                    throw new WrongConstraintCountException(constraint.getName() + " constraints: " + c3notcons.length);
                }
                c3Constraint = LogicalConstraintFactory.not(c3notcons[0]);
                break;
            case ALLDIFFERENT:
                IntVar[] vars = new IntVar[constraint.getNbVars()];
                for (int i = 0; i < vars.length; i++) {
                    vars[i] = buildVar(constraint.getVariable(i));
                }
                c3Constraint = IntConstraintFactory.alldifferent(vars);
                break;
            case REIFIEDAND:
                if (constraint.getNbVars() < 3) {
                    throw new WrongVariableCountException(constraint.pretty() + " Vars: " + constraint.getNbVars());
                }
                BoolVar c3var = (BoolVar) buildVar(constraint.getVariable(0));
                Variable[] c2Vars = new Variable[constraint.getNbVars() - 1];
                for (int i = 0; i < c2Vars.length; i++) {
                    c2Vars[i] = constraint.getVariable(i + 1);
                }
                BoolVar[] c3vars = getBoolVars(c2Vars);

                c3Constraint = LogicalConstraintFactory.reification(c3var, LogicalConstraintFactory.and(c3vars));
                break;
            case REIFIEDNAND:
                if (constraint.getNbVars() < 3) {
                    throw new WrongVariableCountException(constraint.pretty() + " Vars: " + constraint.getNbVars());
                }
                c3var = (BoolVar) buildVar(constraint.getVariable(0));
                c2Vars = new Variable[constraint.getNbVars() - 1];
                for (int i = 0; i < c2Vars.length; i++) {
                    c2Vars[i] = constraint.getVariable(i + 1);
                }
                c3vars = getBoolVars(c2Vars);

                c3Constraint = LogicalConstraintFactory
                        .reification(c3var, LogicalConstraintFactory.not(LogicalConstraintFactory.and(c3vars)));
                break;
            case REIFIEDOR:
                if (constraint.getNbVars() < 3) {
                    throw new WrongVariableCountException(constraint.pretty() + " Vars: " + constraint.getNbVars());
                }
                c3var = (BoolVar) buildVar(constraint.getVariable(0));
                c2Vars = new Variable[constraint.getNbVars() - 1];
                for (int i = 0; i < c2Vars.length; i++) {
                    c2Vars[i] = constraint.getVariable(i + 1);
                }
                c3vars = getBoolVars(c2Vars);

                c3Constraint = LogicalConstraintFactory.reification(c3var, LogicalConstraintFactory.or(c3vars));
                break;
            case REIFIEDNOR:
                if (constraint.getNbVars() < 3) {
                    throw new WrongVariableCountException(constraint.pretty() + " Vars: " + constraint.getNbVars());
                }
                c3var = (BoolVar) buildVar(constraint.getVariable(0));
                c2Vars = new Variable[constraint.getNbVars() - 1];
                for (int i = 0; i < c2Vars.length; i++) {
                    c2Vars[i] = constraint.getVariable(i + 1);
                }
                c3vars = getBoolVars(c2Vars);

                c3Constraint = LogicalConstraintFactory
                        .reification(c3var, LogicalConstraintFactory.not(LogicalConstraintFactory.or(c3vars)));
                break;
            case REIFIEDXOR:
                if (constraint.getNbVars() < 3) {
                    throw new WrongVariableCountException(constraint.pretty() + " Vars: " + constraint.getNbVars());
                }
                c3var = (BoolVar) buildVar(constraint.getVariable(0));
                c2Vars = new Variable[constraint.getNbVars() - 1];
                for (int i = 0; i < c2Vars.length; i++) {
                    c2Vars[i] = constraint.getVariable(i + 1);
                }
                c3vars = getBoolVars(c2Vars);

                c3Constraint = LogicalConstraintFactory
                        .reification(c3var, IntConstraintFactory.sum(c3vars, VariableFactory.fixed(1, solver)));
                break;
            default:
                throw new ConstraintTypeNotKnownException(constraint.getConstraintType().name);
        }
        return c3Constraint;
    }

    private BoolVar[] getBoolVars(Variable[] vars) throws Choco2ToChoco3Exception {
        BoolVar[] c3vars = new BoolVar[vars.length];
        for (int i = 0; i < vars.length; i++) {
            c3vars[i] = (BoolVar) buildVar(vars[i]);
        }
        return c3vars;
    }

    /**
     * Builds and returns Choco3 constraints of the subconstraints of the given Choco2 meta constraint (like and, or).
     *
     * @param constraint
     * @return
     * @throws Choco2ToChoco3Exception
     */
    private solver.constraints.Constraint[] buildConstraintsOfMetaConstraint(MetaConstraint<?> constraint)
            throws Choco2ToChoco3Exception {
        Constraint[] cs = constraint.getConstraints();
        solver.constraints.Constraint[] c3cons = new solver.constraints.Constraint[cs.length];
        for (int i = 0; i < cs.length; i++) {
            c3cons[i] = buildConstraint(cs[i]);
        }
        return c3cons;
    }

    /**
     * Checks the given variable array for the given number of variables and otherwise throws an exception.
     *
     * @param va
     * @param nbVars
     * @throws WrongVariableCountException
     */
    private void checkForNbVars(IVariableArray va, int nbVars) throws WrongVariableCountException {
        if (va.getNbVars() != nbVars) {
            throw new WrongVariableCountException(va.pretty() + " Vars: " + va.getNbVars());
        }
    }

    /**
     * Builds an arithm Choco3 constraint with the given variables and operator.
     *
     * @param var1
     * @param op
     * @param var2
     * @return
     * @throws Choco2ToChoco3Exception
     */
    private solver.constraints.Constraint buildArithm(Variable var1, String op, Variable var2)
            throws Choco2ToChoco3Exception {
        solver.variables.IntVar c3v1 = buildVar(var1);

        solver.variables.IntVar c3v2 = buildVar(var2);

        solver.constraints.Constraint c3c = IntConstraintFactory.arithm(c3v1, op, c3v2);

        return c3c;
    }

    /**
     * Builds a new temp variable, if the given Choco2 variable is an integer expression or a constant.
     * Otherwise returns the Choco3 variable that is mapped to the given Choco2 variable.
     *
     * @param var
     * @return
     * @throws Choco2ToChoco3Exception
     */
    private IntVar buildVar(Variable var) throws Choco2ToChoco3Exception {
        switch (var.getVariableType()) {
            case INTEGER_EXPRESSION:
                IntegerExpressionVariable expVar = (IntegerExpressionVariable) var;
                Operator op = expVar.getOperator();

                IntVar c3v1 = null, c3v2 = null;
                // Check for operators that do not need exactly 2 variables
                if (op != Operator.SUM && op != Operator.ABS) {
                    checkForNbVars(expVar, 2);
//				if (expVar.getNbVars() != 2) {
//					throw new VariableTypeNotKnownException(var.getVariableType().name + " Vars: " + expVar.getNbVars());
//				}

                    c3v1 = buildVar(expVar.getVariable(0));
                    c3v2 = buildVar(expVar.getVariable(1));
                }

                IntVar c3resultVar = createIntVar();

                switch (op) {
                    case PLUS:
                        solver.post(IntConstraintFactory.sum(new IntVar[]{c3v1, c3v2}, c3resultVar));
                        break;
                    case MINUS:
                        solver.post(IntConstraintFactory.sum(new IntVar[]{c3resultVar, c3v2}, c3v1));
                        break;
                    case MULT:
                        solver.post(IntConstraintFactory.times(c3v1, c3v2, c3resultVar));
                        break;
                    case DIV:
                        solver.post(IntConstraintFactory.eucl_div(c3v1, c3v2, c3resultVar));
                        break;
                    case MIN:
                        solver.post(IntConstraintFactory.minimum(c3resultVar, c3v1, c3v2));
                        break;
                    case MAX:
                        solver.post(IntConstraintFactory.maximum(c3resultVar, c3v1, c3v2));
                        break;
                    case ABS:
                        checkForNbVars(expVar, 1);
                        c3v1 = buildVar(expVar.getVariable(0));
                        solver.post(IntConstraintFactory.absolute(c3resultVar, c3v1));
                        break;
                    case SUM:
                        IntVar[] c3vars = new IntVar[expVar.getNbVars()];
                        for (int i = 0; i < c3vars.length; i++) {
                            c3vars[i] = buildVar(expVar.getVariable(i));
                        }
                        solver.post(IntConstraintFactory.sum(c3vars, c3resultVar));
                        break;
                    case IFTHENELSE:
                        Constraint[] cs = var.getConstraints();
                        if (cs.length != 1) {
                            throw new OperatorTypeNotKnownException(
                                    expVar.getOperator().name + " Constraints: " + cs.length);
                        }
                        solver.constraints.Constraint c3then = IntConstraintFactory.arithm(c3resultVar, "=", c3v1);
                        solver.constraints.Constraint c3else = IntConstraintFactory.arithm(c3resultVar, "=", c3v2);
                        solver.constraints.Constraint c3if = buildConstraint(cs[0]);

                        solver.post(LogicalConstraintFactory.ifThenElse(c3if, c3then, c3else));
                        break;
                    default:
                        throw new OperatorTypeNotKnownException(expVar.getOperator().name);
                }

//			solver.post(IntConstraintFactory.arithm(c3expVar, "=", c3v1, "-", c3v2));
                return c3resultVar;

            case INTEGER:
                return theVariables.get(var);
            case CONSTANT_INTEGER:
                solver.variables.IntVar intvar = VariableFactory
                        .fixed(((IntegerConstantVariable) var).getValue(), solver);
                return intvar;
            default:
                throw new VariableTypeNotKnownException(var.getVariableType().name);
        }
    }

    /**
     * Creates a new temp variable with the standard bounds.
     *
     * @return
     */
    private IntVar createIntVar() {
        return VariableFactory.bounded("tmp" + tmpID++, min, max, solver);
    }

    /**
     * Creates all variables of the tests.diagnosis model
     */
    private void createVariables() {
        theVariables = new HashMap<Variable, IntVar>();

        // Create the variables according to the model
        List<Variable> variables = diagnosisModel.getVariables();
        for (Variable v : variables) {
            IntegerVariable integervar = (IntegerVariable) v;
            // Create a solver variable
            solver.variables.IntVar intvar = VariableFactory
                    .bounded(v.getName(), integervar.getLowB(), integervar.getUppB(), solver);
            theVariables.put(v, intvar);
        }
    }

    @Override
    public Set<Constraint> calculateEntailments() {
        throw new NotImplementedException();
    }

}