package org.exquisite.protege.ui.list;

import org.protege.editor.core.ui.list.MListItem;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class AxiomListItem implements MListItem {

    private OWLLogicalAxiom axiom;

    private OWLOntology ontology;

    public OWLLogicalAxiom getAxiom() {
        return axiom;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public AxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology) {
        this.axiom = axiom;
        this.ontology = ontology;
    }

    public boolean isEditable() {
        return false;
    }

    public void handleEdit() {
    }

    public boolean isDeleteable() {
        return false;
    }

    public boolean handleDelete() {
        return false;
    }

    public String getTooltip() {
        return "AxiomType:" + axiom.getAxiomType().getName();
    }

}
