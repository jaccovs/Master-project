package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.model.OntologyDebugger;
import org.exquisite.protege.model.error.QueryErrorHandler;
import org.exquisite.protege.ui.view.AbstractQueryViewComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class StartDebuggingButton extends AbstractGuiButton {

    public StartDebuggingButton(final AbstractQueryViewComponent toolboxView) {
        super("Start","Start a new debugging session","player_play.png",KeyEvent.VK_D,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        final OntologyDebugger debugger = toolboxView.getEditorKitHook().getActiveOntologyDebugger();

                        if (debugger.isSessionStopped()) {
                            debugger.doStartDebugging(new QueryErrorHandler());
                        } else if (debugger.isSessionRunning()) {
                            debugger.doRestartDebugging(new QueryErrorHandler());
                        }
                    }

                }
        );
    }

    public void updateView(OntologyDebugger debugger) {
        if (debugger.isSessionStopped()) {
            setIcon(loadCustomIcon("player_play.png"));
            setText("Start");
            setToolTipText("Start a new debugging session");
        } else if (debugger.isSessionRunning()) {
            setIcon(loadCustomIcon("player_rewind.png"));
            setText("Restart");
            setToolTipText("Stops current session and starts a new debugging session");
        }
    }

}