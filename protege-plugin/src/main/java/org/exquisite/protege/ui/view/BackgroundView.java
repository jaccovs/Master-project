package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.configuration.DiagnosisEngineFactory;
import org.exquisite.protege.ui.component.BackgroundAxiomFinder;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.Set;
import java.util.TreeSet;

public class BackgroundView extends AbstractQueryViewComponent {

    private BasicAxiomList backgroundAxiomsList;

    private BasicAxiomList possiblyFaultyAxiomsList;

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        setLayout(new BorderLayout(10, 10));
        backgroundAxiomsList = new BasicAxiomList(getOWLEditorKit(), this, true);
        possiblyFaultyAxiomsList = new BasicAxiomList(getOWLEditorKit(), this, false);

        Box box = Box.createVerticalBox();

        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.add(createAxiomCreationToolBar(),BorderLayout.NORTH);
        backgroundPanel.add(ComponentFactory.createScrollPane(backgroundAxiomsList),BorderLayout.CENTER);
        box.add(backgroundPanel);

        JPanel kbPanel = new JPanel(new BorderLayout());
        kbPanel.add(createSwitchAxiomsToolBar(), BorderLayout.NORTH);
        kbPanel.add(ComponentFactory.createScrollPane(possiblyFaultyAxiomsList),BorderLayout.CENTER);
        box.add(kbPanel);

        add(box, BorderLayout.CENTER);
        updateDisplayedBackgroundAxioms();
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

    private JToolBar createSwitchAxiomsToolBar() {
        JToolBar toolBar = createToolBar();
        toolBar.add(createLabel("Possibly Faulty Axioms (KB)"));
        toolBar.add(Box.createHorizontalStrut(20));
        JPanel axiomFinderPanel = new JPanel();
        axiomFinderPanel.add(new BackgroundAxiomFinder(this,getOWLEditorKit()));
        toolBar.add(axiomFinderPanel);
        toolBar.setMaximumSize(toolBar.getPreferredSize());
        toolBar.setToolTipText("Axioms from the knowledge base are possible candidates for diagnoses.");

        return toolBar;
    }

    private JToolBar createAxiomCreationToolBar() {
        JToolBar toolBar = createToolBar();
        toolBar.add(createLabel("Correct Axioms (Background)"));
        toolBar.setToolTipText("Axioms from the background are to be considered correct and hence are no candidates for diagnoses.");
        toolBar.add(Box.createVerticalStrut(25));
        toolBar.setMaximumSize(toolBar.getPreferredSize());
        return toolBar;
    }

    public void updateDisplayedPossiblyFaultyAxioms() {
        DiagnosisEngineFactory diagnosisEngineFactory = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getDiagnosisEngineFactory();
        Set<OWLLogicalAxiom> possiblyFaultyFormulas = new TreeSet<>(diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        possiblyFaultyAxiomsList.updateList(possiblyFaultyFormulas,ontology);

    }

    private void updateDisplayedBackgroundAxioms() {
        DiagnosisEngineFactory diagnosisEngineFactory = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getDiagnosisEngineFactory();
        Set<OWLLogicalAxiom> bgAxioms = new TreeSet<>(diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel().getCorrectFormulas());
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        backgroundAxiomsList.updateList(bgAxioms,ontology);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateDisplayedBackgroundAxioms();
        updateDisplayedPossiblyFaultyAxioms();
    }

}
