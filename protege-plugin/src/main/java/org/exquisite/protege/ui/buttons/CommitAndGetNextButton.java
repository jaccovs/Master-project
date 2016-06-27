package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;
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
                        queryView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().doCommitAndGetNewQuery(new QueryErrorHandler());
                    }
                }
        );

        final OntologyDiagnosisSearcher s = queryView.getEditorKitHook().getActiveOntologyDiagnosisSearcher();
        setEnabled(s.isSessionRunning() && s.sizeOfEntailedAndNonEntailedAxioms() > 0);

    }
}
