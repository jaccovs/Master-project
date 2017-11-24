package org.exquisite.protege.ui.list.item;

import org.exquisite.protege.Debugger;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author wolfi
 */
public class RepairTestcaseListItem extends TestcaseListItem {

    public RepairTestcaseListItem(OWLLogicalAxiom axiom, Debugger.TestcaseType type, OWLOntology ontology, Debugger debugger) {
        super(axiom, type, ontology, debugger);
    }

    @Override
    public boolean isDeleteable() {
        return false;
    }

    @Override
    public boolean isEditable() {
        return false;
    }
}
