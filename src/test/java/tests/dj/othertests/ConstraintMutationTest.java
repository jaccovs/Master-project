package tests.dj.othertests;

import java.util.Iterator;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.MetaConstraint;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Some API testss
 * @author dietmar
 *
 */
public class ConstraintMutationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConstraintMutationTest test = new ConstraintMutationTest();
		System.out.println("Starting tests");
		try {
			test.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Test ends here");

	}
	
	
	/**
	 * A worker
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void run() throws Exception {
		CPModel cpmodel = new CPModel();
		
		IntegerVariable v1 = Choco.makeIntVar("v1", 1,3);
		IntegerVariable v2 = Choco.makeIntVar("v2", 1,3);
		IntegerVariable v3 = Choco.makeIntVar("v3", 1,3);
		
//		Constraint c0 = Choco.eq(v2, 3);
		Constraint c1 = Choco.and(Choco.eq(v1, v2),Choco.eq(v3, 1));
		
//		cpmodel.addConstraints(c0,c1);
		cpmodel.addConstraints(c1);
		cpmodel.addVariables(v1,v2,v3);

		// Let's see if we can look inside the constraints.
		Iterator<Constraint> ct_it = cpmodel.getConstraintIterator();
		MetaConstraint<Constraint> ct = null;
		while (ct_it.hasNext()) {
			ct = (MetaConstraint<Constraint>) ct_it.next();
			System.out.println("Got constraint: " + ct.getClass() + " " + ct.getConstraintType());
			Constraint[] cts = ct.getConstraints();
			// Make it an or...
			// only two arguments for the moment
			Constraint newConstraint = Choco.or(cts);
			cpmodel.removeConstraint(ct);
			cpmodel.addConstraint(newConstraint);
		}
		
//		if (true) {return;}
		CPSolver solver = new CPSolver();
		solver.read(cpmodel);
		solver.solve();
		
		int maxSolutions = 10;
		int cnt = 0;
		do {
			cnt++;
			System.out.println(solver.solutionToString());
			if (cnt > maxSolutions) {
				break;
			}
		} while (solver.nextSolution());
		
		
	}

}
