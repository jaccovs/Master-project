package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.model.error.SearchErrorHandler;
import org.exquisite.protege.ui.view.DiagnosesView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class SearchDiagnosesButton extends AbstractGuiButton {

    public SearchDiagnosesButton(final DiagnosesView toolboxView) {
        super("Search Diagnoses","start to calculate diagnoses","Search.png",KeyEvent.VK_D,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toolboxView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().doCalculateDiagnosis(new SearchErrorHandler());
                    }
                }
        );

    }
}
