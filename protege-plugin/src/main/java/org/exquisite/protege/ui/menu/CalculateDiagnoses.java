package org.exquisite.protege.ui.menu;

import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.model.error.SearchErrorHandler;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

import java.awt.event.ActionEvent;

public class CalculateDiagnoses extends ProtegeOWLAction {

    @Override
    public void actionPerformed(ActionEvent e) {

        EditorKitHook editorKitHook = getOWLModelManager(). get("org.exquisite.protege.EditorKitHook");
        OntologyDiagnosisSearcher ods = editorKitHook.getActiveOntologyDiagnosisSearcher();
        ods.doCalculateDiagnosis(new SearchErrorHandler());
    }

    @Override
    public void initialise() throws Exception {
    }

    @Override
    public void dispose() throws Exception {
    }

}
