package org.exquisite.protege.ui.list;

import org.exquisite.core.query.Answer;
import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryHistoryAxiomList extends AbstractAxiomList {

    private EditorKitHook editorKitHook;

    private OWLEditorKit editorKit;

    public QueryHistoryAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit);
        this.editorKitHook = editorKitHook;
        this.editorKit = editorKit;
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
                final OntologyDiagnosisSearcher searcher = getEditorKitHook().getActiveOntologyDiagnosisSearcher();
                searcher.doRemoveQueryHistoryAnswer(item.getAnswer());
            }
        }
    }

    public void updateView() {
        List<Answer<OWLLogicalAxiom>> queryHistory = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getQueryHistory();
        final OWLOntology ontology = getEditorKit().getModelManager().getActiveOntology();

        List<Object> items = new LinkedList<>();

        for (int i = queryHistory.size() - 1; i >= 0; i--) {
            Answer<OWLLogicalAxiom> answer = queryHistory.get(i);
            items.add(new QueryHistoryItem(answer,i+1));
            items.addAll(answer.positive.stream().map(axiom -> new AxiomListItem(axiom, ontology)).collect(Collectors.toList()));
            items.addAll(answer.negative.stream().map(axiom -> new AxiomListItem(axiom, ontology)).collect(Collectors.toList()));
            items.add(" ");
        }

        setListData(items.toArray());
    }
}