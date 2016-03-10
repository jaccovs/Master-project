package org.exquisite.fragmentation.complexity;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.datamodel.ExquisiteAppXML;

import java.util.Dictionary;

/**
 * Abstract class to calculate a specific complexity of a formula or a fragment.
 *
 * @author Thomas
 */
public abstract class AbstractComplexityClass {
    protected ExquisiteAppXML xml;
    protected Dictionary<String, CommonTree> formulaTrees;

    public AbstractComplexityClass(ExquisiteAppXML xml, Dictionary<String, CommonTree> formulaTrees) {
        this.xml = xml;
        this.formulaTrees = formulaTrees;
    }
}
