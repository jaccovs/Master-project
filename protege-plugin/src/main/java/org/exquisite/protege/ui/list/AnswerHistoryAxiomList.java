package org.exquisite.protege.ui.list;

import org.exquisite.core.query.Answer;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.buttons.AxiomIsEntailedButton;
import org.exquisite.protege.ui.buttons.AxiomIsNotEntailedButton;
import org.exquisite.protege.ui.list.item.AnswerHistoryAxiomListItem;
import org.exquisite.protege.ui.list.header.AnswerHistoryListHeader;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerHistoryAxiomList extends AssertedOrInferredAxiomList {

    public AnswerHistoryAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit,editorKitHook);
        updateView();
    }

    @Override
    protected void handleDelete() {
        super.handleDelete();
        if (this.getSelectedValue() instanceof AnswerHistoryListHeader) {
            for (int number : getSelectedIndices()) {
                AnswerHistoryListHeader item = (AnswerHistoryListHeader) getModel().getElementAt(number);
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
            items.add(new AnswerHistoryListHeader(answer,"Answer " + (i+1)));
            items.addAll(answer.positive.stream().map(axiom -> new AnswerHistoryAxiomListItem(true, axiom, ontology, debugger)).collect(Collectors.toList()));
            items.addAll(answer.negative.stream().map(axiom -> new AnswerHistoryAxiomListItem(false, axiom, ontology, debugger)).collect(Collectors.toList()));
            items.add(" ");
        }

        setListData(items.toArray());
    }

    @Override
    protected List<MListButton> getButtons(Object value) {
        if (value instanceof AnswerHistoryAxiomListItem) {
            List<MListButton> buttons = new ArrayList<>();
            buttons.addAll(super.getButtons(value));
            if (((AnswerHistoryAxiomListItem) value).isEntailed()) {
                buttons.add(new AxiomIsEntailedButton("You answered with YES"));
            } else {
                buttons.add(new AxiomIsNotEntailedButton("You answered with NO"));
            }
            return buttons;
        } else {
            return super.getButtons(value);
        }
    }

    @Override
    protected List<MListButton> getListItemButtons(MListItem item) {
        return super.getListItemButtons(item);
    }
}