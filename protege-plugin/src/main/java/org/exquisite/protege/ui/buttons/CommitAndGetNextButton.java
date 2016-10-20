package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.model.error.QueryErrorHandler;
import org.exquisite.protege.ui.view.QueryView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CommitAndGetNextButton extends AbstractGuiButton {

    public CommitAndGetNextButton(final QueryView queryView) {
        super("Submit","Submit your answers", "Next2.png", KeyEvent.VK_C ,
                new AbstractAction(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        queryView.getEditorKitHook().getActiveOntologyDebugger().doCommitAndGetNewQuery(new QueryErrorHandler());
                    }
                }
        );

        final Debugger debugger = queryView.getEditorKitHook().getActiveOntologyDebugger();
        setEnabled(debugger.isSessionRunning() && debugger.sizeOfEntailedAndNonEntailedAxioms() > 0);

    }
}
