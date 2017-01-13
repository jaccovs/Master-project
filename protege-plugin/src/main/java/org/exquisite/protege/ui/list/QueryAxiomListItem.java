package org.exquisite.protege.ui.list;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.model.QueryExplanation;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

class QueryAxiomListItem extends AxiomListItem {

    private Debugger debugger;

    QueryAxiomListItem(OWLLogicalAxiom axiom, OWLOntology ontology, Debugger debugger) {
        super(axiom, ontology);
        this.debugger = debugger;
    }

    @Override
    public String getTooltip() {
        if (QueryExplanation.isAxiomInferredFromDebugger(debugger, axiom)) {
            return "Inferred";
        }

        return super.getTooltip();
    }
}
