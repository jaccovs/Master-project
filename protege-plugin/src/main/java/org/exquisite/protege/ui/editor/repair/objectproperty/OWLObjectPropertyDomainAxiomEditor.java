package org.exquisite.protege.ui.editor.repair.objectproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.*;

import java.awt.*;

/**
 * Repair editor for Object Property Domains.
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Object_Property_Domain">9.2.5 Object Property Domain</a>
 * @see org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyDomainFrameSectionRow
 * @author wolfi
 */
public class OWLObjectPropertyDomainAxiomEditor extends AbstractOWLObjectRepairEditor<OWLObjectPropertyDomainAxiom, OWLClassExpression> {

    private OWLObjectPropertyExpression rootObject = null;

    private OWLClassExpression domain = null;

    public OWLObjectPropertyDomainAxiomEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLObjectPropertyDomainAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
        rootObject = axiom.getProperty();
        domain = axiom.getDomain();
    }

    @Override
    public OWLObjectEditor<OWLClassExpression> getOWLObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(domain, AxiomType.OBJECT_PROPERTY_DOMAIN);
    }

    @Override
    public OWLObjectPropertyDomainAxiom createAxiom(OWLClassExpression editedObject) {
        return getOWLDataFactory().getOWLObjectPropertyDomainAxiom(rootObject, editedObject);
    }

    @Override
    public void setAxiom(OWLObjectPropertyDomainAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getProperty();
        domain = axiom.getDomain();
    }
}
