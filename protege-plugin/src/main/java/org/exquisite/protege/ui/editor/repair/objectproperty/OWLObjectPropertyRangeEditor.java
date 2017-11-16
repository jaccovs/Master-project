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
 * Repair editor for Object Property Range.
 *
 * <p>An object property range axiom ObjectPropertyRange( OPE CE ) states that the range of the object property
 * expression OPE is the class expression CE — that is, if some individual is connected by OPE with an individual x,
 * then x is an instance of CE. Each such axiom can be seen as a syntactic shortcut for the following axiom:
 * SubClassOf( owl:Thing ObjectAllValuesFrom( OPE CE ) )</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Object_Property_Range">9.2.6 Object Property Range</a>
 * @see org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyRangeFrameSectionRow
 * @author wolfi
 */
public class OWLObjectPropertyRangeEditor extends AbstractOWLObjectRepairEditor<OWLObjectProperty, OWLObjectPropertyRangeAxiom, OWLClassExpression> {

    public OWLObjectPropertyRangeEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLObjectPropertyRangeAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLClassExpression> getOWLObjectEditor() {
        final OWLReasonerManager reasonerManager = getOWLEditorKit().getOWLModelManager().getOWLReasonerManager();
        OWLClassDescriptionEditor editor;
        final OWLComponentFactory owlComponentFactory = getOWLEditorKit().getWorkspace().getOWLComponentFactory();

        try {
            editor = owlComponentFactory.getOWLClassDescriptionEditor(getAxiom().getRange(), AxiomType.OBJECT_PROPERTY_RANGE);
        } catch (InconsistentOntologyException e) {
            // for inconsistent ontologies an InconsistentOntologyException will be thrown when creating an editor.
            // therefore let us kill the current running reasoner and try it again.
            reasonerManager.killCurrentReasoner();
            getOWLModelManager().fireEvent(EventType.REASONER_CHANGED);
            try {
                editor = owlComponentFactory.getOWLClassDescriptionEditor(getAxiom().getRange(), AxiomType.OBJECT_PROPERTY_RANGE);
            } catch (Exception ex) {
                // fall back solution if there is still an exception
                editor = owlComponentFactory.getOWLClassDescriptionEditor(null, AxiomType.OBJECT_PROPERTY_RANGE);
            }
        }
        return editor;
    }

    @Override
    public OWLObjectPropertyRangeAxiom createAxiom(OWLClassExpression editedObject) {
        return getOWLDataFactory().getOWLObjectPropertyRangeAxiom(getRootObject(), editedObject);
    }

    @Override
    public void setAxiom(OWLObjectPropertyRangeAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getProperty().asOWLObjectProperty();
    }
}
