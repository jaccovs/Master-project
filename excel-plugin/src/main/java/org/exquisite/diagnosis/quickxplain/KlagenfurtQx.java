package org.exquisite.diagnosis.quickxplain;

import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.Example;

import java.util.ArrayList;
import java.util.List;

public class KlagenfurtQx<T> extends ConstraintsQuickXPlain<T> {

    public KlagenfurtQx(ExcelExquisiteSession<T> sessionData) {
        super(sessionData);
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
    public ConflictCheckingResult<T> checkExamples(List<Example<T>> examples, List<T> constraintsToIgnore,
                                                   boolean createConflicts) throws DomainSizeException {
        // TODO Auto-generated method stub
        List<Example<T>> allExamples = new ArrayList<>(this.sessionData.getDiagnosisModel().getPositiveExamples());
        return super.checkExamples(allExamples, constraintsToIgnore, createConflicts);
    }
}
