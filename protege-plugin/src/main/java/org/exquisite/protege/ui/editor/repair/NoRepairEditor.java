package org.exquisite.protege.ui.editor.repair;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

/**
 * General repair editor for unsupported axiom types.
 *
 * @author wolfi
 */
public class NoRepairEditor extends AbstractOWLObjectRepairEditor<OWLAxiom, OWLAxiom, OWLAxiom> {

    NoRepairEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLAxiom> getOWLObjectEditor() {
        throw new UnsupportedOperationException("There is no editor for axiom type " + getAxiom().getAxiomType());
    }

    @Override
    public OWLAxiom createAxiom(OWLAxiom editedObject) {
        throw new UnsupportedOperationException("The axiom creation for " + getAxiom().getAxiomType() + " is not supported");
    }

    @Override
    public boolean hasEditor() {
        return false;
    }
}
