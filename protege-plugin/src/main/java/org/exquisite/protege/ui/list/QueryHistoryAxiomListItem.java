package org.exquisite.protege.ui.list;

import org.exquisite.protege.Debugger;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Axiom in query history classified as entailed.
 */
class QueryHistoryAxiomListItem extends AssertedOrInferredAxiomListItem {

    QueryHistoryAxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology, Debugger debugger) {
        super(axiom, ontology, debugger);
    }

}
