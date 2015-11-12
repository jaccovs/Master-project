package tests.diagnosis;

import java.util.ArrayList;
import java.util.List;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

public class OtherTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Runnding small tests");
		negativeExampleTest();

	}
	
	
	public static void negativeExampleTest() {
		try {
			IntegerVariable a = Choco.makeIntVar("a", 1,10);
			IntegerVariable b = Choco.makeIntVar("b", 1,10);
			IntegerVariable c = Choco.makeIntVar("c", 1,10);
			
			List<Constraint> negExample = new ArrayList<Constraint>();
			negExample.add(Choco.eq(a, 1));
			negExample.add(Choco.eq(b, 1));
			
			Constraint negExampleCt1 = generateConstraintFromNegExample(negExample);
			
			CPModel cpmodel = new CPModel();
			cpmodel.addVariables(a,b,c);
			cpmodel.addConstraint(negExampleCt1);
			
			cpmodel.addConstraint(Choco.eq(a, 1));
			cpmodel.addConstraint(Choco.eq(b, 1));
			
			CPSolver solver = new CPSolver();
			solver.read(cpmodel);
			
			if (solver.solve()) {
				System.out.println("Found a solution");
			}
			else {
				System.out.println("No solution found");
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param example
	 * @return
	 */
	static Constraint generateConstraintFromNegExample(List<Constraint> exampleConstraints) {
		Constraint constraintToAdd = Choco.TRUE;
		for (Constraint exConstraint : exampleConstraints) {
			constraintToAdd = Choco.and(constraintToAdd,exConstraint);
		}
		constraintToAdd = Choco.not(constraintToAdd);
		return constraintToAdd; 
	}
	

}
