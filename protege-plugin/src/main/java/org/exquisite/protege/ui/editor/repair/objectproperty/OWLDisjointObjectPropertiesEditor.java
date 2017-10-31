package org.exquisite.protege.ui.editor.repair.objectproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyTabbedSetEditor;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Repair editor for Disjoint Object Properties.
 *
 * <p>A disjoint object properties axiom DisjointObjectProperties( OPE1 ... OPEn ) states that all of the object property
 * expressions OPEi, 1 ≤ i ≤ n, are pairwise disjoint; that is, no individual x can be connected to an individual y by
 * both OPEi and OPEj for i ≠ j.</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Disjoint_Object_Properties">9.2.3 Disjoint Object Properties</a>
 * @see org.protege.editor.owl.ui.frame.objectproperty.OWLDisjointObjectPropertiesAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLDisjointObjectPropertiesEditor extends AbstractOWLObjectRepairEditor<OWLObjectProperty, OWLDisjointObjectPropertiesAxiom, Set<OWLObjectPropertyExpression>> {

    public OWLDisjointObjectPropertiesEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLDisjointObjectPropertiesAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<Set<OWLObjectPropertyExpression>> getOWLObjectEditor() {
        final OWLObjectPropertyTabbedSetEditor editor = new OWLObjectPropertyTabbedSetEditor(getOWLEditorKit());
        final Set<OWLObjectPropertyExpression> disjoints = new HashSet<>(getAxiom().getProperties());
        disjoints.remove(getRootObject());
        editor.setEditedObject(disjoints);
        return editor;
    }

    @Override
    public OWLDisjointObjectPropertiesAxiom createAxiom(Set<OWLObjectPropertyExpression> editedObject) {
        Set<OWLObjectPropertyExpression> props = new HashSet<>();
        props.add(getRootObject());
        props.addAll(editedObject);
        return getOWLDataFactory().getOWLDisjointObjectPropertiesAxiom(props);
    }

    @Override
    public void setAxiom(OWLDisjointObjectPropertiesAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getProperties().iterator().next().asOWLObjectProperty();
    }

    @Override
    public boolean checkEditorResults(OWLObjectEditor<Set<OWLObjectPropertyExpression>> editor) {
        Set<OWLObjectPropertyExpression> equivalents = editor.getEditedObject();
        return !equivalents.contains(getRootObject());
    }
}
