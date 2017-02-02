package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.model.error.QueryErrorHandler;
import org.exquisite.protege.ui.view.QueryView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class SubmitButton extends AbstractGuiButton {

    public SubmitButton(final QueryView queryView) {
        super("Submit","Submit your answers. At least one axiom must be answered with yes or no.", "Next2.png", KeyEvent.VK_C ,
                new AbstractAction(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        queryView.getEditorKitHook().getActiveOntologyDebugger().doCommitAndGetNewQueryAsync(new QueryErrorHandler());
                    }
                }
        );

        final Debugger debugger = queryView.getEditorKitHook().getActiveOntologyDebugger();
        setEnabled(debugger.isSessionRunning() && debugger.sizeOfEntailedAndNonEntailedAxioms() > 0);

    }
}
