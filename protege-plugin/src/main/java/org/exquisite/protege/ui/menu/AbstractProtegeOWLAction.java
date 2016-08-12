package org.exquisite.protege.ui.menu;

import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Abstract class to be extended for all Debugger menu items that depend
 */
abstract class AbstractProtegeOWLAction extends ProtegeOWLAction implements ChangeListener {

    /**
     * Get the active OntologyDiagnosisSearcher instance.
     * @return The active OntologyDiagnosisSearcher instance.
     */
    OntologyDiagnosisSearcher getActiveOntologyDiagnosisSearcher() {
        EditorKitHook editorKitHook = getOWLModelManager().get("org.exquisite.protege.EditorKitHook");
        return editorKitHook.getActiveOntologyDiagnosisSearcher();
    }

    /**
     * Returns the current state of the debugging session.
     * @return Either <code>true</code> if current active diagnosis searcher's DebuggingSession is in state STARTED or
     * <code>false</code> if the state is in STOPPED.
     */
    boolean isSessionRunning() {
        return getActiveOntologyDiagnosisSearcher().isSessionRunning();
    }

    @Override
    public void initialise() throws Exception {
        // notify this menu item when the ontology is changed.
        EditorKitHook editorKitHook = getOWLModelManager().get("org.exquisite.protege.EditorKitHook");
        editorKitHook.addActiveSearcherChangeListener(this);
        updateState();
    }

    @Override
    public void dispose() throws Exception {
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateState();
    }

    abstract void updateState();
}