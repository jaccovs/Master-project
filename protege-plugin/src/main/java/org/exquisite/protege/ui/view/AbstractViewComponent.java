package org.exquisite.protege.ui.view;

import org.exquisite.protege.EditorKitHook;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import javax.swing.event.ChangeListener;

/**
 * The top level view component for the debugger plugin.
 * Each debugger view inherits from this abstract class.
 */
public abstract class AbstractViewComponent extends AbstractOWLViewComponent implements ChangeListener {

    private EditorKitHook editorKitHook;

    public EditorKitHook getEditorKitHook() {
        return editorKitHook;
    }

    @Override
    protected void initialiseOWLView() throws Exception {
        editorKitHook = (EditorKitHook) getOWLEditorKit().get("org.exquisite.protege.EditorKitHook");
        getEditorKitHook().addActiveDebuggerChangeListener(this);
    }

    @Override
    protected void disposeOWLView() {
        getEditorKitHook().removeActiveDebuggerChangeListener(this);
        this.editorKitHook = null;
    }

}
