package tests.ts.interactivity.spreadsheets;

import tests.ts.interactivity.IUserQuery;
import choco.kernel.model.constraints.Constraint;

/**
 * A user query asking about the correctness of a spreadsheet formula (constraint).
 * 
 * @author Schmitz
 *
 */
public class FormulaQuery implements IUserQuery {
	private Constraint constraint;

	public FormulaQuery(Constraint constraint) {
		this.setConstraint(constraint);
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
}
