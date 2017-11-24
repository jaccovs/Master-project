package org.exquisite.protege.ui.editor.repair.individual;

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
 * Repair editor for Class Assertions.
 *
 * <p>A class assertion ClassAssertion( CE a ) states that the individual a is an instance of the class expression CE.</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Class_Assertions">9.6.3 Class Assertions</a>
 * @see org.protege.editor.owl.ui.frame.individual.OWLClassAssertionAxiomTypeFrameSectionRow
 * @author wolfi
 */
public class OWLClassAssertionEditor extends AbstractOWLObjectRepairEditor<OWLIndividual, OWLClassAssertionAxiom, OWLClassExpression> {

    public OWLClassAssertionEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLClassAssertionAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLClassExpression> getOWLObjectEditor() {
        final OWLReasonerManager reasonerManager = getOWLEditorKit().getOWLModelManager().getOWLReasonerManager();
        OWLClassDescriptionEditor editor;
        final OWLComponentFactory owlComponentFactory = getOWLEditorKit().getWorkspace().getOWLComponentFactory();

        try {
            editor = owlComponentFactory.getOWLClassDescriptionEditor(getAxiom().getClassExpression(), AxiomType.CLASS_ASSERTION);
        } catch (InconsistentOntologyException e) {
            // for inconsistent ontologies an InconsistentOntologyException will be thrown when creating an editor.
            // therefore let us kill the current running reasoner and try it again.
            reasonerManager.killCurrentReasoner();
            getOWLModelManager().fireEvent(EventType.REASONER_CHANGED);
            try {
                editor = owlComponentFactory.getOWLClassDescriptionEditor(getAxiom().getClassExpression(), AxiomType.CLASS_ASSERTION);
            } catch (Exception ex) {
                // fall back solution if there is still an exception
                editor = owlComponentFactory.getOWLClassDescriptionEditor(null, AxiomType.CLASS_ASSERTION);
            }
        }
        return editor;
    }

    @Override
    public OWLClassAssertionAxiom createAxiom(OWLClassExpression editedObject) {
        return getOWLDataFactory().getOWLClassAssertionAxiom(editedObject, getRootObject());
    }

    @Override
    public void setAxiom(OWLClassAssertionAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getIndividual();
    }
}
