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
                        final OntologyDiagnosisSearcher debugger = toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher();
                        debugger.doStopDebugging(OntologyDiagnosisSearcher.SessionStopReason.INVOKED_BY_USER);
                    }
                }
        );

        updateView(toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher());
    }

    public void updateView(OntologyDiagnosisSearcher debugger) {
        setEnabled(!debugger.isSessionStopped());
    }

}