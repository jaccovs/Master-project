package org.exquisite.protege.ui.editor.repair.individual;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyIndividualPairEditor2;
import org.protege.editor.owl.ui.frame.OWLObjectPropertyIndividualPair;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

/**
 * Repair editor for Positive Object Property Assertions.
 *
 * <p>A positive object property assertion ObjectPropertyAssertion( OPE a1 a2 ) states that the individual a1 is
 * connected by the object property expression OPE to the individual a2.</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Positive_Object_Property_Assertions">9.6.4 Positive Object Property Assertions</a>
 * @see org.protege.editor.owl.ui.frame.individual.OWLObjectPropertyAssertionAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLObjectPropertyAssertionEditor extends AbstractOWLObjectRepairEditor<OWLIndividual, OWLObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair> {

    public OWLObjectPropertyAssertionEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLObjectPropertyAssertionAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLObjectPropertyIndividualPair> getOWLObjectEditor() {
        final OWLObjectPropertyIndividualPairEditor2 editor = new OWLObjectPropertyIndividualPairEditor2(getOWLEditorKit());
        editor.setEditedObject(new OWLObjectPropertyIndividualPair(getAxiom().getProperty().asOWLObjectProperty(), getAxiom().getObject()));
        return editor;
    }

    @Override
    public OWLObjectPropertyAssertionAxiom createAxiom(OWLObjectPropertyIndividualPair editedObject) {
        return getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(editedObject.getProperty(), getRootObject(), editedObject.getIndividual());
    }

    @Override
    public void setAxiom(OWLObjectPropertyAssertionAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getSubject();
    }
}
