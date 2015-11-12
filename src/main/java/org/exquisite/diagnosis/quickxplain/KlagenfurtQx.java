package org.exquisite.diagnosis.quickxplain;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.Example;

import choco.kernel.model.constraints.Constraint;

public class KlagenfurtQx extends QuickXPlain {

	public KlagenfurtQx(ExquisiteSession sessionData, AbstractHSDagBuilder dagbuilder) {
		super(sessionData, dagbuilder);
	}
	
	
	/**
	 * Psuedo-code from Kostya.
	 * 
	 * Effectively runs is consistent check for every test case.
	 * This could be emulated simply by overriding checkExamples method by forcing it to use all the test cases?
	 * 
	 * public boolean isConsistent(CSP cs, List<Test> tests){
		  if (!solver.isConsistent(cs)) return false;
		  cs1 = new CSP();
		  cs1.addAll(cs);
		  for(Test t : tests){
		    cs1 = cs1.addAll(t);
		    if (!solver.isConsistent(cs1)) return false;
		    cs1.removeAll(t); 
		  }
		  return true;
		}
	 */

	/**
	 * This override ignores what examples are given as input parameters and checks all of the test examples every
	 * time it is called.
	 */
	@Override
	public ConflictCheckingResult checkExamples(List<Example> examples, List<Constraint> constraintsToIgnore, boolean createConflicts) throws DomainSizeException {
		// TODO Auto-generated method stub
		List<Example> allExamples = new ArrayList<Example>(this.sessionData.diagnosisModel.getPositiveExamples());
		return super.checkExamples(allExamples, constraintsToIgnore, createConflicts);
	}
}
