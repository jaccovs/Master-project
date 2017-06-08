package org.exquisite.protege.ui.list.handler;

import org.exquisite.core.Utils;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.List;

/**
 * A transfer handler for the moving axioms from correct to possibly faulty axioms via drag and drop.
 *
 * @author wolfi
 */
public class CorrectAxiomsTransferHandler extends AbstractAxiomsTransferHandler {

    /**
     * A transfer handler for the moving axioms from correct to possibly faulty axioms via drag and drop.
     *
     * @param debugger the debugger instance.
     */
    public CorrectAxiomsTransferHandler(Debugger debugger) {
        super(debugger);
    }

    @Override
    protected void moveSelectedAxioms(List<AxiomListItem> selectedAxioms) {
        this.debugger.moveToPossiblyFaultyAxioms(selectedAxioms);
    }

    @Override
    protected boolean canImport(List<OWLLogicalAxiom> selectedAxioms) {
        final DiagnosisModel<OWLLogicalAxiom> dm = debugger.getDiagnosisModel();
        return !Utils.hasIntersection(dm.getCorrectFormulas(), selectedAxioms)
                &&
                dm.getPossiblyFaultyFormulas().containsAll(selectedAxioms);
    }
}
