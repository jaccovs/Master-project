package org.exquisite.protege.ui.list;

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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestCaseType;
import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestCaseType.*;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 05.09.12
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
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

    public void updateView() { // TODO
        /*
        OntologyDiagnosisSearcher diagnosisSearcher = getEditorKitHook().getActiveOntologyDiagnosisSearcher();
        OWLTheory theory = (OWLTheory) diagnosisSearcher.getSearchCreator().getSearch().getSearchable();

        List<Object> items = new ArrayList<Object>();
        addToItems(items, POSITIVE_TC, theory.getKnowledgeBase().getPositiveTests());
        addToItems(items, NEGATIVE_TC, theory.getKnowledgeBase().getNegativeTests());
        addToItems(items, ENTAILED_TC, theory.getKnowledgeBase().getEntailedTests());
        addToItems(items, NON_ENTAILED_TC, theory.getKnowledgeBase().getNonentailedTests());

        setListData(items.toArray());
        */
    }

    protected void addToItems(List<Object> items, TestCaseType type, Collection<Set<OWLLogicalAxiom>> testcases) {
        OWLOntology ontology = getEditorKit().getModelManager().getActiveOntology();

        items.add(new TcaeListHeader(type));
        for (Set<OWLLogicalAxiom> testcase : testcases) {
            items.add(new TcaeListItem(testcase,type));
            for (OWLLogicalAxiom axiom : testcase)
                items.add(new AxiomListItem(axiom,ontology));
            items.add(" ");
        }

    }

}
