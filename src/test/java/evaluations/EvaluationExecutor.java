package evaluations;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;

/**
 * One class to rule them all.
 * Startpoint to launch the different evaluations with console parameters.
 * @author Thomas
 *
 */
public class EvaluationExecutor {
	
	@Option(name = "-i", usage="Do N initialization runs per scenario")
	private int initRun = 20;

	@Option(name = "-r", usage="Do N evaluation runs per scenario")
	private int evaluationRuns = 100;
	
	@Option(name = "-c3", usage="Use Choco 3 instead of Choco 2")
	private boolean choco3 = false;
	
	enum EvaluationType {Sim, DXC, DXCMZ, CSPs, XLS};
	
	@Option(name = "-t", required = true, usage="The evaluation type")
//	@Argument(index=0, required = true, usage="The evaluation type", metaVar="EvaluationType")
	private EvaluationType evaluationType;
	
	enum Evaluation {AllDiags, OneDiag, PRDFSThreads};
	
	@Option(name = "-e", required = true, usage = "The evaluation")
	private Evaluation evaluation;
	
	public static void main(String[] args) {
		new EvaluationExecutor().start(args);
	}

	private void start(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		
		parser.setUsageWidth(80);
		
		try {
			parser.parseArgument(args);
		}
		catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
			System.err.println("java Evaluation <options...>");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			// print option sample. This is useful some time
			System.err.println(" Example: java Evaluation"+parser.printExample(ExampleMode.ALL));
			return;
		}
		
		AbstractEvaluation eval = null;
		
		switch (evaluationType) {
		case Sim:
			eval = new DiagnosisSimulator();
			break;
		case CSPs:
			eval = new MutatedConstraintsIndividual();
			break;
		case DXC:
			eval = new DXCSyntheticBenchmark();
			break;
		case DXCMZ:
			eval = new DXCSyntheticMZBenchmark();
			break;
		case XLS:
			eval = new SpreadsheetsIndividual();
			break;
		}
		
		eval.runTests(initRun, evaluationRuns, null, null);
		
//		DXCSyntheticBenchmark dxc = new DXCSyntheticBenchmark();
//		dxc.runTests(20, 100, DXCSyntheticBenchmark.runConfigurations, DXCSyntheticBenchmark.scenarios);
	}

}
