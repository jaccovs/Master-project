package org.exquisite.protege.ui.editor.repair;

import org.exquisite.protege.ui.editor.repair.cls.OWLGeneralClassEditor;
import org.exquisite.protege.ui.editor.repair.dataproperty.OWLDataPropertyDomainEditor;
import org.exquisite.protege.ui.editor.repair.individual.*;
import org.exquisite.protege.ui.editor.repair.objectproperty.*;
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

        // ... general class expression axioms (@see https://www.w3.org/TR/owl2-syntax/#Class_Expression_Axioms)
        if (axiom instanceof OWLClassAxiom) {
            return new OWLGeneralClassEditor(editorKit, parent, ontology, (OWLClassAxiom) axiom, handler);

            // ... object property axioms (@see https://www.w3.org/TR/owl2-syntax/#Object_Property_Axioms)
        } else if (axiom instanceof OWLDisjointObjectPropertiesAxiom) {
            return new OWLDisjointObjectPropertiesEditor(editorKit, parent, ontology, (OWLDisjointObjectPropertiesAxiom)axiom, handler);
        } else if (axiom instanceof OWLEquivalentObjectPropertiesAxiom) {
            return new OWLEquivalentObjectPropertiesEditor(editorKit, parent, ontology, (OWLEquivalentObjectPropertiesAxiom)axiom, handler);
        } else if (axiom instanceof OWLInverseObjectPropertiesAxiom) {
            return new OWLInverseObjectPropertiesEditor(editorKit, parent, ontology, (OWLInverseObjectPropertiesAxiom)axiom, handler);
        } else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
            return new OWLObjectPropertyDomainEditor(editorKit, parent, ontology, (OWLObjectPropertyDomainAxiom)axiom, handler);
        } else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
            return new OWLObjectPropertyRangeEditor(editorKit, parent, ontology, (OWLObjectPropertyRangeAxiom)axiom, handler);
        } else if (axiom instanceof OWLSubPropertyChainOfAxiom) {
            return new OWLPropertyChainEditor(editorKit, parent, ontology, (OWLSubPropertyChainOfAxiom)axiom, handler);
        } else if (axiom instanceof OWLSubObjectPropertyOfAxiom) {
            return new OWLSubObjectPropertyEditor(editorKit, parent, ontology, (OWLSubObjectPropertyOfAxiom)axiom, handler);

        // individual assertions (@see https://www.w3.org/TR/owl2-syntax/#Assertions)
        } else if (axiom instanceof OWLClassAssertionAxiom) {
            return new OWLClassAssertionEditor(editorKit, parent, ontology, (OWLClassAssertionAxiom)axiom, handler);
        } else if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
            return new OWLObjectPropertyAssertionEditor(editorKit, parent, ontology, (OWLObjectPropertyAssertionAxiom)axiom, handler);
        } else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
            return new OWLDataPropertyAssertionEditor(editorKit, parent, ontology, (OWLDataPropertyAssertionAxiom)axiom, handler);
        } else if (axiom instanceof OWLSameIndividualAxiom) {
            return new OWLSameIndividualsEditor(editorKit, parent, ontology, (OWLSameIndividualAxiom)axiom, handler);
        } else if (axiom instanceof OWLDifferentIndividualsAxiom) {
            return new OWLDifferentIndividualsEditor(editorKit, parent, ontology, (OWLDifferentIndividualsAxiom)axiom, handler);
        } else if (axiom instanceof OWLNegativeObjectPropertyAssertionAxiom) {
            return new OWLNegativeObjectPropertyAssertionEditor(editorKit, parent, ontology, (OWLNegativeObjectPropertyAssertionAxiom)axiom, handler);
        } else if (axiom instanceof OWLNegativeDataPropertyAssertionAxiom) {
            return new OWLNegativeDataPropertyAssertionEditor(editorKit, parent, ontology, (OWLNegativeDataPropertyAssertionAxiom)axiom, handler);


        } else if (axiom instanceof OWLDataPropertyDomainAxiom) {
            return new OWLDataPropertyDomainEditor(editorKit, parent, ontology, (OWLDataPropertyDomainAxiom)axiom, handler);

            // .. or no editor for all other axiom types
        } else {
            return new NoRepairEditor(editorKit, parent, ontology, axiom, handler);
        }
    }
}
