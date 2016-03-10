package evaluations.conflictposition;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.mergexplain.MergeXplain;

import java.util.List;

/**
 * A variant of MXP that uses a set of predefined conflicts
 * @author dietmar
 *
 */
public class MergeXPlainKC extends MergeXplain<Constraint> {

	private int numberOfConstraints = 0;
	/**
	 * Create this guy
	 * @param sessionData
	 * @param dagbuilder
	 */
	public MergeXPlainKC(ExcelExquisiteSession sessionData,
						 int numberOfConstraints) {
		super(sessionData);
		this.numberOfConstraints = numberOfConstraints;
//		System.out.println("Created MXP with known conflicts");
	}
	
	/**
	 * Use the set of known conflicts. A set of constraints is consistent if it
	 * is not a superset of a known nodeLabel
	 */
	@Override
	public boolean isConsistent(List<Constraint> constraints)
			throws DomainSizeException {
		return QXKCTools.isConsistent(constraints, this.numberOfConstraints);
	}


}
