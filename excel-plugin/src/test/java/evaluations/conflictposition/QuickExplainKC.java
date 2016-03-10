package evaluations.conflictposition;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;

import java.util.List;


/**
 * A method that overrides the isConsistent method of QX and uses a
 * set of known conflicts to determine if a set of statements is nodeLabel
 * @author dietmar
 *
 */
public class QuickExplainKC extends ConstraintsQuickXPlain<Constraint> {

	private int numberOfConstraints = 0;
	/**
	 * Construct QX from the data
	 * @param sessionData
	 * @param dagbuilder
	 */
	public QuickExplainKC(ExcelExquisiteSession sessionData,
			int numberOfConstraints) {
		super(sessionData);
//		System.out.println("Created QXP with known conflicts");
	}

	@Override
	// Override the method and use a list of predefined conflicts
	public boolean isConsistent(List<Constraint> constraints)
			throws DomainSizeException {
		
		return QXKCTools.isConsistent(constraints, this.numberOfConstraints);
	}
	
}
