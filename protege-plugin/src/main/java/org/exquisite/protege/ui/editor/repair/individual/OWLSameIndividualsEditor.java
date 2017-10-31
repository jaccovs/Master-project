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
 * <p>An individual equality axiom SameIndividual( a1 ... an ) states that all of the individuals ai, 1 ≤ i ≤ n,
 * are equal to each other. This axiom allows one to use each ai as a synonym for each aj — that is, in any expression
 * in the ontology containing such an axiom, ai can be replaced with aj without affecting the meaning of the ontology.</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Individual_Equality">9.6.1 Individual Equality</a>
 * @see org.protege.editor.owl.ui.frame.individual.OWLSameIndividualsAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLSameIndividualsEditor extends AbstractOWLObjectRepairEditor<OWLNamedIndividual, OWLSameIndividualAxiom, Set<OWLNamedIndividual>> {

    public OWLSameIndividualsEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLSameIndividualAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<Set<OWLNamedIndividual>> getOWLObjectEditor() {
        return new OWLIndividualSetEditor(getOWLEditorKit());
    }

    @Override
    public OWLSameIndividualAxiom createAxiom(Set<OWLNamedIndividual> editedObject) {
        editedObject.add(this.getRootObject());
        return getOWLDataFactory().getOWLSameIndividualAxiom(editedObject);
    }

    @Override
    public boolean checkEditorResults(OWLObjectEditor<Set<OWLNamedIndividual>> editor) {
        Set<OWLNamedIndividual> equivalents = editor.getEditedObject();
        return !equivalents.contains(this.getRootObject());
    }

    @Override
    public void setAxiom(OWLSameIndividualAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getIndividuals().iterator().next().asOWLNamedIndividual();
    }
}
