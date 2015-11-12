package org.exquisite.fragmentation.complexity.fragments;

import java.util.Dictionary;
import java.util.Enumeration;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.Fragment;
import org.exquisite.fragmentation.complexity.formulas.AbstractFormulaComplexity;

public class FormulasComplexity extends AbstractFragmentComplexity {
	
	Dictionary<AbstractFormulaComplexity, Float> complexityWeights;

	public FormulasComplexity(ExquisiteAppXML xml, Dictionary<String, CommonTree> formulaTrees, Dictionary<AbstractFormulaComplexity, Float> complexityWeights) {
		super(xml, formulaTrees);
		this.complexityWeights = complexityWeights;
	}

	@Override
	public float getComplexity(Fragment fragment) {
		float complexitySum = 0;
		for (String cell: fragment.getInterims()) {
			complexitySum += getFormulaComplexity(cell);
		}
		for (String cell: fragment.getOutputs()) {
			complexitySum += getFormulaComplexity(cell);
		}
		return complexitySum;
	}
	
	public float getFormulaComplexity(String cell) {
		Enumeration<AbstractFormulaComplexity> complexities = complexityWeights.keys();
		float weightSum = 0;
		float complexity = 0;
		while (complexities.hasMoreElements()) {
			AbstractFormulaComplexity comp = complexities.nextElement();
			weightSum += complexityWeights.get(comp);
			complexity += comp.getComplexity(cell) * complexityWeights.get(comp);
		}
		return complexity / weightSum;
	}

}
