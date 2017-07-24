package org.exquisite.protege.ui.list.item;

import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author wolfi
 */
public class RepairListItem extends AxiomListItem {

    public RepairListItem(OWLLogicalAxiom axiom, OWLOntology ontology) {
        super(axiom, ontology);
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    public void handleEdit() {
        super.handleEdit();
    }

    @Override
    public boolean handleDelete() {
        return super.handleDelete();
    }
}
