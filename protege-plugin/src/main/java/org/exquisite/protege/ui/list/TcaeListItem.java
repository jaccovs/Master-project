package org.exquisite.protege.ui.list;

import org.protege.editor.core.ui.list.MListItem;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.Set;
import java.util.logging.Logger;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestCaseType;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 05.09.12
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class TcaeListItem implements MListItem {

    private Logger logger = Logger.getLogger(TcaeListItem.class.getName());

    private Set<OWLLogicalAxiom> testcase;

    private TestCaseType type;

    public Set<OWLLogicalAxiom> getTestcase() {
        return testcase;
    }

    public TcaeListItem(Set<OWLLogicalAxiom> testcase, TestCaseType type) {
        this.testcase = testcase;
        this.type = type;
    }


    public void handleEdit() {
    }


    public boolean handleDelete() {
        return false;
    }


    public String getTooltip() {
        // ontology.getOntologyID()
        return "Simple Axiom" ;
    }

    public TestCaseType getType() {
        return type;
    }

    public boolean isDeleteable() {
        return true;
    }

    public String getEditorTitleSuffix() {
        switch (type) {
            case POSITIVE_TC:
                return "Positive Testcase";
            case NEGATIVE_TC:
                return "Negative Testcase";
            case ENTAILED_TC:
                return "Entailed Testcase";
            case NON_ENTAILED_TC:
                return "Non Entailed Testcase";
            default:
                throw new IllegalStateException("Unknown Header Type");
        }

    }

    public String toString() {

        String prefix = "";
        switch (type) {
            case POSITIVE_TC:
                prefix = "Positive ";
                break;
            case NEGATIVE_TC:
                prefix = "Negative ";
                break;
            case ENTAILED_TC:
                prefix = "Entailed ";
                break;
            case NON_ENTAILED_TC:
                prefix = "Nonentailed ";
                break;
        }

        return prefix + "Testcase (size: " + getTestcase().size() + ")";
    }

    public boolean isEditable() {
        return true;
    }

}
