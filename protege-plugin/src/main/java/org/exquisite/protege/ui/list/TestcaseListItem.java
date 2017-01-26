package org.exquisite.protege.ui.list;

import org.exquisite.protege.Debugger;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;
import java.util.TreeSet;

import static org.exquisite.protege.Debugger.TestcaseType;

class TestcaseListItem extends AssertedOrInferredAxiomListItem {

    private Set<OWLLogicalAxiom> testCaseAxioms;

    private TestcaseType type;

    TestcaseListItem(OWLLogicalAxiom axiom, TestcaseType type, OWLOntology ontology, Debugger debugger) {
        super(axiom, ontology, debugger);
        this.type = type;
        this.testCaseAxioms = new TreeSet<>();
        this.testCaseAxioms.add(this.axiom);
    }

    Set<OWLLogicalAxiom> getTestcase() {
        return testCaseAxioms;
    }

    public TestcaseType getType() {
        return type;
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

}
