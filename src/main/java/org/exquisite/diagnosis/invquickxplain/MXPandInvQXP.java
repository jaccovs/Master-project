package org.exquisite.diagnosis.invquickxplain;

import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.mergexplain.MergeXplain;
import org.exquisite.diagnosis.quickxplain.mergexplain.MergeXplain.ConflictSearchModes;

import java.util.ArrayList;
import java.util.List;

/**
 * Diagnosis engine that first uses MergeXplain to calculate a set of conflicts which constraints can be used to calculate a tests.diagnosis.
 * These constraints are then passed to Inverse-QuickXplain.
 *
 * @author Thomas
 */
public class MXPandInvQXP<T> extends InvQXDiagnosisEngine<T> {
    public MXPandInvQXP(ExcelExquisiteSession sessionData) {
        super(sessionData);
    }

    @Override
    public List<Diagnosis<T>> calculateDiagnoses() throws DiagnosisException {
        // TEST
//		long start = System.nanoTime();

        // call MergeXplain
        MergeXplain<T> mxp = new MergeXplain<>(sessionData);
        MergeXplain.ConflictSearchMode = ConflictSearchModes.Least;
        List<List<T>> conflicts = mxp.findConflicts();

        // use constraints from conflicts as new possibly faulty constraints
        List<T> newPossiblyFaulty = new ArrayList<>();
        for (List<T> conflict : conflicts) {
            for (T c : conflict) {
                if (!newPossiblyFaulty.contains(c)) {
                    newPossiblyFaulty.add(c);
                }
            }
        }

        // Add rest of old possibly faulty constraints to correct constraints
        getDiagnosisModel().getCorrectStatements().addAll(getDiagnosisModel().getPossiblyFaultyStatements());
        getDiagnosisModel().getCorrectStatements().removeAll(newPossiblyFaulty);

        // set new possibly faulty constraints
        getDiagnosisModel().setPossiblyFaultyStatements(newPossiblyFaulty);

        // TEST
//		long end = System.nanoTime();
//		List<Diagnosis> diagnoses = super.calculateDiagnoses();		
//		finishedTime = System.nanoTime() - (end-start);		
//		return diagnoses;

        // call Inverse-QuickXplain
        return super.calculateDiagnoses();
    }
}
