package org.exquisite.protege.ui.list.header;

import org.exquisite.core.query.Answer;
import org.protege.editor.core.ui.list.MListItem;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

public class AnswerHistoryListHeader implements MListItem {

    private Answer<OWLLogicalAxiom> answer;

    private String name;

    public AnswerHistoryListHeader(Answer<OWLLogicalAxiom> answer, String name) {
        this.answer = answer;
        this.name = name;
    }

    public Answer<OWLLogicalAxiom> getAnswer() {
        return this.answer;
    }

    public void handleEdit() {}

    public boolean handleDelete() {
        return false;
    }

    public String getTooltip() {
        return name ;
    }

    public boolean isDeleteable() {
        return true;
    }

    public String toString() {
        return name;
    }

    public boolean isEditable() {
        return false;
    }

}
