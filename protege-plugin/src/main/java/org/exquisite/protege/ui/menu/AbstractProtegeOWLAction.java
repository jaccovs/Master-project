package org.exquisite.protege.ui.menu;

import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.Debugger;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Abstract class to be extended for all OntologyDebugger menu items that depend.
 */
abstract class AbstractProtegeOWLAction extends ProtegeOWLAction implements ChangeListener {

    /**
     * Get the active OntologyDebugger instance.
     * @return The active OntologyDebugger instance.
     */
    Debugger getActiveOntologyDebugger() {
        EditorKitHook editorKitHook = getOWLModelManager().get("org.exquisite.protege.EditorKitHook");
        return editorKitHook.getActiveOntologyDebugger();
    }

    /**
     * Returns the current state of the debugging session.
     * @return Either <code>true</code> if current active diagnosis searcher's DebuggingSession is in state RUNNING or
     * <code>false</code> if the state is in STOPPED.
     */
    boolean isSessionRunning() {
        Debugger debugger = getActiveOntologyDebugger();
        return debugger != null && debugger.isSessionRunning();
    }

    @Override
    public void initialise() throws Exception {
        // notify this menu item when the ontology is changed.
        final EditorKitHook editorKitHook = getOWLModelManager().get("org.exquisite.protege.EditorKitHook");
        editorKitHook.addActiveDebuggerChangeListener(this);
        updateState();
    }

    @Override
    public void dispose() throws Exception {
        final EditorKitHook editorKitHook = getOWLModelManager().get("org.exquisite.protege.EditorKitHook");
        if (editorKitHook != null) editorKitHook.removeActiveDebuggerChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateState();
    }

    abstract void updateState();
}
