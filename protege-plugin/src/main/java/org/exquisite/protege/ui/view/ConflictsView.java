package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.model.configuration.DiagnosisEngineFactory;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.slf4j.LoggerFactory;

import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.Set;

public class ConflictsView extends AbstractAxiomSetView {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(ConflictsView.class.getName());

    @Override
    public void stateChanged(ChangeEvent e) {

        DiagnosisEngineFactory diagnosisEngineFactory = ((OntologyDiagnosisSearcher) e.getSource()).getDiagnosisEngineFactory();

        Set<Set<OWLLogicalAxiom>> conflicts = diagnosisEngineFactory.getDiagnosisEngine().getConflicts();
        logger.debug("updateView: got conflicts: " + conflicts);
        updateList(conflicts);
    }

    @Override
    protected Color getHeaderColor() {
        return new Color(52, 79, 255, 139);
    }

    @Override
    protected String getHeaderPrefix() {
        return "Conflict ";
    }

}
