package org.exquisite.diagnosis.quickxplain.choco3;

import org.antlr.runtime.tree.CommonTree;

/**
 * Holds information about the formula for a constraint
 *
 * @author dietmar
 */
public class FormulaInfo {
    public CommonTree tree;
    public String formula;
    public String cellName;
    public String operator;

    /**
     * Create a new object
     *
     * @param tree
     * @param formula
     * @param cellName
     */
    public FormulaInfo(CommonTree tree, String cellName, String operator, String formula) {
        super();
        this.tree = tree;
        this.formula = formula;
        this.cellName = cellName;
        this.operator = operator;
    }


}
