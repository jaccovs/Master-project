package tests.diagnosis;

import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.HSDagBuilder;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.tools.Utilities;

import choco.Choco;
import choco.kernel.model.variables.integer.IntegerVariable;

public class HardCodedModelExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new HardCodedModelExample().run();
	}
	
	public void run()
	{
		try{
			ExquisiteSession sessionData = new ExquisiteSession();
			sessionData.diagnosisModel = makeModel();
			HSDagBuilder hsdag = new HSDagBuilder(sessionData);
//			hsdag.setMaxSearchDepth(-1);			
//			hsdag.setMaxDiagnoses(-1);
			
			List<Diagnosis> diagnoses = hsdag.calculateDiagnoses();
			System.out.println("Found " + diagnoses.size() + " diagnoses");
			for (int i = 0; i < diagnoses.size(); i++) 
			{
				System.out.println("-- Diagnosis #" + i);
				System.out.println("    " + Utilities.printConstraintList((diagnoses.get(i).getElements()), sessionData.diagnosisModel));
				System.out.println("--");
			}
		}
		catch (DomainSizeException e)
		{
			e.printStackTrace();
		}
	}
	
	DiagnosisModel makeModel()
	{
		DiagnosisModel model = new DiagnosisModel();
		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 1,100));
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 1,100));
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 1,100));
		IntegerVariable b2 = model.addIntegerVariable(Choco.makeIntVar("b2", 1,100));
		IntegerVariable c1 = model.addIntegerVariable(Choco.makeIntVar("c1", 1, 100));
		
		model.addPossiblyFaultyConstraint(Choco.eq(b2, Choco.mult(a2, 3)), "b2 = a2 * 3");	
		model.addPossiblyFaultyConstraint(Choco.eq(b1, Choco.mult(a1, 2)), "b1 = a1 * 2");			
		model.addPossiblyFaultyConstraint(Choco.eq(c1, Choco.plus(b1, b2)), "c1 = b1 + b2 (should be *)"); // should be *

		Example exTestExample = new Example();
		exTestExample.addConstraint(Choco.eq(a2, 2), "a2=2");
		exTestExample.addConstraint(Choco.eq(a1, 2), "a1=2");		
		exTestExample.addConstraint(Choco.eq(c1, 24), "c1=24");
		
		Example negTestCase = new Example(true);
		//negTestCase.addConstraint(Choco.eq(a2, 2), "a2=2");
		//negTestCase.addConstraint(Choco.eq(a1, 2), "a1=2");		
		//negTestCase.addConstraint(Choco.eq(c1, 24), "c1=10");
		negTestCase.addConstraint(Choco.and(Choco.eq(a2, 2), Choco.eq(a1,2), Choco.eq(c1, 10)), "NEG");
		
		
		

		model.getPositiveExamples().add(exTestExample);
		//model.getNegativeExamples().add(negTestCase);
	
		return model;
	}

}
