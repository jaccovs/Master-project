package org.exquisite.protege.ui.menu;

import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTabPlugin;
import org.protege.editor.owl.model.OWLWorkspace;

import java.awt.event.ActionEvent;

abstract class OpenTabAction extends ProtegeAction {

    public abstract String getViewId();

    public void actionPerformed(ActionEvent e) {

        org.protege.editor.core.ui.workspace.WorkspaceTab tab = null;
        TabbedWorkspace tabbedWorkspace = (TabbedWorkspace)getWorkspace();

        if (tabbedWorkspace.containsTab(getViewId())) {
            tab = (((OWLWorkspace)getWorkspace()).getWorkspaceTab(getViewId()));
        }
        else {
            for (WorkspaceTabPlugin plugin : tabbedWorkspace.getOrderedPlugins())
                if (plugin.getId().equals(getViewId())) {
                    tab = tabbedWorkspace.addTabForPlugin(plugin);
                    break;
                }

        }
        tabbedWorkspace.setSelectedTab(tab);
    }

    @Override
    public void initialise() throws Exception {
    }

    @Override
    public void dispose() throws Exception {
    }

}
