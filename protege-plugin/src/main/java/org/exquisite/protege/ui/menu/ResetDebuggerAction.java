package org.exquisite.protege.ui.menu;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.dialog.DebuggingDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Reset debugging session: stop debugging + reset test cases.
 */
public class ResetDebuggerAction extends AbstractProtegeOWLAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        Debugger debugger = getActiveOntologyDebugger();

        if (!debugger.areTestcasesEmpty()) {
            int answer = DebuggingDialog.showConfirmDialog("Do you also want to delete the testcases?", "Reset Type");
            if (answer == JOptionPane.YES_OPTION)
                debugger.doResetDebugger();
            else if (answer == JOptionPane.NO_OPTION)
                debugger.doStopDebugging(Debugger.SessionStopReason.DEBUGGER_RESET);
        } else
            debugger.doResetDebugger();

        DebuggingDialog.showDebuggerResetMessage();
    }

    @Override
    void updateState() {
        setEnabled(isSessionRunning());
    }
}
