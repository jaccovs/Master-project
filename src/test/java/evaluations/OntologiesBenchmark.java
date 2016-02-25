package evaluations;

import evaluations.configuration.AbstractRunConfiguration;
import evaluations.configuration.AbstractScenario;
import evaluations.configuration.OntologyScenario;
import evaluations.configuration.StdRunConfiguration;
import evaluations.configuration.StdRunConfiguration.ExecutionMode;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.datamodel.ExquisiteEnums.EngineType;
import org.exquisite.diagnosis.EngineFactory;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain.SolverType;
import org.exquisite.diagnosis.quickxplain.ontologies.OntologyTools;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;

/**
 * Benchmark for Ontologies. Uses OWL API + HermiT instead of Choco for solving.
 * @author Schmitz
 *
 */
public class OntologiesBenchmark extends AbstractEvaluation<OWLLogicalAxiom> {

	// ----------------------------------------------------
	// Directories
	static String inputFileDirectory = "experiments/Ontologies/";
	static String logFileDirectory = "logs/Ontologies/";
	// Number of runs
//	static int nbInitRuns = 500;
//	static int nbTestRuns = 400;
	static int nbInitRuns = 20;
	static int nbTestRuns = 100;
	// Standard scenario settings
//		static int searchDepth = -1;
	static int maxDiags = 1;
	static StdRunConfiguration[] runConfigurations = new StdRunConfiguration[]{
			new StdRunConfiguration(ExecutionMode.singlethreaded, 1, false),
//		new StdRunConfiguration(ExecutionMode.mergexplain, 1, false),
//		new StdRunConfiguration(ExecutionMode.levelparallel, 4, false),
			new StdRunConfiguration(ExecutionMode.fullparallel, 4, false),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 8, false),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 10, false),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 12, false),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 16, false),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 20, false),
//		new StdRunConfiguration(ExecutionMode.levelparallel, 8, true),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 8, true),
//		new StdRunConfiguration(ExecutionMode.levelparallel, 10, true),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 10, true),
//		new StdRunConfiguration(ExecutionMode.levelparallel, 12, true),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 12, true),
//		new StdRunConfiguration(ExecutionMode.continuingmergexplain, 1, false),
//		new StdRunConfiguration(ExecutionMode.parallelmergexplain, 4, false),
//		new StdRunConfiguration(ExecutionMode.inversequickxplain, 1, false),
//		new StdRunConfiguration(ExecutionMode.mxpandinvqxp, 1, false),
//		new StdRunConfiguration(ExecutionMode.fpandmxp, 4, false),
//		new StdRunConfiguration(ExecutionMode.continuingfpandmxp, 4, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 2, false),
//		new StdRunConfiguration(ExecutionMode.hybrid, 2, false),

//		new StdRunConfiguration(ExecutionMode.levelparallel, 4, false),
//		new StdRunConfiguration(ExecutionMode.fullparallel, 4, false),
			new StdRunConfiguration(ExecutionMode.heuristic, 1, false),
			new StdRunConfiguration(ExecutionMode.heuristic, 4, false),
			new StdRunConfiguration(ExecutionMode.hybrid, 4, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 2, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 3, false),
//		new StdRunConfiguration(ExecutionMode.heuristic, 4, false),
//		new StdRunConfiguration(ExecutionMode.prdfs, 1, false),
//		new StdRunConfiguration(ExecutionMode.prdfs, 2, false),
//		new StdRunConfiguration(ExecutionMode.prdfs, 3, false),
//		new StdRunConfiguration(ExecutionMode.prdfs, 4, false),
	};
	// Scenarios
	static OntologyScenario[] scenarios = new OntologyScenario[]{
//		new OntologyScenario("tambis.owl", -1, -1), // Exception!
//		new OntologyScenario("Terrorism.owl", -1, -1), // Exception!
//		new OntologyScenario("dualISWC2012.owl", -1, -1),
//		new OntologyScenario("dualpaper.owl", -1, -1),
//		new OntologyScenario("ecai.1.owl", -1, -1),
//		new OntologyScenario("ecai.2.owl", -1, -1),
//		new OntologyScenario("ecai.3.owl", -1, -1),
//		new OntologyScenario("ecai.owl", -1, -1),
//		new OntologyScenario("ecai.simple.owl", -1, -1, false, false),
//		new OntologyScenario("ecai2010.owl", -1, -1),
//		new OntologyScenario("example1.owl", -1, -1),
//		new OntologyScenario("example1302.owl", -1, -1),
//		new OntologyScenario("giz_3.owl", -1, -1),
//		new OntologyScenario("iswc2005.owl", -1, -1),
//		new OntologyScenario("Jws-example2.owl", -1, -1),
//		new OntologyScenario("negativeAboxT.owl", -1, -1),
//		new OntologyScenario("onediag.owl", -1, -1),
//		new OntologyScenario("query.owl", -1, -1),
//		new OntologyScenario("test.owl", -1, -1),
//		new OntologyScenario("test1.owl", -1, -1),
//		new OntologyScenario("testoptquery.owl", -1, -1),
//		new OntologyScenario("testPaperSm.owl", -1, -1),
//		new OntologyScenario("uni_3.owl", -1, -1),


//		new OntologyScenario("CHEM-A.owl", -1, -1, true, true, true),
//		new OntologyScenario("koala.owl", -1, -1, true, true, true),
//		new OntologyScenario("koala2.owl", -1, -1, true, true, true),
//		new OntologyScenario("koala4.owl", -1, -1, true, true, true),
//		new OntologyScenario("buggy-sweet-jpl.owl", -1, -1, true, true, true), // Pellet only
//		new OntologyScenario("miniTambis.owl", -1, -1, true, true, true),
//		new OntologyScenario("University.owl", -1, -1, true, true, true),
//		new OntologyScenario("Economy-SDA.owl", -1, -1, true, true, true),
//		new OntologyScenario("Transportation-SDA.owl", -1, -1, true, true, true),
//		new OntologyScenario("big/ctonmod.owl", -1, -1, true, true, true),
//		new OntologyScenario("big/opengalen-no-propchainsmod.owl", -1, -1, true, true, true),


			new OntologyScenario("CHEM-A.owl", -1, maxDiags, true, true, true),
			new OntologyScenario("koala.owl", -1, maxDiags, true, true, true),
//		new OntologyScenario("koala2.owl", -1, maxDiags, true, true, true),
//		new OntologyScenario("koala4.owl", -1, maxDiags, true, true, true),
			new OntologyScenario("buggy-sweet-jpl.owl", -1, maxDiags, true, true, true), // Exception!
			new OntologyScenario("miniTambis.owl", -1, maxDiags, true, true, true),
			new OntologyScenario("University.owl", -1, maxDiags, true, true, true),
			new OntologyScenario("Economy-SDA.owl", -1, maxDiags, true, true, true),
			new OntologyScenario("Transportation-SDA.owl", -1, maxDiags, true, true, true),
			new OntologyScenario("big/ctonmod.owl", -1, maxDiags, true, true, true),
//		new OntologyScenario("big/opengalen-no-propchainsmod.owl", -1, maxDiags, true, true, true),
	};

	public static void main(String[] args) {
		OntologiesBenchmark ontologiesBenchmark = new OntologiesBenchmark();
		ontologiesBenchmark.runTests(nbInitRuns, nbTestRuns, runConfigurations, scenarios);
	}

	@Override
	public String getEvaluationName() {
		return "Ontologies";
	}
	// ----------------------------------------------------

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

	@Override
	public boolean alwaysWriteDiagnoses() {
		return false;
	}

	@Override
	public boolean alwaysWriteConflicts() {
		return false;
	}

	@Override
	public boolean usePersistentConstraintOrder() {
		return true;
	}
	
	@Override
	public IDiagnosisEngine<OWLLogicalAxiom> prepareRun(
			AbstractRunConfiguration abstractRunConfiguration,
			AbstractScenario abstractScenario, int subScenario, int iteration) {


		try {

		StdRunConfiguration runConfiguration = (StdRunConfiguration)abstractRunConfiguration;
		OntologyScenario scenario = (OntologyScenario)abstractScenario;

		EngineType engineType = chooseEngineType(scenario, runConfiguration);


			// Load ontology
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		// map the ontology IRI to a physical IRI (files for example)
			File input = new File(inputFileDirectory + scenario.inputFileName);
		OWLOntology o = m.loadOntologyFromOntologyDocument(input);

		if (scenario.isModuleExtraction()) {
			o = OntologyTools.getIncoherentPartAsOntology(o);
		}

			DiagnosisModel<OWLLogicalAxiom> diagModel = OntologyTools
					.createDiagnosisModel(o, scenario.isAssertionsCorrect());

		if (scenario.isReduceToUnsatisfiability()) {
			OntologyTools.reduceToUnsatisfiability(diagModel);
		}


			// Create the engine
			ExcelExquisiteSession<OWLLogicalAxiom> sessionData = new ExcelExquisiteSession<>(null,
					null, new DiagnosisModel<>(diagModel));
		// Do not try to find a better strategy for the moment
		sessionData.getConfiguration().searchStrategy = SearchStrategies.Default;
		sessionData.getConfiguration().maxDiagnoses = scenario.maxDiags;


			// With a value of -2 the maxDiagSize is set to the size of the actual error of the scenario
//		if (scenario.searchDepth == -2) {
//			sessionData.getConfiguration().searchDepth = scn.getFaultyComponents().size();
//		}
//		else {
			sessionData.getConfiguration().searchDepth = scenario.searchDepth;
//		}

			IDiagnosisEngine<OWLLogicalAxiom> engine = EngineFactory
					.makeEngine(engineType, sessionData, runConfiguration.threads);

		ConstraintsQuickXPlain.ARTIFICIAL_WAIT_TIME = scenario.waitTime;
		ConstraintsQuickXPlain.SOLVERTYPE = SolverType.OWLAPI;

//		System.out.println(diagModel.getCorrectStatements().size() + "," + diagModel.getPossiblyFaultyStatements().size() + "; ");

			return engine;

		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
