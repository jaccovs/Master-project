package org.exquisite.protege.model.search;

import org.exquisite.protege.EditorKitHook;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchMetadataImportContext;

class DebuggerSearchMetadataImportContext extends SearchMetadataImportContext {

    private EditorKitHook editorKitHook;

    DebuggerSearchMetadataImportContext(OWLEditorKit editorKit) {
        super(editorKit);
        this.editorKitHook = editorKit.getOWLModelManager().get("org.exquisite.protege.EditorKitHook");
    }

    public EditorKitHook getEditorKitHook() {
        return editorKitHook;
    }
}
