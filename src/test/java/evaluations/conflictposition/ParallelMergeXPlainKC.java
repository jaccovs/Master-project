package evaluations.conflictposition;

import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.mergexplain.ParallelMergeXplain;

import choco.kernel.model.constraints.Constraint;

/**
 * A variant that uses parallelisms and relies on a set of known conflicts
 * @author dietmar
 *
 */
public class ParallelMergeXPlainKC extends ParallelMergeXplain {

	public ParallelMergeXPlainKC(ExquisiteSession sessionData,
			AbstractHSDagBuilder dagbuilder) {
		super(sessionData, dagbuilder);
	}
	
	@Override
	public boolean isConsistent(List<Constraint> constraints)
			throws DomainSizeException {
		return QXKCTools.isConsistent(constraints, this.diagnosisEngine);
	}

}
