package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.view.DiagnosesView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class StopDebuggingButton extends AbstractGuiButton {

    public StopDebuggingButton(final DiagnosesView toolboxView) {
        super("Stop Debugging","Stop current debugging session","stop.png",KeyEvent.VK_S,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().doStopDebugging();
                    }
                }
        );

        setEnabled(toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().isSessionRunning());
    }

}
