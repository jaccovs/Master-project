package org.exquisite.protege.ui.editor.repair.dataproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.*;

import java.awt.*;

/**
 * Repair editor for Data Property Domains.
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Data_Property_Domain">9.3.4 Data Property Domain</a>
 * @see org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyDomainFrameSectionRow
 * @author wolfi
 */
public class OWLDataPropertyDomainAxiomEditor extends AbstractOWLObjectRepairEditor<OWLDataPropertyDomainAxiom, OWLClassExpression> {

    private OWLDataPropertyExpression rootObject = null;

    private OWLClassExpression domain = null;

    public OWLDataPropertyDomainAxiomEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLDataPropertyDomainAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
        rootObject = axiom.getProperty();
        domain = axiom.getDomain();
    }

    @Override
    public OWLObjectEditor<OWLClassExpression> getOWLObjectEditor() {
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
