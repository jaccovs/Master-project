package org.exquisite.protege.ui.view;

import org.exquisite.protege.EditorKitHook;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import javax.swing.event.ChangeListener;

public abstract class AbstractQueryViewComponent extends AbstractOWLViewComponent implements ChangeListener {

    private EditorKitHook editorKitHook;

    public EditorKitHook getEditorKitHook() {
        return editorKitHook;
    }

    @Override
    protected void initialiseOWLView() throws Exception {
        addActiveSearcherListener();
    }

    private void addActiveSearcherListener() {
        editorKitHook = (EditorKitHook)
                getOWLEditorKit().get("org.exquisite.protege.EditorKitHook");
        getEditorKitHook().addActiveDebuggerChangeListener(this);
    }

    @Override
    protected void disposeOWLView() {
        getEditorKitHook().removeActiveDebuggerChangeListener(this);
    }


}
