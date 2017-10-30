package org.exquisite.protege.ui.editor.repair.individual;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLIndividualSetEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

import java.awt.*;
import java.util.Set;

/**
 * Repair editor for Individual Equality.
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Individual_Equality">9.6.1 Individual Equality</a>
 * @see org.protege.editor.owl.ui.frame.individual.OWLSameIndividualsAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLSameIndividualsEditor extends AbstractOWLObjectRepairEditor<OWLSameIndividualAxiom, Set<OWLNamedIndividual>> {

    public OWLSameIndividualsEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLSameIndividualAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<Set<OWLNamedIndividual>> getOWLObjectEditor() {
        return new OWLIndividualSetEditor(getOWLEditorKit());
    }

    @Override
    public OWLSameIndividualAxiom createAxiom(Set<OWLNamedIndividual> editedObject) {
        return getOWLDataFactory().getOWLSameIndividualAxiom(editedObject);
    }

}
