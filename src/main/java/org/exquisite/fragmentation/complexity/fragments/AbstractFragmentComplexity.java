package org.exquisite.fragmentation.complexity.fragments;

import java.util.Dictionary;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.Fragment;
import org.exquisite.fragmentation.complexity.AbstractComplexityClass;

/**
 * Abstract class to calculate a specific complexity of a fragment.
 * @author Thomas
 *
 */
public abstract class AbstractFragmentComplexity extends AbstractComplexityClass {
	
	public AbstractFragmentComplexity(ExquisiteAppXML xml, Dictionary<String, CommonTree> formulaTrees) {
		super(xml, formulaTrees);
	}
	
	public abstract float getComplexity(Fragment fragment);
}
