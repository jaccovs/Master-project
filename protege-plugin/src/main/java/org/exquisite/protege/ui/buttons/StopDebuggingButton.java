package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.model.OntologyDebugger;
import org.exquisite.protege.ui.view.AbstractQueryViewComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class StopDebuggingButton extends AbstractGuiButton {

    public StopDebuggingButton(final AbstractQueryViewComponent toolboxView) {
        super("Stop Debugging","Stop current debugging session","player_stop.png",KeyEvent.VK_S,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final OntologyDebugger debugger = toolboxView.getEditorKitHook().getActiveOntologyDebugger();
                        debugger.doStopDebugging(OntologyDebugger.SessionStopReason.INVOKED_BY_USER);
                    }
                }
        );

        updateView(toolboxView.getEditorKitHook().getActiveOntologyDebugger());
    }

    public void updateView(OntologyDebugger debugger) {
        setEnabled(!debugger.isSessionStopped());
    }

}