package org.exquisite.protege.ui.editor.repair.cls;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLGeneralAxiomEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

/**
 * Repair editor for Class Expression Axioms.
 * Includes expressions for SubClassOf | EquivalentClasses | DisjointClasses | DisjointUnion.
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Class_Expression_Axioms">9.1 Class Expression Axioms</a>
 * @see org.protege.editor.owl.ui.frame.cls.OWLClassGeneralClassAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLGeneralClassAxiomEditor extends AbstractOWLObjectRepairEditor<OWLClassAxiom, OWLClassAxiom> {

    public OWLGeneralClassAxiomEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLClassAxiom axiom, OWLObjectEditorHandler handler) {
        super(editorKit, parent, ontology, axiom, handler);
    }

    @Override
    public OWLObjectEditor<OWLClassAxiom> getOWLObjectEditor() {
        OWLGeneralAxiomEditor editor =  new OWLGeneralAxiomEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom());
        return editor;
    }

    @Override
    public OWLClassAxiom createAxiom(OWLClassAxiom editedObject) {
        return editedObject;
    }

}
