package org.exquisite.protege.ui.editor;

import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.error.ErrorHandler;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.ui.list.TcaeListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 06.09.12
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
public class TcaeItemEditor extends AbstractEditorTCaE {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(TcaeItemEditor.class.getName());

    protected String getEditorTitle() {
        return "Edit " + item.getEditorTitleSuffix();
    }

    private TcaeListItem item;

    public TcaeItemEditor(TcaeListItem item, OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit, editorKitHook);
        this.item = item;
    }

    @Override
    protected void setEditedAxioms(ExpressionEditor<Set<OWLLogicalAxiom>> editor) {
        editor.setExpressionObject(item.getTestcase());
    }

    protected void handleEditorConfirmed(Set<OWLLogicalAxiom> testcase) {
        OntologyDiagnosisSearcher diagnosisSearcher = getEditorKitHook().getActiveOntologyDiagnosisSearcher();

        diagnosisSearcher.doUpdateTestcase(item.getTestcase(),testcase,item.getType(), new ErrorHandler() {
            @Override
            public void errorHappend(OntologyDiagnosisSearcher.ErrorStatus error) {
                JOptionPane.showMessageDialog(null, "The reformulated testcase is not compatible with already specified testcases. ", "Inconsistent Theory Exception", JOptionPane.ERROR_MESSAGE);
            }
        });

        logger.debug("OK");
    }
}
