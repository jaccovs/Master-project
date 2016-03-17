package org.exquisite.protege.model;

import org.protege.editor.owl.OWLEditorKit;

/**
 * Created by wolfi on 17.03.2016.
 */
public class OntologyDiagnosisSearcher {

    public static enum QuerySearchStatus { IDLE, SEARCH_DIAG, GENERATING_QUERY, MINIMZE_QUERY, ASKING_QUERY }

    private QuerySearchStatus querySearchStatus = QuerySearchStatus.IDLE;


    public OntologyDiagnosisSearcher(OWLEditorKit editorKit) {
    }

    public void addChangeListener(EditorKitHook editorKitHook) {
    }

    public void removeChangeListener(EditorKitHook editorKitHook) {
    }

    public QuerySearchStatus getQuerySearchStatus() {
        return querySearchStatus;
    }
}
