package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.event.EventType;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.ui.list.AnswerHistoryAxiomList;
import org.protege.editor.core.ui.list.MList;

import javax.swing.event.ChangeEvent;
import java.util.EnumSet;

import static org.exquisite.protege.model.event.EventType.*;

public class AnswerHistoryView extends AbstractListViewComponent {

    @Override
    protected MList createListForComponent() {
        return new AnswerHistoryAxiomList(getOWLEditorKit(), getEditorKitHook());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        final EventType type = ((OntologyDebuggerChangeEvent) e).getType();
        if (EnumSet.of(ACTIVE_ONTOLOGY_CHANGED, SESSION_STATE_CHANGED, QUERY_CALCULATED, DIAGNOSIS_FOUND).contains(type))
            ((AnswerHistoryAxiomList)getList()).updateView();
    }

}
