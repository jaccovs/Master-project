package org.exquisite.protege.ui.editor.repair.dataproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataPropertyEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.CollectionFactory;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Repair editor for Equivalent Data Properties.
 *
 * <p>
 * An equivalent data properties axiom EquivalentDataProperties( DPE1 ... DPEn ) states that all the data property
 * expressions DPEi, 1 ≤ i ≤ n, are semantically equivalent to each other. This axiom allows one to use each DPEi as a
 * synonym for each DPEj — that is, in any expression in the ontology containing such an axiom, DPEi can be replaced
 * with DPEj without affecting the meaning of the ontology.
 *
 * The axiom EquivalentDataProperties( DPE1 DPE2 ) can be seen as a syntactic shortcut for the following axiom:
 * SubDataPropertyOf( DPE1 DPE2 )
 * SubDataPropertyOf( DPE2 DPE1 )
 * </p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Equivalent_Data_Properties">9.3.2 Equivalent Data Properties</a>
 * @see org.protege.editor.owl.ui.frame.dataproperty.OWLEquivalentDataPropertiesFrameSectionRow
 * @author wolfi
 */
public class OWLEquivalentDataPropertiesEditor extends AbstractOWLObjectRepairEditor<OWLDataProperty, OWLEquivalentDataPropertiesAxiom, OWLDataProperty> {

    public OWLEquivalentDataPropertiesEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLEquivalentDataPropertiesAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLDataProperty> getOWLObjectEditor() {
        final OWLDataPropertyEditor editor = new OWLDataPropertyEditor(getOWLEditorKit());
        final Set<OWLDataPropertyExpression> equivs = new HashSet<>(getAxiom().getProperties());
        equivs.remove(getRootObject());
        if (equivs.size() == 1){
            final OWLDataPropertyExpression p = equivs.iterator().next();
            if (!p.isAnonymous()){
                editor.setEditedObject(p.asOWLDataProperty());
            }
        }
        return editor;
    }

    @Override
    public OWLEquivalentDataPropertiesAxiom createAxiom(OWLDataProperty editedObject) {
        return getOWLDataFactory().getOWLEquivalentDataPropertiesAxiom(CollectionFactory.createSet(getRootObject(), editedObject));
    }

    @Override
    public void setAxiom(OWLEquivalentDataPropertiesAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getProperties().iterator().next().asOWLDataProperty();
    }

    @Override
    public boolean checkEditorResults(OWLObjectEditor<OWLDataProperty> editor) {
        final Set<OWLDataProperty> equivalents = editor.getEditedObjects();
        return equivalents.size() != 1 || !equivalents.contains(getRootObject());
    }
}
