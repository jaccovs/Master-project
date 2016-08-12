package org.exquisite.protege.ui.list;

import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.ui.editor.TestCaseHeaderEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestcaseType;

abstract public class AbstractTestcaseAxiomList extends AbstractAxiomList {

    private EditorKitHook editorKitHook;

    private OWLEditorKit editorKit;

    AbstractTestcaseAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
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
        if (this.getSelectedValue() instanceof TestcaseListHeader) {
            TestCaseHeaderEditor editor = new TestCaseHeaderEditor((TestcaseListHeader) getSelectedValue(),getEditorKit(),getEditorKitHook());
            editor.show();
        }
    }

    @Override
    protected void handleEdit() {
        super.handleEdit();
    }

    @Override
    protected void handleDelete() {
        super.handleDelete();
        if (this.getSelectedValue() instanceof TestcaseListItem) {
            for (int number : getSelectedIndices()) {
                TestcaseListItem item = (TestcaseListItem) getModel().getElementAt(number);
                getEditorKitHook().getActiveOntologyDiagnosisSearcher().doRemoveTestcase(item.getTestcase(),item.getType());
            }
        }
    }

    public void updateView() {
        List<Object> items = new ArrayList<>();
        addToItems(items, getEntailedType(), getEntailedTestcases());
        addToItems(items, getNonEntailedType(), getNonEntailedTestcases());

        setListData(items.toArray());
    }

    private void addToItems(List<Object> items, TestcaseType type, List<OWLLogicalAxiom> testcases) {
        OWLOntology ontology = getEditorKit().getModelManager().getActiveOntology();

        items.add(new TestcaseListHeader(type));
        items.addAll(testcases.stream().map(axiom -> new TestcaseListItem(axiom, type, ontology)).collect(Collectors.toList()));
        items.add(" ");
    }

    protected abstract TestcaseType getEntailedType();

    protected abstract List<OWLLogicalAxiom> getEntailedTestcases();

    protected abstract TestcaseType getNonEntailedType();

    protected abstract List<OWLLogicalAxiom> getNonEntailedTestcases();

}
