package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.model.error.SearchErrorHandler;
import org.exquisite.protege.ui.view.DiagnosesView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class StartDebuggingButton extends AbstractGuiButton {

    public StartDebuggingButton(final DiagnosesView toolboxView) {
        super("Start Debugging","Start a new debugging session","Search.png",KeyEvent.VK_D,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().doStartDebugging(new SearchErrorHandler());
                    }
                }
        );

        setEnabled(!toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().isSessionRunning());
    }

}
