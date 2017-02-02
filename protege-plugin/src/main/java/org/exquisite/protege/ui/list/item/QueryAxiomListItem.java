package org.exquisite.protege.ui.list.item;

import org.exquisite.protege.Debugger;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 *     A query item in the query view to be classified as entailed or non-entailed.
 * </p>
 *
 * @author wolfi
 */
public class QueryAxiomListItem extends AssertedOrInferredAxiomListItem {

    public QueryAxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology, Debugger debugger) {
        super(axiom, ontology, debugger);
    }

}
