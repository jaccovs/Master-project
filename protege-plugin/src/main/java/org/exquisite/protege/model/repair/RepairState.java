package org.exquisite.protege.model.repair;

import org.exquisite.protege.ui.editor.repair.RepairEditor;
import org.exquisite.protege.ui.list.RepairAxiomList;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author wolfi
 */
public class RepairState implements OWLObjectEditorHandler<OWLLogicalAxiom> {

    private OWLLogicalAxiom originalAxiom;

    private OWLOntology ontology;

    private OWLEditorKit editorKit;

    private RepairAxiomList list;

    private RepairListItem listItem;

    private boolean hasEditor;

    private boolean isDeleted;

    private boolean isModified;

    public RepairState(OWLLogicalAxiom axiom, OWLOntology ontology, OWLEditorKit editorKit, RepairAxiomList list, RepairListItem listItem) {
        //this.axiom = axiom;
        this.originalAxiom = axiom;
        this.ontology = ontology;
        this.editorKit = editorKit;
        this.list = list;
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
        java.util.List<OWLOntologyChange> changes = new ArrayList<>();
        OWLOntology ontology = getOntology();
        changes.add(new RemoveAxiom(ontology, axiom));
        editorKit.getOWLModelManager().applyChanges(changes);
        this.isDeleted = true;
        list.updateListItem(this.listItem);
    }

    private void restoreAxiom(OWLLogicalAxiom axiom) {
        java.util.List<OWLOntologyChange> changes = new ArrayList<>();
        OWLOntology ontology = getOntology();
        changes.add(new AddAxiom(ontology, axiom));
        editorKit.getOWLModelManager().applyChanges(changes);
        this.isDeleted = false;
        this.isModified = false;
        list.updateListItem(this.listItem);
    }

    private void modifyAxiom(OWLLogicalAxiom newAxiom, OWLLogicalAxiom oldAxiom) {
        Set<OWLAnnotation> axiomAnnotations = oldAxiom.getAnnotations();
        if (!axiomAnnotations.isEmpty()) {
            newAxiom = (OWLLogicalAxiom) newAxiom.getAnnotatedAxiom(axiomAnnotations);
        }
        isModified = !newAxiom.equals(oldAxiom);

        if (isModified) {
            java.util.List<OWLOntologyChange> changes = new ArrayList<>();
            OWLOntology ontology = getOntology();
            if (ontology != null) {
                changes.add(new RemoveAxiom(ontology, oldAxiom));
                changes.add(new AddAxiom(ontology, newAxiom));
            } else {
                OWLOntology activeOntology = editorKit.getOWLModelManager().getActiveOntology();
                changes.add(new AddAxiom(activeOntology, newAxiom));
            }
            editorKit.getOWLModelManager().applyChanges(changes);
            listItem.setAxiom(newAxiom);

            this.isDeleted = false;
            list.updateListItem(this.listItem);
        }
    }

    private OWLOntology getOntology() {
        return this.ontology;
    }

    private OWLLogicalAxiom getAxiom() {
        return listItem.getAxiom();
    }

}
