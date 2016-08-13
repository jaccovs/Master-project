package org.exquisite.protege.ui.view;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.*;

/**
 * A view to present the set of correct and possibly faulty axioms in our input ontology.
 * To be more precise, this view represents a direct mapping of the diagnosis model used in the debugger.
 */
public class InputOntologyView extends AbstractQueryViewComponent {

    private BasicAxiomList correctAxiomsList;

    private BasicAxiomList possiblyFaultyAxiomsList;

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        setLayout(new BorderLayout(10, 10));
        correctAxiomsList = new BasicAxiomList(getOWLEditorKit(), this, true);
        possiblyFaultyAxiomsList = new BasicAxiomList(getOWLEditorKit(), this, false);

        Box box = Box.createVerticalBox();

        JPanel correctAxiomsPanel = new JPanel(new BorderLayout());
        correctAxiomsPanel.add(createCorrectAxiomsToolBar(),BorderLayout.NORTH);
        correctAxiomsPanel.add(ComponentFactory.createScrollPane(correctAxiomsList),BorderLayout.CENTER);
        box.add(correctAxiomsPanel);

        JPanel possiblyFaultyPanel = new JPanel(new BorderLayout());
        possiblyFaultyPanel.add(createPossiblyFaultyAxiomsToolBar(), BorderLayout.NORTH);
        possiblyFaultyPanel.add(ComponentFactory.createScrollPane(possiblyFaultyAxiomsList),BorderLayout.CENTER);
        box.add(possiblyFaultyPanel);

        add(box, BorderLayout.CENTER);
        updateDisplayedCorrectAxioms();
        updateDisplayedPossiblyFaultyAxioms();
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setBorder(null);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        return toolBar;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize()+1));
        return label;
    }

    private JToolBar createPossiblyFaultyAxiomsToolBar() {
        JToolBar toolBar = createToolBar();
        toolBar.add(createLabel("Possibly Faulty Axioms (KB)"));
        /* TODO reactivate this finder after a working version has been implemented
        toolBar.add(Box.createHorizontalStrut(20));
        JPanel axiomFinderPanel = new JPanel();
        axiomFinderPanel.add(new PossiblyFaultyAxiomsFinder(this,getOWLEditorKit()));
        toolBar.add(axiomFinderPanel);
        */
        toolBar.setMaximumSize(toolBar.getPreferredSize());
        toolBar.setToolTipText("Axioms from the knowledge base are possible candidates for diagnoses.");

        return toolBar;
    }

    private JToolBar createCorrectAxiomsToolBar() {
        JToolBar toolBar = createToolBar();
        toolBar.add(createLabel("Correct Axioms (Background)"));
        toolBar.setToolTipText("Axioms from the background are considered to be correct and therefore are not candidates for diagnoses.");
        toolBar.add(Box.createVerticalStrut(25));
        toolBar.setMaximumSize(toolBar.getPreferredSize());
        return toolBar;
    }

    /**
     * Updates the view of displayed possible faulty axioms.
     * @see #stateChanged(ChangeEvent)
     */
    public void updateDisplayedPossiblyFaultyAxioms() {
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = getEditorKitHook().getActiveOntologyDiagnosisSearcher().
                getDiagnosisEngineFactory().getDiagnosisEngine().getSolver().getDiagnosisModel();
        updateDisplayedAxioms(possiblyFaultyAxiomsList, diagnosisModel.getPossiblyFaultyFormulas());
    }

    /**
     * Updates the view of displayed correct axioms.
     * @see #stateChanged(ChangeEvent)
     */
    private void updateDisplayedCorrectAxioms() {
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = getEditorKitHook().getActiveOntologyDiagnosisSearcher().
                getDiagnosisEngineFactory().getDiagnosisEngine().getSolver().getDiagnosisModel();
        updateDisplayedAxioms(correctAxiomsList, diagnosisModel.getCorrectFormulas());
    }

    /**
     * Updates a list with a set of axioms that exists in the active ontology.
     *
     * @param list The list to update.
     * @param axioms The axioms to update the list with after a check if all axioms doe exist in the active ontology.
     * @see #updateDisplayedCorrectAxioms()
     * @see #updateDisplayedPossiblyFaultyAxioms()
     */
    private void updateDisplayedAxioms(BasicAxiomList list, java.util.List<OWLLogicalAxiom> axioms) {
        final OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();

        // show only those axioms that do also exist in the active ontology and show them in a sorted order (TreeSet)
        Set<OWLLogicalAxiom> axiomsToDisplay = new TreeSet<>(axioms);
        axiomsToDisplay.retainAll(ontology.getLogicalAxioms());
        list.updateList(axiomsToDisplay, ontology);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateDisplayedCorrectAxioms();
        updateDisplayedPossiblyFaultyAxioms();
    }

}