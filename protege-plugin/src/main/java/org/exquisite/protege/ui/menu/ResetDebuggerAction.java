package org.exquisite.protege.ui.menu;

import org.exquisite.protege.model.OntologyDebugger;

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
            int answer = JOptionPane.showConfirmDialog(null, "Do you also want to delete the testcases?", "Reset Type", JOptionPane.YES_NO_CANCEL_OPTION);
            if (answer == JOptionPane.YES_OPTION)
                debugger.doResetDebugger();
            else if (answer == JOptionPane.NO_OPTION)
                debugger.doStopDebugging(OntologyDebugger.SessionStopReason.DEBUGGER_RESET);
        } else
            debugger.doResetDebugger();

        JOptionPane.showMessageDialog(null, "The debugger has been reset!", "OntologyDebugger reset", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    void updateState() {
        setEnabled(isSessionRunning());
    }
}
