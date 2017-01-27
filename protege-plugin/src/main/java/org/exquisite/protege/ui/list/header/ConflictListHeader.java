package org.exquisite.protege.ui.list.header;

import org.protege.editor.core.ui.list.MListSectionHeader;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.Set;

public class ConflictListHeader implements MListSectionHeader {

    private Set<OWLLogicalAxiom> axioms;

    private String name;

    public ConflictListHeader(Set<OWLLogicalAxiom> axioms, String name) {
        this.axioms = axioms;
        this.name = name;
    }

    public Set<OWLLogicalAxiom> getAxioms() {
        return axioms;
    }

    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean canAdd() {
        return false;
    }
}
