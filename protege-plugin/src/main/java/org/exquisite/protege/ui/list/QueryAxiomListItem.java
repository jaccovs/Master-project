package org.exquisite.protege.ui.list;

import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class QueryAxiomListItem extends AxiomListItem {

    public QueryAxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology) {
        super(axiom, ontology);
    }

}
