package org.exquisite.protege.ui.editor.repair;

import org.exquisite.protege.ui.editor.repair.cls.OWLGeneralClassAxiomEditor;
import org.exquisite.protege.ui.editor.repair.dataproperty.OWLDataPropertyDomainAxiomEditor;
import org.exquisite.protege.ui.editor.repair.individual.*;
import org.exquisite.protege.ui.editor.repair.objectproperty.OWLObjectPropertyDomainAxiomEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.semanticweb.owlapi.model.*;

import java.awt.*;

/**
 * Factory generating the appropriate editor for each supported axiom type.
 *
 * @author wolfi
 */
public class OWLObjectRepairEditorFactory {

    public static AbstractOWLObjectRepairEditor createRepairEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, OWLAxiom axiom, OWLObjectEditorHandler handler) {
        // this factory method returns an editor for ...

        // ... general class expression axioms
        if (axiom instanceof OWLClassAxiom) {
            return new OWLGeneralClassAxiomEditor(editorKit, parent, ontology, (OWLClassAxiom) axiom, handler);

        // individual assertions
        } else if (axiom instanceof OWLClassAssertionAxiom) {
            return new OWLClassAssertionEditor(editorKit, parent, ontology, (OWLClassAssertionAxiom) axiom, handler);
        } else if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
            return new OWLObjectPropertyAssertionEditor(editorKit, parent, ontology, (OWLObjectPropertyAssertionAxiom) axiom, handler);
        } else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
            return new OWLDataPropertyAssertionEditor(editorKit, parent, ontology, (OWLDataPropertyAssertionAxiom) axiom, handler);
        } else if (axiom instanceof OWLSameIndividualAxiom) {
            return new OWLSameIndividualsEditor(editorKit, parent, ontology, (OWLSameIndividualAxiom) axiom, handler);
        } else if (axiom instanceof OWLDifferentIndividualsAxiom) {
            return new OWLDifferentIndividualsEditor(editorKit, parent, ontology, (OWLDifferentIndividualsAxiom) axiom, handler);
        } else if (axiom instanceof OWLNegativeObjectPropertyAssertionAxiom) {
            return new OWLNegativeObjectPropertyAssertionEditor(editorKit, parent, ontology, (OWLNegativeObjectPropertyAssertionAxiom) axiom, handler);
        } else if (axiom instanceof OWLNegativeDataPropertyAssertionAxiom) {
            return new OWLNegativeDataPropertyAssertionEditor(editorKit, parent, ontology, (OWLNegativeDataPropertyAssertionAxiom) axiom, handler);

        } else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
            return new OWLObjectPropertyDomainAxiomEditor(editorKit, parent, ontology, (OWLObjectPropertyDomainAxiom)axiom, handler);
        } else if (axiom instanceof OWLDataPropertyDomainAxiom) {
            return new OWLDataPropertyDomainAxiomEditor(editorKit, parent, ontology, (OWLDataPropertyDomainAxiom)axiom, handler);

            // .. or no editor for all other axiom types
        } else {
            return new NoRepairEditor(editorKit, parent, ontology, axiom, handler);
        }
    }
}
