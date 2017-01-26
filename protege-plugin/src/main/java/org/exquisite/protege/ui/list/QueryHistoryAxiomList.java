package org.exquisite.protege.ui.list;

import org.exquisite.core.query.Answer;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class QueryHistoryAxiomList extends AssertedOrInferredAxiomList {

    public QueryHistoryAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit,editorKitHook);
        updateView();
    }

    public EditorKitHook getEditorKitHook() {
        return editorKitHook;
    }

    public OWLEditorKit getEditorKit() {
        return editorKit;
    }

    @Override
    protected void handleDelete() {
        super.handleDelete();
        if (this.getSelectedValue() instanceof QueryHistoryItem) {
            for (int number : getSelectedIndices()) {
                QueryHistoryItem item = (QueryHistoryItem) getModel().getElementAt(number);
                final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();
                debugger.doRemoveQueryHistoryAnswer(item.getAnswer());
            }
        }
    }

    public void updateView() {
        final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();
        List<Answer<OWLLogicalAxiom>> queryHistory = debugger.getQueryHistory();
        final OWLOntology ontology = getEditorKit().getModelManager().getActiveOntology();

        List<Object> items = new LinkedList<>();

        for (int i = queryHistory.size() - 1; i >= 0; i--) {
            Answer<OWLLogicalAxiom> answer = queryHistory.get(i);
            items.add(new QueryHistoryItem(answer,i+1));
            items.addAll(answer.positive.stream().map(axiom -> new QueryHistoryAxiomListItem(axiom, ontology, debugger)).collect(Collectors.toList()));
            items.addAll(answer.negative.stream().map(axiom -> new QueryHistoryAxiomListItem(axiom, ontology, debugger)).collect(Collectors.toList()));
            items.add(" ");
        }

        setListData(items.toArray());
    }

}