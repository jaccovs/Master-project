package org.exquisite.fragmentation.complexity.formulas;

import java.util.Dictionary;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.fragmentation.complexity.AbstractComplexityClass;

/**
 * Abstract class to calculate a specific complexity of a formula.
 * @author Thomas
 *
 */
public abstract class AbstractFormulaComplexity extends AbstractComplexityClass {	
	
	public AbstractFormulaComplexity(ExquisiteAppXML xml,Dictionary<String, CommonTree> formulaTrees) {
		super(xml, formulaTrees);
	}

	protected String getFormula(String cell) {
		return xml.getFormulas().get(cell);
	}
	
	public abstract float getComplexity(String cell);
}
