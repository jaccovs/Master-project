package org.exquisite.protege.ui.list.item;

import org.exquisite.protege.model.explanation.Explanation;
import org.exquisite.protege.model.repair.RepairManager;
import org.exquisite.protege.ui.editor.repair.RepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.awt.*;
import java.util.Set;

/**
 * @author wolfi
 */
public class RepairListItem extends AxiomListItem {

    private OWLEditorKit editorKit;

    private Component parent;

    private RepairManager repairState;

    private Explanation explanation;


    public RepairListItem(OWLLogicalAxiom axiom, Explanation explanation, OWLEditorKit editorKit, Component parent) {
        super(axiom, explanation.getOntology());
        this.editorKit = editorKit;
        this.parent = parent;
        this.explanation = explanation;
        this.repairState = new RepairManager(axiom, explanation, editorKit, this);
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

    public void dispose() {
        explanation.dispose();
    }

    public void explain() {
        explanation.explain();
    }

}
