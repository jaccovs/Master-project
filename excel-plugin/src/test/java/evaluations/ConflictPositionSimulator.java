package evaluations;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import evaluations.configuration.AbstractRunConfiguration;
import evaluations.configuration.AbstractScenario;
import evaluations.configuration.PosSimScenario;
import evaluations.configuration.PosSimScenario.ConflictPosition;
import evaluations.configuration.StdRunConfiguration;
import evaluations.configuration.StdRunConfiguration.ExecutionMode;
import evaluations.conflictposition.QXKCTools;
import evaluations.conflictposition.QXKCTools.WaitMode;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.engines.AbstractHSDagEngine.QuickXplainType;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import tests.dj.qxsim.SimUtilities;

import java.io.File;
import java.util.*;

public class ConflictPositionSimulator extends AbstractEvaluation<Constraint> {
	
	// The gaussian to create the nodeLabel sizes
	public static Random random = new Random();
	
	// ----------------------------------------------------
	// Directories
	static String inputFileDirectory = "experiments/ConflictPosSim/";
	static String logFileDirectory = "logs/ConflictPosSim/";
	// Number of runs
	static int nbInitRuns = 20;
	// ----------------------------------------------------
	static int nbTestRuns = 100;
	// Run configurations
	static StdRunConfiguration[] runConfigurations = new StdRunConfiguration[] {
		new StdRunConfiguration(ExecutionMode.singlethreaded, 1, false),
		new StdRunConfiguration(ExecutionMode.mergexplain, 1, false),
		new StdRunConfiguration(ExecutionMode.parallelmergexplain, 4, false),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 2, false),
//		new StdRunConfiguration(ExecutionMode.levelparallel, 2, false),
//
//		new StdRunConfiguration(ExecutionMode.heuristic, 2, false),
//		new StdRunConfiguration(ExecutionMode.hybrid, 2, false),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 4, false),
//		new StdRunConfiguration(ExecutionMode.levelparallel, 4, false),
//		new StdRunConfiguration(ExecutionMode.hybrid, 4, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 1, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 2, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 3, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 4, false),
//		new StdRunConfiguration(ExecutionMode.prdfs, 1, false),
//		new StdRunConfiguration(ExecutionMode.prdfs, 2, false),
//		new StdRunConfiguration(ExecutionMode.prdfs, 3, false),
//		new StdRunConfiguration(ExecutionMode.prdfs, 4, false),
	};
	static PosSimScenario[] scenarios = new PosSimScenario[] {
//		new PosSimScenario(200, 5, 2, ConflictPosition.distributed, WaitMode.quadratic, -1, 5, 1),
//		new PosSimScenario(200, 5, 2, ConflictPosition.left, WaitMode.quadratic, -1, 5, 1),
//		new PosSimScenario(200, 5, 2, ConflictPosition.right, WaitMode.quadratic, -1, 5, 1),
//		new PosSimScenario(200, 5, 2, ConflictPosition.leftAndRight, WaitMode.quadratic, -1, 5, 1),
//		new PosSimScenario(200, 5, 2, ConflictPosition.leftAndRightNeighboring, WaitMode.quadratic, -1, 5, 1),
//		new PosSimScenario(200, 5, 2, ConflictPosition.neighboring, WaitMode.quadratic, -1, 5, 1),

//		new PosSimScenario(200, 5, 2, ConflictPosition.distributed, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(200, 5, 2, ConflictPosition.left, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(200, 5, 2, ConflictPosition.right, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(200, 5, 2, ConflictPosition.leftAndRight, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(200, 5, 2, ConflictPosition.leftAndRightNeighboring, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(200, 5, 2, ConflictPosition.neighboring, WaitMode.quadratic, -1, 5, 10),


//		new PosSimScenario(200, 5, 2, ConflictPosition.right, WaitMode.constant, -1, 5, 1),

//		new PosSimScenario(100, 5, 2, ConflictPosition.distributed, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(100, 5, 2, ConflictPosition.left, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(100, 5, 2, ConflictPosition.right, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(100, 5, 2, ConflictPosition.leftAndRight, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(100, 5, 2, ConflictPosition.neighboring, WaitMode.quadratic, -1, 5, 10),

//		new PosSimScenario(50, 5, 2, ConflictPosition.distributed, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 5, 2, ConflictPosition.left, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 5, 2, ConflictPosition.right, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 5, 2, ConflictPosition.leftAndRight, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 5, 2, ConflictPosition.neighboring, WaitMode.quadratic, -1, 5, 10),

//		new PosSimScenario(50, 15, 4, ConflictPosition.distributed, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 15, 4, ConflictPosition.left, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 15, 4, ConflictPosition.right, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 15, 4, ConflictPosition.leftAndRight, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 15, 4, ConflictPosition.neighboring, WaitMode.quadratic, -1, 5, 10),

//		new PosSimScenario(100, 5, 4, ConflictPosition.distributed, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(100, 5, 4, ConflictPosition.left, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(100, 5, 4, ConflictPosition.right, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(100, 5, 4, ConflictPosition.leftAndRight, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(100, 5, 4, ConflictPosition.neighboring, WaitMode.quadratic, -1, 5, 10),

		new PosSimScenario(100, 15, 4, ConflictPosition.distributed, WaitMode.quadratic, -1, 5, 10),
		new PosSimScenario(100, 15, 4, ConflictPosition.left, WaitMode.quadratic, -1, 5, 10),
		new PosSimScenario(100, 15, 4, ConflictPosition.right, WaitMode.quadratic, -1, 5, 10),
		new PosSimScenario(100, 15, 4, ConflictPosition.leftAndRight, WaitMode.quadratic, -1, 5, 10),
		new PosSimScenario(100, 15, 4, ConflictPosition.neighboring, WaitMode.quadratic, -1, 5, 10),

//		new PosSimScenario(50, 20, 4, ConflictPosition.distributed, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 20, 4, ConflictPosition.left, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 20, 4, ConflictPosition.right, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 20, 4, ConflictPosition.leftAndRight, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 20, 4, ConflictPosition.neighboring, WaitMode.quadratic, -1, 5, 10),
//
//		new PosSimScenario(50, 9, 4, ConflictPosition.distributed, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 9, 4, ConflictPosition.left, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 9, 4, ConflictPosition.right, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 9, 4, ConflictPosition.leftAndRight, WaitMode.quadratic, -1, 5, 10),
//		new PosSimScenario(50, 9, 4, ConflictPosition.neighboring, WaitMode.quadratic, -1, 5, 10),


//		new PosSimScenario(200, 9, 4, ConflictPosition.distributed, WaitMode.quadratic, -1, 5, 1),
//		new PosSimScenario(200, 9, 4, ConflictPosition.left, WaitMode.quadratic, -1, 5, 1),
//		new PosSimScenario(200, 9, 4, ConflictPosition.right, WaitMode.quadratic, -1, 5, 1),
//		new PosSimScenario(200, 9, 4, ConflictPosition.leftAndRight, WaitMode.quadratic, -1, 5, 0),
//		new PosSimScenario(200, 9, 4, ConflictPosition.neighboring, WaitMode.quadratic, -1, 5, 0),
	};
	public String conflictDirectory = "experiments/ConflictPosSim/";
	boolean persistent = true;
	boolean differentModelsForRuns = true;

	/**
	 * Create a nodeLabel from a given list
	 *
	 * @param avgConflictSize the expected size
	 * @param whereToPickFrom the list to chose from
	 * @return the random list
	 */
	public static List<Constraint> createConflictFromList(int avgConflictSize, List<Constraint> whereToPickFrom) {
		List<Constraint> shuffledList = new ArrayList<Constraint>(whereToPickFrom);
		List<Constraint> newConflict = new ArrayList<Constraint>();
		// default strategy
		// randomly pick avgConflictSize elements from the set of constraints
		// do not use exactly the avg, but the gaussian around it
		double g = random.nextGaussian() * (avgConflictSize * 0.2) + avgConflictSize;
		long nbConflictsThisTime = Math.round(g);

//		System.out.println("Conflict size (avg should be: " + avgConflictSize + ") " + nbConflictsThisTime);
		while (newConflict.size() < nbConflictsThisTime) {
			Collections.shuffle(shuffledList);
			Constraint randomElement = shuffledList.get(0);
			if (!newConflict.contains(randomElement)) {
				newConflict.add(randomElement);

			}
		}
		return newConflict;
	}

	/**
	 * Add a nodeLabel to the list of it is not already there
	 *
	 * @param generatedConflicts the existing conflicts
	 * @param newConflict        the new conflicts
	 * @return true if a new element was added
	 */
	public static boolean addConflictNoDups(List<List<Constraint>> generatedConflicts,
											List<Constraint> newConflict) {
		boolean alreadyThere = false;
		for (List<Constraint> known : generatedConflicts) {
			if (known.containsAll(newConflict)) {
				alreadyThere = true;
				break;
			}
		}
		if (!alreadyThere) {
			generatedConflicts.add(newConflict);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create a number of dummy constraints
	 *
	 * @param kbSize nb of cts to be created
	 * @return the list
	 */
	public static DiagnosisModel<Constraint> createModel(int kbSize) {
		DiagnosisModel<Constraint> result = new DiagnosisModel<Constraint>();
		for (int i = 0; i < kbSize; i++) {
			String name = "C" + i;
			IntegerVariable v = Choco.makeIntVar(name, 0, 1);
			result.addPossiblyFaultyConstraint(Choco.eq(v, 1), name);
		}
		return result;
	}

	public static void main(String[] args) {
		ConflictPositionSimulator conflictPositionSimulator = new ConflictPositionSimulator();
		conflictPositionSimulator.runTests(nbInitRuns, nbTestRuns, runConfigurations, scenarios);
	}
	
	@Override
	public String getEvaluationName() {
		return "ConflictPositionSimulator";
	}

	@Override
	public String getResultPath() {
		return logFileDirectory;
	}


	/**
	 * Do the work for one simulation
	 * @throws Exception
	 */
//	public void runOne(boolean evaluate, QuickXplainType qxtype) throws Exception {
//		this.qxtype = qxtype;
//		// Run the scenario a number of times
//		// Get the file first
//		
//		
//
//		
//		diagnose(evaluate);
////		System.out.println("done with one");
//
//	}

	/**
	 * Searches for diagnoses given the parameters
	 *
	 * @param evaluate
	 * @throws Exception
	 */
//	public void diagnose(boolean evaluate) throws Exception {
//		
//		
//
//		long start = System.nanoTime();
//		List<Diagnosis> diagnoses = engine.calculateDiagnoses();
//		long stop  = System.nanoTime();
//		System.out.println("Found " + diagnoses.size() + " diagnoses in " + (stop-start)/1000000 + "ms.");
////		for (Diagnosis diag : diagnoses) {
////			System.out.println(Utilities.printConstraintList(diag.getElements(), model));
////		}
//		if (evaluate) {
//			if (qxtype == QuickXplainType.QX_KC) {
//				evalQX.analyzeRun(engine, start, stop);
//			}
//			else if ((qxtype == QuickXplainType.MX_KC)) {
//				evalMX.analyzeRun(engine, start, stop);
//			}
//			else if ((qxtype == QuickXplainType.PMX_KC)) {
//				evalPMX.analyzeRun(engine, start, stop);
//			}
//		}
//		
//	}

	@Override
	public String getConstraintOrderPath() {
		return conflictDirectory;
	}
	
	@Override
	protected boolean shouldShuffleConstraints() {
		return false;
	}
	
	@Override
	public boolean usePersistentConstraintOrder() {
		return false;
	}
	
	@Override
	public IDiagnosisEngine<Constraint> prepareRun(
			AbstractRunConfiguration abstractRunConfiguration,
			AbstractScenario abstractScenario, int subScenario, int iteration) {

		StdRunConfiguration runConfiguration = (StdRunConfiguration) abstractRunConfiguration;
		PosSimScenario scenario = (PosSimScenario)abstractScenario;


//		System.out.print(iteration);


//		model = new DiagnosisModel();
//		constraintNames = new LinkedHashMap<Constraint,String>();
//		knownConflicts = new ArrayList<List<Constraint>>();
		DiagnosisModel<Constraint> model;
		try {
			model = loadOrCreateModel(scenario.kbSize, scenario.nbConflicts, scenario.avgConflictSize, scenario.conflictPosition, iteration);
		} catch (Exception e) {
			addError("Could not load or create model", runConfiguration, subScenario, iteration);
			return null;
		}



		// do a diagnosis
		ExcelExquisiteSession sessionData = new ExcelExquisiteSession();
		sessionData.getDiagnosisModel() = model;
		// create a dummy example
		// Add a dummy example
		Example exTestExample = new Example();
		exTestExample.addConstraint(SimUtilities.createDummyConstraint(), "dummyexample");
		model.getConsistentExamples().add(exTestExample);

		chooseEngineType(scenario, runConfiguration);

		switch (AbstractHSDagEngine.USE_QXTYPE) {
		case QuickXplain:
			AbstractHSDagEngine.USE_QXTYPE = QuickXplainType.QX_KC;
			break;
		case MergeXplain:
			AbstractHSDagEngine.USE_QXTYPE = QuickXplainType.MX_KC;
			break;
		case ParallelMergeXplain:
			AbstractHSDagEngine.USE_QXTYPE = QuickXplainType.PMX_KC;
			break;


			default:
			break;
		}

		// create an engine and register the known conflicts to qx
		AbstractHSDagEngine engine = new HSDagEngine(sessionData);
//		AbstractHSDagEngine.USE_QXTYPE = qxtype;
		QXKCTools.MAX_WAIT_TIME = scenario.maxWaitTime;
		QXKCTools.WAIT_MODE = scenario.waitMode;

		// Limit the number of diags to search for
		sessionData.getConfiguration().maxDiagnoses = scenario.maxDiags;

		return engine;
	}

	/**
	 * Create a new model with the given data or load it from the file system
	 * if it was already there. Also fills the required datastructures (model, known conflicts etc)
	 * Not thread-safe
	 * @param f the filehandle
	 * @throws Exception
	 */
	private DiagnosisModel<Constraint> loadOrCreateModel(int kbSize, int nbConflicts, int avgConflictSize,
														 ConflictPosition conflictPosition, int run) throws Exception {

		Map<Constraint, String> constraintNames;
		List<List<Constraint>> knownConflicts;

		constraintNames = new LinkedHashMap<Constraint,String>();
		knownConflicts = new ArrayList<List<Constraint>>();

		String constraintFile = createFileName(kbSize, nbConflicts, avgConflictSize, conflictPosition, run);
//		System.out.println("Running scenario: " + constraintFile);
		String fname = constraintFile + ".txt";
		File f = new File(fname);

		// Create folder for scenario files
		new File(f.getParent()).mkdirs();

		DiagnosisModel<Constraint> model;
//		System.out.println("Filename called: " + f.getName());
		// load an existing file
		if (persistent && f.exists()) {
			model = new DiagnosisModel<Constraint>();
//			System.out.println("Loading from file system");
			SimUtilities.loadConstraintsFromFile(f, constraintNames, knownConflicts);
			// create a diagnosis model from this information
			for (Constraint c : constraintNames.keySet()) {
				model.addPossiblyFaultyConstraint(c, constraintNames.get(c));
			}

		}
		else {
//			System.out.println("Creating new problem");
			model = createModel(kbSize);
			List<Constraint> constraints = model.getPossiblyFaultyStatements();

			knownConflicts = createKnownConflicts(constraints, nbConflicts, avgConflictSize, conflictPosition);
		}
		// how many constraints
//		System.out.println("Loaded " + knownConflicts.size() + " conflicts in: ") ;
		List<Constraint> theConstraints = new ArrayList<Constraint>();
		for (Constraint c : model.getConstraintNames().keySet()) {
			theConstraints.add(c);
		}
//		System.out.println(Utilities.printConstraintList(theConstraints, model));
//		for (List<Constraint> nodeLabel : knownConflicts)  {
//			System.out.println("Known Conflict: " + Utilities.printConstraintList(nodeLabel, model));
//		}
		if (persistent && !f.exists()) {
			SimUtilities.writeConstraintsToFile(f.getAbsolutePath(), model.getConstraintNames(), knownConflicts);
//			System.out.println("Wrote problem file ..");
		}

		QXKCTools.knownConflicts = knownConflicts;
		return model;
	}
	
	/**
	 * Create a list of conflicts
	 * @param nbConflicts nb of conflicts to be created
	 * @param avgConflictSize avg size of the nodeLabel
	 * @param position position of the conflicts
	 * @return the list of conflicts
	 */
	private List<List<Constraint>> createKnownConflicts(
			List<Constraint> constraints,
			int nbConflicts,
			int avgConflictSize, ConflictPosition position) {

		// the result list
		List<List<Constraint>> generatedConflicts = new ArrayList<List<Constraint>>();

		List<Constraint> whereToPickFrom = new ArrayList<Constraint>(constraints);

		switch(position) {
		case left:
			whereToPickFrom = whereToPickFrom.subList(0, whereToPickFrom.size()/2);
			break;
		case right:
			whereToPickFrom = whereToPickFrom.subList(whereToPickFrom.size()/2, whereToPickFrom.size());
			break;
		case distributed:
			// do nothing for the moment
			break;
		case leftAndRight:
			break;
		}
		// as long as we do not have enough conflicts
		// make sure externally that enough conflicts can be found theoretically
		// if there should be conflicts from either side. pick one here and one there
		boolean lastWasLeft = false;
		while (generatedConflicts.size() < nbConflicts) {
			if (position == ConflictPosition.leftAndRight || position == ConflictPosition.leftAndRightNeighboring) {
//				System.out.println("generating two parts ..");
				// todo. do this once only
				List<Constraint> leftPart = constraints.subList(0, constraints.size()/2);
				List<Constraint> rightPart = constraints.subList(constraints.size()/2, constraints.size());
				// alternate between the two parts.
				if (lastWasLeft == true) {
					List<Constraint> newConflict;
					if (position == ConflictPosition.leftAndRight) {
						newConflict = createConflictFromList(avgConflictSize, rightPart);
					} else { // should be neighboring
						Random r = new Random();
						int num = r.nextInt(rightPart.size() - avgConflictSize);
						newConflict = rightPart.subList(num, num + avgConflictSize);
					}
					// check if the generated nodeLabel is not already there
					if (addConflictNoDups(generatedConflicts, newConflict)) {
//						System.out.println("Added right part nodeLabel");
						lastWasLeft = false;
					}
					else {
//						System.out.println("Could not add things right");
					}
				}
				else {
					List<Constraint> newConflict;
					if (position == ConflictPosition.leftAndRight) {
						newConflict = createConflictFromList(avgConflictSize, leftPart);
					} else { // should be neighboring
						Random r = new Random();
						int num = r.nextInt(leftPart.size() - avgConflictSize);
						newConflict = leftPart.subList(num, num + avgConflictSize);
					}
					// check if the generated nodeLabel is not already there
					if (addConflictNoDups(generatedConflicts, newConflict)) {
//						System.out.println("Added left part nodeLabel");
						lastWasLeft = true;
					}
					else {
//						System.out.println("Could not add things left ");
					}
				}

			}
			// find some neighboring elements
			else if (position == ConflictPosition.neighboring) {
				Random r = new Random();
				int num = r.nextInt(constraints.size()-avgConflictSize);
				List<Constraint> newConflict = constraints.subList(num, num + avgConflictSize);
				addConflictNoDups(generatedConflicts, newConflict);
			}
			else {
				// use the list we have created
				// create a new nodeLabel
				List<Constraint> newConflict = createConflictFromList(avgConflictSize, whereToPickFrom);
				// check if the generated nodeLabel is not already there
				addConflictNoDups(generatedConflicts, newConflict);
			}
		}
		return generatedConflicts;
	}
	
	/**
	 * What is the file name to use
	 * @return a file name indicating the main settings
	 */
	private String createFileName(int kbSize, int nbConflicts, int avgConflictSize, ConflictPosition conflictPosition, int run) {
		String result = this.conflictDirectory;
		result += "KBSize " + kbSize+" NBConflicts " + nbConflicts + " conflictSize " + avgConflictSize + " conflictPosition " + conflictPosition;
		if (differentModelsForRuns) {
			result += " Run " + run;
		}
		return result;
	}

}
