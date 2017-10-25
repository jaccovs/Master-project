package org.exquisite.protege.ui.editor.repair;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.*;

import java.awt.*;

/**
 * Object property domain editor for repair.
 *
 * @author wolfi
 */
public class OWLObjectPropertyDomainEditor extends AbstractOWLObjectRepairEditor<OWLObjectPropertyDomainAxiom, OWLClassExpression> {

    private OWLObjectPropertyExpression rootObject = null;

    private OWLClassExpression domain = null;

    OWLObjectPropertyDomainEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLObjectPropertyDomainAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
        rootObject = axiom.getProperty();
        domain = axiom.getDomain();
    }

    @Override
    public OWLObjectEditor getOWLObjectEditor() {
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
