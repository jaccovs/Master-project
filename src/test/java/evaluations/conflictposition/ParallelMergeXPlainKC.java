package evaluations.conflictposition;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.mergexplain.ParallelMergeXplain;

import java.util.List;

/**
 * A variant that uses parallelisms and relies on a set of known conflicts
 * @author dietmar
 *
 */
public class ParallelMergeXPlainKC extends ParallelMergeXplain<Constraint> {

	private int numberOfConstraints = 0;

	public ParallelMergeXPlainKC(ExcelExquisiteSession sessionData, int numberOfConstraints) {
		super(sessionData);
		this.numberOfConstraints = numberOfConstraints;
	}
	
	@Override
	public boolean isConsistent(List<Constraint> constraints)
			throws DomainSizeException {
		return QXKCTools.isConsistent(constraints, this.numberOfConstraints);
	}

}
