package tests.dj.heuristicsearch;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.HeuristicDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.util.List;

import static org.exquisite.core.measurements.MeasurementManager.*;

/**
 * Test driver for heuristic search
 * 
 * @author dietmar
 * 
 */
public class TestHeuristicSearch {

	/*
	 * Test entry point
	 */
	public static void main(String[] args) {
		System.out.println("Starting tests");
		try {
			TestHeuristicSearch test = new TestHeuristicSearch();
			test.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done with tests");

	}

	/**
	 * Create a small test model
	 *
	 * @return the diagnosis model
	 */
	static DiagnosisModel<Constraint> createSimpleDiagnosisModel() {
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();

		int domainSize = 10;

		IntegerVariable v1 = Choco.makeIntVar("v1", 0, domainSize);
		IntegerVariable v2 = Choco.makeIntVar("v2", 0, domainSize);
		IntegerVariable v3 = Choco.makeIntVar("v3", 0, domainSize);
		IntegerVariable v4 = Choco.makeIntVar("v4", 0, domainSize);
		IntegerVariable v5 = Choco.makeIntVar("v5", 0, domainSize);
		IntegerVariable v6 = Choco.makeIntVar("v6", 0, domainSize);

		model.addIntegerVariable(v1);
		model.addIntegerVariable(v2);
		model.addIntegerVariable(v3);
		model.addIntegerVariable(v4);
		model.addIntegerVariable(v5);
		model.addIntegerVariable(v6);

		Constraint c1 = Choco.eq(v1, v2);
		Constraint c2 = Choco.times(v1, v2, v3);
		Constraint c3 = Choco.eq(v4, v5);
		Constraint c4 = Choco.times(v4, v5, v6);

		model.addPossiblyFaultyConstraint(c1, "c1");
		model.addPossiblyFaultyConstraint(c2, "c2"); // should
														// be
														// plus
		model.addPossiblyFaultyConstraint(c3, "c3");
		model.addPossiblyFaultyConstraint(c4, "c4"); // should
														// be
														// plus
														// Map<Constraint,C3Runner>
														// c3runners = new
														// HashMap<Constraint,
														// C3Runner>();
		//
		// C3Runner c3runner = new C3Runner() {
		// public void postConstraint() {
		// solver.constraints.Constraint ct =
		// IntConstraintFactory.arithm(var("v1"), "=",var("v2"));
		// solver.post(ct);
		// }
		// };
		//
		// c3runners.put(c1,c3runner);
		//
		// c3runner = new C3Runner() {
		// public void postConstraint() {
		// solver.constraints.Constraint ct =
		// IntConstraintFactory.times(var("v1"),var("v2"),var("v3"));
		// solver.post(ct);
		// }
		// };
		// c3runners.put(c2,c3runner);
		//
		// c3runner = new C3Runner() {
		// public void postConstraint() {
		// solver.constraints.Constraint ct =
		// IntConstraintFactory.arithm(var("v4"), "=", var("v5"));
		// solver.post(ct);
		// }
		// };
		//
		// c3runners.put(c3,c3runner);
		//
		// c3runner = new C3Runner() {
		// public void postConstraint() {
		// solver.constraints.Constraint ct =
		// IntConstraintFactory.times(var("v4"), var("v5"),var("v6"));
		// solver.post(ct);
		//
		// }
		// };
		//
		// c3runners.put(c4,c3runner);
		//
		// // Set in the model
		// model.c3runners = new HashMap<Constraint, C3Runner>(c3runners);

		// model.addPossiblyFaultyConstraint(c2, "c2");
		// model.addPossiblyFaultyConstraint(c3, "c3");
		// model.addPossiblyFaultyConstraint(c4, "c4");
		// model.addPossiblyFaultyConstraint(c5, "c5");
		// model.addPossiblyFaultyConstraint(c6, "c6");
		// model.addPossiblyFaultyConstraint(c7, "c7");
		// model.addPossiblyFaultyConstraint(c8, "c8");

		Example pos1 = new Example();
		pos1.addConstraint(Choco.eq(v1, 3), "v1=3");
		pos1.addConstraint(Choco.eq(v3, 6), "v3=6");
		pos1.addConstraint(Choco.eq(v4, 3), "v4=3");
		pos1.addConstraint(Choco.eq(v6, 6), "v6=6");

		model.getConsistentExamples().add(pos1);

		// Create a runner for the example
		// c3runner = new C3Runner() {
		// public void postConstraint() {
		//
		// solver.constraints.Constraint ct =
		// IntConstraintFactory.arithm(var("v1"),"=",3);
		// solver.post(ct);
		// ct = IntConstraintFactory.arithm(var("v3"),"=",6);
		// solver.post(ct);
		// ct = IntConstraintFactory.arithm(var("v4"),"=",3);
		// solver.post(ct);
		// ct = IntConstraintFactory.arithm(var("v6"),"=",6);
		// solver.post(ct);
		// }
		// };
		//
		// model.c3examplerunners.put(pos1, c3runner);

		// Example pos2 = new Example();
		// pos2.addConstraint(Choco.eq(v1, 3), "v1=3");
		// pos1.addConstraint(Choco.eq(v6, 6), "v6=6");

		// model.getConsistentExamples().add(pos2);

		return model;

	}

	/**
	 * Running a simple test - use the problem
	 */
	void run() {
		DiagnosisModel<Constraint> model = createSimpleDiagnosisModel();

		// Create a session
		ExcelExquisiteSession sessionData = new ExcelExquisiteSession();
		sessionData.getDiagnosisModel() = model;
		sessionData.getConfiguration().searchStrategy = SearchStrategies.Default;
		sessionData.getConfiguration().searchDepth = 3;
		sessionData.getConfiguration().maxDiagnoses = 1;

		// Create the engine and run
		Debug.DEBUGGING_ON = false;

		IDiagnosisEngine<Constraint> engine = new HeuristicDiagnosisEngine(sessionData, 1);

		// To compare with the original results:
//		engine = EngineFactory.makeDAGEngineStandardQx(sessionData);
		// engine = EngineFactory.makeParaDagEngineStandardQx(sessionData, 2);

		List<Diagnosis<Constraint>> diagnoses = null;

		long stop = 0;
		long start = System.currentTimeMillis();
		try {
			diagnoses = engine.calculateDiagnoses();
			stop = System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Found " + diagnoses.size() + " diagnoses in "
				+ (stop - start) + " millisecs");


		// System.out.println("Constructed nodes: " + ((AbstractHSDagEngine)
		// engine).allConstructedNodes.getCollection().size());
		// // TEST ONLY

		if (diagnoses != null && diagnoses.size() > 0) {
			Diagnosis<Constraint> d = diagnoses.get(0);
			System.out.println("The diagn: " + d + " , elements: " + d.getElements().size());

			System.out.println(Utilities.printSortedDiagnoses(diagnoses, '\n'));
			System.out.println("Nb of solves : " + getCounter(COUNTER_CSP_SOLUTIONS));
			System.out.println("Nb of s calls: " + getCounter(COUNTER_SOLVER_CALLS).value());
			System.out.println("Nb of props  : " + getCounter(COUNTER_PROPAGATION).value());
		} else {
			System.out.println("No diagnosis found");
		}


		//NodeUtilities.traverseNode(engine.getRootNode(), sessionData.getDiagnosisModel().getConstraintNames());

	}
}