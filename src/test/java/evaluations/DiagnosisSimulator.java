package evaluations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder.QuickXplainType;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.tools.Utilities;

import tests.dj.qxsim.QXSim;
import tests.dj.qxsim.RandomGaussian;
import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import evaluations.configuration.AbstractRunConfiguration;
import evaluations.configuration.AbstractScenario;
import evaluations.configuration.SimulatorScenario;
import evaluations.configuration.StdRunConfiguration;
import evaluations.configuration.StdRunConfiguration.ExecutionMode;

/**
 * Simulation tests
 * For documentation of overridden methods, see AbstractEvaluation
 * @author dietmar, Thomas
 *
 */
public class DiagnosisSimulator extends AbstractEvaluation {
	
	// Possible distribution strategies.
	public enum ConflictGenerationStrategy_Size {uniform, gaussian};
	public enum ConflictGenerationStrategy_VarDistribution  {uniform, gaussian};

	@Override
	public String getEvaluationName() {
		return "DiagnosisSimulator";
	}

	@Override
	public String getResultPath() {
		return logFileDirectory;
	}
	
	@Override
	public String getConstraintOrderPath() {
		return conflictDirectory;
	}

	@Override
	protected boolean shouldShuffleConstraints() {
		return false;
	}
	
	@Override
	public boolean alwaysWriteDiagnoses() {
		return false;
	}
	
	@Override
	public boolean usePersistentConstraintOrder() {
		return false;
	}
	
	// ----------------------------------------------------
	// Directories
	static String inputFileDirectory = "experiments/QXSim/";
	static String logFileDirectory = "logs/QXSim/";
	public String conflictDirectory = "experiments/QXSim/storedconflicts";
	// ----------------------------------------------------
	
	// Number of runs
	static int nbInitRuns = 5;
	static int nbTestRuns = 100;
	
	// store to file system
	static boolean persistent = true;
	// store different files for each run to have randomization, but better comparability from sequential to parallelized versions
	static boolean differentModelsForRuns = true;
	
	// Cache the constraint names
	Map<Constraint, String> constraintNames = new HashMap<Constraint, String>();
	List<List<Constraint>> conflicts = new ArrayList<List<Constraint>>();
	
	// how to generate
	static ConflictGenerationStrategy_Size genStrategySize = ConflictGenerationStrategy_Size.gaussian;
	
	// the random gaussian
	RandomGaussian randomGaussian;
	// A copy of the constraints
	List<Constraint> shuffledConstraints = null;
	
	// Run configurations
	static StdRunConfiguration[] runConfigurations = new StdRunConfiguration[] {
		new StdRunConfiguration(ExecutionMode.singlethreaded, 1, false),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 2, false),
//		new StdRunConfiguration(ExecutionMode.levelparallel, 2, false),
		
//		new StdRunConfiguration(ExecutionMode.heuristic, 2, false),
//		new StdRunConfiguration(ExecutionMode.hybrid, 2, false),
		new StdRunConfiguration(ExecutionMode.fullparallel, 4, false),
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
	
	static SimulatorScenario[] scenarios = new SimulatorScenario[] {
		// Settings for finding 1 diagnosis (All engines except levelparallel)
//		new SimulatorScenario(100, 12, 9, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 10), // Settings, where Hybrid is faster than FP and heuristic
//		
//		new SimulatorScenario(50, 5, 4, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 0),
//		new SimulatorScenario(50, 5, 4, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 1),
//		new SimulatorScenario(50, 5, 4, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 10),
//		new SimulatorScenario(50, 5, 4, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 100),
//		
//		new SimulatorScenario(50, 5, 6, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 10),
//		new SimulatorScenario(50, 5, 9, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 10),
//		new SimulatorScenario(50, 5, 12, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 10),
//		
//		new SimulatorScenario(50, 10, 9, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 10),
//		new SimulatorScenario(75, 10, 9, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 10),
//		new SimulatorScenario(100, 10, 9, ConflictGenerationStrategy_VarDistribution.uniform, -1, 1, 10),
		
		// Settings for finding all diagnoses (No Heuristic / Hybrid engines!)
		new SimulatorScenario(50, 5, 4, ConflictGenerationStrategy_VarDistribution.gaussian, 5, -1, 0),
		new SimulatorScenario(50, 5, 4, ConflictGenerationStrategy_VarDistribution.gaussian, 5, -1, 1),
		new SimulatorScenario(50, 5, 4, ConflictGenerationStrategy_VarDistribution.gaussian, 5, -1, 10),
		new SimulatorScenario(50, 5, 4, ConflictGenerationStrategy_VarDistribution.gaussian, 5, -1, 100),
		
		new SimulatorScenario(50, 5, 6, ConflictGenerationStrategy_VarDistribution.gaussian, 5, -1, 10),
		new SimulatorScenario(50, 5, 9, ConflictGenerationStrategy_VarDistribution.gaussian, 5, -1, 10),
		new SimulatorScenario(50, 5, 12, ConflictGenerationStrategy_VarDistribution.gaussian, 5, -1, 10),
		
		new SimulatorScenario(50, 10, 9, ConflictGenerationStrategy_VarDistribution.gaussian, 5, -1, 10),
		new SimulatorScenario(75, 10, 9, ConflictGenerationStrategy_VarDistribution.gaussian, 5, -1, 10),
		new SimulatorScenario(100, 10, 9, ConflictGenerationStrategy_VarDistribution.gaussian, 5, -1, 10),
	};

	@Override
	public IDiagnosisEngine prepareRun(
			AbstractRunConfiguration abstractRunConfiguration,
			AbstractScenario abstractScenario, int subScenario, int iteration) {
		
		StdRunConfiguration runConfiguration = (StdRunConfiguration)abstractRunConfiguration;
		SimulatorScenario scenario = (SimulatorScenario)abstractScenario;
		
		AbstractHSDagBuilder.USE_QXTYPE = QuickXplainType.SimulatedQuickXplain;
		QXSim.ARTIFICIAL_WAIT_TIME = scenario.waitTime;

		ExquisiteSession sessionData = new ExquisiteSession();
		try {
			sessionData.diagnosisModel = defineModel(scenario.nbConstraints, scenario.nbConflicts, scenario.conflictSize, scenario.varDistribution, iteration);
		}
		catch (Exception e) {
			addError(e.getMessage(), runConfiguration, subScenario, iteration);
			return null;
		}
//		System.out.println("Created the model with examples");
//			AbstractHSDagBuilder hsdag; 
		
		// Do not try to find a better strategy for the moment
		sessionData.config.searchStrategy = SearchStrategies.Default;

		sessionData.config.searchDepth = scenario.searchDepth;
		sessionData.config.maxDiagnoses = scenario.maxDiags;
		
		EngineType engineType = null;
		switch (runConfiguration.executionMode) {
		case fullparallel:
			engineType = EngineType.FullParaHSDagStandardQX;
			break;
		case levelparallel:
			engineType = EngineType.ParaHSDagStandardQX;
			break;
		case heuristic:
			engineType = EngineType.HeuristicSearch;
			break;
		case hybrid:
			engineType = EngineType.Hybrid;
			break;
		case singlethreaded:
			engineType = EngineType.HSDagStandardQX;
			break;
		case minizinc:
			engineType = EngineType.MiniZinc;
			break;
		case prdfs:
			engineType = EngineType.PRDFS;
			break;
		}
		
		IDiagnosisEngine engine = EngineFactory.makeEngine(engineType, sessionData, runConfiguration.threads);
		

		((AbstractHSDagBuilder)engine).setSearchDepth(scenario.searchDepth);
		
		
		return engine;
	}
	
	/**
	 * Creates a new diagnosis model
	 * @param nbConstraints   number of constraints to create
	 * @param nbConflicts   number of conflicts to create
	 * @param conflictSize   average size of conflicts
	 * @param run   the number of the run (for storage)
	 * @return   the defined model
	 * @throws Exception
	 */
	DiagnosisModel defineModel(int nbConstraints, int nbConflicts, int conflictSize, ConflictGenerationStrategy_VarDistribution varDistribution, int run) throws Exception {
//		System.out.println("" + run);
		DiagnosisModel model = new DiagnosisModel();
		conflicts = new ArrayList<List<Constraint>>();
		constraintNames = new HashMap<Constraint, String>();
		shuffledConstraints = null;
		
		// Create a set of conflicts
		createConflicts(nbConstraints, nbConflicts, conflictSize, varDistribution, run);
		// Remember them globally
		QXSim.conflicts = this.conflicts;
		QXSim.constraintNames = this.constraintNames;

		// feed the diagnosis model
		for (Constraint c : this.constraintNames.keySet()) {
			model.addPossiblyFaultyConstraint(c, this.constraintNames.get(c));
		}

		// Add a dummy example
		Example exTestExample = new Example();
		exTestExample.addConstraint(createDummyConstraint(), "dummyexample");
		model.getPositiveExamples().add(exTestExample);

//		System.out.println("Known conflicts");
//		for (List<Constraint> conflict : conflicts) {
//			System.out.println(conflictToString(this.constraintNames, conflict));
//		}
		return model;
	}
	
	/**
	 * Creates and returns a list of conflicts.
	 * @param nbConstraints   number of constraints to create
	 * @param nbConflicts   number of conflicts to create
	 * @param conflictSize   average size of conflicts
	 * @param run   the number of the run (for storage)
	 * @throws Exception
	 */
	void createConflicts(int nbConstraints, int nbConflicts, int conflictSize, ConflictGenerationStrategy_VarDistribution varDistribution, int run) throws Exception {
		
		// Different strategies.
		// Strategy one: All constraints are of equal size
		// Strategy two: Vary the size of the conflicts -> make sure we have no subsets
		// Strategy three to 100: Vary the distribution of 
		
		// Read from file system or not
		if (persistent) {
			this.constraintNames.clear();
			this.conflicts.clear();
			File f = new File(getFullFileName(nbConstraints, nbConflicts, conflictSize, varDistribution, run));
			if (f.exists()) {
				addLog("Loading model " + getFullFileName(nbConstraints, nbConflicts, conflictSize, varDistribution, run));
				loadConstraintsFromFile(f);
				return;
			}
		}
		
		
		addLog("Creating new model " + getFullFileName(nbConstraints, nbConflicts, conflictSize, varDistribution, run));
		// Create a set of constraints (all dummy)
		createConstraints(nbConstraints);
//		System.out.println("Constraint names: " + this.constraintNames.values());
		// 		


		// Determine the size of each conflict to be generated.
		int[] conflictSizes = new int[nbConflicts]; 
		if (genStrategySize == ConflictGenerationStrategy_Size.uniform) {
			for (int i=0;i<nbConflicts;i++) {
				conflictSizes[i] = conflictSize;
			}
		}
		else {
			// Get some distribution of values
			RandomGaussian randomGaussian = new RandomGaussian(
													conflictSize, // The mean 
													(int) (conflictSize/2),  // half of the mean value
													2,  // double conflicts down there
													(conflictSize-2)+conflictSize); // upper bound at same distance than lower bound
			for (int i=0;i<nbConflicts;i++) {
				conflictSizes[i] = (int) randomGaussian.getGaussian();
			}
			
		}

		int cnt=0;
		// while not enough conflicts
		while (this.conflicts.size() < nbConflicts) {
			List<Constraint> oneConflict = new ArrayList<Constraint>();
			// Shuffle the list and pick the first n
			
			if (varDistribution == ConflictGenerationStrategy_VarDistribution.uniform) {
				oneConflict = getConflictEqualVarDistribution(conflictSizes[cnt]);
			}
			else {
				oneConflict = getConflictGaussianVarDistribution(conflictSizes[cnt]);
			}

			if (!Utilities.isSubsetOfKnownConflict(oneConflict, this.conflicts)) {
				cnt++;
				this.conflicts.add(oneConflict);
			}
			else {
//				System.out.println("dup found..");
			}
			
					}
		
		// some testing
//		Map<Constraint, Integer> constraintCount = new HashMap<Constraint, Integer>();
//		System.out.println("Known conflicts");
//		for (List<Constraint> conflict : conflicts) {
//			System.out.println(conflictToString(this.constraintNames, conflict));
//			for (Constraint c : conflict) {
//				Integer cCount = constraintCount.get(c);
//				if (cCount == null){
//					constraintCount.put(c, 1);
//				}
//				else {
//					constraintCount.put(c, cCount+1);
//					
//				}
//			}
//		}
//		System.out.println("Constraint distribution " + constraintCount);

		// Write things to a text file
		if (persistent) {
			writeConstraintsToFile(getFullFileName(nbConstraints, nbConflicts, conflictSize, varDistribution, run));
		}
//		if (true) System.exit(0);
				
	}
	
	/**
	 * Creates a dummy constraint
	 */
	Constraint createDummyConstraint() {
		IntegerVariable v = Choco.makeIntVar("dummy", 0,1);
		return (Choco.eq(v,1));
	}

	
	/**
	 * Create and return a list of constraints based on the parameters of the instance.
	 *  Store the names somwehere in the instance
	 * @return a list of dummy constraints
	 */
	void createConstraints(int nbConstraints) {
		for (int i=0;i<nbConstraints;i++) {
			Constraint c = createDummyConstraint();
			this.constraintNames.put(c,"C" + i);
		}
		
	}
	
	/**
	 * Creates a file name with some details encoded into it.
	 * @param nbConstraints   number of constraints to create
	 * @param nbConflicts   number of conflicts to create
	 * @param conflictSize   average size of conflicts
	 * @param run   the number of the run
	 * @return filename + nbcts + nbconflits + avg-conflict size + run
	 */
	String getFullFileName(int nbConstraints, int nbConflicts, int conflictSize, ConflictGenerationStrategy_VarDistribution varDistribution, int run) {
		String result = this.conflictDirectory;
		result += "-" + nbConstraints + "-" + nbConflicts + "-" + conflictSize + "-" + varDistribution.toString();
		if (differentModelsForRuns) {
			result += "_" + run;
		}
		result += ".txt";
		return result;		
	}
	
	/**
	 * Loads stuff
	 * @throws Exception
	 */
	void loadConstraintsFromFile(File f) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		try {
			String line = reader.readLine();
			String[] ctnames = line.split(" ");
			
			for (int i=0;i<ctnames.length;i++) {
				this.constraintNames.put(createDummyConstraint(), ctnames[i]);
			}
			line = reader.readLine();
	//		System.out.println("2nd line: " + line);
			String[] cfs = line.split(" ");
			for (String cf : cfs) {
	//			System.out.println("Found conflict" + cf );
				String cftrimmed = cf.substring(1,cf.length()-1);
				String[] cnames = cftrimmed.split(",");
				List<Constraint> conflict = new ArrayList<Constraint>();
				for (String cname : cnames) {
					// Get the constraint from the map
					conflict.add(getConstraintByName(cname));
				}
				this.conflicts.add(conflict);
			}
		}
		finally {
			reader.close();
		}

	}
	
	/**
	 * Writes stuff to the file system
	 * @throws Exception
	 */
	void writeConstraintsToFile(String filename) throws Exception {
		
		File f = new File(filename);
		BufferedWriter bw = new BufferedWriter (new FileWriter (f));
		try {
			StringBuffer buf = new StringBuffer();
			for (String cname : this.constraintNames.values()) {
				buf.append(cname).append(" ");
			}
			buf.append("\n");
			for (List<Constraint> ct : this.conflicts) {
				buf.append(conflictToString(this.constraintNames, ct)).append(" ");
			}
			bw.write(buf.toString());
		}
		finally {
			bw.close();
		}
	}
	
	/**
	 * Returns a list of constraints of a given size randomly and uniformly from all constraints
	 * @return
	 */
	List<Constraint> getConflictEqualVarDistribution(int size) {
		List<Constraint> constraints = new ArrayList<Constraint>(this.constraintNames.keySet());
		Collections.shuffle(constraints);
		List<Constraint> result = new ArrayList<Constraint>();
		for (int i=0;i<size;i++) {
			result.add(constraints.get(i));
		}
		return result;
	}
	/**
	 * Returns a list of constraints of a given size randomly chosen from all constraints , gaussian
	 * @return
	 */
	List<Constraint> getConflictGaussianVarDistribution(int size) {
		if (shuffledConstraints == null) {
			shuffledConstraints = new ArrayList<Constraint>(this.constraintNames.keySet());
			// do an initial shuffle
			Collections.shuffle(shuffledConstraints);
			randomGaussian = new RandomGaussian(
									this.constraintNames.keySet().size(), 
									this.constraintNames.keySet().size()/3, 
									0, 
									this.constraintNames.keySet().size()-1);
		}
		List<Constraint> result = new ArrayList<Constraint>();
		while (result.size() < size) {
			int nextVar = (int) randomGaussian.getGaussian();
			Constraint c = shuffledConstraints.get(nextVar);
			if (!result.contains(c)) {
				result.add(c);
			}
		}
	
		return result;
	}
	
	/**
	 * returns a string rep of the conflict
	 * @param constraints
	 * @return
	 */
	public static String conflictToString(Map<Constraint,String> constraintNames, List<Constraint> constraints) {
		if (constraints == null) {
			return null;
		}
		List<String> names = new ArrayList<String>();
		for (Constraint c : constraints) {
			String name = constraintNames.get(c);
			if (name == null) {
				System.out.println("Name is null for " + c.getName());
				System.out.println("Size of constraints: " + constraints.size() + ", size of constraintNames: " + constraintNames.size());
			}
			names.add(name);
		}
		Collections.sort(names);
		
		return names.toString().replace(" ","");
	}
	
	/** 
	 * Return the constraint name
	 * @param name
	 * @return
	 */
	Constraint getConstraintByName(String name) {
		for (Constraint ct : this.constraintNames.keySet()) {
			if (this.constraintNames.get(ct).equals(name)){
				return ct;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		DiagnosisSimulator diagnosisSimulator = new DiagnosisSimulator();
		diagnosisSimulator.runTests(nbInitRuns, nbTestRuns, runConfigurations, scenarios);
	}

}
