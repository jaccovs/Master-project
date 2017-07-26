package org.exquisite.protege.ui.list.item;

import org.exquisite.protege.model.repair.RepairManager;
import org.exquisite.protege.ui.editor.repair.RepairEditor;
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

    private boolean isEditable;

    public RepairListItem(OWLLogicalAxiom axiom, OWLOntology ontology, OWLEditorKit editorKit, RepairManager repairManager, Component parent) {
        super(axiom, ontology);
        this.editorKit = editorKit;
        this.parent = parent;
        this.isEditable = RepairEditor.hasEditor(axiom);
    }

    @Override
    public boolean isEditable() {
        return this.isEditable;
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    public void handleEdit() {
        if (isEditable) {
            new RepairEditor(editorKit, parent, axiom).showEditorDialog(editor -> {
                final Set editedObjects = editor.getEditedObjects();
                editor.getHandler().handleEditingFinished(editedObjects);
            });
        }
    }

    @Override
    public boolean handleDelete() {
        return super.handleDelete();
    }

}
