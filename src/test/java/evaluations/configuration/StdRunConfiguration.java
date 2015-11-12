package evaluations.configuration;


/**
 * A standard run configuration class.
 * @author Thomas
 */
public class StdRunConfiguration extends AbstractRunConfiguration {
	public static enum ExecutionMode {fullparallel, levelparallel, singlethreaded, heuristic, hybrid, minizinc, prdfs, mergexplain, fpandmxp, continuingmergexplain, continuingfpandmxp, parallelmergexplain, inversequickxplain, mxpandinvqxp, sfl};
	
	public ExecutionMode executionMode;
	
	public boolean choco3;
	
	public StdRunConfiguration(
			ExecutionMode executionMode,
			int threads,
			boolean choco3) {
		super(threads);
		this.executionMode = executionMode;
		this.choco3 = choco3;
	}
	
	@Override
	public String getName() {
		StringBuilder sb = new StringBuilder();
		switch (executionMode) {
		case fullparallel:
			sb.append("PARALLEL_FULL_");
			sb.append(threads);
			break;
		case levelparallel:
			sb.append("PARALLEL_LEVEL_");
			sb.append(threads);
			break;
		case heuristic:
			sb.append("HEURISTIC_");
			sb.append(threads);
			break;
		case hybrid:
			sb.append("HYBRID_");
			sb.append(threads);
			break;
		case minizinc:
			sb.append("MINIZINC_");
			sb.append(threads);
			break;
		case prdfs:
			sb.append("PRDFS_");
			sb.append(threads);
			break;
		case singlethreaded:
			sb.append("SINGLE_THREADED");
			break;
		case mergexplain:
			sb.append("MERGE_XPLAIN");
			break;
		case fpandmxp:
			sb.append("FP_AND_MXP_");
			sb.append(threads);
			break;
		case continuingmergexplain:
			sb.append("CONTINUING_MERGE_XPLAIN");
			break;
		case continuingfpandmxp:
			sb.append("CONTINUING_FP_AND_MXP_");
			sb.append(threads);
			break;
		case parallelmergexplain:
			sb.append("PARALLEL_MERGE_XPLAIN_");
			sb.append(threads);
			break;
		case inversequickxplain:
			sb.append("INVERSE_QUICK_XPLAIN");
			break;
		case mxpandinvqxp:
			sb.append("MXP_AND_INVQXP");
			break;
		case sfl:
			sb.append("SFL");
			break;
		}
		if (choco3) {
			sb.append("_CHOCO3");
		}
		else {
			sb.append("_CHOCO2");
		}
		return sb.toString();
	}
}
