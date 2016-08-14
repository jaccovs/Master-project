package org.exquisite.protege.ui.menu;

import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTabPlugin;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

import java.awt.event.ActionEvent;

/**
 * This menu entry opens the debugger's workspace tab.
 */
public class OpenDebuggerTabAction extends ProtegeOWLAction {

    public String getViewId() {
        return "org.exquisite.protege.OntologyDebugging";
    }

    @Override
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
    public void initialise() throws Exception {}

    @Override
    public void dispose() throws Exception {}
}
