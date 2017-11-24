package org.exquisite.protege.ui.editor.repair.cls;

import org.exquisite.protege.ui.editor.repair.AbstractOWLObjectRepairEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLGeneralAxiomEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

/**
 * Repair editor for Class Expression Axioms.
 * Includes expressions for SubClassOf | EquivalentClasses | DisjointClasses | DisjointUnion.
 *
 * <p>OWL 2 provides axioms that allow relationships to be established between class expressions.
 * The <strong>SubClassOf</strong> axiom allows one to state that each instance of one class expression is also an
 * instance of another class expression, and thus to construct a hierarchy of classes.
 * The <strong>EquivalentClasses</strong> axiom allows one to state that several class expressions are equivalent to
 * each other. The <strong>DisjointClasses</strong> axiom allows one to state that several class expressions are
 * pairwise disjoint — that is, that they have no instances in common.
 * Finally, the <strong>DisjointUnion</strong> class expression allows one to define a class as a disjoint union of
 * several class expressions and thus to express covering constraints.</p>
 *
 * @see <a href="https://www.w3.org/TR/owl2-syntax/#Class_Expression_Axioms">9.1 Class Expression Axioms</a>
 * @see org.protege.editor.owl.ui.frame.cls.OWLClassGeneralClassAxiomFrameSectionRow
 * @author wolfi
 */
public class OWLGeneralClassEditor extends AbstractOWLObjectRepairEditor<OWLClass, OWLClassAxiom, OWLClassAxiom> {

    public OWLGeneralClassEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLClassAxiom axiom, OWLObjectEditorHandler handler) {
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
