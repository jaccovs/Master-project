package tests.diagnosis;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.datamodel.ExquisiteGraph;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.engines.ParallelHSDagBuilder;
import org.exquisite.diagnosis.engines.common.NodeUtilities;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.tools.Utilities;

import java.util.List;

public class ParallelDAGTest {

	public static void main(String[]args) {
		System.out.println("Starting Parallel DAG test");
		new ParallelDAGTest().run();
		System.out.println("Parallel DAG Test ended");
	}
	
	/**
	 * A runner method
	 */
	public void run() {
		try {
			ExquisiteSession sessionData = new ExquisiteSession();
			sessionData.diagnosisModel = defineMiniModel();
			sessionData.graph = new ExquisiteGraph<String>();
			
			System.out.println("Created the model with examples");
			ParallelHSDagBuilder engine = (ParallelHSDagBuilder) EngineFactory.makeParaDagEngineStandardQx(sessionData, 4);
//			engine.setMaxSearchDepth(-1);			
//			engine.setMaxDiagnoses(-1);
						
			List<Diagnosis> diagnoses = engine.calculateDiagnoses();
			System.out.println("Found " + diagnoses.size() + " diagnoses");
			for (int i = 0; i < diagnoses.size(); i++) 
			{
				System.out.println("-- Diagnosis #" + i);
				System.out.println("    " + Utilities.printConstraintList((diagnoses.get(i).getElements()), sessionData.diagnosisModel));
				System.out.println("--");
			}
			//
			
			System.out.println("#cspsolved: " + engine.getCspSolvedCount());
			System.out.println("#propagations: " + engine.getPropagationCount());
			System.out.println("#solver calls: " + engine.getSolverCalls());
			//*/
			
			isConstraintSetContainedInListTest();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	DiagnosisModel<Constraint> defineMiniModel()
	{
		DiagnosisModel<Constraint> model = new DiagnosisModel<>();
		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 1,100));
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 1,100));
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 1,100));
		
		model.addPossiblyFaultyConstraint(Choco.eq(a1,3), "a1 = 3");
		model.addPossiblyFaultyConstraint(Choco.eq(a2,5), "a2 = 5");		
		model.addPossiblyFaultyConstraint(Choco.eq(b1, Choco.mult(a1, a2)), "B1 = a1 * a2"); // should be +

		Example<Constraint> exTestExample = new Example<>();
		exTestExample.addConstraint(Choco.eq(a1, 3), "a1=3");
		exTestExample.addConstraint(Choco.eq(a2, 5), "a2=5");
		exTestExample.addConstraint(Choco.eq(b1, 8), "b1=8");

		model.getPositiveExamples().add(exTestExample);		
		return model;
	}	
	
	public void isConstraintSetContainedInListTest(){
		MockNodeData mockNodeData = MockNodeData.duplicatePathsExample();
		
		System.out.println("BEFORE DUPLICATE CHECK.");
		NodeUtilities.traverseNode(mockNodeData.rootNode, mockNodeData.constraints);
		ParallelHSDagBuilder.checkForDuplicatePaths(mockNodeData.graph);
		System.out.println("AFTER DUPLICATE CHECK.");
		NodeUtilities.traverseNode(mockNodeData.rootNode, mockNodeData.constraints);
	}

}
