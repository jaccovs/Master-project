package tests.ts;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import evaluations.tools.DiagnosisCheck;

public class DiagnosisCheckTest {
	
	public void runNonMinimalDiagnosisTest()
	{
		DiagnosisModel model = new DiagnosisModel();
		
		List<Diagnosis> diagnoses = new ArrayList<Diagnosis>();
		
		IntegerExpressionVariable var1 = Choco.makeIntVar("V1", 0, 10);
		IntegerExpressionVariable var2 = Choco.makeIntVar("V2", 0, 10);
		
		List<Constraint> constraints = new ArrayList<Constraint>();
		Constraint c1 = Choco.eq(var1, var2);
		constraints.add(c1);
		model.addPossiblyFaultyConstraint(c1, "C1");
		
		diagnoses.add(new Diagnosis(constraints, model));
		
		constraints = new ArrayList<Constraint>(constraints);
		Constraint c2 = Choco.gt(var1, 5);
		constraints.add(c2);
		model.addPossiblyFaultyConstraint(c2, "C2");
		
		diagnoses.add(new Diagnosis(constraints, model));
		
		constraints = new ArrayList<Constraint>();
		Constraint c22 = Choco.gt(var1, 5);
		constraints.add(c22);
		model.addPossiblyFaultyConstraint(c22, "C2");
		
		diagnoses.add(new Diagnosis(constraints, model));
		
		DiagnosisCheck check = new DiagnosisCheck();
		List<Diagnosis> nonMinimal = check.checkForNonMinimalDiagnoses(diagnoses);
		System.out.println("Not Minimal: " + nonMinimal.size());
		
		check.printNonMinimalDiagnoses(diagnoses);
	}
	
	public void runTests()
	{
		runNonMinimalDiagnosisTest();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		DiagnosisCheckTest test = new DiagnosisCheckTest();
		test.runTests();
	}

}
