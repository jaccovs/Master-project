package org.exquisite.protege.model;

import org.exquisite.protege.Debugger;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

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

}
