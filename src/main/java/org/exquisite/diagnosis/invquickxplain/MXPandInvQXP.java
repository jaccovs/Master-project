package org.exquisite.diagnosis.invquickxplain;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.mergexplain.MergeXplain;
import org.exquisite.diagnosis.quickxplain.mergexplain.MergeXplain.ConflictSearchModes;

import choco.kernel.model.constraints.Constraint;

/**
 * Diagnosis engine that first uses MergeXplain to calculate a set of conflicts which constraints can be used to calculate a tests.diagnosis.
 * These constraints are then passed to Inverse-QuickXplain.
 * @author Thomas
 */
public class MXPandInvQXP extends InvQXDiagnosisEngine {
	public MXPandInvQXP(ExquisiteSession sessionData) {
		super(sessionData);
	}
	
	@Override
	public List<Diagnosis> calculateDiagnoses() throws DiagnosisException {
		// TEST
//		long start = System.nanoTime();
		
		// call MergeXplain
		MergeXplain mxp = new MergeXplain(sessionData, this);
		MergeXplain.ConflictSearchMode = ConflictSearchModes.Least;
		List<List<Constraint>> conflicts = mxp.findConflicts();
		
		// use constraints from conflicts as new possibly faulty constraints
		List<Constraint> newPossiblyFaulty = new ArrayList<Constraint>();		
		for (List<Constraint> conflict: conflicts) {
			for (Constraint c: conflict) {
				if (!newPossiblyFaulty.contains(c)) {
					newPossiblyFaulty.add(c);
				}
			}
		}
		
		// Add rest of old possibly faulty constraints to correct constraints
		getModel().getCorrectStatements().addAll(getModel().getPossiblyFaultyStatements());
		getModel().getCorrectStatements().removeAll(newPossiblyFaulty);

		// set new possibly faulty constraints
		getModel().setPossiblyFaultyStatements(newPossiblyFaulty);
		
		// TEST
//		long end = System.nanoTime();
//		List<Diagnosis> diagnoses = super.calculateDiagnoses();		
//		finishedTime = System.nanoTime() - (end-start);		
//		return diagnoses;
		
		// call Inverse-QuickXplain
		return super.calculateDiagnoses();
	}
}
