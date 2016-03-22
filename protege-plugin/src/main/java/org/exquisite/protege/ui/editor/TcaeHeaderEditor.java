package org.exquisite.protege.ui.editor;

import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.ErrorHandler;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.ui.list.TcaeListHeader;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Set;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.ErrorStatus.*;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 06.09.12
 * Time: 10:13
 * To change this template use File | Settings | File Templates.
 */
public class TcaeHeaderEditor extends AbstractEditorTCaE {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(TcaeHeaderEditor.class.getName());

    protected String getEditorTitle() {
        return "Add " + header.getEditorTitleSuffix();
    }

    private TcaeListHeader header;

    public TcaeHeaderEditor(TcaeListHeader header, OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit, editorKitHook);
        this.header = header;
    }

    protected void handleEditorConfirmed(Set<OWLLogicalAxiom> testcase) {
        OntologyDiagnosisSearcher diagnosisSearcher = getEditorKitHook().getActiveOntologyDiagnosisSearcher();

        diagnosisSearcher.doAddTestcase(testcase,header.getType(),new ErrorHandler() {
            @Override
            public void errorHappend(OntologyDiagnosisSearcher.ErrorStatus error) {
                JOptionPane.showMessageDialog(null, "This testcase would not compatible with already specified testcases and was therefore nod added. To resolve this problem you can try to delete testcase which are conflicting. ", "Inconsistent Theory Exception", JOptionPane.ERROR_MESSAGE);
            }
        });
        logger.debug("OK");
    }



}
