package tests.diagnosis;

import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.diagnosis.quickxplain.parallelqx.ParallelQXPlain;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Testing the parallel QuickXPlain version
 * @author dietmar
 *
 */
public class ParallelQXTest {

	
	/**
	 * Does the main work - define some test problems and run
	 * @param args
	 */
	public static void main(String[] args) {
		Debug.DEBUGGING_ON = false;
		Debug.QX_DEBUGGING = true;
		System.out.println("--Starting multi qxp tests");
		try {
			// First test
			ParallelQXTest instance = new ParallelQXTest();						
			System.out.println("-- Running qx test with model defineMiniModel(). ");
			runQxTest(instance.defineMiniModel(), true);
			System.out.println("------------------------------------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		System.out.println("QXPlain tests ended");

	}

	
	/**
	 * Define small problem that should not work..
	 * @return
	 */
	DiagnosisModel defineMiniModel(){		
		DiagnosisModel model = new DiagnosisModel();
		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 1,100));
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 1,100));
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 1,100));
		
		//formulae
		model.addPossiblyFaultyConstraint(Choco.eq(a1,3), "A1");
		model.addPossiblyFaultyConstraint(Choco.eq(a2,5), "A2");		
		model.addPossiblyFaultyConstraint(Choco.eq(b1, Choco.mult(a1, a2)), "B1 = a1 * a2"); // should be +
	
		//test case input
		model.addCorrectConstraint(Choco.eq(b1, 8), "b1=8");
		System.out.println("Expected candidates to include in result: { B1 = a1 * a2 }");
		
		
		// Simple add an independent conflict for the next test.
		IntegerVariable d1 = model.addIntegerVariable(Choco.makeIntVar("d1", 1,100));
		IntegerVariable d2 = model.addIntegerVariable(Choco.makeIntVar("d2", 1,100));
		IntegerVariable e1 = model.addIntegerVariable(Choco.makeIntVar("e1", 1,100));
		
		//formulae
		model.addPossiblyFaultyConstraint(Choco.eq(d1,3), "D1");
		model.addPossiblyFaultyConstraint(Choco.eq(d2,5), "D2");		
		model.addPossiblyFaultyConstraint(Choco.eq(e1, Choco.mult(d1, d2)), "E1 = d1 * d2"); // should be +
		System.out.println("Expected candidates to include in result: { e1 = d1 * d2 }");
	
		//test case input
		model.addCorrectConstraint(Choco.eq(e1, 8), "e1=8");

		
		return model;
	}	
	
	/**
	 * Runs quickxplain with a given diagnosis model.
	 * @param model
	 */
	private static void runQxTest(DiagnosisModel model, boolean parallel){
		ExquisiteSession sessionData = new ExquisiteSession();
		sessionData.diagnosisModel = model;
		long duration = 0;
		
		QuickXPlain qxp;
		if (parallel) {
			System.out.println("Multi threaded run ..");
			qxp = new ParallelQXPlain(sessionData, null);
		}
		else {
			System.out.println("Single threaded run ..");
			qxp = new QuickXPlain(sessionData, null);
		}
		List<List<Constraint>> conflicts = null;
		try {
			long start = System.currentTimeMillis();
			conflicts = qxp.findConflicts();
			long stop = System.currentTimeMillis();
			duration = stop - start;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Found " + conflicts.size() + " conflict(s) in " + (duration) + " ms");
		for (List<Constraint> c : conflicts) {
			System.out.println("Conflict (size: " + c.size() +"): " + Utilities.printConstraintList(c, sessionData.diagnosisModel));
		}

	}
}
