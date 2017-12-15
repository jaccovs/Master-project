package org.exquisite.protege.ui.list.header;

import org.protege.editor.core.ui.list.MListSectionHeader;

import static org.exquisite.protege.Debugger.TestcaseType;

/**
 * <p>
 *     Section header for the test cases.
 * </p>
 *
 * @author wolfi
 */
public class TestcaseListHeader implements MListSectionHeader {

    private TestcaseType type;

    public TestcaseListHeader(TestcaseType type) {
        this.type = type;
    }

    @Override
    public String getName() {
        switch (type) {
            case ACQUIRED_ENTAILED_TC:
            case ORIGINAL_ENTAILED_TC:
            case ENTAILED_TC:
                return "Entailed Testcases";
            case ACQUIRED_NON_ENTAILED_TC:
            case ORIGINAL_NON_ENTAILED_TC:
            case NON_ENTAILED_TC:
                return "Non Entailed Testcases";
            default:
                throw new IllegalStateException("Unknown Header Type");
        }
    }

    public String getEditorTitleSuffix() {
        switch (type) {
            case ACQUIRED_ENTAILED_TC:
            case ORIGINAL_ENTAILED_TC:
            case ENTAILED_TC:
                return "Entailed Testcase";
            case ACQUIRED_NON_ENTAILED_TC:
            case ORIGINAL_NON_ENTAILED_TC:
            case NON_ENTAILED_TC:
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
        return type.equals(TestcaseType.ORIGINAL_ENTAILED_TC) || type.equals(TestcaseType.ORIGINAL_NON_ENTAILED_TC);
    }

}
