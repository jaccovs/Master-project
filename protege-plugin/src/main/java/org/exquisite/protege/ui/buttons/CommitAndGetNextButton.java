package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.model.OntologyDebugger;
import org.exquisite.protege.model.error.QueryErrorHandler;
import org.exquisite.protege.ui.view.QueryView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CommitAndGetNextButton extends AbstractGuiButton {

    public CommitAndGetNextButton(final QueryView queryView) {
        super("Commit","Commit and Get New Query", "Next2.png", KeyEvent.VK_C ,
                new AbstractAction(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        queryView.getEditorKitHook().getActiveOntologyDebugger().doCommitAndGetNewQuery(new QueryErrorHandler());
                    }
                }
        );

        final OntologyDebugger debugger = queryView.getEditorKitHook().getActiveOntologyDebugger();
        setEnabled(debugger.isSessionRunning() && debugger.sizeOfEntailedAndNonEntailedAxioms() > 0);

    }
}
