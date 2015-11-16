package org.exquisite.diagnosis.invquickxplain;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Kostyas Inverse-QuickXplain, that directly calculates a tests.diagnosis based on the QuickXplain algorithm.
 *
 * @author Thomas
 */
public class InverseQuickXplain<T> extends QuickXPlain<T> {
    public InverseQuickXplain(ExquisiteSession<T> sessionData,
                              IDiagnosisEngine<T> diagnosisEngine) {
        super(sessionData, diagnosisEngine);
    }

    @Override
    public boolean isConsistent(List<T> constraints)
            throws DomainSizeException {

        List<T> inverse = new ArrayList<T>(this.currentDiagnosisModel.getPossiblyFaultyStatements());
        inverse.removeAll(constraints);
        inverse.addAll(this.currentDiagnosisModel.getCorrectStatements());

        return !super.isConsistent(inverse);
    }

    @Override
    public boolean checkConsistency() throws DomainSizeException {
        return !isConsistent(new ArrayList<T>());
    }
}
