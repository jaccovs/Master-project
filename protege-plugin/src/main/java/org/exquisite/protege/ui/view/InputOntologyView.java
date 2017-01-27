package org.exquisite.protege.ui.view;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.exquisite.protege.ui.panel.axioms.CorrectAxiomsPanel;
import org.exquisite.protege.ui.panel.axioms.PossiblyFaultyAxiomsPanel;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * A view to present the set of correct and possibly faulty axioms in our input ontology.
 * To be more precise, this view represents a direct mapping of the diagnosis model used in the debugger.
 */
public class InputOntologyView extends AbstractViewComponent {

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

        setAxiomsToDisplay();
        this.possiblyFaultyAxiomsPanel.updateDisplayedAxioms();
        this.correctAxiomsPanel.updateDisplayedAxioms();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        switch (((OntologyDebuggerChangeEvent) e).getType()) {
            case ACTIVE_ONTOLOGY_CHANGED:
                this.possiblyFaultyAxiomsPanel.getSearchPanel().resetSearchField();
                setAxiomsToDisplay();
                possiblyFaultyAxiomsPanel.updateDisplayedAxioms();
                correctAxiomsPanel.updateDisplayedAxioms();
                break;
            case INPUT_ONTOLOGY_CHANGED:
                this.possiblyFaultyAxiomsPanel.getSearchPanel().clearCache();
                this.possiblyFaultyAxiomsPanel.getSearchPanel().doSearch(); // doSearch sets the axioms to display and updates the list
                correctAxiomsPanel.updateDisplayedAxioms();
                break;
            case SESSION_STATE_CHANGED:
                setAxiomsToDisplay();
                possiblyFaultyAxiomsPanel.updateDisplayedAxioms();
                correctAxiomsPanel.updateDisplayedAxioms();
                break;
        }
    }

    public PossiblyFaultyAxiomsPanel getPossiblyFaultyAxiomsPanel() {
        return possiblyFaultyAxiomsPanel;
    }

    private void setAxiomsToDisplay() {
        final OWLOntology ont = getOWLEditorKit().getModelManager().getActiveOntology();
        final DiagnosisModel<OWLLogicalAxiom> dm = getEditorKitHook().getActiveOntologyDebugger().getDiagnosisModel();
        this.possiblyFaultyAxiomsPanel.setAxiomsToDisplay(PossiblyFaultyAxiomsPanel.getAllPossiblyFaultyLogicalAxioms(ont,dm));
    }

}