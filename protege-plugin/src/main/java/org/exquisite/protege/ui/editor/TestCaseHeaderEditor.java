package org.exquisite.protege.ui.editor;

import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.model.error.AbstractErrorHandler;
import org.exquisite.protege.ui.list.header.TestcaseListHeader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
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
    protected boolean isWellFormed(ExpressionEditor<Set<OWLLogicalAxiom>> editor) {
        boolean isWellFormed = super.isWellFormed(editor);
        if (!isWellFormed) notificationLabel.hideNotification();
        return isWellFormed;
    }

    /**
     * A new test case is only valid if it does not already occur as an entailed or non entailed axiom or in the background.
     * If the same axiom is both entailed and non entailed or already defined as an axiom in the background then we
     * would generate an inconsistency (if it is both entailed and non entailed or if it occurs in the background).
     *
     * @param axioms The axiom representing the to be added axiom.
     * @return <code>true</code> if the axiom is ok for the diagnosis model, <code>false</code> otherwise.
     */
    @Override
    protected boolean isValid(Set<OWLLogicalAxiom> axioms) {
        final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();

        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = debugger.getDiagnosisModel();
        final OWLLogicalAxiom axiom = axioms.iterator().next();
        if (diagnosisModel.getEntailedExamples().contains(axiom)) {
            notificationLabel.showNotification("<html>Warning!<br>The axiom is already defined as an entailed testcase!</html>");
            return false;
        } else if (diagnosisModel.getNotEntailedExamples().contains(axiom)) {
            notificationLabel.showNotification("<html>Warning!<br>The axiom is already defined as not entailed testcase!</html>");
            return false;
        } else if (diagnosisModel.getCorrectFormulas().contains(axiom)) {
            notificationLabel.showNotification("<html>Warning!<br>The axiom is already defined as a correct axiom!</html>");
            return false;
        }

        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = debugger.getDiagnosisEngineFactory().getDiagnosisEngine();

        // no diagnosis session has been started before, therefore no diagnosis engine has been created yet
        // or
        // a debugging session has been stopped and thus the solvers diagnosismodel is set to null by the dispose method.
        if (diagnosisEngine == null || diagnosisEngine.getSolver().getDiagnosisModel() == null) {
            debugger.getDiagnosisEngineFactory().reset();
            diagnosisEngine = debugger.getDiagnosisEngineFactory().getDiagnosisEngine();
        }


        if (!diagnosisModel.getPossiblyFaultyFormulas().contains(axiom)) {
            switch (header.getType()) {
                case ORIGINAL_ENTAILED_TC:
                    debugger.getDiagnosisModel().getEntailedExamples().add(axiom);
                    boolean isConsistent = debugger.getDiagnosisEngineFactory().getDiagnosisEngine().getSolver().isConsistent(axioms);
                    debugger.getDiagnosisModel().getEntailedExamples().remove(axiom);
                    if (isConsistent)
                        notificationLabel.hideNotification();
                    else
                        notificationLabel.showNotification("<html>Warning!<br>The axiom causes an inconsistency!</html>");
                    return isConsistent;
                case ORIGINAL_NON_ENTAILED_TC:
                    debugger.getDiagnosisModel().getNotEntailedExamples().add(axiom);
                    boolean isConsistent_ = debugger.getDiagnosisEngineFactory().getDiagnosisEngine().getSolver().isConsistent(axioms);
                    debugger.getDiagnosisModel().getNotEntailedExamples().remove(axiom);
                    if (isConsistent_)
                        notificationLabel.hideNotification();
                    else
                        notificationLabel.showNotification("<html>Warning!<br>The axiom causes an inconsistency!</html>");
                    return isConsistent_;
                default:
                    throw new UnsupportedOperationException("Consistency check for testcases of type " + header.getType() + " is not supported.");
            }
        } else {
            notificationLabel.hideNotification();
            return true; // possibly faulty axioms added as test case shall always be possible
        }
    }

}
