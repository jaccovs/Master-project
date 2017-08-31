package org.exquisite.protege.model.repair;

import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.protege.model.explanation.Explanation;
import org.exquisite.protege.ui.editor.repair.RepairEditor;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.Set;

/**
 * @author wolfi
 */
public class RepairManager implements OWLObjectEditorHandler<OWLLogicalAxiom> {

    private OWLLogicalAxiom originalAxiom;

    private OWLEditorKit editorKit;

    private RepairListItem listItem;

    private boolean hasEditor;

    private boolean isDeleted;

    private boolean isModified;

    private Explanation explanation;

    public RepairManager(OWLLogicalAxiom axiom, Explanation explanation, OWLEditorKit editorKit, RepairListItem listItem) {
        this.originalAxiom = axiom;
        this.explanation = explanation;
        this.editorKit = editorKit;
        this.listItem = listItem;
        this.listItem.setAxiom(axiom);
        this.hasEditor = RepairEditor.hasEditor(axiom);
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
        return this.hasEditor && !isDeleted;
    }

    public boolean isDeleteable() {
        return !isDeleted;
    }

    public boolean isModified() { return isModified; }

    public boolean hasEditor() { return hasEditor; }

    public boolean handleDelete() {
        deleteAxiom(listItem.getAxiom());
        return true;
    }

    public void handleReset() {
        if (isModified) {
            modifyAxiom(originalAxiom, listItem.getAxiom());
            isModified = false;
            isDeleted = false;
        } else if (isDeleted) {
            restoreAxiom(originalAxiom);
        }
    }

    @Override
    public void handleEditingFinished(Set<OWLLogicalAxiom> editedObjects) {
        if (editedObjects.isEmpty()) {
            return;
        }

        OWLLogicalAxiom newAxiom = editedObjects.iterator().next();
        // the editor should protect from this, but just in case
        if (newAxiom == null) {
            return;
        }

        OWLLogicalAxiom oldAxiom = getAxiom();

        modifyAxiom(newAxiom, oldAxiom);
    }

    private void deleteAxiom(OWLLogicalAxiom axiom) {
        explanation.getDiagnosisModel().getCorrectFormulas().remove(axiom);
        this.isDeleted = true;
        explanation.explain();

    }

    private void restoreAxiom(OWLLogicalAxiom axiom) {
        try {
            explanation.getDiagnosisModel().getCorrectFormulas().add(axiom);
        } catch (DiagnosisRuntimeException e) {
            // the diagnosis model can become inconsistent!
        }
        this.isDeleted = false;
        this.isModified = false;
        explanation.explain();
    }

    private void modifyAxiom(OWLLogicalAxiom newAxiom, OWLLogicalAxiom oldAxiom) {
        Set<OWLAnnotation> axiomAnnotations = oldAxiom.getAnnotations();
        if (!axiomAnnotations.isEmpty()) {
            newAxiom = (OWLLogicalAxiom) newAxiom.getAnnotatedAxiom(axiomAnnotations);
        }
        isModified = !newAxiom.equals(oldAxiom);

        if (isModified) {
            explanation.getDiagnosisModel().getCorrectFormulas().remove(oldAxiom);
            try {
                explanation.getDiagnosisModel().getCorrectFormulas().add(newAxiom);
            } catch (DiagnosisRuntimeException e) {
                // the diagnosis model can become inconsistent!
            }
            listItem.setAxiom(newAxiom);
            this.isDeleted = false;
            this.explanation.explain();
        }
    }

    private OWLLogicalAxiom getAxiom() {
        return listItem.getAxiom();
    }

    public void dispose() {

    }

}
