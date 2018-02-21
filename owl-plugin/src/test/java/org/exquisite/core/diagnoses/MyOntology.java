package org.exquisite.core.diagnoses;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Set;

public class MyOntology {

    String ontologyName;

    OWLOntology ontology;

    OWLOntologyManager manager;

    IRI iri;

    MyOntology(String ontName) throws Exception{
        ontologyName = ontName;
        iri = IRI.create(ontologyName);
        manager = OWLManager.createOWLOntologyManager();
    }

    MyOntology(String ontName, Set<OWLOntology> ontologies) throws Exception{
        ontologyName = ontName;
        iri = IRI.create(ontologyName);
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.createOntology(iri, ontologies);
    }

    public void setOntology (OWLOntology ont){
        ontology = ont;
    }

    public void setManager (OWLOntologyManager man){
        manager = man;
    }

    public void setIRI (IRI x){
        iri = x;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public IRI getIri() {
        return iri;
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public String getOntologyName() {
        return ontologyName;
    }
}
