package org.exquisite.protege.ui.editor.repair.individual;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataPropertyRelationshipEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.frame.OWLDataPropertyConstantPair;
import org.semanticweb.owlapi.model.*;

import java.awt.*;

/**
 * Repair editor for Positive Data Property Assertions.
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Positive_Data_Property_Assertions">9.6.6 Positive Data Property Assertions</a>
 * @see org.protege.editor.owl.ui.frame.individual.OWLDataPropertyAssertionAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLDataPropertyAssertionEditor extends AbstractOWLObjectRepairEditor<OWLDataPropertyAssertionAxiom, OWLDataPropertyConstantPair> {

    private OWLIndividual rootObject = null;

    public OWLDataPropertyAssertionEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLDataPropertyAssertionAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
        rootObject = axiom.getSubject();
    }

    @Override
    public OWLObjectEditor<OWLDataPropertyConstantPair> getOWLObjectEditor() {
        final OWLDataPropertyRelationshipEditor editor = new OWLDataPropertyRelationshipEditor(getOWLEditorKit());
        editor.setDataPropertyAxiom(getAxiom());
        return editor;
    }

    @Override
    public OWLDataPropertyAssertionAxiom createAxiom(OWLDataPropertyConstantPair editedObject) {
        return getOWLDataFactory().getOWLDataPropertyAssertionAxiom(editedObject.getProperty(), rootObject, editedObject.getConstant());
    }

    @Override
    public void setAxiom(OWLDataPropertyAssertionAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getSubject();
    }
}
