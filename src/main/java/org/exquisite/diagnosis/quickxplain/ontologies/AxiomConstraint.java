package org.exquisite.diagnosis.quickxplain.ontologies;

import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.ExpressionManager;
import choco.kernel.model.variables.Variable;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * A dummy constraint for our system that uses the Choco 2 Constraint interface for diagnosable components.
 * Stores an axiom that can be used by OWL API solvers.
 *
 * @author Schmitz
 */
public class AxiomConstraint implements Constraint {

    OWLAxiom axiom;

    public AxiomConstraint(OWLAxiom axiom) {
        this.axiom = axiom;
    }

    public OWLAxiom getAxiom() {
        return axiom;
    }

    @Override
    public String toString() {
        return axiom.toString();
    }

    @Override
    public String pretty() {
        return axiom.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (AxiomConstraint.class.isInstance(o)) {
            AxiomConstraint that = (AxiomConstraint) o;
            return axiom != null && axiom.equals(that.axiom);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return axiom.hashCode();
    }

    @Override
    public long getIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public DisposableIterator<Variable> getVariableIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Variable getVariable(int i) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Variable[] getVariables() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getNbVars() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Variable[] extractVariables() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void replaceBy(Variable outVar, Variable inVar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void findManager(Properties properties) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addOption(String option) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addOptions(String options) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addOptions(String[] options) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addOptions(List<String> options) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addOptions(Set<String> options) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String> getOptions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean containsOption(String option) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ConstraintType getConstraintType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return axiom.toString();
    }

    @Override
    public int[] getFavoriteDomains() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExpressionManager getExpressionManager() {
        // TODO Auto-generated method stub
        return null;
    }

}
