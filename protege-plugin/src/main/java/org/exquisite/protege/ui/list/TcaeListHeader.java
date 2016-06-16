package org.exquisite.protege.ui.list;

import org.protege.editor.core.ui.list.MListSectionHeader;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestCaseType;

public class TcaeListHeader implements MListSectionHeader {

    private TestCaseType type;

    public TcaeListHeader(TestCaseType type) {
        this.type = type;
    }

    @Override
    public String getName() {
        switch (type) {
            case CONSISTENT_TC:
                return "Consistent Testcases";
            case INCONSISTENT_TC:
                return "Inconsistent Testcases";
            case ENTAILED_TC:
                return "Entailed Testcases";
            case NON_ENTAILED_TC:
                return "Non Entailed Testcases";
            default:
                throw new IllegalStateException("Unknown Header Type");
        }
    }

    public String getEditorTitleSuffix() {
        switch (type) {
            case CONSISTENT_TC:
                return "Consistent Testcase";
            case INCONSISTENT_TC:
                return "Inconsistent Testcase";
            case ENTAILED_TC:
                return "Entailed Testcase";
            case NON_ENTAILED_TC:
                return "Non Entailed Testcase";
            default:
                throw new IllegalStateException("Unknown Header Type");
        }
    }

    public TestCaseType getType() {
        return type;
    }

    @Override
    public boolean canAdd() {
        return true;
    }

}
