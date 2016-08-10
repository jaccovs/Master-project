package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.configuration.DiagnosisEngineFactory;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * A view to present the set of correct and possibly faulty axioms in our input ontology.
 * To be more precise, this view represents a direct mapping of the diagnosis model used in the debugger.
 */
public class InputOntologyView extends AbstractQueryViewComponent {

    private BasicAxiomList correctAxiomsList;

    private BasicAxiomList possiblyFaultyAxiomsList;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(InputOntologyView.class.getName());

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
        toolBar.setToolTipText("Axioms from the background are to be considered correct and hence are no candidates for diagnoses.");
        toolBar.add(Box.createVerticalStrut(25));
        toolBar.setMaximumSize(toolBar.getPreferredSize());
        return toolBar;
    }

    public void updateDisplayedPossiblyFaultyAxioms() {
        logger.debug("---- displaying possibly faulty axioms ----");

        DiagnosisEngineFactory diagnosisEngineFactory = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getDiagnosisEngineFactory();
        Set<OWLLogicalAxiom> possiblyFaultyAxioms = hideAxiomsWithAnonymousIndividuals(diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        possiblyFaultyAxiomsList.updateList(possiblyFaultyAxioms,ontology);
    }

    private void updateDisplayedCorrectAxioms() {
        logger.debug("---- displaying correct axioms ----");

        DiagnosisEngineFactory diagnosisEngineFactory = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getDiagnosisEngineFactory();
        Set<OWLLogicalAxiom> correctAxioms = hideAxiomsWithAnonymousIndividuals(diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel().getCorrectFormulas());
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        correctAxiomsList.updateList(correctAxioms,ontology);
    }

    /**
     * The list of correct axioms and possibly faulty axioms shall not show the automatically created axioms with
     * anonymous individuals (while still beeing present in the debug ontology and diagnoses model behind).
     *
     * @param axioms the set of correct or possibly faulty axioms from the diagnosis model.
     * @return A copy of the set of axioms cleaned up by the axioms with anonymous individuals.
     */
    private Set<OWLLogicalAxiom> hideAxiomsWithAnonymousIndividuals(java.util.List<OWLLogicalAxiom> axioms) {
        Set<OWLLogicalAxiom> resultingAxioms = new TreeSet<>();
        for (OWLLogicalAxiom axiom : axioms) {
            if (axiom.getAnonymousIndividuals().isEmpty())
                resultingAxioms.add(axiom);
            else
                logger.debug("Hide axiom with anonymous individual " + axiom);
        }
        return resultingAxioms;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateDisplayedCorrectAxioms();
        updateDisplayedPossiblyFaultyAxioms();
    }

}
