package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.OntologyDebugger;
import org.exquisite.protege.model.event.EventType;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;

import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.EnumSet;

import static org.exquisite.protege.model.event.EventType.*;

public class ConflictsView extends AbstractAxiomSetView {

    @Override
    public void stateChanged(ChangeEvent e) {
        final EventType type = ((OntologyDebuggerChangeEvent) e).getType();
        if (EnumSet.of(ACTIVE_ONTOLOGY_CHANGED, SESSION_STATE_CHANGED, QUERY_CALCULATED, DIAGNOSIS_FOUND).contains(type))
            updateList(((OntologyDebugger) e.getSource()).getConflicts());
    }

    @Override
    protected Color getHeaderColor() {
        return new Color(52, 79, 255, 139);
    }

    @Override
    protected String getHeaderPrefix() {
        return "Conflict ";
    }

}
