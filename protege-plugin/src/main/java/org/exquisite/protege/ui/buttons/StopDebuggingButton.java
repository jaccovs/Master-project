package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.view.AbstractViewComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class StopDebuggingButton extends AbstractGuiButton {

    public StopDebuggingButton(final AbstractViewComponent toolboxView) {
        super("Stop","Stop the current running debugging session","player_stop.png",KeyEvent.VK_S,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final Debugger debugger = toolboxView.getEditorKitHook().getActiveOntologyDebugger();
                        debugger.doStopDebugging(Debugger.SessionStopReason.INVOKED_BY_USER);
                    }
                }
        );

        updateView(toolboxView.getEditorKitHook().getActiveOntologyDebugger());
    }

    public void updateView(Debugger debugger) {
        setEnabled(!debugger.isSessionStopped());
    }

}