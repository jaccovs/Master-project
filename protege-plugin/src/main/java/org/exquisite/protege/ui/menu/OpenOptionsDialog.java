package org.exquisite.protege.ui.menu;

import org.protege.editor.core.ui.preferences.PreferencesDialogPanel;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

import java.awt.event.ActionEvent;

public class OpenOptionsDialog extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {

        PreferencesDialogPanel.showPreferencesDialog("Debugger",getOWLEditorKit());

    }

    @Override
    public void initialise() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
