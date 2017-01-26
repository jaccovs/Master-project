package org.exquisite.protege.ui.list;

import org.exquisite.protege.Debugger;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

class QueryAxiomListItem extends AssertedOrInferredAxiomListItem {

    QueryAxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology, Debugger debugger) {
        super(axiom, ontology, debugger);
    }

}
