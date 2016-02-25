package org.exquisite.diagnosis.invquickxplain;

import org.exquisite.datamodel.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Kostyas Inverse-QuickXplain, that directly calculates a tests.diagnosis based on the QuickXplain algorithm.
 *
 * @author Thomas
 */
public class InverseQuickXplain<T> extends ConstraintsQuickXPlain<T> {
    public InverseQuickXplain(DiagnosisModel<T> sessionData) {
        super(sessionData);
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
