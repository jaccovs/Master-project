package org.exquisite.protege.ui.editor.repair.objectproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyChainEditor;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import java.awt.*;
import java.util.List;

/**
 * Repair editor for Object Property Chains.
 *
 * @see org.protege.editor.owl.ui.frame.objectproperty.OWLPropertyChainAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLPropertyChainEditor extends AbstractOWLObjectRepairEditor<OWLObjectProperty, OWLSubPropertyChainOfAxiom, List<OWLObjectPropertyExpression>> {

    public OWLPropertyChainEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLSubPropertyChainOfAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<List<OWLObjectPropertyExpression>> getOWLObjectEditor() {
        OWLObjectPropertyChainEditor editor = new OWLObjectPropertyChainEditor(getOWLEditorKit());
        editor.setAxiom(getAxiom());
        return editor;
    }

    @Override
    public OWLSubPropertyChainOfAxiom createAxiom(List<OWLObjectPropertyExpression> editedObject) {
        return getOWLDataFactory().getOWLSubPropertyChainOfAxiom(editedObject, getRootObject());
    }

    @Override
    public void setAxiom(OWLSubPropertyChainOfAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getSuperProperty().asOWLObjectProperty();
    }
}
