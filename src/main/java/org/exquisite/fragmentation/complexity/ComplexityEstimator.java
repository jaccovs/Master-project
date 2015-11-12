package org.exquisite.fragmentation.complexity;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.Fragment;
import org.exquisite.fragmentation.complexity.formulas.AbstractFormulaComplexity;
import org.exquisite.fragmentation.complexity.formulas.ConditionalComplexity;
import org.exquisite.fragmentation.complexity.formulas.ReferenceCount;
import org.exquisite.fragmentation.complexity.fragments.AbstractFragmentComplexity;
import org.exquisite.fragmentation.complexity.fragments.FormulasComplexity;
import org.exquisite.fragmentation.complexity.fragments.InputComplexity;
import org.exquisite.fragmentation.complexity.fragments.OutputComplexity;
import org.exquisite.fragmentation.complexity.fragments.SpannedAreaComplexity;
import org.exquisite.parser.FormulaParser;

/**
 * Estimates the overall complexity of a fragment. Combines all other complexity calculations.
 * @author Thomas
 *
 */
public class ComplexityEstimator {
	Dictionary<AbstractFragmentComplexity, Float> complexityWeights = new Hashtable<AbstractFragmentComplexity, Float>();
	
	public boolean Debug = false;
	
	public ComplexityEstimator(ExquisiteAppXML xml) {
		
		// Parse all formulas to create formula trees
		Dictionary<String, CommonTree> formulaTrees = new Hashtable<String, CommonTree>(xml.getFormulas().size());
		FormulaParser parser = new FormulaParser(null);
		
		Enumeration<String> keys = xml.getFormulas().keys();
		while (keys.hasMoreElements()) {
			String cell = keys.nextElement();
			String formula = xml.getFormulas().get(cell);
			
			parser.parse(formula);
			
			formulaTrees.put(cell, parser.FormulaTree);
		}
		
		// Add the different complexity measures
		complexityWeights.put(new InputComplexity(xml, formulaTrees), 0f);
		complexityWeights.put(new OutputComplexity(xml, formulaTrees), 1f);
		complexityWeights.put(new SpannedAreaComplexity(xml, formulaTrees), 0.5f);
		
		// Formula complexities
		Dictionary<AbstractFormulaComplexity, Float> formulaComplexityWeights = new Hashtable<AbstractFormulaComplexity, Float>();
		formulaComplexityWeights.put(new ConditionalComplexity(xml, formulaTrees), 1f);
		formulaComplexityWeights.put(new ReferenceCount(xml, formulaTrees), 1f);
		complexityWeights.put(new FormulasComplexity(xml, formulaTrees, formulaComplexityWeights), 1f);
	}
	
	/**
	 * Calculates the overall complexity of a fragment
	 * @param fragment
	 * @return
	 */
	public float getComplexity(Fragment fragment) {
		if (Debug) {
			System.out.println("----------------------------------------------------");
			System.out.println("Complexity calculation for fragment " + fragment.getName());
		}
		float weightSum = 0;
		float complexitySum = 0;
		Enumeration<AbstractFragmentComplexity> fragmentComplexities = complexityWeights.keys();
		while (fragmentComplexities.hasMoreElements()) {
			AbstractFragmentComplexity comp = fragmentComplexities.nextElement();
			float weight = complexityWeights.get(comp);
			weightSum += weight;
			float complexity = comp.getComplexity(fragment);
			complexitySum += complexity * weight;

			if (Debug) {
				System.out.println(comp.getClass().getSimpleName() + " (" + weight + "): " + complexity);
			}
		}
		if (Debug) {
			System.out.println("Result: " + complexitySum + "(C) / " + weightSum + "(W) = " + (complexitySum / weightSum));
		}
		return complexitySum / weightSum;
	}
}
