package org.exquisite.protege.ui.menu;

import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Reset the ontology debugger.
 */
public class Reset extends ProtegeOWLAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        EditorKitHook editorKitHook = getOWLModelManager().get("org.exquisite.protege.EditorKitHook");
        OntologyDiagnosisSearcher ods = editorKitHook.getActiveOntologyDiagnosisSearcher();

        if (!ods.isTestcasesEmpty()) {
            int answer = JOptionPane.showConfirmDialog(null, "Do you also want to delete the testcases?", "Reset Type", JOptionPane.YES_NO_CANCEL_OPTION);
            if (answer == JOptionPane.YES_OPTION)
                ods.doFullReset();
            else if (answer == JOptionPane.NO_OPTION)
                ods.doReset();
        } else
            ods.doFullReset();

        JOptionPane.showMessageDialog(null, "The debugger has been reset!", "Debugger reset", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void initialise() throws Exception {}

    @Override
    public void dispose() throws Exception {}

}
