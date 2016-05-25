package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.model.error.QueryErrorHandler;
import org.exquisite.protege.ui.view.QueryView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GetQueryButton extends AbstractGuiButton {

    public GetQueryButton(final QueryView queryView) {
        super("Get Query","Get Query", "Query2.png", KeyEvent.VK_Q,
                new AbstractAction(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        queryView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().doGetQuery(new QueryErrorHandler());
                    }
                }
        );

    }
}
