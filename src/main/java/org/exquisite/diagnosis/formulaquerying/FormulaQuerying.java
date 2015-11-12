package org.exquisite.diagnosis.formulaquerying;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;

import choco.kernel.model.constraints.Constraint;

/**
 * Class for determining the formulas that should be queried for correctness.
 * @author Thomas
 */
public class FormulaQuerying {
	
	private ExquisiteSession session;
	
	public FormulaQuerying(ExquisiteSession session) {
		this.session = session;
	}

	/**
	 * Determines the formulas that should be queried for correctness next. The first element 
	 * represents the cell to directly ask the user. The other elements are copy equivalent cells.
	 * @param diagnoses
	 * @return
	 */
	public List<String> determineFormulasToQuery(List<Diagnosis> diagnoses) {
		// TODO: Determine ranking of formulas and sort them wrt. the ranking
		
		String queryCell = findFormulaToQuery(diagnoses);
		if (!queryCell.isEmpty())
		{
			List<String> list = findEquivalentFormulas(queryCell);
			list.add(queryCell);
			removeCorrectFormulas(list);
			Collections.sort(list, new CellReferenceComparator());
			return list;
		} else {
			return new ArrayList<String>();
		}
	}
	
	/**
	 * Removes the correct formulas from the given list.
	 * @param list
	 */
	private void removeCorrectFormulas(List<String> list) {
		Enumeration<String> cells = session.appXML.getCorrectFormulas().keys();
		while (cells.hasMoreElements()) {
			String cell = (String) cells.nextElement();
			list.remove(cell);
		}
	}

	/**
	 * Chooses the formula that should be queried next.
	 * @param diagnoses
	 * @return
	 */
	private String findFormulaToQuery(List<Diagnosis> diagnoses) {
		if (diagnoses == null)  {
			System.out.println("No tests.diagnosis in formula (null) to query");
			return "";
		}
		if (diagnoses.size() == 0) {
			System.out.println("No tests.diagnosis in formula (zero) to query");
			return "";
		}
		System.out.println("Looking at diagnoses: " + diagnoses.size());
		Diagnosis d = diagnoses.get(0);
		System.out.println("Got a tests.diagnosis " + d);
		List<Constraint> constraints = d.getElements();
		System.out.println("Got the constraints: " + constraints);
		Constraint c = constraints.get(0);
		System.out.println("Here is the first: " + c);
		System.out.println("Got a session: " + session);
		System.out.println("Got a diagmodel: " + session.diagnosisModel);
		DiagnosisModel dm = session.diagnosisModel;
		return dm.getConstraintName(c);
		// return session.diagnosisModel.getConstraintName(diagnoses.get(0).getElements().get(0));
	}
	
	/**
	 * Searches for equivalent formulas.
	 * @param formulaCell
	 * @param mutatedXML
	 * @return
	 */
	private List<String> findEquivalentFormulas(String formulaCell) {
		List<String> r = new ArrayList<String>();
		
		Dictionary<String, String> r1c1 = session.appXML.getFormulasR1C1(); 
		String formula = r1c1.get(formulaCell);
		Enumeration<String> cells = r1c1.keys();
		while (cells.hasMoreElements()) {
			String cell = (String) cells.nextElement();
			if (formula.equals(r1c1.get(cell)) && !formulaCell.equals(cell)) {
				r.add(cell);
			}
		}
		return r;
	}
}
