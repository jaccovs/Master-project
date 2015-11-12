package org.exquisite.fragmentation.complexity.formulas;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.regex.Pattern;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.tools.StringUtilities;

public class ReferenceCount extends AbstractFormulaComplexity {

	public ReferenceCount(ExquisiteAppXML xml, Dictionary<String, CommonTree> formulaTrees) {
		super(xml, formulaTrees);
	}

	@Override
	public float getComplexity(String cell) {
		// Counts cell references in the formula string. Does not work with named ranges.
		// A1:A5 -> 2 references
		// A1+A1 -> 2 references
		List<String> references = StringUtilities.parse(xml.getFormulas().get(cell), Pattern.compile("([A-Z]+[0-9]+)"));
		//System.out.println("Formula " + xml.getFormulas().get(cell) + " has " + references.size() + " references.");
		return references.size();
		
		//Counts real cell references
		// A1:A5 -> 5 references
		// A1+A1 -> 1 reference
		/*Set<String> inputs = new HashSet<String>();
		FragmentExtractor.findAllReferences(formulaTrees.get(cell), inputs);
		System.out.println("Formula " + xml.getFormulas().get(cell) + " has " + inputs.size() + " references.");
		return inputs.size();*/
		
		
		// Counts number of reference statements.
		// A1:A5 -> 1 reference
		// A1+A1 -> 2 references
		/*CommonTree tree = formulaTrees.get(cell);
		int count = countReferences(tree);
		return count;*/
	}
	
	protected int countReferences(CommonTree tree) {
		int childCount = 0;
		if (tree.getChildren() != null) {
			@SuppressWarnings("unchecked")
			List<CommonTree> children = new ArrayList<CommonTree>(tree.getChildren());
			for (CommonTree child: children) {
				childCount += countReferences(child);
			}
		}
		else {
			String s = tree.toString();
			System.out.println(s + " isString: " + isString(s) + " isNumber: " + isNumber(s));
			if (!isString(s) && !isNumber(s)) {
				childCount++;
			}
		}
		return childCount;
	}
	
	protected boolean isString(String s) {
		return s.startsWith("\"") && s.endsWith("\"");
	}
	
	protected boolean isNumber(String s) {
		try
		{
			Float.parseFloat(s);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}

}
