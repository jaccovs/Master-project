package tests.diagnosis;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import evaluations.conflictposition.MergeXPlainKC;
import evaluations.conflictposition.QXKCTools;
import evaluations.conflictposition.QuickExplainKC;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.mergexplain.MergeXplain;
import org.exquisite.tools.Debug;
import org.exquisite.tools.Utilities;

import java.util.List;

/**
 * Tests quickxplain
 * @author Dietmar
 *
 */
public class QuickXPlainTest {

	/**
	 * A main method to start the tests
	 * @param args
	 */
	public static void main(String[] args) {
		Debug.DEBUGGING_ON = true;
//		Debug.QX_DEBUGGING = true;
		System.out.println("Starting qxp tests\n");
		try {				
			QuickXPlainTest instance = new QuickXPlainTest();
						
//			System.out.println("-- Running qx test with model defineMiniModel(). ");
//			runQxTest(instance.defineMiniModel());
//			System.out.println("------------------------------------\n");
			
			System.out.println("-- Running qx test with small model defineModelForSmallExampleInPaper(). ");
			runQxTest(instance.defineModelForSmallExampleInPaper());
			System.out.println("------------------------------------\n");
						
//			System.out.println("-- Running qx test with model defineTestModelNoConstraints(). ");
//			runQxTest(instance.defineTestModelGtAndLt());
//			System.out.println("------------------------------------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		System.out.println("QXPlain tests ended");
	}
	
	/**
	 * Runs quickxplain with a given diagnosis model.
	 * @param model
	 */
	public static void runQxTest(DiagnosisModel<Constraint> model) {
		ExcelExquisiteSession<Constraint> sessionData = new ExcelExquisiteSession<>();
		sessionData.getDiagnosisModel() = model;
//		ConstraintsQuickXPlain qxp = new ConstraintsQuickXPlain(sessionData, null);
		ConstraintsQuickXPlain<Constraint> qxp = new MergeXplain<>(sessionData);
//		ParallelMergeXplain.maxThreadPoolSize = 1;
//		ConstraintsQuickXPlain qxp = new ParallelMergeXplain(sessionData, null);

		List<List<Constraint>> conflicts;
		try {
			conflicts = qxp.findConflicts();
			if (conflicts != null) {
				System.out.println("Found " + conflicts.size() + " conflicts.");
				for(List<Constraint> conflict: conflicts) {
					System.out.println("Found nodeLabel of size " + conflict.size());
					System.out.println("Candidates: " + Utilities.printConstraintList(conflict, sessionData.getDiagnosisModel()));
				}
			}
			else {
				System.out.println("QuickExplain returned empty list");
			}
		} catch (DomainSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Runs quickxplain with a given diagnosis model.
	 * @param model the diag model
	 * @param type the qxp engine type
	 * @param knownConflicts the list of known conflicts
	 */
	public static void runQxTest(DiagnosisModel<Constraint> model, QXType type, List<List<Constraint>> knownConflicts) {
		ExcelExquisiteSession sessionData = new ExcelExquisiteSession();
		sessionData.getDiagnosisModel() = model;
//		ConstraintsQuickXPlain qxp = new ConstraintsQuickXPlain(sessionData, null);
//		ConstraintsQuickXPlain qxp = new MergeXplain(sessionData, null);

		ConstraintsQuickXPlain<Constraint> qxp = null;
		int numberOfConstraints = model.getPossiblyFaultyStatements().size() + model
				.getCorrectStatements().size();
		if (type == QXType.MXP) {
			qxp = new MergeXPlainKC(sessionData, numberOfConstraints);
			QXKCTools.knownConflicts = knownConflicts;
			System.out.println("using mergexplain");
		}
		else if (type == QXType.QXP) {
			qxp = new QuickExplainKC(sessionData, numberOfConstraints);
			QXKCTools.knownConflicts = knownConflicts;
			System.out.println("using qxp w known conflits");
		}


		List<List<Constraint>> conflicts;
		try {
			conflicts = qxp.findConflicts();
			if (conflicts != null) {
				System.out.println("Found " + conflicts.size() + " conflicts.");
				for(List<Constraint> conflict: conflicts) {
					System.out.println("Found nodeLabel of size " + conflict.size());
					System.out.println("Candidates: " + Utilities.printConstraintList(conflict, sessionData.getDiagnosisModel()));
				}
			}
			else {
				System.out.println("QuickExplain returned empty list");
			}
		} catch (DomainSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setQXVariant(ConstraintsQuickXPlain<Constraint> quickXPlain) {
		// TODO Auto-generated method stub

	}

	/**
	 * A small test case
	 * @return a filled model
	 */
	public DiagnosisModel<Constraint> defineMiniModel() {
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();
		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 1,100));
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 1,100));
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 1,100));

		//formulae
		model.addPossiblyFaultyConstraint(Choco.eq(a1,3), "A1");
		model.addPossiblyFaultyConstraint(Choco.eq(a2, 5), "A2");
		model.addPossiblyFaultyConstraint(Choco.eq(b1, Choco.mult(a1, a2)), "B1 = a1 * a2"); // should be +

		//test case input
		model.addCorrectFormula(Choco.eq(b1, 8), "b1=8");
		System.out.println("Expected candidates are: { A1, B1 = a1 * a2 } or { A2, B1 = a1 * a2 }");
		return model;
	}

	/**
	 * Representing the small example from the paper.
	 */
	public DiagnosisModel<Constraint> defineModelForSmallExampleInPaper()
	{
		System.out.println("Defining model for small example from paper.");
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();

		IntegerVariable a1 = model.addIntegerVariable(Choco.makeIntVar("a1", 1,15));//max value from test examples
		IntegerVariable a2 = model.addIntegerVariable(Choco.makeIntVar("a2", 1,10));//max value from test examples
		IntegerVariable b1 = model.addIntegerVariable(Choco.makeIntVar("b1", 1,40));//as 15*2=30
		IntegerVariable b2 = model.addIntegerVariable(Choco.makeIntVar("b2", 1,30));//as 10*3=30
		IntegerVariable c1 = model.addIntegerVariable(Choco.makeIntVar("c1", 1,900));//as 30*30=2400
		IntegerVariable d1 = model.addIntegerVariable(Choco.makeIntVar("d1", 1,910));//as 900+10=910

		//add the suspect formulae...
		model.addPossiblyFaultyConstraint(Choco.eq(b1, Choco.mult(a1, 2)), "B1");
		model.addPossiblyFaultyConstraint(Choco.eq(b2, Choco.mult(a2, 3)), "B2");
		model.addPossiblyFaultyConstraint(Choco.eq(c1, Choco.mult(b1, b2)), "C1");//should be +
		model.addPossiblyFaultyConstraint(Choco.eq(d1, Choco.plus(c1, 10)), "D1");//should be *

		//some examples to test.

		//Add/remove //'s next to examples to show diagnosis result per example.
		//Example 1 //returns {B2}, {C1}
//		System.out.println("Expected candidates are { B2, C1 }");
//		model.addCorrectFormula(Choco.eq(a1, 1), "a1 = 1");
//		model.addCorrectFormula(Choco.eq(a2, 6), "a2 = 6");
//		model.addCorrectFormula(Choco.eq(c1, 20), "c1 = 20");

		//Example 2 //returns {B1}, {C1}  - this is different to the paper where b1 & b2 are returned as one diagnosis.
//		System.out.println("Expected candidates are {B1, C1}");
//		model.addCorrectFormula(Choco.eq(a1, 4), "a1 = 4");
//		model.addCorrectFormula(Choco.eq(a2, 5), "a2 = 5");
//		model.addCorrectFormula(Choco.eq(c1, 23), "c1 = 23");

		//Example 3 //returns {B1}, {B2}, {C1}
		System.out.println("Expected candidates are {B1, B2, C1}");
		model.addCorrectFormula(Choco.eq(a1, 15), "a1 = 15");
		model.addCorrectFormula(Choco.eq(a2, 10), "a2 = 10");
		model.addCorrectFormula(Choco.eq(c1, 60), "c1 = 60");

		//Example 4 //returns {B1}, {C1}
//		System.out.println("Expected candidates are {B1, C1}");
//		model.addCorrectFormula(Choco.eq(a1, 6), "a1 = 6");
//		model.addCorrectFormula(Choco.eq(a2, 1), "a2 = 1");
//		model.addCorrectFormula(Choco.eq(c1, 15), "c1 = 15");

		return model;
	}

	public DiagnosisModel<Constraint> defineTestModelGtAndLt() {
		System.out.println("Defining model");
		DiagnosisModel<Constraint> model = new DiagnosisModel<Constraint>();

		IntegerVariable a = model.addIntegerVariable(Choco.makeIntVar("a", 1,10));
		IntegerVariable b = model.addIntegerVariable(Choco.makeIntVar("b", 1,10));
		IntegerVariable c = model.addIntegerVariable(Choco.makeIntVar("c", 1,10));
		IntegerVariable d = model.addIntegerVariable(Choco.makeIntVar("d", 1,10));
		IntegerVariable e = model.addIntegerVariable(Choco.makeIntVar("e", 1,10));

		model.addPossiblyFaultyConstraint(Choco.gt(a,6), "C1 a>6");
		model.addPossiblyFaultyConstraint(Choco.eq(a,d), "C2 a==d");
		model.addPossiblyFaultyConstraint(Choco.eq(d, e), "C3 d==e");

		// Something assumed to be correct
		model.addCorrectFormula(Choco.eq(d,e), "C4 d==e");

		// An input value
		model.addCorrectFormula(Choco.eq(a,1), "a=1");
		model.addCorrectFormula(Choco.eq(b,1), "b=1");
		model.addCorrectFormula(Choco.eq(e,1), "e=1");

		System.out.println("Expected candidates to include in result: { C1 a>6 }");
		return model;
	}

	public enum QXType {QXP, MXP}
}