package org.exquisite.protege.ui.editor.repair.individual;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataPropertyRelationshipEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.frame.OWLDataPropertyConstantPair;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

/**
 * Repair editor for Negative Data Property Assertions.
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Negative_Data_Property_Assertions">9.6.7 Negative Data Property Assertions</a>
 * @see org.protege.editor.owl.ui.frame.individual.OWLNegativeDataPropertyAssertionFrameSectionRow
 * @author wolfi
 */
public class OWLNegativeDataPropertyAssertionEditor extends AbstractOWLObjectRepairEditor<OWLNegativeDataPropertyAssertionAxiom, OWLDataPropertyConstantPair> {

    private OWLIndividual rootObject = null;

    public OWLNegativeDataPropertyAssertionEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLNegativeDataPropertyAssertionAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLDataPropertyConstantPair> getOWLObjectEditor() {
        final OWLDataPropertyRelationshipEditor editor = new OWLDataPropertyRelationshipEditor(getOWLEditorKit());
        editor.setDataPropertyAxiom(getAxiom());
        return editor;
    }

    @Override
    public OWLNegativeDataPropertyAssertionAxiom createAxiom(OWLDataPropertyConstantPair editedObject) {
        return getOWLDataFactory().getOWLNegativeDataPropertyAssertionAxiom(editedObject.getProperty(), rootObject, editedObject.getConstant());
    }

    @Override
    public void setAxiom(OWLNegativeDataPropertyAssertionAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getSubject();
    }
}
