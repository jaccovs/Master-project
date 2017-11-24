package org.exquisite.protege.ui.editor.repair.objectproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyExpressionEditor;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.CollectionFactory;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Repair editor for Equivalent Object Properties.
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Equivalent_Object_Properties">9.2.2 Equivalent Object Properties</a>
 * @see org.protege.editor.owl.ui.frame.objectproperty.OWLEquivalentObjectPropertiesAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLEquivalentObjectPropertiesEditor extends AbstractOWLObjectRepairEditor<OWLObjectProperty, OWLEquivalentObjectPropertiesAxiom, OWLObjectPropertyExpression> {

    public OWLEquivalentObjectPropertiesEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLEquivalentObjectPropertiesAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLObjectPropertyExpression> getOWLObjectEditor() {
        final OWLObjectPropertyExpressionEditor editor = new OWLObjectPropertyExpressionEditor(getOWLEditorKit());
        final Set<OWLObjectPropertyExpression> equivs =  new HashSet<>(getAxiom().getProperties());
        equivs.remove(rootObject);
        if (equivs.size() == 1){
            final OWLObjectPropertyExpression p = equivs.iterator().next();
            editor.setEditedObject(p);
        }
        return editor;
    }

    @Override
    public OWLEquivalentObjectPropertiesAxiom createAxiom(OWLObjectPropertyExpression editedObject) {
        return getOWLDataFactory().getOWLEquivalentObjectPropertiesAxiom(CollectionFactory.createSet(rootObject, editedObject));
    }

    @Override
    public void setAxiom(OWLEquivalentObjectPropertiesAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getProperties().iterator().next().asOWLObjectProperty();
    }

    @Override
    public boolean checkEditorResults(OWLObjectEditor<OWLObjectPropertyExpression> editor) {
        Set<OWLObjectPropertyExpression> equivalents = editor.getEditedObjects();
        return equivalents.size() != 1 || !equivalents.contains(getRootObject());
    }
}
