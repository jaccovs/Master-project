package tests.diagnosis;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.parallelqx.ParallelQXPlain;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.util.List;

/**
 * Testing the parallel ConstraintsQuickXPlain version
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
	 * Runs quickxplain with a given diagnosis model.
	 * @param model
	 */
	private static void runQxTest(DiagnosisModel<Constraint> model, boolean parallel) {
		ExcelExquisiteSession sessionData = new ExcelExquisiteSession();
		sessionData.getDiagnosisModel() = model;
		long duration = 0;

		ConstraintsQuickXPlain<Constraint> qxp;
		if (parallel) {
			System.out.println("Multi threaded run ..");
			qxp = new ParallelQXPlain<>(sessionData);
		}
		else {
			System.out.println("Single threaded run ..");
			qxp = new ConstraintsQuickXPlain<>(sessionData);
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
		System.out.println("Found " + conflicts.size() + " nodeLabel(s) in " + (duration) + " ms");
		for (List<Constraint> c : conflicts) {
			System.out.println("Conflict (size: " + c.size() +"): " + Utilities.printConstraintList(c, sessionData.getDiagnosisModel()));
		}

	}

	/**
	 * Define small problem that should not work..
	 *
	 * @return
	 */
	DiagnosisModel<Constraint> defineMiniModel() {
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();
		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 1, 100));
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 1, 100));
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 1, 100));

		//formulae
		model.addPossiblyFaultyConstraint(Choco.eq(a1, 3), "A1");
		model.addPossiblyFaultyConstraint(Choco.eq(a2, 5), "A2");
		model.addPossiblyFaultyConstraint(Choco.eq(b1, Choco.mult(a1, a2)), "B1 = a1 * a2"); // should be +

		//test case input
		model.addCorrectFormula(Choco.eq(b1, 8), "b1=8");
		System.out.println("Expected candidates to include in result: { B1 = a1 * a2 }");


		// Simple add an independent nodeLabel for the next test.
		IntegerVariable d1 = model.addIntegerVariable(Choco.makeIntVar("d1", 1, 100));
		IntegerVariable d2 = model.addIntegerVariable(Choco.makeIntVar("d2", 1, 100));
		IntegerVariable e1 = model.addIntegerVariable(Choco.makeIntVar("e1", 1, 100));

		//formulae
		model.addPossiblyFaultyConstraint(Choco.eq(d1, 3), "D1");
		model.addPossiblyFaultyConstraint(Choco.eq(d2, 5), "D2");
		model.addPossiblyFaultyConstraint(Choco.eq(e1, Choco.mult(d1, d2)), "E1 = d1 * d2"); // should be +
		System.out.println("Expected candidates to include in result: { e1 = d1 * d2 }");

		//test case input
		model.addCorrectFormula(Choco.eq(e1, 8), "e1=8");


		return model;
	}
}
