package org.exquisite.protege.ui.editor.repair.cls;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.editor.OWLPropertySetEditor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import java.awt.*;
import java.util.Set;

/**
 * Repair editor for Keys.
 *
 * <p>A key axiom HasKey( CE ( OPE1 ... OPEm ) ( DPE1 ... DPEn ) ) states that each (named) instance of the class
 * expression CE is uniquely identified by the object property expressions OPEi and/or the data property experssions
 * DPEj — that is, no two distinct (named) instances of CE can coincide on the values of all object property
 * expressions OPEi and all data property expressions DPEj. In each such axiom in an OWL ontology, m or n (or both) must
 * be larger than zero. A key axiom of the form HasKey( owl:Thing ( OPE ) () ) is similar to the axiom
 * InverseFunctionalObjectProperty( OPE ), the main differences being that the former axiom is applicable only to
 * individuals that are explicitly named in an ontology, while the latter axiom is also applicable to anonymous
 * individuals and individuals whose existence is implied by existential quantification.</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Keys">9.5 Keys</a>
 * @see org.protege.editor.owl.ui.frame.cls.OWLKeyAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLHasKeyEditor extends AbstractOWLObjectRepairEditor<OWLClass, OWLHasKeyAxiom, Set<OWLPropertyExpression>> {

    public OWLHasKeyEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLHasKeyAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<Set<OWLPropertyExpression>> getOWLObjectEditor() {
        final OWLPropertySetEditor editor = new OWLPropertySetEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom().getPropertyExpressions());
        return editor;
    }

    @Override
    public OWLHasKeyAxiom createAxiom(Set<OWLPropertyExpression> editedObject) {
        return getOWLDataFactory().getOWLHasKeyAxiom(getRootObject(), editedObject);
    }
}
