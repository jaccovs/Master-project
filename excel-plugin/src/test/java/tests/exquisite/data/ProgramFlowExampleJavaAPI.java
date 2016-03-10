package tests.exquisite.data;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.util.List;

public class ProgramFlowExampleJavaAPI {

	public static void main(String[] args) 
	{
		new ProgramFlowExampleJavaAPI().run();
	}
	
	public void run()
	{
		Debug.DEBUGGING_ON = true;
		Debug.msg("Program flow (Java API) example... START\n");
				
		Debug.msg("    Instantiate a DiagnosisModel object to populate.");
		DiagnosisModel<Constraint> model = makeDiagnosisModel();
		
		Debug.msg("    Instantiate a ExquisiteGraph object to populate.");
		ExquisiteGraph<String> graph = new ExquisiteGraph<String>();
		
		ExcelExquisiteSession sessionData = new ExcelExquisiteSession();
		sessionData.getDiagnosisModel() = model;
		sessionData.graph = graph;
		
		
		Debug.msg("\nPrepare to perform diagnosis...");
		
		Debug.msg("    Instantiate diagnosisEngine.");
		IDiagnosisEngine<Constraint> diagnosisEngine = EngineFactory.makeDAGEngineStandardQx(sessionData);
		
		Debug.msg("    diagnosisEngine.setModel(model)");
		diagnosisEngine.setDiagnosisModel(sessionData);
		
		Debug.msg("    diagnosisEngine.calculateDiagnoses()\n");
		try{
			List<Diagnosis<Constraint>> result = diagnosisEngine.calculateDiagnoses();
			Debug.msg("Diagnosis result:\n");
			for (Diagnosis<Constraint> diagnosis : result)
			{
				Debug.msg("-- Diagnosis --\n");
				String list = Utilities.printConstraintList(diagnosis.getElements(), model);
				Debug.msg("    " + list);
				Debug.msg("---------------\n");
			}
		} catch (DiagnosisException e){
			e.printStackTrace();
		}
		Debug.msg("\nEND of Program flow (Java API) example.");
	}
	
	/**
	 * Load appXML from source xml file.
	 * @param data.xmlFilePath - the full path to the xml file to be read.
	 */
	private DiagnosisModel<Constraint> makeDiagnosisModel() {
		DiagnosisModel<Constraint> model = new DiagnosisModel<>();
		
		Debug.msg("\nMake any variables first that have had a user specified (i.e. global) value bound associated with them.");			
		Debug.msg("Number of variables with global value bounds to make: 10");
		
		//Variables defined with custom (global) value bounds
		//d2:e3 0 >= & <= 40
		int d2e3Min = 0;
		int d2e3Max = 40;		
		IntegerVariable d2 = new IntegerVariable("WS_1_D2", d2e3Min, d2e3Max);
		IntegerVariable d3 = new IntegerVariable("WS_1_D3", d2e3Min, d2e3Max );
		IntegerVariable e2 = new IntegerVariable("WS_1_E2", d2e3Min, d2e3Max );
		IntegerVariable e3 = new IntegerVariable("WS_1_E3", d2e3Min, d2e3Max );
		
		//b2:c3 0 >= & <= 50
		int b2c3Min = 0;
		int b2c3Max = 50;
		IntegerVariable b2 = new IntegerVariable("WS_1_B2", b2c3Min, b2c3Max );
		IntegerVariable b3 = new IntegerVariable("WS_1_B3", b2c3Min, b2c3Max );
		IntegerVariable c2 = new IntegerVariable("WS_1_C2", b2c3Min, b2c3Max );
		IntegerVariable c3 = new IntegerVariable("WS_1_C3", b2c3Min, b2c3Max );
		
		//f2:f3 0 >= & <= 30
		int f2f3Min = 0;
		int f2f3Max = 30;
		IntegerVariable f2 = new IntegerVariable("WS_1_F2", f2f3Min, f2f3Max );
		IntegerVariable f3 = new IntegerVariable("WS_1_F3", f2f3Min, f2f3Max );
		
		Debug.msg("\nGet the default min / max for remaining variables to be constructed.");
		int defaultMin = 0;
		int defaultMax = 1000;
		Debug.msg("    default min = " + defaultMin);
		Debug.msg("    default max = " + defaultMax);
				
		Debug.msg("Make the rest of the variables with the default min/max values.");
		//Make the variables
		Debug.msg("\n-- Input cell variables.");
		//all inputs in this example have had custom value bounds applied to them so have been constructed already.
						
		model.addIntegerVariable(d2);
		model.addIntegerVariable(e2);
		model.addIntegerVariable(b2);
		model.addIntegerVariable(c2);
		model.addIntegerVariable(d3);
		model.addIntegerVariable(e3);
		model.addIntegerVariable(b3);
		model.addIntegerVariable(c3);
		
		Debug.msg("\n-- Interim cell variables.");	
		IntegerVariable g2 = new IntegerVariable("WS_1_G2", defaultMin, defaultMax );
		IntegerVariable g3 = new IntegerVariable("WS_1_G3", defaultMin, defaultMax );
		IntegerVariable h2 = new IntegerVariable("WS_1_H2", defaultMin, defaultMax );
		IntegerVariable h3 = new IntegerVariable("WS_1_H3", defaultMin, defaultMax );
		IntegerVariable g5 = new IntegerVariable("WS_1_G5", defaultMin, defaultMax );
		IntegerVariable g6 = new IntegerVariable("WS_1_G6", defaultMin, defaultMax );
		
		model.addIntegerVariable(f2);
		model.addIntegerVariable(f3);
		model.addIntegerVariable(g2);
		model.addIntegerVariable(g3);
		model.addIntegerVariable(h2);
		model.addIntegerVariable(h3);
		model.addIntegerVariable(g5);
		model.addIntegerVariable(g6);
		
		Debug.msg("\n-- Output cell variables.");
		IntegerVariable g7 = new IntegerVariable("WS_1_G7", defaultMin, defaultMax );
		model.addIntegerVariable(g7);		
		
		Debug.msg("\nMake the constraint representations of the spreadsheet formulae.");
		//f2=d2+e2
		model.addPossiblyFaultyConstraint(Choco.eq(f2, Choco.plus(d2, e2)), "WS_1_F2");
		//g2=f2*b2
		model.addPossiblyFaultyConstraint(Choco.eq(g2, Choco.mult(f2, b2)), "WS_1_G2");
		//f3=d3+e3		
		model.addPossiblyFaultyConstraint(Choco.eq(f3, Choco.plus(d3, e3)), "WS_1_F3");
		//g3=f3*b3
		model.addPossiblyFaultyConstraint(Choco.eq(g3, Choco.mult(f3, b3)), "WS_1_G3");
		//g5=g2*g3
		model.addPossiblyFaultyConstraint(Choco.eq(g5, Choco.mult(g2, g3)), "WS_1_G5");
		//g6=h2+h3
		model.addPossiblyFaultyConstraint(Choco.eq(g6, Choco.plus(h2, h3)), "WS_1_G6");
		
		//Correct statements
		//h2=f2+c2
		model.addCorrectFormula(Choco.eq(h2, Choco.plus(f2, c2)), "WS_1_H2");
		//h3=f3*c3
		model.addCorrectFormula(Choco.eq(h3, Choco.mult(f3, c3)), "WS_1_H3");
		//g7=g5-g6
		model.addCorrectFormula(Choco.eq(g7, Choco.minus(g5, g6)), "WS_1_G7");
		
		Debug.msg("\nMake globally defined value bounds - number to make is: 5");	
		//d2:e3 0 >= & <= 40
		model.addCorrectFormula(Choco.and(Choco.geq(d2, d2e3Min), Choco.leq(d2, d2e3Max)), "valueBoundConstraint_d2");
		model.addCorrectFormula(Choco.and(Choco.geq(d3, d2e3Min), Choco.leq(d3, d2e3Max)), "valueBoundConstraint_d3");
		model.addCorrectFormula(Choco.and(Choco.geq(e2, d2e3Min), Choco.leq(e2, d2e3Max)), "valueBoundConstraint_e2");
		model.addCorrectFormula(Choco.and(Choco.geq(e3, d2e3Min), Choco.leq(e3, d2e3Max)), "valueBoundConstraint_e3");
		
		//b2:c3 0 >= & <= 50
		model.addCorrectFormula(Choco.and(Choco.geq(b2, b2c3Min), Choco.leq(b2, b2c3Max)), "valueBoundConstraint_b2");
		model.addCorrectFormula(Choco.and(Choco.geq(b3, b2c3Min), Choco.leq(b3, b2c3Max)), "valueBoundConstraint_b3");
		model.addCorrectFormula(Choco.and(Choco.geq(c2, b2c3Min), Choco.leq(c2, b2c3Max)), "valueBoundConstraint_c2");
		model.addCorrectFormula(Choco.and(Choco.geq(c3, b2c3Min), Choco.leq(c3, b2c3Max)), "valueBoundConstraint_c3");
		
		//f2:f3 0 >= & <= 30
		model.addCorrectFormula(Choco.and(Choco.geq(f2, f2f3Min), Choco.leq(f2, f2f3Max)), "valueBoundConstraint_f2");
		model.addCorrectFormula(Choco.and(Choco.geq(f3, f2f3Min), Choco.leq(f3, f2f3Max)), "valueBoundConstraint_f3");
		
		Debug.msg("\nTransform ExquisiteAppXML test cases into Example objects.");
		Debug.msg("No. of test cases to transform: 1");
		Example<Constraint> positiveExample = new Example();
		
		//Faulty values
		//G5 expected value 340		
		positiveExample.addConstraint(Choco.eq(g5, 340), "expectedValue WS_1_G5 = 340");
		//Inputs
		Choco.eq(g5, 16800);
		
		positiveExample.addConstraint(Choco.eq(d2, 2), "inputValue WS_1_D2 = 2");
		positiveExample.addConstraint(Choco.eq(e2, 4), "inputValue WS_1_E2 = 4");
		positiveExample.addConstraint(Choco.eq(b2, 10), "inputValue WS_1_B2 = 10");
		positiveExample.addConstraint(Choco.eq(c2, 5), "inputValue WS_1_C2 = 5");
		positiveExample.addConstraint(Choco.eq(d3, 6), "inputValue WS_1_D3 = 6");
		positiveExample.addConstraint(Choco.eq(e3, 8), "inputValue WS_1_E3 = 8");
		positiveExample.addConstraint(Choco.eq(b3, 20), "inputValue WS_1_B3 = 20");
		positiveExample.addConstraint(Choco.eq(c3, 15), "inputValue WS_1_C3 = 15");
		
		//Correct values
		//none for this example.
		
		//Value bounds
		//none for this example.
		
		model.getConsistentExamples().add(positiveExample);
		
		//Negative example
		//!(d2=2 & e2=4 & b2=10 & c2=5 & d3=6 & e3=8 & b3=20 & c3=15 & g5=16800)
		Constraint negativeConstraint = Choco.and(Choco.and(Choco.and(Choco.and(Choco.and(Choco.and(Choco.and(Choco.and(Choco.and(Choco.and(Choco.eq(d2, 2), Choco.eq(e2, 4)), Choco.eq(b2, 10), Choco.eq(c2, 5)), Choco.eq(d3, 6), Choco.eq(e3, 8)), Choco.eq(b3, 20), Choco.eq(c3, 15),Choco.eq(g5, 16800))))))));
		Example<Constraint> negativeExample = new Example<>(true);
		negativeExample.addConstraint(negativeConstraint, "negative constraint derived from positive example constraints.");
				
		model.getInconsistentExamples().add(negativeExample);
		
		Debug.msg("Variables in model:");
		for(Variable variable : model.getVariables())
		{
			Debug.msg(variable.pretty());
		}
		
		Debug.msg("Correct constraints in model:");
		String correctStatements = Utilities.printConstraintList(model.getCorrectStatements(), model);
		for(Constraint correctStatement : model.getCorrectStatements())
		{
			Debug.msg(correctStatement.pretty());
		}
				
		Debug.msg("Possibly faulty statements in model:");
		String possiblyFaultyStatements = Utilities.printConstraintList(model.getPossiblyFaultyStatements(), model);
		for(Constraint possibleFault : model.getPossiblyFaultyStatements())
		{
			Debug.msg(possibleFault.pretty());
		}
		
		Debug.msg("Positive Example Constraints");
		for(Constraint exampleConstraint : positiveExample.constraints)
		{
			Debug.msg(exampleConstraint.pretty());
		}
		
		Debug.msg("Negative Example Constraint");
		for(Constraint negConstraint : negativeExample.constraints)
		{
			Debug.msg(negConstraint.pretty());
		}
		
		return model;
	}
}
