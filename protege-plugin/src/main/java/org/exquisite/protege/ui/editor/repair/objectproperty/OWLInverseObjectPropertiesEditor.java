package org.exquisite.protege.ui.editor.repair.objectproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyEditor;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

/**
 * Repair editor for Inverse Object Properties.
 *
 * <p>An inverse object properties axiom InverseObjectProperties( OPE1 OPE2 ) states that the object property expression
 * OPE1 is an inverse of the object property expression OPE2. Thus, if an individual x is connected by OPE1 to an
 * individual y, then y is also connected by OPE2 to x, and vice versa.
 * Each such axiom can be seen as a syntactic shortcut for the following axiom:
 * EquivalentObjectProperties( OPE1 ObjectInverseOf( OPE2 ) )</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Inverse_Object_Properties_2">9.2.4 Inverse Object Properties</a>
 * @see org.protege.editor.owl.ui.frame.objectproperty.OWLInverseObjectPropertiesAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLInverseObjectPropertiesEditor extends AbstractOWLObjectRepairEditor<OWLObjectProperty, OWLInverseObjectPropertiesAxiom, OWLObjectProperty> {

    public OWLInverseObjectPropertiesEditor(OWLEditorKit editorKit,
                                            Component parent,
                                            OWLOntology ontology,
                                            OWLInverseObjectPropertiesAxiom axiom,
                                            OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLObjectProperty> getOWLObjectEditor() {
        OWLObjectPropertyEditor editor = new OWLObjectPropertyEditor(getOWLEditorKit());
        OWLObjectPropertyExpression p = getAxiom().getFirstProperty();
        if (p.equals(getRootObject())){
            p = getAxiom().getSecondProperty();
        }

        if (!p.isAnonymous()){
            editor.setEditedObject(p.asOWLObjectProperty());
        }
        return editor;
    }

    @Override
    public OWLInverseObjectPropertiesAxiom createAxiom(OWLObjectProperty editedObject) {
        return getOWLDataFactory().getOWLInverseObjectPropertiesAxiom(getRootObject(), editedObject);
    }

    @Override
    public void setAxiom(OWLInverseObjectPropertiesAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getFirstProperty().asOWLObjectProperty();
    }

}
