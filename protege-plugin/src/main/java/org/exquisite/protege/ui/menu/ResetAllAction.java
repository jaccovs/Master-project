package org.exquisite.protege.ui.menu;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Resets all = Reset debugging session + reload of ontology
 */
public class ResetAllAction extends AbstractProtegeOWLAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        OntologyDiagnosisSearcher ods = getActiveOntologyDiagnosisSearcher();

        int answer = JOptionPane.showConfirmDialog(null, "The ontology will be reloaded and the debugger will be fully reset!<br>Please confirm this!", "Reset Type", JOptionPane.YES_NO_CANCEL_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            try {
                ods.doResetAll();
                JOptionPane.showMessageDialog(null, "The debugger has been fully reset!", "Debugger full reset", JOptionPane.INFORMATION_MESSAGE);
            } catch (OWLOntologyCreationException e1) {
                JOptionPane.showMessageDialog(null, "The Reset operation failed! Reason: " + e1.getMessage(), "Debugger full reset failure!", JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }

    @Override
    void updateState() {
        setEnabled(isSessionRunning());
    }
}
