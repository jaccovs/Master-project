package org.exquisite.protege.ui.list.item;

import org.exquisite.protege.model.repair.RepairState;
import org.exquisite.protege.ui.editor.repair.RepairEditor;
import org.exquisite.protege.ui.list.RepairAxiomList;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.Set;

/**
 * @author wolfi
 */
public class RepairListItem extends AxiomListItem {

    private OWLEditorKit editorKit;

    private Component parent;

    private RepairState repairState;

    public RepairListItem(RepairAxiomList list, OWLLogicalAxiom axiom, OWLOntology ontology, OWLEditorKit editorKit, Component parent) {
        super(axiom, ontology);
        this.editorKit = editorKit;
        this.parent = parent;
        this.repairState = new RepairState(axiom, ontology, editorKit, list, this);
    }

    public void setAxiom(OWLLogicalAxiom axiom) {
        this.axiom = axiom;
    }

    public boolean isDeleted() {
        return repairState.isDeleted();
    }

    public boolean hasChanged() {
        return repairState.hasChanged();
    }

    @Override
    public boolean isEditable() {
        return repairState.isEditable();
    }

    @Override
    public boolean isDeleteable() {
        return repairState.isDeleteable();
    }

    @Override
    public void handleEdit() {
        if (repairState.hasEditor()) {
            new RepairEditor(editorKit, parent, axiom, repairState).showEditorDialog(editor -> {
                final Set editedObjects = editor.getEditedObjects();
                editor.getHandler().handleEditingFinished(editedObjects);
            });
        }
    }

    @Override
    public boolean handleDelete() {
        return repairState.handleDelete();
    }

    public void handleReset() {
        repairState.handleReset();
    }

}
