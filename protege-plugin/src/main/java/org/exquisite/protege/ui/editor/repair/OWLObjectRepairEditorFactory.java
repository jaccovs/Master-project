package org.exquisite.protege.ui.editor.repair;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.*;

import java.awt.*;

/**
 * Factory generating the appropriate editor for the type of axiom.
 *
 * @author wolfi
 */
public class OWLObjectRepairEditorFactory {

    public static AbstractOWLObjectRepairEditor createRepairEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLAxiom axiom, OWLObjectEditorHandler handler) {
        if (axiom instanceof OWLClassAxiom) {
            return new OWLGeneralClassAxiomRepairEditor(editorKit, parent, ontology, (OWLClassAxiom) axiom, handler);
        } else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
            return new OWLObjectPropertyDomainEditor(editorKit, parent, ontology, (OWLObjectPropertyDomainAxiom)axiom, handler);
        } else if (axiom instanceof OWLDataPropertyDomainAxiom) {
            return new OWLDataPropertyDomainEditor(editorKit, parent, ontology, (OWLDataPropertyDomainAxiom)axiom, handler);
        } else if (axiom instanceof OWLClassAssertionAxiom) {
            return new NoRepairEditor(editorKit, parent, ontology, axiom, handler);
        } else {
            return new NoRepairEditor(editorKit, parent, ontology, axiom, handler);
        }
    }
}
