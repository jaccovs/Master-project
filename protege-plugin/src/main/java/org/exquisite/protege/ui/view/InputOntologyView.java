package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.event.EventType;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.exquisite.protege.ui.panel.axioms.CorrectAxiomsPanel;
import org.exquisite.protege.ui.panel.axioms.PossiblyFaultyAxiomsPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.EnumSet;

import static org.exquisite.protege.model.event.EventType.*;

/**
 * A view to present the set of correct and possibly faulty axioms in our input ontology.
 * To be more precise, this view represents a direct mapping of the diagnosis model used in the debugger.
 */
public class InputOntologyView extends AbstractQueryViewComponent {

    private CorrectAxiomsPanel correctAxiomsPanel;

    private PossiblyFaultyAxiomsPanel possiblyFaultyAxiomsPanel;

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        setLayout(new BorderLayout(10, 10));
        BasicAxiomList correctAxiomsList = new BasicAxiomList(getOWLEditorKit(), this, true);
        BasicAxiomList possiblyFaultyAxiomsList = new BasicAxiomList(getOWLEditorKit(), this, false);

        Box box = Box.createVerticalBox();

        this.correctAxiomsPanel = new CorrectAxiomsPanel(getOWLEditorKit(), getEditorKitHook(), correctAxiomsList);
        this.possiblyFaultyAxiomsPanel = new PossiblyFaultyAxiomsPanel(getOWLEditorKit(), getEditorKitHook(),possiblyFaultyAxiomsList);

        box.add(this.possiblyFaultyAxiomsPanel);
        box.add(this.correctAxiomsPanel);

        add(box, BorderLayout.CENTER);

        this.possiblyFaultyAxiomsPanel.updateDisplayedAxioms();
        this.correctAxiomsPanel.updateDisplayedAxioms();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        final EventType type = ((OntologyDebuggerChangeEvent) e).getType();
        if (EnumSet.of(ACTIVE_ONTOLOGY_CHANGED, SESSION_STATE_CHANGED, INPUT_ONTOLOGY_CHANGED).contains(type)) {
            possiblyFaultyAxiomsPanel.updateDisplayedAxioms();
            correctAxiomsPanel.updateDisplayedAxioms();
        }
    }

    public PossiblyFaultyAxiomsPanel getPossiblyFaultyAxiomsPanel() {
        return possiblyFaultyAxiomsPanel;
    }

}