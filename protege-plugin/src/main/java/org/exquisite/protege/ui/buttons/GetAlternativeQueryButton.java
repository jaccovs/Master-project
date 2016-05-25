package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.view.QueryView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GetAlternativeQueryButton extends AbstractGuiButton {

    public GetAlternativeQueryButton(final QueryView queryView) {
        super("Get Alternative Query","If you don't want to answer the actual query, there is no correct answer or you don't know you can get a new quey", "next.png", KeyEvent.VK_A,
                new AbstractAction(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        queryView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().doGetAlternativeQuery();
                    }
                }
        );

    }

}
