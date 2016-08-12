package org.exquisite.protege.ui.list;

import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Axiom in query history classified as non entailed.
 */
class QueryHistoryNonEntailedAxiomListItem extends AxiomListItem {

    QueryHistoryNonEntailedAxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology) {
        super(axiom, ontology);
    }

    @Override
    public String getTooltip() {
        return "Classified as non entailed";
    }
}
