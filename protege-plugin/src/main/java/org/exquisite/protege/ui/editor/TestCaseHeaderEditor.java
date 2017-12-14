package org.exquisite.protege.ui.editor;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.model.error.AbstractErrorHandler;
import org.exquisite.protege.ui.list.header.TestcaseListHeader;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.swing.*;
import java.util.Set;

public class TestCaseHeaderEditor extends AbstractEditor {

    protected String getEditorTitle() {
        return "Add a new " + header.getEditorTitleSuffix();
    }

    private TestcaseListHeader header;

    public TestCaseHeaderEditor(TestcaseListHeader header, OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit, editorKitHook);
        this.header = header;
    }

    protected void handleEditorConfirmed(Set<OWLLogicalAxiom> testcase) {
        final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();

        debugger.doAddTestcase(testcase,header.getType(),new AbstractErrorHandler() {
            @Override
            public void errorHappened(Debugger.ErrorStatus error, Exception ex) {
                showErrorDialog(null, "An error occurred when this new testcase was added.",
                        "Error On Add Testcase", JOptionPane.ERROR_MESSAGE, ex);
            }
        });
    }

    @Override
    protected boolean isValid(Set<OWLLogicalAxiom> testcase) {
        final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();

        return debugger.isValidNewTestCase(testcase, header.getType());
    }

}
