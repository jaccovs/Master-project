package org.exquisite.fragmentation.complexity.formulas;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.datamodel.ExquisiteAppXML;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class ConditionalComplexity extends AbstractFormulaComplexity {

    public ConditionalComplexity(ExquisiteAppXML xml, Dictionary<String, CommonTree> formulaTrees) {
        super(xml, formulaTrees);
    }

    @Override
    public float getComplexity(String cell) {
        CommonTree tree = formulaTrees.get(cell);
        int count = countRecursive(tree, "IF");
        return count;
    }

    protected int countRecursive(CommonTree tree, String s) {
        int childCount = 0;
        if (tree.getChildren() != null) {
            @SuppressWarnings("unchecked")
            List<CommonTree> children = new ArrayList<CommonTree>(tree.getChildren());
            for (CommonTree child : children) {
                childCount += countRecursive(child, s);
            }
        }
        if (tree.toString().equals(s)) {
            childCount++;
        }
        return childCount;
    }

}
