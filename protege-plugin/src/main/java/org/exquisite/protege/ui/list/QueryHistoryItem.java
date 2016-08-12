package org.exquisite.protege.ui.list;

import org.exquisite.core.query.Answer;
import org.protege.editor.core.ui.list.MListItem;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

class QueryHistoryItem implements MListItem {

    private Answer<OWLLogicalAxiom> answer;

    private Integer num;

    QueryHistoryItem(Answer<OWLLogicalAxiom> answer, int num) {
        this.answer = answer;
        this.num = num;
    }

    Answer<OWLLogicalAxiom> getAnswer() {
        return this.answer;
    }

    public void handleEdit() {}

    public boolean handleDelete() {
        return false;
    }

    public String getTooltip() {
        return "Answer " + num ;
    }

    public boolean isDeleteable() {
        return true;
    }

    public String toString() {
        return "Answer " + num;
    }

    public boolean isEditable() {
        return false;
    }

}
