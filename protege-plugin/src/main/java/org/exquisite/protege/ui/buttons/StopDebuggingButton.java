package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.ui.view.DiagnosesView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class StopDebuggingButton extends AbstractGuiButton {

    public StopDebuggingButton(final DiagnosesView toolboxView) {
        super("Stop Debugging","Stop current debugging session","player_stop.png",KeyEvent.VK_S,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().doStopDebugging(OntologyDiagnosisSearcher.SessionStopReason.INVOKED_BY_USER);
                    }
                }
        );

        setEnabled(toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().isSessionRunning());
    }

    public void updateView(OntologyDiagnosisSearcher debugger) {
        if (debugger.isSessionStopped()) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
    }

}