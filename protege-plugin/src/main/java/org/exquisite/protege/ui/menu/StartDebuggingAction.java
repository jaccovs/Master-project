package org.exquisite.protege.ui.menu;

import org.exquisite.protege.model.DebuggingSession;
import org.exquisite.protege.model.error.SearchErrorHandler;

import java.awt.event.ActionEvent;

/**
 * Starts a debugging session: computes diagnoses and computes queries.
 */
public class StartDebuggingAction extends AbstractProtegeOWLAction {

    public StartDebuggingAction() {
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getActiveOntologyDiagnosisSearcher().doStartDebugging(new SearchErrorHandler());
    }

    @Override
    void updateState() {
        setEnabled(!isSessionRunning());
    }
}
