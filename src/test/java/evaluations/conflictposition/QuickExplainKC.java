package evaluations.conflictposition;
import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.AbstractHSDagBuilder;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;

import choco.kernel.model.constraints.Constraint;


/**
 * A method that overrides the isConsistent method of QX and uses a
 * set of known conflicts to determine if a set of statements is conflict
 * @author dietmar
 *
 */
public class QuickExplainKC extends QuickXPlain {

	/**
	 * Construct QX from the data
	 * @param sessionData
	 * @param dagbuilder
	 */
	public QuickExplainKC(ExquisiteSession sessionData,
			AbstractHSDagBuilder dagbuilder) {
		super(sessionData, dagbuilder);
//		System.out.println("Created QXP with known conflicts");
	}

	@Override
	// Override the method and use a list of predefined conflicts
	public boolean isConsistent(List<Constraint> constraints)
			throws DomainSizeException {
		
		return QXKCTools.isConsistent(constraints, this.diagnosisEngine);
	}
	
}
