package org.exquisite.protege.model;

import org.exquisite.core.model.DiagnosisModel;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.Set;
import java.util.TreeSet;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestcaseType;

/**
 * @author wolfi
 */
public class Testcases {

    private OntologyDiagnosisSearcher ods;

    private DiagnosisModel<OWLLogicalAxiom> diagnosisModel;

    private Set<OWLLogicalAxiom> originalEntailedTestcases;

    private Set<OWLLogicalAxiom> originalNonEntailedTestcases;

    private Set<OWLLogicalAxiom> acquiredEntailedTestcases;

    private Set<OWLLogicalAxiom> acquiredNonEntailedTestcases;

    Testcases(OntologyDiagnosisSearcher ods) {
        this.ods = ods;
        this.diagnosisModel = ods.getDiagnosisEngineFactory().getDiagnosisEngine().getSolver().getDiagnosisModel();
        this.originalEntailedTestcases = new TreeSet<>(diagnosisModel.getEntailedExamples());
        this.originalNonEntailedTestcases = new TreeSet<>(diagnosisModel.getNotEntailedExamples());
        this.acquiredEntailedTestcases = new TreeSet<>();
        this.acquiredNonEntailedTestcases = new TreeSet<>();
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

    void addTestcase(Set<OWLLogicalAxiom> testcaseAxioms, TestcaseType type) {
        if (type==TestcaseType.ACQUIRED_ENTAILED_TC || type==TestcaseType.ORIGINAL_ENTAILED_TC) {
            this.diagnosisModel.getEntailedExamples().addAll(testcaseAxioms);
            if (type==TestcaseType.ACQUIRED_ENTAILED_TC)
                this.acquiredEntailedTestcases.addAll(testcaseAxioms);
            else
                this.originalEntailedTestcases.addAll(testcaseAxioms);
        } else {
            this.diagnosisModel.getNotEntailedExamples().addAll(testcaseAxioms);
            if (type==TestcaseType.ACQUIRED_NON_ENTAILED_TC)
                this.acquiredNonEntailedTestcases.addAll(testcaseAxioms);
            else
                this.originalNonEntailedTestcases.addAll(testcaseAxioms);
        }
    }

    void removeTestcase(Set<OWLLogicalAxiom> testcaseAxioms, TestcaseType type) {
        if (type==TestcaseType.ACQUIRED_ENTAILED_TC || type==TestcaseType.ORIGINAL_ENTAILED_TC) {
            this.diagnosisModel.getEntailedExamples().removeAll(testcaseAxioms);
            if (type==TestcaseType.ACQUIRED_ENTAILED_TC)
                this.acquiredEntailedTestcases.removeAll(testcaseAxioms);
            else
                this.originalEntailedTestcases.removeAll(testcaseAxioms);
        } else {
            this.diagnosisModel.getNotEntailedExamples().removeAll(testcaseAxioms);
            if (type==TestcaseType.ACQUIRED_NON_ENTAILED_TC)
                this.acquiredNonEntailedTestcases.removeAll(testcaseAxioms);
            else
                this.originalNonEntailedTestcases.removeAll(testcaseAxioms);
        }
    }

    boolean areTestcasesEmpty() {
        return this.acquiredEntailedTestcases.isEmpty() && this.acquiredNonEntailedTestcases.isEmpty();
    }

    void reset() {
        this.diagnosisModel.getEntailedExamples().removeAll(this.acquiredEntailedTestcases);
        this.diagnosisModel.getNotEntailedExamples().removeAll(this.acquiredNonEntailedTestcases);

        this.originalEntailedTestcases = new TreeSet<>(diagnosisModel.getEntailedExamples());
        this.originalNonEntailedTestcases = new TreeSet<>(diagnosisModel.getNotEntailedExamples());

        this.acquiredEntailedTestcases.clear();
        this.acquiredNonEntailedTestcases.clear();
    }

}
