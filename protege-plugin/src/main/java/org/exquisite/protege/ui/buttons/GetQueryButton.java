package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.view.QueryView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 07.09.12
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
public class GetQueryButton extends AbstractGuiButton {

    public GetQueryButton(final QueryView queryView) {
        super("Get Query","Get Query", "Query2.png", KeyEvent.VK_Q,
                new AbstractAction(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //queryView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().doGetQuery(new QueryErrorHandler()); // TODO
                    }
                }
        );

    }
}
