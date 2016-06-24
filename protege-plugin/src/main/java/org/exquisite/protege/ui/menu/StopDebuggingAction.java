package org.exquisite.protege.ui.menu;

import java.awt.event.ActionEvent;

/**
 * Stop debugging session = reset engine, diagnosis, conflicts, queries and history.
 */
public class StopDebuggingAction extends AbstractProtegeOWLAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        getActiveOntologyDiagnosisSearcher().doStopDebugging();
    }

    @Override
    void updateState() {
        setEnabled(isSessionRunning());
    }

}
