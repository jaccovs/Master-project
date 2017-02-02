package org.exquisite.protege.ui.list.item;

import org.protege.editor.core.ui.list.MListItem;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;


public class AxiomListItem implements MListItem {

    protected OWLLogicalAxiom axiom;

    protected OWLOntology ontology;

    public AxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology) {
        this.axiom = axiom;
        this.ontology = ontology;
    }

    public OWLLogicalAxiom getAxiom() {
        return axiom;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void handleEdit() {}

    @Override
    public boolean isDeleteable() {
        return false;
    }

    @Override
    public boolean handleDelete() {
        return false;
    }

    @Override
    public String getTooltip() {
        return null;
    }

}
