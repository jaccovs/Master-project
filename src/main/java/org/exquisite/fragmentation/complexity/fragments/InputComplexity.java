package org.exquisite.fragmentation.complexity.fragments;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.Fragment;

import java.util.Dictionary;

public class InputComplexity extends AbstractFragmentComplexity {

    public InputComplexity(ExquisiteAppXML xml, Dictionary<String, CommonTree> formulaTrees) {
        super(xml, formulaTrees);
    }

    @Override
    public float getComplexity(Fragment fragment) {
        return fragment.getInputs().size();
    }

}
