package org.exquisite.protege.ui.list;

import org.protege.editor.core.ui.list.MListItem;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestCaseType;

public class TcaeListItem implements MListItem {

    private Logger logger = Logger.getLogger(TcaeListItem.class.getName());

    private List<OWLLogicalAxiom> testcase;

    private TestCaseType type;

    public Set<OWLLogicalAxiom> getTestcase() {
        return new HashSet<>(testcase);
    }

    public TcaeListItem(List<OWLLogicalAxiom> testcase, TestCaseType type) {
        this.testcase = testcase;
        this.type = type;
    }

    public void handleEdit() {
    }

    public boolean handleDelete() {
        return false;
    }

    public String getTooltip() {
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
            case CONSISTENT_TC:
                return "Consistent Examples";
            case INCONSISTENT_TC:
                return "Inconsistent Examples";
            case ENTAILED_TC:
                return "Entailed Examples";
            case NON_ENTAILED_TC:
                return "Non Entailed Examples";
            default:
                throw new IllegalStateException("Unknown Header Type");
        }
    }

    public String toString() {
        String prefix = "";
        switch (type) {
            case CONSISTENT_TC:
                prefix = "Consistent ";
                break;
            case INCONSISTENT_TC:
                prefix = "Inconsistent ";
                break;
            case ENTAILED_TC:
                prefix = "Entailed ";
                break;
            case NON_ENTAILED_TC:
                prefix = "Non Entailed ";
                break;
        }

        return prefix + "Testcase (size: " + getTestcase().size() + ")";
    }

    public boolean isEditable() {
        return true;
    }

}
