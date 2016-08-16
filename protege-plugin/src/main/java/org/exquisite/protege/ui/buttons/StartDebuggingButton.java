package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.model.error.QueryErrorHandler;
import org.exquisite.protege.ui.view.DiagnosesView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class StartDebuggingButton extends AbstractGuiButton {

    public StartDebuggingButton(final DiagnosesView toolboxView) {
        super("Start Debugging","Start a new debugging session","player_play.png",KeyEvent.VK_D,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        final OntologyDiagnosisSearcher debugger = toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher();

                        if (debugger.isSessionStopped()) {
                            debugger.doStartDebugging(new QueryErrorHandler());
                        } else if (debugger.isSessionRunning()) {
                            debugger.doPauseDebugging();
                        } else if (debugger.isSessionPaused()) {
                            debugger.doResumeDebugging();
                        }

                    }

                }
        );
    }

    public void updateView(OntologyDiagnosisSearcher debugger) {
        if (debugger.isSessionStopped()) {
            setIcon(loadCustomIcon("player_play.png"));
            setText("Start Debugging");
            setToolTipText("Start a new debugging session");
        } else if (debugger.isSessionRunning()) {
            setIcon(loadCustomIcon("player_pause.png"));
            setText("Pause Debugging");
            setToolTipText("Pause debugging session");
        } else if (debugger.isSessionPaused()) {
            setIcon(loadCustomIcon("player_play.png"));
            setText("Resume Debugging");
            setToolTipText("Resume the paused debugging session");
        }
    }

}