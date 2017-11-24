package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.event.EventType;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.ui.list.AnswerHistoryAxiomList;
import org.protege.editor.core.ui.list.MList;

import javax.swing.event.ChangeEvent;
import java.util.EnumSet;

import static org.exquisite.protege.model.event.EventType.*;

/**
 * <p>
 *     A view that represents the user's answer history of the currently running debugging session.
 * </p>
 * <p>
 *     Each iteration of the dialogue contains two sets of axioms which were answered either positively (entailed) or
 *     negatively (not entailed) by the user.
 * </p>
 *
 * @author wolfi
 */
public class AnswerHistoryView extends AbstractListViewComponent {

    @Override
    protected MList createListForComponent() {
        return new AnswerHistoryAxiomList(getOWLEditorKit(), getEditorKitHook());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        final EventType type = ((OntologyDebuggerChangeEvent) e).getType();
        if (EnumSet.of(ACTIVE_ONTOLOGY_CHANGED, SESSION_STATE_CHANGED, QUERY_CALCULATED, DIAGNOSIS_FOUND, DIAGNOSIS_MODEL_CHANGED).contains(type))
            ((AnswerHistoryAxiomList)getList()).updateView();
    }

}
