package org.exquisite.protege.ui.editor.repair.dataproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataPropertyEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;

import java.awt.*;

/**
 * Repair editor for Data Subproperties.
 *
 * <p>A data subproperty axiom SubDataPropertyOf( DPE1 DPE2 ) states that the data property expression DPE1 is a subproperty
 * of the data property expression DPE2 — that is, if an individual x is connected by DPE1 to a literal y, then x is
 * connected by DPE2 to y as well.</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Data_Subproperties">9.3.1 Data Subproperties</a>
 * @see org.protege.editor.owl.ui.frame.dataproperty.OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow
 * @author wolfi
 */
public class OWLSubDataPropertyEditor extends AbstractOWLObjectRepairEditor<OWLDataProperty, OWLSubDataPropertyOfAxiom, OWLDataProperty> {

    public OWLSubDataPropertyEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLSubDataPropertyOfAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLDataProperty> getOWLObjectEditor() {
        OWLDataPropertyEditor editor = new OWLDataPropertyEditor(getOWLEditorKit());
        OWLDataPropertyExpression p = getAxiom().getSuperProperty();
        if (!p.isAnonymous()){
            editor.setEditedObject(p.asOWLDataProperty());
        }
        return editor;
    }

    @Override
    public OWLSubDataPropertyOfAxiom createAxiom(OWLDataProperty editedObject) {
        return getOWLDataFactory().getOWLSubDataPropertyOfAxiom(getRootObject(), editedObject);
    }

    @Override
    public void setAxiom(OWLSubDataPropertyOfAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = getAxiom().getSubProperty().asOWLDataProperty();
    }
}
