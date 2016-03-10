package tests.ts;

import java.util.HashMap;
import java.util.Map;

import solver.Solver;
import solver.constraints.IntConstraintFactory;
import solver.exception.ContradictionException;
import solver.variables.IntVar;
import solver.variables.VariableFactory;

public class Choco3Bug {
	public static void main(String[] args) {
		Choco3Bug bug = new Choco3Bug();
		bug.run();
	}
	
	Solver solver;
	public Map<String, IntVar> variablesMap;
	
	// Change the min/max settings to avoid the bug
	int min = Integer.MIN_VALUE + 10;
	int max = Integer.MAX_VALUE - 10;
	
//	int min = Integer.MIN_VALUE + 1000000;
//	int max = Integer.MAX_VALUE - 1000000;

	private void run() {
		solver = new Solver();
		variablesMap = new HashMap<String, IntVar>() ;
		createVariables();
		addConstraints();
		solve();
	}

	private void solve() {
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (solver.findSolution())
			System.out.println("Solution found.");
		else
			System.out.println("No solution found.");
	}

	private void addConstraints() {
		IntVar A1 = variablesMap.get("WS_1_A1");
		IntVar A2 = variablesMap.get("WS_1_A2");
		IntVar C1 = variablesMap.get("WS_1_C1");
		IntVar C2 = variablesMap.get("WS_1_C2");
		IntVar D1 = variablesMap.get("WS_1_D1");
		IntVar B1 = variablesMap.get("WS_1_B1");
		IntVar B2 = variablesMap.get("WS_1_B2");
		IntVar tmp14 = variablesMap.get("tmp14");
		IntVar tmp15 = variablesMap.get("tmp15");
		
		solver.post(IntConstraintFactory.arithm(A2, "=", 5));
		solver.post(IntConstraintFactory.arithm(A1, "=", 4));
		solver.post(IntConstraintFactory.arithm(C2, "=", 5));
		solver.post(IntConstraintFactory.arithm(D1, "=", 230));
		
		solver.post(IntConstraintFactory.sum(new IntVar[] {B1, tmp15}, B2));
		solver.post(IntConstraintFactory.arithm(tmp14, "=", tmp15, "-", 5));
		solver.post(IntConstraintFactory.arithm(tmp14, "=", C1));
	}

	private void createVariables() {
		IntVar v = VariableFactory.bounded("WS_1_A1", 0, 100000, solver);
		variablesMap.put(v.getName(), v);
		
		v = VariableFactory.bounded("WS_1_A2", 0, 100000, solver);
		variablesMap.put(v.getName(), v);
		
		v = VariableFactory.bounded("WS_1_B1", 0, 100000, solver);
		variablesMap.put(v.getName(), v);
		
		v = VariableFactory.bounded("WS_1_B2", 0, 100000, solver);
		variablesMap.put(v.getName(), v);
		
		v = VariableFactory.bounded("WS_1_C1", 0, 100000, solver);
		variablesMap.put(v.getName(), v);
		
		v = VariableFactory.bounded("WS_1_D1", 0, 100000, solver);
		variablesMap.put(v.getName(), v);
		
		v = VariableFactory.bounded("WS_1_C2", 0, 100000, solver);
		variablesMap.put(v.getName(), v);
		
		v = VariableFactory.bounded("tmp14", min, max, solver);
		variablesMap.put(v.getName(), v);
		
		v = VariableFactory.bounded("tmp15", min, max, solver);
		variablesMap.put(v.getName(), v);
	}
}
