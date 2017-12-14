package org.exquisite.protege.model;

import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.Debugger;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.exquisite.protege.Debugger.TestcaseType;

/**
 * A model of the test cases that differs between the original entailed and non-entailed test cases and
 * new (acquired) entailed and non-entailed test cases that are identified during an interactive debugging session.
 *
 * The the diagnosis model behind collects them alltogebher this model differs these two kinds of test cases
 * for the views AcquiredTestcasesView and OriginalTestcasesView.
 *
 * @see org.exquisite.protege.ui.view.AcquiredTestcasesView
 * @see org.exquisite.protege.ui.view.OriginalTestcasesView
 */
public class TestcasesModel {

    private Debugger debugger;

    private Set<OWLLogicalAxiom> originalEntailedTestcases;

    private Set<OWLLogicalAxiom> originalNonEntailedTestcases;

    private Set<OWLLogicalAxiom> acquiredEntailedTestcases;

    private Set<OWLLogicalAxiom> acquiredNonEntailedTestcases;

    public TestcasesModel(Debugger debugger) {
        this.debugger = debugger;
        this.originalEntailedTestcases = new TreeSet<>();
        this.originalNonEntailedTestcases = new TreeSet<>();
        this.acquiredEntailedTestcases = new TreeSet<>();
        this.acquiredNonEntailedTestcases = new TreeSet<>();
    }

    public void setOriginalEntailedTestcases(Set<OWLLogicalAxiom> originalEntailedTestcases) {
        this.originalEntailedTestcases = originalEntailedTestcases;
    }

    public void setOriginalNonEntailedTestcases(Set<OWLLogicalAxiom> originalNonEntailedTestcases) {
        this.originalNonEntailedTestcases = originalNonEntailedTestcases;
    }

    public Set<OWLLogicalAxiom> getOriginalEntailedTestcases() {
        return originalEntailedTestcases;
    }

    public Set<OWLLogicalAxiom> getOriginalNonEntailedTestcases() {
        return originalNonEntailedTestcases;
    }

    public Set<OWLLogicalAxiom> getAcquiredEntailedTestcases() {
        return acquiredEntailedTestcases;
    }

    public Set<OWLLogicalAxiom> getAcquiredNonEntailedTestcases() {
        return acquiredNonEntailedTestcases;
    }

    public void addTestcase(Set<OWLLogicalAxiom> testcaseAxioms, TestcaseType type) {
        if (type==TestcaseType.ACQUIRED_ENTAILED_TC || type==TestcaseType.ORIGINAL_ENTAILED_TC) {
            this.debugger.getDiagnosisModel().getEntailedExamples().addAll(testcaseAxioms);
            if (type==TestcaseType.ACQUIRED_ENTAILED_TC)
                this.acquiredEntailedTestcases.addAll(testcaseAxioms);
            else
                this.originalEntailedTestcases.addAll(testcaseAxioms);
        } else {
            this.debugger.getDiagnosisModel().getNotEntailedExamples().addAll(testcaseAxioms);
            if (type==TestcaseType.ACQUIRED_NON_ENTAILED_TC)
                this.acquiredNonEntailedTestcases.addAll(testcaseAxioms);
            else
                this.originalNonEntailedTestcases.addAll(testcaseAxioms);
        }
    }

    public void removeTestcase(Set<OWLLogicalAxiom> testcaseAxioms, TestcaseType type) {

        if (type==TestcaseType.ACQUIRED_ENTAILED_TC || type==TestcaseType.ORIGINAL_ENTAILED_TC) {
            this.debugger.getDiagnosisModel().getEntailedExamples().removeAll(testcaseAxioms);
            if (type==TestcaseType.ACQUIRED_ENTAILED_TC)
                this.acquiredEntailedTestcases.removeAll(testcaseAxioms);
            else
                this.originalEntailedTestcases.removeAll(testcaseAxioms);
        } else {
            this.debugger.getDiagnosisModel().getNotEntailedExamples().removeAll(testcaseAxioms);
            if (type==TestcaseType.ACQUIRED_NON_ENTAILED_TC)
                this.acquiredNonEntailedTestcases.removeAll(testcaseAxioms);
            else
                this.originalNonEntailedTestcases.removeAll(testcaseAxioms);
        }
    }

    public boolean areTestcasesEmpty() {
        return this.acquiredEntailedTestcases.isEmpty() && this.acquiredNonEntailedTestcases.isEmpty();
    }

    public void reset() {
        debugger.getDiagnosisModel().getEntailedExamples().removeAll(this.acquiredEntailedTestcases);
        debugger.getDiagnosisModel().getNotEntailedExamples().removeAll(this.acquiredNonEntailedTestcases);

        this.originalEntailedTestcases = new TreeSet<>(debugger.getDiagnosisModel().getEntailedExamples());
        this.originalNonEntailedTestcases = new TreeSet<>(debugger.getDiagnosisModel().getNotEntailedExamples());

        this.acquiredEntailedTestcases.clear();
        this.acquiredNonEntailedTestcases.clear();
    }

    /**
     * A new test case is only valid if it does not already occur as an entailed or non entailed axiom or in the background.
     * If the same axiom is both entailed and non entailed or already defined as an axiom in the background then we
     * would generate an inconsistency (if it is both entailed and non entailed or if it occurs in the background).
     *
     * @param axioms The axiom representing the to be added axiom.
     * @param type
     * @return <code>true</code> if the axiom is ok for the diagnosis model, <code>false</code> otherwise.
     */
    public boolean isValidNewTestCase(Set<OWLLogicalAxiom> axioms, TestcaseType type) {
        if (axioms.size() == 1) {
            final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = this.debugger.getDiagnosisModel();
            final OWLLogicalAxiom axiom = axioms.iterator().next();
            final List<OWLLogicalAxiom> entailedExamples = diagnosisModel.getEntailedExamples();
            final List<OWLLogicalAxiom> notEntailedExamples = diagnosisModel.getNotEntailedExamples();
            final List<OWLLogicalAxiom> correctFormulas = diagnosisModel.getCorrectFormulas();
            final boolean isAlreadyDefined = entailedExamples.contains(axiom) || notEntailedExamples.contains(axiom) || correctFormulas.contains(axiom);
            return !isAlreadyDefined && isConsistent(axioms, type);
        } else {
            throw new UnsupportedOperationException("An unexpected case has occurred. The validity check for test cases expects only one axiom to test.");
        }
    }

    private boolean isConsistent(Set<OWLLogicalAxiom> axioms, TestcaseType type) {
        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = debugger.getDiagnosisEngineFactory().getDiagnosisEngine();

        if (diagnosisEngine == null) {
            // no diagnosis session has been started before, therefore no diagnosis engine has been created yet
            debugger.getDiagnosisEngineFactory().reset();
            diagnosisEngine = debugger.getDiagnosisEngineFactory().getDiagnosisEngine();
        }
        // temporarily add the axioms to the testcases to be check the consistency
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = this.debugger.getDiagnosisModel();
        final OWLLogicalAxiom axiom = axioms.iterator().next();
        if (!diagnosisModel.getPossiblyFaultyFormulas().contains(axiom)) {
            switch (type) {
                case ORIGINAL_ENTAILED_TC:
                    return isConsistent(axioms, debugger.getDiagnosisModel().getEntailedExamples());
                case ORIGINAL_NON_ENTAILED_TC:
                    // we want
                    return isConsistent(axioms, debugger.getDiagnosisModel().getNotEntailedExamples());
                default:
                    throw new UnsupportedOperationException("Consistency check for testcases of type " + type + " is not supported.");
            }
        } else return true; // possibly faulty axioms added as test case shall be always possible
    }

    private boolean isConsistent(Set<OWLLogicalAxiom> axioms, List<OWLLogicalAxiom> testcases) {
        final OWLLogicalAxiom axiom = axioms.iterator().next();
        testcases.add(axiom);
        boolean isConsistent = debugger.getDiagnosisEngineFactory().getDiagnosisEngine().getSolver().isConsistent(axioms);
        testcases.remove(axiom);
        return isConsistent;
    }
}
