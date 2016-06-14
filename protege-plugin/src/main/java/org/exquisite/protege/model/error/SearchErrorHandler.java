package org.exquisite.protege.model.error;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;

import javax.swing.*;

public class SearchErrorHandler extends ErrorHandler {

    @Override
    public void errorHappend(OntologyDiagnosisSearcher.ErrorStatus error) {
        switch (error) {
            case SOLVER_EXCEPTION:
                JOptionPane.showMessageDialog(null, "There are problems with the solver", "IReasoner Exception", JOptionPane.ERROR_MESSAGE);
                break;
            case INCONSISTENT_THEORY_EXCEPTION:
                JOptionPane.showMessageDialog(null, "The set of testcases itself is inconsistent with the theory.", "Inconsistent Theory Exception", JOptionPane.ERROR_MESSAGE);
                break;
            case NO_CONFLICT_EXCEPTION:
                JOptionPane.showMessageDialog(null, "There are no conflicts and therefore no diagnoses", "No Conflict Exception", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

}
