package org.exquisite.protege.model.error;

import static org.exquisite.protege.model.OntologyDebugger.ErrorStatus;

import javax.swing.*;

public class SearchErrorHandler extends AbstractErrorHandler {

    @Override
    public void errorHappened(ErrorStatus error, Exception ex) {
        switch (error) {
            case SOLVER_EXCEPTION:
                showErrorDialog(null, "There are problems with the solver", "IReasoner Exception", JOptionPane.ERROR_MESSAGE, ex);
                break;
            case INCONSISTENT_THEORY_EXCEPTION:
                showErrorDialog(null, "The set of testcases itself is inconsistent with the theory.", "Inconsistent Theory Exception", JOptionPane.ERROR_MESSAGE, ex);
                break;
            case NO_CONFLICT_EXCEPTION:
                showErrorDialog(null, "There are no conflicts and therefore no diagnoses", "No Conflict Exception", JOptionPane.INFORMATION_MESSAGE, ex);
                break;
        }
    }

}
