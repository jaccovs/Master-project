package org.exquisite.protege.ui.list.item;

import org.exquisite.protege.Debugger;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Axiom in query history classified as entailed or not entailed.
 */
public class AnswerHistoryAxiomListItem extends AssertedOrInferredAxiomListItem {

    private boolean isEntailed;

    public AnswerHistoryAxiomListItem(boolean isEntailed, OWLLogicalAxiom axiom, OWLOntology ontology, Debugger debugger) {
        super(axiom, ontology, debugger);
        this.isEntailed = isEntailed;
    }

    public boolean isEntailed() {
        return isEntailed;
    }
}
