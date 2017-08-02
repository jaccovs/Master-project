package org.exquisite.protege.ui.list.item;

import org.exquisite.protege.ui.editor.repair.RepairEditor;
import org.exquisite.protege.ui.list.RepairAxiomList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author wolfi
 */
public class RepairListItem extends AxiomListItem implements OWLObjectEditorHandler<OWLLogicalAxiom> {

    private OWLLogicalAxiom originalAxiom;

    private OWLEditorKit editorKit;

    private Component parent;

    private boolean hasEditor;

    private RepairAxiomList list;

    private boolean isDeleted;

    private boolean isModified;

    public RepairListItem(RepairAxiomList list, OWLLogicalAxiom axiom, OWLOntology ontology, OWLEditorKit editorKit, Component parent) {
        super(axiom, ontology);
        this.originalAxiom = axiom;
        this.list = list;
        this.editorKit = editorKit;
        this.parent = parent;
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

    @Override
    public boolean isEditable() {
        return this.hasEditor && !isDeleted;
    }

    @Override
    public boolean isDeleteable() {
        return !isDeleted;
    }

    @Override
    public void handleEdit() {
        if (hasEditor) {
            new RepairEditor(editorKit, parent, axiom, this).showEditorDialog(editor -> {
                final Set editedObjects = editor.getEditedObjects();
                editor.getHandler().handleEditingFinished(editedObjects);
            });
        }
    }

    @Override
    public boolean handleDelete() {
        deleteAxiom(axiom);
        return true;
    }

    public void handleReset() {
        if (isModified) {
            modifyAxiom(originalAxiom, axiom);
            isModified = false;
            isDeleted = false;
        } else if (isDeleted) {
            isModified = false;
            isDeleted = false;
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
        list.updateListItem(this);
    }

    private void restoreAxiom(OWLLogicalAxiom axiom) {
        java.util.List<OWLOntologyChange> changes = new ArrayList<>();
        OWLOntology ontology = getOntology();
        changes.add(new AddAxiom(ontology, axiom));
        editorKit.getOWLModelManager().applyChanges(changes);
        this.isDeleted = false;
        this.isModified = false;
        list.updateListItem(this);
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
            this.axiom = newAxiom;
            this.isDeleted = false;
            list.updateListItem(this);
        }
    }
}
