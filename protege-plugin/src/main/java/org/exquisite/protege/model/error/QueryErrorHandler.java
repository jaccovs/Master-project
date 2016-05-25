package org.exquisite.protege.model.error;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;

import javax.swing.*;

public class QueryErrorHandler extends SearchErrorHandler {

    @Override
    public void errorHappend(OntologyDiagnosisSearcher.ErrorStatus error) {

        switch (error) {
            case NO_QUERY:
                JOptionPane.showMessageDialog(null, "There is no possible query", "No Query", JOptionPane.INFORMATION_MESSAGE);
                break;
            case NO_CONFLICT_EXCEPTION:
                // we have not found new diagnoses but perhaps there are still diagnoses to process so this is no prob
                break;
            case ONLY_ONE_DIAG:
                JOptionPane.showMessageDialog(null, "There is only one diagnosis so we can not discriminate  ", "Only One Diagnosis", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                super.errorHappend(error);
                break;
        }
    }

}
