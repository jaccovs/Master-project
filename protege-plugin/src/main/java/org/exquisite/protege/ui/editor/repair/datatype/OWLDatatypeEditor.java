package org.exquisite.protege.ui.editor.repair.datatype;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataRangeEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

/**
 * Repair editor for Datatype Definitions.
 *
 * <p>A datatype definition DatatypeDefinition( DT DR ) defines a new datatype DT as being semantically equivalent to
 * the data range DR; the latter must be a unary data range. This axiom allows one to use the defined datatype DT as a
 * synonym for DR — that is, in any expression in the ontology containing such an axiom, DT can be replaced with DR
 * without affecting the meaning of the ontology. </p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Datatype_Definitions">9.4 Datatype Definitions</a>
 * @see org.protege.editor.owl.ui.frame.datatype.OWLDatatypeDefinitionFrameSectionRow
 * @author wolfi
 */
public class OWLDatatypeEditor extends AbstractOWLObjectRepairEditor<OWLDatatype, OWLDatatypeDefinitionAxiom, OWLDataRange> {

    public OWLDatatypeEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLDatatypeDefinitionAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLDataRange> getOWLObjectEditor() {
        OWLDataRangeEditor editor = new OWLDataRangeEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom().getDataRange());
        return editor;
    }

    @Override
    public OWLDatatypeDefinitionAxiom createAxiom(OWLDataRange editedObject) {
        return getOWLDataFactory().getOWLDatatypeDefinitionAxiom(getRootObject(), editedObject);
    }

}
