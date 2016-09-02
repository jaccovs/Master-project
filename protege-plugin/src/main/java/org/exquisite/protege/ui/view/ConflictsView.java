package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.OntologyDebugger;

import javax.swing.event.ChangeEvent;
import java.awt.*;

public class ConflictsView extends AbstractAxiomSetView {

    @Override
    public void stateChanged(ChangeEvent e) {
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
