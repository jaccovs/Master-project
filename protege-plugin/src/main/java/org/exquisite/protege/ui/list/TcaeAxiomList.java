package org.exquisite.protege.ui.list;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.ui.editor.TcaeHeaderEditor;
import org.exquisite.protege.ui.editor.TcaeItemEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestCaseType;
import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestCaseType.*;

public class TcaeAxiomList extends AbstractAxiomList {

    private EditorKitHook editorKitHook;

    private OWLEditorKit editorKit;

    public TcaeAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit);
        this.editorKitHook = editorKitHook;
        this.editorKit = editorKit;
        setupKeyboardHandlers();
        updateView();
    }

    private void setupKeyboardHandlers() {
        InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE_SEL");
        am.put("DELETE_SEL", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ADD");
        am.put("ADD", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleEdit();
            }
        });
    }

    public EditorKitHook getEditorKitHook() {
        return editorKitHook;
    }

    public OWLEditorKit getEditorKit() {
        return editorKit;
    }

    @Override
    protected void handleAdd() {
        super.handleAdd();
        if (this.getSelectedValue() instanceof TcaeListHeader) {
            TcaeHeaderEditor editor = new TcaeHeaderEditor((TcaeListHeader) getSelectedValue(),getEditorKit(),getEditorKitHook());
            editor.show();
        }
    }

    @Override
    protected void handleEdit() {
        super.handleEdit();
        if (this.getSelectedValue() instanceof TcaeListItem) {
            TcaeItemEditor editor = new TcaeItemEditor((TcaeListItem) getSelectedValue(),getEditorKit(),getEditorKitHook());
            editor.show();
        }
    }

    @Override
    protected void handleDelete() {
        super.handleDelete();
        if (this.getSelectedValue() instanceof TcaeListItem) {
            for (int number : getSelectedIndices()) {
                TcaeListItem item = (TcaeListItem) getModel().getElementAt(number);
                getEditorKitHook().getActiveOntologyDiagnosisSearcher().doRemoveTestcase(item.getTestcase(),item.getType());
            }
        }
    }

    public void updateView() {
        OntologyDiagnosisSearcher diagnosisSearcher = getEditorKitHook().getActiveOntologyDiagnosisSearcher();
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = diagnosisSearcher.getDiagnosisEngineFactory().getDiagnosisEngine().getSolver().getDiagnosisModel();

        List<Object> items = new ArrayList<>();
        addToItems(items, ENTAILED_TC, diagnosisModel.getEntailedExamples());
        addToItems(items, NON_ENTAILED_TC, diagnosisModel.getNotEntailedExamples());
        addToItems(items, CONSISTENT_TC, diagnosisModel.getConsistentExamples());
        addToItems(items, INCONSISTENT_TC, diagnosisModel.getInconsistentExamples());

        setListData(items.toArray());
    }

    protected void addToItems(List<Object> items, TestCaseType type, List<OWLLogicalAxiom> testcases) {
        OWLOntology ontology = getEditorKit().getModelManager().getActiveOntology();

        items.add(new TcaeListHeader(type));
        //for (Set<OWLLogicalAxiom> testcase : testcases) {
            items.add(new TcaeListItem(testcases, type));
            for (OWLLogicalAxiom axiom : testcases)
                items.add(new AxiomListItem(axiom,ontology));
            items.add(" ");
        //}

    }

}
