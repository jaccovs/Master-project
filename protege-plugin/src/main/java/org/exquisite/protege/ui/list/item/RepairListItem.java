package org.exquisite.protege.ui.list.item;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.model.explanation.Explanation;
import org.exquisite.protege.model.repair.RepairState;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * List item for the repair list showing an axiom from the diagnosis. The item is editable, deletable and deleted items
 * can be restored/reset.
 *
 * @author wolfi
 */
public class RepairListItem extends AxiomListItem {

    private Component parent;

    private RepairState repairState;

    private Explanation explanation;

    public RepairListItem(OWLLogicalAxiom axiom, Explanation explanation, Component parent) {
        super(axiom, explanation.getOntology());
        this.parent = parent;
        this.explanation = explanation;
        this.repairState = new RepairState(axiom, explanation, this);
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
            repairState.getRepairEditor().showEditorDialog(editor -> {
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

    public void showExplanation() {
        explanation.showExplanation();
    }

    public void showNoExplanation() {
        explanation.showNoExplanation(null);
    }

    public List<OWLAxiomChange> getChanges(final OWLOntology ontology) {
        return repairState.getChanges(ontology);
    }

    public DiagnosisModel<OWLLogicalAxiom> getDiagnosisModel() {
        return explanation.getDiagnosisModel();
    }

    public Component getParent() {
        return parent;
    }

    @Override
    public String getTooltip() {
        if (isDeleted()) {
            return "Deleted";
        } else if (hasChanged()) {
            return "Modified";
        } else {
            return super.getTooltip();
        }
    }
}
