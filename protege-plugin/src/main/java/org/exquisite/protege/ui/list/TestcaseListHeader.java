package org.exquisite.protege.ui.list;

import org.protege.editor.core.ui.list.MListSectionHeader;

import static org.exquisite.protege.Debugger.TestcaseType;

public class TestcaseListHeader implements MListSectionHeader {

    private TestcaseType type;

    TestcaseListHeader(TestcaseType type) {
        this.type = type;
    }

    @Override
    public String getName() {
        switch (type) {
            case ACQUIRED_ENTAILED_TC:
            case ORIGINAL_ENTAILED_TC:
                return "Entailed Testcases";
            case ACQUIRED_NON_ENTAILED_TC:
            case ORIGINAL_NON_ENTAILED_TC:
                return "Non Entailed Testcases";
            default:
                throw new IllegalStateException("Unknown Header Type");
        }
    }

    public String getEditorTitleSuffix() {
        switch (type) {
            case ACQUIRED_ENTAILED_TC:
            case ORIGINAL_ENTAILED_TC:
                return "Entailed Testcase";
            case ACQUIRED_NON_ENTAILED_TC:
            case ORIGINAL_NON_ENTAILED_TC:
                return "Non Entailed Testcase";
            default:
                throw new IllegalStateException("Unknown Header Type");
        }
    }

    public TestcaseType getType() {
        return type;
    }

    @Override
    public boolean canAdd() {
        return false;
    } // TODO not yet fully functional implemented

}
