package org.exquisite.protege.ui.editor.repair.objectproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.ui.editor.OWLClassDescriptionEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.util.OWLComponentFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;

import java.awt.*;

/**
 * Repair editor for Object Property Domains.
 *
 * <p>An object property domain axiom ObjectPropertyDomain( OPE CE ) states that the domain of the object property
 * expression OPE is the class expression CE — that is, if an individual x is connected by OPE with some other individual,
 * then x is an instance of CE. Each such axiom can be seen as a syntactic shortcut for the following axiom:
 * SubClassOf( ObjectSomeValuesFrom( OPE owl:Thing ) CE )</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Object_Property_Domain">9.2.5 Object Property Domain</a>
 * @see org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyDomainFrameSectionRow
 * @author wolfi
 */
public class OWLObjectPropertyDomainEditor extends AbstractOWLObjectRepairEditor<OWLObjectPropertyExpression, OWLObjectPropertyDomainAxiom, OWLClassExpression> {

    public OWLObjectPropertyDomainEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLObjectPropertyDomainAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLClassExpression> getOWLObjectEditor() {
        final OWLReasonerManager reasonerManager = getOWLEditorKit().getOWLModelManager().getOWLReasonerManager();
        OWLClassDescriptionEditor editor;
        final OWLComponentFactory owlComponentFactory = getOWLEditorKit().getWorkspace().getOWLComponentFactory();

        try {
            editor = owlComponentFactory.getOWLClassDescriptionEditor(getAxiom().getDomain(), AxiomType.OBJECT_PROPERTY_DOMAIN);
        } catch (InconsistentOntologyException e) {
            // for inconsistent ontologies an InconsistentOntologyException will be thrown when creating an editor.
            // therefore let us kill the current running reasoner and try it again.
            reasonerManager.killCurrentReasoner();
            getOWLModelManager().fireEvent(EventType.REASONER_CHANGED);
            try {
                editor = owlComponentFactory.getOWLClassDescriptionEditor(getAxiom().getDomain(), AxiomType.OBJECT_PROPERTY_DOMAIN);
            } catch (Exception ex) {
                // fall back solution if there is still an exception
                editor = owlComponentFactory.getOWLClassDescriptionEditor(null, AxiomType.OBJECT_PROPERTY_DOMAIN);
            }
        }
        return editor;
    }

    @Override
    public OWLObjectPropertyDomainAxiom createAxiom(OWLClassExpression editedObject) {
        return getOWLDataFactory().getOWLObjectPropertyDomainAxiom(getRootObject(), editedObject);
    }

    @Override
    public void setAxiom(OWLObjectPropertyDomainAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getProperty();
    }
}
