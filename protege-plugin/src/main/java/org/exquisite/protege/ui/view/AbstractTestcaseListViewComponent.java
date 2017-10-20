package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.event.EventType;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.ui.list.AbstractTestcaseAxiomList;

import javax.swing.event.ChangeEvent;
import java.util.EnumSet;

import static org.exquisite.protege.model.event.EventType.*;

/**
 * Abstract super class for the acquired and orginal testcase view.
 *
 * @author wolfi
 */
abstract public class AbstractTestcaseListViewComponent extends AbstractListViewComponent {

    @Override
    public void stateChanged(ChangeEvent e) {
        final EventType type = ((OntologyDebuggerChangeEvent) e).getType();
        if (EnumSet.of(ACTIVE_ONTOLOGY_CHANGED, SESSION_STATE_CHANGED, QUERY_CALCULATED, DIAGNOSIS_FOUND, DIAGNOSIS_MODEL_CHANGED).contains(type))
            ((AbstractTestcaseAxiomList)getList()).updateView();
    }

}
