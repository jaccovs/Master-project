package org.exquisite.diagnosis.invquickxplain;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;

import choco.kernel.model.constraints.Constraint;

/**
 * Implementation of Kostyas Inverse-QuickXplain, that directly calculates a tests.diagnosis based on the QuickXplain algorithm.
 * @author Thomas
 */
public class InverseQuickXplain extends QuickXPlain {
	public InverseQuickXplain(ExquisiteSession sessionData,
			IDiagnosisEngine diagnosisEngine) {
		super(sessionData, diagnosisEngine);
	}
	
	@Override
	public boolean isConsistent(List<Constraint> constraints)
			throws DomainSizeException {

		List<Constraint> inverse = new ArrayList<Constraint>(this.currentDiagnosisModel.getPossiblyFaultyStatements());
		inverse.removeAll(constraints);
		inverse.addAll(this.currentDiagnosisModel.getCorrectStatements());
		
		return !super.isConsistent(inverse);
	}
	
	@Override
	public boolean checkConsistency() throws DomainSizeException {
		return !isConsistent(new ArrayList<Constraint>());
	}
}
