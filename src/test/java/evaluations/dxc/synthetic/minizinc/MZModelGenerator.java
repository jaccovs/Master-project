package evaluations.dxc.synthetic.minizinc;

import java.util.LinkedList;
import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;

import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealConstantVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.model.variables.set.SetConstantVariable;
import choco.kernel.model.variables.set.SetVariable;

/**
 * Created by kostya on 8/17/14.
 */
public class MZModelGenerator {

    private final ExquisiteSession sessionData;
    private int abCounter = -1;


    public MZModelGenerator(ExquisiteSession sessionData) {
        this.sessionData = sessionData;
    }

    public List<String> getModel() {
        List<String> model = new LinkedList<>();
        DiagnosisModel dm = this.sessionData.diagnosisModel;
        generateVariables(model, dm.getVariables());
        generateConstraints(model, dm.getCorrectStatements(), Mode.CORRECT);
        generateConstraints(model, dm.getPossiblyFaultyStatements(), Mode.ABNORMAL);

        generateExampleConstraints(model, dm.getNegativeExamples(), Mode.NEGATIVE);

        // positive examples are the same as correct constraints that are just not a part of the mode
        generateExampleConstraints(model, dm.getPositiveExamples(), Mode.CORRECT);

        //include prerequisites
        String pre = "% minizinc encoding \n" +
                "set of int: Const = 0.." + abCounter + ";\n" +
                "array[Const] of var bool : ab;\n" +
                "int : diagCard;\n" +
                "constraint if (diagCard > 0) then sum(i in Const)(bool2int(ab[i])) = diagCard else true endif;";

        model.add(0, pre);
        model.add("output [if fix(ab[i]) then \"ab[\" ++ show(i) ++ \"] \" else \"\" endif | i in Const] ++ [\"\\n\"] ;");
        return model;
    }

    /**
     * Generates constraints for the negative examples.
     *
     * Note that, implementation of the negative examples is done by addition of the following constraints

      constraint (neg_i) -> false

      for each negative example neg_i. That is, each assignment in which neg_i is true
      must not be a solution of the problem.
      TODO check if this solution corresponds to the semantics of the negative examples
      The problem is that for one and the same assignment of abnormal vars there might be
      extensions, i.e. assignments to other vars, in which neg_i is false and extensions in which neg_i is true.
      The current implementation will return an assignment of abnormal vars if there is
      at least one extension in which neg_i is false, i.e. CSP \not\models neg_i.
      However, if the semantics states that CSP \land neg_i must be unsat, then
      the current implementation is *not* correct! In the latter case we must require that
      neg_i is false in *all* possible extensions.

     * @param model MiniZinc model
     * @param examples a set of example
     * @param mode type of constraints that must be generated for examples
     */
    private void generateExampleConstraints(List<String> model, List<Example> examples, Mode mode) {
        for (Example example : examples) {
            generateConstraints(model, example.constraints, mode);
        }
    }

    enum Mode {CORRECT, ABNORMAL, NEGATIVE}

    private void generateConstraints(List<String> model, List<Constraint> constraints, Mode mode) {
        for (Constraint constraint : constraints) {
            String connective = getMiniZincConstraint(constraint.getConstraintType());
            StringBuilder sb = new StringBuilder();
            sb.append("constraint ");
            switch (mode){
                case ABNORMAL:
                    sb.append("ab[").append(++abCounter).append("] \\/ (");
                    break;
                case NEGATIVE:
                    sb.append("(");
                    break;
            }

            for (Variable variable : constraint.getVariables()) {
                sb.append(getMiniZincVariable(variable));
                sb.append(connective);
            }
            removeLastConnective(connective, sb);
            switch (mode){
                case ABNORMAL:
                    sb.append(")");
                    break;
                case NEGATIVE:
                    sb.append(") -> false");
                    break;
            }
            sb.append(";");
            model.add(sb.toString());
        }
    }

    private void removeLastConnective(String connective, StringBuilder sb) {
        sb.delete(sb.length()-(connective.length()), sb.length());
    }

    private String getMiniZincVariable(Variable variable) {
        switch(variable.getVariableType()){
            case INTEGER_EXPRESSION:{
                IntegerExpressionVariable var = (IntegerExpressionVariable) variable;
                return "(" + generateIntegerExpression(var) + ")";
            }
            case REAL_EXPRESSION:{
                throw new RuntimeException("Real expressions are not supported!");
            }
            case SET_EXPRESSION:{
                throw new RuntimeException("Set expressions are not supported!");
            }
            case MULTIPLE_VARIABLES:{
                throw new IllegalArgumentException("Cannot handle MULTIPLE_VARIABLES variable type!");
            }
            default:{
                return variable.getName();
            }
        }
    }

    private String generateIntegerExpression(IntegerExpressionVariable var) {
        Operator op = var.getOperator();
        String operator = getMiniZincOperator(op);
        StringBuilder sb = new StringBuilder();
        if (isMiniZincPredicate(op)){
            sb.append(operator).append("(");
            for (Variable variable : var.getVariables()) {
                sb.append(variable.getName()).append(",");
            }
            removeLastConnective(",", sb);
            sb.append(")");
            return sb.toString();
        }

        for (Variable variable : var.getVariables()) {
            sb.append(getMiniZincVariable(variable));
            sb.append(operator);
        }
        removeLastConnective(operator, sb);
        return sb.toString();
    }

    private String getMiniZincOperator(Operator operator) {
        switch (operator) {
            case PLUS:
                return "+";
            case SUM:
                return "+";
            case MINUS:
                return "-";
            case ABS:
                return "abs";
            case MULT:
                return "*";
            case DIV:
                return "/";
            default:
                throw new IllegalArgumentException("Unknown operator type " + operator);
        }
    }


    private boolean isMiniZincPredicate(Operator operator) {
        switch (operator) {
            case PLUS:
                return false;
            case SUM:
                return false;
            case MINUS:
                return false;
            case ABS:
                return true;
            case MULT:
                return false;
            case DIV:
                return false;
            default:
                throw new IllegalArgumentException("Unknown operator type " + operator);
        }
    }

    private String getMiniZincConstraint(ConstraintType type) {
        switch (type){
            case EQ:
               return "=";
            case NEQ:
                return "!=";
            case GT:
                return ">";
            case GEQ:
                return ">=";
            case LT:
                return "<";
            case LEQ:
                return "<=";
            case AND:
                return "/\\";
            case OR:
                return "\\/";
            case IMPLIES:
                return "->";
            default:
                throw new IllegalArgumentException("Constraint " + type.getName()+ " is not implemented!");
        }


    }

    private void generateVariables(List<String> model, List<Variable> dm) {
        for (Variable variable : dm) {
            switch (variable.getVariableType()) {
                case INTEGER: {
                    IntegerVariable var = (IntegerVariable) variable;
                    int[] values = var.getValues();
                    if (values == null || values.length==2)
                        model.add("var " + var.getLowB() + ".." + var.getUppB() + ": " + variable.getName() + ";");
                    else
                        model.add("var " + generateDomain(values) + ": " + variable.getName() + ";");
                    break;
                }
                case CONSTANT_DOUBLE: {
                    RealConstantVariable var = (RealConstantVariable) variable;
                    model.add("float: " + var.getName() + "=" + var.getValue() + ";");
                    break;
                }
                case CONSTANT_INTEGER: {
                    IntegerConstantVariable var = (IntegerConstantVariable) variable;
                    model.add("int: " + var.getName() + "=" + var.getValue() + ";");
                    break;
                }
                case REAL: {
                    RealVariable var = (RealVariable) variable;
                    model.add("var " + var.getLowB() + ".." + var.getUppB() + ": " + variable.getName() + ";");
                    break;
                }
                case SET:{
                    SetVariable var = (SetVariable)variable;
                    model.add("var " + generateDomain(var.getValues()) + ": " + variable.getName() + ";");
                    break;
                }
                case CONSTANT_SET:{
                    SetConstantVariable var = (SetConstantVariable)variable;
                    model.add("set of int:"  + variable.getName() + " = " + generateDomain(var.getValues()) + ";");
                    break;
                }
                default:
                    throw new RuntimeException("Unknown variable type " + variable.getVariableType());
            }

        }
    }

    private String generateDomain(int[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int value : values) {
            sb.append(value).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("}");
        return sb.toString();
    }

    public int getAbnormalsCount() {
        return this.abCounter;
    }

    public MZDiagnosisEngine.SearchType getSearchType() {
        return MZDiagnosisEngine.SearchType.AllMinCardinality;
    }
}
