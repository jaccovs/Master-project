package org.exquisite.protege.model.repair;

import org.exquisite.protege.explanation.Explanation;
import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.exquisite.protege.ui.editor.repair.OWLObjectRepairEditorFactory;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The repair state keeps track of the state of repair for an axiom from a diagnosis.
 *
 * @author wolfi
 */
public class RepairState implements OWLObjectEditorHandler<OWLLogicalAxiom> {

    /**
     * The original axiom from a diagnosis, which can be repaired.
     */
    private OWLLogicalAxiom originalAxiom;

    /**
     * A reference to the list item in order to update the view if the axiom gets manipulated.
     */
    private RepairListItem listItem;

    /**
     * An instance of the appropriate editor.
     */
    private AbstractOWLObjectRepairEditor repairEditor;

    /**
     * Did the user delete this axiom.
     */
    private boolean isDeleted;

    /**
     * Did the user modify the axiom either by editing or deleting it.
     */
    private boolean isModified;

    /**
     * A reference to the explanation in order to call back on updates.
     */
    private Explanation explanation;

    public RepairState(OWLLogicalAxiom axiom, Explanation explanation, RepairListItem listItem) {
        this.originalAxiom = axiom;
        this.explanation = explanation;
        this.listItem = listItem;
        this.listItem.setAxiom(axiom);
        this.repairEditor = OWLObjectRepairEditorFactory.createRepairEditor(explanation.getOWLEditorKit(), listItem.getParent(), explanation.getOntology(), axiom, this);
        this.isDeleted = false;
        this.isModified = false;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean hasChanged() {
        return isDeleted || isModified;
    }

    public boolean isEditable() {
        return this.repairEditor.hasEditor() && !isDeleted;
    }

    public boolean isDeleteable() {
        return !isDeleted;
    }

    public boolean hasEditor() { return this.repairEditor.hasEditor(); }

    public boolean handleDelete() {
        deleteAxiom(listItem.getAxiom());
        return true;
    }

    public void handleReset() {
        if (isModified) {
            modifyAxiom(originalAxiom, listItem.getAxiom());
        } else if (isDeleted) {
            restoreAxiom();
        }
    }

    @Override
    public void handleEditingFinished(Set<OWLLogicalAxiom> editedObjects) {
        if (editedObjects.isEmpty()) {
            return;
        }

        OWLAxiom axiomAfterEditing = this.repairEditor.createAxiom(editedObjects.iterator().next());
        // the editor should protect us from this, but just in case
        if (axiomAfterEditing == null) {
            return;
        }

        final OWLLogicalAxiom axiomBeforeEditing = getAxiom();
        modifyAxiom(axiomAfterEditing, axiomBeforeEditing);
    }

    public List<OWLAxiomChange> getChanges(final OWLOntology ontology) {
        if (hasChanged()) {
            List<OWLAxiomChange> changes = new ArrayList<>();
            changes.add(new RemoveAxiom(ontology, this.originalAxiom)); // either the axiom is deleted ..
            if (isModified) {
                changes.add(new AddAxiom(ontology, listItem.getAxiom())); // .. or modified
            }
            return changes;
        } else {
            return Collections.emptyList();
        }
    }

    public void dispose() {}

    private void deleteAxiom(OWLLogicalAxiom axiom) {
        final boolean hasBeenDeleted = explanation.deleteAxiom(axiom);
        if (hasBeenDeleted) {
            // deleting a modified axiom means that resetting to the original axiom becomes possible
            if (!axiom.equals(this.originalAxiom)) {
                listItem.setAxiom(this.originalAxiom);
                repairEditor.setAxiom(this.originalAxiom);
            }
            this.isModified = false;
            this.isDeleted = true;
        }
    }

    private void restoreAxiom() {
        final boolean hasBeenRestored = explanation.restoreAxiom(this.originalAxiom);
        if (hasBeenRestored) {
            this.isDeleted = false;
            this.isModified = false;
        }
    }

    private void modifyAxiom(OWLAxiom newAxiom, OWLAxiom oldAxiom) {
        Set<OWLAnnotation> axiomAnnotations = oldAxiom.getAnnotations();
        if (!axiomAnnotations.isEmpty()) {
            newAxiom = (OWLLogicalAxiom) newAxiom.getAnnotatedAxiom(axiomAnnotations);
        }

        if (!newAxiom.equals(oldAxiom)) {

            isModified = explanation.modifyAxiom((OWLLogicalAxiom)newAxiom, (OWLLogicalAxiom)oldAxiom);

            if (isModified) {
                listItem.setAxiom((OWLLogicalAxiom)newAxiom);
                repairEditor.setAxiom(newAxiom);
                isDeleted = false;
                // The modification itself might restore the original axiom
                if (newAxiom.equals(originalAxiom))
                    isModified = false;
            }
        }
    }

    private OWLLogicalAxiom getAxiom() {
        return listItem.getAxiom();
    }

    public AbstractOWLObjectRepairEditor getRepairEditor() {
        return repairEditor;
    }
}
