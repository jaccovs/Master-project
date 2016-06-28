package org.exquisite.protege.ui.list;

import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestCaseType;

public class TcaeListItem extends AxiomListItem {

    private Set<OWLLogicalAxiom> testcase;

    private TestCaseType type;

    public TcaeListItem(OWLLogicalAxiom axiom, TestCaseType type, OWLOntology ontology) {
        super(axiom, ontology);
        this.type = type;
        this.testcase = new HashSet<>();
        this.testcase.add(this.axiom);
    }

    public Set<OWLLogicalAxiom> getTestcase() {
        return testcase;
    }

    public TestCaseType getType() {
        return type;
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

}
