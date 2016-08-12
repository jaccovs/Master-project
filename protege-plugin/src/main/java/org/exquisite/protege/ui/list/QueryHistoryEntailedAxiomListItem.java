package org.exquisite.protege.ui.list;

import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Axiom in query history classified as entailed.
 */
class QueryHistoryEntailedAxiomListItem extends AxiomListItem {

    QueryHistoryEntailedAxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology) {
        super(axiom, ontology);
    }

    @Override
    public String getTooltip() {
        return "Classified as entailed";
    }
}
