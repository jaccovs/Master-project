package org.exquisite.protege.ui.editor.repair.dataproperty;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataRangeEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

/**
 * Repair editor for Data Property Ranges.
 *
 * <p>A data property range axiom DataPropertyRange( DPE DR ) states that the range of the data property expression DPE is
 * the data range DR — that is, if some individual is connected by DPE with a literal x, then x is in DR.
 * The arity of DR must be one. Each such axiom can be seen as a syntactic shortcut for the following axiom:
 * SubClassOf( owl:Thing DataAllValuesFrom( DPE DR ) )</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Data_Property_Range">9.3.5 Data Property Range</a>
 * @see org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyRangeFrameSectionRow
 * @author wolfi
 */
public class OWLDataPropertyRangeEditor extends AbstractOWLObjectRepairEditor<OWLDataProperty, OWLDataPropertyRangeAxiom, OWLDataRange> {

    public OWLDataPropertyRangeEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLDataPropertyRangeAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLDataRange> getOWLObjectEditor() {
        OWLDataRangeEditor editor = new OWLDataRangeEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom().getRange());
        return editor;
    }

    @Override
    public OWLDataPropertyRangeAxiom createAxiom(OWLDataRange editedObject) {
        return getOWLDataFactory().getOWLDataPropertyRangeAxiom(getRootObject(), editedObject);
    }

    @Override
    public void setAxiom(OWLDataPropertyRangeAxiom axiom) {
        super.setAxiom(axiom);
        rootObject = axiom.getProperty().asOWLDataProperty();
    }
}
