package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.EditorKitHook;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import javax.swing.event.ChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 04.09.12
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractQueryViewComponent extends AbstractOWLViewComponent implements ChangeListener {

    private EditorKitHook editorKitHook;

    public EditorKitHook getEditorKitHook() {
        return editorKitHook;
    }

    @Override
    protected void initialiseOWLView() throws Exception {
        addActiveSearcherListener();
    }

    protected void addActiveSearcherListener() {
        editorKitHook = (EditorKitHook)
                getOWLEditorKit().get("org.exquisite.protege.EditorKitHook");
        getEditorKitHook().addActiveSearcherChangeListener(this);
    }

    @Override
    protected void disposeOWLView() {
        getEditorKitHook().removeActiveSearcherChangeListener(this);
    }


}
