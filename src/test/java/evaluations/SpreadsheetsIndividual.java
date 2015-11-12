package evaluations;

import javax.swing.JTable.PrintMode;

import org.exquisite.data.ConstraintsFactory;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.diagnosis.quickxplain.QuickXPlain.SolverType;

import evaluations.configuration.AbstractRunConfiguration;
import evaluations.configuration.AbstractScenario;
import evaluations.configuration.SIRunConfiguration;
import evaluations.configuration.SIRunConfiguration.PruningMode;
import evaluations.configuration.StdRunConfiguration.ExecutionMode;
import evaluations.configuration.StdScenario;

/**
 * Evaluation for the individual spreadsheet files.
 * For documentation of overridden methods, see AbstractEvaluation
 * @author Thomas
 */
public class SpreadsheetsIndividual extends AbstractEvaluation {


    @Override
    public String getEvaluationName() {
        return "SpreadsheetsIndividual";
    }

    @Override
    public String getResultPath() {
        return logFileDirectory;
    }
    
    @Override
    public String getConstraintOrderPath() {
    	return inputFileDirectory;
    }

    @Override
    protected boolean shouldShuffleConstraints() {
        return true;
    }

    // ----------------------------------------------------
    // Directories
    static String inputFileDirectory = "experiments/spreadsheetsindividual/";
    static String logFileDirectory = "logs/spreadsheetsindividual/";
    // ----------------------------------------------------

    // Number of runs
    static int nbInitRuns = 1;
    static int nbTestRuns = 2;

    // Standard scenario settings
    static int searchDepth = -1;
    static int maxDiags = -1;

    // Run configurations
    static SIRunConfiguration[] runConfigurations = new SIRunConfiguration[]{
//            new SIRunConfiguration(ExecutionMode.minizinc, 2, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.singlethreaded, 1, PruningMode.on, true),
    		new SIRunConfiguration(ExecutionMode.sfl, 1, PruningMode.off, false),
//            new SIRunConfiguration(ExecutionMode.fullparallel, 2, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.heuristic, 2, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.hybrid, 2, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.levelparallel, 4, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.fullparallel, 4, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.fullparallel, 8, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.fullparallel, 10, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.fullparallel, 12, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.fullparallel, 16, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.fullparallel, 20, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.hybrid, 4, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.heuristic, 1, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.heuristic, 2, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.heuristic, 3, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.heuristic, 4, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.prdfs, 1, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.prdfs, 2, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.prdfs, 3, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.prdfs, 4, PruningMode.on, true),
//
//            new SIRunConfiguration(ExecutionMode.singlethreaded, 1, PruningMode.on, false),
////            new SIRunConfiguration(ExecutionMode.mergexplain, 1, PruningMode.on, true),
////            new SIRunConfiguration(ExecutionMode.continuingmergexplain, 1, PruningMode.on, false),
////            new SIRunConfiguration(ExecutionMode.parallelmergexplain, 4, PruningMode.on, false),
//////            new SIRunConfiguration(ExecutionMode.fullparallel, 2, PruningMode.on, false),
//////            new SIRunConfiguration(ExecutionMode.heuristic, 2, PruningMode.on, false),
//////            new SIRunConfiguration(ExecutionMode.hybrid, 2, PruningMode.on, false),
////            new SIRunConfiguration(ExecutionMode.levelparallel, 4, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.fullparallel, 4, PruningMode.on, false),
////            new SIRunConfiguration(ExecutionMode.fpandmxp, 4, PruningMode.on, true),
////            new SIRunConfiguration(ExecutionMode.continuingfpandmxp, 4, PruningMode.on, true),
//            new SIRunConfiguration(ExecutionMode.heuristic, 1, PruningMode.on, false),
////            new SIRunConfiguration(ExecutionMode.heuristic, 2, PruningMode.on, false),
////            new SIRunConfiguration(ExecutionMode.heuristic, 3, PruningMode.on, false),
//            new SIRunConfiguration(ExecutionMode.heuristic, 4, PruningMode.on, false),
//            new SIRunConfiguration(ExecutionMode.prdfs, 4, PruningMode.on, false),
//            new SIRunConfiguration(ExecutionMode.hybrid, 4, PruningMode.on, false),
    };

    // Scenarios
    StdScenario[] scenarios = new StdScenario[]{
//    	new StdScenario("SemUnitEx2_SFL2Fault.xml", searchDepth, maxDiags),
//      new StdScenario("paper.xml", searchDepth, maxDiags),
        new StdScenario("SFLTCs.xml", searchDepth, maxDiags),
//		new StdScenario("new/benchmark-5faults.xml", searchDepth, maxDiags),
//		new StdScenario("new/Hospital_Payment_Calculation.xml", searchDepth, maxDiags),
//		new StdScenario("new/xxen.xml", searchDepth, maxDiags),
//		new StdScenario("SemUnitEx2_2fault.xml", searchDepth, maxDiags),
//		new StdScenario("VDEPPreserve_1fault.xml", searchDepth, maxDiags),
    		
//		new StdScenario("new/benchmark-5faults.xml", searchDepth, 1),
//		new StdScenario("new/Hospital_Payment_Calculation.xml", searchDepth, 1),
//		new StdScenario("new/xxen.xml", searchDepth, 1),
//		new StdScenario("SemUnitEx2_2fault.xml", searchDepth, 1),
//		new StdScenario("VDEPPreserve_1fault.xml", searchDepth, 1),
		
//		new StdScenario("new/benchmark-5faults.xml", searchDepth, 5),
//		new StdScenario("new/Hospital_Payment_Calculation.xml", searchDepth, 5),
//		new StdScenario("new/xxen.xml", searchDepth, 5),
//		new StdScenario("SemUnitEx2_2fault.xml", searchDepth, 5),
//		new StdScenario("VDEPPreserve_1fault.xml", searchDepth, 5),
    		
    		
//		new StdScenario("new/benchmark-5faults.xml", searchDepth, maxDiags),
//		new StdScenario("new/Hospital_Payment_Calculation.xml", searchDepth, maxDiags),
////		new StdScenario("new/salesforecast_TC2.xml", searchDepth, maxDiags),
//		new StdScenario("new/xxen.xml", searchDepth, maxDiags),
////
////
//		new StdScenario("salesforecast_TC_IBB.xml", searchDepth, maxDiags),
//		new StdScenario("salesforecast_TC_2Faults.xml", searchDepth, maxDiags),
////				new StdScenario("salesforecast_TC_2FaultsHeavy.xml", searchDepth, maxDiags),
//		new StdScenario("SemUnitEx1_DJ.xml", searchDepth, maxDiags),
//		new StdScenario("SemUnitEx2_1fault.xml", searchDepth, maxDiags),
//		new StdScenario("SemUnitEx2_2fault.xml", searchDepth, maxDiags),
//		new StdScenario("VDEPPreserve_1fault.xml", searchDepth, maxDiags),
//		new StdScenario("VDEPPreserve_2fault.xml", searchDepth, maxDiags),
////				new StdScenario("VDEPPreserve_3fault.xml", searchDepth, maxDiags),
//		new StdScenario("AZA4.xml", searchDepth, maxDiags),
//		new StdScenario("Consultant_form.xml", searchDepth, maxDiags),
//		new StdScenario("Hospital_Payment_Calculation.xml", searchDepth, maxDiags),
//		new StdScenario("Hospital_Payment_Calculation_v2.xml", searchDepth, maxDiags),
//		new StdScenario("Hospital_Payment_Calculation_C3.xml", searchDepth, maxDiags),

//		new StdScenario("11_or_12_diagnoses.xml", searchDepth, maxDiags),
//		new StdScenario("choco_loop.xml", searchDepth, maxDiags),
//		new StdScenario("Paper2.xml", searchDepth, maxDiags),
//		new StdScenario("Test_If.xml", searchDepth, maxDiags),
//		new StdScenario("Test_If2.xml", searchDepth, maxDiags),
    };

    /**
     * @param args
     */
    public static void main(String[] args) {
//    	for (int i=5; i <= 10; i++) {
//    		maxDiags = 1;
	        SpreadsheetsIndividual spreadsheetsIndividual = new SpreadsheetsIndividual();
	        spreadsheetsIndividual.runTests(nbInitRuns, nbTestRuns, runConfigurations, spreadsheetsIndividual.scenarios);
	        
//	        maxDiags = 5;
//	        spreadsheetsIndividual = new SpreadsheetsIndividual();
//	        spreadsheetsIndividual.runTests(nbInitRuns, nbTestRuns, runConfigurations, spreadsheetsIndividual.scenarios);
//    	}
    }

    @Override
    public IDiagnosisEngine prepareRun(
            AbstractRunConfiguration abstractRunConfiguration,
            AbstractScenario abstractScenario, int subScenario, int iteration) {

        SIRunConfiguration runConfiguration = (SIRunConfiguration) abstractRunConfiguration;
        StdScenario scenario = (StdScenario) abstractScenario;


        // Set the correct engine type
        EngineType engineType = chooseEngineType(scenario, runConfiguration);

        // Set the pruning mode
        ConstraintsFactory.PRUNE_IRRELEVANT_CELLS = true;
        if (runConfiguration.pruningMode == PruningMode.off) {
            ConstraintsFactory.PRUNE_IRRELEVANT_CELLS = false;
        }

        // Create the engine
        String fullInputFilename = inputFileDirectory + scenario.inputFileName;

        IDiagnosisEngine iDiagnosisEngine = EngineFactory.makeEngineFromXMLFile(
                engineType,
                fullInputFilename,
                runConfiguration.threads);

        if (iDiagnosisEngine instanceof AbstractHSDagBuilder) {
            AbstractHSDagBuilder diagnosisEngine = (AbstractHSDagBuilder)
                    iDiagnosisEngine;

            // Set the search depth
            diagnosisEngine.setSearchDepth(scenario.searchDepth);
            diagnosisEngine.getSessionData().config.searchDepth = scenario.searchDepth;
            diagnosisEngine.getSessionData().config.maxDiagnoses = scenario.maxDiags;
            
            // TS: TEST!!!!!!!!
    		
//            try {
//	            for (Constraint c: diagnosisEngine.model.getPossiblyFaultyStatements()) {
//	            	System.out.println(Utilities.printConstraint(c));
//	            }
//            } 
//            catch (Exception e) {
//            	e.printStackTrace();
//            }

            if (runConfiguration.choco3) {
    			QuickXPlain.SOLVERTYPE = SolverType.Choco3;
    		} else {
    			QuickXPlain.SOLVERTYPE = SolverType.Choco2;
    		}
            QuickXPlain.ARTIFICIAL_WAIT_TIME = scenario.waitTime;
            return diagnosisEngine;
        }

        return iDiagnosisEngine;
    }

}
