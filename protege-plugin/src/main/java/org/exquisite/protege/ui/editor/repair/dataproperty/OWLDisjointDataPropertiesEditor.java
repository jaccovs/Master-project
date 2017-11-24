package org.exquisite.protege.ui.editor.repair.dataproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataPropertySetEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Repair editor for Disjoint Data Properties.
 *
 * <p>A disjoint data properties axiom DisjointDataProperties( DPE1 ... DPEn ) states that all of the data property
 * expressions DPEi, 1 ≤ i ≤ n, are pairwise disjoint; that is, no individual x can be connected to a literal y by both
 * DPEi and DPEj for i ≠ j.</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Disjoint_Data_Properties">9.3.3 Disjoint Data Properties</a>
 * @see org.protege.editor.owl.ui.frame.dataproperty.OWLDisjointDataPropertiesFrameSectionRow
 * @author wolfi
 */
public class OWLDisjointDataPropertiesEditor extends AbstractOWLObjectRepairEditor<OWLDataProperty, OWLDisjointDataPropertiesAxiom, Set<OWLDataProperty>> {

    public OWLDisjointDataPropertiesEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLDisjointDataPropertiesAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<Set<OWLDataProperty>> getOWLObjectEditor() {
        final OWLDataPropertySetEditor editor = new OWLDataPropertySetEditor(getOWLEditorKit());
        final Set<OWLDataPropertyExpression> disjoints = getAxiom().getProperties();
        final Set<OWLDataProperty> namedDisjoints = new HashSet<>();
        for (OWLDataPropertyExpression p : disjoints){
            if (!p.isAnonymous()){
                namedDisjoints.add(p.asOWLDataProperty());
            }
        }
        namedDisjoints.remove(getRootObject());
        editor.setEditedObject(namedDisjoints);
        // @@TODO handle property expressions
        return editor;
    }

    @Override
    public OWLDisjointDataPropertiesAxiom createAxiom(Set<OWLDataProperty> editedObject) {
        final Set<OWLDataProperty> props = new HashSet<>();
        props.add(getRootObject());
        props.addAll(editedObject);
        return getOWLDataFactory().getOWLDisjointDataPropertiesAxiom(props);
    }

    @Override
    public void setAxiom(OWLDisjointDataPropertiesAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getProperties().iterator().next().asOWLDataProperty();
    }

    @Override
    public boolean checkEditorResults(OWLObjectEditor<Set<OWLDataProperty>> editor) {
        final Set<OWLDataProperty> equivalents = editor.getEditedObject();
        return !equivalents.contains(getRootObject());
    }
}
