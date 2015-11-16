package tests.diagnosis;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.logging.ExquisiteLogger;

import java.util.*;
import java.util.logging.Logger;

/**
 * This class implements a variable-size representation of the example problem
 * in the JCKBSE 2010 paper
 * @author Dietmar
 *
 */
public class JCKBSETest {
	
	/**
	 * Change this variable to vary the size of the variables and constraints
	 */
	final static int NB_ROWS = 4;

	// max domain sizes
	final static int MAX_DOMAIN = 4000;
	
	// number of pos examples to generate
	final static int NB_POS_EXAMPLES = 3;

	// a logger for recording test results to file.
	final static Logger LOGGER = Logger.getLogger(JCKBSETest.class.getName());
	/**
	 * The main test driver
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting JCKBSE test ...");
		try {
			ExquisiteLogger.setup("JCKBSE.csv");
			new JCKBSETest().run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			System.out.println(" -- TEST JCKBSE ended ");
		}

	}
	
	/**
	 * The main worker method
	 */
	public void run() throws Exception {
		
		/** Create the model */
		JCKBSEKnowledgeBase kb = new JCKBSEKnowledgeBase();
		kb.defineModel(NB_ROWS);
		
		/** Generate some positive examples **/
		List<Map<IntegerVariable,Integer>> posExamples = new ArrayList<Map<IntegerVariable, Integer>>();
		for (int i=0;i<NB_POS_EXAMPLES;i++) {
			List<Constraint> inputs = kb.createRandomInputs();
			Solver solver = new CPSolver();
			Map<IntegerVariable,Integer> inputOutputPairs = calculateSolution(solver,kb,inputs);
			posExamples.add(inputOutputPairs);
			kb.printSolution(solver);
		}
		
		// mutate the knowledge base
		kb.mutate(1);
		// create the input constraints - just a simple test if the first mutation did work..
		List<Constraint> inputs = kb.createInputsFromInputOutputPairs(posExamples.get(0));
		// create a new solver and look for a solution
		Solver solver = new CPSolver();
		Map<IntegerVariable,Integer> inputOutputPairs = calculateSolution(solver,kb,inputs);
		if (inputOutputPairs != null) {
			kb.printSolution(solver);
		}
		else {
			// prepare and run the diagnosis component
			DiagnosisModel<Constraint> dmodel = new DiagnosisModel<>();
			// add all the variables
			for (IntegerVariable var : kb.variables) {
				dmodel.addIntegerVariable(var);
			}
			// copy the constraints
			for (Constraint c : kb.constraints) {
				dmodel.addPossiblyFaultyConstraint(c, kb.constraintNames.get(c));
			}
			// copy the positive examples
			List<Example<Constraint>> thePosExamples = new ArrayList<>();
			for (int i=0;i<NB_POS_EXAMPLES;i++) {
				Map<IntegerVariable, Integer> posExample = posExamples.get(i);
				Example ex = new Example();
				// construct and add input constraints
				Set<IntegerVariable> varsToSet = posExample.keySet();
				for (IntegerVariable var : varsToSet) {
					ex.addConstraint(Choco.eq(var,posExample.get(var)), var.getName());
				}
				thePosExamples.add(ex);
			}
			dmodel.setPositiveExamples(thePosExamples);

			ExquisiteSession sessionData = new ExquisiteSession();
			sessionData.diagnosisModel = dmodel;
			
			
			// Now diagnose?
			IDiagnosisEngine<Constraint> diagEngine = EngineFactory.makeDAGEngineStandardQx(sessionData);
//			diagEngine.setMaxSearchDepth(1);
			long start = System.currentTimeMillis();
			List<Diagnosis<Constraint>> diagnoses = diagEngine.calculateDiagnoses();
			long diagnosisTime = (System.currentTimeMillis() - start);
			System.out.println("Found " + diagnoses.size() + " diagnosis in " + diagnosisTime + " milliseconds");
			for (Diagnosis<Constraint> d : diagnoses) {
				System.out.println("Diagnosis: ");
				List<Constraint> constraints = (d.getElements());
				for (Constraint c : constraints) {
					System.out.println(c.getName() + " " + kb.constraintNames.get(c));
				}
				System.out.println();
				
				System.out.println("-----");
			}
			//"#Rows, #Vars., #Constrs., #CSP prop., #CSP Solved, #Diag. found, Diag. time(ms)\n";
			String logEntry = NB_ROWS + ", " + kb.variables.size() + ", " + kb.constraints.size() + ", todo, todo, " + diagnoses.size() + ", " + diagnosisTime;
			LOGGER.info(logEntry);			
			System.out.println("Found " + diagnoses.size() + " diagnosis in " + diagnosisTime + " milliseconds");

		}		
	}
	
	/**
	 * A method that searches for a solution for a given knowledge base and inputs
	 * @param kb the knowledge base
	 * @param inputs the input values
	 * @return the list of integer variables plus their values, and null if no solution was found
	 */
	public Map<IntegerVariable, Integer> calculateSolution(Solver solver, JCKBSEKnowledgeBase kb, List<Constraint> inputs) {
		// Copy the variables and the constraints to the cp model
		CPModel cpmodel = kb.createCPModel(inputs);
		solver.read(cpmodel);
		if (solver.solve()) {
			System.out.println("Found solution");
			// copy things from the knowledge base
			return kb.getInputOutputValuesOfSolution(solver);
		}
		else {
			System.out.println("No solution found");
			return null;
		}
	}
	
	/**
	 * An inner class to model the knowledge base consisting of variables and constraints but without inputs
	 * The knowledge base is specific for the given setting
	 * @author Dietmar
	 *
	 */
	class JCKBSEKnowledgeBase {
		// Inputs
		// row C
		public IntegerVariable[] productionCosts;
		// row D
		public IntegerVariable[] salesPrizes;
		// Output variables
		public IntegerVariable sales;
		public IntegerVariable revenue;
		public IntegerVariable productionCost;
		public IntegerVariable profit;
		// The constraints
		public List<Constraint> constraints;
		// names for the constraints
		public HashMap<Constraint, String> constraintNames;
		// collecting all the vairables
		public List<IntegerVariable> variables;
		// matrix E3 to Q
		IntegerVariable[][] salesPerMonth;
		// Intermediate calculations
		IntegerVariable[] r_values;
		IntegerVariable[] s_values;
		IntegerVariable[] t_values;
		// the list of constraints to be mutated
		List<Constraint> mutationConstraints = new ArrayList<Constraint>();

		int nbRows = -1;
		
		/**
		 * Method to initialize the model for a given number of rows
		 * @param nbRows
		 * @param faulty indicates if an error should be injected or not
		 */
		public void defineModel(int nbRows) {
			System.out.println("Defining the model with " + nbRows + " rows");
			this.nbRows = nbRows;
			// create the input data arrays
			productionCosts = Choco.makeIntVarArray("ProductionCosts", nbRows, Options.V_BOUND);
			salesPrizes = Choco.makeIntVarArray("SalesPrizes", nbRows, Options.V_BOUND);
			salesPerMonth = new IntegerVariable [nbRows][12];
			
			for (int i=0;i<nbRows;i++) {
				// Create all the variables
				IntegerVariable[] newRow = Choco.makeIntVarArray("SalesRow_" + i, 12, 0, MAX_DOMAIN, Options.V_BOUND);
				salesPerMonth[i] = newRow;
			}
			
			// create the intermediate variables
			r_values = Choco.makeIntVarArray("R_values", nbRows, 0, MAX_DOMAIN, Options.V_BOUND);
			s_values = Choco.makeIntVarArray("S_values", nbRows, 0, MAX_DOMAIN, Options.V_BOUND);
			t_values = Choco.makeIntVarArray("T_values", nbRows, 0, MAX_DOMAIN, Options.V_BOUND);

			// create the outputs
			sales = Choco.makeIntVar("Sales", 0, MAX_DOMAIN);
			revenue = Choco.makeIntVar("Revenue", 0, MAX_DOMAIN);
			productionCost = Choco.makeIntVar("Cost", 0, MAX_DOMAIN);
			profit = Choco.makeIntVar("Profit", 0, MAX_DOMAIN);
			
			// We are done with the variables. Put them in the global list for convenient access later on.
			this.variables = new ArrayList<IntegerVariable>();
			this.variables.add(sales);
			this.variables.add(revenue);
			this.variables.add(productionCost);
			this.variables.add(profit);
			
			for (int i=0;i<this.nbRows;i++) {
				this.variables.add(productionCosts[i]);
				this.variables.add(salesPrizes[i]);
				this.variables.add(r_values[i]);
				this.variables.add(s_values[i]);
				this.variables.add(t_values[i]);
				IntegerVariable[] oneRow = salesPerMonth[i];
				for (int j=0;j<12;j++) {
					this.variables.add(oneRow[j]);
				}
			}
			
			
			// Now the constraints
			constraints = new ArrayList<Constraint>();
			constraintNames = new HashMap<Constraint, String>();
			
			Constraint c;
			for (int i=0;i<nbRows;i++) {
				// total sales per year
				c = Choco.eq(r_values[i], Choco.sum(salesPerMonth[i]));
				constraints.add(c);
				constraintNames.put(c, "R" + (i+2));

				// sales numbers times sales prize
				c = Choco.eq(s_values[i], Choco.mult(r_values[i],salesPrizes[i]));
				// remember the mutation constraint
				if (i==0) {
					mutationConstraints.add(c);
				}
				
				constraints.add(c);
				constraintNames.put(c, "S" + (i+2));
				
				// production costs
				c = Choco.eq(t_values[i], Choco.mult(r_values[i],productionCosts[i]));
				constraints.add(c);
				constraintNames.put(c, "T" + (i+2));
			}

			// Add the final constraints
			c = Choco.eq(sales, Choco.sum(r_values));
			constraints.add(c);
			constraintNames.put(c, "S13");
			
			c = Choco.eq(revenue, Choco.sum(s_values));
			constraints.add(c);
			constraintNames.put(c, "S14");

			c = Choco.eq(productionCost, Choco.sum(t_values));
			constraints.add(c);
			constraintNames.put(c, "S15");

			c = Choco.eq(profit, Choco.minus(revenue, productionCost));
			constraints.add(c);
			constraintNames.put(c, "S16");
		}
		
		/**
		 * Creates a set of random inputs for a given knowledgebase
		 * @return a set of constraints on the input variables
		 */
		public List<Constraint> createRandomInputs() {
			List<Constraint> inputConstraints = new ArrayList<Constraint>();
			
			Random random = new Random();
			int MAX_VALUE = 10;
			
			for (int i=0;i<this.nbRows;i++) {
				// production costs
				int prodCost = random.nextInt(MAX_VALUE)+1;
				inputConstraints.add(Choco.eq(productionCosts[i], prodCost));
				// make sure that sales prizes are always higher
				inputConstraints.add(Choco.eq(salesPrizes[i], (prodCost + 10)));
				// now the sales numbers
				IntegerVariable[] salesRow = salesPerMonth[i];
				for (int j=0;j<12;j++) {
					// Add some small sales numbers
					inputConstraints.add(Choco.eq(salesRow[j], random.nextInt(MAX_VALUE/2)));
				}
			}
			System.out.println("NB constraints " + inputConstraints.size());
			return inputConstraints;
		}
		
		
		/**
		 * Creates a set of constraints on variables based on IO-pairs
		 * @return the list of unary constraints
		 */
		public List<Constraint> createInputsFromInputOutputPairs(Map<IntegerVariable,Integer> inputOutputPairs) {
			List<Constraint> inputConstraints = new ArrayList<Constraint>();
			Set<IntegerVariable> variables = inputOutputPairs.keySet();
			for (IntegerVariable var : variables) {
				inputConstraints.add(Choco.eq(var,inputOutputPairs.get(var)));
			}
			return inputConstraints;
		}
		
		
		
		/**
		 * Creates a new CP model with all the definitions and inputs
		 * @param cpmodel the cp model to fill
		 * @param inputs the inputs to add
		 */
		public CPModel createCPModel(List<Constraint> inputs) {
			CPModel cpmodel = new CPModel();
			for (int i=0;i<this.nbRows;i++) {
				// inputs
				cpmodel.addVariable(productionCosts[i]);
				cpmodel.addVariable(salesPrizes[i]);
				for (int j=0;j<12;j++) {
					cpmodel.addVariable(salesPerMonth[i][j]);
				}
				// intermediate
				cpmodel.addVariable(r_values[i]);
				cpmodel.addVariable(s_values[i]);
				cpmodel.addVariable(t_values[i]);
			}
			// outputs
			cpmodel.addVariable(sales);
			cpmodel.addVariable(revenue);
			cpmodel.addVariable(profit);
			cpmodel.addVariable(productionCost);
			
			// add the constraints
			for (Constraint c : this.constraints) {
				cpmodel.addConstraint(c);
			}
			// add the inputs
			for (Constraint input : inputs) {
				cpmodel.addConstraint(input);
			}
			return cpmodel;
		}
		
		
		/**
		 * Returns the input and output values only
		 * @param solver the solver containing the solution
		 * @return a map of variables and values (limited to integers at the moment)
		 */
		Map<IntegerVariable, Integer> getInputOutputValuesOfSolution(Solver solver) {
			Map<IntegerVariable, Integer> result = new HashMap<IntegerVariable, Integer>();
			// copy the inputs for all rows
			for (int i=0;i<this.nbRows;i++) {
				result.put(productionCosts[i],solver.getVar(productionCosts[i]).getVal());
				result.put(salesPrizes[i],solver.getVar(salesPrizes[i]).getVal());
				IntegerVariable[] oneRow = salesPerMonth[i];
				for (int j=0;j<12;j++) {
					result.put(oneRow[j], solver.getVar(oneRow[j]).getVal());
				}
			}
			// copy the final outputs
			result.put(productionCost, solver.getVar(productionCost).getVal());
			result.put(sales, solver.getVar(sales).getVal());
			result.put(revenue, solver.getVar(revenue).getVal());
			result.put(profit, solver.getVar(profit).getVal());
			
			return result;
		}
		
		
		/**
		 * Modifies the knowledge base with a mutation error
		 * @param nbMutations
		 */
		public void mutate(int nbMutations) {
			// first mutation, do this in any case
			// change the operation of the s_value calculation of the first row to an addition (instead of a multiplicaiton)
			this.constraints.remove(mutationConstraints.get(0));
//			Correct version -> to test if still works
//			this.constraints.add(Choco.eq(s_values[0], Choco.mult(r_values[0],salesPrizes[0])));
//			Mutated version
			Constraint c = Choco.eq(s_values[0], Choco.plus(r_values[0],salesPrizes[0]));
			this.constraints.add(c);
			this.constraintNames.put(c, "Mutated s_value 0");
			
			if (nbMutations > 0) {
				// do more mutations
			}
		}
		
		/**
		 * Prints the inputs and outputs
		 * @param solver the solver containing the solution
		 */
		public void printSolution(Solver solver) {
			for (int i=0;i<this.nbRows;i++) {
				System.out.print(solver.getVar(productionCosts[i]) + "\t" + solver.getVar(salesPrizes[i]) + " || \t");
				
				IntegerVariable[] oneRow = salesPerMonth[i];
				for (int j=0;j<12;j++) {
					System.out.print(solver.getVar(oneRow[j]).getVal() + "\t");
				}
				System.out.print(" ||\t");
				System.out.println(solver.getVar(r_values[i]) + "\t" + solver.getVar(s_values[i]) + "\t" + solver.getVar(t_values[i]));
			}
			System.out.println("\t" + solver.getVar(productionCost));
			System.out.println("\t" + solver.getVar(sales));
			System.out.println("\t" + solver.getVar(revenue));
			System.out.println("\t" + solver.getVar(profit));
			
			// The production costs
		}
	}
}
