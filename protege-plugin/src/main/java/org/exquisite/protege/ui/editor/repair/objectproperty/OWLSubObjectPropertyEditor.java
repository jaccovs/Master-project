package org.exquisite.protege.ui.editor.repair.objectproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyExpressionEditor;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import java.awt.*;

/**
 * Repair editor for Object Subproperties.
 *
 * <p>Object subproperty axioms are analogous to subclass axioms, and they come in two forms.
 * The basic form is SubObjectPropertyOf( OPE1 OPE2 ). This axiom states that the object property expression OPE1 is a
 * subproperty of the object property expression OPE2 — that is, if an individual x is connected by OPE1 to an
 * individual y, then x is also connected by OPE2 to y.
 * The more complex form is SubObjectPropertyOf( ObjectPropertyChain( OPE1 ... OPEn ) OPE ).
 * This axiom states that, if an individual x is connected by a sequence of object property expressions
 * OPE1, ..., OPEn with an individual y, then x is also connected with y by the object property expression OPE.
 * Such axioms are also known as complex role inclusions</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Object_Subproperties">9.2.1 Object Subproperties</a>
 * @see org.protege.editor.owl.ui.frame.objectproperty.OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow
 * @author wolfi
 */
public class OWLSubObjectPropertyEditor extends AbstractOWLObjectRepairEditor<OWLObjectProperty, OWLSubObjectPropertyOfAxiom, OWLObjectPropertyExpression> {

    public OWLSubObjectPropertyEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLSubObjectPropertyOfAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLObjectPropertyExpression> getOWLObjectEditor() {
        OWLObjectPropertyExpressionEditor editor = new OWLObjectPropertyExpressionEditor(getOWLEditorKit());
        OWLObjectPropertyExpression p = getAxiom().getSuperProperty();
        editor.setEditedObject(p);
        return editor;
    }

    @Override
    public OWLSubObjectPropertyOfAxiom createAxiom(OWLObjectPropertyExpression editedObject) {
        return getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(getRootObject(), editedObject);
    }

    @Override
    public void setAxiom(OWLSubObjectPropertyOfAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getSubProperty().asOWLObjectProperty();
    }
}
