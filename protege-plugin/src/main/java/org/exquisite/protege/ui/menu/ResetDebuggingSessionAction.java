package org.exquisite.protege.ui.menu;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Reset debugging session: stop debugging + reset test cases.
 */
public class ResetDebuggingSessionAction extends AbstractProtegeOWLAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        OntologyDiagnosisSearcher ods = getActiveOntologyDiagnosisSearcher();

        if (!ods.areTestcasesEmpty()) {
            int answer = JOptionPane.showConfirmDialog(null, "Do you also want to delete the testcases?", "Reset Type", JOptionPane.YES_NO_CANCEL_OPTION);
            if (answer == JOptionPane.YES_OPTION)
                ods.doResetDebugger();
            else if (answer == JOptionPane.NO_OPTION)
                ods.doStopDebugging();
        } else
            ods.doResetDebugger();

        JOptionPane.showMessageDialog(null, "The debugger has been reset!", "Debugger reset", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    void updateState() {
        setEnabled(isSessionRunning());
    }
}
