package org.exquisite.protege.ui.list;

import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

class QueryAxiomListItem extends AxiomListItem {

    QueryAxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology) {
        super(axiom, ontology);
    }

}
