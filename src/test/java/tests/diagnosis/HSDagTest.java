package tests.diagnosis;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.HSDagBuilder;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.tools.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Test the HS-DAG algorithm
 * @author Dietmar
 *
 */
public class HSDagTest {
	
	public static void main(String[] args) {
		System.out.println("Starting HS-DAG test");
		new HSDagTest().run();
		System.out.println("HS-DAG Test ended");
	}
	
	/**
	 * A runner method
	 */
	public void run() {
		try {
			ExquisiteSession sessionData = new ExquisiteSession();
//			 Create a constraint model
//			sessionData.diagnosisModel = this.defineModelForSmallExampleInPaper();
//			sessionData.diagnosisModel = defineModelAndInputs();
//			sessionData.diagnosisModel = defineMiniModel();
//			sessionData.diagnosisModel = notSoMiniModel();
			sessionData.diagnosisModel = defineExampleFromPaper5Vars();
			System.out.println("Created the model with examples");
			HSDagBuilder hsdag = new HSDagBuilder(sessionData);
//			hsdag.setMaxSearchDepth(-1);			
//			hsdag.setMaxDiagnoses(-1);

			List<Diagnosis<Constraint>> diagnoses = hsdag.calculateDiagnoses();
			System.out.println("Found " + diagnoses.size() + " diagnoses");
			for (int i = 0; i < diagnoses.size(); i++) 
			{
				System.out.println("-- Diagnosis #" + i);
				System.out.println("    " + Utilities.printConstraintList((diagnoses.get(i).getElements()), sessionData.diagnosisModel));
				System.out.println("--");
			}
			//*/			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	DiagnosisModel<Constraint> defineMiniModel()
	{
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();
		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 1,100));
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 1,100));
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 1,100));
		
		model.addPossiblyFaultyConstraint(Choco.eq(a1,3), "a1 = 3");
		model.addPossiblyFaultyConstraint(Choco.eq(a2,5), "a2 = 5");

		
		model.addPossiblyFaultyConstraint(Choco.eq(b1, Choco.mult(a1, a2)), "B1 = a1 * a2"); // should be +

		Example<Constraint> exTestExample = new Example<>();
//		exTestExample.addConstraint(Choco.eq(a1, 3), "a1=3");
//		exTestExample.addConstraint(Choco.eq(a2, 5), "a2=5");
		exTestExample.addConstraint(Choco.eq(b1, 8), "b1=8");


		model.getPositiveExamples().add(exTestExample);

		
		return model;
	}

	DiagnosisModel<Constraint> notSoMiniModel()
	{
		DiagnosisModel<Constraint> model = new DiagnosisModel<>();
		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 1,100));
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 1,100));
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 1,100));
		IntegerVariable b2 = model.addIntegerVariable(Choco.makeIntVar("b2", 1,100));
		IntegerVariable c1 = model.addIntegerVariable(Choco.makeIntVar("c1", 1, 100));
		
		model.addPossiblyFaultyConstraint(Choco.eq(b2, Choco.plus(a2, 3)), "B2 = a2 + 3");	
		model.addPossiblyFaultyConstraint(Choco.eq(b1, Choco.plus(a1, 2)), "B1 = a1 + 2");			
		model.addPossiblyFaultyConstraint(Choco.eq(c1, Choco.mult(b1, b2)), "C1 = b1 * b2"); // should be +

		Example<Constraint> exTestExample = new Example<>();
		exTestExample.addConstraint(Choco.eq(a2, 2), "a2=2");
		exTestExample.addConstraint(Choco.eq(a1, 1), "a1=1");		
		exTestExample.addConstraint(Choco.eq(c1, 8), "c1=8");


		model.getPositiveExamples().add(exTestExample);

		
		return model;
	}	
	
	
	/*
	 * 
	 * Representing the small example from the paper.
	 * 
	 */
	DiagnosisModel<Constraint> defineModelForSmallExampleInPaper()
	{
		System.out.println("Defining model for small example from paper.");
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();
		
		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 1,100));//max value from test examples
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 1,100));//max value from test examples
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 1,1000));//as 15*2=30
		IntegerVariable b2 = model.addIntegerVariable(Choco.makeIntVar("b2", 1,1000));//as 10*3=30
		IntegerVariable c1 = model.addIntegerVariable(Choco.makeIntVar("c1", 1,1000));//as 30+30=60
		IntegerVariable d1 = model.addIntegerVariable(Choco.makeIntVar("d1", 1,10000));//as 900+10=910
		
		//add the suspect formulae...
		model.addPossiblyFaultyConstraint(Choco.eq(b1, Choco.mult(a1, 2)), "B1");
		model.addPossiblyFaultyConstraint(Choco.eq(b2, Choco.mult(a2, 3)), "B2");
		model.addPossiblyFaultyConstraint(Choco.eq(c1, Choco.plus(b1, b2)), "C1");//should be +
		model.addPossiblyFaultyConstraint(Choco.eq(d1, Choco.plus(c1, 10)), "D1");//should be *
		
		//make the examples...
		
		//extended example
//		Constraint exTest = Choco.and(Choco.and(Choco.and(Choco.and(Choco.eq(a1, 4), Choco.eq(a2, 5), Choco.eq(d1, 230)))));
		// WHY THREE ANDS???
		Constraint exTest = Choco.and(Choco.eq(a1, 4), Choco.eq(a2, 5), Choco.eq(d1, 230));
		Example<Constraint> exTestExample = new Example<>();
		exTestExample.addConstraint(Choco.eq(a1, 4), "a1=4");
		exTestExample.addConstraint(Choco.eq(a2, 5), "a2=5");
		exTestExample.addConstraint(Choco.eq(d1, 230), "d1=230");
		exTestExample.addConstraint(exTest, "exTest");

		Example<Constraint> exPositive = new Example<>();
//		exPositive.addConstraint(Choco.and(Choco.gt(c1, 100), Choco.lt(c1, 200), Choco.gt(d1, 1000)), "c1>100&c1<200&d1>1000");
		exPositive.addConstraint(Choco.gt(c1, 10), "c1>100");
		exPositive.addConstraint(Choco.lt(c1, 20), "c1<200");
		exPositive.addConstraint(Choco.gt(d1, 100), "d1>1000");
		
		//model.addCorrectConstraint(Choco.and(Choco.gt(c1, 100), Choco.lt(c1, 200), Choco.gt(d1, 1000)), "c1>100&c1<200&d1>1000");

		Example<Constraint> exNegative = new Example<>();
		exNegative.addConstraint(Choco.gt(c1, 100), "c1>100");
		exNegative.addConstraint(Choco.lt(d1, 1000), "d1<1000");
		
		Example exPos2 = new Example();
//		model.addCorrectConstraint(Choco.and(Choco.and(Choco.gt(c1, 14), Choco.lt(c1, 61)), Choco.gt(d1, 100)), "(c1>100 & c1 < 200) -> d1 > 1000");
		model.addCorrectConstraint(Choco.and(Choco.gt(c1, 14), Choco.lt(c1, 61)), "(c1>100 & c1 < 200) -> d1 > 1000");
		
//		model.addCorrectConstraint(Choco.lt(c1, 100), "c1<100");

		
//		//a1 = 1 && a2 = 6 && c1 = 20
//		Constraint test1 = Choco.and(Choco.and(Choco.and(Choco.eq(a1, 1), Choco.eq(a2, 6)), Choco.eq(c1, 20)));
//		Example pExample1 = new Example();
//		pExample1.addConstraint(test1, "test1");
//		
//		//a1 = 4 && a2 = 5 && c1 = 23
//		Constraint test2 = Choco.and(Choco.and(Choco.and(Choco.eq(a1, 4), Choco.eq(a2, 5)), Choco.eq(c1, 23)));
//		Example pExample2 = new Example();
//		pExample2.addConstraint(test2, "test2");
//		
//		//a1 = 15 && a2 = 10 && c1 = 60
//		Constraint test3 = Choco.and(Choco.and(Choco.and(Choco.eq(a1, 15), Choco.eq(a2, 10)), Choco.eq(c1, 60)));
//		Example pExample3 = new Example();
//		pExample3.addConstraint(test3, "test3");
//		
//		//a1 = 6 && a2 = 1 && c1 = 15
//		Constraint test4 = Choco.and(Choco.and(Choco.and(Choco.eq(a1, 6), Choco.eq(a2, 1)), Choco.eq(c1, 15)));
//		Example pExample4 = new Example();
//		pExample4.addConstraint(test4, "test4");
				
		//Add/remove //'s next to examples to show diagnosis result per example.
		//model.getPositiveExamples().add(pExample1); //returns {B2}, {C1}
		//model.getPositiveExamples().add(pExample2); //returns {B1}, {C1}  - this is different to the paper where b1 & b2 are returned as one diagnosis.
		//model.getPositiveExamples().add(pExample3); //returns {B1}, {B2}, {C1}
		//model.getPositiveExamples().add(pExample4); //returns {B1}, {C1}
		model.getPositiveExamples().add(exTestExample);
		
//		model.getPositiveExamples().add(exPositive);
		model.getPositiveExamples().add(exPos2);
		
//		model.getNegativeExamples().add(exNegative);
		
		
		return model;
	}


	DiagnosisModel<Constraint> defineExampleFromPaper5Vars()
	{
		DiagnosisModel<Constraint> model = new DiagnosisModel<>();
		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 0,15));
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 0,15));
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 0,30));
		IntegerVariable b2 = model.addIntegerVariable(Choco.makeIntVar("b2", 0,45));
		IntegerVariable c1 = model.addIntegerVariable(Choco.makeIntVar("c1", 0,75));
		
		model.addPossiblyFaultyConstraint(Choco.eq(b1, Choco.mult(a1, 2)), "b1=a1*2");
		model.addPossiblyFaultyConstraint(Choco.eq(b2, Choco.mult(a2, 3)), "b2=a2*3");		
		model.addPossiblyFaultyConstraint(Choco.eq(c1, Choco.mult(b1, b2)), "c1=b1*b2"); // should be +

		Example<Constraint> posEx1 = new Example<>();
		posEx1.addConstraint(Choco.eq(a1, 1), "a1=1");
		posEx1.addConstraint(Choco.eq(a2, 6), "a2=6");
		posEx1.addConstraint(Choco.eq(c1, 20), "c1=20");

		Example<Constraint> posEx2 = new Example<>();
		posEx2.addConstraint(Choco.eq(a1, 4), "a1=4");
		posEx2.addConstraint(Choco.eq(a2, 5), "a2=5");
		posEx2.addConstraint(Choco.eq(c1,23), "c1=23");

		Example<Constraint> posEx3 = new Example<>();
		posEx3.addConstraint(Choco.eq(a1, 15), "a1=15");
		posEx3.addConstraint(Choco.eq(a2, 10), "a2=10");
		posEx3.addConstraint(Choco.eq(c1,60), "c1=60");

		Example<Constraint> posEx4 = new Example<>();
		posEx4.addConstraint(Choco.eq(a1, 6), "a1=6");
		posEx4.addConstraint(Choco.eq(a2, 1), "a2=1");
		posEx4.addConstraint(Choco.eq(c1, 15), "c1=15");

		List<Example<Constraint>> positiveExamples = new ArrayList<>();
		positiveExamples.add(posEx1);
		positiveExamples.add(posEx2);
		positiveExamples.add(posEx3);
		positiveExamples.add(posEx4);
		
		model.setPositiveExamples(positiveExamples);
		
		return model;
	}
	
	
	/**
	 * A method to define model
	 * @return
	 */
	DiagnosisModel<Constraint> defineModelAndInputs() {
		System.out.println("Defining model");
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();
		
		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 1,1000));
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 1,1000));
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 1,1000));
		IntegerVariable b2 = model.addIntegerVariable(Choco.makeIntVar("b2", 1,1000));
		IntegerVariable c1 = model.addIntegerVariable(Choco.makeIntVar("c1", 1,1000));
		
		// Dynamically shrink the domains to what the user has specified.
		
		model.addCorrectConstraint(Choco.lt(a1, 100), "a1<100");
		model.addCorrectConstraint(Choco.lt(a2, 100), "a2<100");
		model.addCorrectConstraint(Choco.lt(b1, 100), "b1<100");
		model.addCorrectConstraint(Choco.lt(b2, 100), "b2<100");
		model.addCorrectConstraint(Choco.lt(c1, 100), "c1<100");
		
		model.addCorrectConstraint(Choco.gt(a1, 0), "a1>0");
		model.addCorrectConstraint(Choco.gt(a2, 0), "a2>0");
		model.addCorrectConstraint(Choco.gt(b1, 0), "b1>0");
		model.addCorrectConstraint(Choco.gt(b2, 0), "b2>0");
		model.addCorrectConstraint(Choco.gt(c1, 0), "c1>0");
		
		model.addPossiblyFaultyConstraint(Choco.eq(b1,Choco.mult(a1, 2)), "C1 (b1 = a1 * 2) (1)");
		model.addPossiblyFaultyConstraint(Choco.eq(b2,Choco.mult(a2, 3)), "C2 (b2 = a2 * 3) (2)");
		model.addPossiblyFaultyConstraint(Choco.eq(c1,Choco.mult(b1, b2)), "C3 (c1 = b1 * b2) (3)"); //should be plus...

		// Something assumed to be correct
		//model.addCorrectConstraint(Choco.eq(b2,Choco.mult(a2, 3)), "C2");
		
		// One example
		Example<Constraint> pExample1 = new Example<>();
		pExample1.addConstraint(Choco.eq(a1, 1), "a1=1");
		pExample1.addConstraint(Choco.eq(a2, 6), "a2=6");
		pExample1.addConstraint(Choco.eq(c1, 20), "c1=20");
		
//		Example pExample2 = new Example();
//		pExample2.addConstraint(Choco.eq(a1, 4), "a1=4");
//		pExample2.addConstraint(Choco.eq(a2, 5), "a2=5");
//		pExample2.addConstraint(Choco.eq(c1, 23), "c1=21");
//		
//		Example pExample3 = new Example();
//		pExample3.addConstraint(Choco.eq(a1, 15), "a1=15");
//		pExample3.addConstraint(Choco.eq(a2, 10), "a2=10");
//		pExample3.addConstraint(Choco.eq(c1, 60), "c1=47");
		
		model.getPositiveExamples().add(pExample1);
		//model.getPositiveExamples().add(pExample2);
		//model.getPositiveExamples().add(pExample3);

		Example<Constraint> nExample1 = new Example<>(true);
		nExample1.addConstraint(Choco.eq(a1, 1), "a1=1");
		nExample1.addConstraint(Choco.gt(a2, 6), "a2=6");
		nExample1.addConstraint(Choco.gt(c1, 36), "c1=36");
		
//		model.getNegativeExamples().add(nExample1);
		return model;
	}	
}