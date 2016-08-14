package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.model.error.QueryErrorHandler;
import org.exquisite.protege.ui.view.DiagnosesView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * This button starts a new debugging session.
 */
public class StartDebuggingButton extends AbstractGuiButton {

    public StartDebuggingButton(final DiagnosesView toolboxView) {
        super("Start Debugging","Start a new debugging session","Search.png",KeyEvent.VK_D,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().doStartDebugging(new QueryErrorHandler());
                    }
                }
        );

        setEnabled(!toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().isSessionRunning());
    }
}
