package org.exquisite.protege.model.error;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.ErrorStatus;

import javax.swing.*;

public class SearchErrorHandler extends AbstractErrorHandler {

    @Override
    public void errorHappened(ErrorStatus error, Exception ex) {
        switch (error) {
            case SOLVER_EXCEPTION:
                showMessageDialog(null, "There are problems with the solver", "IReasoner Exception", JOptionPane.ERROR_MESSAGE, ex);
                break;
            case INCONSISTENT_THEORY_EXCEPTION:
                showMessageDialog(null, "The set of testcases itself is inconsistent with the theory.", "Inconsistent Theory Exception", JOptionPane.ERROR_MESSAGE, ex);
                break;
            case NO_CONFLICT_EXCEPTION:
                showMessageDialog(null, "There are no conflicts and therefore no diagnoses", "No Conflict Exception", JOptionPane.INFORMATION_MESSAGE, ex);
                break;
        }
    }

}
