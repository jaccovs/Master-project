package org.exquisite.protege.model.search;

import org.exquisite.protege.model.EditorKitHook;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchMetadataImportContext;

/**
 * @author wolfi
 */
public class DebuggerSearchMetadataImportContext extends SearchMetadataImportContext {

    private EditorKitHook editorKitHook;

    public DebuggerSearchMetadataImportContext(OWLEditorKit editorKit) {
        super(editorKit);
        this.editorKitHook = editorKit.getOWLModelManager().get("org.exquisite.protege.EditorKitHook");
    }

    public EditorKitHook getEditorKitHook() {
        return editorKitHook;
    }
}
