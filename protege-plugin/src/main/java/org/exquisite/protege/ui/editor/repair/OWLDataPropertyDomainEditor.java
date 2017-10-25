package org.exquisite.protege.ui.editor.repair;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.*;

import java.awt.*;

/**
 * Data property domain editor for repair.
 *
 * @author wolfi
 */
public class OWLDataPropertyDomainEditor extends AbstractOWLObjectRepairEditor<OWLDataPropertyDomainAxiom, OWLClassExpression> {

    private OWLDataPropertyExpression rootObject = null;

    private OWLClassExpression domain = null;

    OWLDataPropertyDomainEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLDataPropertyDomainAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
        rootObject = axiom.getProperty();
        domain = axiom.getDomain();
    }

    @Override
    public OWLObjectEditor getOWLObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(domain, AxiomType.DATA_PROPERTY_DOMAIN);
    }

    @Override
    public OWLDataPropertyDomainAxiom createAxiom(OWLClassExpression editedObject) {
        return getOWLDataFactory().getOWLDataPropertyDomainAxiom(rootObject, editedObject);
    }

    @Override
    public void setAxiom(OWLDataPropertyDomainAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getProperty();
        domain = axiom.getDomain();
    }
}
