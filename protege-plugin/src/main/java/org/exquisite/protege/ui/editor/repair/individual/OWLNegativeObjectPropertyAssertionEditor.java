package org.exquisite.protege.ui.editor.repair.individual;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyIndividualPairEditor2;
import org.protege.editor.owl.ui.frame.OWLObjectPropertyIndividualPair;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

/**
 * Repair editor for Negative Object Property Assertions.
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Negative_Object_Property_Assertions">9.6.5 Negative Object Property Assertions</a>
 * @see org.protege.editor.owl.ui.frame.individual.OWLNegativeObjectPropertyAssertionFrameSectionRow
 * @author wolfi
 */
public class OWLNegativeObjectPropertyAssertionEditor extends AbstractOWLObjectRepairEditor<OWLNegativeObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair>  {

    private OWLIndividual rootObject = null;

    public OWLNegativeObjectPropertyAssertionEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLNegativeObjectPropertyAssertionAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLObjectPropertyIndividualPair> getOWLObjectEditor() {
        OWLObjectPropertyIndividualPairEditor2 editor = new OWLObjectPropertyIndividualPairEditor2(getOWLEditorKit());
        editor.setEditedObject(new OWLObjectPropertyIndividualPair(getAxiom().getProperty(), getAxiom().getObject()));
        return editor;
    }

    @Override
    public OWLNegativeObjectPropertyAssertionAxiom createAxiom(OWLObjectPropertyIndividualPair editedObject) {
        return getOWLDataFactory().getOWLNegativeObjectPropertyAssertionAxiom(editedObject.getProperty(), rootObject, editedObject.getIndividual());
    }

    @Override
    public void setAxiom(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getSubject();
    }
}
