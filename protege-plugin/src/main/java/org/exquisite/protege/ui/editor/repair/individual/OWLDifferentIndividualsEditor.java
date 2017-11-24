package org.exquisite.protege.ui.editor.repair.individual;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLIndividualSetEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.Set;

/**
 * Repair editor for Individual Inequality.
 *
 * <p>An individual inequality axiom DifferentIndividuals( a1 ... an ) states that all of the individuals ai, 1 ≤ i ≤ n,
 * are different from each other; that is, no individuals ai and aj with i ≠ j can be derived to be equal.
 * This axiom can be used to axiomatize the unique name assumption — the assumption that all different individual names
 * denote different individuals.</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Individual_Inequality">9.6.2 Individual Inequality</a>
 * @see org.protege.editor.owl.ui.frame.individual.OWLDifferentIndividualAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLDifferentIndividualsEditor extends AbstractOWLObjectRepairEditor<OWLNamedIndividual, OWLDifferentIndividualsAxiom, Set<OWLNamedIndividual>> {

    public OWLDifferentIndividualsEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLDifferentIndividualsAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<Set<OWLNamedIndividual>> getOWLObjectEditor() {
        return new OWLIndividualSetEditor(getOWLEditorKit());
    }

    @Override
    public OWLDifferentIndividualsAxiom createAxiom(Set<OWLNamedIndividual> editedObject) {
        editedObject.add(getRootObject());
        return getOWLDataFactory().getOWLDifferentIndividualsAxiom(editedObject);
    }

    @Override
    public boolean checkEditorResults(OWLObjectEditor<Set<OWLNamedIndividual>> editor) {
        Set<OWLNamedIndividual> equivalents = editor.getEditedObject();
        return !equivalents.contains(getRootObject());
    }

    @Override
    public void setAxiom(OWLDifferentIndividualsAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getIndividuals().iterator().next().asOWLNamedIndividual();
    }
}
