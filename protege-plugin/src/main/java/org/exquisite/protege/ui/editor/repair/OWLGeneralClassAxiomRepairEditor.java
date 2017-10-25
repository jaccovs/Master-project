package org.exquisite.protege.ui.editor.repair;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLGeneralAxiomEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

/**
 * General class axiom editor for repair.
 *
 * @author wolfi
 */
public class OWLGeneralClassAxiomRepairEditor extends AbstractOWLObjectRepairEditor<OWLClassAxiom, OWLClassAxiom> {

    OWLGeneralClassAxiomRepairEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLClassAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor getOWLObjectEditor() {
        OWLGeneralAxiomEditor editor =  new OWLGeneralAxiomEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom());
        return editor;
    }

    @Override
    public OWLClassAxiom createAxiom(OWLClassAxiom editedObject) {
        return editedObject;
    }

}
