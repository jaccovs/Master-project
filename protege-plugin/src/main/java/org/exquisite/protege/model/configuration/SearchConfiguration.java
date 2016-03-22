package org.exquisite.protege.model.configuration;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 21.05.12
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class SearchConfiguration {

    public static enum SearchType {
        UNIFORM_COST,
        BREATHFIRST;

        @Override
        public String toString() {
            switch (this) {
                case UNIFORM_COST:
                    return "Uniform Cost Search";
                case  BREATHFIRST:
                    return "Breadth First Search";
                default:
                    return this.toString();
            }
        }

    }

    public static enum TreeType {
        REITER,
        DUAL;

        @Override
        public String toString() {
            switch (this) {
                case REITER:
                    return "HS-Tree";
                case  DUAL:
                    return "Inv-HS-Tree";
                default:
                    return this.toString();
            }
        }

    }

    public static enum QSS {
        MINSCORE,
        SPLIT,
        DYNAMIC;

        @Override
        public String toString() {
            switch (this) {
                case MINSCORE:
                    return "Entropy";
                case  SPLIT:
                    return "Split in Half";
                case  DYNAMIC:
                    return "Dynamic Risk";
                default:
                    return this.toString();
            }
        }

    }


    public Boolean aBoxInBG = true;
    public Boolean tBoxInBG = false;
    public SearchType searchType = SearchType.UNIFORM_COST;
    public TreeType treeType = TreeType.REITER;
    public Integer numOfLeadingDiags = 9;
    public QSS qss = QSS.MINSCORE;
    public Boolean reduceIncoherency = false;
    public Boolean minimizeQuery = true;
    public Boolean calcAllDiags = false;

    public Boolean inclEntSubClass = true;
    public Boolean incEntClassAssert = true;
    public Boolean incEntEquivClass = false;
    public Boolean incEntDisjClasses = false;
    public Boolean incEntPropAssert = false;
    public Boolean incOntolAxioms = true;
    public Boolean incAxiomsRefThing = false;

    public Double entailmentCalThres = 0.01;

    public String toString() {
        return "SearchType: " +  searchType + ", " +
                "TreeType: " +  treeType + ", " +
                "QSS: " +  qss + ", " +
                "aboxInBg: " + aBoxInBG + ", " +
                "tboxInBg: " + tBoxInBG + ", " +
                "numOfLeadingDiags: " + numOfLeadingDiags + ", " +
                "reduceIncoherency: " + reduceIncoherency + ", " +
                "minimizeQuery: " + minimizeQuery + ", " +
                "calcAllDiags: " + calcAllDiags + ", " +
                "SubClass: " + inclEntSubClass + ", " +
                "ClassAssert: " + incEntClassAssert + ", " +
                "EquivClass: " + incEntEquivClass + ", " +
                "DisjointClass: " + incEntEquivClass + ", " +
                "PropertyAssertions: " + incEntPropAssert + ", " +
                "OntologyAxioms: " + incOntolAxioms + ", " +
                "RefThing: " + incAxiomsRefThing + ", " +
                "double threshold: " + entailmentCalThres;

    }


}
