package tests.ts.interactivity.spreadsheets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;

import tests.ts.interactivity.DiagnosisModelExpansion;
import tests.ts.interactivity.IUserInteraction;
import tests.ts.interactivity.IUserQuery;
import choco.kernel.model.constraints.Constraint;

/**
 * User Interaction for spreadsheet formulas. The possible queries are given by all formulas that are contained in the diagnoses. The simulated user
 * interaction uses a correct version of the debugged spreadsheet (as xml) to verify the correctness of formulas.
 * 
 * @author Schmitz
 *
 */
public class SpreadsheetFormulaInteraction implements IUserInteraction {

	// private static final Logger log = Logger.getLogger(SpreadsheetFormulaInteraction.class.getSimpleName());

	ExquisiteAppXML correctXML;
	ExquisiteAppXML mutatedXML;
	DiagnosisModel diagModel;

	Set<Constraint> alreadyUsedQueries = new HashSet<Constraint>();

	public SpreadsheetFormulaInteraction(String correctXMLFilename, ExquisiteAppXML mutatedXML, DiagnosisModel diagModel) {
		this.correctXML = ExquisiteAppXML.parseToAppXML(correctXMLFilename);
		this.mutatedXML = mutatedXML;
		this.diagModel = diagModel;
	}

	/**
	 * Simulates user interaction to query the correctness of one formula.
	 * 
	 * @param formulaCell
	 * @param mutatedXML
	 * @param correctXML
	 * @return
	 */
	private boolean queryFormula(String formulaCell, ExquisiteAppXML mutatedXML, ExquisiteAppXML correctXML) {
		// userInteractionsNeeded++;
		String mutatedFormula = mutatedXML.getFormulas().get(formulaCell);
		String correctFormula = correctXML.getFormulas().get(formulaCell);
		// System.out.println("Is the formula " + mutatedFormula + " in cell " + formulaCell + " correct?");
		if (mutatedFormula.equals(correctFormula)) {
			// System.out.println("Yes!");
			return true;
		} else {
			// System.out.println("No!");
			return false;
		}
	}

	/**
	 * Simulates the user interaction by verifying the correctness of a formula with a comparison between the debugged and the correct version of a
	 * spreadsheet. Correct formulas are added as correct constraints while faulty formulas are added as certainly faulty constraints.
	 */
	@Override
	public DiagnosisModelExpansion simulateUserInteraction(IUserQuery query) {
		FormulaQuery q = (FormulaQuery) query;
		Constraint c = q.getConstraint();

		alreadyUsedQueries.add(c);

		boolean answer = queryFormula(diagModel.getConstraintName(c), mutatedXML, correctXML);

		DiagnosisModelExpansion exp = new DiagnosisModelExpansion();

		if (answer) {
			exp.getCorrectConstraintsToAdd().add(c);
		} else {
			exp.getCertainlyFaultyConstraintsToAdd().add(c);
		}
		exp.getPossiblyFaultyConstraintsToRemove().add(c);

		return exp;
	}

	/**
	 * The possible queries are given by all formulas that are contained in the diagnoses.
	 */
	@Override
	public List<IUserQuery> calculatePossibleQueries(List<Diagnosis> diagnoses) {
		// Old Code: Because of the iteration over a hashset, the order of the result is not deterministic
		// Set<Constraint> constraints = new HashSet<Constraint>();
		// for (Diagnosis diag: diagnoses) {
		// for (Constraint c: diag.getElements()) {
		// if (!alreadyUsedQueries.contains(c)) {
		// constraints.add(c);
		// }
		// }
		// }
		//
		// List<IUserQuery> queries = new ArrayList<IUserQuery>(constraints.size());
		//
		// for (Constraint c: constraints) {
		// FormulaQuery query = new FormulaQuery(c);
		//
		// queries.add(query);
		// }

		Set<Constraint> constraints = new HashSet<Constraint>();
		List<IUserQuery> queries = new ArrayList<IUserQuery>();
		for (Diagnosis diag : diagnoses) {
			for (Constraint c : diag.getElements()) {
				if (!alreadyUsedQueries.contains(c)) {
					if (constraints.add(c)) {
						FormulaQuery query = new FormulaQuery(c);

						queries.add(query);
					}
				}
			}
		}

		return queries;
	}

	@Override
	public List<Diagnosis> getSupportedDiagnoses(IUserQuery query, List<Diagnosis> diagnoses) {
		FormulaQuery q = (FormulaQuery) query;

		List<Diagnosis> supportedDiagnoses = new ArrayList<Diagnosis>();

		for (Diagnosis diag : diagnoses) {
			if (diag.getElements().contains(q.getConstraint())) {
				supportedDiagnoses.add(diag);
			}
		}

		return supportedDiagnoses;
	}
}
