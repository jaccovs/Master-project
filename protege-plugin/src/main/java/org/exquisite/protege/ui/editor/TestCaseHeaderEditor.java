package org.exquisite.protege.ui.editor;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.model.error.AbstractErrorHandler;
import org.exquisite.protege.ui.list.header.TestcaseListHeader;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class TestCaseHeaderEditor extends AbstractEditor {

    protected String getEditorTitle() {
        return "Add a new " + header.getEditorTitleSuffix();
    }

    private TestcaseListHeader header;

    private final NotificationLabel notificationLabel = new NotificationLabel();

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
    protected JToolBar createAddEntitiesToolbar() {
        JToolBar toolBar = super.createAddEntitiesToolbar();
        toolBar.add(notificationLabel,
                new GridBagConstraints(
                        4, 0,
                        1, 1,
                        0, 0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0, 0
                )
        );
        return toolBar;
    }

    @Override
    protected boolean isValid(Set<OWLLogicalAxiom> testcase) {
        final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();

        boolean isValid = debugger.isValidNewTestCase(testcase, header.getType());
        if (isValid) notificationLabel.hideNotification();
        else notificationLabel.showNotification("This axiom cannot be added because an inconsistency/incoherency would be introduced!");

        return isValid;
    }

}
