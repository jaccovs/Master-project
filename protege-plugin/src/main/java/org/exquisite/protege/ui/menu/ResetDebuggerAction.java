package org.exquisite.protege.ui.menu;

import org.exquisite.protege.model.OntologyDebugger;
import org.exquisite.protege.ui.dialog.DebuggingDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Reset debugging session: stop debugging + reset test cases.
 */
public class ResetDebuggerAction extends AbstractProtegeOWLAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        OntologyDebugger debugger = getActiveOntologyDebugger();

        if (!debugger.areTestcasesEmpty()) {
            int answer = DebuggingDialog.showConfirmDialog("Do you also want to delete the testcases?", "Reset Type");
            if (answer == JOptionPane.YES_OPTION)
                debugger.doResetDebugger();
            else if (answer == JOptionPane.NO_OPTION)
                debugger.doStopDebugging(OntologyDebugger.SessionStopReason.DEBUGGER_RESET);
        } else
            debugger.doResetDebugger();

        DebuggingDialog.showDebuggerResetMessage();
    }

    @Override
    void updateState() {
        setEnabled(isSessionRunning());
    }
}
