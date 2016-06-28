package org.exquisite.protege.ui.list;

import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.Set;

class AxiomListHeader {

    private Set<OWLLogicalAxiom> axioms;

    private String headerPref;

    AxiomListHeader(Set<OWLLogicalAxiom> axioms, String headerPref) {
        this.axioms = axioms;
        this.headerPref = headerPref;
    }

    private Set<OWLLogicalAxiom> getAxioms() {
        return axioms;
    }

    public String toString() {
        String r = headerPref + "(Size: " + getAxioms().size();
        return r + ")";
    }

}
