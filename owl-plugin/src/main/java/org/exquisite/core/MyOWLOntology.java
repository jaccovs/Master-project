package org.exquisite.core;

import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.AxiomAnnotations;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.parameters.Navigation;
import org.semanticweb.owlapi.util.OWLAxiomSearchFilter;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyImpl;

import javax.annotation.Nonnull;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;

public class MyOWLOntology implements OWLOntology, Cloneable{

    private OWLOntology ontology;

    @Override
    public MyOWLOntology clone() {
        try {
            return (MyOWLOntology) super.clone();
        } catch (Exception E) {
            System.out.println("error");
            return null;
        }
    }

    public void setOntology(OWLOntology ont){
        ontology = ont;
    }

    public OWLOntology getOntology(){
        return ontology;
    }

    @Override
    public void accept(@Nonnull OWLNamedObjectVisitor owlNamedObjectVisitor) {

    }

    @Nonnull
    @Override
    public <O> O accept(@Nonnull OWLNamedObjectVisitorEx<O> owlNamedObjectVisitorEx) {
        return null;
    }

    @Nonnull
    @Override
    public OWLOntologyManager getOWLOntologyManager() {
        return null;
    }

    @Override
    public void setOWLOntologyManager(OWLOntologyManager owlOntologyManager) {

    }

    @Nonnull
    @Override
    public OWLOntologyID getOntologyID() {
        return null;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotation> getAnnotations() {
        return null;
    }

    @Nonnull
    @Override
    public Set<IRI> getDirectImportsDocuments() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLOntology> getDirectImports() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLOntology> getImports() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLOntology> getImportsClosure() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLImportsDeclaration> getImportsDeclarations() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getTBoxAxioms(@Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getABoxAxioms(@Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getRBoxAxioms(@Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLClassAxiom> getGeneralClassAxioms() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLEntity> getSignature() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLEntity> getSignature(@Nonnull Imports imports) {
        return null;
    }

    @Override
    public boolean isDeclared(@Nonnull OWLEntity owlEntity) {
        return false;
    }

    @Override
    public boolean isDeclared(@Nonnull OWLEntity owlEntity, @Nonnull Imports imports) {
        return false;
    }

    @Override
    public void saveOntology() throws OWLOntologyStorageException {

    }

    @Override
    public void saveOntology(@Nonnull IRI iri) throws OWLOntologyStorageException {

    }

    @Override
    public void saveOntology(@Nonnull OutputStream outputStream) throws OWLOntologyStorageException {

    }

    @Override
    public void saveOntology(@Nonnull OWLDocumentFormat owlDocumentFormat) throws OWLOntologyStorageException {

    }

    @Override
    public void saveOntology(@Nonnull OWLDocumentFormat owlDocumentFormat, @Nonnull IRI iri) throws OWLOntologyStorageException {

    }

    @Override
    public void saveOntology(@Nonnull OWLDocumentFormat owlDocumentFormat, @Nonnull OutputStream outputStream) throws OWLOntologyStorageException {

    }

    @Override
    public void saveOntology(@Nonnull OWLOntologyDocumentTarget owlOntologyDocumentTarget) throws OWLOntologyStorageException {

    }

    @Override
    public void saveOntology(@Nonnull OWLDocumentFormat owlDocumentFormat, @Nonnull OWLOntologyDocumentTarget owlOntologyDocumentTarget) throws OWLOntologyStorageException {

    }

    @Override
    public int compareTo(OWLObject o) {
        return 0;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnonymousIndividual> getAnonymousIndividuals() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLClass> getClassesInSignature() {
        return null;
    }

    @Override
    public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity) {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLDatatype> getDatatypesInSignature() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLEntity> getEntitiesInSignature(@Nonnull IRI iri) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLNamedIndividual> getIndividualsInSignature() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLObjectProperty> getObjectPropertiesInSignature() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getAxioms(@Nonnull Imports imports) {
        return null;
    }

    @Override
    public int getAxiomCount(@Nonnull Imports imports) {
        return 0;
    }

    @Nonnull
    @Override
    public Set<OWLLogicalAxiom> getLogicalAxioms(@Nonnull Imports imports) {
        return null;
    }

    @Override
    public int getLogicalAxiomCount(@Nonnull Imports imports) {
        return 0;
    }

    @Nonnull
    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(@Nonnull AxiomType<T> axiomType, @Nonnull Imports imports) {
        return null;
    }

    @Override
    public <T extends OWLAxiom> int getAxiomCount(@Nonnull AxiomType<T> axiomType, @Nonnull Imports imports) {
        return 0;
    }

    @Override
    public boolean containsAxiom(@Nonnull OWLAxiom owlAxiom, @Nonnull Imports imports, @Nonnull AxiomAnnotations axiomAnnotations) {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getAxiomsIgnoreAnnotations(@Nonnull OWLAxiom owlAxiom, @Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getReferencingAxioms(@Nonnull OWLPrimitive owlPrimitive, @Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLClassAxiom> getAxioms(@Nonnull OWLClass owlClass, @Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLObjectPropertyAxiom> getAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression, @Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDataPropertyAxiom> getAxioms(@Nonnull OWLDataProperty owlDataProperty, @Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLIndividualAxiom> getAxioms(@Nonnull OWLIndividual owlIndividual, @Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotationAxiom> getAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty, @Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDatatypeDefinitionAxiom> getAxioms(@Nonnull OWLDatatype owlDatatype, @Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getAxioms(boolean b) {
        return null;
    }

    @Override
    public int getAxiomCount(boolean b) {
        return 0;
    }

    @Nonnull
    @Override
    public Set<OWLLogicalAxiom> getLogicalAxioms(boolean b) {
        return null;
    }

    @Override
    public int getLogicalAxiomCount(boolean b) {
        return 0;
    }

    @Nonnull
    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(@Nonnull AxiomType<T> axiomType, boolean b) {
        return null;
    }

    @Override
    public <T extends OWLAxiom> int getAxiomCount(@Nonnull AxiomType<T> axiomType, boolean b) {
        return 0;
    }

    @Override
    public boolean containsAxiom(@Nonnull OWLAxiom owlAxiom, boolean b) {
        return false;
    }

    @Override
    public boolean containsAxiomIgnoreAnnotations(@Nonnull OWLAxiom owlAxiom, boolean b) {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getAxiomsIgnoreAnnotations(@Nonnull OWLAxiom owlAxiom, boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getReferencingAxioms(@Nonnull OWLPrimitive owlPrimitive, boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLClassAxiom> getAxioms(@Nonnull OWLClass owlClass, boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLObjectPropertyAxiom> getAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDataPropertyAxiom> getAxioms(@Nonnull OWLDataProperty owlDataProperty, boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLIndividualAxiom> getAxioms(@Nonnull OWLIndividual owlIndividual, boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotationAxiom> getAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty, boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDatatypeDefinitionAxiom> getAxioms(@Nonnull OWLDatatype owlDatatype, boolean b) {
        return null;
    }

    @Override
    public int getAxiomCount() {
        return 0;
    }

    @Override
    public int getLogicalAxiomCount() {
        return 0;
    }

    @Override
    public <T extends OWLAxiom> int getAxiomCount(@Nonnull AxiomType<T> axiomType) {
        return 0;
    }

    @Override
    public boolean containsAxiomIgnoreAnnotations(@Nonnull OWLAxiom owlAxiom) {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getAxiomsIgnoreAnnotations(@Nonnull OWLAxiom owlAxiom) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getReferencingAxioms(@Nonnull OWLPrimitive owlPrimitive) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLClassAxiom> getAxioms(@Nonnull OWLClass owlClass) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLObjectPropertyAxiom> getAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDataPropertyAxiom> getAxioms(@Nonnull OWLDataProperty owlDataProperty) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLIndividualAxiom> getAxioms(@Nonnull OWLIndividual owlIndividual) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotationAxiom> getAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDatatypeDefinitionAxiom> getAxioms(@Nonnull OWLDatatype owlDatatype) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getAxioms() {
        return null;
    }

    @Nonnull
    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(@Nonnull AxiomType<T> axiomType) {
        return null;
    }

    @Override
    public boolean containsAxiom(@Nonnull OWLAxiom owlAxiom) {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLLogicalAxiom> getLogicalAxioms() {
        return null;
    }

    @Nonnull
    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(@Nonnull Class<T> aClass, @Nonnull OWLObject owlObject, @Nonnull Imports imports, @Nonnull Navigation navigation) {
        return null;
    }

    @Nonnull
    @Override
    public <T extends OWLAxiom> Collection<T> filterAxioms(@Nonnull OWLAxiomSearchFilter owlAxiomSearchFilter, @Nonnull Object o, @Nonnull Imports imports) {
        return null;
    }

    @Override
    public boolean contains(@Nonnull OWLAxiomSearchFilter owlAxiomSearchFilter, @Nonnull Object o, @Nonnull Imports imports) {
        return false;
    }

    @Nonnull
    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(@Nonnull Class<T> aClass, @Nonnull Class<? extends OWLObject> aClass1, @Nonnull OWLObject owlObject, @Nonnull Imports imports, @Nonnull Navigation navigation) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLSubAnnotationPropertyOfAxiom> getSubAnnotationPropertyOfAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDeclarationAxiom> getDeclarationAxioms(@Nonnull OWLEntity owlEntity) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(@Nonnull OWLAnnotationSubject owlAnnotationSubject) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSubClass(@Nonnull OWLClass owlClass) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSuperClass(@Nonnull OWLClass owlClass) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms(@Nonnull OWLClass owlClass) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms(@Nonnull OWLClass owlClass) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms(@Nonnull OWLClass owlClass) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLHasKeyAxiom> getHasKeyAxioms(@Nonnull OWLClass owlClass) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSubProperty(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSuperProperty(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLFunctionalObjectPropertyAxiom> getFunctionalObjectPropertyAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseFunctionalObjectPropertyAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLSymmetricObjectPropertyAxiom> getSymmetricObjectPropertyAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAsymmetricObjectPropertyAxiom> getAsymmetricObjectPropertyAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLReflexiveObjectPropertyAxiom> getReflexiveObjectPropertyAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLTransitiveObjectPropertyAxiom> getTransitiveObjectPropertyAxioms(@Nonnull OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSubProperty(@Nonnull OWLDataProperty owlDataProperty) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSuperProperty(@Nonnull OWLDataPropertyExpression owlDataPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(@Nonnull OWLDataProperty owlDataProperty) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(@Nonnull OWLDataProperty owlDataProperty) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxioms(@Nonnull OWLDataProperty owlDataProperty) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms(@Nonnull OWLDataProperty owlDataProperty) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLFunctionalDataPropertyAxiom> getFunctionalDataPropertyAxioms(@Nonnull OWLDataPropertyExpression owlDataPropertyExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(@Nonnull OWLIndividual owlIndividual) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(@Nonnull OWLClassExpression owlClassExpression) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms(@Nonnull OWLIndividual owlIndividual) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms(@Nonnull OWLIndividual owlIndividual) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms(@Nonnull OWLIndividual owlIndividual) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms(@Nonnull OWLIndividual owlIndividual) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLSameIndividualAxiom> getSameIndividualAxioms(@Nonnull OWLIndividual owlIndividual) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms(@Nonnull OWLIndividual owlIndividual) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDatatypeDefinitionAxiom> getDatatypeDefinitions(@Nonnull OWLDatatype owlDatatype) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLClassExpression> getNestedClassExpressions() {
        return null;
    }

    @Override
    public void accept(@Nonnull OWLObjectVisitor owlObjectVisitor) {

    }

    @Nonnull
    @Override
    public <O> O accept(@Nonnull OWLObjectVisitorEx<O> owlObjectVisitorEx) {
        return null;
    }

    @Override
    public boolean isTopEntity() {
        return false;
    }

    @Override
    public boolean isBottomEntity() {
        return false;
    }

    @Nonnull
    @Override
    public String toString() {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLClass> getClassesInSignature(@Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLObjectProperty> getObjectPropertiesInSignature(@Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDataProperty> getDataPropertiesInSignature(@Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLNamedIndividual> getIndividualsInSignature(@Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals(@Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDatatype> getDatatypesInSignature(@Nonnull Imports imports) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature(@Nonnull Imports imports) {
        return null;
    }

    @Override
    public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity, @Nonnull Imports imports) {
        return false;
    }

    @Override
    public boolean containsEntityInSignature(@Nonnull IRI iri, @Nonnull Imports imports) {
        return false;
    }

    @Override
    public boolean containsClassInSignature(@Nonnull IRI iri, @Nonnull Imports imports) {
        return false;
    }

    @Override
    public boolean containsObjectPropertyInSignature(@Nonnull IRI iri, @Nonnull Imports imports) {
        return false;
    }

    @Override
    public boolean containsDataPropertyInSignature(@Nonnull IRI iri, @Nonnull Imports imports) {
        return false;
    }

    @Override
    public boolean containsAnnotationPropertyInSignature(@Nonnull IRI iri, @Nonnull Imports imports) {
        return false;
    }

    @Override
    public boolean containsDatatypeInSignature(@Nonnull IRI iri, @Nonnull Imports imports) {
        return false;
    }

    @Override
    public boolean containsIndividualInSignature(@Nonnull IRI iri, @Nonnull Imports imports) {
        return false;
    }

    @Override
    public boolean containsDatatypeInSignature(@Nonnull IRI iri) {
        return false;
    }

    @Override
    public boolean containsEntityInSignature(@Nonnull IRI iri) {
        return false;
    }

    @Override
    public boolean containsClassInSignature(@Nonnull IRI iri) {
        return false;
    }

    @Override
    public boolean containsObjectPropertyInSignature(@Nonnull IRI iri) {
        return false;
    }

    @Override
    public boolean containsDataPropertyInSignature(@Nonnull IRI iri) {
        return false;
    }

    @Override
    public boolean containsAnnotationPropertyInSignature(@Nonnull IRI iri) {
        return false;
    }

    @Override
    public boolean containsIndividualInSignature(@Nonnull IRI iri) {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLEntity> getEntitiesInSignature(@Nonnull IRI iri, @Nonnull Imports imports) {
        return null;
    }

    @Override
    public Set<IRI> getPunnedIRIs(@Nonnull Imports imports) {
        return null;
    }

    @Override
    public boolean containsReference(@Nonnull OWLEntity owlEntity, @Nonnull Imports imports) {
        return false;
    }

    @Override
    public boolean containsReference(@Nonnull OWLEntity owlEntity) {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLClass> getClassesInSignature(boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLObjectProperty> getObjectPropertiesInSignature(boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDataProperty> getDataPropertiesInSignature(boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLNamedIndividual> getIndividualsInSignature(boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals(boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLDatatype> getDatatypesInSignature(boolean b) {
        return null;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature(boolean b) {
        return null;
    }

    @Override
    public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity, boolean b) {
        return false;
    }

    @Override
    public boolean containsEntityInSignature(@Nonnull IRI iri, boolean b) {
        return false;
    }

    @Override
    public boolean containsClassInSignature(@Nonnull IRI iri, boolean b) {
        return false;
    }

    @Override
    public boolean containsObjectPropertyInSignature(@Nonnull IRI iri, boolean b) {
        return false;
    }

    @Override
    public boolean containsDataPropertyInSignature(@Nonnull IRI iri, boolean b) {
        return false;
    }

    @Override
    public boolean containsAnnotationPropertyInSignature(@Nonnull IRI iri, boolean b) {
        return false;
    }

    @Override
    public boolean containsDatatypeInSignature(@Nonnull IRI iri, boolean b) {
        return false;
    }

    @Override
    public boolean containsIndividualInSignature(@Nonnull IRI iri, boolean b) {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLEntity> getEntitiesInSignature(@Nonnull IRI iri, boolean b) {
        return null;
    }

    @Override
    public boolean containsReference(@Nonnull OWLEntity owlEntity, boolean b) {
        return false;
    }

    @Nonnull
    @Override
    public Set<OWLDataProperty> getDataPropertiesInSignature() {
        return null;
    }
}
