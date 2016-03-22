package org.exquisite.protege.ui.list;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.protege.editor.core.ui.list.MListSectionHeader;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestCaseType;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 05.09.12
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class TcaeListHeader implements MListSectionHeader {


    public TcaeListHeader(TestCaseType type) {
        this.type = type;
    }

    private TestCaseType type;


    @Override
    public String getName() {
        switch (type) {
            case POSITIVE_TC:
                return "Positive Testcases";
            case NEGATIVE_TC:
                return "Negative Testcases";
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



    public TestCaseType getType() {
        return type;
    }

    @Override
    public boolean canAdd() {
        return true;
    }

}
